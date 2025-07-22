package org.example.meal_planner.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.example.meal_planner.models.Ingredient;
import org.example.meal_planner.models.MealTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MealTemplateService {
    private static final String MEAL_TEMPLATES_FILE = "data/meal_templates.xml";

    public static List<MealTemplate> loadMealTemplates() {
        List<MealTemplate> templates = new ArrayList<>();
        
        try {
            File xmlFile = new File(MEAL_TEMPLATES_FILE);
            if (!xmlFile.exists()) {
                createDefaultMealTemplates();
            }
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList mealNodes = doc.getElementsByTagName("meal");
            for (int i = 0; i < mealNodes.getLength(); i++) {
                Node mealNode = mealNodes.item(i);
                if (mealNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element mealElement = (Element) mealNode;
                    MealTemplate template = new MealTemplate();
                    
                    // Handle both old and new name formats for backward compatibility
                    NodeList nameNodes = mealElement.getElementsByTagName("name");
                    if (nameNodes.getLength() > 0) {
                        template.setName(nameNodes.item(0).getTextContent());
                    } else {
                        // Try old format with 'n' tag
                        NodeList nNodes = mealElement.getElementsByTagName("n");
                        if (nNodes.getLength() > 0) {
                            template.setName(nNodes.item(0).getTextContent());
                        }
                    }
                    
                    template.setServings(Integer.parseInt(mealElement.getElementsByTagName("servings").item(0).getTextContent()));
                    template.setCalories(Double.parseDouble(mealElement.getElementsByTagName("calories").item(0).getTextContent()));
                    template.setCarbs(Double.parseDouble(mealElement.getElementsByTagName("carbs").item(0).getTextContent()));
                    template.setFat(Double.parseDouble(mealElement.getElementsByTagName("fat").item(0).getTextContent()));
                    template.setProtein(Double.parseDouble(mealElement.getElementsByTagName("protein").item(0).getTextContent()));
                    
                    // Load ingredients if they exist
                    NodeList ingredientsNodes = mealElement.getElementsByTagName("ingredients");
                    if (ingredientsNodes.getLength() > 0) {
                        Element ingredientsElement = (Element) ingredientsNodes.item(0);
                        NodeList ingredientNodes = ingredientsElement.getElementsByTagName("ingredient");
                        List<Ingredient> ingredients = new ArrayList<>();
                        
                        for (int j = 0; j < ingredientNodes.getLength(); j++) {
                            Element ingredientElement = (Element) ingredientNodes.item(j);
                            
                            // Handle both old and new name formats for ingredient names
                            String name = "";
                            NodeList ingredientNameNodes = ingredientElement.getElementsByTagName("name");
                            if (ingredientNameNodes.getLength() > 0) {
                                name = ingredientNameNodes.item(0).getTextContent();
                            } else {
                                // Try old format with 'n' tag
                                NodeList ingredientNNodes = ingredientElement.getElementsByTagName("n");
                                if (ingredientNNodes.getLength() > 0) {
                                    name = ingredientNNodes.item(0).getTextContent();
                                }
                            }
                            
                            double amount = Double.parseDouble(ingredientElement.getElementsByTagName("amount").item(0).getTextContent());
                            String unit = ingredientElement.getElementsByTagName("unit").item(0).getTextContent();
                            ingredients.add(new Ingredient(name, amount, unit));
                        }
                        template.setIngredients(ingredients);
                    }
                    
                    templates.add(template);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return templates;
    }

    public static void saveMealTemplates(List<MealTemplate> templates) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            
            Element rootElement = doc.createElement("mealTemplates");
            doc.appendChild(rootElement);
            
            for (MealTemplate template : templates) {
                Element mealElement = doc.createElement("meal");
                
                Element nameElement = doc.createElement("name");
                nameElement.appendChild(doc.createTextNode(template.getName()));
                mealElement.appendChild(nameElement);
                
                Element servingsElement = doc.createElement("servings");
                servingsElement.appendChild(doc.createTextNode(String.valueOf(template.getServings())));
                mealElement.appendChild(servingsElement);
                
                Element caloriesElement = doc.createElement("calories");
                caloriesElement.appendChild(doc.createTextNode(String.valueOf(template.getCalories())));
                mealElement.appendChild(caloriesElement);
                
                Element carbsElement = doc.createElement("carbs");
                carbsElement.appendChild(doc.createTextNode(String.valueOf(template.getCarbs())));
                mealElement.appendChild(carbsElement);
                
                Element fatElement = doc.createElement("fat");
                fatElement.appendChild(doc.createTextNode(String.valueOf(template.getFat())));
                mealElement.appendChild(fatElement);
                
                Element proteinElement = doc.createElement("protein");
                proteinElement.appendChild(doc.createTextNode(String.valueOf(template.getProtein())));
                mealElement.appendChild(proteinElement);
                
                // Add ingredients if they exist
                if (template.getIngredients() != null && !template.getIngredients().isEmpty()) {
                    Element ingredientsElement = doc.createElement("ingredients");
                    for (Ingredient ingredient : template.getIngredients()) {
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
                    mealElement.appendChild(ingredientsElement);
                }
                
                rootElement.appendChild(mealElement);
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(MEAL_TEMPLATES_FILE));
            transformer.transform(source, result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultMealTemplates() {
        List<MealTemplate> defaultTemplates = new ArrayList<>();
        
        // 1. Nasi Gudeg (Yogyakarta Special Sweet Jackfruit Rice)
        MealTemplate gudeg = new MealTemplate("Nasi Gudeg", 2, 65.0, 12.0, 18.0);
        gudeg.getIngredients().add(new Ingredient("Young Jackfruit", 200, "g"));
        gudeg.getIngredients().add(new Ingredient("Coconut Milk", 150, "ml"));
        gudeg.getIngredients().add(new Ingredient("Palm Sugar", 30, "g"));
        gudeg.getIngredients().add(new Ingredient("Jasmine Rice", 150, "g"));
        gudeg.getIngredients().add(new Ingredient("Chicken Thigh", 100, "g"));
        defaultTemplates.add(gudeg);
        
        // 2. Rendang Daging (Spicy Beef Rendang)
        MealTemplate rendang = new MealTemplate("Rendang Daging", 4, 25.0, 35.0, 45.0);
        rendang.getIngredients().add(new Ingredient("Beef Chuck", 500, "g"));
        rendang.getIngredients().add(new Ingredient("Coconut Milk", 400, "ml"));
        rendang.getIngredients().add(new Ingredient("Shallots", 50, "g"));
        rendang.getIngredients().add(new Ingredient("Garlic", 20, "g"));
        rendang.getIngredients().add(new Ingredient("Chili Paste", 30, "g"));
        rendang.getIngredients().add(new Ingredient("Lemongrass", 2, "stalks"));
        defaultTemplates.add(rendang);
        
        // 3. Gado-Gado (Mixed Vegetable Salad with Peanut Sauce)
        MealTemplate gadogado = new MealTemplate("Gado-Gado", 2, 35.0, 18.0, 15.0);
        gadogado.getIngredients().add(new Ingredient("Mixed Vegetables", 200, "g"));
        gadogado.getIngredients().add(new Ingredient("Boiled Eggs", 2, "pieces"));
        gadogado.getIngredients().add(new Ingredient("Peanuts", 80, "g"));
        gadogado.getIngredients().add(new Ingredient("Palm Sugar", 20, "g"));
        gadogado.getIngredients().add(new Ingredient("Tamarind Water", 30, "ml"));
        gadogado.getIngredients().add(new Ingredient("Rice Cake", 100, "g"));
        defaultTemplates.add(gadogado);
        
        // 4. Soto Ayam (Indonesian Chicken Soup)
        MealTemplate sotoAyam = new MealTemplate("Soto Ayam", 3, 20.0, 8.0, 25.0);
        sotoAyam.getIngredients().add(new Ingredient("Chicken Breast", 200, "g"));
        sotoAyam.getIngredients().add(new Ingredient("Rice Vermicelli", 100, "g"));
        sotoAyam.getIngredients().add(new Ingredient("Bean Sprouts", 80, "g"));
        sotoAyam.getIngredients().add(new Ingredient("Turmeric", 5, "g"));
        sotoAyam.getIngredients().add(new Ingredient("Ginger", 15, "g"));
        sotoAyam.getIngredients().add(new Ingredient("Chicken Broth", 500, "ml"));
        defaultTemplates.add(sotoAyam);
        
        // 5. Nasi Liwet (Coconut Rice with Side Dishes)
        MealTemplate nasiLiwet = new MealTemplate("Nasi Liwet", 2, 55.0, 15.0, 20.0);
        nasiLiwet.getIngredients().add(new Ingredient("Jasmine Rice", 150, "g"));
        nasiLiwet.getIngredients().add(new Ingredient("Coconut Milk", 200, "ml"));
        nasiLiwet.getIngredients().add(new Ingredient("Fried Chicken", 120, "g"));
        nasiLiwet.getIngredients().add(new Ingredient("Tofu", 100, "g"));
        nasiLiwet.getIngredients().add(new Ingredient("Tempeh", 80, "g"));
        defaultTemplates.add(nasiLiwet);
        
        // 6. Bakso (Indonesian Meatball Soup)
        MealTemplate bakso = new MealTemplate("Bakso", 2, 30.0, 10.0, 22.0);
        bakso.getIngredients().add(new Ingredient("Beef Meatballs", 150, "g"));
        bakso.getIngredients().add(new Ingredient("Egg Noodles", 100, "g"));
        bakso.getIngredients().add(new Ingredient("Bok Choy", 80, "g"));
        bakso.getIngredients().add(new Ingredient("Beef Broth", 400, "ml"));
        bakso.getIngredients().add(new Ingredient("Fried Tofu", 50, "g"));
        defaultTemplates.add(bakso);
        
        // 7. Ayam Bakar (Grilled Chicken)
        MealTemplate ayamBakar = new MealTemplate("Ayam Bakar", 2, 8.0, 20.0, 35.0);
        ayamBakar.getIngredients().add(new Ingredient("Chicken Thigh", 250, "g"));
        ayamBakar.getIngredients().add(new Ingredient("Sweet Soy Sauce", 30, "ml"));
        ayamBakar.getIngredients().add(new Ingredient("Shallots", 25, "g"));
        ayamBakar.getIngredients().add(new Ingredient("Garlic", 15, "g"));
        ayamBakar.getIngredients().add(new Ingredient("Chili", 10, "g"));
        defaultTemplates.add(ayamBakar);
        
        // 8. Pecel Lele (Fried Catfish with Peanut Sauce)
        MealTemplate pecelLele = new MealTemplate("Pecel Lele", 1, 25.0, 22.0, 28.0);
        pecelLele.getIngredients().add(new Ingredient("Catfish", 200, "g"));
        pecelLele.getIngredients().add(new Ingredient("Peanut Sauce", 60, "g"));
        pecelLele.getIngredients().add(new Ingredient("Raw Vegetables", 100, "g"));
        pecelLele.getIngredients().add(new Ingredient("Steamed Rice", 150, "g"));
        pecelLele.getIngredients().add(new Ingredient("Cooking Oil", 15, "ml"));
        defaultTemplates.add(pecelLele);
        
        // 9. Nasi Padang (Padang Style Rice with Various Dishes)
        MealTemplate nasiPadang = new MealTemplate("Nasi Padang", 2, 50.0, 25.0, 30.0);
        nasiPadang.getIngredients().add(new Ingredient("Steamed Rice", 200, "g"));
        nasiPadang.getIngredients().add(new Ingredient("Beef Rendang", 100, "g"));
        nasiPadang.getIngredients().add(new Ingredient("Sambal Lado", 30, "g"));
        nasiPadang.getIngredients().add(new Ingredient("Sayur Nangka", 80, "g"));
        nasiPadang.getIngredients().add(new Ingredient("Fried Fish", 80, "g"));
        defaultTemplates.add(nasiPadang);
        
        // 10. Mie Ayam (Chicken Noodles)
        MealTemplate mieAyam = new MealTemplate("Mie Ayam", 1, 45.0, 12.0, 20.0);
        mieAyam.getIngredients().add(new Ingredient("Egg Noodles", 100, "g"));
        mieAyam.getIngredients().add(new Ingredient("Chicken Meat", 80, "g"));
        mieAyam.getIngredients().add(new Ingredient("Chicken Broth", 200, "ml"));
        mieAyam.getIngredients().add(new Ingredient("Green Vegetables", 50, "g"));
        mieAyam.getIngredients().add(new Ingredient("Soy Sauce", 15, "ml"));
        defaultTemplates.add(mieAyam);
        
        // 11. Ikan Bakar (Grilled Fish)
        MealTemplate ikanBakar = new MealTemplate("Ikan Bakar", 2, 5.0, 15.0, 40.0);
        ikanBakar.getIngredients().add(new Ingredient("Snapper Fish", 300, "g"));
        ikanBakar.getIngredients().add(new Ingredient("Tamarind Paste", 20, "g"));
        ikanBakar.getIngredients().add(new Ingredient("Chili Paste", 25, "g"));
        ikanBakar.getIngredients().add(new Ingredient("Palm Sugar", 15, "g"));
        ikanBakar.getIngredients().add(new Ingredient("Banana Leaves", 2, "pieces"));
        defaultTemplates.add(ikanBakar);
        
        // 12. Rawon (East Javanese Black Beef Soup)
        MealTemplate rawon = new MealTemplate("Rawon", 3, 15.0, 18.0, 32.0);
        rawon.getIngredients().add(new Ingredient("Beef Brisket", 300, "g"));
        rawon.getIngredients().add(new Ingredient("Keluak Nuts", 40, "g"));
        rawon.getIngredients().add(new Ingredient("Shallots", 30, "g"));
        rawon.getIngredients().add(new Ingredient("Galangal", 20, "g"));
        rawon.getIngredients().add(new Ingredient("Bean Sprouts", 100, "g"));
        defaultTemplates.add(rawon);
        
        // 13. Bebek Betutu (Balinese Roasted Duck)
        MealTemplate bebekBetutu = new MealTemplate("Bebek Betutu", 4, 10.0, 28.0, 38.0);
        bebekBetutu.getIngredients().add(new Ingredient("Duck", 800, "g"));
        bebekBetutu.getIngredients().add(new Ingredient("Spice Paste", 80, "g"));
        bebekBetutu.getIngredients().add(new Ingredient("Cassava Leaves", 100, "g"));
        bebekBetutu.getIngredients().add(new Ingredient("Banana Leaves", 4, "pieces"));
        bebekBetutu.getIngredients().add(new Ingredient("Coconut Oil", 30, "ml"));
        defaultTemplates.add(bebekBetutu);
        
        // 14. Pepes Ikan (Steamed Fish in Banana Leaves)
        MealTemplate pepesIkan = new MealTemplate("Pepes Ikan", 2, 8.0, 12.0, 35.0);
        pepesIkan.getIngredients().add(new Ingredient("Mackerel Fish", 250, "g"));
        pepesIkan.getIngredients().add(new Ingredient("Spice Paste", 50, "g"));
        pepesIkan.getIngredients().add(new Ingredient("Basil Leaves", 20, "g"));
        pepesIkan.getIngredients().add(new Ingredient("Banana Leaves", 3, "pieces"));
        pepesIkan.getIngredients().add(new Ingredient("Coconut Milk", 100, "ml"));
        defaultTemplates.add(pepesIkan);
        
        // 15. Asinan Betawi (Jakarta Style Pickled Salad)
        MealTemplate asianBetawi = new MealTemplate("Asinan Betawi", 1, 40.0, 5.0, 8.0);
        asianBetawi.getIngredients().add(new Ingredient("Mixed Fruits", 200, "g"));
        asianBetawi.getIngredients().add(new Ingredient("Tofu", 100, "g"));
        asianBetawi.getIngredients().add(new Ingredient("Rice Cake", 80, "g"));
        asianBetawi.getIngredients().add(new Ingredient("Tamarind Water", 50, "ml"));
        asianBetawi.getIngredients().add(new Ingredient("Palm Sugar", 25, "g"));
        defaultTemplates.add(asianBetawi);
        
        // 16. Gudeg Yogya (Yogyakarta Style Sweet Jackfruit Curry)
        MealTemplate gudegYogya = new MealTemplate("Gudeg Yogya", 3, 55.0, 20.0, 22.0);
        gudegYogya.getIngredients().add(new Ingredient("Young Jackfruit", 300, "g"));
        gudegYogya.getIngredients().add(new Ingredient("Coconut Milk", 250, "ml"));
        gudegYogya.getIngredients().add(new Ingredient("Chicken", 150, "g"));
        gudegYogya.getIngredients().add(new Ingredient("Boiled Eggs", 3, "pieces"));
        gudegYogya.getIngredients().add(new Ingredient("Palm Sugar", 40, "g"));
        defaultTemplates.add(gudegYogya);
        
        // 17. Tahu Gejrot (Fried Tofu in Sweet Sour Sauce)
        MealTemplate tahuGejrot = new MealTemplate("Tahu Gejrot", 1, 20.0, 15.0, 12.0);
        tahuGejrot.getIngredients().add(new Ingredient("Fried Tofu", 150, "g"));
        tahuGejrot.getIngredients().add(new Ingredient("Palm Sugar", 20, "g"));
        tahuGejrot.getIngredients().add(new Ingredient("Tamarind Water", 40, "ml"));
        tahuGejrot.getIngredients().add(new Ingredient("Chili", 15, "g"));
        tahuGejrot.getIngredients().add(new Ingredient("Shallots", 20, "g"));
        defaultTemplates.add(tahuGejrot);
        
        // 18. Sate Kambing (Goat Satay)
        MealTemplate sateKambing = new MealTemplate("Sate Kambing", 2, 8.0, 18.0, 32.0);
        sateKambing.getIngredients().add(new Ingredient("Goat Meat", 200, "g"));
        sateKambing.getIngredients().add(new Ingredient("Sweet Soy Sauce", 30, "ml"));
        sateKambing.getIngredients().add(new Ingredient("Peanut Sauce", 60, "g"));
        sateKambing.getIngredients().add(new Ingredient("Rice Cake", 100, "g"));
        sateKambing.getIngredients().add(new Ingredient("Wooden Skewers", 10, "pieces"));
        defaultTemplates.add(sateKambing);
        
        // 19. Opor Ayam (Chicken in Coconut Curry)
        MealTemplate oporAyam = new MealTemplate("Opor Ayam", 3, 18.0, 22.0, 28.0);
        oporAyam.getIngredients().add(new Ingredient("Chicken Pieces", 400, "g"));
        oporAyam.getIngredients().add(new Ingredient("Coconut Milk", 300, "ml"));
        oporAyam.getIngredients().add(new Ingredient("Spice Paste", 60, "g"));
        oporAyam.getIngredients().add(new Ingredient("Lemongrass", 2, "stalks"));
        oporAyam.getIngredients().add(new Ingredient("Bay Leaves", 3, "pieces"));
        defaultTemplates.add(oporAyam);
        
        // 20. Es Cendol (Indonesian Sweet Dessert Drink)
        MealTemplate esCendol = new MealTemplate("Es Cendol", 1, 45.0, 8.0, 4.0);
        esCendol.getIngredients().add(new Ingredient("Cendol Noodles", 100, "g"));
        esCendol.getIngredients().add(new Ingredient("Coconut Milk", 150, "ml"));
        esCendol.getIngredients().add(new Ingredient("Palm Sugar Syrup", 50, "ml"));
        esCendol.getIngredients().add(new Ingredient("Shaved Ice", 200, "g"));
        esCendol.getIngredients().add(new Ingredient("Jackfruit", 50, "g"));
        defaultTemplates.add(esCendol);
        
        saveMealTemplates(defaultTemplates);
    }

    /**
     * Add a new meal template to the collection
     */
    public static void addMealTemplate(MealTemplate newMeal) {
        List<MealTemplate> templates = loadMealTemplates();
        templates.add(newMeal);
        saveMealTemplates(templates);
    }

    /**
     * Remove a meal template from the collection
     */
    public static void removeMealTemplate(MealTemplate mealToRemove) {
        List<MealTemplate> templates = loadMealTemplates();
        templates.removeIf(meal -> meal.getName().equals(mealToRemove.getName()));
        saveMealTemplates(templates);
    }

    /**
     * Update an existing meal template
     */
    public static void updateMealTemplate(MealTemplate oldMeal, MealTemplate newMeal) {
        List<MealTemplate> templates = loadMealTemplates();
        for (int i = 0; i < templates.size(); i++) {
            if (templates.get(i).getName().equals(oldMeal.getName())) {
                templates.set(i, newMeal);
                break;
            }
        }
        saveMealTemplates(templates);
    }
}
