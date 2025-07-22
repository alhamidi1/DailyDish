package org.example.meal_planner.services;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Ai {
    private Client client;
    private Gson gson;
    String model = "gemini-2.5-flash";
    private String apiKey;

    public Ai() {
        // Try to get API key from multiple sources
        this.apiKey = getApiKey();
        
        // Initialize the client and Gson instance
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API Key is missing. Please set the GEMINI_API_KEY environment variable, gemini.api.key system property, or create a .env file with GEMINI_API_KEY.");
        }
        this.gson = new Gson();
        this.client = Client.builder().apiKey(apiKey).build();
    }

    private String getApiKey() {
        // 1. Try system property
        String key = System.getProperty("gemini.api.key");
        if (key != null && !key.isEmpty()) {
            return key;
        }
        
        // 2. Try environment variable
        key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.isEmpty()) {
            return key;
        }
        
        // 3. Try reading from .env file
        return readFromEnvFile();
    }

    private String readFromEnvFile() {
        try {
            Path envPath = Paths.get(".env");
            if (!Files.exists(envPath)) {
                return null;
            }
            
            List<String> lines = Files.readAllLines(envPath);
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("GEMINI_API_KEY=")) {
                    return line.substring("GEMINI_API_KEY=".length()).trim();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading .env file: " + e.getMessage());
        }
        return null;
    }

    public String AskAi(String prompt){
        String response = "" ;

        List<Content> contents = ImmutableList.of(
                Content.builder()
                        .role("user")
                        .parts(ImmutableList.of(
                                Part.fromText(prompt)
                        ))
                        .build()
        );
        GenerateContentConfig config =
                GenerateContentConfig
                        .builder()
                        .thinkingConfig(
                                ThinkingConfig
                                        .builder()
                                        .thinkingBudget(-1)
                                        .build()
                        )
                        .safetySettings(ImmutableList.of(
                                SafetySetting.builder()
                                        .category("HARM_CATEGORY_HARASSMENT")
                                        .threshold("BLOCK_LOW_AND_ABOVE")  // Block most
                                        .build(),
                                SafetySetting.builder()
                                        .category("HARM_CATEGORY_HATE_SPEECH")
                                        .threshold("BLOCK_LOW_AND_ABOVE")  // Block most
                                        .build(),
                                SafetySetting.builder()
                                        .category("HARM_CATEGORY_SEXUALLY_EXPLICIT")
                                        .threshold("BLOCK_LOW_AND_ABOVE")  // Block most
                                        .build(),
                                SafetySetting.builder()
                                        .category("HARM_CATEGORY_DANGEROUS_CONTENT")
                                        .threshold("BLOCK_LOW_AND_ABOVE")  // Block most
                                        .build()
                        ))
                        .responseMimeType("application/json")
                        .responseSchema(Schema.fromJson("""
        {
          "type": "object",
          "properties": {
            "meals": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "breakfast": {
                    "type": "object",
                    "properties": {
                      "foods": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "name": {
                              "type": "string"
                            },
                            "food_calories": {
                              "type": "number"
                            },
                            "food_carbs_g": {
                              "type": "number"
                            },
                            "food_protein_g": {
                              "type": "number"
                            },
                            "food_fat_g": {
                              "type": "number"
                            },
                            "ingredients": {
                              "type": "array",
                              "items": {
                                "type": "object",
                                "properties": {
                                  "name": {
                                    "type": "string"
                                  },
                                  "amount": {
                                    "type": "number"
                                  },
                                  "unit": {
                                    "type": "string"
                                  }
                                },
                                "required": [
                                  "name",
                                  "amount",
                                  "unit"
                                ],
                                "propertyOrdering": [
                                  "name",
                                  "amount",
                                  "unit"
                                ]
                              }
                            }
                          },
                          "required": [
                            "name",
                            "food_calories",
                            "food_carbs_g",
                            "food_protein_g",
                            "food_fat_g",
                            "ingredients"
                          ],
                          "propertyOrdering": [
                            "name",
                            "food_calories",
                            "food_carbs_g",
                            "food_protein_g",
                            "food_fat_g",
                            "ingredients"
                          ]
                        }
                      }
                    },
                    "required": [
                      "foods"
                    ],
                    "propertyOrdering": [
                      "foods"
                    ]
                  },
                  "lunch": {
                    "type": "object",
                    "properties": {
                      "foods": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "name": {
                              "type": "string"
                            },
                            "food_calories": {
                              "type": "number"
                            },
                            "food_carbs_g": {
                              "type": "number"
                            },
                            "food_protein_g": {
                              "type": "number"
                            },
                            "food_fat_g": {
                              "type": "number"
                            },
                            "ingredients": {
                              "type": "array",
                              "items": {
                                "type": "object",
                                "properties": {
                                  "name": {
                                    "type": "string"
                                  },
                                  "amount": {
                                    "type": "number"
                                  },
                                  "unit": {
                                    "type": "string"
                                  }
                                },
                                "required": [
                                  "name",
                                  "amount",
                                  "unit"
                                ],
                                "propertyOrdering": [
                                  "name",
                                  "amount",
                                  "unit"
                                ]
                              }
                            }
                          },
                          "required": [
                            "name",
                            "food_calories",
                            "food_carbs_g",
                            "food_protein_g",
                            "food_fat_g",
                            "ingredients"
                          ],
                          "propertyOrdering": [
                            "name",
                            "food_calories",
                            "food_carbs_g",
                            "food_protein_g",
                            "food_fat_g",
                            "ingredients"
                          ]
                        }
                      }
                    },
                    "required": [
                      "foods"
                    ],
                    "propertyOrdering": [
                      "foods"
                    ]
                  },
                  "dinner": {
                    "type": "object",
                    "properties": {
                      "foods": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "name": {
                              "type": "string"
                            },
                            "food_calories": {
                              "type": "number"
                            },
                            "food_carbs_g": {
                              "type": "number"
                            },
                            "food_protein_g": {
                              "type": "number"
                            },
                            "food_fat_g": {
                              "type": "number"
                            },
                            "ingredients": {
                              "type": "array",
                              "items": {
                                "type": "object",
                                "properties": {
                                  "name": {
                                    "type": "string"
                                  },
                                  "amount": {
                                    "type": "number"
                                  },
                                  "unit": {
                                    "type": "string"
                                  }
                                },
                                "required": [
                                  "name",
                                  "amount",
                                  "unit"
                                ],
                                "propertyOrdering": [
                                  "name",
                                  "amount",
                                  "unit"
                                ]
                              }
                            }
                          },
                          "required": [
                            "name",
                            "food_calories",
                            "food_carbs_g",
                            "food_protein_g",
                            "food_fat_g",
                            "ingredients"
                          ],
                          "propertyOrdering": [
                            "name",
                            "food_calories",
                            "food_carbs_g",
                            "food_protein_g",
                            "food_fat_g",
                            "ingredients"
                          ]
                        }
                      }
                    },
                    "required": [
                      "foods"
                    ],
                    "propertyOrdering": [
                      "foods"
                    ]
                  }
                },
                "required": [
                  "breakfast",
                  "lunch",
                  "dinner"
                ],
                "propertyOrdering": [
                  "breakfast",
                  "lunch",
                  "dinner"
                ]
              }
            }
          },
          "required": [
            "meals"
          ],
          "propertyOrdering": [
            "meals"
          ]
       }
      """))
                        .systemInstruction(
                                Content
                                        .fromParts(
                                                Part.fromText("Part.fromText(\"You are a world-class Nutritional AI and expert meal planner. Your primary mission is to generate nutritionally accurate and well-balanced meal plans.\\n" + //
                                                                                                    "\\n" + //
                                                                                                    "###Core Directives\\n" + //
                                                                                                    "* Highest Priority: Nutritional Accuracy. All nutritional data (calories, macros) for each dish must be factually correct. You must internally verify every value against a reliable source like the USDA FoodData Central database based on the actual portion size of the full dish.\\n" + //
                                                                                                    "* Second Priority: Macronutrient Balance. The total daily nutrition must be well-balanced. Following the macronutrient rules is more important than matching the exact calorie target provided in the user prompt.\\n" + //
                                                                                                    "\\n" + //
                                                                                                    "###Macronutrient & Balance Rules\\n" + //
                                                                                                    "* The total daily macronutrient split must be approximately 40% Carbohydrates, 30% Protein, and 30% Fat.\\n" + //
                                                                                                    "* Total daily fat intake must never fall below 40g for a 2000-calorie diet. Adjust proportionally for higher or lower calorie needs.\\n" + //
                                                                                                    "* Each individual meal must be balanced and appropriate for a healthy diet.\\n" + //
                                                                                                    "\\n" + //
                                                                                                    "###Meal Requirements\\n" + //
                                                                                                    "* Meals must be Indonesian and Islamic(Halal).\\n" + //
                                                                                                    "* Meals must be simple and practical — easy to prepare at home.\\n" + //
                                                                                                    "* Use affordable, commonly available ingredients.\\n" + //
                                                                                                    "* Each meal must cost no more than $4 USD.\\n" + //
                                                                                                    "* Each meal (breakfast, lunch, dinner) must include multiple dishes, not just a single food item.\\n" + //
                                                                                                    "* Each dish must be nutritionally coherent and represent a real-world plate.\\n" + //
                                                                                                    "* Each meal must contain unique dishes — no duplication of the same dish across different meals.\\n" + //
                                                                                                    "\\n" + //
                                                                                                    "###Dish Rules\\n" + //
                                                                                                    "* Each item in the foods array must be a dish, not an ingredient.\\n" + //
                                                                                                    "*Dish names must be simple, realistic, and commonly known, such as: “Grilled Chicken and Rice Bowl”, “Oatmeal with Peanut Butter and Banana”, or “Lentil Stew with Spinach”.\\n" + //
                                                                                                    "* Do not list the ingredients of the dish. Only include the full dish as one item with its total nutritional values.\\n" + //
                                                                                                    "* Dishes must reflect practical home-cooked meals, not restaurant-style or gourmet plates.\\n" + //
                                                                                                    "\\n" + //
                                                                                                    "###Accuracy & Formatting Rules\\n" + //
                                                                                                    "* Every dish must have factually accurate nutritional values based on realistic portion sizes.\\n" + //
                                                                                                    "* Use one decimal place for values with fractions (e.g., 117.6), and use whole integers where appropriate (e.g., 320).\\n" + //
                                                                                                    "* Never guess or round arbitrarily. Always use verified values based on the exact portion size of the full dish.\\n" + //
                                                                                                    "\\n" + //
                                                                                                    "###Food & Ingredient Rules\\n" + //
                                                                                                    "* For each food, provide a list of its ingredients.\\n" + //
                                                                                                    "* Only use everyday foods such as: eggs, rice, oats, chicken, beans, lentils, fruits, vegetables, bread, pasta, cooking oil, nuts, and basic spices.\\n" + //
                                                                                                    "* Avoid: restaurant-style meals, expensive or rare ingredients, complicated recipes, or anything that contains dietary restrictions.\\n" + //
                                                                                                    "\\n" + //
                                                                                                    "###Output Format\\n" + //
                                                                                                    "* Return a single, valid JSON object that adheres strictly to the provided schema.\\n" + //
                                                                                                    "* Do not include extra characters, comments, explanations, or wrapper text.\\n" + //
                                                                                                    "* Do not include Optional[], null, or undefined anywhere in the output.\\n" + //
                                                                                                    "* The JSON object must be complete, valid, and ready for programmatic parsing.\")\n" + //
                                                                                                    "a")
                                        )
                        )
                        .build();

        ResponseStream<GenerateContentResponse> responseStream = client.models.generateContentStream(model, contents, config);

        for (GenerateContentResponse res : responseStream) {
            if (res.candidates().isEmpty() || res.candidates().get().get(0).content().isEmpty() || res.candidates().get().get(0).content().get().parts().isEmpty()) {
                continue;
            }

            List<Part> parts = res.candidates().get().get(0).content().get().parts().get();
            for (Part part : parts) {
                response += part.text();
            }
        }

        responseStream.close();

        return response.replaceAll("\\]?Optional\\[", "")
                .replace("Optional[", "")
                .replace("]Optional[", "");
    }
}