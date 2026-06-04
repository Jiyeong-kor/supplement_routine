import Foundation

struct RoutineSnapshot: Codable, Equatable {
    var supplements: [StoredSupplement]
    var records: [StoredIntakeRecord]
    var mealTimeSettings: StoredMealTimeSettings

    static let empty = RoutineSnapshot(
        supplements: [],
        records: [],
        mealTimeSettings: StoredMealTimeSettings()
    )
}

struct StoredSupplement: Codable, Equatable, Identifiable {
    let id: String
    var name: String
    var dosageText: String
    var scheduledTimeText: String
    var isNotificationEnabled: Bool
}

struct StoredIntakeRecord: Codable, Equatable, Identifiable {
    let id: String
    let supplementId: String
    let dateKey: String
    var isDone: Bool
}

struct StoredMealTimeSettings: Codable, Equatable {
    var breakfastTimeText: String = "08:00"
    var lunchTimeText: String = "12:30"
    var dinnerTimeText: String = "18:30"
}

protocol RoutineLocalStore {
    func loadSnapshot() -> RoutineSnapshot
    func saveSnapshot(_ snapshot: RoutineSnapshot)
    func clearSnapshot()
}

final class UserDefaultsRoutineStore: RoutineLocalStore {
    private let defaults: UserDefaults
    private let storageKey = "supplement_routine_ios_snapshot_v1"
    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
    }

    func loadSnapshot() -> RoutineSnapshot {
        guard let data = defaults.data(forKey: storageKey) else {
            return .empty
        }

        return (try? decoder.decode(RoutineSnapshot.self, from: data)) ?? .empty
    }

    func saveSnapshot(_ snapshot: RoutineSnapshot) {
        guard let data = try? encoder.encode(snapshot) else {
            return
        }

        defaults.set(data, forKey: storageKey)
    }

    func clearSnapshot() {
        defaults.removeObject(forKey: storageKey)
    }
}
