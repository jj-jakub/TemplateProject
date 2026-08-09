import SwiftUI
import Presentation

@main
struct iOSApp: App {
    init() {
        // Mirrors TemplateProjectApplication.onCreate() calling KoinLauncher.startKoin on Android.
        // bootstrapKoin() lives in IosKoin.kt, so Kotlin/Native's generated Objective-C header
        // names its top-level-function wrapper class after the file: IosKoinKt.
        IosKoinKt.bootstrapKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea()
        }
    }
}
