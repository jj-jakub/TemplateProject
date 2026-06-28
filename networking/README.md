# :networking

Retrofit 3 + kotlinx-serialization data layer. Defines the remote `TemplateService`, wraps calls in a thin `TemplateNetwork`, and exposes them to the domain through `DefaultTemplateRepository`. Every call is mapped to a `BaseResult<Data, NetworkError>` so failures never escape as crashes.

Depends only on `:domain`. The concrete `Retrofit`/`OkHttpClient` (timeouts, retry, auth header) is built by `RetrofitFactory` in `:app` and injected in via Koin — this module never constructs HTTP clients itself.

## Layers

```
TemplateService      Retrofit interface — raw suspend calls returning Response<T>
      │
TemplateNetwork      wraps each call in safeApiCall -> BaseResult<…, NetworkError>
      │
DefaultTemplateRepository   switches to dispatcherProvider.io, implements the domain interface
      │
(domain) TemplateRepository
```

## TemplateService

The Retrofit service. Uses `suspend` functions directly — Retrofit 3 supports them natively, so there is no call adapter.

```kotlin
interface TemplateService {
    @GET("/")
    suspend fun getGoogleData(): Response<Unit>

    @GET("/")
    suspend fun getGoogleStatus(): Response<Unit>
}
```

The instance is created in `networkingModule` from the injected `Retrofit`:

```kotlin
single { get<Retrofit>().create(TemplateService::class.java) }
```

## TemplateNetwork

A thin wrapper that turns each service call into a `BaseResult`. It chooses `safeApiCall` vs `toResult` and derives the success value from the response.

```kotlin
class TemplateNetwork(private val templateService: TemplateService) {

    suspend fun getGoogleData(): BaseResult<String, NetworkError> =
        safeApiCall(apiCall = { templateService.getGoogleData() }) { response ->
            response.code().toString()
        }

    suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError> =
        safeApiCall(apiCall = { templateService.getGoogleStatus() }) { }
}
```

## DefaultTemplateRepository

Implements the domain `TemplateRepository`. Its only extra job is threading: it moves work onto the IO dispatcher via the injected `DispatcherProvider`, keeping `:networking` testable (tests pass a `TestDispatcherProvider`).

```kotlin
class DefaultTemplateRepository(
    private val templateNetwork: TemplateNetwork,
    private val dispatcherProvider: DispatcherProvider,
) : TemplateRepository {

    override suspend fun getGoogleData() = withContext(dispatcherProvider.io) {
        templateNetwork.getGoogleData()
    }

    override suspend fun getGoogleStatus() = withContext(dispatcherProvider.io) {
        templateNetwork.getGoogleStatus()
    }
}
```

## toResult vs safeApiCall

Both live in `data/utils/NetworkUtils.kt`.

- **`Response<T>.toResult { }`** — maps an *already-received* `Response`. A 2xx becomes `Success(onSuccess(response))`; a non-2xx becomes `Error(NetworkError.Http(code, message))`. It does **not** catch exceptions — use it when you already hold a `Response` (e.g. inside tests, or when you handle exceptions yourself).
- **`safeApiCall(apiCall) { }`** — the one to use from `TemplateNetwork`. It runs the suspending call, delegates the happy path to `toResult`, and additionally catches thrown exceptions, classifying each into a typed `NetworkError`.

Pass a transform that extracts your data (e.g. `{ it.code().toString() }`), or `{ }` for endpoints where only success/failure matters (a `Unit` result).

```kotlin
inline fun <T, R> Response<T>.toResult(onSuccess: (Response<T>) -> R): BaseResult<R, NetworkError>

suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> Response<T>,
    onSuccess: (Response<T>) -> R,
): BaseResult<R, NetworkError>
```

## NetworkError mapping

`NetworkError` (in `:domain`) is a sealed interface where every variant exposes a non-null `message`. `safeApiCall` maps as follows:

| Condition | Caught type | Result |
| --- | --- | --- |
| non-2xx response | — (from `toResult`) | `NetworkError.Http(code, message)` |
| request timed out | `SocketTimeoutException` | `NetworkError.Timeout` |
| offline / DNS / connect / other IO | `UnknownHostException`, `ConnectException`, `IOException` | `NetworkError.Connectivity` |
| body could not be parsed | `SerializationException` | `NetworkError.Serialization(message)` |
| anything else | `Exception` | `NetworkError.Unknown(message)` |
| coroutine cancelled | `CancellationException` | **rethrown** (never swallowed) |

`CancellationException` is rethrown first so structured-concurrency cancellation keeps working — only genuine failures are converted to `NetworkError`.

## RetrofitFactory lives in :app

`:networking` consumes a `Retrofit` instance but never builds one. `RetrofitFactory` (in `:app`, `data/network/RetrofitFactory.kt`) owns the HTTP concerns:

- **Timeouts** — connect 15s, read/write 30s, call 60s; `retryOnConnectionFailure(true)`.
- **Retry** — a `RetryInterceptor(maxRetries)` retries transient IO failures (default 2 extra attempts).
- **Auth header seam** — `headerProvider: () -> Map<String, String>` is invoked per request, so it can return freshly-read values (e.g. a current auth token); defaults to no extra headers.
- **Logging** — full `BODY` logging only when `BuildConfig.DEBUG`, `NONE` in release (bodies may contain tokens/user data).
- **Serialization** — `Json { ignoreUnknownKeys = true }` via `asConverterFactory("application/json")`; kotlinx-serialization only (no Gson).

It is wired in `app`'s `mainModule`, which provides the `Retrofit` that `networkingModule` then turns into a `TemplateService`:

```kotlin
// app mainModule
single { RetrofitFactory() }
single { get<RetrofitFactory>().retrofit(baseUrl = get<AppConfiguration>().baseUrl) }
```

## Koin wiring (networkingModule)

```kotlin
val networkingModule = module {
    single { get<Retrofit>().create(TemplateService::class.java) }
    single { TemplateNetwork(templateService = get()) }
    single<TemplateRepository> {
        DefaultTemplateRepository(templateNetwork = get(), dispatcherProvider = get())
    }
}
```

## Adding a new endpoint

1. **Service** — add the call to `TemplateService`:

   ```kotlin
   @GET("widgets/{id}")
   suspend fun getWidget(@Path("id") id: String): Response<WidgetDto>
   ```

2. **DTO** — add a `@Serializable` model (kotlinx-serialization):

   ```kotlin
   @Serializable
   data class WidgetDto(val id: String, val name: String)
   ```

3. **Network** — wrap it in `TemplateNetwork` with `safeApiCall`, mapping the body to your domain/return type:

   ```kotlin
   suspend fun getWidget(id: String): BaseResult<WidgetDto, NetworkError> =
       safeApiCall(apiCall = { templateService.getWidget(id) }) { response ->
           response.body() ?: error("Empty body")
       }
   ```

4. **Repository** — expose it through the domain interface and run it on IO. Add the method to `TemplateRepository` in `:domain`, then implement it:

   ```kotlin
   override suspend fun getWidget(id: String) = withContext(dispatcherProvider.io) {
       templateNetwork.getWidget(id)
   }
   ```

No DI changes are needed for new methods on existing types. Add new `single { … }` bindings only when you introduce a new service or network class.

## Testing

JUnit5 + MockK + coroutines-test, with `TestDispatcherProvider` for deterministic threading. See `ToResultTest`, `SafeApiCallTest`, `TemplateNetworkTest`, and `DefaultTemplateRepositoryTest`.
