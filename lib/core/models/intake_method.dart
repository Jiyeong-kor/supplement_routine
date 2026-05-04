enum IntakeMethod {
  fixedTime('정해진 시간'),
  mealBased('식사 기준'),
  interval('일정 간격');

  final String label;
  const IntakeMethod(this.label);
}
