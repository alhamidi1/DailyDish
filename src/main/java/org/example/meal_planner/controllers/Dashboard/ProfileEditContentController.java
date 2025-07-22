package org.example.meal_planner.controllers.Dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.example.meal_planner.controllers.Profile_Setup.ProfileSetupData;
import org.example.meal_planner.services.XmlDataService;
import org.example.meal_planner.utils.ModalUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Content Controller for the Profile Edit page that works with NavigationUtils.
 * Provides profile editing functionality without navigation elements.
 */
public class ProfileEditContentController {

    @FXML private ComboBox<String> sexComboBox;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> bodyFatComboBox;
    @FXML private ComboBox<String> activityLevelComboBox;
    @FXML private ComboBox<String> dietTypeComboBox;
    @FXML private TextArea foodsToAvoidTextArea;
    @FXML private ComboBox<String> goalTypeComboBox;
    @FXML private ComboBox<String> generalGoalComboBox;
    @FXML private TextField exactWeightGoalField;
    @FXML private TextField exactWeightChangeRateField;
    @FXML private VBox exactGoalFields;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private ProfileSetupData currentProfile;

    @FXML
    public void initialize() {
        setupComboBoxes();
        loadCurrentProfile();
        setupEventHandlers();
    }

    private void setupComboBoxes() {
        // Sex options
        sexComboBox.getItems().addAll("Male", "Female");
        sexComboBox.setPromptText("Select sex");

        // Body fat level options
        bodyFatComboBox.getItems().addAll(
                "Low (6-13%)", 
                "Medium (14-17%)", 
                "High (18-24%)", 
                "Very High (25%+)"
        );
        bodyFatComboBox.setPromptText("Select body fat level");

        // Activity level options
        activityLevelComboBox.getItems().addAll(
                "Sedentary (no exercise)",
                "Lightly active (light exercise 1-3 days/week)",
                "Moderately active (moderate exercise 3-5 days/week)",
                "Very active (hard exercise 6-7 days/week)",
                "Active daily, frequent exercise"
        );
        activityLevelComboBox.setPromptText("Select activity level");

        // Diet type options
        dietTypeComboBox.getItems().addAll(
                "Anything",
                "Vegetarian",
                "Vegan",
                "Ketogenic",
                "Mediterranean",
                "Paleo",
                "Low-Carb",
                "High-Protein"
        );
        dietTypeComboBox.setPromptText("Select diet type");

        // Goal type options
        goalTypeComboBox.getItems().addAll("General Goal", "Exact Goal");
        goalTypeComboBox.setPromptText("Select goal type");

        // General goal options
        generalGoalComboBox.getItems().addAll(
                "Lose fat",
                "Maintain",
                "Build muscle",
                "Gain weight"
        );
        generalGoalComboBox.setPromptText("Select general goal");
    }

    private void loadCurrentProfile() {
        try {
            currentProfile = XmlDataService.loadProfileData();
            if (currentProfile != null) {
                // Load basic info
                sexComboBox.setValue(currentProfile.getSex());
                heightField.setText(currentProfile.getHeight() != null ? currentProfile.getHeight().toString() : "");
                weightField.setText(currentProfile.getWeight() != null ? currentProfile.getWeight().toString() : "");
                ageField.setText(currentProfile.getAge() != null ? currentProfile.getAge().toString() : "");
                bodyFatComboBox.setValue(currentProfile.getBodyfatLevel());
                activityLevelComboBox.setValue(currentProfile.getActivityLevel());
                dietTypeComboBox.setValue(currentProfile.getDietType());

                // Load foods to avoid
                if (currentProfile.getFoodsToAvoid() != null) {
                    String foodsText = String.join(", ", currentProfile.getFoodsToAvoid());
                    foodsToAvoidTextArea.setText(foodsText);
                }

                // Load goals
                goalTypeComboBox.setValue(currentProfile.getGoalType());
                generalGoalComboBox.setValue(currentProfile.getGeneralGoalAction());
                
                if (currentProfile.getExactWeightGoal() != null) {
                    exactWeightGoalField.setText(currentProfile.getExactWeightGoal().toString());
                }
                if (currentProfile.getExactWeightChangeRate() != null) {
                    exactWeightChangeRateField.setText(currentProfile.getExactWeightChangeRate().toString());
                }

                // Show exact goal fields if needed
                toggleExactGoalFields();
            }
        } catch (Exception e) {
            System.err.println("Error loading profile data: " + e.getMessage());
            showStatus("Error loading profile data", true);
        }
    }

    private void setupEventHandlers() {
        // Goal type change handler
        goalTypeComboBox.setOnAction(_ -> toggleExactGoalFields());
    }

    private void toggleExactGoalFields() {
        boolean showExactFields = "Exact Goal".equals(goalTypeComboBox.getValue());
        if (exactGoalFields != null) {
            exactGoalFields.setVisible(showExactFields);
            exactGoalFields.setManaged(showExactFields);
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }

            // Create updated profile data
            ProfileSetupData updatedProfile = new ProfileSetupData();
            
            // Set basic info
            updatedProfile.setSex(sexComboBox.getValue());
            updatedProfile.setHeight(Integer.parseInt(heightField.getText()));
            updatedProfile.setWeight(Integer.parseInt(weightField.getText()));
            updatedProfile.setAge(Integer.parseInt(ageField.getText()));
            updatedProfile.setBodyfatLevel(bodyFatComboBox.getValue());
            updatedProfile.setActivityLevel(activityLevelComboBox.getValue());
            updatedProfile.setDietType(dietTypeComboBox.getValue());

            // Set foods to avoid
            String foodsText = foodsToAvoidTextArea.getText();
            if (foodsText != null && !foodsText.trim().isEmpty()) {
                try {
                    // Split by comma and trim each item
                    List<String> foodsList = Arrays.asList(foodsText.split("\\s*,\\s*"));
                    // Remove any empty strings
                    foodsList = foodsList.stream()
                            .filter(s -> s != null && !s.trim().isEmpty())
                            .collect(java.util.stream.Collectors.toList());
                    updatedProfile.setFoodsToAvoid(foodsList);
                } catch (Exception e) {
                    System.err.println("Error processing foods to avoid: " + e.getMessage());
                    updatedProfile.setFoodsToAvoid(new ArrayList<>());
                }
            } else {
                updatedProfile.setFoodsToAvoid(new ArrayList<>());
            }

            // Set goals
            updatedProfile.setGoalType(goalTypeComboBox.getValue());
            updatedProfile.setGeneralGoalAction(generalGoalComboBox.getValue());
            
            if ("Exact Goal".equals(goalTypeComboBox.getValue())) {
                if (!exactWeightGoalField.getText().isEmpty()) {
                    updatedProfile.setExactWeightGoal(Double.parseDouble(exactWeightGoalField.getText()));
                }
                if (!exactWeightChangeRateField.getText().isEmpty()) {
                    updatedProfile.setExactWeightChangeRate(Double.parseDouble(exactWeightChangeRateField.getText()));
                }
            }

            // Save to XML
            XmlDataService.saveProfileData(updatedProfile);

            // Show success notification using ModalUtils
            Pane rootPane = ModalUtils.getRootPane(saveButton);
            if (rootPane != null) {
                ModalUtils.showSuccessNotification(
                    rootPane,
                    "Profile Saved Successfully!",
                    "Your profile information has been updated and saved.",
                    null
                );
            } else {
                // Fallback to status label if root pane not found
                showStatus("Profile saved successfully! ✅", false);
            }
            
            currentProfile = updatedProfile;

        } catch (NumberFormatException e) {
            showStatus("Please enter valid numbers for age, height, weight, and goal values.", true);
        } catch (Exception e) {
            System.err.println("Error saving profile: " + e.getMessage());
            showStatus("Error saving profile: " + e.getMessage(), true);
        }
    }

    private boolean validateInputs() {
        // Check required fields
        if (sexComboBox.getValue() == null || sexComboBox.getValue().isEmpty()) {
            showStatus("Please select your sex.", true);
            return false;
        }

        if (heightField.getText().isEmpty()) {
            showStatus("Please enter your height.", true);
            return false;
        }

        if (weightField.getText().isEmpty()) {
            showStatus("Please enter your weight.", true);
            return false;
        }

        if (ageField.getText().isEmpty()) {
            showStatus("Please enter your age.", true);
            return false;
        }

        if (bodyFatComboBox.getValue() == null || bodyFatComboBox.getValue().isEmpty()) {
            showStatus("Please select your body fat level.", true);
            return false;
        }

        if (activityLevelComboBox.getValue() == null || activityLevelComboBox.getValue().isEmpty()) {
            showStatus("Please select your activity level.", true);
            return false;
        }

        if (dietTypeComboBox.getValue() == null || dietTypeComboBox.getValue().isEmpty()) {
            showStatus("Please select your diet type.", true);
            return false;
        }

        if (goalTypeComboBox.getValue() == null || goalTypeComboBox.getValue().isEmpty()) {
            showStatus("Please select your goal type.", true);
            return false;
        }

        if (generalGoalComboBox.getValue() == null || generalGoalComboBox.getValue().isEmpty()) {
            showStatus("Please select your general goal.", true);
            return false;
        }

        // Validate numeric inputs
        try {
            int height = Integer.parseInt(heightField.getText());
            if (height <= 0 || height > 300) {
                showStatus("Please enter a valid height (1-300 cm).", true);
                return false;
            }

            int weight = Integer.parseInt(weightField.getText());
            if (weight <= 0 || weight > 500) {
                showStatus("Please enter a valid weight (1-500 kg).", true);
                return false;
            }

            int age = Integer.parseInt(ageField.getText());
            if (age <= 0 || age > 150) {
                showStatus("Please enter a valid age (1-150 years).", true);
                return false;
            }

            // Validate exact goal fields if visible
            if ("Exact Goal".equals(goalTypeComboBox.getValue())) {
                if (!exactWeightGoalField.getText().isEmpty()) {
                    double weightGoal = Double.parseDouble(exactWeightGoalField.getText());
                    if (weightGoal <= 0 || weightGoal > 500) {
                        showStatus("Please enter a valid target weight (1-500 kg).", true);
                        return false;
                    }
                }

                if (!exactWeightChangeRateField.getText().isEmpty()) {
                    double changeRate = Double.parseDouble(exactWeightChangeRateField.getText());
                    if (changeRate <= 0 || changeRate > 5) {
                        showStatus("Please enter a valid weight change rate (0.1-5 kg/week).", true);
                        return false;
                    }
                }
            }

        } catch (NumberFormatException e) {
            showStatus("Please enter valid numbers for numeric fields.", true);
            return false;
        }

        return true;
    }

    private void showStatus(String message, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(isError ? 
                "-fx-text-fill: #F44336; -fx-background-color: #FFEBEE; -fx-padding: 10;" :
                "-fx-text-fill: #4CAF50; -fx-background-color: #E8F5E9; -fx-padding: 10;"
            );
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);

            // Auto-hide success messages after 3 seconds
            if (!isError) {
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(3),
                        _ -> {
                            statusLabel.setVisible(false);
                            statusLabel.setManaged(false);
                        }
                    )
                );
                timeline.play();
            }
        }
    }
}
