package org.example.meal_planner.controllers.Dashboard;

import java.util.ArrayList;
import java.util.List;

import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.MealTemplate;
import org.example.meal_planner.utils.ModalUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MealDialogController {

    @FXML private VBox rootPane; // Reference to root pane for modal alerts
    @FXML private TextField nameField;
    @FXML private TextField servingsField;
    @FXML private Label calculatedCaloriesLabel;
    @FXML private TextField carbsField;
    @FXML private TextField fatField;
    @FXML private TextField proteinField;
    @FXML private VBox ingredientsContainer;
    @FXML private Button addIngredientButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private MealTemplate result;

    @FXML
    private void initialize() {
        // Add listeners to auto-calculate calories when macronutrients change
        carbsField.textProperty().addListener((obs, oldVal, newVal) -> updateCalculatedCalories());
        fatField.textProperty().addListener((obs, oldVal, newVal) -> updateCalculatedCalories());
        proteinField.textProperty().addListener((obs, oldVal, newVal) -> updateCalculatedCalories());
    }

    private void updateCalculatedCalories() {
        try {
            double carbs = isValidDouble(carbsField.getText()) ? Double.parseDouble(carbsField.getText().trim()) : 0;
            double fat = isValidDouble(fatField.getText()) ? Double.parseDouble(fatField.getText().trim()) : 0;
            double protein = isValidDouble(proteinField.getText()) ? Double.parseDouble(proteinField.getText().trim()) : 0;
            
            double calculatedCalories = (carbs * 4) + (fat * 9) + (protein * 4);
            calculatedCaloriesLabel.setText(String.format("%.1f calories", calculatedCalories));
        } catch (Exception e) {
            calculatedCaloriesLabel.setText("0.0 calories");
        }
    }

    public void setMealTemplate(MealTemplate template) {
        if (template != null) {
            nameField.setText(template.getName());
            servingsField.setText(String.valueOf(template.getServings()));
            carbsField.setText(String.valueOf(template.getCarbs()));
            fatField.setText(String.valueOf(template.getFat()));
            proteinField.setText(String.valueOf(template.getProtein()));
            
            // Load ingredients
            populateIngredients(template.getIngredients());
            
            updateCalculatedCalories();
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            result = new MealTemplate();
            result.setName(nameField.getText().trim());
            result.setServings(Integer.parseInt(servingsField.getText().trim()));
            result.setCarbs(Double.parseDouble(carbsField.getText().trim()));
            result.setFat(Double.parseDouble(fatField.getText().trim()));
            result.setProtein(Double.parseDouble(proteinField.getText().trim()));
            
            // Collect ingredients
            result.setIngredients(collectIngredients());
            
            closeDialog();
        }
        else {
            closeDialog();
        }
    }

    @FXML
    private void handleCancel() {
        result = null;
        closeDialog();
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.append("Meal name is required.\n");
        }

        if (!isValidInteger(servingsField.getText())) {
            errors.append("Servings must be a valid number.\n");
        }

        if (!isValidDouble(carbsField.getText())) {
            errors.append("Carbs must be a valid number.\n");
        }

        if (!isValidDouble(fatField.getText())) {
            errors.append("Fat must be a valid number.\n");
        }

        if (!isValidDouble(proteinField.getText())) {
            errors.append("Protein must be a valid number.\n");
        }

        if (errors.length() > 0) {
            showAlert("Input Error", "Please correct the following errors:\n" + errors.toString());
            return false;
        }

        return true;
    }
    
    private void showAlert(String title, String content) {
        Pane targetPane = rootPane;
        if (targetPane == null) {
            targetPane = ModalUtils.getRootPane(nameField);
        }
        
        if (targetPane != null) {
            ModalUtils.showInPageAlert(targetPane, title, content, null);
        } else {
            // Use rootPane as fallback
            ModalUtils.showInPageAlert(rootPane, title, content, null);
        }
    }

    private boolean isValidInteger(String text) {
        try {
            Integer.parseInt(text.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDouble(String text) {
        try {
            Double.parseDouble(text.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public MealTemplate getResult() {
        return result;
    }

    @FXML
    private void handleAddIngredient() {
        addIngredientRow("", 0.0, "");
    }

    private void populateIngredients(List<Ingredient> ingredients) {
        if (ingredientsContainer != null) {
            ingredientsContainer.getChildren().clear();
            if (ingredients != null && !ingredients.isEmpty()) {
                for (Ingredient ingredient : ingredients) {
                    addIngredientRow(ingredient.getName(), ingredient.getAmount(), ingredient.getUnit());
                }
            }
        }
    }

    private void addIngredientRow(String name, double amount, String unit) {
        if (ingredientsContainer != null) {
            javafx.scene.layout.HBox ingredientRow = new javafx.scene.layout.HBox(10);
            ingredientRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            TextField ingredientNameField = new TextField(name);
            ingredientNameField.setPromptText("Ingredient name");
            ingredientNameField.setPrefWidth(200);
            ingredientNameField.getStyleClass().add("ingredient-field");
            
            TextField amountField = new TextField(amount > 0 ? String.valueOf(amount) : "");
            amountField.setPromptText("Amount");
            amountField.setPrefWidth(70);
            amountField.getStyleClass().add("ingredient-field");
            
            ComboBox<String> unitComboBox = new ComboBox<>();
            unitComboBox.getItems().addAll(
                "g", "kg", "ml", "l", "cup", "tbsp", "tsp", "oz", "lb", "piece", "slice", "can", "package"
            );
            unitComboBox.setEditable(true);
            if (unit != null && !unit.isEmpty()) {
                unitComboBox.setValue(unit);
            } else {
                unitComboBox.setPromptText("Unit");
            }
            unitComboBox.setPrefWidth(80);
            unitComboBox.getStyleClass().add("combo-box");
            
            Button removeButton = new Button("🗑️");
            removeButton.getStyleClass().add("ingredient-remove-button");
            removeButton.setOnAction(e -> ingredientsContainer.getChildren().remove(ingredientRow));
            
            ingredientRow.getChildren().addAll(ingredientNameField, amountField, unitComboBox, removeButton);
            ingredientsContainer.getChildren().add(ingredientRow);
        }
    }

    private List<Ingredient> collectIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        if (ingredientsContainer != null) {
            for (javafx.scene.Node node : ingredientsContainer.getChildren()) {
                if (node instanceof javafx.scene.layout.HBox) {
                    javafx.scene.layout.HBox row = (javafx.scene.layout.HBox) node;
                    if (row.getChildren().size() >= 3) {
                        TextField ingredientNameField = (TextField) row.getChildren().get(0);
                        TextField amountField = (TextField) row.getChildren().get(1);
                        ComboBox<String> unitComboBox = (ComboBox<String>) row.getChildren().get(2);
                        
                        String name = ingredientNameField.getText().trim();
                        String amountText = amountField.getText().trim();
                        String unit = unitComboBox.getValue() != null ? unitComboBox.getValue().trim() : "";
                        
                        if (!name.isEmpty() && !amountText.isEmpty() && !unit.isEmpty()) {
                            try {
                                double amount = Double.parseDouble(amountText);
                                ingredients.add(new Ingredient(name, amount, unit));
                            } catch (NumberFormatException e) {
                                // Skip invalid ingredients
                            }
                        }
                    }
                }
            }
        }
        return ingredients;
    }
}
