package org.example.meal_planner.controllers.Profile_Setup;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.meal_planner.models.ProfileSetupData;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DietTypeController implements WizardController, Initializable {

    // FXML fields for UI components
    @FXML private HBox anythingOptionBox;
    @FXML private HBox ketoOptionBox;
    @FXML private HBox mediterraneanOptionBox;
    // ... add fx:id for other diet HBoxes here

    @FXML private Label userIdLabel;
    @FXML private VBox dietOptionsContainer;
    @FXML private Button backButton;
    @FXML private Button continueButton;

    private SceneNavigator sceneNavigator;
    private ProfileSetupData profileSetupData;
    private List<HBox> allOptionBoxes;
    private HBox currentlySelectedBox;

    @Override
    public void init(SceneNavigator sceneNavigator, ProfileSetupData profileSetupData) {
        this.sceneNavigator = sceneNavigator;
        this.profileSetupData = profileSetupData;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allOptionBoxes = new ArrayList<>();
        allOptionBoxes.add(anythingOptionBox);
        allOptionBoxes.add(ketoOptionBox);
        allOptionBoxes.add(mediterraneanOptionBox);
        // ... add other diet boxes to the list here

        for (HBox box : allOptionBoxes) {
            box.setOnMouseClicked(event -> selectBox(box));
        }

        // Set a default selection
        selectBox(anythingOptionBox);
    }

    @FXML
    private void onContinueClicked() {
        // Find the name of the diet from the selected box and save it
        if (currentlySelectedBox != null) {
            // The diet name is in the first Label inside the VBox of the HBox
            VBox labelsVBox = (VBox) currentlySelectedBox.getChildren().get(0);
            Label dietNameLabel = (Label) labelsVBox.getChildren().get(0);
            profileSetupData.setDietType(dietNameLabel.getText());
        }
        sceneNavigator.showNextPage();
    }

    @FXML
    private void onBackClicked() {
        sceneNavigator.showPreviousPage();
    }

    private void selectBox(HBox boxToSelect) {
        if (currentlySelectedBox != null) {
            currentlySelectedBox.getStyleClass().remove("selected");
        }
        boxToSelect.getStyleClass().add("selected");
        currentlySelectedBox = boxToSelect;
    }
}