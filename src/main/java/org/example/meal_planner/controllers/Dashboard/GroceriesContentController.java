package org.example.meal_planner.controllers.Dashboard;

import java.util.List;

import org.example.meal_planner.models.DayMealPlan;
import org.example.meal_planner.models.GroceryItem;
import org.example.meal_planner.models.GroceryList;
import org.example.meal_planner.services.GroceryService;
import org.example.meal_planner.utils.ModalUtils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Content Controller for the Groceries Page that works with NavigationUtils.
 * Provides a clean, functional grocery list derived from user's selected meals.
 */
public class GroceriesContentController {

    @FXML private VBox ingredientListContainer;
    @FXML private Label emptyStateLabel;
    @FXML private TextField addItemNameField;
    @FXML private TextField addItemAmountField;
    @FXML private TextField addItemUnitField;
    @FXML private Button addItemButton;
    @FXML private Button generateFromMealsButton;
    @FXML private Button clearAllButton;
    @FXML private Label totalItemsLabel;
    @FXML private Label checkedItemsLabel;
    @FXML private Label progressLabel;

    private GroceryList groceryList;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Load existing grocery list or create new one
        groceryList = GroceryService.loadGroceryList();
        
        // Setup button handlers
        setupEventHandlers();
        
        // Populate the UI with existing items
        refreshIngredientDisplay();
        
        // Update statistics
        updateStatistics();
    }

    /**
     * Setup event handlers for buttons and input fields
     */
    private void setupEventHandlers() {
        if (generateFromMealsButton != null) {
            generateFromMealsButton.setOnAction(event -> generateFromMeals());
        }
        
        if (clearAllButton != null) {
            clearAllButton.setOnAction(event -> clearAllItems());
        }
        
        if (addItemButton != null) {
            addItemButton.setOnAction(event -> addCustomItem());
        }
        
        // Allow Enter key to add items from any of the input fields
        if (addItemNameField != null) {
            addItemNameField.setOnAction(event -> addCustomItem());
        }
        if (addItemAmountField != null) {
            addItemAmountField.setOnAction(event -> addCustomItem());
        }
        if (addItemUnitField != null) {
            addItemUnitField.setOnAction(event -> addCustomItem());
        }
    }

    /**
     * Generate grocery list from current meal plan
     */
    private void generateFromMeals() {
        generateFromMealsButton.setDisable(true);
        generateFromMealsButton.setText("Generating...");

        Task<GroceryList> generateTask = new Task<>() {
            @Override
            protected GroceryList call() throws Exception {
                // Load current meal plan
                DayMealPlan mealPlan = GroceryService.loadCurrentMealPlan();
                
                if (mealPlan == null) {
                    throw new Exception("No meal plan found. Please generate a meal plan first.");
                }
                
                // Clear existing items
                groceryList.clearAll();
                
                // Generate new grocery list from meals
                GroceryList generatedList = GroceryService.generateFromMealPlan(mealPlan);
                
                // Add generated items to list
                for (GroceryItem item : generatedList.getItems()) {
                    groceryList.addItem(item);
                }
                
                return groceryList;
            }
        };

        generateTask.setOnSucceeded(taskEvent -> {
            Platform.runLater(() -> {
                generateFromMealsButton.setDisable(false);
                generateFromMealsButton.setText("Generate from Meals");
                
                // Save updated grocery list
                GroceryService.saveGroceryList(groceryList);
                
                // Refresh display
                refreshIngredientDisplay();
                updateStatistics();
                
                // Get root pane for modal
                Pane rootPane = ModalUtils.getRootPane(ingredientListContainer);
                ModalUtils.showInPageAlert(rootPane, "Success", 
                    "Generated " + groceryList.getItems().size() + " ingredients from your meal plan!", null);
            });
        });

        generateTask.setOnFailed(taskEvent -> {
            Platform.runLater(() -> {
                generateFromMealsButton.setDisable(false);
                generateFromMealsButton.setText("Generate from Meals");
                
                Throwable exception = generateTask.getException();
                // Get root pane for modal
                Pane rootPane = ModalUtils.getRootPane(ingredientListContainer);
                ModalUtils.showInPageAlert(rootPane, "Unable to Generate", 
                    exception != null ? exception.getMessage() : "Unknown error occurred", null);
            });
        });

        Thread thread = new Thread(generateTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Add a custom item to the grocery list
     */
    private void addCustomItem() {
        String name = addItemNameField.getText().trim();
        String amountText = addItemAmountField.getText().trim();
        String unit = addItemUnitField.getText().trim();
        
        if (name.isEmpty()) {
            Pane rootPane = ModalUtils.getRootPane(ingredientListContainer);
            ModalUtils.showInPageAlert(rootPane, "Invalid Input", "Please enter an ingredient name.", null);
            return;
        }
        
        // Parse amount
        double amount = 1.0; // Default amount
        if (!amountText.isEmpty()) {
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    Pane rootPane = ModalUtils.getRootPane(ingredientListContainer);
                    ModalUtils.showInPageAlert(rootPane, "Invalid Amount", "Amount must be a positive number.", null);
                    return;
                }
            } catch (NumberFormatException e) {
                Pane rootPane = ModalUtils.getRootPane(ingredientListContainer);
                ModalUtils.showInPageAlert(rootPane, "Invalid Amount", "Please enter a valid number for the amount.", null);
                return;
            }
        }
        
        // Use default unit if empty
        if (unit.isEmpty()) {
            unit = "unit";
        }
        
        // Create and add new item
        GroceryItem newItem = new GroceryItem(name, amount, unit);
        groceryList.addItem(newItem);
        
        // Save and refresh
        GroceryService.saveGroceryList(groceryList);
        
        // Clear input fields
        addItemNameField.clear();
        addItemAmountField.clear();
        addItemUnitField.clear();
        
        // Refresh display
        refreshIngredientDisplay();
        updateStatistics();
    }

    /**
     * Clear all items from the grocery list
     */
    private void clearAllItems() {
        Pane rootPane = ModalUtils.getRootPane(ingredientListContainer);
        ModalUtils.showConfirmationDialog(rootPane, "Clear All Items", 
            "Are you sure? This will remove all items from your grocery list.",
            () -> {
                // Yes clicked
                groceryList.clearAll();
                GroceryService.saveGroceryList(groceryList);
                refreshIngredientDisplay();
                updateStatistics();
            },
            null // No action needed for cancel
        );
    }

    /**
     * Refresh the ingredient list display
     */
    private void refreshIngredientDisplay() {
        if (ingredientListContainer == null) return;
        
        // Clear existing items (except empty state label)
        ingredientListContainer.getChildren().removeIf(node -> !(node instanceof Label && node == emptyStateLabel));
        
        List<GroceryItem> items = groceryList.getItems();
        
        if (items.isEmpty()) {
            if (emptyStateLabel != null) {
                emptyStateLabel.setVisible(true);
            }
        } else {
            if (emptyStateLabel != null) {
                emptyStateLabel.setVisible(false);
            }
            
            // Add each ingredient item
            for (GroceryItem item : items) {
                HBox itemRow = createIngredientRow(item);
                ingredientListContainer.getChildren().add(itemRow);
            }
        }
    }

    /**
     * Create a visual row for an ingredient item
     */
    private HBox createIngredientRow(GroceryItem item) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(15.0);
        row.setPadding(new Insets(10, 15, 10, 15));
        row.setStyle("-fx-background-color: " + (item.isPurchased() ? "#E8F5E8" : "#FFFFFF") + 
                     "; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Checkbox
        CheckBox checkbox = new CheckBox();
        checkbox.setSelected(item.isPurchased());
        checkbox.setStyle("-fx-text-fill: #2E7D32;");
        checkbox.setOnAction(e -> {
            item.setPurchased(checkbox.isSelected());
            GroceryService.saveGroceryList(groceryList);
            refreshIngredientDisplay();
            updateStatistics();
        });

        // Ingredient info
        VBox infoBox = new VBox();
        infoBox.setSpacing(2.0);
        
        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("Lato Regular", 16.0));
        nameLabel.setStyle("-fx-text-fill: " + (item.isPurchased() ? "#666666" : "#333333") + 
                          (item.isPurchased() ? "; -fx-strikethrough: true" : "") + ";");
        
        Label quantityLabel = new Label(formatItemQuantity(item));
        quantityLabel.setFont(Font.font("Lato Regular", 14.0));
        quantityLabel.setStyle("-fx-text-fill: #888888;");
        
        infoBox.getChildren().addAll(nameLabel, quantityLabel);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Delete button
        Button deleteButton = new Button("🗑️");
        deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF6B6B; -fx-font-size: 16px; -fx-padding: 5;");
        deleteButton.setOnAction(e -> {
            groceryList.removeItem(item);
            GroceryService.saveGroceryList(groceryList);
            refreshIngredientDisplay();
            updateStatistics();
        });
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #FFE5E5; -fx-text-fill: #FF6B6B; -fx-font-size: 16px; -fx-padding: 5; -fx-border-radius: 3; -fx-background-radius: 3;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF6B6B; -fx-font-size: 16px; -fx-padding: 5;"));

        row.getChildren().addAll(checkbox, infoBox, spacer, deleteButton);
        return row;
    }

    /**
     * Update statistics display
     */
    private void updateStatistics() {
        if (totalItemsLabel == null || checkedItemsLabel == null || progressLabel == null) return;
        
        int totalItems = groceryList.getItems().size();
        int checkedItems = (int) groceryList.getItems().stream().mapToInt(item -> item.isPurchased() ? 1 : 0).sum();
        double progress = totalItems > 0 ? (double) checkedItems / totalItems * 100 : 0;
        
        totalItemsLabel.setText("Total Items: " + totalItems);
        checkedItemsLabel.setText("Purchased: " + checkedItems);
        progressLabel.setText(String.format("%.0f%% Complete", progress));
    }

    /**
     * Format item quantity for display
     */
    private String formatItemQuantity(GroceryItem item) {
        if (item.getAmount() > 0 && item.getUnit() != null && !item.getUnit().isEmpty()) {
            // Format the amount to remove unnecessary decimal places
            if (item.getAmount() == (long) item.getAmount()) {
                return String.format("%.0f %s", item.getAmount(), item.getUnit());
            } else {
                return String.format("%.1f %s", item.getAmount(), item.getUnit());
            }
        } else {
            return "1 unit"; // Default quantity
        }
    }
}
