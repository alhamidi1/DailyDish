package org.example.meal_planner.controllers.Profile_Setup;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.example.meal_planner.models.DayMealPlan;
import org.example.meal_planner.models.Food;
import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.Meal;
import org.example.meal_planner.services.Ai;
import org.example.meal_planner.services.MealPlanParser;
import org.example.meal_planner.services.XmlDataService;
import org.example.meal_planner.utils.ModalUtils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UserGoalsController implements WizardController, Initializable {

    @FXML private ToggleGroup goalTypeToggleGroup;
    @FXML private ToggleButton generalGoalButton;
    @FXML private ToggleButton exactGoalButton;
    @FXML private VBox generalGoalPane;
    @FXML private VBox exactGoalPane;
    @FXML private ToggleGroup goalActionToggleGroup;
    @FXML private Label userIdLabel;
    @FXML private TextField weightGoalField;
    @FXML private TextField weightChangeField;
    @FXML private Button backButton;
    @FXML private Button continueButton;
    @FXML private Button GenerateMeals;
    @FXML private BorderPane rootPane; // Add reference to root pane for modal dialogs

    private SceneNavigator sceneNavigator;
    private ProfileSetupData profileSetupData;
    private StackPane loadingOverlay; // Track loading screen

    @Override
    public void init(SceneNavigator sceneNavigator, ProfileSetupData profileSetupData) {
        this.sceneNavigator = sceneNavigator;
        this.profileSetupData = profileSetupData;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        goalTypeToggleGroup.selectedToggleProperty().addListener((_, _, newToggle) -> {
            if (newToggle == generalGoalButton) {
                setPaneVisibility(true);
            } else if (newToggle == exactGoalButton) {
                setPaneVisibility(false);
            }
        });
        setPaneVisibility(true); // Set initial state
    }

    @FXML
    private void onContinueClicked() {
        // 1. Save data from the UI
        ToggleButton selectedGoalType = (ToggleButton) goalTypeToggleGroup.getSelectedToggle();
        if (selectedGoalType == null) {
            showAlert("Selection Required", "Please select a goal type.");
            return;
        }
        profileSetupData.setGoalType(selectedGoalType.getText());

        if (selectedGoalType == generalGoalButton) {
            ToggleButton selectedAction = (ToggleButton) goalActionToggleGroup.getSelectedToggle();
            if (selectedAction != null) {
                profileSetupData.setGeneralGoalAction(selectedAction.getText());
            }
        } else {
             try {
                 double weightGoal = Double.parseDouble(weightGoalField.getText());
                 profileSetupData.setExactWeightGoal(weightGoal);

                 double weightChangeRate = Double.parseDouble(weightChangeField.getText().trim());
                 profileSetupData.setExactWeightChangeRate(weightChangeRate);
             } catch (NumberFormatException e) {
                 showAlert("Invalid Input", "Please enter a valid weight goal.");
                 return;
             }
        }

        // 2. Show loading screen to prevent user interaction
        showLoadingScreen();

        Task<List<DayMealPlan>> generateMealPlanTask = new Task<>() {
            @Override
            protected List<DayMealPlan> call() throws Exception {
                String prompt = createPrompt();
                Ai ai = new Ai();
                String jsonResponse = ai.AskAi(prompt);
                return MealPlanParser.parseJsonToMealPlan(jsonResponse);
            }
        };

        // 3. Define what happens when the task SUCCEEDS
        generateMealPlanTask.setOnSucceeded(_ -> {
            List<DayMealPlan> mealPlans = generateMealPlanTask.getValue();

            printMealPlanDetails(mealPlans);

            if (mealPlans != null && !mealPlans.isEmpty()) {
                DayMealPlan myPlan = mealPlans.get(0);
                XmlDataService.save(myPlan, "data/meal_plan.xml", Meal.class, Food.class, Ingredient.class);
            }

            XmlDataService.save(profileSetupData, "data/user_profile.xml");
            
            // Hide loading screen and navigate
            Platform.runLater(() -> {
                hideLoadingScreen();
                sceneNavigator.showNextPage();
            });
        });

        // 4. Define what happens when the task FAILS
        generateMealPlanTask.setOnFailed(_ -> {
            Throwable ex = generateMealPlanTask.getException();
            System.err.println("Error generating meal plan: " + ex.getMessage());
            ex.printStackTrace();

            // Hide loading screen and show error
            Platform.runLater(() -> {
                hideLoadingScreen();
                showAlert("Error", "Failed to generate meal plan. Please check your connection and try again.");
            });
        });

        // 5. Start the task on a new thread
        new Thread(generateMealPlanTask).start();
    }

    @FXML
    private void onBackClicked() {
        sceneNavigator.showPreviousPage();
    }

    private void setPaneVisibility(boolean isGeneralPaneVisible) {
        generalGoalPane.setVisible(isGeneralPaneVisible);
        generalGoalPane.setManaged(isGeneralPaneVisible);
        exactGoalPane.setVisible(!isGeneralPaneVisible);
        exactGoalPane.setManaged(!isGeneralPaneVisible);
    }

    private String createPrompt() {
        String prompt = "The Diet Type: " + profileSetupData.getDietType() +
                ", Foods to Avoid: " + String.join(", ", profileSetupData.getFoodsToAvoid()) +
                ", Sex: " + profileSetupData.getSex() +
                ", Height: " + profileSetupData.getHeight() +
                ", Weight: " + profileSetupData.getWeight() +
                ", Age: " + profileSetupData.getAge() +
                ", Body Fat Level: " + profileSetupData.getBodyfatLevel() +
                ", Activity Level: " + profileSetupData.getActivityLevel() +
                ", Goal Type: " + profileSetupData.getGoalType();

        if (profileSetupData.getGoalType().equals("General Goal")) {
            prompt += ", General Goal Action: " + profileSetupData.getGeneralGoalAction();
        } else if (profileSetupData.getGoalType().equals("Exact Goal")) {
            if (profileSetupData.getExactWeightGoal() != null) {
                prompt += ", Exact Weight Goal(in kg): " + profileSetupData.getExactWeightGoal();
            }
            if (profileSetupData.getExactWeightChangeRate() != null) {
                prompt += ", Exact Weight Change Rate(in kg) each week: " + profileSetupData.getExactWeightChangeRate();
            }
        }
        return prompt;
    }

    private void printMealPlanDetails(List<DayMealPlan> mealPlans) {
        if (mealPlans == null || mealPlans.isEmpty()) {
            return;
        }
        DayMealPlan firstDay = mealPlans.get(0);

        Meal breakfast = firstDay.getBreakfast();
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

        Meal lunch = firstDay.getLunch();
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

        Meal dinner = firstDay.getDinner();
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

    private void showAlert(String title, String content) {
        // Get the root pane - if rootPane is null, try to find it from the scene graph
        Pane targetPane = rootPane;
        if (targetPane == null) {
            targetPane = ModalUtils.getRootPane(continueButton);
        }
        
        if (targetPane != null) {
            System.out.println("Showing alert on pane: " + targetPane.getClass().getSimpleName() + 
                              " Size: " + targetPane.getWidth() + "x" + targetPane.getHeight());
            ModalUtils.showInPageAlert(targetPane, title, content, null);
        } else {
            System.out.println("No target pane found, using rootPane as fallback");
            // Use rootPane as fallback
            ModalUtils.showInPageAlert(rootPane, title, content, null);
        }
    }
    
    private void showLoadingScreen() {
        Pane targetPane = rootPane;
        if (targetPane == null) {
            targetPane = ModalUtils.getRootPane(continueButton);
        }
        
        if (targetPane != null) {
            System.out.println("Showing loading screen on pane: " + targetPane.getClass().getSimpleName() + 
                              " Size: " + targetPane.getWidth() + "x" + targetPane.getHeight());
            loadingOverlay = ModalUtils.showLoadingScreen(targetPane, "Generating Your Meal Plan");
        } else {
            System.out.println("No target pane found for loading screen");
        }
    }
    
    private void hideLoadingScreen() {
        if (loadingOverlay != null) {
            Pane targetPane = rootPane;
            if (targetPane == null) {
                targetPane = ModalUtils.getRootPane(continueButton);
            }
            
            if (targetPane != null) {
                ModalUtils.hideLoadingScreen(targetPane, loadingOverlay);
            }
            loadingOverlay = null;
        }
    }
}