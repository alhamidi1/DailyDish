package org.example.meal_planner.controllers.Dashboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.example.meal_planner.controllers.Profile_Setup.ProfileSetupData;
import org.example.meal_planner.models.DayMealPlan;
import org.example.meal_planner.models.Food;
import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.Meal;
import org.example.meal_planner.services.Ai;
import org.example.meal_planner.services.MealPlanParser;
import org.example.meal_planner.services.MealPlanXmlParser;
import org.example.meal_planner.services.XmlDataService;
import org.example.meal_planner.utils.ModalUtils;
import org.example.meal_planner.utils.NavigationUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


public class TodayDashboardController {

    @FXML private BorderPane rootPane; // Reference to root pane for modal alerts
    private StackPane loadingOverlay; // Store reference to loading overlay
    
    // FXML binding for the PieChart
    @FXML private PieChart nutritionPieChart;
    @FXML private VBox breakfastMealsVBox;
    @FXML private VBox lunchMealsVBox;
    @FXML private VBox dinnerMealsVBox;
    @FXML private Label totalCaloriesLabel;
    @FXML private Label totalCarbsLabel;
    @FXML private Label totalFatLabel;
    @FXML private Label totalProteinLabel;
    @FXML private Label targetCaloriesLabel;
    @FXML private Label targetCarbsLabel;
    @FXML private Label targetFatLabel;
    @FXML private Label targetProteinLabel;
    @FXML private HBox warningBox;
    @FXML private Label warningLabel;
    @FXML private javafx.scene.control.Button generateMealsButton;
    @FXML private Label statusLabel;
    @FXML private Label greetingLabel;
    @FXML private Label suggestionLabel;
    
    // Progress bars and labels
    @FXML private ProgressBar caloriesProgressBar;
    @FXML private ProgressBar carbsProgressBar;
    @FXML private ProgressBar fatProgressBar;
    @FXML private ProgressBar proteinProgressBar;
    @FXML private Label caloriesProgressLabel;
    @FXML private Label carbsProgressLabel;
    @FXML private Label fatProgressLabel;
    @FXML private Label proteinProgressLabel;
    
    // Add Food buttons
    @FXML private javafx.scene.control.Button breakfastAddButton;
    @FXML private javafx.scene.control.Button lunchAddButton;
    @FXML private javafx.scene.control.Button dinnerAddButton;
    
    // Store the current meal plan
    private DayMealPlan currentMealPlan;
    private static final String CHECKBOX_STATES_FILE = "data/checkbox_states.properties";

    @FXML
    public void initialize() {
        // Initialize the navigation system with this BorderPane
        NavigationUtils.initializeMainLayout(rootPane);
        
        // Set up the greeting first
        setupGreeting();
        
        // Load the meal plan using XmlDataService (matches how we save it)
        currentMealPlan = XmlDataService.load("data/meal_plan.xml", DayMealPlan.class, Meal.class, Food.class, Ingredient.class);
        
        // If no meal plan exists, create an empty one
        if (currentMealPlan == null) {
            currentMealPlan = new DayMealPlan();
        }
        
        updateDashboard(currentMealPlan);
        
        // Update nutrition display based on saved checkbox states
        updateNutritionFromCheckboxes();
        
        // Show generate button if meal plan is empty/sparse
        updateGenerateButtonVisibility();
    }
    
    private void setupGreeting() {
        if (greetingLabel != null) {
            String greeting = getTimeBasedGreeting();
            greetingLabel.setText(greeting);
        }
        
        // Set initial suggestion (will be updated after meal plan loads)
        if (suggestionLabel != null) {
            suggestionLabel.setText("Enjoy your personalized meal plan!");
        }
    }
    
    private String getTimeBasedGreeting() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        
        if (hour >= 5 && hour < 12) {
            return "Good Morning! 🌅";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon! ☀️";
        } else if (hour >= 17 && hour < 21) {
            return "Good Evening! 🌇";
        } else {
            return "Good Night! 🌙";
        }
    }
    
    private void updateSuggestionText() {
        if (suggestionLabel == null || currentMealPlan == null) return;
        
        // Check current meal plan state
        if (isAllMealsEmpty()) {
            suggestionLabel.setText("Your meal plan is empty. Generate meals to get started!");
        } else if (areAllMealsChecked()) {
            suggestionLabel.setText("Great! All your meals are selected. Let's generate new meals!");
        } else {
            suggestionLabel.setText("Enjoy your personalized meal plan!");
        }
    }
    
    private boolean isAllMealsEmpty() {
        return (currentMealPlan.getBreakfast() == null || currentMealPlan.getBreakfast().getFoods().isEmpty()) &&
               (currentMealPlan.getLunch() == null || currentMealPlan.getLunch().getFoods().isEmpty()) &&
               (currentMealPlan.getDinner() == null || currentMealPlan.getDinner().getFoods().isEmpty());
    }
    
    private boolean areAllMealsChecked() {
        boolean breakfastResult = areAllMealsInSectionChecked(breakfastMealsVBox);
        boolean lunchResult = areAllMealsInSectionChecked(lunchMealsVBox);
        boolean dinnerResult = areAllMealsInSectionChecked(dinnerMealsVBox);
        
        boolean breakfastEmpty = isSectionEmpty(breakfastMealsVBox);
        boolean lunchEmpty = isSectionEmpty(lunchMealsVBox);
        boolean dinnerEmpty = isSectionEmpty(dinnerMealsVBox);
        
        // Return true if each section is either fully checked or empty
        return (breakfastResult || breakfastEmpty) && 
               (lunchResult || lunchEmpty) && 
               (dinnerResult || dinnerEmpty);
    }

    private boolean isSectionEmpty(VBox mealVBox) {
        for (javafx.scene.Node child : mealVBox.getChildren()) {
            // Skip empty state labels
            if (child instanceof Label) {
                continue;
            }
            
            if (child instanceof HBox) {
                HBox foodCard = (HBox) child;
                // Skip if this is the "Add Food" button container
                if (foodCard.getStyleClass().contains("button-container")) {
                    continue;
                }
                
                // If we find any actual meal card, section is not empty
                return false;
            }
        }
        return true; // No meal cards found, section is empty
    }
    
    private boolean areAllMealsInSectionChecked(VBox mealVBox) {
        boolean hasAnyMeals = false;
        for (javafx.scene.Node child : mealVBox.getChildren()) {
            // Skip empty state labels (they are direct children of VBox)
            if (child instanceof Label) {
                continue;
            }
            
            if (child instanceof HBox) {
                HBox foodCard = (HBox) child;
                // Skip if this is the "Add Food" button container
                if (foodCard.getStyleClass().contains("button-container")) {
                    continue;
                }
                
                hasAnyMeals = true;
                // Look for unchecked checkboxes
                for (javafx.scene.Node cardChild : foodCard.getChildren()) {
                    if (cardChild instanceof CheckBox) {
                        CheckBox checkbox = (CheckBox) cardChild;
                        if (!checkbox.isSelected()) {
                            return false; // Found an unchecked meal
                        }
                    }
                }
            }
        }
        return hasAnyMeals; // All meals are checked (and there are meals)
    }

    private void updateDashboard(DayMealPlan dayMealPlan) {
        this.currentMealPlan = dayMealPlan;
        populateMealSection(breakfastMealsVBox, dayMealPlan.getBreakfast());
        populateMealSection(lunchMealsVBox, dayMealPlan.getLunch());
        populateMealSection(dinnerMealsVBox, dayMealPlan.getDinner());
        
        // Initialize progress bars with zero values (since no checkboxes are selected initially)
        updateProgressBars(0, 0, 0, 0);
        
        updateGenerateButtonVisibility();
        
        // Update suggestion text based on new meal state
        updateSuggestionText();
    }

    private void populateMealSection(VBox mealVBox, Meal meal) {
        // Load persistent checkbox states
        Map<String, Boolean> persistentStates = loadCheckboxStates();
        String sectionName = getMealSectionName(mealVBox);
        
        // Store current checkbox states before clearing (for current session)
        Map<String, Boolean> currentStates = new HashMap<>();
        for (javafx.scene.Node child : mealVBox.getChildren()) {
            if (child instanceof HBox) {
                HBox foodCard = (HBox) child;
                // Skip if this is the "Add Food" button container
                if (foodCard.getStyleClass().contains("button-container")) {
                    continue;
                }
                
                // Find the checkbox and food name to store state
                CheckBox checkbox = null;
                String foodName = null;
                
                for (javafx.scene.Node cardChild : foodCard.getChildren()) {
                    if (cardChild instanceof CheckBox) {
                        checkbox = (CheckBox) cardChild;
                    } else if (cardChild instanceof VBox) {
                        VBox foodInfoVBox = (VBox) cardChild;
                        for (javafx.scene.Node infoChild : foodInfoVBox.getChildren()) {
                            if (infoChild instanceof Label) {
                                Label label = (Label) infoChild;
                                if (label.getStyleClass().contains("meal-name")) {
                                    foodName = label.getText();
                                    break;
                                }
                            }
                        }
                    }
                }
                
                // Store the checkbox state with food name as key
                if (checkbox != null && foodName != null) {
                    currentStates.put(foodName, checkbox.isSelected());
                }
            }
        }
        
        mealVBox.getChildren().clear();
        if (meal != null && !meal.getFoods().isEmpty()) {
            for (Food food : meal.getFoods()) {
                HBox foodHBox = createFoodCard(food, meal);
                
                // Determine checkbox state: first check current session, then persistent storage
                boolean shouldBeSelected = false;
                String foodName = food.getName();
                
                // Priority 1: Current session state (if food existed before)
                if (currentStates.containsKey(foodName)) {
                    shouldBeSelected = currentStates.get(foodName);
                } 
                // Priority 2: Persistent storage state
                else {
                    String persistentKey = sectionName + "." + foodName;
                    shouldBeSelected = persistentStates.getOrDefault(persistentKey, false);
                }
                
                // Set the checkbox state
                if (shouldBeSelected) {
                    for (javafx.scene.Node child : foodHBox.getChildren()) {
                        if (child instanceof CheckBox) {
                            CheckBox checkbox = (CheckBox) child;
                            checkbox.setSelected(true);
                            break;
                        }
                    }
                }
                
                mealVBox.getChildren().add(foodHBox);
                // Ensure the meal card expands to fill the container width
                VBox.setVgrow(foodHBox, javafx.scene.layout.Priority.NEVER);
                foodHBox.prefWidthProperty().bind(mealVBox.widthProperty().subtract(20)); // Subtract for padding/margins
            }
        } else {
            // Show empty state message when no foods are present
            Label emptyStateLabel = new Label("No food added yet.");
            emptyStateLabel.getStyleClass().add("empty-state-label");
            mealVBox.getChildren().add(emptyStateLabel);
        }
    }

    private HBox createFoodCard(Food food, Meal meal) {
        HBox foodHBox = new HBox();
        foodHBox.setSpacing(10);
        foodHBox.getStyleClass().add("meal-card");
        
        // Set HBox to fill available width and align children properly
        foodHBox.setFillHeight(true);
        foodHBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        foodHBox.setMaxWidth(Double.MAX_VALUE);

        // Checkbox for meal selection
        CheckBox mealCheckBox = new CheckBox();
        mealCheckBox.getStyleClass().add("meal-checkbox");
        mealCheckBox.setSelected(false); // Default to unchecked
        mealCheckBox.setOnAction(event -> {
            updateNutritionFromCheckboxes();
            saveCheckboxStates(); // Save states when checkbox changes
            updateSuggestionText(); // Update suggestion text when checkbox state changes
        });

        // Food name and nutrition info container
        VBox foodInfoVBox = new VBox();
        foodInfoVBox.setSpacing(5);
        foodInfoVBox.setFillWidth(true);
        HBox.setHgrow(foodInfoVBox, javafx.scene.layout.Priority.ALWAYS);

        Label foodNameLabel = new Label(food.getName());
        foodNameLabel.getStyleClass().add("meal-name");
        foodNameLabel.setWrapText(true);
        foodNameLabel.setMaxWidth(Double.MAX_VALUE); // Allow label to expand fully

        // Nutrition facts stacked vertically
        Label caloriesLabel = new Label(String.format("Calories: %.1f", food.getFoodCalories()));
        caloriesLabel.getStyleClass().add("meal-calories");

        Label proteinLabel = new Label(String.format("Protein: %.1fg", food.getFoodProteinG()));
        proteinLabel.getStyleClass().add("meal-protein");

        Label carbsLabel = new Label(String.format("Carbs: %.1fg", food.getFoodCarbsG()));
        carbsLabel.getStyleClass().add("meal-carbs");

        Label fatLabel = new Label(String.format("Fat: %.1fg", food.getFoodFatG()));
        fatLabel.getStyleClass().add("meal-fat");

        foodInfoVBox.getChildren().addAll(foodNameLabel, caloriesLabel, proteinLabel, carbsLabel, fatLabel);

        // Spacer to push delete button to the right
        Region spacer = new Region();
        spacer.getStyleClass().add("spacer");
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Delete button with SVG icon
        Button deleteButton = new Button();
        SVGPath trashIcon = new SVGPath();
        trashIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        deleteButton.setGraphic(trashIcon);
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> deleteFood(food, meal));

        foodHBox.getChildren().addAll(mealCheckBox, foodInfoVBox, spacer, deleteButton);
        return foodHBox;
    }

    private void deleteFood(Food food, Meal meal) {
        if (meal != null && meal.getFoods() != null) {
            meal.getFoods().remove(food);
            
            // Save the changes to XML file
            MealPlanXmlParser.saveMealPlan("data/meal_plan.xml", currentMealPlan);
            
            // Refresh the entire dashboard to reflect changes (checkbox states will be preserved)
            updateDashboard(currentMealPlan);
            
            // Update nutrition display based on preserved checkbox states
            updateNutritionFromCheckboxes();
        }
    }

    private void addFood(Meal meal) {
        // Open meal list manager as a popup window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/meal_planner/Dashboard_Resources/fxml/content/MealListManager.fxml"));
            Parent root = loader.load();
            
            MealListManagerController controller = loader.getController();
            controller.setSelectedMealType(meal.getMealType());
            controller.setDashboardController(this); // Pass reference to dashboard controller
            
            // Create new popup stage
            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Meal List Manager");
            popupStage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            popupStage.initOwner(breakfastMealsVBox.getScene().getWindow()); // Set parent window
            popupStage.setResizable(false);
            
            // Show popup and wait for it to close
            popupStage.showAndWait();
            
            // Refresh dashboard when popup closes
            refreshDashboard();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Action methods for FXML buttons
    @FXML
    private void addBreakfastFood() {
        if (currentMealPlan != null && currentMealPlan.getBreakfast() != null) {
            addFood(currentMealPlan.getBreakfast());
        }
    }
    
    @FXML
    private void addLunchFood() {
        if (currentMealPlan != null && currentMealPlan.getLunch() != null) {
            addFood(currentMealPlan.getLunch());
        }
    }
    
    @FXML
    private void addDinnerFood() {
        if (currentMealPlan != null && currentMealPlan.getDinner() != null) {
            addFood(currentMealPlan.getDinner());
        }
    }

    // Method to refresh the dashboard after meals are added
    public void refreshDashboard() {
        // Reload the meal plan from XML using XmlDataService
        currentMealPlan = XmlDataService.load("data/meal_plan.xml", DayMealPlan.class, Meal.class, Food.class, Ingredient.class);
        
        // If no meal plan exists, create an empty one
        if (currentMealPlan == null) {
            currentMealPlan = new DayMealPlan();
        }
        
        updateDashboard(currentMealPlan);
        
        // Update nutrition display based on preserved checkbox states
        updateNutritionFromCheckboxes();
    }

    // Method to update nutrition based on checked meals only
    private void updateNutritionFromCheckboxes() {
        double totalCalories = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalProtein = 0;

        // Iterate through all meal sections to find checked items
        totalCalories += getCheckedNutritionFromSection(breakfastMealsVBox, "calories");
        totalCarbs += getCheckedNutritionFromSection(breakfastMealsVBox, "carbs");
        totalFat += getCheckedNutritionFromSection(breakfastMealsVBox, "fat");
        totalProtein += getCheckedNutritionFromSection(breakfastMealsVBox, "protein");

        totalCalories += getCheckedNutritionFromSection(lunchMealsVBox, "calories");
        totalCarbs += getCheckedNutritionFromSection(lunchMealsVBox, "carbs");
        totalFat += getCheckedNutritionFromSection(lunchMealsVBox, "fat");
        totalProtein += getCheckedNutritionFromSection(lunchMealsVBox, "protein");

        totalCalories += getCheckedNutritionFromSection(dinnerMealsVBox, "calories");
        totalCarbs += getCheckedNutritionFromSection(dinnerMealsVBox, "carbs");
        totalFat += getCheckedNutritionFromSection(dinnerMealsVBox, "fat");
        totalProtein += getCheckedNutritionFromSection(dinnerMealsVBox, "protein");

        // Update progress bars and labels
        updateProgressBars(totalCalories, totalCarbs, totalFat, totalProtein);
    }

    // Helper method to get nutrition values from checked items in a meal section
    private double getCheckedNutritionFromSection(VBox mealSection, String nutrientType) {
        double total = 0;
        
        for (javafx.scene.Node node : mealSection.getChildren()) {
            if (node instanceof HBox) {
                HBox foodCard = (HBox) node;
                
                // Skip if this is the "Add Food" button container
                if (foodCard.getStyleClass().contains("button-container")) {
                    continue;
                }
                
                // Find the checkbox in this food card
                CheckBox checkbox = null;
                for (javafx.scene.Node child : foodCard.getChildren()) {
                    if (child instanceof CheckBox) {
                        checkbox = (CheckBox) child;
                        break;
                    }
                }
                
                // If checkbox is checked, add nutrition values
                if (checkbox != null && checkbox.isSelected()) {
                    total += getNutritionValueFromCard(foodCard, nutrientType);
                }
            }
        }
        
        return total;
    }

    // Helper method to extract nutrition value from a food card
    private double getNutritionValueFromCard(HBox foodCard, String nutrientType) {
        for (javafx.scene.Node child : foodCard.getChildren()) {
            if (child instanceof VBox) {
                VBox foodInfoVBox = (VBox) child;
                for (javafx.scene.Node infoChild : foodInfoVBox.getChildren()) {
                    if (infoChild instanceof Label) {
                        Label label = (Label) infoChild;
                        String text = label.getText().toLowerCase();
                        
                        if (text.contains(nutrientType.toLowerCase() + ":")) {
                            // Use regex to find the first decimal number in the text
                            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+(?:\\.\\d+)?");
                            java.util.regex.Matcher matcher = pattern.matcher(text);
                            
                            if (matcher.find()) {
                                try {
                                    double value = Double.parseDouble(matcher.group());
                                    return value;
                                } catch (NumberFormatException e) {
                                    return 0;
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    // Method to update progress bars with calculated values
    private void updateProgressBars(double totalCalories, double totalCarbs, double totalFat, double totalProtein) {
        // Calculate target values from meal plan XML
        double targetCalories = 0;
        double targetCarbs = 0;
        double targetFat = 0;
        double targetProtein = 0;
        
        // Calculate targets from the current meal plan
        double[] targets = calculateTargetNutritions();
        targetCalories = targets[0];
        targetCarbs = targets[1];
        targetFat = targets[2];
        targetProtein = targets[3];

        // Update progress bars
        if (caloriesProgressBar != null) {
            double caloriesProgress = targetCalories > 0 ? Math.min(totalCalories / targetCalories, 1.0) : 0.0;
            caloriesProgressBar.setProgress(caloriesProgress);
        }
        if (carbsProgressBar != null) {
            double carbsProgress = targetCarbs > 0 ? Math.min(totalCarbs / targetCarbs, 1.0) : 0.0;
            carbsProgressBar.setProgress(carbsProgress);
        }
        if (fatProgressBar != null) {
            double fatProgress = targetFat > 0 ? Math.min(totalFat / targetFat, 1.0) : 0.0;
            fatProgressBar.setProgress(fatProgress);
        }
        if (proteinProgressBar != null) {
            double proteinProgress = targetProtein > 0 ? Math.min(totalProtein / targetProtein, 1.0) : 0.0;
            proteinProgressBar.setProgress(proteinProgress);
        }

        // Update progress labels
        if (caloriesProgressLabel != null) {
            caloriesProgressLabel.setText(String.format("%.0f / %.0f", totalCalories, targetCalories));
        }
        if (carbsProgressLabel != null) {
            carbsProgressLabel.setText(String.format("%.1fg / %.0fg", totalCarbs, targetCarbs));
        }
        if (fatProgressLabel != null) {
            fatProgressLabel.setText(String.format("%.1fg / %.0fg", totalFat, targetFat));
        }
        if (proteinProgressLabel != null) {
            proteinProgressLabel.setText(String.format("%.1fg / %.0fg", totalProtein, targetProtein));
        }

        // Update target labels
        if (targetCaloriesLabel != null) {
            targetCaloriesLabel.setText(String.format("%.0f", targetCalories));
        }
        if (targetCarbsLabel != null) {
            targetCarbsLabel.setText(String.format("%.0fg", targetCarbs));
        }
        if (targetFatLabel != null) {
            targetFatLabel.setText(String.format("%.0fg", targetFat));
        }
        if (targetProteinLabel != null) {
            targetProteinLabel.setText(String.format("%.0fg", targetProtein));
        }

        // Update pie chart with current values
        updatePieChartFromCheckboxes(totalCarbs, totalFat, totalProtein);

        // Update warning box based on targets
        updateWarningBox(totalCalories, totalCarbs, totalFat, totalProtein, targetCalories, targetCarbs, targetFat, targetProtein);
    }

    // Method to update pie chart based on checkbox-calculated values
    private void updatePieChartFromCheckboxes(double totalCarbs, double totalFat, double totalProtein) {
        if (nutritionPieChart != null) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            // Calculate total macronutrients for percentage calculation
            double totalMacros = totalCarbs + totalFat + totalProtein;
            
            // Only add slices for macronutrients that have values > 0
            if (totalCarbs > 0) {
                double carbsPercentage = (totalCarbs / totalMacros) * 100;
                PieChart.Data carbsData = new PieChart.Data(String.format("Carbs (%.1f%%)", carbsPercentage), totalCarbs);
                pieChartData.add(carbsData);
            }
            
            if (totalFat > 0) {
                double fatPercentage = (totalFat / totalMacros) * 100;
                PieChart.Data fatData = new PieChart.Data(String.format("Fat (%.1f%%)", fatPercentage), totalFat);
                pieChartData.add(fatData);
            }
            
            if (totalProtein > 0) {
                double proteinPercentage = (totalProtein / totalMacros) * 100;
                PieChart.Data proteinData = new PieChart.Data(String.format("Protein (%.1f%%)", proteinPercentage), totalProtein);
                pieChartData.add(proteinData);
            }
            
            // If no macronutrients are selected, show a placeholder
            if (pieChartData.isEmpty()) {
                pieChartData.add(new PieChart.Data("No meals selected", 1));
            }
            
            nutritionPieChart.setData(pieChartData);
            nutritionPieChart.setLegendVisible(false); // Hide legend since percentages are shown beside the chart
            nutritionPieChart.setTitle("Macronutrient Distribution");
        }
    }

    // Method to update warning box based on nutrition targets
    private void updateWarningBox(double totalCalories, double totalCarbs, double totalFat, double totalProtein,
                                 double targetCalories, double targetCarbs, double targetFat, double targetProtein) {
        
        boolean caloriesMet = targetCalories > 0 ? 
            (totalCalories >= (targetCalories * 0.8) && totalCalories <= (targetCalories * 1.2)) : true;
        boolean carbsMet = targetCarbs > 0 ? 
            (totalCarbs >= (targetCarbs * 0.8) && totalCarbs <= (targetCarbs * 1.2)) : true;
        boolean fatMet = targetFat > 0 ? 
            (totalFat >= (targetFat * 0.8) && totalFat <= (targetFat * 1.2)) : true;
        boolean proteinMet = targetProtein > 0 ? 
            (totalProtein >= (targetProtein * 0.8)) : true;
        
        boolean allTargetsMet = caloriesMet && carbsMet && fatMet && proteinMet;
        
        if (warningBox != null && warningLabel != null) {
            if (!allTargetsMet) {
                warningBox.setVisible(true);
                warningBox.setManaged(true);
                
                StringBuilder message = new StringBuilder();
                if (!caloriesMet && targetCalories > 0) {
                    if (totalCalories < targetCalories * 0.8) {
                        message.append("Low calories. ");
                    } else {
                        message.append("High calories. ");
                    }
                }
                if (!proteinMet && targetProtein > 0) {
                    message.append("Low protein. ");
                }
                if ((!carbsMet && targetCarbs > 0) || (!fatMet && targetFat > 0)) {
                    message.append("Check macro balance. ");
                }
                
                if (message.length() > 0) {
                    warningLabel.setText(message.toString().trim());
                } else {
                    warningLabel.setText("Some targets are not being met");
                }
            } else {
                warningBox.setVisible(false);
                warningBox.setManaged(false);
            }
        }
    }

    /**
     * Calculate target nutrition values from the current meal plan XML file.
     * @return Array containing [calories, carbs, fat, protein] targets
     */
    private double[] calculateTargetNutritions() {
        double[] targets = new double[4]; // [calories, carbs, fat, protein]
        
        try {
            // Load the meal plan from XML using XmlDataService
            DayMealPlan mealPlan = XmlDataService.load("data/meal_plan.xml", DayMealPlan.class, Meal.class, Food.class, Ingredient.class);
            
            if (mealPlan != null) {
                // Calculate total nutrition values from all meals in the plan
                targets[0] = mealPlan.getTotalDayCalories(); // Total calories
                targets[1] = mealPlan.getTotalDayCarbs();    // Total carbs
                targets[2] = mealPlan.getTotalDayFat();      // Total fat
                targets[3] = mealPlan.getTotalDayProtein();  // Total protein
            } else {
                // If no meal plan exists, set default target values
                targets[0] = 2000.0; // Default calories
                targets[1] = 250.0;  // Default carbs (g)
                targets[2] = 65.0;   // Default fat (g)
                targets[3] = 150.0;  // Default protein (g)
            }
        } catch (Exception e) {
            System.err.println("Error calculating target nutritions: " + e.getMessage());
            // Set default values on error
            targets[0] = 2000.0; // Default calories
            targets[1] = 250.0;  // Default carbs (g)
            targets[2] = 65.0;   // Default fat (g)
            targets[3] = 150.0;  // Default protein (g)
        }
        
        return targets;
    }

    private void updateNutritionSummary(DayMealPlan dayMealPlan) {
        // Get current totals
        double totalCalories = dayMealPlan.getTotalDayCalories();
        double totalCarbs = dayMealPlan.getTotalDayCarbs();
        double totalFat = dayMealPlan.getTotalDayFat();
        double totalProtein = dayMealPlan.getTotalDayProtein();
        
        // Update the display labels
        totalCaloriesLabel.setText(String.format("%.1f", totalCalories));
        totalCarbsLabel.setText(String.format("%.1fg", totalCarbs));
        totalFatLabel.setText(String.format("%.1fg", totalFat));
        totalProteinLabel.setText(String.format("%.1fg", totalProtein));
        
        // Calculate target values from meal plan XML
        double[] targets = calculateTargetNutritions();
        double targetCalories = targets[0];
        double targetCarbs = targets[1];
        double targetFat = targets[2];
        double targetProtein = targets[3];
        
        // Check if targets are met (allowing for some reasonable margin)
        boolean caloriesMet = totalCalories >= (targetCalories * 0.8) && totalCalories <= (targetCalories * 1.2);
        boolean carbsMet = totalCarbs >= (targetCarbs * 0.8) && totalCarbs <= (targetCarbs * 1.2);
        boolean fatMet = totalFat >= (targetFat * 0.8) && totalFat <= (targetFat * 1.2);
        boolean proteinMet = totalProtein >= (targetProtein * 0.8);
        
        // Show warning only if any target is not met
        boolean allTargetsMet = caloriesMet && carbsMet && fatMet && proteinMet;
        
        if (warningBox != null && warningLabel != null) {
            if (!allTargetsMet) {
                warningBox.setVisible(true);
                warningBox.setManaged(true);
                
                // Create a specific message based on what's not met
                StringBuilder message = new StringBuilder();
                if (!caloriesMet) {
                    if (totalCalories < targetCalories * 0.8) {
                        message.append("Low calories. ");
                    } else {
                        message.append("High calories. ");
                    }
                }
                if (!proteinMet) {
                    message.append("Low protein. ");
                }
                if (!carbsMet || !fatMet) {
                    message.append("Check macro balance. ");
                }
                
                if (message.length() > 0) {
                    warningLabel.setText(message.toString().trim());
                } else {
                    warningLabel.setText("Some targets are not being met");
                }
            } else {
                warningBox.setVisible(false);
                warningBox.setManaged(false);
            }
        }
    }

    private void updatePieChart(DayMealPlan dayMealPlan) {
        double totalCalories = dayMealPlan.getTotalDayCalories();
        if (totalCalories == 0) {
            nutritionPieChart.setData(FXCollections.observableArrayList());
            return;
        }

        // Calculate calories from macronutrients (carbs and protein = 4 cal/g, fat = 9 cal/g)
        double carbsCalories = dayMealPlan.getTotalDayCarbs() * 4;
        double fatCalories = dayMealPlan.getTotalDayFat() * 9;
        double proteinCalories = dayMealPlan.getTotalDayProtein() * 4;

        // Calculate total macronutrient calories
        double totalMacroCalories = carbsCalories + fatCalories + proteinCalories;
        
        // Handle edge case where macro calories might be 0
        if (totalMacroCalories == 0) {
            nutritionPieChart.setData(FXCollections.observableArrayList());
            return;
        }

        // Calculate percentages based on macro calories (normalized to 100%)
        double carbsPercentage = (carbsCalories / totalMacroCalories) * 100;
        double fatPercentage = (fatCalories / totalMacroCalories) * 100;
        double proteinPercentage = (proteinCalories / totalMacroCalories) * 100;

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data(String.format("Fat %.0f%%", fatPercentage), fatPercentage),
                        new PieChart.Data(String.format("Carbs %.0f%%", carbsPercentage), carbsPercentage),
                        new PieChart.Data(String.format("Protein %.0f%%", proteinPercentage), proteinPercentage));

        nutritionPieChart.setData(pieChartData);
        nutritionPieChart.setLabelsVisible(false);
        nutritionPieChart.setStartAngle(90);
        nutritionPieChart.setLegendVisible(true);
    }
    
    @FXML
    private void openProfileEdit(MouseEvent event) {
        NavigationUtils.loadCenterContentWithHighlight("/org/example/meal_planner/Dashboard_Resources/fxml/content/ProfileEditContent.fxml", "profile");
    }

    @FXML
    private void openGroceriesPage(MouseEvent event) {
        NavigationUtils.loadCenterContentWithHighlight("/org/example/meal_planner/Dashboard_Resources/fxml/content/GroceriesContent.fxml", "groceries");
    }

    @FXML
    private void openDiscoverPage(MouseEvent event) {
        NavigationUtils.loadCenterContentWithHighlight("/org/example/meal_planner/Dashboard_Resources/fxml/content/DiscoverContent.fxml", "discover");
    }

    @FXML
    private void onDashboardButtonClick(MouseEvent event) {
        NavigationUtils.restoreDashboardContent();
    }

    /**
     * Restore the Today Dashboard content (used by other controllers to go back)
     */
    public static void showTodayDashboard() {
        NavigationUtils.restoreDashboardContent();
    }

    @FXML
    private void generateMealsWithAI() {
        // Check if there are unchecked meals and show confirmation dialog
        if (hasUncheckedMeals()) {
            // Show confirmation dialog using ModalUtils
            ModalUtils.showConfirmationDialog(
                rootPane,
                "Unchecked Meals Detected",
                "You have unchecked meals in your current plan. Generating new meals will replace your current meal plan. Any unchecked meals will be lost.\n\nDo you want to continue?",
                () -> { 
                    // User confirmed - proceed with AI generation
                    proceedWithAIGeneration();
                }, 
                () -> { 
                    // User cancelled - do nothing
                }
            );
            return; // Exit early, let the callback handle the continuation
        }
        
        // No unchecked meals, proceed directly
        proceedWithAIGeneration();
    }
    
    private void proceedWithAIGeneration() {
        
        try {
            // Show loading screen instead of status text
            showLoadingScreen();
            
            // Get user profile for personalized meal generation
            ProfileSetupData profile = XmlDataService.loadProfileData();
            if (profile == null) {
                hideLoadingScreen();
                showStatus("Please set up your profile first to generate personalized meals", true);
                return;
            }
            
            // Generate meals in background thread to avoid blocking UI
            CompletableFuture.runAsync(() -> {
                try {
                    // Create AI instance and generate meals
                    Ai aiService = new Ai();
                    String prompt = buildMealGenerationPrompt(profile);
                    String aiResponse = aiService.AskAi(prompt);
                    
                    // Parse AI response and update meal plan
                    javafx.application.Platform.runLater(() -> {
                        try {
                            DayMealPlan newMealPlan = parseAiResponse(aiResponse);
                            if (newMealPlan != null) {
                                // Save the new meal plan with ingredients support
                                XmlDataService.save(newMealPlan, "data/meal_plan.xml", Meal.class, Food.class, Ingredient.class);
                                
                                // Update the dashboard with new meals
                                updateDashboard(newMealPlan);
                                
                                // Hide loading screen
                                hideLoadingScreen();
                                showStatus("New meals generated successfully! 🍽️", false);
                                
                                // Auto-hide the success message after 3 seconds
                                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                                    showStatus("", false);
                                    updateSuggestionText(); // Reset to default suggestion
                                }));
                                timeline.play();
                            } else {
                                hideLoadingScreen();
                                showStatus("Failed to generate meals. Please try again.", true);
                            }
                        } catch (Exception e) {
                            hideLoadingScreen();
                            showStatus("Error processing AI response: " + e.getMessage(), true);
                        }
                    });
                    
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        hideLoadingScreen();
                        showStatus("Error generating meals: " + e.getMessage(), true);
                    });
                }
            });
            
        } catch (Exception e) {
            hideLoadingScreen();
            showStatus("Error starting meal generation: " + e.getMessage(), true);
        }
    }
    
    private String buildMealGenerationPrompt(ProfileSetupData profile) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a daily meal plan for today with the following user preferences:\n\n");
        
        // Add user details
        prompt.append("User Profile:\n");
        prompt.append("- Age: ").append(profile.getAge() != null ? profile.getAge() : "Not specified").append("\n");
        prompt.append("- Sex: ").append(profile.getSex() != null ? profile.getSex() : "Not specified").append("\n");
        prompt.append("- Height: ").append(profile.getHeight() != null ? profile.getHeight() + "cm" : "Not specified").append("\n");
        prompt.append("- Weight: ").append(profile.getWeight() != null ? profile.getWeight() + "kg" : "Not specified").append("\n");
        prompt.append("- Activity Level: ").append(profile.getActivityLevel() != null ? profile.getActivityLevel() : "Not specified").append("\n");
        prompt.append("- Diet Type: ").append(profile.getDietType() != null ? profile.getDietType() : "Not specified").append("\n");
        
        // Add goal information
        if (profile.getGoalType() != null) {
            prompt.append("- Goal: ").append(profile.getGoalType());
            if ("General Goal".equals(profile.getGoalType()) && profile.getGeneralGoalAction() != null) {
                prompt.append(" (").append(profile.getGeneralGoalAction()).append(")");
            }
            prompt.append("\n");
        }
        
        // Add foods to avoid
        if (profile.getFoodsToAvoid() != null && !profile.getFoodsToAvoid().isEmpty()) {
            prompt.append("- Foods to Avoid: ").append(String.join(", ", profile.getFoodsToAvoid())).append("\n");
        }
        
        prompt.append("\nPlease generate balanced, nutritious meals that align with these preferences and goals.");
        
        return prompt.toString();
    }
    
    private DayMealPlan parseAiResponse(String aiResponse) {
        try {
            // Clean the response to extract JSON
            String cleanedResponse = extractJsonFromResponse(aiResponse);
            
            // Use the proper MealPlanParser that handles ingredients
            java.util.List<DayMealPlan> mealPlans = MealPlanParser.parseJsonToMealPlan(cleanedResponse);
            
            if (mealPlans == null || mealPlans.isEmpty()) {
                System.err.println("Failed to parse AI response into meal plans");
                return null;
            }
            
            DayMealPlan dayMealPlan = mealPlans.get(0);
            
            // Print meal plan details with ingredients to console
            printMealPlanDetails(dayMealPlan);
            
            return dayMealPlan;
            
        } catch (Exception e) {
            System.err.println("Error parsing AI response: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void printMealPlanDetails(DayMealPlan dayMealPlan) {
        if (dayMealPlan == null) {
            return;
        }

        Meal breakfast = dayMealPlan.getBreakfast();
        if (breakfast != null) {
            System.out.println("Breakfast foods:");
            for (Food food : breakfast.getFoods()) {
                System.out.println("  " + food.toString());
                if (food.getIngredients() != null && !food.getIngredients().isEmpty()) {
                    System.out.println("    Ingredients:");
                    for (Ingredient ingredient : food.getIngredients()) {
                        System.out.println("      - " + ingredient.getName() + " (" + ingredient.getAmount() + " " + ingredient.getUnit() + ")");
                    }
                }
            }
            System.out.println("Total breakfast calories: " + breakfast.getTotalCalories());
        }

        Meal lunch = dayMealPlan.getLunch();
        if (lunch != null) {
            System.out.println("\nLunch foods:");
            for (Food food : lunch.getFoods()) {
                System.out.println("  " + food.toString());
                if (food.getIngredients() != null && !food.getIngredients().isEmpty()) {
                    System.out.println("    Ingredients:");
                    for (Ingredient ingredient : food.getIngredients()) {
                        System.out.println("      - " + ingredient.getName() + " (" + ingredient.getAmount() + " " + ingredient.getUnit() + ")");
                    }
                }
            }
            System.out.println("Total lunch calories: " + lunch.getTotalCalories());
        }

        Meal dinner = dayMealPlan.getDinner();
        if (dinner != null) {
            System.out.println("\nDinner foods:");
            for (Food food : dinner.getFoods()) {
                System.out.println("  " + food.toString());
                if (food.getIngredients() != null && !food.getIngredients().isEmpty()) {
                    System.out.println("    Ingredients:");
                    for (Ingredient ingredient : food.getIngredients()) {
                        System.out.println("      - " + ingredient.getName() + " (" + ingredient.getAmount() + " " + ingredient.getUnit() + ")");
                    }
                }
            }
            System.out.println("Total dinner calories: " + dinner.getTotalCalories());
        }
    }
    
    private Meal parseMealFromJson(JsonObject mealJson) {
        Meal meal = new Meal();
        
        if (mealJson.has("foods")) {
            JsonArray foodsArray = mealJson.getAsJsonArray("foods");
            for (JsonElement foodElement : foodsArray) {
                JsonObject foodJson = foodElement.getAsJsonObject();
                
                Food food = new Food();
                food.setName(foodJson.get("name").getAsString());
                food.setFoodCalories(foodJson.get("food_calories").getAsDouble());
                food.setFoodCarbsG(foodJson.get("food_carbs_g").getAsDouble());
                food.setFoodFatG(foodJson.get("food_fat_g").getAsDouble());
                food.setFoodProteinG(foodJson.get("food_protein_g").getAsDouble());
                
                meal.addFood(food);
            }
        }
        
        return meal;
    }
    
    private String extractJsonFromResponse(String aiResponse) {
        // Try to find JSON object boundaries
        int jsonStart = aiResponse.indexOf("{");
        int jsonEnd = aiResponse.lastIndexOf("}");
        
        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            String extractedJson = aiResponse.substring(jsonStart, jsonEnd + 1);
            return extractedJson;
        }
        
        // If no clear JSON boundaries found, return the original response
        return aiResponse;
    }
    
    private boolean isMealPlanEmpty() {
        if (currentMealPlan == null) return true;
        
        // Check if all meals are empty or have no foods
        boolean breakfastEmpty = currentMealPlan.getBreakfast() == null || 
                                currentMealPlan.getBreakfast().getFoods() == null || 
                                currentMealPlan.getBreakfast().getFoods().isEmpty();
        
        boolean lunchEmpty = currentMealPlan.getLunch() == null || 
                            currentMealPlan.getLunch().getFoods() == null || 
                            currentMealPlan.getLunch().getFoods().isEmpty();
        
        boolean dinnerEmpty = currentMealPlan.getDinner() == null || 
                             currentMealPlan.getDinner().getFoods() == null || 
                             currentMealPlan.getDinner().getFoods().isEmpty();
        
        // Consider meal plan empty if 2 or more meals are empty
        int emptyMeals = 0;
        if (breakfastEmpty) emptyMeals++;
        if (lunchEmpty) emptyMeals++;
        if (dinnerEmpty) emptyMeals++;
        
        return emptyMeals >= 2;
    }
    
    private void updateGenerateButtonVisibility() {
        // Button is always visible now - user can generate meals anytime
        if (generateMealsButton != null) {
            generateMealsButton.setVisible(true);
            generateMealsButton.setManaged(true);
        }
    }
    
    private boolean hasUncheckedMeals() {
        return hasUncheckedMealsInSection(breakfastMealsVBox) ||
               hasUncheckedMealsInSection(lunchMealsVBox) ||
               hasUncheckedMealsInSection(dinnerMealsVBox);
    }
    
    private boolean hasUncheckedMealsInSection(VBox mealVBox) {
        for (javafx.scene.Node child : mealVBox.getChildren()) {
            if (child instanceof HBox) {
                HBox foodCard = (HBox) child;
                // Skip if this is the "Add Food" button container or empty state label
                if (foodCard.getStyleClass().contains("button-container")) {
                    continue;
                }
                
                // Look for unchecked checkboxes
                for (javafx.scene.Node cardChild : foodCard.getChildren()) {
                    if (cardChild instanceof CheckBox) {
                        CheckBox checkbox = (CheckBox) cardChild;
                        if (!checkbox.isSelected()) {
                            return true; // Found an unchecked meal
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private void showStatus(String message, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(isError ? "-fx-text-fill: #d32f2f;" : "-fx-text-fill: #2e7d32;");
            statusLabel.setVisible(!message.isEmpty());
        }
    }
    
    // Methods for checkbox state persistence
    private void saveCheckboxStates() {
        try {
            Properties props = new Properties();
            
            // Save states from all meal sections
            saveCheckboxStatesFromSection(breakfastMealsVBox, "breakfast", props);
            saveCheckboxStatesFromSection(lunchMealsVBox, "lunch", props);
            saveCheckboxStatesFromSection(dinnerMealsVBox, "dinner", props);
            
            // Ensure data directory exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            // Save to file
            try (FileOutputStream out = new FileOutputStream(CHECKBOX_STATES_FILE)) {
                props.store(out, "Checkbox States for DailyDish");
            }
        } catch (IOException e) {
            System.err.println("Error saving checkbox states: " + e.getMessage());
        }
    }
    
    private void saveCheckboxStatesFromSection(VBox mealVBox, String sectionName, Properties props) {
        if (mealVBox == null) return;
        
        for (javafx.scene.Node child : mealVBox.getChildren()) {
            if (child instanceof HBox) {
                HBox foodCard = (HBox) child;
                if (foodCard.getStyleClass().contains("button-container")) {
                    continue;
                }
                
                CheckBox checkbox = null;
                String foodName = null;
                
                for (javafx.scene.Node cardChild : foodCard.getChildren()) {
                    if (cardChild instanceof CheckBox) {
                        checkbox = (CheckBox) cardChild;
                    } else if (cardChild instanceof VBox) {
                        VBox foodInfoVBox = (VBox) cardChild;
                        for (javafx.scene.Node infoChild : foodInfoVBox.getChildren()) {
                            if (infoChild instanceof Label) {
                                Label label = (Label) infoChild;
                                if (label.getStyleClass().contains("meal-name")) {
                                    foodName = label.getText();
                                    break;
                                }
                            }
                        }
                    }
                }
                
                if (checkbox != null && foodName != null) {
                    String key = sectionName + "." + foodName;
                    props.setProperty(key, String.valueOf(checkbox.isSelected()));
                }
            }
        }
    }
    
    private Map<String, Boolean> loadCheckboxStates() {
        Map<String, Boolean> states = new HashMap<>();
        try {
            File file = new File(CHECKBOX_STATES_FILE);
            if (file.exists()) {
                Properties props = new Properties();
                try (FileInputStream in = new FileInputStream(file)) {
                    props.load(in);
                    
                    for (String key : props.stringPropertyNames()) {
                        states.put(key, Boolean.parseBoolean(props.getProperty(key)));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading checkbox states: " + e.getMessage());
        }
        return states;
    }
    
    private String getMealSectionName(VBox mealVBox) {
        if (mealVBox == breakfastMealsVBox) return "breakfast";
        if (mealVBox == lunchMealsVBox) return "lunch";
        if (mealVBox == dinnerMealsVBox) return "dinner";
        return "unknown";
    }

    private void showLoadingScreen() {
        try {
            Pane targetPane = ModalUtils.getRootPane(generateMealsButton);
            if (targetPane != null) {
                // Hide the loading overlay first if it exists
                if (loadingOverlay != null) {
                    ModalUtils.hideLoadingScreen(targetPane, loadingOverlay);
                }
                loadingOverlay = ModalUtils.showLoadingScreen(targetPane, "Generating Your Meal Plan");
            }
        } catch (Exception e) {
            System.err.println("Error showing loading screen: " + e.getMessage());
        }
    }

    private void hideLoadingScreen() {
        try {
            if (loadingOverlay != null) {
                Pane targetPane = ModalUtils.getRootPane(generateMealsButton);
                if (targetPane != null) {
                    ModalUtils.hideLoadingScreen(targetPane, loadingOverlay);
                }
                loadingOverlay = null;
            }
        } catch (Exception e) {
            System.err.println("Error hiding loading screen: " + e.getMessage());
        }
    }
}
