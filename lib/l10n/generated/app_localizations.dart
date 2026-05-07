import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_localizations_ko.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'generated/app_localizations.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale)
    : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations)!;
  }

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates =
      <LocalizationsDelegate<dynamic>>[
        delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[Locale('ko')];

  /// No description provided for @appTitle.
  ///
  /// In ko, this message translates to:
  /// **'Supplement Routine'**
  String get appTitle;

  /// No description provided for @navToday.
  ///
  /// In ko, this message translates to:
  /// **'오늘'**
  String get navToday;

  /// No description provided for @navSupplements.
  ///
  /// In ko, this message translates to:
  /// **'영양제'**
  String get navSupplements;

  /// No description provided for @navHistory.
  ///
  /// In ko, this message translates to:
  /// **'기록'**
  String get navHistory;

  /// No description provided for @navSettings.
  ///
  /// In ko, this message translates to:
  /// **'설정'**
  String get navSettings;

  /// No description provided for @todayAppBarTitle.
  ///
  /// In ko, this message translates to:
  /// **'오늘의 복용'**
  String get todayAppBarTitle;

  /// No description provided for @todayDate.
  ///
  /// In ko, this message translates to:
  /// **'{year}년 {month}월 {day}일 {weekday}요일'**
  String todayDate(int year, int month, int day, String weekday);

  /// No description provided for @weekdayMonday.
  ///
  /// In ko, this message translates to:
  /// **'월'**
  String get weekdayMonday;

  /// No description provided for @weekdayTuesday.
  ///
  /// In ko, this message translates to:
  /// **'화'**
  String get weekdayTuesday;

  /// No description provided for @weekdayWednesday.
  ///
  /// In ko, this message translates to:
  /// **'수'**
  String get weekdayWednesday;

  /// No description provided for @weekdayThursday.
  ///
  /// In ko, this message translates to:
  /// **'목'**
  String get weekdayThursday;

  /// No description provided for @weekdayFriday.
  ///
  /// In ko, this message translates to:
  /// **'금'**
  String get weekdayFriday;

  /// No description provided for @weekdaySaturday.
  ///
  /// In ko, this message translates to:
  /// **'토'**
  String get weekdaySaturday;

  /// No description provided for @weekdaySunday.
  ///
  /// In ko, this message translates to:
  /// **'일'**
  String get weekdaySunday;

  /// No description provided for @todayMemoQuote.
  ///
  /// In ko, this message translates to:
  /// **'내 메모 · {supplementName}: {memo}'**
  String todayMemoQuote(String supplementName, String memo);

  /// No description provided for @habitQuoteCheckAfterTaking.
  ///
  /// In ko, this message translates to:
  /// **'복용 후 바로 체크하면 오늘 기록이 더 정확해집니다.'**
  String get habitQuoteCheckAfterTaking;

  /// No description provided for @habitQuoteFixedRoutine.
  ///
  /// In ko, this message translates to:
  /// **'복용 시간을 고정하면 루틴을 확인하기 쉽습니다.'**
  String get habitQuoteFixedRoutine;

  /// No description provided for @habitQuoteReviewToday.
  ///
  /// In ko, this message translates to:
  /// **'오늘 복용할 항목을 확인하고 완료 여부를 기록해보세요.'**
  String get habitQuoteReviewToday;

  /// No description provided for @todayRoutineTitle.
  ///
  /// In ko, this message translates to:
  /// **'오늘의 루틴'**
  String get todayRoutineTitle;

  /// No description provided for @todayProgressCount.
  ///
  /// In ko, this message translates to:
  /// **'{done} / {total} 완료'**
  String todayProgressCount(int done, int total);

  /// No description provided for @todayListTitle.
  ///
  /// In ko, this message translates to:
  /// **'복용 목록'**
  String get todayListTitle;

  /// No description provided for @todayEmptyTitle.
  ///
  /// In ko, this message translates to:
  /// **'등록된 복용 일정이 없습니다'**
  String get todayEmptyTitle;

  /// No description provided for @todayEmptyDescription.
  ///
  /// In ko, this message translates to:
  /// **'오른쪽 아래 + 버튼으로 영양제를 등록해보세요.'**
  String get todayEmptyDescription;

  /// No description provided for @supplementListTitle.
  ///
  /// In ko, this message translates to:
  /// **'영양제'**
  String get supplementListTitle;

  /// No description provided for @deleteSupplementTitle.
  ///
  /// In ko, this message translates to:
  /// **'영양제 삭제'**
  String get deleteSupplementTitle;

  /// No description provided for @deleteSupplementMessage.
  ///
  /// In ko, this message translates to:
  /// **'{supplementName}을(를) 삭제하시겠습니까?'**
  String deleteSupplementMessage(String supplementName);

  /// No description provided for @cancel.
  ///
  /// In ko, this message translates to:
  /// **'취소'**
  String get cancel;

  /// No description provided for @delete.
  ///
  /// In ko, this message translates to:
  /// **'삭제'**
  String get delete;

  /// No description provided for @edit.
  ///
  /// In ko, this message translates to:
  /// **'수정'**
  String get edit;

  /// No description provided for @supplementDailyCount.
  ///
  /// In ko, this message translates to:
  /// **'{methodLabel} · 하루 {dailyCount}회'**
  String supplementDailyCount(String methodLabel, int dailyCount);

  /// No description provided for @supplementDosage.
  ///
  /// In ko, this message translates to:
  /// **'1회 {dosage} {unit}'**
  String supplementDosage(String dosage, String unit);

  /// No description provided for @supplementEmptyTitle.
  ///
  /// In ko, this message translates to:
  /// **'등록된 영양제가 없습니다'**
  String get supplementEmptyTitle;

  /// No description provided for @supplementEmptyDescription.
  ///
  /// In ko, this message translates to:
  /// **'오른쪽 아래 + 버튼으로 영양제를 등록해보세요.'**
  String get supplementEmptyDescription;

  /// No description provided for @supplementAddTitle.
  ///
  /// In ko, this message translates to:
  /// **'영양제 등록'**
  String get supplementAddTitle;

  /// No description provided for @supplementEditTitle.
  ///
  /// In ko, this message translates to:
  /// **'영양제 수정'**
  String get supplementEditTitle;

  /// No description provided for @supplementNameSection.
  ///
  /// In ko, this message translates to:
  /// **'영양제 이름'**
  String get supplementNameSection;

  /// No description provided for @supplementNameHint.
  ///
  /// In ko, this message translates to:
  /// **'예: 비타민 D, 오메가3'**
  String get supplementNameHint;

  /// No description provided for @supplementDosageSection.
  ///
  /// In ko, this message translates to:
  /// **'1회 복용량'**
  String get supplementDosageSection;

  /// No description provided for @supplementUnitSection.
  ///
  /// In ko, this message translates to:
  /// **'단위'**
  String get supplementUnitSection;

  /// No description provided for @supplementMethodSection.
  ///
  /// In ko, this message translates to:
  /// **'복용 방식 선택'**
  String get supplementMethodSection;

  /// No description provided for @supplementRoutineMethod.
  ///
  /// In ko, this message translates to:
  /// **'식사/생활 루틴'**
  String get supplementRoutineMethod;

  /// No description provided for @supplementManualTimeMethod.
  ///
  /// In ko, this message translates to:
  /// **'직접 시간 지정'**
  String get supplementManualTimeMethod;

  /// No description provided for @supplementTimingSection.
  ///
  /// In ko, this message translates to:
  /// **'언제 복용하시나요? (다중 선택)'**
  String get supplementTimingSection;

  /// No description provided for @supplementSpecificTimeMethod.
  ///
  /// In ko, this message translates to:
  /// **'특정 시각 지정'**
  String get supplementSpecificTimeMethod;

  /// No description provided for @supplementIntervalMethod.
  ///
  /// In ko, this message translates to:
  /// **'일정 간격 반복'**
  String get supplementIntervalMethod;

  /// No description provided for @supplementDailyCountLabel.
  ///
  /// In ko, this message translates to:
  /// **'하루 복용 횟수'**
  String get supplementDailyCountLabel;

  /// No description provided for @supplementScheduledTimeLabel.
  ///
  /// In ko, this message translates to:
  /// **'복용 시각 {index}'**
  String supplementScheduledTimeLabel(int index);

  /// No description provided for @supplementStartTimeLabel.
  ///
  /// In ko, this message translates to:
  /// **'첫 복용 시작 시각'**
  String get supplementStartTimeLabel;

  /// No description provided for @supplementIntervalHoursLabel.
  ///
  /// In ko, this message translates to:
  /// **'반복 간격(시간)'**
  String get supplementIntervalHoursLabel;

  /// No description provided for @supplementIntervalNotice.
  ///
  /// In ko, this message translates to:
  /// **'※ 오늘 자정 전까지만 일정이 생성됩니다.'**
  String get supplementIntervalNotice;

  /// No description provided for @supplementOtherSettingsSection.
  ///
  /// In ko, this message translates to:
  /// **'기타 설정'**
  String get supplementOtherSettingsSection;

  /// No description provided for @supplementNotificationSwitch.
  ///
  /// In ko, this message translates to:
  /// **'복용 알림 받기'**
  String get supplementNotificationSwitch;

  /// No description provided for @supplementMemoSection.
  ///
  /// In ko, this message translates to:
  /// **'메모 (선택)'**
  String get supplementMemoSection;

  /// No description provided for @supplementMemoHint.
  ///
  /// In ko, this message translates to:
  /// **'주의사항 등을 적어주세요'**
  String get supplementMemoHint;

  /// No description provided for @supplementAddDone.
  ///
  /// In ko, this message translates to:
  /// **'등록 완료'**
  String get supplementAddDone;

  /// No description provided for @supplementEditDone.
  ///
  /// In ko, this message translates to:
  /// **'수정 완료'**
  String get supplementEditDone;

  /// No description provided for @supplementNameRequired.
  ///
  /// In ko, this message translates to:
  /// **'영양제 이름을 입력해주세요.'**
  String get supplementNameRequired;

  /// No description provided for @supplementDosageInvalid.
  ///
  /// In ko, this message translates to:
  /// **'1회 복용량은 0보다 큰 숫자로 입력해주세요.'**
  String get supplementDosageInvalid;

  /// No description provided for @supplementTimingRequired.
  ///
  /// In ko, this message translates to:
  /// **'복용 시간대를 하나 이상 선택해주세요.'**
  String get supplementTimingRequired;

  /// No description provided for @historyTitle.
  ///
  /// In ko, this message translates to:
  /// **'기록'**
  String get historyTitle;

  /// No description provided for @historyTodayDate.
  ///
  /// In ko, this message translates to:
  /// **'오늘 · {month}월 {day}일 {weekday}요일'**
  String historyTodayDate(int month, int day, String weekday);

  /// No description provided for @historyCompletion.
  ///
  /// In ko, this message translates to:
  /// **'완료율 {percent}% ({done}/{total})'**
  String historyCompletion(int percent, int done, int total);

  /// No description provided for @historyEmptyToday.
  ///
  /// In ko, this message translates to:
  /// **'오늘 등록된 복용 일정이 없습니다.'**
  String get historyEmptyToday;

  /// No description provided for @settingsTitle.
  ///
  /// In ko, this message translates to:
  /// **'설정'**
  String get settingsTitle;

  /// No description provided for @settingsDefaultSection.
  ///
  /// In ko, this message translates to:
  /// **'기본 설정'**
  String get settingsDefaultSection;

  /// No description provided for @settingsMealTimeTitle.
  ///
  /// In ko, this message translates to:
  /// **'식사 시간 설정'**
  String get settingsMealTimeTitle;

  /// No description provided for @settingsMealTimeSummary.
  ///
  /// In ko, this message translates to:
  /// **'아침 {breakfast} · 점심 {lunch} · 저녁 {dinner}'**
  String settingsMealTimeSummary(String breakfast, String lunch, String dinner);

  /// No description provided for @settingsNotificationTitle.
  ///
  /// In ko, this message translates to:
  /// **'알림 설정'**
  String get settingsNotificationTitle;

  /// No description provided for @settingsNotificationOn.
  ///
  /// In ko, this message translates to:
  /// **'새 영양제 등록 시 알림을 기본으로 켭니다'**
  String get settingsNotificationOn;

  /// No description provided for @settingsNotificationOff.
  ///
  /// In ko, this message translates to:
  /// **'새 영양제 등록 시 알림을 기본으로 끕니다'**
  String get settingsNotificationOff;

  /// No description provided for @settingsDataSection.
  ///
  /// In ko, this message translates to:
  /// **'데이터 관리'**
  String get settingsDataSection;

  /// No description provided for @settingsResetTitle.
  ///
  /// In ko, this message translates to:
  /// **'데이터 초기화'**
  String get settingsResetTitle;

  /// No description provided for @settingsResetMessage.
  ///
  /// In ko, this message translates to:
  /// **'모든 영양제 정보와 복용 기록이 삭제됩니다. 정말 초기화하시겠습니까?'**
  String get settingsResetMessage;

  /// No description provided for @settingsResetConfirm.
  ///
  /// In ko, this message translates to:
  /// **'초기화'**
  String get settingsResetConfirm;

  /// No description provided for @settingsResetDone.
  ///
  /// In ko, this message translates to:
  /// **'데이터가 초기화되었습니다.'**
  String get settingsResetDone;

  /// No description provided for @settingsInfoSection.
  ///
  /// In ko, this message translates to:
  /// **'정보'**
  String get settingsInfoSection;

  /// No description provided for @settingsUsageGuideTitle.
  ///
  /// In ko, this message translates to:
  /// **'앱 사용 가이드'**
  String get settingsUsageGuideTitle;

  /// No description provided for @settingsDisclaimerTitle.
  ///
  /// In ko, this message translates to:
  /// **'면책 고지'**
  String get settingsDisclaimerTitle;

  /// No description provided for @settingsVersionTitle.
  ///
  /// In ko, this message translates to:
  /// **'앱 버전'**
  String get settingsVersionTitle;

  /// No description provided for @settingsMealTimeDescription.
  ///
  /// In ko, this message translates to:
  /// **'오늘 일정 계산에 사용할 기본 식사 시간입니다'**
  String get settingsMealTimeDescription;

  /// No description provided for @mealBreakfast.
  ///
  /// In ko, this message translates to:
  /// **'아침'**
  String get mealBreakfast;

  /// No description provided for @mealLunch.
  ///
  /// In ko, this message translates to:
  /// **'점심'**
  String get mealLunch;

  /// No description provided for @mealDinner.
  ///
  /// In ko, this message translates to:
  /// **'저녁'**
  String get mealDinner;

  /// No description provided for @confirm.
  ///
  /// In ko, this message translates to:
  /// **'확인'**
  String get confirm;

  /// No description provided for @usageGuideStepRegisterTitle.
  ///
  /// In ko, this message translates to:
  /// **'1. 영양제를 등록하세요'**
  String get usageGuideStepRegisterTitle;

  /// No description provided for @usageGuideStepRegisterDescription.
  ///
  /// In ko, this message translates to:
  /// **'이름, 복용 방식, 복용 조건, 알림 여부, 메모를 직접 입력합니다.'**
  String get usageGuideStepRegisterDescription;

  /// No description provided for @usageGuideStepTodayTitle.
  ///
  /// In ko, this message translates to:
  /// **'2. 오늘 화면에서 확인하세요'**
  String get usageGuideStepTodayTitle;

  /// No description provided for @usageGuideStepTodayDescription.
  ///
  /// In ko, this message translates to:
  /// **'입력한 규칙을 기준으로 오늘의 복용 일정과 진행률을 확인합니다.'**
  String get usageGuideStepTodayDescription;

  /// No description provided for @usageGuideStepCheckTitle.
  ///
  /// In ko, this message translates to:
  /// **'3. 복용 후 체크하세요'**
  String get usageGuideStepCheckTitle;

  /// No description provided for @usageGuideStepCheckDescription.
  ///
  /// In ko, this message translates to:
  /// **'복용을 완료하면 오늘 목록에서 해당 일정을 체크합니다.'**
  String get usageGuideStepCheckDescription;

  /// No description provided for @usageGuideStepHistoryTitle.
  ///
  /// In ko, this message translates to:
  /// **'4. 기록 화면에서 돌아보세요'**
  String get usageGuideStepHistoryTitle;

  /// No description provided for @usageGuideStepHistoryDescription.
  ///
  /// In ko, this message translates to:
  /// **'오늘 완료 개수와 완료율을 확인해 복용 기록을 관리합니다.'**
  String get usageGuideStepHistoryDescription;

  /// No description provided for @usageGuideDisclaimer.
  ///
  /// In ko, this message translates to:
  /// **'Supplement Routine은 영양제를 추천하거나 의료 조언을 제공하지 않습니다.'**
  String get usageGuideDisclaimer;

  /// No description provided for @disclaimerBody.
  ///
  /// In ko, this message translates to:
  /// **'본 앱은 사용자가 입력한 정보를 기반으로 복용 일정을 관리해주는 도구일 뿐, 의료적 조언이나 진단을 제공하지 않습니다.\n\n영양제 복용에 관한 결정은 전문의와 상담하시기 바랍니다. 앱에서 제공하는 정보의 오류나 누락으로 인한 책임은 사용자에게 있습니다.'**
  String get disclaimerBody;
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) =>
      <String>['ko'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {
  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'ko':
      return AppLocalizationsKo();
  }

  throw FlutterError(
    'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
    'an issue with the localizations generation tool. Please file an issue '
    'on GitHub with a reproducible sample app and the gen-l10n configuration '
    'that was used.',
  );
}
