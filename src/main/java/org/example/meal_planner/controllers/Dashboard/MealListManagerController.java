package org.example.meal_planner.controllers.Dashboard;

import java.io.IOException;
import java.util.List;

import org.example.meal_planner.models.DayMealPlan;
import org.example.meal_planner.models.Food;
import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.Meal;
import org.example.meal_planner.models.MealTemplate;
import org.example.meal_planner.services.MealTemplateService;
import org.example.meal_planner.services.XmlDataService;
import org.example.meal_planner.utils.ModalUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MealListManagerController {

    @FXML private TableView<MealTemplate> mealTableView;
    @FXML private TableColumn<MealTemplate, String> nameColumn;
    @FXML private TableColumn<MealTemplate, Integer> servingsColumn;
    @FXML private TableColumn<MealTemplate, Double> caloriesColumn;
    @FXML private TableColumn<MealTemplate, Double> carbsColumn;
    @FXML private TableColumn<MealTemplate, Double> fatColumn;
    @FXML private TableColumn<MealTemplate, Double> proteinColumn;
    
    @FXML private Button addNewMealButton;
    @FXML private Button editMealButton;
    @FXML private Button deleteMealButton;
    @FXML private Button addToPlanButton;
    @FXML private BorderPane rootPane; // Reference to root pane for modal alerts

    private ObservableList<MealTemplate> mealTemplates;
    private String selectedMealType; // breakfast, lunch, or dinner
    private TodayDashboardController dashboardController; // Reference to dashboard controller

    // Setter method to receive dashboard controller reference
    public void setDashboardController(TodayDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadMealTemplates();
        setupButtonStates();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        servingsColumn.setCellValueFactory(new PropertyValueFactory<>("servings"));
        caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));
        carbsColumn.setCellValueFactory(new PropertyValueFactory<>("carbs"));
        fatColumn.setCellValueFactory(new PropertyValueFactory<>("fat"));
        proteinColumn.setCellValueFactory(new PropertyValueFactory<>("protein"));

        // Format numeric columns with better styling
        caloriesColumn.setCellFactory(col -> new TableCell<MealTemplate, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1f cal", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #2E7D32;");
                }
            }
        });

        carbsColumn.setCellFactory(col -> new TableCell<MealTemplate, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1fg", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #FF6F00;");
                }
            }
        });

        fatColumn.setCellFactory(col -> new TableCell<MealTemplate, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1fg", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #795548;");
                }
            }
        });

        proteinColumn.setCellFactory(col -> new TableCell<MealTemplate, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1fg", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #C2185B;");
                }
            }
        });

        // Add styling to servings column
        servingsColumn.setCellFactory(col -> new TableCell<MealTemplate, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item + " serving" + (item > 1 ? "s" : ""));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
                }
            }
        });

        // Add styling to name column
        nameColumn.setCellFactory(col -> new TableCell<MealTemplate, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
                }
            }
        });
    }

    private void loadMealTemplates() {
        List<MealTemplate> templates = MealTemplateService.loadMealTemplates();
        mealTemplates = FXCollections.observableArrayList(templates);
        mealTableView.setItems(mealTemplates);
    }

    private void setupButtonStates() {
        // Enable/disable buttons based on selection
        mealTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean hasSelection = newValue != null;
                editMealButton.setDisable(!hasSelection);
                deleteMealButton.setDisable(!hasSelection);
                addToPlanButton.setDisable(!hasSelection);
            });
    }

    @FXML
    private void handleAddNewMeal() {
        MealTemplate newMeal = showMealDialog("Add New Meal", new MealTemplate());
        if (newMeal != null) {
            mealTemplates.add(newMeal);
            saveMealTemplates();
            mealTableView.getSelectionModel().select(newMeal);
        }
    }

    @FXML
    private void handleEditMeal() {
        MealTemplate selectedMeal = mealTableView.getSelectionModel().getSelectedItem();
        if (selectedMeal != null) {
            MealTemplate editedMeal = showMealDialog("Edit Meal", selectedMeal);
            if (editedMeal != null) {
                int index = mealTemplates.indexOf(selectedMeal);
                mealTemplates.set(index, editedMeal);
                saveMealTemplates();
                mealTableView.refresh();
            }
        }
    }

    @FXML
    private void handleDeleteMeal() {
        MealTemplate selectedMeal = mealTableView.getSelectionModel().getSelectedItem();
        if (selectedMeal != null) {
            // Show confirmation dialog using ModalUtils
            ModalUtils.showConfirmationDialog(
                rootPane,
                "Delete Meal",
                "Are you sure you want to delete \"" + selectedMeal.getName() + "\"?",
                () -> {
                    // User confirmed - delete the meal
                    mealTemplates.remove(selectedMeal);
                    saveMealTemplates();
                },
                () -> {
                    // User cancelled - do nothing
                }
            );
        }
    }

    @FXML
    private void handleAddToPlan() {
        MealTemplate selectedMeal = mealTableView.getSelectionModel().getSelectedItem();
        if (selectedMeal != null && selectedMealType != null) {
            try {
                // Load current meal plan from XML using XmlDataService
                DayMealPlan currentMealPlan = XmlDataService.load("data/meal_plan.xml", DayMealPlan.class, Meal.class, Food.class, Ingredient.class);
                
                // If no meal plan exists, create an empty one
                if (currentMealPlan == null) {
                    currentMealPlan = new DayMealPlan();
                }
                
                // Convert MealTemplate to Food with ingredients
                Food newFood = new Food(
                    selectedMeal.getName(),
                    selectedMeal.getCalories(),
                    selectedMeal.getCarbs(),
                    selectedMeal.getFat(),
                    selectedMeal.getProtein()
                );
                
                // Copy ingredients from MealTemplate to Food
                if (selectedMeal.getIngredients() != null && !selectedMeal.getIngredients().isEmpty()) {
                    newFood.setIngredients(new java.util.ArrayList<>(selectedMeal.getIngredients()));
                }
                
                // Add to appropriate meal
                switch (selectedMealType.toLowerCase()) {
                    case "breakfast":
                        if (currentMealPlan.getBreakfast() == null) {
                            currentMealPlan.setBreakfast(new Meal("breakfast"));
                        }
                        currentMealPlan.getBreakfast().addFood(newFood);
                        break;
                    case "lunch":
                        if (currentMealPlan.getLunch() == null) {
                            currentMealPlan.setLunch(new Meal("lunch"));
                        }
                        currentMealPlan.getLunch().addFood(newFood);
                        break;
                    case "dinner":
                        if (currentMealPlan.getDinner() == null) {
                            currentMealPlan.setDinner(new Meal("dinner"));
                        }
                        currentMealPlan.getDinner().addFood(newFood);
                        break;
                }
                
                // Save updated meal plan using XmlDataService
                XmlDataService.save(currentMealPlan, "data/meal_plan.xml", Meal.class, Food.class, Ingredient.class);
                
                // Show success message using ModalUtils
                ModalUtils.showInPageAlert(
                    rootPane,
                    "✅ Meal Added Successfully",
                    "\"" + selectedMeal.getName() + "\" has been added to your " + selectedMealType + " plan!",
                    () -> {
                        // Close the popup window when OK is clicked
                        Stage stage = (Stage) addToPlanButton.getScene().getWindow();
                        stage.close();
                    }
                );
                
            } catch (Exception e) {
                e.printStackTrace();
                // Show error message using ModalUtils
                ModalUtils.showInPageAlert(
                    rootPane,
                    "❌ Error",
                    "Failed to add meal to plan. Please try again.",
                    () -> {
                        // Do nothing on OK click
                    }
                );
            }
        }
    }

    private MealTemplate showMealDialog(String title, MealTemplate meal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/meal_planner/Dashboard_Resources/fxml/content/MealDialog.fxml"));
            Parent root = loader.load();
            
            MealDialogController controller = loader.getController();
            controller.setMealTemplate(meal);
            
            Stage dialog = new Stage();
            dialog.setTitle(title);
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(mealTableView.getScene().getWindow());
            
            // Set fixed dimensions and disable resizing for consistent layout
            dialog.setResizable(false);
            dialog.setWidth(520); // Slightly larger than FXML width to account for window decorations
            dialog.setHeight(640); // Slightly larger than FXML height to account for window decorations
            
            dialog.showAndWait();
            
            return controller.getResult();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveMealTemplates() {
        MealTemplateService.saveMealTemplates(mealTemplates);
    }

    // Method to set the selected meal type when navigating from dashboard
    public void setSelectedMealType(String mealType) {
        this.selectedMealType = mealType;
    }
}
