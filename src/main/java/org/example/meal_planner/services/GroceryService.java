package org.example.meal_planner.services;

import java.util.List;

import org.example.meal_planner.models.DayMealPlan;
import org.example.meal_planner.models.Food;
import org.example.meal_planner.models.GroceryItem;
import org.example.meal_planner.models.GroceryList;
import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.Meal;

public class GroceryService {
    
    /**
     * Generates a grocery list from the user's current meal plan
     */
    public static GroceryList generateFromMealPlan(DayMealPlan mealPlan) {
        GroceryList groceryList = new GroceryList();
        
        if (mealPlan == null) {
            return groceryList;
        }
        
        // Process all meals
        processMeal(groceryList, mealPlan.getBreakfast());
        processMeal(groceryList, mealPlan.getLunch());
        processMeal(groceryList, mealPlan.getDinner());
        
        return groceryList;
    }
    
    /**
     * Generates a grocery list from multiple day meal plans
     */
    public static GroceryList generateFromMealPlans(List<DayMealPlan> mealPlans) {
        GroceryList groceryList = new GroceryList();
        
        if (mealPlans == null || mealPlans.isEmpty()) {
            return groceryList;
        }
        
        for (DayMealPlan dayPlan : mealPlans) {
            if (dayPlan != null) {
                processMeal(groceryList, dayPlan.getBreakfast());
                processMeal(groceryList, dayPlan.getLunch());
                processMeal(groceryList, dayPlan.getDinner());
            }
        }
        
        return groceryList;
    }
    
    /**
     * Processes a single meal and adds its ingredients to the grocery list
     */
    private static void processMeal(GroceryList groceryList, Meal meal) {
        if (meal == null || meal.getFoods() == null) {
            return;
        }

        for (Food food : meal.getFoods()) {
            if (food.getIngredients() != null) {
                for (Ingredient ingredient : food.getIngredients()) {
                    GroceryItem groceryItem = GroceryItem.fromIngredient(ingredient);
                    groceryList.addItem(groceryItem);
                }
            }
        }
    }    /**
     * Loads grocery list data from XML file
     */
    public static GroceryList loadGroceryList() {
        GroceryList groceryList = XmlDataService.load("data/grocery_list.xml", GroceryList.class, GroceryItem.class);
        return groceryList != null ? groceryList : new GroceryList();
    }
    
    /**
     * Saves grocery list data to XML file
     */
    public static void saveGroceryList(GroceryList groceryList) {
        XmlDataService.save(groceryList, "data/grocery_list.xml", GroceryItem.class);
    }
    
    /**
     * Loads the current meal plan from XML
     */
    public static DayMealPlan loadCurrentMealPlan() {
        return XmlDataService.load("data/meal_plan.xml", DayMealPlan.class, Meal.class, Food.class, Ingredient.class);
    }
}
