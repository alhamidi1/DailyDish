package org.example.meal_planner.controllers.Profile_Setup;

import java.util.List;

public class ProfileSetupData {

    // Data from DietType.fxml
    private String dietType;

    // Data from FoodAvoidance.fxml
    private List<String> foodsToAvoid;

    // Data from UserDetails.fxml
    private String sex;
    private Integer height;
    private Integer weight;
    private Integer age;
    private String bodyfatLevel;
    private String activityLevel;

    // Data from UserGoals.fxml
    private String goalType; // "General Goal" or "Exact Goal"
    private String generalGoalAction; // "Lose fat", "Maintain weight", etc.
    private Double exactWeightGoal;
    private Double exactWeightChangeRate;

    // --- Getters and Setters for all fields ---
    // (You can generate these automatically in your IDE)

    public String getDietType() {
        return dietType;
    }
    public void setDietType(String dietType) {
        this.dietType = dietType;
    }

    public List<String> getFoodsToAvoid() {
        return foodsToAvoid;
    }
    public void setFoodsToAvoid(List<String> foodsToAvoid) {
        this.foodsToAvoid = foodsToAvoid;
    }

    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBodyfatLevel() {
        return bodyfatLevel;
    }
    public void setBodyfatLevel(String bodyfatLevel) {
        this.bodyfatLevel = bodyfatLevel;
    }

    public String getActivityLevel() {
        return activityLevel;
    }
    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getGoalType() {
        return goalType;
    }
    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public String getGeneralGoalAction() {
        return generalGoalAction;
    }
    public void setGeneralGoalAction(String generalGoalAction) {
        this.generalGoalAction = generalGoalAction;
    }

    public Double getExactWeightGoal() {
        return exactWeightGoal;
    }
    public void setExactWeightGoal(Double exactWeightGoal) {
        this.exactWeightGoal = exactWeightGoal;
    }

    public Double getExactWeightChangeRate() {
        return exactWeightChangeRate;
    }
    public void setExactWeightChangeRate(Double exactWeightChangeRate) {
        this.exactWeightChangeRate = exactWeightChangeRate;
    }
}