import Combine
import Foundation
import SupplementRoutineShared

@MainActor
final class SharedRoutineViewModel: ObservableObject {
    let appName: String
    let destinationLabelText: String
    let iosFallbackMessage: String
    @Published private(set) var snapshot: RoutineSnapshot
    @Published private(set) var notificationPermissionState: IosNotificationPermissionState = .unknown
    @Published private(set) var notificationStatusText: String = "알림 상태를 확인해 주세요."

    private let store: RoutineLocalStore
    private let reminderScheduler: IosReminderScheduler
    private let todayKey: String

    var supplements: [StoredSupplement] {
        snapshot.supplements
    }

    var totalCount: Int {
        snapshot.supplements.count
    }

    var completedCount: Int {
        snapshot.supplements.filter { supplement in
            record(for: supplement.id)?.isDone == true
        }.count
    }

    var progressText: String {
        "\(completedCount) / \(totalCount) 완료"
    }

    init(
        summary: SharedAppSummary = SharedAppSummary(),
        store: RoutineLocalStore = UserDefaultsRoutineStore(),
        reminderScheduler: IosReminderScheduler = UserNotificationReminderScheduler(),
        today: Date = Date()
    ) {
        self.appName = summary.appName()
        self.destinationLabelText = summary.destinationLabelText()
        self.iosFallbackMessage = summary.iosFallbackMessage()
        self.store = store
        self.reminderScheduler = reminderScheduler
        self.todayKey = Self.dateKey(for: today)
        self.snapshot = store.loadSnapshot()
    }

    func addLocalSupplement() {
        let nextIndex = snapshot.supplements.count + 1
        let supplement = StoredSupplement(
            id: UUID().uuidString,
            name: "영양제 \(nextIndex)",
            dosageText: "1정",
            scheduledTimeText: "09:00",
            isNotificationEnabled: true
        )

        mutateSnapshot { snapshot in
            snapshot.supplements.append(supplement)
            snapshot.records.append(
                StoredIntakeRecord(
                    id: "r_\(supplement.id)_\(todayKey)",
                    supplementId: supplement.id,
                    dateKey: todayKey,
                    isDone: false
                )
            )
        }
    }

    func toggleFirstSupplementRecord() {
        guard let firstSupplement = snapshot.supplements.first else {
            return
        }

        mutateSnapshot { snapshot in
            if let recordIndex = snapshot.records.firstIndex(where: { record in
                record.supplementId == firstSupplement.id && record.dateKey == todayKey
            }) {
                snapshot.records[recordIndex].isDone.toggle()
            }
        }
    }

    func resetLocalData() {
        snapshot = .empty
        store.clearSnapshot()
    }

    func refreshNotificationPermissionState() async {
        notificationPermissionState = await reminderScheduler.currentPermissionState()
        notificationStatusText = "알림 권한: \(notificationPermissionState.statusText)"
    }

    func requestNotificationAuthorization() async {
        notificationPermissionState = await reminderScheduler.requestAuthorization()
        notificationStatusText = "알림 권한: \(notificationPermissionState.statusText)"
    }

    func syncNotificationReminders() async {
        do {
            let count = try await reminderScheduler.syncDailyReminders(for: snapshot.supplements)
            notificationPermissionState = await reminderScheduler.currentPermissionState()
            notificationStatusText = count > 0 ? "\(count)개 알림을 예약했습니다." : "예약된 알림이 없습니다."
        } catch {
            notificationStatusText = "알림 예약에 실패했습니다."
        }
    }

    func cancelNotificationReminders() async {
        await reminderScheduler.cancelRoutineReminders()
        notificationStatusText = "예약된 iOS 알림을 취소했습니다."
    }

    func record(for supplementId: String) -> StoredIntakeRecord? {
        snapshot.records.first { record in
            record.supplementId == supplementId && record.dateKey == todayKey
        }
    }

    private func mutateSnapshot(_ mutation: (inout RoutineSnapshot) -> Void) {
        var nextSnapshot = snapshot
        mutation(&nextSnapshot)
        snapshot = nextSnapshot
        store.saveSnapshot(nextSnapshot)
    }

    private static func dateKey(for date: Date) -> String {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .gregorian)
        formatter.locale = Locale(identifier: "ko_KR")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: date)
    }
}
