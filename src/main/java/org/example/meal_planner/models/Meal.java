package org.example.meal_planner.models;

import java.util.ArrayList;
import java.util.List;

public class Meal {
  private String mealType; // "breakfast", "lunch", "dinner"
  private List<Food> foods;

  public Meal() {
    this.foods = new ArrayList<>();
  }

  public Meal(String mealType) {
    this.mealType = mealType;
    this.foods = new ArrayList<>();
  }

  // Getters and Setters
  public String getMealType() { return mealType; }
  public void setMealType(String mealType) { this.mealType = mealType; }

  public List<Food> getFoods() { return foods; }
  public void setFoods(List<Food> foods) { this.foods = foods; }

  public void addFood(Food food) { this.foods.add(food); }

  // Calculate total nutrition for this meal
  public double getTotalCalories() {
    return roundToOneDecimal(foods.stream().mapToDouble(Food::getFoodCalories).sum());
  }

  public double getTotalCarbs() {
    return roundToOneDecimal(foods.stream().mapToDouble(Food::getFoodCarbsG).sum());
  }

  public double getTotalFat() {
    return roundToOneDecimal(foods.stream().mapToDouble(Food::getFoodFatG).sum());
  }

  public double getTotalProtein() {
    return roundToOneDecimal(foods.stream().mapToDouble(Food::getFoodProteinG).sum());
  }

  public static double roundToOneDecimal(double value) {
    return Math.round(value * 10.0) / 10.0;
  }
}
