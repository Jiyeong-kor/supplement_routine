import Foundation
import SupplementRoutineShared

final class SharedRoutineViewModel: ObservableObject {
    let appName: String
    let destinationLabelText: String
    let iosFallbackMessage: String

    init(summary: SharedAppSummary = SharedAppSummary()) {
        self.appName = summary.appName()
        self.destinationLabelText = summary.destinationLabelText()
        self.iosFallbackMessage = summary.iosFallbackMessage()
    }
}
