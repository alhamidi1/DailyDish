module org.example.meal_planner {
  requires javafx.controls;
  requires javafx.fxml;
  requires google.genai;
  requires com.google.gson;
  requires com.google.common;
  requires com.google.api.client;
  requires com.fasterxml.jackson.databind;
  requires xstream;
  requires java.annotation;
  requires java.xml;

  exports org.example.meal_planner;
  exports org.example.meal_planner.controllers;
  exports org.example.meal_planner.controllers.Profile_Setup;
  exports org.example.meal_planner.models;
  exports org.example.meal_planner.services;
  exports org.example.meal_planner.controllers.Dashboard;

  opens org.example.meal_planner to javafx.fxml;
  opens org.example.meal_planner.controllers to javafx.fxml;
  opens org.example.meal_planner.services to javafx.fxml;
  opens org.example.meal_planner.models to javafx.fxml, xstream;
  opens org.example.meal_planner.controllers.Profile_Setup to javafx.fxml, xstream;
  opens org.example.meal_planner.controllers.Dashboard to javafx.fxml;
}