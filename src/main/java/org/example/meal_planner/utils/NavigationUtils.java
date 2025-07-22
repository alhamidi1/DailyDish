package org.example.meal_planner.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Navigation utility class that provides seamless content switching
 * without changing the main layout or exiting fullscreen mode
 */
public class NavigationUtils {
    
    private static BorderPane mainLayout;
    private static Node leftNavigation;
    private static Node originalDashboardContent;
    
    // Navigation button references for highlighting
    private static HBox dashboardButton;
    private static HBox groceriesButton;
    private static HBox discoverButton;
    private static HBox profileButton;
    
    /**
     * Initialize the navigation system with the main layout
     * @param layout The main BorderPane layout
     */
    public static void initializeMainLayout(BorderPane layout) {
        mainLayout = layout;
        leftNavigation = layout.getLeft(); // Store the navigation pane
        originalDashboardContent = layout.getCenter(); // Cache the original dashboard content
        
        // Find and store navigation button references
        findNavigationButtons();
        
        // Set initial highlighting - Dashboard is active by default
        highlightButton("dashboard");
    }
    
    /**
     * Find and store references to navigation buttons
     */
    private static void findNavigationButtons() {
        if (leftNavigation instanceof BorderPane) {
            BorderPane navPane = (BorderPane) leftNavigation;
            Node centerNode = navPane.getCenter();
            if (centerNode instanceof VBox) {
                VBox navContainer = (VBox) centerNode;
                for (Node child : navContainer.getChildren()) {
                    if (child instanceof HBox) {
                        HBox button = (HBox) child;
                        String id = button.getId();
                        
                        if ("dashboardButton".equals(id)) {
                            dashboardButton = button;
                        } else if ("groceriesButton".equals(id)) {
                            groceriesButton = button;
                        } else if ("discoverButton".equals(id)) {
                            discoverButton = button;
                        } else if ("profileButton".equals(id)) {
                            profileButton = button;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Highlight the active navigation button and remove highlighting from others
     * @param activeButton The button to highlight ("dashboard", "groceries", "discover", "profile")
     */
    public static void highlightButton(String activeButton) {
        // Remove active styling from all buttons
        removeActiveStyleFromButton(dashboardButton);
        removeActiveStyleFromButton(groceriesButton);
        removeActiveStyleFromButton(discoverButton);
        removeActiveStyleFromButton(profileButton);
        
        // Add active styling to the specified button
        switch (activeButton.toLowerCase()) {
            case "dashboard":
                addActiveStyleToButton(dashboardButton);
                break;
            case "groceries":
                addActiveStyleToButton(groceriesButton);
                break;
            case "discover":
                addActiveStyleToButton(discoverButton);
                break;
            case "profile":
                addActiveStyleToButton(profileButton);
                break;
        }
    }
    
    /**
     * Remove active styling from a navigation button
     */
    private static void removeActiveStyleFromButton(HBox button) {
        if (button != null) {
            button.getStyleClass().removeAll("nav-button-active");
        }
    }
    
    /**
     * Add active styling to a navigation button
     */
    private static void addActiveStyleToButton(HBox button) {
        if (button != null) {
            if (!button.getStyleClass().contains("nav-button-active")) {
                button.getStyleClass().add("nav-button-active");
            }
        }
    }
    
    /**
     * Restore the original dashboard content
     */
    public static void restoreDashboardContent() {
        if (mainLayout != null && originalDashboardContent != null) {
            mainLayout.setCenter(originalDashboardContent);
            // Ensure left navigation is always present
            if (mainLayout.getLeft() == null) {
                mainLayout.setLeft(leftNavigation);
            }
        }
        // Highlight the dashboard button
        highlightButton("dashboard");
    }
    
    /**
     * Load content into the center of the main layout, preserving navigation and fullscreen
     * @param fxmlPath The path to the FXML file to load as center content
     */
    public static void loadCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtils.class.getResource(fxmlPath));
            Node content = loader.load();
            
            if (mainLayout != null) {
                // Only replace the center content, keep navigation intact
                mainLayout.setCenter(content);
                // Ensure left navigation is always present
                if (mainLayout.getLeft() == null) {
                    mainLayout.setLeft(leftNavigation);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error loading content from " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load content and highlight the corresponding navigation button
     * @param fxmlPath The path to the FXML file to load as center content
     * @param activeButton The button to highlight ("dashboard", "groceries", "discover", "profile")
     */
    public static void loadCenterContentWithHighlight(String fxmlPath, String activeButton) {
        loadCenterContent(fxmlPath);
        highlightButton(activeButton);
    }
    
    /**
     * Get the main layout BorderPane
     * @return The main BorderPane layout
     */
    public static BorderPane getMainLayout() {
        return mainLayout;
    }
    
    /**
     * Legacy method for backward compatibility - now delegates to content switching
     * @deprecated Use loadCenterContent instead for seamless navigation
     */
    @Deprecated
    public static void navigateToPage(Stage currentStage, String fxmlPath, String title) {
        try {
            // Remember the current fullscreen state
            boolean wasFullScreen = currentStage.isFullScreen();
            
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(NavigationUtils.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Create new scene with the same dimensions
            Scene currentScene = currentStage.getScene();
            Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
            
            // Set the new scene
            currentStage.setScene(newScene);
            currentStage.setTitle(title);
            
            // Restore fullscreen state if it was enabled
            if (wasFullScreen) {
                currentStage.setFullScreen(true);
            }
            
        } catch (IOException e) {
            System.err.println("Error navigating to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get the current stage from any JavaFX node
     * @param source Any JavaFX node that is part of the scene graph
     * @return The stage containing the node
     */
    public static Stage getCurrentStage(javafx.scene.Node source) {
        return (Stage) source.getScene().getWindow();
    }
}
