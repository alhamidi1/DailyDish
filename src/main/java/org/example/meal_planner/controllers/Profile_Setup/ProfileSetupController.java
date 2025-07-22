package org.example.meal_planner.controllers.Profile_Setup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

// STEP 1: Implement the WizardController interface
public class ProfileSetupController implements WizardController {

    // STEP 2: Add fields for the navigator and data model
    private SceneNavigator sceneNavigator;
    private ProfileSetupData profileSetupData;

    // STEP 3: Implement the init method to receive the objects
    @Override
    public void init(SceneNavigator sceneNavigator, ProfileSetupData profileSetupData) {
        this.sceneNavigator = sceneNavigator;
        this.profileSetupData = profileSetupData;
    }

    // STEP 4: Update your button handler to use the navigator
    @FXML
    private void onGetStartedClicked() {
        // Instead of loading a new scene, just tell the navigator to go next
        sceneNavigator.showNextPage();
    }

    @FXML private Label usernameLabel;
    @FXML private Button getStartedButton;
}