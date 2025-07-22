package org.example.meal_planner.services;

import java.util.ArrayList;
import java.util.List;

import org.example.meal_planner.models.DayMealPlan;
import org.example.meal_planner.models.Food;
import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.Meal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MealPlanParser {
    public static List<DayMealPlan> parseJsonToMealPlan(String jsonString) {
        List<DayMealPlan> mealPlans = new ArrayList<>();

        try {
            // Create ObjectMapper to parse JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Get the "meals" array
            JsonNode mealsArray = rootNode.get("meals");

            // Loop through each day in the meals array
            for (JsonNode dayNode : mealsArray) {
                DayMealPlan dayMealPlan = new DayMealPlan();

                // Parse breakfast
                if (dayNode.has("breakfast")) {
                    Meal breakfast = parseMeal(dayNode.get("breakfast"), "breakfast");
                    dayMealPlan.setBreakfast(breakfast);
                }

                // Parse lunch
                if (dayNode.has("lunch")) {
                    Meal lunch = parseMeal(dayNode.get("lunch"), "lunch");
                    dayMealPlan.setLunch(lunch);
                }

                // Parse dinner
                if (dayNode.has("dinner")) {
                    Meal dinner = parseMeal(dayNode.get("dinner"), "dinner");
                    dayMealPlan.setDinner(dinner);
                }

                mealPlans.add(dayMealPlan);
            }

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return mealPlans;
    }

    // Helper method to parse individual meals
    private static Meal parseMeal(JsonNode mealNode, String mealType) {
        Meal meal = new Meal(mealType);

        // Get the "foods" array within this meal
        JsonNode foodsArray = mealNode.get("foods");

        // Loop through each food item
        for (JsonNode foodNode : foodsArray) {
            Food food = new Food();

            // Extract food properties
            food.setName(foodNode.path("name").asText());
            food.setFoodCalories(roundToOneDecimal(foodNode.path("food_calories").asDouble()));
            food.setFoodCarbsG(roundToOneDecimal(foodNode.path("food_carbs_g").asDouble()));
            food.setFoodFatG(roundToOneDecimal(foodNode.path("food_fat_g").asDouble()));
            food.setFoodProteinG(roundToOneDecimal(foodNode.path("food_protein_g").asDouble()));

            if (foodNode.has("ingredients")) {
                List<Ingredient> ingredients = new ArrayList<>();
                JsonNode ingredientsArray = foodNode.get("ingredients");
                for (JsonNode ingredientNode : ingredientsArray) {
                    String name = ingredientNode.path("name").asText();
                    double amount = ingredientNode.path("amount").asDouble();
                    String unit = ingredientNode.path("unit").asText();
                    ingredients.add(new Ingredient(name, amount, unit));
                }
                food.setIngredients(ingredients);
            }

            meal.addFood(food);
        }
        return meal;
    }
    private static double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

}