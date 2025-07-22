package org.example.meal_planner.utils;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Utility class for creating modal overlays, alerts, and loading screens
 * that work properly in fullscreen mode
 */
public class ModalUtils {
    
    /**
     * Creates an in-page alert overlay that doesn't create a separate window
     */
    public static void showInPageAlert(Pane parentPane, String title, String message, Runnable onOkClicked) {
        // Create overlay background that fills the entire parent
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        
        // Get parent dimensions
        double parentWidth = parentPane.getWidth();
        double parentHeight = parentPane.getHeight();
        
        // If parent dimensions are not available, use bounds
        if (parentWidth <= 0 || parentHeight <= 0) {
            parentWidth = parentPane.getBoundsInLocal().getWidth();
            parentHeight = parentPane.getBoundsInLocal().getHeight();
        }
        
        // If still no dimensions, use default large size
        if (parentWidth <= 0 || parentHeight <= 0) {
            parentWidth = 1280;
            parentHeight = 738;
        }
        
        // Set absolute positioning and sizing - force the overlay to be exactly the parent size
        overlay.setLayoutX(0);
        overlay.setLayoutY(0);
        overlay.resize(parentWidth, parentHeight);
        overlay.setPrefSize(parentWidth, parentHeight);
        overlay.setMinSize(parentWidth, parentHeight);
        overlay.setMaxSize(parentWidth, parentHeight);
        overlay.setPickOnBounds(true);
        overlay.setMouseTransparent(false);
        
        // Consume ALL mouse and key events to block interaction with underlying content
        overlay.setOnMouseClicked(event -> event.consume());
        overlay.setOnMousePressed(event -> event.consume());
        overlay.setOnMouseReleased(event -> event.consume());
        overlay.setOnMouseDragged(event -> event.consume());
        overlay.setOnMouseMoved(event -> event.consume());
        overlay.setOnKeyPressed(event -> event.consume());
        overlay.setOnKeyReleased(event -> event.consume());
        overlay.setOnKeyTyped(event -> event.consume());
        overlay.setOnScroll(event -> event.consume());
        
        // Create alert box
        VBox alertBox = new VBox(15);
        alertBox.setAlignment(Pos.CENTER);
        alertBox.setPrefWidth(400);
        alertBox.setPrefHeight(200);
        alertBox.setMaxWidth(400);
        alertBox.setMaxHeight(200);
        alertBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);"
        );
        alertBox.setPadding(new Insets(20));
        
        // Title label
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #d32f2f;"
        );
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(350);
        
        // Message label
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #333333;"
        );
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(350);
        
        // OK button
        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: #1976d2;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;"
        );
        okButton.setOnAction(event -> {
            parentPane.getChildren().remove(overlay);
            if (onOkClicked != null) {
                onOkClicked.run();
            }
        });
        
        // Add hover effect to button
        okButton.setOnMouseEntered(event -> okButton.setStyle(
            "-fx-background-color: #1565c0;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;"
        ));
        okButton.setOnMouseExited(event -> okButton.setStyle(
            "-fx-background-color: #1976d2;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;"
        ));
        
        alertBox.getChildren().addAll(titleLabel, messageLabel, okButton);
        
        // Center the alert box in the overlay
        StackPane.setAlignment(alertBox, Pos.CENTER);
        overlay.getChildren().add(alertBox);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Add overlay to parent and bring to front
        parentPane.getChildren().add(overlay);
        overlay.toFront();
        
        // Debug: Print overlay information
        System.out.println("Alert overlay added - Size: " + parentWidth + "x" + parentHeight + 
                          " Position: " + overlay.getLayoutX() + "," + overlay.getLayoutY() +
                          " Parent size: " + parentPane.getWidth() + "x" + parentPane.getHeight());
        
        fadeIn.play();
    }
    
    /**
     * Creates a loading screen overlay with progress indicator
     */
    public static StackPane showLoadingScreen(Pane parentPane, String message) {
        // Create overlay background that fills the entire parent
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95);");
        
        // Get parent dimensions
        double parentWidth = parentPane.getWidth();
        double parentHeight = parentPane.getHeight();
        
        // If parent dimensions are not available, use bounds
        if (parentWidth <= 0 || parentHeight <= 0) {
            parentWidth = parentPane.getBoundsInLocal().getWidth();
            parentHeight = parentPane.getBoundsInLocal().getHeight();
        }
        
        // If still no dimensions, use default large size
        if (parentWidth <= 0 || parentHeight <= 0) {
            parentWidth = 1280;
            parentHeight = 738;
        }
        
        // Set absolute positioning and sizing - force the overlay to be exactly the parent size
        overlay.setLayoutX(0);
        overlay.setLayoutY(0);
        overlay.resize(parentWidth, parentHeight);
        overlay.setPrefSize(parentWidth, parentHeight);
        overlay.setMinSize(parentWidth, parentHeight);
        overlay.setMaxSize(parentWidth, parentHeight);
        overlay.setPickOnBounds(true);
        overlay.setMouseTransparent(false);
        
        // Make sure it consumes ALL mouse and key events to block interaction
        overlay.setOnMouseClicked(event -> event.consume());
        overlay.setOnMousePressed(event -> event.consume());
        overlay.setOnMouseReleased(event -> event.consume());
        overlay.setOnMouseDragged(event -> event.consume());
        overlay.setOnMouseMoved(event -> event.consume());
        overlay.setOnKeyPressed(event -> event.consume());
        overlay.setOnKeyReleased(event -> event.consume());
        overlay.setOnKeyTyped(event -> event.consume());
        overlay.setOnScroll(event -> event.consume());
        
        // Apply blur effect to parent content
        BoxBlur blur = new BoxBlur(5, 5, 3);
        for (Node child : parentPane.getChildren()) {
            if (child != overlay) {
                child.setEffect(blur);
            }
        }
        
        // Create loading box
        VBox loadingBox = new VBox(20);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPrefWidth(320);
        loadingBox.setPrefHeight(200);
        loadingBox.setMaxWidth(320);
        loadingBox.setMaxHeight(200);
        loadingBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 3);"
        );
        loadingBox.setPadding(new Insets(30));
        
        // Progress indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(60, 60);
        progressIndicator.setStyle(
            "-fx-accent: #1976d2;" +
            "-fx-progress-color: #1976d2;"
        );
        
        // Loading message
        Label loadingLabel = new Label(message);
        loadingLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #333333;"
        );
        loadingLabel.setAlignment(Pos.CENTER);
        loadingLabel.setWrapText(true);
        loadingLabel.setMaxWidth(250);
        
        // Subtitle message
        Label subtitleLabel = new Label("Please wait...");
        subtitleLabel.setWrapText(true);
        subtitleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #666666;"
        );
        subtitleLabel.setAlignment(Pos.CENTER);
        subtitleLabel.setMaxWidth(250);

        Label subtitleLabel_2 = new Label("Our  🤖  is preparing your personalized meal plan");
        subtitleLabel_2.setWrapText(true);
        subtitleLabel_2.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #666666;"
        );
        subtitleLabel_2.setAlignment(Pos.CENTER);
        subtitleLabel_2.setMaxWidth(250);

        loadingBox.getChildren().addAll(progressIndicator, loadingLabel, subtitleLabel, subtitleLabel_2);

        // Center the loading box in the overlay
        StackPane.setAlignment(loadingBox, Pos.CENTER);
        overlay.getChildren().add(loadingBox);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), overlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Add overlay to parent and bring to front
        parentPane.getChildren().add(overlay);
        overlay.toFront();
        
        // Debug: Print overlay information
        System.out.println("Loading overlay added - Size: " + parentWidth + "x" + parentHeight + 
                          " Position: " + overlay.getLayoutX() + "," + overlay.getLayoutY() +
                          " Parent size: " + parentPane.getWidth() + "x" + parentPane.getHeight());
        
        fadeIn.play();
        
        return overlay;
    }
    
    /**
     * Creates an in-page confirmation dialog with Yes/No buttons
     */
    public static void showConfirmationDialog(Pane parentPane, String title, String message, 
                                              Runnable onYesClicked, Runnable onNoClicked) {
        // Create overlay background that fills the entire parent
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        
        // Get parent dimensions
        double parentWidth = parentPane.getWidth();
        double parentHeight = parentPane.getHeight();
        
        // If parent dimensions are not available, use bounds
        if (parentWidth <= 0 || parentHeight <= 0) {
            parentWidth = parentPane.getBoundsInLocal().getWidth();
            parentHeight = parentPane.getBoundsInLocal().getHeight();
        }
        
        // If still no dimensions, use default large size
        if (parentWidth <= 0 || parentHeight <= 0) {
            parentWidth = 1280;
            parentHeight = 738;
        }
        
        // Set absolute positioning and sizing
        overlay.setLayoutX(0);
        overlay.setLayoutY(0);
        overlay.resize(parentWidth, parentHeight);
        overlay.setPrefSize(parentWidth, parentHeight);
        overlay.setMinSize(parentWidth, parentHeight);
        overlay.setMaxSize(parentWidth, parentHeight);
        overlay.setPickOnBounds(true);
        overlay.setMouseTransparent(false);
        
        // Consume ALL mouse and key events to block interaction with underlying content
        overlay.setOnMouseClicked(event -> event.consume());
        overlay.setOnMousePressed(event -> event.consume());
        overlay.setOnMouseReleased(event -> event.consume());
        overlay.setOnMouseDragged(event -> event.consume());
        overlay.setOnMouseMoved(event -> event.consume());
        overlay.setOnKeyPressed(event -> event.consume());
        overlay.setOnKeyReleased(event -> event.consume());
        overlay.setOnKeyTyped(event -> event.consume());
        overlay.setOnScroll(event -> event.consume());
        
        // Create confirmation dialog box
        VBox dialogBox = new VBox(20);
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setPrefWidth(450);
        dialogBox.setPrefHeight(250);
        dialogBox.setMaxWidth(450);
        dialogBox.setMaxHeight(250);
        dialogBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);"
        );
        dialogBox.setPadding(new Insets(20));
        
        // Title label
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #ff9800;"
        );
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(400);
        
        // Message label
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #333333;"
        );
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(400);
        
        // Button container
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        // Yes button
        Button yesButton = new Button("Yes");
        yesButton.setStyle(
            "-fx-background-color: #4caf50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 5;"
        );
        yesButton.setOnAction(event -> {
            parentPane.getChildren().remove(overlay);
            if (onYesClicked != null) {
                onYesClicked.run();
            }
        });
        
        // No button
        Button noButton = new Button("No");
        noButton.setStyle(
            "-fx-background-color: #f44336;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 5;"
        );
        noButton.setOnAction(event -> {
            parentPane.getChildren().remove(overlay);
            if (onNoClicked != null) {
                onNoClicked.run();
            }
        });
        
        // Add hover effects
        yesButton.setOnMouseEntered(event -> yesButton.setStyle(
            "-fx-background-color: #45a049;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 5;"
        ));
        yesButton.setOnMouseExited(event -> yesButton.setStyle(
            "-fx-background-color: #4caf50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 5;"
        ));
        
        noButton.setOnMouseEntered(event -> noButton.setStyle(
            "-fx-background-color: #da190b;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 5;"
        ));
        noButton.setOnMouseExited(event -> noButton.setStyle(
            "-fx-background-color: #f44336;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 5;"
        ));
        
        buttonBox.getChildren().addAll(yesButton, noButton);
        dialogBox.getChildren().addAll(titleLabel, messageLabel, buttonBox);
        
        // Center the dialog box in the overlay
        StackPane.setAlignment(dialogBox, Pos.CENTER);
        overlay.getChildren().add(dialogBox);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Add overlay to parent and bring to front
        parentPane.getChildren().add(overlay);
        overlay.toFront();
        
        fadeIn.play();
    }
    
    /**
     * Removes the loading screen and restores the parent content
     */
    public static void hideLoadingScreen(Pane parentPane, StackPane loadingOverlay) {
        if (loadingOverlay != null && parentPane.getChildren().contains(loadingOverlay)) {
            // Remove blur effect from parent content
            for (Node child : parentPane.getChildren()) {
                if (child != loadingOverlay) {
                    child.setEffect(null);
                }
            }
            
            // Fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), loadingOverlay);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> parentPane.getChildren().remove(loadingOverlay));
            fadeOut.play();
        }
    }
    
    /**
     * Creates a success notification overlay with green styling
     */
    public static void showSuccessNotification(Pane parentPane, String title, String message, Runnable onOkClicked) {
        // Create overlay background that fills the entire parent
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        
        // Get parent dimensions
        double parentWidth = parentPane.getWidth();
        double parentHeight = parentPane.getHeight();
        
        // If parent dimensions are not available, use bounds
        if (parentWidth <= 0 || parentHeight <= 0) {
            parentWidth = parentPane.getBoundsInLocal().getWidth();
            parentHeight = parentPane.getBoundsInLocal().getHeight();
        }
        
        // If still no dimensions, use default large size
        if (parentWidth <= 0 || parentHeight <= 0) {
            parentWidth = 1280;
            parentHeight = 738;
        }
        
        // Set absolute positioning and sizing - force the overlay to be exactly the parent size
        overlay.setLayoutX(0);
        overlay.setLayoutY(0);
        overlay.resize(parentWidth, parentHeight);
        overlay.setPrefSize(parentWidth, parentHeight);
        overlay.setMinSize(parentWidth, parentHeight);
        overlay.setMaxSize(parentWidth, parentHeight);
        overlay.setPickOnBounds(true);
        overlay.setMouseTransparent(false);
        
        // Consume ALL mouse and key events to block interaction with underlying content
        overlay.setOnMouseClicked(event -> event.consume());
        overlay.setOnMousePressed(event -> event.consume());
        overlay.setOnMouseReleased(event -> event.consume());
        overlay.setOnMouseDragged(event -> event.consume());
        overlay.setOnMouseMoved(event -> event.consume());
        overlay.setOnKeyPressed(event -> event.consume());
        overlay.setOnKeyReleased(event -> event.consume());
        overlay.setOnKeyTyped(event -> event.consume());
        overlay.setOnScroll(event -> event.consume());
        
        // Create success notification box
        VBox notificationBox = new VBox(15);
        notificationBox.setAlignment(Pos.CENTER);
        notificationBox.setPrefWidth(400);
        notificationBox.setPrefHeight(200);
        notificationBox.setMaxWidth(400);
        notificationBox.setMaxHeight(200);
        notificationBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #4caf50;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);"
        );
        notificationBox.setPadding(new Insets(20));
        
        // Success icon and title label
        Label titleLabel = new Label("✅ " + title);
        titleLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2e7d32;"
        );
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(350);
        
        // Message label
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #333333;"
        );
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(350);
        
        // OK button with green styling
        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: #2e7d32;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;"
        );
        okButton.setOnAction(_ -> {
            parentPane.getChildren().remove(overlay);
            if (onOkClicked != null) {
                onOkClicked.run();
            }
        });
        
        // Add hover effect to button
        okButton.setOnMouseEntered(_ -> okButton.setStyle(
            "-fx-background-color: #1b5e20;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;"
        ));
        okButton.setOnMouseExited(_ -> okButton.setStyle(
            "-fx-background-color: #2e7d32;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;"
        ));
        
        notificationBox.getChildren().addAll(titleLabel, messageLabel, okButton);
        
        // Center the notification box in the overlay
        StackPane.setAlignment(notificationBox, Pos.CENTER);
        overlay.getChildren().add(notificationBox);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Add overlay to parent and bring to front
        parentPane.getChildren().add(overlay);
        overlay.toFront();
        
        // Debug: Print overlay information
        System.out.println("Success notification overlay added - Size: " + parentWidth + "x" + parentHeight + 
                          " Position: " + overlay.getLayoutX() + "," + overlay.getLayoutY() +
                          " Parent size: " + parentPane.getWidth() + "x" + parentPane.getHeight());
        
        fadeIn.play();
    }

    /**
     * Gets the root pane from any node in the scene graph
     */
    public static Pane getRootPane(Node node) {
        Node current = node;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        if (current instanceof Pane) {
            return (Pane) current;
        }
        return null;
    }
}
