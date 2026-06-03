package com.jiyeong.supplementroutine.shared.domain

enum class IntakeMethod {
    FixedTime,
    MealBased,
    Interval,
}

enum class IntakeCondition {
    BeforeMeal,
    AfterMeal,
    BetweenMeals,
    Fasting,
    BeforeSleep,
    None,
}

enum class MealType {
    Breakfast,
    Lunch,
    Dinner,
}
