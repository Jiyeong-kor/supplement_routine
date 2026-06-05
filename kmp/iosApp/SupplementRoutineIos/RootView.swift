import SwiftUI

private let routineBackground = Color(red: 1.00, green: 0.99, blue: 0.98)
private let routineSurface = Color.white
private let routineSurfaceSoft = Color(red: 0.97, green: 0.97, blue: 0.98)
private let routineInk = Color(red: 0.13, green: 0.14, blue: 0.17)
private let routineSubtle = Color(red: 0.40, green: 0.44, blue: 0.52)
private let routinePrimary = Color(red: 0.91, green: 0.37, blue: 0.48)
private let routinePrimarySoft = Color(red: 1.00, green: 0.90, blue: 0.93)
private let routineSuccess = Color(red: 0.14, green: 0.72, blue: 0.54)
private let routineWarning = Color(red: 0.96, green: 0.65, blue: 0.14)
private let routineBackgroundGradient = LinearGradient(
    colors: [
        routineBackground,
        Color(red: 1.00, green: 0.93, blue: 0.95),
        Color(red: 0.97, green: 0.98, blue: 0.99),
    ],
    startPoint: .top,
    endPoint: .bottom
)

struct RootView: View {
    @ObservedObject var viewModel: SharedRoutineViewModel

    var body: some View {
        TabView {
            TodayView(viewModel: viewModel)
                .tabItem {
                    Label("오늘", systemImage: "checkmark.circle.fill")
                }

            SupplementsView(viewModel: viewModel)
                .tabItem {
                    Label("영양제", systemImage: "leaf")
                }

            HistoryView(viewModel: viewModel)
                .tabItem {
                    Label("기록", systemImage: "clock.arrow.circlepath")
                }

            SettingsView(viewModel: viewModel)
                .tabItem {
                    Label("설정", systemImage: "gearshape")
                }
        }
        .tint(routinePrimary)
    }
}

private struct TodayView: View {
    @ObservedObject var viewModel: SharedRoutineViewModel

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    HeaderBlock(
                        title: "오늘",
                        subtitle: "복용할 항목을 확인하고 한 번에 기록합니다."
                    )

                    ProgressCard(viewModel: viewModel)

                    if viewModel.supplements.isEmpty {
                        EmptyStateCard(
                            systemImage: "plus.circle",
                            title: "아직 등록된 영양제가 없습니다.",
                            message: "영양제 탭에서 첫 항목을 추가하면 오늘 목록과 기록에 반영됩니다."
                        )
                    } else {
                        VStack(spacing: 10) {
                            ForEach(viewModel.supplements) { supplement in
                                SupplementChecklistRow(
                                    supplement: supplement,
                                    isDone: viewModel.record(for: supplement.id)?.isDone == true
                                ) {
                                    viewModel.toggleRecord(for: supplement.id)
                                }
                            }
                        }
                    }
                }
                .padding(20)
            }
            .background(routineBackgroundGradient)
            .navigationTitle("오늘")
            .task {
                await viewModel.refreshNotificationPermissionState()
            }
        }
    }
}

private struct SupplementsView: View {
    @ObservedObject var viewModel: SharedRoutineViewModel
    @State private var isPresentingAddForm = false
    @State private var editingSupplement: StoredSupplement?
    @State private var deletingSupplement: StoredSupplement?

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    HeaderBlock(
                        title: "영양제",
                        subtitle: "복용량, 시간, 알림 여부를 local store에 저장합니다."
                    )

                    if viewModel.supplements.isEmpty {
                        EmptyStateCard(
                            systemImage: "leaf",
                            title: "복용 루틴을 추가해보세요.",
                            message: "이름, 복용량, 시간을 입력하면 오늘 일정과 알림 예약에 사용할 수 있습니다."
                        )
                    } else {
                        VStack(spacing: 12) {
                            ForEach(viewModel.supplements) { supplement in
                                SupplementManagementCard(
                                    supplement: supplement,
                                    onEdit: {
                                        editingSupplement = supplement
                                    },
                                    onDelete: {
                                        deletingSupplement = supplement
                                    }
                                )
                            }
                        }
                    }
                }
                .padding(20)
            }
            .background(routineBackgroundGradient)
            .navigationTitle("영양제")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        isPresentingAddForm = true
                    } label: {
                        Image(systemName: "plus")
                    }
                    .accessibilityLabel("영양제 추가")
                }
            }
            .sheet(isPresented: $isPresentingAddForm) {
                SupplementFormView(
                    title: "영양제 추가",
                    supplement: nil
                ) { name, dosage, time, notificationEnabled in
                    viewModel.addSupplement(
                        name: name,
                        dosageText: dosage,
                        scheduledTimeText: time,
                        isNotificationEnabled: notificationEnabled
                    )
                }
            }
            .sheet(item: $editingSupplement) { supplement in
                SupplementFormView(
                    title: "영양제 수정",
                    supplement: supplement
                ) { name, dosage, time, notificationEnabled in
                    viewModel.updateSupplement(
                        id: supplement.id,
                        name: name,
                        dosageText: dosage,
                        scheduledTimeText: time,
                        isNotificationEnabled: notificationEnabled
                    )
                }
            }
            .confirmationDialog(
                "영양제를 삭제할까요?",
                isPresented: Binding(
                    get: { deletingSupplement != nil },
                    set: { if !$0 { deletingSupplement = nil } }
                ),
                titleVisibility: .visible
            ) {
                if let supplement = deletingSupplement {
                    Button("삭제", role: .destructive) {
                        viewModel.deleteSupplement(id: supplement.id)
                        deletingSupplement = nil
                    }
                }
                Button("취소", role: .cancel) {
                    deletingSupplement = nil
                }
            } message: {
                Text("삭제하면 오늘 기록에서도 함께 제거됩니다.")
            }
        }
    }
}

private struct HistoryView: View {
    @ObservedObject var viewModel: SharedRoutineViewModel

    private let weekdayLabels = ["일", "월", "화", "수", "목", "금", "토"]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    HeaderBlock(
                        title: "기록",
                        subtitle: "완료율과 최근 흐름을 한눈에 확인합니다."
                    )

                    HistoryOverviewCard(viewModel: viewModel)

                    VStack(alignment: .leading, spacing: 12) {
                        SectionTitle("월간 기록", subtitle: "상태는 색과 텍스트가 함께 표시됩니다.")
                        MonthGrid(summaries: viewModel.monthSummaries, weekdayLabels: weekdayLabels)
                    }

                    VStack(alignment: .leading, spacing: 12) {
                        SectionTitle("최근 기록", subtitle: "최근 2주 동안의 완료율입니다.")
                        ForEach(viewModel.recentSummaries) { summary in
                            RecentHistoryRow(summary: summary)
                        }
                    }
                }
                .padding(20)
            }
            .background(routineBackgroundGradient)
            .navigationTitle("기록")
        }
    }
}

private struct SettingsView: View {
    @ObservedObject var viewModel: SharedRoutineViewModel
    @State private var breakfast: String
    @State private var lunch: String
    @State private var dinner: String
    @State private var isShowingResetConfirmation = false

    init(viewModel: SharedRoutineViewModel) {
        _viewModel = ObservedObject(wrappedValue: viewModel)
        _breakfast = State(initialValue: viewModel.snapshot.mealTimeSettings.breakfastTimeText)
        _lunch = State(initialValue: viewModel.snapshot.mealTimeSettings.lunchTimeText)
        _dinner = State(initialValue: viewModel.snapshot.mealTimeSettings.dinnerTimeText)
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    HeaderBlock(
                        title: "설정",
                        subtitle: "식사 시간, 알림 권한, 데이터 관리를 확인합니다."
                    )

                    SettingsSection(title: "식사 시간") {
                        TimeFieldRow(label: "아침", text: $breakfast)
                        TimeFieldRow(label: "점심", text: $lunch)
                        TimeFieldRow(label: "저녁", text: $dinner)
                        if !areMealTimesValid {
                            ValidationMessage("시간은 00:00부터 23:59 사이로 입력해주세요.")
                        }
                        Button("식사 시간 저장") {
                            viewModel.updateMealTimes(
                                breakfast: breakfast,
                                lunch: lunch,
                                dinner: dinner
                            )
                        }
                        .buttonStyle(.borderedProminent)
                        .disabled(!areMealTimesValid)
                    }

                    SettingsSection(title: "알림") {
                        Label(viewModel.notificationStatusText, systemImage: "bell.badge")
                            .foregroundStyle(routineInk)

                        Button("알림 권한 요청") {
                            Task {
                                await viewModel.requestNotificationAuthorization()
                            }
                        }
                        .buttonStyle(.borderedProminent)

                        Button("복용 알림 예약") {
                            Task {
                                await viewModel.syncNotificationReminders()
                            }
                        }
                        .buttonStyle(.bordered)
                        .disabled(!viewModel.notificationPermissionState.canScheduleReminders || viewModel.supplements.isEmpty)

                        Button("테스트 알림 보내기") {
                            Task {
                                await viewModel.sendTestNotification()
                            }
                        }
                        .buttonStyle(.bordered)
                        .disabled(!viewModel.notificationPermissionState.canScheduleReminders)

                        Button("15초 뒤 테스트 알림 예약") {
                            Task {
                                await viewModel.scheduleTestNotification()
                            }
                        }
                        .buttonStyle(.bordered)
                        .disabled(!viewModel.notificationPermissionState.canScheduleReminders)

                        Button("예약 알림 취소", role: .destructive) {
                            Task {
                                await viewModel.cancelNotificationReminders()
                            }
                        }
                        .buttonStyle(.borderless)
                    }

                    SettingsSection(title: "사용 안내") {
                        Text("이 앱은 사용자가 정한 루틴을 기록하는 도구이며, 영양제 추천이나 의료 판단을 제공하지 않습니다.")
                            .font(.footnote)
                            .foregroundStyle(routineSubtle)
                    }

                    SettingsSection(title: "데이터 관리") {
                        Button("iOS 로컬 데이터 초기화", role: .destructive) {
                            isShowingResetConfirmation = true
                        }
                        .buttonStyle(.bordered)
                        .disabled(viewModel.supplements.isEmpty)
                    }
                }
                .padding(20)
            }
            .background(routineBackgroundGradient)
            .navigationTitle("설정")
            .task {
                await viewModel.refreshNotificationPermissionState()
            }
            .confirmationDialog(
                "로컬 데이터를 초기화할까요?",
                isPresented: $isShowingResetConfirmation,
                titleVisibility: .visible
            ) {
                Button("초기화", role: .destructive) {
                    viewModel.resetLocalData()
                    breakfast = "08:00"
                    lunch = "12:30"
                    dinner = "18:30"
                }
                Button("취소", role: .cancel) {}
            } message: {
                Text("영양제와 오늘 기록이 iOS local store에서 삭제됩니다.")
            }
        }
    }

    private var areMealTimesValid: Bool {
        RoutineTimeText.isValid(breakfast)
            && RoutineTimeText.isValid(lunch)
            && RoutineTimeText.isValid(dinner)
    }
}

private struct HeaderBlock: View {
    let title: String
    let subtitle: String

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.largeTitle.bold())
                .foregroundStyle(routineInk)
            Text(subtitle)
                .font(.subheadline)
                .foregroundStyle(routineSubtle)
        }
    }
}

private struct ProgressCard: View {
    @ObservedObject var viewModel: SharedRoutineViewModel

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("오늘 복용 요약")
                        .font(.headline)
                    Text(viewModel.todaySummaryText)
                        .font(.subheadline)
                        .foregroundStyle(routineSubtle)
                }
                Spacer()
                Text(viewModel.progressText)
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(routinePrimary)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 7)
                    .background(routinePrimarySoft)
                    .clipShape(Capsule())
            }

            ProgressView(value: viewModel.completionRate)
                .tint(routinePrimary)
                .accessibilityLabel("오늘 완료율")
                .accessibilityValue(viewModel.todaySummaryText)
        }
        .routineCard()
    }
}

private struct SupplementChecklistRow: View {
    let supplement: StoredSupplement
    let isDone: Bool
    let onToggle: () -> Void

    var body: some View {
        Button(action: onToggle) {
            HStack(spacing: 12) {
                Image(systemName: isDone ? "checkmark.circle.fill" : "circle")
                    .font(.title3)
                    .foregroundStyle(isDone ? routineSuccess : routineSubtle)

                VStack(alignment: .leading, spacing: 4) {
                    Text(supplement.name)
                        .font(.body.weight(.semibold))
                        .foregroundStyle(routineInk)
                    Text("\(supplement.scheduledTimeText) · \(supplement.dosageText)")
                        .font(.caption)
                        .foregroundStyle(routineSubtle)
                }

                Spacer()

                Text(isDone ? "완료됨" : "미완료")
                    .font(.caption.weight(.semibold))
                    .foregroundStyle(isDone ? routineSuccess : routineSubtle)
            }
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .routineCard()
        .accessibilityLabel("\(supplement.name), \(supplement.scheduledTimeText), \(isDone ? "완료됨" : "미완료")")
    }
}

private struct SupplementManagementCard: View {
    let supplement: StoredSupplement
    let onEdit: () -> Void
    let onDelete: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 5) {
                    Text(supplement.name)
                        .font(.headline)
                        .foregroundStyle(routineInk)
                    Text("\(supplement.scheduledTimeText) · \(supplement.dosageText)")
                        .font(.subheadline)
                        .foregroundStyle(routineSubtle)
                }
                Spacer()
                Label(
                    supplement.isNotificationEnabled ? "알림 켬" : "알림 끔",
                    systemImage: supplement.isNotificationEnabled ? "bell.fill" : "bell.slash"
                )
                .font(.caption.weight(.semibold))
                .foregroundStyle(supplement.isNotificationEnabled ? routinePrimary : routineSubtle)
            }

            HStack {
                Button("수정", action: onEdit)
                    .buttonStyle(.bordered)
                Button("삭제", role: .destructive, action: onDelete)
                    .buttonStyle(.borderless)
                Spacer()
            }
        }
        .routineCard()
    }
}

private struct SupplementFormView: View {
    let title: String
    let onSave: (String, String, String, Bool) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var name: String
    @State private var dosage: String
    @State private var time: String
    @State private var notificationEnabled: Bool

    init(
        title: String,
        supplement: StoredSupplement?,
        onSave: @escaping (String, String, String, Bool) -> Void
    ) {
        self.title = title
        self.onSave = onSave
        _name = State(initialValue: supplement?.name ?? "")
        _dosage = State(initialValue: supplement?.dosageText ?? "1정")
        _time = State(initialValue: supplement?.scheduledTimeText ?? "09:00")
        _notificationEnabled = State(initialValue: supplement?.isNotificationEnabled ?? true)
    }

    var body: some View {
        NavigationStack {
            Form {
                Section("복용 정보") {
                    TextField("이름", text: $name)
                    TextField("복용량", text: $dosage)
                    TextField("시간", text: $time)
                        .keyboardType(.numbersAndPunctuation)
                    Toggle("알림 받기", isOn: $notificationEnabled)
                    if let validationMessage {
                        ValidationMessage(validationMessage)
                    }
                }
            }
            .navigationTitle(title)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("취소") {
                        dismiss()
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("저장") {
                        onSave(name, dosage, time, notificationEnabled)
                        dismiss()
                    }
                    .disabled(validationMessage != nil)
                }
            }
        }
    }

    private var validationMessage: String? {
        if name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return "이름을 입력해주세요."
        }

        if dosage.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return "복용량을 입력해주세요."
        }

        if !RoutineTimeText.isValid(time) {
            return "시간은 00:00부터 23:59 사이로 입력해주세요."
        }

        return nil
    }
}

private struct HistoryOverviewCard: View {
    @ObservedObject var viewModel: SharedRoutineViewModel

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            Text("오늘 완료율")
                .font(.headline)
            Text("\(Int(viewModel.completionRate * 100))%")
                .font(.system(size: 42, weight: .bold))
                .foregroundStyle(routinePrimary)
            Text(viewModel.todaySummaryText)
                .font(.subheadline)
                .foregroundStyle(routineSubtle)
            ProgressView(value: viewModel.completionRate)
                .tint(routinePrimary)
        }
        .routineCard()
    }
}

private struct MonthGrid: View {
    let summaries: [DailyProgress]
    let weekdayLabels: [String]

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 6), count: 7)

    var body: some View {
        VStack(spacing: 10) {
            LazyVGrid(columns: columns, spacing: 8) {
                ForEach(weekdayLabels, id: \.self) { label in
                    Text(label)
                        .font(.caption.weight(.semibold))
                        .foregroundStyle(routineSubtle)
                }
                ForEach(gridSlots()) { slot in
                    if let summary = slot.summary {
                        MonthDayTile(summary: summary)
                    } else {
                        Color.clear
                            .frame(height: 42)
                    }
                }
            }
            HStack(spacing: 12) {
                LegendChip(color: routinePrimary, text: "높음")
                LegendChip(color: routineWarning, text: "보통")
                LegendChip(color: routineSubtle, text: "낮음")
                LegendChip(color: routineSurfaceSoft, text: "없음")
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .routineCard()
    }

    private func leadingEmptyCount() -> Int {
        guard let firstDate = summaries.first?.date else {
            return 0
        }

        return Calendar(identifier: .gregorian).component(.weekday, from: firstDate) - 1
    }

    private func gridSlots() -> [MonthGridSlot] {
        let emptySlots = (0..<leadingEmptyCount()).map { index in
            MonthGridSlot(id: "empty-\(index)", summary: nil)
        }
        let summarySlots = summaries.map { summary in
            MonthGridSlot(id: summary.id, summary: summary)
        }
        return emptySlots + summarySlots
    }
}

private struct MonthGridSlot: Identifiable {
    let id: String
    let summary: DailyProgress?
}

private struct MonthDayTile: View {
    let summary: DailyProgress

    var body: some View {
        VStack(spacing: 3) {
            Text(dayText)
                .font(.caption.weight(.semibold))
            Image(systemName: systemImage)
                .font(.caption2)
        }
        .foregroundStyle(foregroundColor)
        .frame(maxWidth: .infinity, minHeight: 42)
        .background(backgroundColor)
        .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
        .accessibilityLabel("\(dayText)일, \(summary.statusText)")
    }

    private var dayText: String {
        String(Calendar(identifier: .gregorian).component(.day, from: summary.date))
    }

    private var systemImage: String {
        guard summary.totalCount > 0 else {
            return "minus"
        }

        return summary.completionRate >= 0.8 ? "checkmark" : summary.completionRate >= 0.4 ? "minus" : "xmark"
    }

    private var backgroundColor: Color {
        guard summary.totalCount > 0 else {
            return routineSurfaceSoft
        }

        return summary.completionRate >= 0.8 ? routinePrimary : summary.completionRate >= 0.4 ? Color(red: 1.00, green: 0.93, blue: 0.82) : Color(red: 0.91, green: 0.91, blue: 0.93)
    }

    private var foregroundColor: Color {
        summary.completionRate >= 0.8 && summary.totalCount > 0 ? .white : routineInk
    }
}

private struct RecentHistoryRow: View {
    let summary: DailyProgress

    var body: some View {
        HStack(spacing: 12) {
            Text(summary.percentText)
                .font(.headline)
                .foregroundStyle(routinePrimary)
                .frame(width: 56)

            VStack(alignment: .leading, spacing: 4) {
                Text(dateText)
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(routineInk)
                Text(summary.statusText)
                    .font(.caption)
                    .foregroundStyle(routineSubtle)
            }

            Spacer()

            ProgressView(value: summary.completionRate)
                .frame(width: 84)
                .tint(routinePrimary)
        }
        .routineCard()
    }

    private var dateText: String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "ko_KR")
        formatter.dateFormat = "M월 d일"
        return formatter.string(from: summary.date)
    }
}

private struct SectionTitle: View {
    let title: String
    let subtitle: String

    init(_ title: String, subtitle: String) {
        self.title = title
        self.subtitle = subtitle
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.title3.weight(.semibold))
                .foregroundStyle(routineInk)
            Text(subtitle)
                .font(.caption)
                .foregroundStyle(routineSubtle)
        }
    }
}

private struct SettingsSection<Content: View>: View {
    let title: String
    let content: Content

    init(title: String, @ViewBuilder content: () -> Content) {
        self.title = title
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.headline)
                .foregroundStyle(routineInk)
            content
        }
        .routineCard()
    }
}

private struct TimeFieldRow: View {
    let label: String
    @Binding var text: String

    var body: some View {
        HStack {
            Text(label)
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(routineInk)
            Spacer()
            TextField("00:00", text: $text)
                .keyboardType(.numbersAndPunctuation)
                .multilineTextAlignment(.trailing)
                .frame(width: 96)
        }
    }
}

private struct EmptyStateCard: View {
    let systemImage: String
    let title: String
    let message: String

    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: systemImage)
                .font(.system(size: 34, weight: .semibold))
                .foregroundStyle(routinePrimary)
            Text(title)
                .font(.headline)
                .foregroundStyle(routineInk)
                .multilineTextAlignment(.center)
            Text(message)
                .font(.subheadline)
                .foregroundStyle(routineSubtle)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 12)
        .routineCard()
    }
}

private struct ValidationMessage: View {
    let message: String

    init(_ message: String) {
        self.message = message
    }

    var body: some View {
        Label(message, systemImage: "exclamationmark.triangle.fill")
            .font(.footnote)
            .foregroundStyle(routineWarning)
            .accessibilityLabel(message)
    }
}

private struct LegendChip: View {
    let color: Color
    let text: String

    var body: some View {
        HStack(spacing: 5) {
            Circle()
                .fill(color)
                .frame(width: 8, height: 8)
            Text(text)
                .font(.caption2.weight(.semibold))
                .foregroundStyle(routineSubtle)
        }
    }
}

private extension View {
    func routineCard() -> some View {
        self
            .padding(16)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 18, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .stroke(routineSurface.opacity(0.62), lineWidth: 1)
            )
            .shadow(color: routinePrimary.opacity(0.08), radius: 18, x: 0, y: 10)
    }
}

#Preview {
    RootView(viewModel: SharedRoutineViewModel())
}
