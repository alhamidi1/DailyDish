package org.example.meal_planner.models;

import java.util.ArrayList;
import java.util.List;

public class MealTemplate {
    private String name;
    private int servings;
    private double carbs;
    private double fat;
    private double protein;
    private List<Ingredient> ingredients;

    public MealTemplate() {
        this.ingredients = new ArrayList<>();
    }

    public MealTemplate(String name, int servings, double carbs, double fat, double protein) {
        this.name = name;
        this.servings = servings;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
        this.ingredients = new ArrayList<>();
    }

    // Backwards compatibility constructor (ignores manually entered calories)
    public MealTemplate(String name, int servings, double ignoredCalories, double carbs, double fat, double protein) {
        this.name = name;
        this.servings = servings;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
        this.ingredients = new ArrayList<>();
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    // Auto-calculate calories from macronutrients (carbs=4cal/g, fat=9cal/g, protein=4cal/g)
    public double getCalories() { 
        return (carbs * 4) + (fat * 9) + (protein * 4);
    }
    
    // Keep setter for backwards compatibility but don't actually store the value
    public void setCalories(double calories) { 
        // Calories are auto-calculated, so we ignore manual input
    }

    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { 
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>(); 
    }

    @Override
    public String toString() {
        return String.format("%s (%d servings, %.1f cal)", name, servings, getCalories());
    }
}
