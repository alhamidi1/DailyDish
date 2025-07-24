package org.example.meal_planner.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.example.meal_planner.models.ProfileSetupData;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlDataService {

  public static <T> void save(T objectToSave, String filePath, Class<?>... classesToAlias) {
    XStream xstream = new XStream(new DomDriver());

    // Create a clean alias for the main object
    xstream.alias(getAliasName(objectToSave.getClass()), objectToSave.getClass());

    // Create aliases for any other specified classes
    if (classesToAlias != null) {
      for (Class<?> clazz : classesToAlias) {
        xstream.alias(getAliasName(clazz), clazz);
      }
    }

    String xml = xstream.toXML(objectToSave);

    try (FileWriter writer = new FileWriter(filePath)) {
      writer.write(xml);
      System.out.println("Successfully saved data to: " + filePath);
    } catch (IOException e) {
      System.err.println("Error saving XML file: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static String getAliasName(Class<?> clazz) {
    String simpleName = clazz.getSimpleName();
    return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
  }

  public static <T> T load(String filePath, Class<T> targetClass, Class<?>... classesToAlias) {
    try {
      File file = new File(filePath);
      if (!file.exists()) {
        return null;
      }

      XStream xstream = new XStream(new DomDriver());
      
      // Add permission for the target class and any additional classes
      xstream.allowTypes(new Class[]{targetClass});
      if (classesToAlias != null) {
        xstream.allowTypes(classesToAlias);
      }
      
      // Add permission for common Java types
      xstream.allowTypesByWildcard(new String[]{
        "java.lang.**",
        "java.util.**",
        "java.time.**"
      });

      // Create a clean alias for the main object
      xstream.alias(getAliasName(targetClass), targetClass);

      // Create aliases for any other specified classes
      if (classesToAlias != null) {
        for (Class<?> clazz : classesToAlias) {
          xstream.alias(getAliasName(clazz), clazz);
        }
      }

      // Add field aliases for backward compatibility
      addBackwardCompatibilityAliases(xstream);

      return (T) xstream.fromXML(file);
    } catch (Exception e) {
      System.err.println("Error loading XML file: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

  public static ProfileSetupData loadProfileData() {
    return load("data/user_profile.xml", ProfileSetupData.class);
  }

  public static void saveProfileData(ProfileSetupData profileData) {
    save(profileData, "data/user_profile.xml");
  }

  private static void addBackwardCompatibilityAliases(XStream xstream) {
    // Handle field name changes for Food class
    xstream.aliasField("name", org.example.meal_planner.models.Food.class, "name");
    xstream.aliasField("n", org.example.meal_planner.models.Food.class, "name");
    
    // Handle field name changes for Ingredient class  
    xstream.aliasField("name", org.example.meal_planner.models.Ingredient.class, "name");
    xstream.aliasField("n", org.example.meal_planner.models.Ingredient.class, "name");
    
    // Ignore the foodCalories field that doesn't exist in the current model
    xstream.omitField(org.example.meal_planner.models.Food.class, "foodCalories");
  }
}