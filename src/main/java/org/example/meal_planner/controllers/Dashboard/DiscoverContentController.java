package org.example.meal_planner.controllers.Dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.MealTemplate;
import org.example.meal_planner.services.MealTemplateService;
import org.example.meal_planner.utils.ModalUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the Meal Discovery Page.
 * Displays searchable meal cards with detailed nutrition and ingredient information.
 */
public class DiscoverContentController {

    @FXML private VBox discoverContentVBox; // Main container for modal operations
    @FXML private TextField searchField;
    @FXML private Button addNewMealButton;
    @FXML private FlowPane mealCardsContainer;
    @FXML private VBox emptyStateContainer;

    private List<MealTemplate> allMeals = new ArrayList<>();
    private List<MealTemplate> filteredMeals = new ArrayList<>();
    private Pane rootPane; // Reference to root pane for modal operations

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        loadMeals();
        displayMealCards(allMeals);
        
        // Initially use the VBox, then try to find a better parent once the scene is ready
        rootPane = discoverContentVBox;
        
        // Wait for the scene to be available and find the main BorderPane for better modal positioning
        discoverContentVBox.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // Try to find the main BorderPane by walking up the parent hierarchy
                javafx.scene.Node current = discoverContentVBox;
                while (current != null && current.getParent() != null) {
                    current = current.getParent();
                    if (current instanceof javafx.scene.layout.BorderPane) {
                        rootPane = (Pane) current;
                        break;
                    }
                }
                // If no BorderPane found, use the scene root as fallback
                if (rootPane == discoverContentVBox && newScene.getRoot() instanceof Pane) {
                    rootPane = (Pane) newScene.getRoot();
                }
            }
        });
    }

    /**
     * Load all meals from the meal templates service
     */
    private void loadMeals() {
        try {
            allMeals = MealTemplateService.loadMealTemplates();
            filteredMeals = new ArrayList<>(allMeals);
        } catch (Exception e) {
            e.printStackTrace();
            allMeals = new ArrayList<>();
            filteredMeals = new ArrayList<>();
        }
    }

    /**
     * Handle search input as user types
     */
    @FXML
    private void handleSearchInput(KeyEvent event) {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            filteredMeals = new ArrayList<>(allMeals);
        } else {
            filteredMeals = allMeals.stream()
                    .filter(meal -> meal.getName().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
        }
        
        displayMealCards(filteredMeals);
    }

    /**
     * Clear search and show all meals
     */
    @FXML
    private void clearSearch() {
        searchField.clear();
        filteredMeals = new ArrayList<>(allMeals);
        displayMealCards(filteredMeals);
    }

    /**
     * Display meal cards in the container
     */
    private void displayMealCards(List<MealTemplate> meals) {
        mealCardsContainer.getChildren().clear();
        
        if (meals.isEmpty()) {
            emptyStateContainer.setVisible(true);
            emptyStateContainer.setManaged(true);
        } else {
            emptyStateContainer.setVisible(false);
            emptyStateContainer.setManaged(false);
            
            for (MealTemplate meal : meals) {
                VBox mealCard = createMealCard(meal);
                mealCardsContainer.getChildren().add(mealCard);
            }
        }
    }

    /**
     * Create a meal card UI component
     */
    private VBox createMealCard(MealTemplate meal) {
        VBox card = new VBox(15);
        card.getStyleClass().addAll("card", "meal-card");
        card.setPrefWidth(300);
        card.setMaxWidth(300);

        // Meal name header
        Label nameLabel = new Label("🍽️ " + meal.getName());
        nameLabel.getStyleClass().addAll("card-title", "meal-card-title");
        nameLabel.setWrapText(true);

        // Nutrition section
        VBox nutritionBox = createNutritionSection(meal);

        // Ingredients section
        VBox ingredientsBox = createIngredientsSection(meal);

        // Remove button
        Button removeButton = new Button("🗑️ Remove Meal");
        removeButton.getStyleClass().addAll("btn-secondary", "remove-meal-button");
        removeButton.setOnAction(event -> removeMeal(meal));

        card.getChildren().addAll(nameLabel, nutritionBox, ingredientsBox, removeButton);
        return card;
    }

    /**
     * Create nutrition facts section for meal card
     */
    private VBox createNutritionSection(MealTemplate meal) {
        VBox nutritionBox = new VBox(8);
        nutritionBox.getStyleClass().add("nutrition-section");
        
        Label nutritionTitle = new Label("🔢 Nutrition Facts");
        nutritionTitle.getStyleClass().addAll("heading-small", "nutrition-title");
        
        GridPane nutritionGrid = new GridPane();
        nutritionGrid.setHgap(15);
        nutritionGrid.setVgap(5);
        
        // Create nutrition labels
        addNutritionRow(nutritionGrid, 0, "Calories:", String.format("%.0f cal", meal.getCalories()));
        addNutritionRow(nutritionGrid, 1, "Carbs:", String.format("%.1f g", meal.getCarbs()));
        addNutritionRow(nutritionGrid, 2, "Protein:", String.format("%.1f g", meal.getProtein()));
        addNutritionRow(nutritionGrid, 3, "Fat:", String.format("%.1f g", meal.getFat()));
        
        nutritionBox.getChildren().addAll(nutritionTitle, nutritionGrid);
        return nutritionBox;
    }

    /**
     * Add a nutrition row to the grid
     */
    private void addNutritionRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().addAll("text-primary-small", "nutrition-label");
        
        Label valueNode = new Label(value);
        valueNode.getStyleClass().addAll("text-primary-small", "nutrition-value");
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    /**
     * Create ingredients section for meal card
     */
    private VBox createIngredientsSection(MealTemplate meal) {
        VBox ingredientsBox = new VBox(8);
        ingredientsBox.getStyleClass().add("ingredients-section");
        
        Label ingredientsTitle = new Label("🧾 Ingredients");
        ingredientsTitle.getStyleClass().addAll("heading-small", "ingredients-title");
        
        VBox ingredientsList = new VBox(3);
        
        if (meal.getIngredients() != null && !meal.getIngredients().isEmpty()) {
            for (Ingredient ingredient : meal.getIngredients()) {
                Label ingredientLabel = new Label(String.format("• %s: %.1f %s", 
                    ingredient.getName(), ingredient.getAmount(), ingredient.getUnit()));
                ingredientLabel.getStyleClass().addAll("text-primary-small", "ingredient-item");
                ingredientLabel.setWrapText(true);
                ingredientsList.getChildren().add(ingredientLabel);
            }
        } else {
            Label noIngredientsLabel = new Label("• No ingredients listed");
            noIngredientsLabel.getStyleClass().addAll("text-primary-small", "ingredient-item");
            noIngredientsLabel.setStyle("-fx-text-fill: #888888;");
            ingredientsList.getChildren().add(noIngredientsLabel);
        }
        
        ingredientsBox.getChildren().addAll(ingredientsTitle, ingredientsList);
        return ingredientsBox;
    }

    /**
     * Remove a meal from the collection
     */
    private void removeMeal(MealTemplate meal) {
        ModalUtils.showConfirmationDialog(
            rootPane,
            "Remove Meal",
            "Are you sure you want to remove this meal?\n\nMeal: " + meal.getName() + "\n\nThis action cannot be undone.",
            () -> {
                // Yes clicked - proceed with removal
                try {
                    MealTemplateService.removeMealTemplate(meal);
                    allMeals.remove(meal);
                    filteredMeals.remove(meal);
                    displayMealCards(filteredMeals);
                    
                    // Show success notification
                    ModalUtils.showSuccessNotification(
                        rootPane,
                        "Meal Removed",
                        "The meal has been successfully removed.",
                        () -> {} // No action needed on OK
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    ModalUtils.showInPageAlert(
                        rootPane,
                        "Error",
                        "Failed to remove meal. An error occurred while removing the meal. Please try again.",
                        () -> {} // No action needed on OK
                    );
                }
            },
            () -> {
                // No clicked - do nothing
            }
        );
    }

    /**
     * Show the add new meal form using MealDialog
     */
    @FXML
    private void showAddMealForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/meal_planner/Dashboard_Resources/fxml/content/MealDialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Meal");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addNewMealButton.getScene().getWindow()); // Set owner to current window

            // Set fixed dimensions and disable resizing for consistent layout
            dialogStage.setResizable(false);
            dialogStage.setWidth(520); // Slightly larger than FXML width to account for window decorations
            dialogStage.setHeight(640);
            // Get the controller
            MealDialogController controller = loader.getController();
        
            // Show dialog and wait for result
            dialogStage.showAndWait();
            
            // Check if a meal was created
            MealTemplate newMeal = controller.getResult();
            if (newMeal != null) {
                onMealAdded(newMeal);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to simple dialog if MealDialog can't be loaded
            showSimpleAddMealDialog();
        }
    }

    /**
     * Simple fallback dialog for adding meals
     */
    private void showSimpleAddMealDialog() {
        Dialog<MealTemplate> dialog = new Dialog<>();
        dialog.setTitle("Add New Meal");
        dialog.setHeaderText("Enter meal details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Meal name");
        TextField carbsField = new TextField();
        carbsField.setPromptText("Carbs (g)");
        TextField proteinField = new TextField();
        proteinField.setPromptText("Protein (g)");
        TextField fatField = new TextField();
        fatField.setPromptText("Fat (g)");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Carbs (g):"), 0, 1);
        grid.add(carbsField, 1, 1);
        grid.add(new Label("Protein (g):"), 0, 2);
        grid.add(proteinField, 1, 2);
        grid.add(new Label("Fat (g):"), 0, 3);
        grid.add(fatField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText().trim();
                    double carbs = Double.parseDouble(carbsField.getText());
                    double protein = Double.parseDouble(proteinField.getText());
                    double fat = Double.parseDouble(fatField.getText());
                    
                    if (name.isEmpty()) {
                        throw new IllegalArgumentException("Name cannot be empty");
                    }
                    
                    return new MealTemplate(name, 1, carbs, fat, protein);
                } catch (Exception e) {
                    ModalUtils.showInPageAlert(
                        rootPane,
                        "Invalid Input",
                        "Please check your input values. Make sure all fields are filled correctly.",
                        () -> {} // No action needed on OK
                    );
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::onMealAdded);
    }

    /**
     * Callback when a new meal is added
     */
    private void onMealAdded(MealTemplate newMeal) {
        try {
            MealTemplateService.addMealTemplate(newMeal);
            allMeals.add(newMeal);
            
            // Update filtered meals if it matches current search
            String searchText = searchField.getText().toLowerCase().trim();
            if (searchText.isEmpty() || newMeal.getName().toLowerCase().contains(searchText)) {
                filteredMeals.add(newMeal);
            }
            
            displayMealCards(filteredMeals);
            
            // Show success notification
            ModalUtils.showSuccessNotification(
                rootPane,
                "Meal Added",
                "The meal has been successfully added to your collection.",
                () -> {} // No action needed on OK
            );
        } catch (Exception e) {
            e.printStackTrace();
            ModalUtils.showInPageAlert(
                rootPane,
                "Error",
                "Failed to add meal. An error occurred while saving the meal. Please try again.",
                () -> {} // No action needed on OK
            );
        }
    }
}
