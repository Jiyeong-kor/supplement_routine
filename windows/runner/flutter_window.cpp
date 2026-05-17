#include "flutter_window.h"

#include <optional>
#include <string>
#include <variant>

#include "flutter/generated_plugin_registrant.h"

namespace {

constexpr const wchar_t kStartupRegistryPath[] =
    L"Software\\Microsoft\\Windows\\CurrentVersion\\Run";
constexpr const wchar_t kStartupRegistryValue[] = L"SupplementRoutine";

std::wstring CurrentExecutablePath() {
  wchar_t path[MAX_PATH];
  const DWORD length = GetModuleFileName(nullptr, path, MAX_PATH);
  return std::wstring(path, length);
}

bool IsStartupEnabled() {
  HKEY key;
  if (RegOpenKeyEx(HKEY_CURRENT_USER, kStartupRegistryPath, 0, KEY_READ, &key) !=
      ERROR_SUCCESS) {
    return false;
  }

  DWORD type = 0;
  DWORD size = 0;
  const auto result =
      RegQueryValueEx(key, kStartupRegistryValue, nullptr, &type, nullptr, &size);
  RegCloseKey(key);
  return result == ERROR_SUCCESS && type == REG_SZ;
}

bool SetStartupEnabled(bool enabled) {
  HKEY key;
  if (RegCreateKeyEx(HKEY_CURRENT_USER, kStartupRegistryPath, 0, nullptr, 0,
                     KEY_SET_VALUE, nullptr, &key, nullptr) != ERROR_SUCCESS) {
    return false;
  }

  LONG result = ERROR_SUCCESS;
  if (enabled) {
    const auto path = CurrentExecutablePath();
    result = RegSetValueEx(
        key, kStartupRegistryValue, 0, REG_SZ,
        reinterpret_cast<const BYTE*>(path.c_str()),
        static_cast<DWORD>((path.size() + 1) * sizeof(wchar_t)));
  } else {
    result = RegDeleteValue(key, kStartupRegistryValue);
    if (result == ERROR_FILE_NOT_FOUND) {
      result = ERROR_SUCCESS;
    }
  }

  RegCloseKey(key);
  return result == ERROR_SUCCESS;
}

}  // namespace

FlutterWindow::FlutterWindow(const flutter::DartProject& project)
    : project_(project) {}

FlutterWindow::~FlutterWindow() {}

bool FlutterWindow::OnCreate() {
  if (!Win32Window::OnCreate()) {
    return false;
  }

  RECT frame = GetClientArea();

  // The size here must match the window dimensions to avoid unnecessary surface
  // creation / destruction in the startup path.
  flutter_controller_ = std::make_unique<flutter::FlutterViewController>(
      frame.right - frame.left, frame.bottom - frame.top, project_);
  // Ensure that basic setup of the controller was successful.
  if (!flutter_controller_->engine() || !flutter_controller_->view()) {
    return false;
  }
  RegisterPlugins(flutter_controller_->engine());
  startup_channel_ =
      std::make_unique<flutter::MethodChannel<flutter::EncodableValue>>(
          flutter_controller_->engine()->messenger(),
          "supplement_routine/windows_startup",
          &flutter::StandardMethodCodec::GetInstance());
  startup_channel_->SetMethodCallHandler(
      [](const flutter::MethodCall<flutter::EncodableValue>& call,
         std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result) {
        if (call.method_name() == "isEnabled") {
          result->Success(flutter::EncodableValue(IsStartupEnabled()));
          return;
        }

        if (call.method_name() == "setEnabled") {
          const auto* enabled = std::get_if<bool>(call.arguments());
          result->Success(
              flutter::EncodableValue(enabled != nullptr &&
                                       SetStartupEnabled(*enabled)));
          return;
        }

        result->NotImplemented();
      });
  SetChildContent(flutter_controller_->view()->GetNativeWindow());

  flutter_controller_->engine()->SetNextFrameCallback([&]() {
    this->Show();
  });

  // Flutter can complete the first frame before the "show window" callback is
  // registered. The following call ensures a frame is pending to ensure the
  // window is shown. It is a no-op if the first frame hasn't completed yet.
  flutter_controller_->ForceRedraw();

  return true;
}

void FlutterWindow::OnDestroy() {
  if (flutter_controller_) {
    flutter_controller_ = nullptr;
  }

  Win32Window::OnDestroy();
}

LRESULT
FlutterWindow::MessageHandler(HWND hwnd, UINT const message,
                              WPARAM const wparam,
                              LPARAM const lparam) noexcept {
  // Give Flutter, including plugins, an opportunity to handle window messages.
  if (flutter_controller_) {
    std::optional<LRESULT> result =
        flutter_controller_->HandleTopLevelWindowProc(hwnd, message, wparam,
                                                      lparam);
    if (result) {
      return *result;
    }
  }

  switch (message) {
    case WM_FONTCHANGE:
      flutter_controller_->engine()->ReloadSystemFonts();
      break;
  }

  return Win32Window::MessageHandler(hwnd, message, wparam, lparam);
}
