package org.example.meal_planner.services;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.example.meal_planner.models.DayMealPlan;
import org.example.meal_planner.models.Food;
import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.Meal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MealPlanXmlParser {

    public static DayMealPlan parseMealPlan(String filePath) {
        DayMealPlan dayMealPlan = new DayMealPlan();

        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            dayMealPlan.setBreakfast(getMeal(doc, "breakfast"));
            dayMealPlan.setLunch(getMeal(doc, "lunch"));
            dayMealPlan.setDinner(getMeal(doc, "dinner"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dayMealPlan;
    }

    private static Meal getMeal(Document doc, String mealType) {
        Meal meal = new Meal(mealType);
        NodeList mealNodes = doc.getElementsByTagName(mealType);
        if (mealNodes.getLength() > 0) {
            Node mealNode = mealNodes.item(0);
            if (mealNode.getNodeType() == Node.ELEMENT_NODE) {
                Element mealElement = (Element) mealNode;
                NodeList foodNodes = mealElement.getElementsByTagName("food");
                for (int i = 0; i < foodNodes.getLength(); i++) {
                    Node foodNode = foodNodes.item(i);
                    if (foodNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element foodElement = (Element) foodNode;
                        Food food = new Food();
                        food.setName(foodElement.getElementsByTagName("name").item(0).getTextContent());
                        food.setFoodCalories(Double.parseDouble(foodElement.getElementsByTagName("foodCalories").item(0).getTextContent()));
                        food.setFoodCarbsG(Double.parseDouble(foodElement.getElementsByTagName("foodCarbsG").item(0).getTextContent()));
                        food.setFoodFatG(Double.parseDouble(foodElement.getElementsByTagName("foodFatG").item(0).getTextContent()));
                        food.setFoodProteinG(Double.parseDouble(foodElement.getElementsByTagName("foodProteinG").item(0).getTextContent()));
                        
                        // Parse ingredients if they exist
                        NodeList ingredientsNodes = foodElement.getElementsByTagName("ingredients");
                        if (ingredientsNodes.getLength() > 0) {
                            Element ingredientsElement = (Element) ingredientsNodes.item(0);
                            NodeList ingredientNodes = ingredientsElement.getElementsByTagName("ingredient");
                            java.util.List<Ingredient> ingredients = new java.util.ArrayList<>();
                            
                            for (int j = 0; j < ingredientNodes.getLength(); j++) {
                                Node ingredientNode = ingredientNodes.item(j);
                                if (ingredientNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element ingredientElement = (Element) ingredientNode;
                                    String name = ingredientElement.getElementsByTagName("name").item(0).getTextContent();
                                    double amount = Double.parseDouble(ingredientElement.getElementsByTagName("amount").item(0).getTextContent());
                                    String unit = ingredientElement.getElementsByTagName("unit").item(0).getTextContent();
                                    ingredients.add(new Ingredient(name, amount, unit));
                                }
                            }
                            food.setIngredients(ingredients);
                        }
                        
                        meal.addFood(food);
                    }
                }
            }
        }
        return meal;
    }

    public static void saveMealPlan(String filePath, DayMealPlan dayMealPlan) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            
            // Root element
            Element rootElement = doc.createElement("dayMealPlan");
            doc.appendChild(rootElement);
            
            // Add breakfast
            if (dayMealPlan.getBreakfast() != null) {
                rootElement.appendChild(createMealElement(doc, dayMealPlan.getBreakfast()));
            }
            
            // Add lunch
            if (dayMealPlan.getLunch() != null) {
                rootElement.appendChild(createMealElement(doc, dayMealPlan.getLunch()));
            }
            
            // Add dinner
            if (dayMealPlan.getDinner() != null) {
                rootElement.appendChild(createMealElement(doc, dayMealPlan.getDinner()));
            }
            
            // Write to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Element createMealElement(Document doc, Meal meal) {
        Element mealElement = doc.createElement(meal.getMealType());
        
        // Add meal type
        Element mealTypeElement = doc.createElement("mealType");
        mealTypeElement.appendChild(doc.createTextNode(meal.getMealType()));
        mealElement.appendChild(mealTypeElement);
        
        // Add foods
        Element foodsElement = doc.createElement("foods");
        mealElement.appendChild(foodsElement);
        
        for (Food food : meal.getFoods()) {
            Element foodElement = doc.createElement("food");
            
            Element nameElement = doc.createElement("name");
            nameElement.appendChild(doc.createTextNode(food.getName()));
            foodElement.appendChild(nameElement);
            
            Element caloriesElement = doc.createElement("foodCalories");
            caloriesElement.appendChild(doc.createTextNode(String.valueOf(food.getFoodCalories())));
            foodElement.appendChild(caloriesElement);
            
            Element carbsElement = doc.createElement("foodCarbsG");
            carbsElement.appendChild(doc.createTextNode(String.valueOf(food.getFoodCarbsG())));
            foodElement.appendChild(carbsElement);
            
            Element fatElement = doc.createElement("foodFatG");
            fatElement.appendChild(doc.createTextNode(String.valueOf(food.getFoodFatG())));
            foodElement.appendChild(fatElement);
            
            Element proteinElement = doc.createElement("foodProteinG");
            proteinElement.appendChild(doc.createTextNode(String.valueOf(food.getFoodProteinG())));
            foodElement.appendChild(proteinElement);
            
            // Add ingredients if they exist
            if (food.getIngredients() != null && !food.getIngredients().isEmpty()) {
                Element ingredientsElement = doc.createElement("ingredients");
                
                for (Ingredient ingredient : food.getIngredients()) {
                    Element ingredientElement = doc.createElement("ingredient");
                    
                    Element ingredientNameElement = doc.createElement("name");
                    ingredientNameElement.appendChild(doc.createTextNode(ingredient.getName()));
                    ingredientElement.appendChild(ingredientNameElement);
                    
                    Element ingredientAmountElement = doc.createElement("amount");
                    ingredientAmountElement.appendChild(doc.createTextNode(String.valueOf(ingredient.getAmount())));
                    ingredientElement.appendChild(ingredientAmountElement);
                    
                    Element ingredientUnitElement = doc.createElement("unit");
                    ingredientUnitElement.appendChild(doc.createTextNode(ingredient.getUnit()));
                    ingredientElement.appendChild(ingredientUnitElement);
                    
                    ingredientsElement.appendChild(ingredientElement);
                }
                
                foodElement.appendChild(ingredientsElement);
            }
            
            foodsElement.appendChild(foodElement);
        }
        
        return mealElement;
    }
}
