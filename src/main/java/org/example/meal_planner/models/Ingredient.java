package org.example.meal_planner.models;

public class Ingredient {
  // The name of the ingredient (e.g., "Chicken Breast", "Brown Rice").
  private String name;

  // The numerical amount of the ingredient required.
  private double amount;

  // The unit of measurement for the amount (e.g., "g", "kg", "ml", "l", "cup", "tbsp").
  private String unit;

  // Default constructor for XML serialization
  public Ingredient() {}

  public Ingredient(String name, double amount, String unit) {
    this.name = name;
    this.amount = amount;
    this.unit = unit;
  }

  // --- Getters and Setters ---

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }
}
