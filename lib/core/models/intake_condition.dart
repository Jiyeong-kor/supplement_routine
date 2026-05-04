enum IntakeCondition {
  beforeMeal('식전'),
  afterMeal('식후'),
  betweenMeals('식간'),
  fasting('공복'),
  beforeSleep('취침 전'),
  none('상관없음');

  final String label;
  const IntakeCondition(this.label);
}
