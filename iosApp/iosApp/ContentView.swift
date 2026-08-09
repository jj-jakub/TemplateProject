import SwiftUI
import Presentation

// Bridges the shared Compose UI (MainViewController() in iosMain) into SwiftUI.
struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
