package org.example.meal_planner.controllers;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.example.meal_planner.controllers.Profile_Setup.SceneNavigator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application controller that manages the overall application flow
 * and decides whether to show profile setup or dashboard based on user data
 */
public class AppController {
    
    private final Stage stage;
    
    // File paths for user data
    private static final String USER_PROFILE_PATH = "data/user_profile.xml";
    private static final String MEAL_PLAN_PATH = "data/meal_plan.xml";
    
    public AppController(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Starts the application by checking if user profile exists
     * and navigating to appropriate screen
     */
    public void startApplication() {
        if (isFirstTimeUser()) {
            showProfileSetup();
        } else {
            showDashboard();
        }
    }
    
    /**
     * Checks if this is the first time the user is opening the app
     * by verifying if profile and meal plan files exist and contain data
     */
    private boolean isFirstTimeUser() {
        File userProfileFile = new File(USER_PROFILE_PATH);
        File mealPlanFile = new File(MEAL_PLAN_PATH);
        

        
        // Check if files are empty or contain minimal data
        try {
            // Check if files exist
            if (!userProfileFile.exists() && !mealPlanFile.exists()) {
                return true;
            }
            
            return !hasCompleteProfileData();
            
        } catch (Exception e) {
            System.err.println("Error checking user data: " + e.getMessage());
            return true; // Assume first-time user if there's an error
        }
    }
    
    /**
     * Checks if the user profile contains complete data
     */
    private boolean hasCompleteProfileData() {
        try {
            File userProfileFile = new File(USER_PROFILE_PATH);
            if (!userProfileFile.exists() || userProfileFile.length() == 0) {
                return false;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(userProfileFile);
            doc.getDocumentElement().normalize();

            // Check for the presence and content of essential nodes
            String[] essentialTags = {"dietType", "height", "weight", "age"};
            for (String tag : essentialTags) {
                NodeList nodeList = doc.getElementsByTagName(tag);
                if (nodeList.getLength() == 0) {
                    // Tag does not exist
                    return false; 
                }
                String content = nodeList.item(0).getTextContent();
                if (content == null || content.trim().isEmpty()) {
                    // Tag is empty
                    return false; 
                }
            }

            // If all checks pass, the profile is considered complete
            return true;

        } catch (Exception e) {
            System.err.println("Error parsing or validating profile XML: " + e.getMessage());
            // If parsing fails, treat it as if the profile is not complete
            return false;
        }
    }
    
    /**
     * Shows the profile setup wizard for first-time users
     */
    public void showProfileSetup() {
        try {
            SceneNavigator sceneNavigator = new SceneNavigator(stage, this);
            stage.setTitle("DailyDish - Profile Setup");
            sceneNavigator.showFirstPage();
        } catch (Exception e) {
            System.err.println("Error showing profile setup: " + e.getMessage());
        }
    }
    
    /**
     * Shows the main dashboard for existing users
     */
    public void showDashboard() {
        try {
            String fxmlPath = "/org/example/meal_planner/Dashboard_Resources/fxml/TodayDashboard.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root, 1280, 800);
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
            }
            
            stage.setTitle("DailyDish - Today's Plan");
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard. FXML might be malformed or a resource is missing.");
            e.printStackTrace(); // Print the full stack trace for detailed debugging
            // Fallback to profile setup if dashboard fails to load
            showProfileSetup();
        }
    }
    
    /**
     * Called when profile setup is completed successfully
     * Transitions user to the dashboard
     */
    public void onProfileSetupComplete() {
        showDashboard();
    }
    
    /**
     * Gets the stage for controllers that need it
     */
    public Stage getStage() {
        return stage;
    }
}
