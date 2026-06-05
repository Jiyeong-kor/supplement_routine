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

    var todaySummaryText: String {
        guard totalCount > 0 else {
            return "오늘 예정된 복용 일정이 없습니다."
        }

        let percent = Int(completionRate * 100)
        return "\(percent)% 완료 · \(completedCount) / \(totalCount) 완료"
    }

    var completionRate: Double {
        guard totalCount > 0 else {
            return 0
        }

        return Double(completedCount) / Double(totalCount)
    }

    var monthSummaries: [DailyProgress] {
        let calendar = Calendar(identifier: .gregorian)
        let today = Date()
        guard let range = calendar.range(of: .day, in: .month, for: today),
              let monthInterval = calendar.dateInterval(of: .month, for: today) else {
            return []
        }

        return range.compactMap { day -> DailyProgress? in
            guard let date = calendar.date(
                byAdding: .day,
                value: day - 1,
                to: monthInterval.start
            ) else {
                return nil
            }

            return progress(for: Self.dateKey(for: date), date: date)
        }
    }

    var recentSummaries: [DailyProgress] {
        let calendar = Calendar(identifier: .gregorian)
        let today = Date()
        return (0..<14).compactMap { offset -> DailyProgress? in
            guard let date = calendar.date(byAdding: .day, value: -offset, to: today) else {
                return nil
            }

            return progress(for: Self.dateKey(for: date), date: date)
        }
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
        ensureTodayRecords()
    }

    func addSupplement(
        name: String,
        dosageText: String,
        scheduledTimeText: String,
        isNotificationEnabled: Bool
    ) {
        let supplement = StoredSupplement(
            id: UUID().uuidString,
            name: normalized(name, fallback: "영양제"),
            dosageText: normalized(dosageText, fallback: "1정"),
            scheduledTimeText: RoutineTimeText.normalized(scheduledTimeText, fallback: "09:00"),
            isNotificationEnabled: isNotificationEnabled
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

    func updateSupplement(
        id: String,
        name: String,
        dosageText: String,
        scheduledTimeText: String,
        isNotificationEnabled: Bool
    ) {
        mutateSnapshot { snapshot in
            guard let index = snapshot.supplements.firstIndex(where: { $0.id == id }) else {
                return
            }

            snapshot.supplements[index].name = normalized(name, fallback: "영양제")
            snapshot.supplements[index].dosageText = normalized(dosageText, fallback: "1정")
            snapshot.supplements[index].scheduledTimeText = RoutineTimeText.normalized(scheduledTimeText, fallback: "09:00")
            snapshot.supplements[index].isNotificationEnabled = isNotificationEnabled
        }
    }

    func deleteSupplement(id: String) {
        mutateSnapshot { snapshot in
            snapshot.supplements.removeAll { $0.id == id }
            snapshot.records.removeAll { $0.supplementId == id }
        }
    }

    func toggleRecord(for supplementId: String) {
        guard snapshot.supplements.contains(where: { $0.id == supplementId }) else {
            return
        }

        mutateSnapshot { snapshot in
            if let recordIndex = snapshot.records.firstIndex(where: { record in
                record.supplementId == supplementId && record.dateKey == todayKey
            }) {
                snapshot.records[recordIndex].isDone.toggle()
            } else {
                snapshot.records.append(
                    StoredIntakeRecord(
                        id: "r_\(supplementId)_\(todayKey)",
                        supplementId: supplementId,
                        dateKey: todayKey,
                        isDone: true
                    )
                )
            }
        }
    }

    func updateMealTimes(breakfast: String, lunch: String, dinner: String) {
        mutateSnapshot { snapshot in
            snapshot.mealTimeSettings.breakfastTimeText = RoutineTimeText.normalized(breakfast, fallback: "08:00")
            snapshot.mealTimeSettings.lunchTimeText = RoutineTimeText.normalized(lunch, fallback: "12:30")
            snapshot.mealTimeSettings.dinnerTimeText = RoutineTimeText.normalized(dinner, fallback: "18:30")
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

    func sendTestNotification() async {
        do {
            try await reminderScheduler.sendTestReminder()
            notificationPermissionState = await reminderScheduler.currentPermissionState()
            notificationStatusText = notificationPermissionState.canScheduleReminders
                ? "테스트 알림을 보냈습니다."
                : "알림 권한이 필요합니다."
        } catch {
            notificationStatusText = "테스트 알림 전송에 실패했습니다."
        }
    }

    func scheduleTestNotification() async {
        do {
            try await reminderScheduler.scheduleTestReminder(after: 15)
            notificationPermissionState = await reminderScheduler.currentPermissionState()
            notificationStatusText = notificationPermissionState.canScheduleReminders
                ? "15초 뒤 테스트 알림을 예약했습니다."
                : "알림 권한이 필요합니다."
        } catch {
            notificationStatusText = "테스트 알림 예약에 실패했습니다."
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

    private func ensureTodayRecords() {
        var nextSnapshot = snapshot
        for supplement in nextSnapshot.supplements
        where !nextSnapshot.records.contains(where: { $0.supplementId == supplement.id && $0.dateKey == todayKey }) {
            nextSnapshot.records.append(
                StoredIntakeRecord(
                    id: "r_\(supplement.id)_\(todayKey)",
                    supplementId: supplement.id,
                    dateKey: todayKey,
                    isDone: false
                )
            )
        }

        if nextSnapshot != snapshot {
            snapshot = nextSnapshot
            store.saveSnapshot(nextSnapshot)
        }
    }

    private func progress(for dateKey: String, date: Date) -> DailyProgress {
        let records = snapshot.records.filter { $0.dateKey == dateKey }
        let total = dateKey == todayKey ? snapshot.supplements.count : records.count
        let done = records.filter(\.isDone).count
        return DailyProgress(
            date: date,
            dateKey: dateKey,
            doneCount: done,
            totalCount: total
        )
    }

    private func normalized(_ text: String, fallback: String) -> String {
        let trimmed = text.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.isEmpty ? fallback : trimmed
    }

    private static func dateKey(for date: Date) -> String {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .gregorian)
        formatter.locale = Locale(identifier: "ko_KR")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: date)
    }
}

struct DailyProgress: Identifiable, Equatable {
    let date: Date
    let dateKey: String
    let doneCount: Int
    let totalCount: Int

    var id: String {
        dateKey
    }

    var completionRate: Double {
        guard totalCount > 0 else {
            return 0
        }

        return Double(doneCount) / Double(totalCount)
    }

    var percentText: String {
        "\(Int(completionRate * 100))%"
    }

    var statusText: String {
        guard totalCount > 0 else {
            return "일정 없음"
        }

        return "\(doneCount) / \(totalCount) 완료"
    }
}

enum RoutineTimeText {
    static func isValid(_ text: String) -> Bool {
        parse(text) != nil
    }

    static func normalized(_ text: String, fallback: String) -> String {
        guard let time = parse(text) else {
            return fallback
        }

        return String(format: "%02d:%02d", time.hour, time.minute)
    }

    private static func parse(_ text: String) -> (hour: Int, minute: Int)? {
        let trimmed = text.trimmingCharacters(in: .whitespacesAndNewlines)
        let parts = trimmed.split(separator: ":")
        guard parts.count == 2,
              let hour = Int(parts[0]),
              let minute = Int(parts[1]),
              (0...23).contains(hour),
              (0...59).contains(minute) else {
            return nil
        }

        return (hour, minute)
    }
}
