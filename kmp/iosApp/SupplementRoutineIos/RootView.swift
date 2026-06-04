import SwiftUI

struct RootView: View {
    @ObservedObject var viewModel: SharedRoutineViewModel

    var body: some View {
        TabView {
            TodayShellView(viewModel: viewModel)
                .tabItem {
                    Label("오늘", systemImage: "checkmark.circle.fill")
                }

            PlaceholderShellView(
                title: "영양제",
                message: "iOS local store에 저장된 영양제 목록입니다.",
                systemImage: "leaf",
                supplements: viewModel.supplements
            )
            .tabItem {
                Label("영양제", systemImage: "leaf")
            }

            PlaceholderShellView(
                title: "기록",
                message: "\(viewModel.progressText)가 iOS local store에서 복원되었습니다.",
                systemImage: "clock.arrow.circlepath",
                supplements: viewModel.supplements
            )
            .tabItem {
                Label("기록", systemImage: "clock.arrow.circlepath")
            }

            PlaceholderShellView(
                title: "설정",
                message: viewModel.iosFallbackMessage,
                systemImage: "gearshape",
                supplements: []
            )
            .tabItem {
                Label("설정", systemImage: "gearshape")
            }
        }
    }
}

private struct TodayShellView: View {
    let viewModel: SharedRoutineViewModel

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 24) {
                Text(viewModel.appName)
                    .font(.largeTitle.bold())
                    .foregroundStyle(Color(red: 0.13, green: 0.14, blue: 0.17))

                VStack(alignment: .leading, spacing: 8) {
                    Text("KMP shared module connected")
                        .font(.headline)
                    Text(viewModel.destinationLabelText)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                .padding(18)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(red: 1.00, green: 0.90, blue: 0.93))
                .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))

                VStack(alignment: .leading, spacing: 12) {
                    HStack {
                        Text("오늘의 루틴")
                            .font(.headline)
                        Spacer()
                        Text(viewModel.progressText)
                            .font(.subheadline.weight(.semibold))
                            .foregroundStyle(Color(red: 0.91, green: 0.37, blue: 0.48))
                    }

                    if viewModel.supplements.isEmpty {
                        Text("저장된 영양제가 없습니다. 아래 버튼으로 iOS local store 저장/복원 경로를 확인할 수 있습니다.")
                            .font(.body)
                            .foregroundStyle(.secondary)
                    } else {
                        ForEach(viewModel.supplements) { supplement in
                            SupplementRow(
                                supplement: supplement,
                                isDone: viewModel.record(for: supplement.id)?.isDone == true
                            )
                        }
                    }
                }
                .padding(18)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(.white)
                .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))

                VStack(spacing: 12) {
                    Button("로컬 영양제 추가") {
                        viewModel.addLocalSupplement()
                    }
                    .buttonStyle(.borderedProminent)

                    Button("첫 항목 체크/해제") {
                        viewModel.toggleFirstSupplementRecord()
                    }
                    .buttonStyle(.bordered)
                    .disabled(viewModel.supplements.isEmpty)

                    Button("iOS 로컬 데이터 초기화", role: .destructive) {
                        viewModel.resetLocalData()
                    }
                    .buttonStyle(.borderless)
                    .disabled(viewModel.supplements.isEmpty)
                }
                .frame(maxWidth: .infinity)

                Spacer()
            }
            .padding(24)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
            .background(Color(red: 1.00, green: 0.99, blue: 0.98))
            .navigationTitle("오늘")
        }
    }
}

private struct PlaceholderShellView: View {
    let title: String
    let message: String
    let systemImage: String
    let supplements: [StoredSupplement]

    var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                Image(systemName: systemImage)
                    .font(.system(size: 44, weight: .semibold))
                    .foregroundStyle(Color(red: 0.91, green: 0.37, blue: 0.48))

                Text(title)
                    .font(.title2.bold())

                Text(message)
                    .font(.body)
                    .multilineTextAlignment(.center)
                    .foregroundStyle(.secondary)
                    .padding(.horizontal, 24)

                if !supplements.isEmpty {
                    VStack(spacing: 8) {
                        ForEach(supplements) { supplement in
                            Text("\(supplement.scheduledTimeText) · \(supplement.name) · \(supplement.dosageText)")
                                .font(.subheadline)
                                .foregroundStyle(Color(red: 0.13, green: 0.14, blue: 0.17))
                        }
                    }
                    .padding(18)
                    .frame(maxWidth: .infinity)
                    .background(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    .padding(.horizontal, 24)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color(red: 1.00, green: 0.99, blue: 0.98))
            .navigationTitle(title)
        }
    }
}

private struct SupplementRow: View {
    let supplement: StoredSupplement
    let isDone: Bool

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: isDone ? "checkmark.circle.fill" : "circle")
                .foregroundStyle(isDone ? Color(red: 0.14, green: 0.72, blue: 0.54) : .secondary)

            VStack(alignment: .leading, spacing: 4) {
                Text(supplement.name)
                    .font(.body.weight(.semibold))
                Text("\(supplement.scheduledTimeText) · \(supplement.dosageText)")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }

            Spacer()

            Text(isDone ? "완료됨" : "미완료")
                .font(.caption.weight(.semibold))
                .foregroundStyle(isDone ? Color(red: 0.14, green: 0.72, blue: 0.54) : .secondary)
        }
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(supplement.name), \(supplement.scheduledTimeText), \(isDone ? "완료됨" : "미완료")")
    }
}

#Preview {
    RootView(viewModel: SharedRoutineViewModel())
}
