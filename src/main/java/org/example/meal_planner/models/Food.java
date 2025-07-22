package org.example.meal_planner.models;

import java.util.List;

public class Food {
  private String name;
  private double foodCarbsG;
  private double foodFatG;
  private double foodProteinG;
  private List<Ingredient> ingredients;

  // Constructors
  public Food() {}

  public Food(String name, double foodCarbsG, double foodFatG, double foodProteinG) {
    this.name = name;
    this.foodCarbsG = foodCarbsG;
    this.foodFatG = foodFatG;
    this.foodProteinG = foodProteinG;
  }

  // Backwards compatibility constructor (ignores manually entered calories)
  public Food(String name, double ignoredCalories, double foodCarbsG, double foodFatG, double foodProteinG) {
    this.name = name;
    this.foodCarbsG = foodCarbsG;
    this.foodFatG = foodFatG;
    this.foodProteinG = foodProteinG;
  }

  // Getters and Setters
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  // Auto-calculate calories from macronutrients (carbs=4cal/g, fat=9cal/g, protein=4cal/g)
  public double getFoodCalories() { 
    return (foodCarbsG * 4) + (foodFatG * 9) + (foodProteinG * 4);
  }
  
  // Keep setter for backwards compatibility but don't actually store the value
  public void setFoodCalories(double foodCalories) { 
    // Calories are auto-calculated, so we ignore manual input
  }

  public double getFoodCarbsG() { return foodCarbsG; }
  public void setFoodCarbsG(double foodCarbsG) { 
    this.foodCarbsG = foodCarbsG; 
  }

  public double getFoodFatG() { return foodFatG; }
  public void setFoodFatG(double foodFatG) { 
    this.foodFatG = foodFatG; 
  }

  public double getFoodProteinG() { return foodProteinG; }
  public void setFoodProteinG(double foodProteinG) { 
    this.foodProteinG = foodProteinG; 
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }
  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  @Override
  public String toString() {
    return String.format("%s (%.1f cal, %.1fg carbs, %.1fg fat, %.1fg protein)",
            name, getFoodCalories(), foodCarbsG, foodFatG, foodProteinG);
  }
}
