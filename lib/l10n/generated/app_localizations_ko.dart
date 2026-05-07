// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Korean (`ko`).
class AppLocalizationsKo extends AppLocalizations {
  AppLocalizationsKo([String locale = 'ko']) : super(locale);

  @override
  String get appTitle => 'Supplement Routine';

  @override
  String get navToday => '오늘';

  @override
  String get navSupplements => '영양제';

  @override
  String get navHistory => '기록';

  @override
  String get navSettings => '설정';

  @override
  String get todayAppBarTitle => '오늘의 복용';

  @override
  String todayDate(int year, int month, int day, String weekday) {
    return '$year년 $month월 $day일 $weekday요일';
  }

  @override
  String get weekdayMonday => '월';

  @override
  String get weekdayTuesday => '화';

  @override
  String get weekdayWednesday => '수';

  @override
  String get weekdayThursday => '목';

  @override
  String get weekdayFriday => '금';

  @override
  String get weekdaySaturday => '토';

  @override
  String get weekdaySunday => '일';

  @override
  String todayMemoQuote(String supplementName, String memo) {
    return '내 메모 · $supplementName: $memo';
  }

  @override
  String get habitQuoteCheckAfterTaking => '복용 후 바로 체크하면 오늘 기록이 더 정확해집니다.';

  @override
  String get habitQuoteFixedRoutine => '복용 시간을 고정하면 루틴을 확인하기 쉽습니다.';

  @override
  String get habitQuoteReviewToday => '오늘 복용할 항목을 확인하고 완료 여부를 기록해보세요.';

  @override
  String get todayRoutineTitle => '오늘의 루틴';

  @override
  String todayProgressCount(int done, int total) {
    return '$done / $total 완료';
  }

  @override
  String get todayListTitle => '복용 목록';

  @override
  String get todayEmptyTitle => '등록된 복용 일정이 없습니다';

  @override
  String get todayEmptyDescription => '오른쪽 아래 + 버튼으로 영양제를 등록해보세요.';

  @override
  String get supplementListTitle => '영양제';

  @override
  String get deleteSupplementTitle => '영양제 삭제';

  @override
  String deleteSupplementMessage(String supplementName) {
    return '$supplementName을(를) 삭제하시겠습니까?';
  }

  @override
  String get cancel => '취소';

  @override
  String get delete => '삭제';

  @override
  String get edit => '수정';

  @override
  String supplementDailyCount(String methodLabel, int dailyCount) {
    return '$methodLabel · 하루 $dailyCount회';
  }

  @override
  String supplementDosage(String dosage, String unit) {
    return '1회 $dosage $unit';
  }

  @override
  String get supplementEmptyTitle => '등록된 영양제가 없습니다';

  @override
  String get supplementEmptyDescription => '오른쪽 아래 + 버튼으로 영양제를 등록해보세요.';

  @override
  String get historyTitle => '기록';

  @override
  String historyTodayDate(int month, int day, String weekday) {
    return '오늘 · $month월 $day일 $weekday요일';
  }

  @override
  String historyCompletion(int percent, int done, int total) {
    return '완료율 $percent% ($done/$total)';
  }

  @override
  String get historyEmptyToday => '오늘 등록된 복용 일정이 없습니다.';

  @override
  String get settingsTitle => '설정';

  @override
  String get settingsDefaultSection => '기본 설정';

  @override
  String get settingsMealTimeTitle => '식사 시간 설정';

  @override
  String settingsMealTimeSummary(
    String breakfast,
    String lunch,
    String dinner,
  ) {
    return '아침 $breakfast · 점심 $lunch · 저녁 $dinner';
  }

  @override
  String get settingsNotificationTitle => '알림 설정';

  @override
  String get settingsNotificationOn => '새 영양제 등록 시 알림을 기본으로 켭니다';

  @override
  String get settingsNotificationOff => '새 영양제 등록 시 알림을 기본으로 끕니다';

  @override
  String get settingsDataSection => '데이터 관리';

  @override
  String get settingsResetTitle => '데이터 초기화';

  @override
  String get settingsResetMessage => '모든 영양제 정보와 복용 기록이 삭제됩니다. 정말 초기화하시겠습니까?';

  @override
  String get settingsResetConfirm => '초기화';

  @override
  String get settingsResetDone => '데이터가 초기화되었습니다.';

  @override
  String get settingsInfoSection => '정보';

  @override
  String get settingsUsageGuideTitle => '앱 사용 가이드';

  @override
  String get settingsDisclaimerTitle => '면책 고지';

  @override
  String get settingsVersionTitle => '앱 버전';

  @override
  String get settingsMealTimeDescription => '오늘 일정 계산에 사용할 기본 식사 시간입니다';

  @override
  String get mealBreakfast => '아침';

  @override
  String get mealLunch => '점심';

  @override
  String get mealDinner => '저녁';

  @override
  String get confirm => '확인';

  @override
  String get usageGuideStepRegisterTitle => '1. 영양제를 등록하세요';

  @override
  String get usageGuideStepRegisterDescription =>
      '이름, 복용 방식, 복용 조건, 알림 여부, 메모를 직접 입력합니다.';

  @override
  String get usageGuideStepTodayTitle => '2. 오늘 화면에서 확인하세요';

  @override
  String get usageGuideStepTodayDescription =>
      '입력한 규칙을 기준으로 오늘의 복용 일정과 진행률을 확인합니다.';

  @override
  String get usageGuideStepCheckTitle => '3. 복용 후 체크하세요';

  @override
  String get usageGuideStepCheckDescription => '복용을 완료하면 오늘 목록에서 해당 일정을 체크합니다.';

  @override
  String get usageGuideStepHistoryTitle => '4. 기록 화면에서 돌아보세요';

  @override
  String get usageGuideStepHistoryDescription =>
      '오늘 완료 개수와 완료율을 확인해 복용 기록을 관리합니다.';

  @override
  String get usageGuideDisclaimer =>
      'Supplement Routine은 영양제를 추천하거나 의료 조언을 제공하지 않습니다.';

  @override
  String get disclaimerBody =>
      '본 앱은 사용자가 입력한 정보를 기반으로 복용 일정을 관리해주는 도구일 뿐, 의료적 조언이나 진단을 제공하지 않습니다.\n\n영양제 복용에 관한 결정은 전문의와 상담하시기 바랍니다. 앱에서 제공하는 정보의 오류나 누락으로 인한 책임은 사용자에게 있습니다.';
}
