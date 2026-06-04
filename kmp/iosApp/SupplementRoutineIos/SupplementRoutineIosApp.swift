import SwiftUI

@main
struct SupplementRoutineIosApp: App {
    var body: some Scene {
        WindowGroup {
            RootView(viewModel: SharedRoutineViewModel())
        }
    }
}
