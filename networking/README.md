# :networking

Ktor + kotlinx-serialization data layer, and a genuine Kotlin Multiplatform module (Android + iOS).
Defines the remote `TemplateService`, wraps calls in `TemplateNetwork`, and exposes them to the
domain through `DefaultTemplateRepository`. Every call is mapped to a `BaseResult<Data, NetworkError>`
so failures never escape as crashes.

Depends only on `:domain`. The concrete `HttpClient` (base URL, timeouts, retry, auth header) is
built by `TemplateHttpClientFactory` in this module but *constructed* by the app layer (`:app`'s
`mainModule` on Android, `IosKoin`'s `iosAppModule` on iOS) and injected in via Koin — only the app
layer knows the real base URL and header provider, so `networkingModule` receives the `HttpClient`
via `get()` the same way it used to receive Retrofit.

## Layers

```
TemplateService      thin HttpClient wrapper — raw suspend calls returning HttpResponse
      │
TemplateNetwork      wraps each call in safeApiCall -> BaseResult<…, NetworkError>
      │
DefaultTemplateRepository   switches to dispatcherProvider.io, implements the domain interface
      │
(domain) TemplateRepository
```

## TemplateService

```kotlin
class TemplateService(private val client: HttpClient) {
    suspend fun getGoogleData(): HttpResponse = client.get("")
    suspend fun getGoogleStatus(): HttpResponse = client.get("")
}
```

`baseUrl` isn't passed per call: it's installed once on the client via Ktor's `DefaultRequest`
plugin (see `TemplateHttpClientFactory` below), so `client.get("")` resolves against it — the same
relative-path model `@GET("/")` gave under Retrofit.

## TemplateNetwork / TemplateNetworkApi

`TemplateNetworkApi` is `TemplateNetwork`'s own contract, extracted so `DefaultTemplateRepository`
(and its tests) can depend on the interface rather than the concrete class — a fakeability seam one
layer below `TemplateRepository`'s own.

```kotlin
class TemplateNetwork(private val templateService: TemplateService) : TemplateNetworkApi {

    override suspend fun getGoogleData(): BaseResult<String, NetworkError> =
        safeApiCall(apiCall = { templateService.getGoogleData() }) { response ->
            response.status.value.toString()
        }

    override suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError> =
        safeApiCall(apiCall = { templateService.getGoogleStatus() }) { }
}
```

## DefaultTemplateRepository

Implements the domain `TemplateRepository`. Its only extra job is threading: it moves work onto the
IO dispatcher via the injected `DispatcherProvider` (an `expect`/`actual` — `Dispatchers.IO` on
Android, `Dispatchers.Default` on iOS, since `Dispatchers.IO` is `internal` on Kotlin/Native),
keeping `:networking` testable (tests pass a fake `DispatcherProvider`).

## toResult vs safeApiCall

Both live in `data/utils/NetworkUtils.kt`.

- **`HttpResponse.toResult { }`** — maps an *already-resolved* `HttpResponse`. A 2xx becomes
  `Success(onSuccess(response))`; a non-2xx becomes `Error(NetworkError.Http(code, description))`.
  It does **not** catch exceptions — use it when you already hold a response.
- **`safeApiCall(apiCall) { }`** — the one to use from `TemplateNetwork`. It runs the suspending
  call, delegates the happy path to `toResult`, and additionally catches thrown exceptions,
  classifying each into a typed `NetworkError`.

```kotlin
inline fun <R> HttpResponse.toResult(onSuccess: (HttpResponse) -> R): BaseResult<R, NetworkError>

suspend fun <R> safeApiCall(
    apiCall: suspend () -> HttpResponse,
    onSuccess: (HttpResponse) -> R,
): BaseResult<R, NetworkError>
```

## NetworkError mapping

| Condition | Caught type | Result |
| --- | --- | --- |
| non-2xx response | — (from `toResult`) | `NetworkError.Http(code, description)` |
| request timed out | `HttpRequestTimeoutException` / `ConnectTimeoutException` / `SocketTimeoutException` (genuinely multiplatform — Ktor's own) | `NetworkError.Timeout` |
| offline / DNS / connect / other transport failure | whatever each platform's own engine throws — see `classifyTransportFailure` below | `NetworkError.Connectivity` or `NetworkError.Timeout` |
| body could not be parsed | `SerializationException` | `NetworkError.Serialization(message)` |
| anything else | `Exception` | `NetworkError.Unknown(message)` |
| coroutine cancelled | `CancellationException` | **rethrown** (never swallowed) |

## classifyTransportFailure (expect/actual)

There is no single multiplatform exception type that safely means "the network is unreachable"
without also swallowing unrelated failures: OkHttp on Android throws `java.io.IOException` and its
subtypes for a DNS/connect failure, and Darwin on iOS surfaces the equivalent as its own
platform-native type. `classifyTransportFailure` is `expect`/`actual` so each platform is asked
using the exact exception types its own engine throws, rather than trying to catch a lowest-common-
denominator type in common code. The Android actual maps `InterruptedIOException` to `Timeout` (not
`Connectivity`) specifically, matching the original OkHttp/Retrofit-era classification — it must be
checked before the generic `IOException` case, since it is itself an `IOException` subtype.

## TemplateHttpClientFactory

The multiplatform counterpart of the old Retrofit-era `RetrofitFactory`: everything that used to be
an OkHttp interceptor is a Ktor client plugin here, and — unlike an interceptor — every plugin is
genuinely shared code. `createPlatformHttpClient` (`expect`/`actual` in `PlatformHttpClient.kt`,
picking the OkHttp engine on Android and the Darwin engine on iOS) is the only piece left that has
to differ per platform.

```kotlin
object TemplateHttpClientFactory {
    fun create(
        baseUrl: String,
        headerProvider: () -> Map<String, String> = { emptyMap() },
        maxRetries: Int = DEFAULT_MAX_RETRIES,   // 2
        logBody: Boolean = false,
    ): HttpClient
}
```

- **Timeouts** (`HttpTimeout`) — connect 15s, socket 30s, request 60s.
- **Retry** (`HttpRequestRetry`) — retries only a transport failure `classifyTransportFailure`
  recognises as transient, never a non-2xx response and never on cancellation; no backoff
  (immediate retry, matching the interceptor it replaces). The plugin's own default retries certain
  non-2xx responses even with no configuration of your own, so the response side is disabled
  explicitly (`retryIf { _, _ -> false }`) to match the old interceptor's exact behavior.
- **Content negotiation** (`ContentNegotiation` + `json`) — `Json { ignoreUnknownKeys = true }`,
  kotlinx-serialization only (no Gson).
- **Logging** (`Logging`) — full `BODY` logging only when the caller passes `logBody = true`
  (debug builds), `NONE` otherwise (bodies may contain tokens/user data).
- **Base URL + headers** (`DefaultRequest`) — `headerProvider` is invoked fresh on every outgoing
  request, so it can return freshly-read values (e.g. a current auth token), the same per-request
  contract the interceptor it replaces had.

It's constructed in the app layer, which is where the real base URL lives:

```kotlin
// :app's mainModule (Android)
single<HttpClient> {
    TemplateHttpClientFactory.create(
        baseUrl = get<AppConfiguration>().baseUrl,
        logBody = BuildProfile.isDebugBuild,
    )
}

// IosKoin's iosAppModule (iOS)
single<HttpClient> { TemplateHttpClientFactory.create(baseUrl = "https://www.google.com") }
```

## Koin wiring (networkingModule)

```kotlin
val networkingModule = module {
    single { TemplateService(client = get<HttpClient>()) }
    single<TemplateNetworkApi> { TemplateNetwork(templateService = get()) }
    single<TemplateRepository> {
        DefaultTemplateRepository(templateNetwork = get(), dispatcherProvider = get())
    }
}
```

Note `HttpClient` and `DispatcherProvider` are both `get()`-only here — see the app layer's own
module (`:app`'s `mainModule` / `IosKoin`'s `iosAppModule`) for where they're actually bound.

## Adding a new endpoint

1. **Service** — add the call to `TemplateService`:

   ```kotlin
   suspend fun getWidget(id: String): HttpResponse = client.get("widgets/$id")
   ```

2. **DTO** — add a `@Serializable` model (kotlinx-serialization) if the endpoint returns a body.

3. **Network** — wrap it in `TemplateNetwork` with `safeApiCall`, mapping the body to your
   domain/return type:

   ```kotlin
   suspend fun getWidget(id: String): BaseResult<WidgetDto, NetworkError> =
       safeApiCall(apiCall = { templateService.getWidget(id) }) { response ->
           response.body()
       }
   ```

4. **Repository** — expose it through the domain interface and run it on IO. Add the method to
   `TemplateRepository` in `:domain`, then implement it in `DefaultTemplateRepository`:

   ```kotlin
   override suspend fun getWidget(id: String) = withContext(dispatcherProvider.io) {
       templateNetwork.getWidget(id)
   }
   ```

No DI changes are needed for new methods on existing types. Add new `single { … }` bindings only
when you introduce a new service or network class.

## Testing

`kotlin.test` + coroutines-test in `commonTest` (this module's Android and iOS unit tests share the
same suite), plus a `MockEngine`-backed `HttpClient` for `TemplateService`/integration-style tests
and a hand-written fake `DispatcherProvider` for deterministic threading (JUnit5/MockK don't apply
here — those are JVM-only and this module's tests run on Kotlin/Native too). See
`ToResultTest`, `SafeApiCallTest`, `TemplateNetworkTest`, and `DefaultTemplateRepositoryTest`.
