package org.example.meal_planner.models;

public class DayMealPlan {
  private Meal breakfast;
  private Meal lunch;
  private Meal dinner;

  public DayMealPlan() {}

  // Getters and Setters
  public Meal getBreakfast() { return breakfast; }
  public void setBreakfast(Meal breakfast) { this.breakfast = breakfast; }

  public Meal getLunch() { return lunch; }
  public void setLunch(Meal lunch) { this.lunch = lunch; }

  public Meal getDinner() { return dinner; }
  public void setDinner(Meal dinner) { this.dinner = dinner; }

  // Calculate total nutrition for the entire day
  public double getTotalDayCalories() {
    double total = 0;
    if (breakfast != null) total += breakfast.getTotalCalories();
    if (lunch != null) total += lunch.getTotalCalories();
    if (dinner != null) total += dinner.getTotalCalories();
    return total;
  }

  public double getTotalDayCarbs() {
    double total = 0;
    if (breakfast != null) total += breakfast.getTotalCarbs();
    if (lunch != null) total += lunch.getTotalCarbs();
    if (dinner != null) total += dinner.getTotalCarbs();
    return total;
  }

  public double getTotalDayFat() {
    double total = 0;
    if (breakfast != null) total += breakfast.getTotalFat();
    if (lunch != null) total += lunch.getTotalFat();
    if (dinner != null) total += dinner.getTotalFat();
    return total;
  }

  public double getTotalDayProtein() {
    double total = 0;
    if (breakfast != null) total += breakfast.getTotalProtein();
    if (lunch != null) total += lunch.getTotalProtein();
    if (dinner != null) total += dinner.getTotalProtein();
    return total;
  }
}
