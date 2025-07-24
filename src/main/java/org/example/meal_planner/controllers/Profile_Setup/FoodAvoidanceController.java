package org.example.meal_planner.controllers.Profile_Setup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import org.example.meal_planner.models.ProfileSetupData;

import java.util.ArrayList;
import java.util.List;

public class FoodAvoidanceController implements WizardController {

    @FXML private FlowPane foodItemsPane; // The container for the buttons
    @FXML private Label userIdLabel;

    @FXML private ToggleButton dairyToggle;
    @FXML private ToggleButton eggsToggle;
    @FXML private ToggleButton fishToggle;
    @FXML private ToggleButton glutenToggle;
    @FXML private ToggleButton peanutsToggle;
    @FXML private ToggleButton sesameToggle;
    @FXML private ToggleButton shellfishToggle;
    @FXML private ToggleButton soyToggle;
    @FXML private ToggleButton treeNutsToggle;

    @FXML private Button backButton;
    @FXML private Button continueButton;

    private SceneNavigator sceneNavigator;
    private ProfileSetupData profileSetupData;

    @Override
    public void init(SceneNavigator sceneNavigator, ProfileSetupData profileSetupData) {
        this.sceneNavigator = sceneNavigator;
        this.profileSetupData = profileSetupData;
    }

    @FXML
    private void onContinueClicked() {
        // Find all selected ToggleButtons and save their text
        List<String> foodsToAvoid = new ArrayList<>();
        foodItemsPane.getChildren().forEach(node -> {
            if (node instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) node;
                if (button.isSelected()) {
                    foodsToAvoid.add(button.getText());
                }
            }
        });
        profileSetupData.setFoodsToAvoid(foodsToAvoid);

        sceneNavigator.showNextPage();
    }

    @FXML
    private void onBackClicked() {
        sceneNavigator.showPreviousPage();
    }
}