import Foundation
import UserNotifications

struct IosNotificationPermissionState: Equatable {
    let statusText: String
    let canScheduleReminders: Bool

    static let unknown = IosNotificationPermissionState(
        statusText: "확인 전",
        canScheduleReminders: false
    )
}

protocol IosReminderScheduler {
    func currentPermissionState() async -> IosNotificationPermissionState
    func requestAuthorization() async -> IosNotificationPermissionState
    func syncDailyReminders(for supplements: [StoredSupplement]) async throws -> Int
    func cancelRoutineReminders() async
}

enum IosReminderSchedulerError: Error {
    case invalidScheduledTime(String)
}

final class UserNotificationReminderScheduler: IosReminderScheduler {
    private let center: UNUserNotificationCenter
    private let identifierPrefix = "supplement_routine_ios_reminder_"

    init(center: UNUserNotificationCenter = .current()) {
        self.center = center
    }

    func currentPermissionState() async -> IosNotificationPermissionState {
        let settings = await notificationSettings()
        return permissionState(from: settings.authorizationStatus)
    }

    func requestAuthorization() async -> IosNotificationPermissionState {
        let _: Bool? = try? await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Bool, Error>) in
            center.requestAuthorization(options: [.alert, .sound]) { granted, error in
                if let error {
                    continuation.resume(throwing: error)
                } else {
                    continuation.resume(returning: granted)
                }
            }
        }

        return await currentPermissionState()
    }

    func syncDailyReminders(for supplements: [StoredSupplement]) async throws -> Int {
        await cancelRoutineReminders()

        let permissionState = await currentPermissionState()
        guard permissionState.canScheduleReminders else {
            return 0
        }

        var scheduledCount = 0
        for supplement in supplements where supplement.isNotificationEnabled {
            guard let time = Self.parseScheduledTime(supplement.scheduledTimeText) else {
                throw IosReminderSchedulerError.invalidScheduledTime(supplement.scheduledTimeText)
            }

            let content = UNMutableNotificationContent()
            content.title = "복용 시간이에요"
            content.body = "\(supplement.name) \(supplement.dosageText) 복용할 시간입니다."
            content.sound = .default

            var dateComponents = DateComponents()
            dateComponents.hour = time.hour
            dateComponents.minute = time.minute

            let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
            let request = UNNotificationRequest(
                identifier: "\(identifierPrefix)\(supplement.id)",
                content: content,
                trigger: trigger
            )
            try await add(request)
            scheduledCount += 1
        }

        return scheduledCount
    }

    func cancelRoutineReminders() async {
        let identifiers = await pendingRoutineReminderIdentifiers()
        center.removePendingNotificationRequests(withIdentifiers: identifiers)
    }

    private func notificationSettings() async -> UNNotificationSettings {
        await withCheckedContinuation { continuation in
            center.getNotificationSettings { settings in
                continuation.resume(returning: settings)
            }
        }
    }

    private func pendingRoutineReminderIdentifiers() async -> [String] {
        await withCheckedContinuation { continuation in
            center.getPendingNotificationRequests { requests in
                let identifiers = requests
                    .map(\.identifier)
                    .filter { identifier in
                        identifier.hasPrefix(self.identifierPrefix)
                    }
                continuation.resume(returning: identifiers)
            }
        }
    }

    private func add(_ request: UNNotificationRequest) async throws {
        try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
            center.add(request) { error in
                if let error {
                    continuation.resume(throwing: error)
                } else {
                    continuation.resume()
                }
            }
        }
    }

    private func permissionState(from status: UNAuthorizationStatus) -> IosNotificationPermissionState {
        switch status {
        case .notDetermined:
            return IosNotificationPermissionState(statusText: "요청 전", canScheduleReminders: false)
        case .denied:
            return IosNotificationPermissionState(statusText: "거부됨", canScheduleReminders: false)
        case .authorized:
            return IosNotificationPermissionState(statusText: "허용됨", canScheduleReminders: true)
        case .provisional:
            return IosNotificationPermissionState(statusText: "임시 허용됨", canScheduleReminders: true)
        case .ephemeral:
            return IosNotificationPermissionState(statusText: "일시 허용됨", canScheduleReminders: true)
        @unknown default:
            return IosNotificationPermissionState(statusText: "알 수 없음", canScheduleReminders: false)
        }
    }

    private static func parseScheduledTime(_ text: String) -> (hour: Int, minute: Int)? {
        let parts = text.split(separator: ":")
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
