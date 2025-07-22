package org.example.meal_planner.controllers.Profile_Setup;

import java.net.URL;
import java.util.ResourceBundle;

import org.example.meal_planner.utils.ModalUtils;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class UserDetailsController implements WizardController, Initializable {

    @FXML private ToggleGroup sexToggleGroup;
    @FXML private ToggleGroup bodyfatToggleGroup;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private TextField ageField;
    @FXML private ChoiceBox<String> activityLevelChoiceBox;
    @FXML private Label userIdLabel;
    @FXML private Button backButton;
    @FXML private Button continueButton;
    @FXML private BorderPane rootPane; // Add reference to root pane for modal alerts

    private SceneNavigator sceneNavigator;
    private ProfileSetupData profileSetupData;

    @Override
    public void init(SceneNavigator sceneNavigator, ProfileSetupData profileSetupData) {
        this.sceneNavigator = sceneNavigator;
        this.profileSetupData = profileSetupData;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        activityLevelChoiceBox.setItems(FXCollections.observableArrayList(
                "Not very active",
                "Lightly active (exercise 1-2 days/week)",
                "Active daily, frequent exercise",
                "Very active (hard exercise every day)"
        ));
        activityLevelChoiceBox.setValue("Active daily, frequent exercise");
    }

    @FXML
    private void onContinueClicked() {
        // Save all data from the form
        ToggleButton selectedSex = (ToggleButton) sexToggleGroup.getSelectedToggle();
        if (selectedSex != null) {
            profileSetupData.setSex(selectedSex.getText());
        }

        ToggleButton selectedBodyfat = (ToggleButton) bodyfatToggleGroup.getSelectedToggle();
        if (selectedBodyfat != null) {
            profileSetupData.setBodyfatLevel(selectedBodyfat.getText());
        }

        try {
            int height = Integer.parseInt(heightField.getText().trim());
            int weight = Integer.parseInt(weightField.getText().trim());
            int age = Integer.parseInt(ageField.getText().trim());

            // Validate minimum values
            if (height < 50 || height > 300) {
                showAlert("Invalid Height", "Please enter a height between 50 and 300 cm.");
                return;
            }

            if (weight < 20 || weight > 500) {
                showAlert("Invalid Weight", "Please enter a weight between 20 and 500 kg.");
                return;
            }

            if (age < 5 || age > 120) {
                showAlert("Invalid Age", "Please enter an age between 5 and 120.");
                return;
            }

            profileSetupData.setHeight(height);
            profileSetupData.setWeight(weight);
            profileSetupData.setAge(age);

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please ensure height, weight, and age are valid numbers.");
            return;
        }

        profileSetupData.setActivityLevel(activityLevelChoiceBox.getValue());
        
        sceneNavigator.showNextPage();
    }

    @FXML
    private void onBackClicked() {
        sceneNavigator.showPreviousPage();
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
}