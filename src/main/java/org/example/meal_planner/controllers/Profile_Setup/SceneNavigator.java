package org.example.meal_planner.controllers.Profile_Setup;

import java.io.IOException;
import java.util.List;

import org.example.meal_planner.controllers.AppController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneNavigator {

    private final Stage stage;
    private final AppController appController;
    private final ProfileSetupData profileSetupData = new ProfileSetupData();
    private final List<String> wizardPages = List.of(
            "ProfileSetup.fxml",
            "DietWelcome.fxml",
            "DietType.fxml",
            "FoodAvoidance.fxml",
            "AboutYouWelcome.fxml",
            "UserDetails.fxml",
            "UserGoals.fxml"
    );

    private int currentPageIndex = 0;

    public SceneNavigator(Stage stage, AppController appController) {
        this.stage = stage;
        this.appController = appController;
    }

    public void showFirstPage() {
        currentPageIndex = 0;
        loadSceneByName(wizardPages.get(currentPageIndex));
    }

    public void showNextPage() {
        if (currentPageIndex < wizardPages.size() - 1) {
            currentPageIndex++;
            loadSceneByName(wizardPages.get(currentPageIndex));
        } else {
            // Wizard finished, transition to dashboard
            appController.onProfileSetupComplete();
        }
    }

    public void showPreviousPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            loadSceneByName(wizardPages.get(currentPageIndex));
        }
    }

    private void loadSceneByName(String fxmlFileName) {
        try {
            // Construct the full path to the FXML file
            String fxmlPath = "/org/example/meal_planner/Profile_Setup_resources/fxml/" + fxmlFileName;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get the controller and pass it the navigator and data model
            WizardController controller = loader.getController();
            controller.init(this, profileSetupData);

            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root, 1280, 800);
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
            }

        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlFileName + " - " + e.getMessage());
        }
    }
}