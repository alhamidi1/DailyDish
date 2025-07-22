package org.example.meal_planner.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.meal_planner.models.DayMealPlan;
import org.example.meal_planner.models.Food;
import org.example.meal_planner.models.GroceryItem;
import org.example.meal_planner.models.GroceryList;
import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.Meal;

public class GroceryService {
    
    // Category mappings for common ingredients
    private static final Map<String, String> INGREDIENT_CATEGORIES = new HashMap<>();
    
    static {
        // Produce
        INGREDIENT_CATEGORIES.put("tomato", "Produce");
        INGREDIENT_CATEGORIES.put("onion", "Produce");
        INGREDIENT_CATEGORIES.put("garlic", "Produce");
        INGREDIENT_CATEGORIES.put("carrot", "Produce");
        INGREDIENT_CATEGORIES.put("potato", "Produce");
        INGREDIENT_CATEGORIES.put("lettuce", "Produce");
        INGREDIENT_CATEGORIES.put("spinach", "Produce");
        INGREDIENT_CATEGORIES.put("broccoli", "Produce");
        INGREDIENT_CATEGORIES.put("bell pepper", "Produce");
        INGREDIENT_CATEGORIES.put("cucumber", "Produce");
        INGREDIENT_CATEGORIES.put("avocado", "Produce");
        INGREDIENT_CATEGORIES.put("lemon", "Produce");
        INGREDIENT_CATEGORIES.put("lime", "Produce");
        INGREDIENT_CATEGORIES.put("apple", "Produce");
        INGREDIENT_CATEGORIES.put("banana", "Produce");
        INGREDIENT_CATEGORIES.put("orange", "Produce");
        INGREDIENT_CATEGORIES.put("mushroom", "Produce");
        INGREDIENT_CATEGORIES.put("celery", "Produce");
        INGREDIENT_CATEGORIES.put("ginger", "Produce");
        INGREDIENT_CATEGORIES.put("herbs", "Produce");
        INGREDIENT_CATEGORIES.put("cilantro", "Produce");
        INGREDIENT_CATEGORIES.put("parsley", "Produce");
        INGREDIENT_CATEGORIES.put("basil", "Produce");
        
        // Protein
        INGREDIENT_CATEGORIES.put("chicken", "Protein");
        INGREDIENT_CATEGORIES.put("beef", "Protein");
        INGREDIENT_CATEGORIES.put("pork", "Protein");
        INGREDIENT_CATEGORIES.put("fish", "Protein");
        INGREDIENT_CATEGORIES.put("salmon", "Protein");
        INGREDIENT_CATEGORIES.put("tuna", "Protein");
        INGREDIENT_CATEGORIES.put("egg", "Protein");
        INGREDIENT_CATEGORIES.put("tofu", "Protein");
        INGREDIENT_CATEGORIES.put("beans", "Protein");
        INGREDIENT_CATEGORIES.put("lentil", "Protein");
        INGREDIENT_CATEGORIES.put("nuts", "Protein");
        INGREDIENT_CATEGORIES.put("turkey", "Protein");
        INGREDIENT_CATEGORIES.put("shrimp", "Protein");
        
        // Dairy
        INGREDIENT_CATEGORIES.put("milk", "Dairy");
        INGREDIENT_CATEGORIES.put("cheese", "Dairy");
        INGREDIENT_CATEGORIES.put("yogurt", "Dairy");
        INGREDIENT_CATEGORIES.put("butter", "Dairy");
        INGREDIENT_CATEGORIES.put("cream", "Dairy");
        INGREDIENT_CATEGORIES.put("sour cream", "Dairy");
        
        // Grains
        INGREDIENT_CATEGORIES.put("rice", "Grains");
        INGREDIENT_CATEGORIES.put("pasta", "Grains");
        INGREDIENT_CATEGORIES.put("bread", "Grains");
        INGREDIENT_CATEGORIES.put("flour", "Grains");
        INGREDIENT_CATEGORIES.put("oats", "Grains");
        INGREDIENT_CATEGORIES.put("quinoa", "Grains");
        INGREDIENT_CATEGORIES.put("barley", "Grains");
        INGREDIENT_CATEGORIES.put("wheat", "Grains");
        INGREDIENT_CATEGORIES.put("cereal", "Grains");
        INGREDIENT_CATEGORIES.put("noodles", "Grains");
    }
    
    /**
     * Determines the category for an ingredient based on its name
     */
    public static String categorizeIngredient(String ingredientName) {
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            return "Other";
        }
        
        String lowerName = ingredientName.toLowerCase();
        
        // Check exact matches first
        for (Map.Entry<String, String> entry : INGREDIENT_CATEGORIES.entrySet()) {
            if (lowerName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Default to Other if no category found
        return "Other";
    }
    
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
                    String category = categorizeIngredient(ingredient.getName());
                    GroceryItem groceryItem = GroceryItem.fromIngredient(ingredient, category);
                    groceryList.addItem(groceryItem);
                }
            }
        }
    }
    
    /**
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
