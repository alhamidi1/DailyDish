package org.example.meal_planner.models;

public class GroceryItem {
    private String name;
    private double amount;
    private String unit;
    private String category;
    private boolean purchased;
    private boolean generated; // true if auto-generated from meals, false if manually added

    // Default constructor for XML serialization
    public GroceryItem() {}

    public GroceryItem(String name, double amount, String unit, String category) {
        this(name, amount, unit, category, false, false);
    }

    public GroceryItem(String name, double amount, String unit, String category, boolean purchased, boolean generated) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.category = category;
        this.purchased = purchased;
        this.generated = generated;
    }

    // Static method to create from Ingredient
    public static GroceryItem fromIngredient(Ingredient ingredient, String category) {
        return new GroceryItem(ingredient.getName(), ingredient.getAmount(), ingredient.getUnit(), category, false, true);
    }

    // Getters and Setters
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    @Override
    public String toString() {
        if (amount > 0 && unit != null && !unit.isEmpty()) {
            return String.format("%s (%.1f %s)", name, amount, unit);
        } else {
            return name;
        }
    }

    // Method to combine amounts when same ingredient appears multiple times
    public void addAmount(double additionalAmount) {
        this.amount += additionalAmount;
    }

    // Check if two grocery items are the same (same name and unit)
    public boolean isSameItem(GroceryItem other) {
        if (other == null) return false;
        return this.name.equalsIgnoreCase(other.name) && 
               this.unit.equalsIgnoreCase(other.unit);
    }
}
