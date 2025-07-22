package org.example.meal_planner;

import java.io.IOException;

import org.example.meal_planner.controllers.AppController;

import javafx.application.Application;
import javafx.stage.Stage;


/**
 *  DailyDish Group Project
 *   
 *   Group Members:
 * - Abdullah Al-Hamidi / 24523229
 * - Mohammad Aateef / 24523270
 * - La Ode Julfikar / 24523077
 * - Farrell Abhivandya Mecca / 24523039
 * - Eza Herda Herdiana / 24523187
 */

public class Main extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    AppController appController = new AppController(stage);
    stage.setTitle("DailyDish");
    appController.startApplication();
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}