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
                message: "영양제 목록과 추가 flow는 shared domain contract 기준으로 연결됩니다.",
                systemImage: "leaf"
            )
            .tabItem {
                Label("영양제", systemImage: "leaf")
            }

            PlaceholderShellView(
                title: "기록",
                message: "복용 기록 요약은 shared history logic을 기준으로 맞춥니다.",
                systemImage: "clock.arrow.circlepath"
            )
            .tabItem {
                Label("기록", systemImage: "clock.arrow.circlepath")
            }

            PlaceholderShellView(
                title: "설정",
                message: viewModel.iosFallbackMessage,
                systemImage: "gearshape"
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

                Text("오늘 복용할 항목을 확인하고 기록하는 iOS shell입니다. 로컬 저장소와 알림은 다음 iOS adapter 작업에서 연결합니다.")
                    .font(.body)
                    .foregroundStyle(.secondary)

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
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color(red: 1.00, green: 0.99, blue: 0.98))
            .navigationTitle(title)
        }
    }
}

#Preview {
    RootView(viewModel: SharedRoutineViewModel())
}
