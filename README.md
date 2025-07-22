# DailyDish - AI-Powered Meal Planning Application

A comprehensive JavaFX-based meal planning application featuring Indonesian cuisine with AI-powered meal generation using Google Gemini API. This application helps users create personalized meal plans, track nutrition, and manage grocery lists.

## 📋 Table of Contents
- [Features](#features)
- [Installation & Running the App](#installation--running-the-app)
- [API Key Setup](#api-key-setup)
- [Application JAR File](#application-jar-file)
- [How to Start Fresh & Application Workflow](#how-to-start-fresh--application-workflow)
- [Project Structure](#project-structure)
- [Technical Details](#technical-details)
- [Troubleshooting](#troubleshooting)

## ✨ Features

### Core Functionality
- 🤖 **AI-Powered Meal Generation**: Intelligent meal suggestions using Google Gemini API
- 🍽️ **Indonesian Cuisine Focus**: 20+ authentic Indonesian meal templates with complete nutritional data
- 📊 **Comprehensive Nutrition Tracking**: Calorie counting, macronutrient analysis (carbs, fat, protein)
- 🎯 **Personalized Meal Planning**: Customized recommendations based on user dietary goals and preferences
- 🛒 **Smart Grocery Management**: Automatic grocery list generation from meal plans
- 💾 **Data Persistence**: XML-based data storage for meal plans, templates, and user profiles

### User Interface Features
- 📱 **Intuitive Profile Setup**: Step-by-step user onboarding with dietary preferences
- 🎨 **Modern JavaFX Interface**: Clean, responsive design with custom CSS styling
- 📅 **Daily Meal Dashboard**: Easy-to-use interface for managing breakfast, lunch, and dinner
- 🔧 **Meal Template Manager**: Add, edit, and organize meal templates with ingredients
- 👤 **Profile Management**: Edit user information, dietary restrictions, and goals

## 🚀 Installation & Running the App

### Prerequisites
- **Java Development Kit (JDK) 24** or higher
- **Maven 3.8+** (included via Maven wrapper)
- **Gemini API Key** from [Google AI Studio](https://aistudio.google.com/app/apikey)

### Method 1: Using Maven (Recommended)

1. **Clone or download the project**:
   ```bash
   git clone <repository-url>
   cd DailyDish
   ```

2. **Set up your API key** (see [API Key Setup](#api-key-setup) section below)

3. **Run the application**:
   ```bash
   # On Windows
   mvnw.cmd clean javafx:run
   
   # On macOS/Linux
   ./mvnw clean javafx:run
   ```

### Method 2: Using IDE (IntelliJ IDEA)

1. **Import the project**:
   - Open IntelliJ IDEA
   - File → Open → Select the DailyDish folder
   - IntelliJ will automatically detect it as a Maven project

2. **Configure the run configuration**:
   - Navigate to `src/main/java/org/example/meal_planner/Main.java`
   - Right-click → Run 'Main.main()'
   - Edit the run configuration to add environment variables (see API Key Setup)

3. **Run the application** using the configured run configuration

### Method 3: Using the JAR file

1. **Build the JAR** (if not already available):
   ```bash
   ./mvnw clean package
   ```

2. **Run the JAR file**:
   ```bash
   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar dist/Meal_Planner-1.0-SNAPSHOT.jar
   ```

   Or use the JavaFX runtime if installed:
   ```bash
   java -jar dist/Meal_Planner-1.0-SNAPSHOT.jar
   ```

## 🔐 API Key Setup

This application integrates with Google Gemini AI service which requires an API key for meal generation features.

⚠️ **For security reasons, the actual API key is NOT included in this repository or submission.**

### To configure the API key:

**Option 1: Environment Variable (Recommended)**
```bash
# Set environment variable before running
export GEMINI_API_KEY="your_api_key_here"
./mvnw javafx:run
```

**Option 2: Create a .env file**
```bash
# Create .env file in project root
echo "GEMINI_API_KEY=your_api_key_here" > .env
```

**Option 3: System Property**
```bash
# Pass as system property
java -Dgemini.api.key="your_api_key_here" -jar dist/Meal_Planner-1.0-SNAPSHOT.jar
```

### Getting an API Key:
1. Visit [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Sign in with your Google account
3. Create a new API key
4. Copy the key and use it in one of the methods above

**Note**: If no API key is configured, the application will still function but AI meal generation features will be disabled.

---

## 🚀 Quick Start for Newcomers

**Want to experience the full app from beginning?** Follow these simple steps:

**Option 1: Use the Reset Script (Easiest)**
```bash
# Run the automated reset script
./reset-to-first-time.sh    # On macOS/Linux
# OR
reset-to-first-time.bat     # On Windows
```

**Option 2: Manual Reset**
```bash
# 1. Navigate to project directory
cd DailyDish

# 2. Reset app to first-time user state
rm -f data/user_profile.xml data/meal_plan.xml data/grocery_list.xml

# 3. Run the application
./mvnw clean javafx:run

# 4. Follow the 7-step Profile Setup Wizard
# 5. Enjoy the Dashboard with Indonesian meal planning!
```

**For Windows users (Manual):**
```cmd
# Use these commands instead
del data\user_profile.xml data\meal_plan.xml data\grocery_list.xml
mvnw.cmd clean javafx:run
```

## 📦 Application JAR File

A pre-built JAR file is included in the `/dist/` folder:
- **File**: `dist/Meal_Planner-1.0-SNAPSHOT.jar`
- **Purpose**: Ready-to-run executable for easy testing and evaluation
- **Dependencies**: All required libraries are managed via Maven and included in the build

### Running the JAR:
```bash
# Basic execution (requires JavaFX on system path)
java -jar dist/Meal_Planner-1.0-SNAPSHOT.jar

# With explicit JavaFX modules (if JavaFX not on system path)
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar dist/Meal_Planner-1.0-SNAPSHOT.jar
```

**Note**: If the JAR fails to run due to environment issues, the source code can always be compiled and run using Maven as described in the installation instructions above.

## 📁 Project Structure

```
DailyDish/
├── src/main/java/                          # Java source files
│   ├── module-info.java                    # Java module configuration
│   └── org/example/meal_planner/
│       ├── Main.java                       # Application entry point
│       ├── controllers/                    # UI Controllers
│       │   ├── AppController.java          # Main application controller
│       │   ├── Dashboard/                  # Dashboard controllers
│       │   └── Profile_Setup/              # Profile setup controllers
│       ├── models/                         # Data models
│       │   ├── MealTemplate.java           # Meal template model
│       │   ├── Ingredient.java             # Ingredient model
│       │   ├── DayMealPlan.java           # Daily meal plan model
│       │   └── ...                        # Other model classes
│       └── services/                       # Business logic services
│           ├── MealTemplateService.java    # Meal template management
│           ├── Ai.java                     # AI integration service
│           └── XmlDataService.java         # Data persistence service
├── src/main/resources/                     # Resources and assets
│   └── org/example/meal_planner/
│       ├── css/                           # Stylesheets
│       ├── Dashboard_Resources/           # Dashboard FXML files
│       └── Profile_Setup_Resources/       # Profile setup FXML files
├── data/                                  # Application data files
│   ├── meal_templates.xml                 # Indonesian meal templates
│   ├── meal_plan.xml                      # User meal plans
│   └── user_profile.xml                   # User profile data
├── dist/                                  # Distribution folder
│   └── Meal_Planner-1.0-SNAPSHOT.jar     # Pre-built JAR file
├── run-app.sh / run-app.bat               # Application launcher scripts
├── reset-to-first-time.sh / .bat          # Reset scripts for newcomers
├── pom.xml                                # Maven configuration
├── .env.template                          # Environment variable template
└── README.md                              # This file
```

## 📖 How to Start Fresh & Application Workflow

### 🔄 For Newcomers: Starting from Scratch

If you want to experience the complete application flow from the beginning (recommended for first-time users and testers):

#### Method 1: Delete Data Files (Recommended)
```bash
# Navigate to project directory
cd DailyDish

# Delete all user data files to reset the app
rm -f data/user_profile.xml
rm -f data/meal_plan.xml
rm -f data/grocery_list.xml
rm -f data/checkbox_states.properties

# Keep meal_templates.xml (contains the 20 Indonesian meal templates)
# This file should NOT be deleted as it contains the meal library

# Now run the application
./mvnw clean javafx:run
```

#### Method 2: Backup and Restore Data Files
```bash
# Backup existing data (optional)
mkdir backup
cp data/*.xml backup/

# Delete user-specific files only
rm -f data/user_profile.xml data/meal_plan.xml data/grocery_list.xml

# Run the application to start fresh
./mvnw clean javafx:run
```

#### ⚠️ Important Files - What to Keep vs. Delete

**✅ KEEP these files (DO NOT DELETE):**
- `data/meal_templates.xml` - Contains 20 Indonesian meal recipes
- `src/` folder - All source code
- `pom.xml` - Project configuration
- `.env` or `.env.template` - API key configuration

**❌ DELETE these files to start fresh:**
- `data/user_profile.xml` - User personal information and preferences
- `data/meal_plan.xml` - User's current meal plan and selected meals
- `data/grocery_list.xml` - User's grocery shopping list
- `data/checkbox_states.properties` - UI state preferences

### 🚀 Application Workflow: Two-Stage System

DailyDish operates in **two distinct stages** based on whether you're a new or returning user:

---

## 📋 Stage 1: Profile Setup Wizard (First-Time Users)

When you start the application for the first time (or after deleting user data), you'll enter the **Profile Setup Wizard**. This is a 7-step guided process:

### Step 1: Welcome Screen (`ProfileSetup.fxml`)
- **Purpose**: Introduction to DailyDish
- **Action**: Click "Get Started" to begin setup
- **Time**: ~30 seconds

### Step 2: Diet Introduction (`DietWelcome.fxml`)
- **Purpose**: Introduction to dietary preferences setup
- **Action**: Learn about how the app will customize meals
- **Time**: ~30 seconds

### Step 3: Diet Type Selection (`DietType.fxml`)
- **Purpose**: Choose your dietary preferences
- **Options**: 
  - Anything (no restrictions)
  - Vegetarian
  - Vegan
  - Keto
  - Low-carb
  - Other dietary preferences
- **Action**: Select your diet type and continue
- **Time**: ~1 minute

### Step 4: Food Avoidance (`FoodAvoidance.fxml`)
- **Purpose**: Specify foods/ingredients to avoid
- **Features**: 
  - Multiple selection checkboxes
  - Common allergens and dietary restrictions
  - Custom text input for specific items
- **Action**: Select foods to avoid or skip if none
- **Time**: ~1-2 minutes

### Step 5: About You Welcome (`AboutYouWelcome.fxml`)
- **Purpose**: Transition to personal information section
- **Action**: Click continue to enter physical details
- **Time**: ~30 seconds

### Step 6: User Details (`UserDetails.fxml`)
- **Purpose**: Enter physical characteristics for nutrition calculations
- **Required Information**:
  - **Age**: Used for metabolic calculations
  - **Gender**: Male/Female/Other
  - **Height**: In centimeters (for BMI calculation)
  - **Weight**: In kilograms (for calorie needs)
  - **Body Fat Level**: Low/Medium/High (affects calorie calculations)
  - **Activity Level**: Not very active/Lightly active/Moderately active/Very active
- **Action**: Fill in all fields accurately
- **Time**: ~2-3 minutes

### Step 7: Goals Setting (`UserGoals.fxml`)
- **Purpose**: Set your health and fitness objectives
- **Goal Types**:
  - **General Goal**: Build muscle, lose weight, maintain weight
  - **Specific Calorie Target**: Custom daily calorie goal
  - **Nutritional Goals**: Custom macro targets
- **Action**: Select goal type and specific objectives
- **AI Integration**: If API key is configured, AI will generate initial meal suggestions
- **Time**: ~2-3 minutes

### Profile Setup Completion
- **Data Saved**: All information is saved to `data/user_profile.xml`
- **Initial Meal Plan**: Basic meal plan created in `data/meal_plan.xml`
- **Transition**: Automatically moves to Dashboard (Stage 2)
- **Total Time**: ~7-10 minutes

---

## 🏠 Stage 2: Main Dashboard (Returning Users)

After completing profile setup, or when returning to the app, you'll see the **Main Dashboard**. This is the primary interface for daily meal management:

### Dashboard Overview (`TodayDashboard.fxml`)

#### A. Header Section
- **User Welcome**: Displays your name and today's date
- **Profile Button**: Edit your profile information
- **Navigation**: Quick access to different sections

#### B. Today's Meals Section
**Breakfast Panel**:
- **Current Meal**: Shows selected breakfast (if any)
- **Nutrition Display**: Calories, carbs, fat, protein
- **Actions**: 
  - "Add Meal" - Choose from Indonesian meal templates
  - "Edit Meal" - Modify current meal
  - "Remove Meal" - Clear breakfast selection

**Lunch Panel**:
- **Same features as breakfast**
- **Separate nutrition tracking**
- **Independent meal selection**

**Dinner Panel**:
- **Same features as breakfast and lunch**
- **Evening meal planning**
- **Complete daily nutrition**

#### C. Daily Nutrition Summary
- **Total Calories**: Sum of all meals
- **Macronutrient Breakdown**:
  - Total carbohydrates (grams)
  - Total fat (grams)
  - Total protein (grams)
- **Goal Progress**: Shows progress toward daily targets
- **Visual Indicators**: Color-coded progress bars

#### D. Quick Action Buttons
- **🛒 Grocery List**: Auto-generated shopping list from selected meals
- **📊 Weekly View**: Plan meals for multiple days
- **🍽️ Meal Templates**: Browse and manage the 20 Indonesian meals
- **🤖 AI Suggestions**: Get personalized meal recommendations (requires API key)

### Dashboard Functionality

#### Adding Meals
1. **Click "Add Meal"** on any meal panel
2. **Browse Indonesian Templates**: Choose from 20 pre-loaded meals
   - Nasi Gudeg, Rendang Daging, Gado-Gado, etc.
   - Complete nutritional information
   - Detailed ingredient lists
3. **AI Suggestions** (if API key configured):
   - Personalized recommendations based on your profile
   - Dietary restriction compliance
   - Goal-oriented suggestions
4. **Custom Meals**: Create your own meal with custom ingredients

#### Meal Information Display
For each selected meal, you'll see:
- **Meal Name**: Indonesian dish name
- **Serving Size**: Number of servings
- **Nutrition Facts**:
  - Calories per serving
  - Carbohydrates (g)
  - Fat (g)
  - Protein (g)
- **Ingredients List**: Complete with quantities and units
- **Cultural Information**: Brief description of the dish

#### Grocery List Generation
- **Automatic**: Updates when meals are added/removed
- **Smart Combining**: Consolidates duplicate ingredients
- **Quantity Calculation**: Accurate amounts for shopping
- **Export Options**: Print or save shopping list

#### Profile Management
- **Edit Profile**: Modify personal information
- **Update Goals**: Change fitness objectives
- **Dietary Preferences**: Adjust food restrictions
- **Recalculation**: Nutrition targets update automatically

### Data Persistence
- **Automatic Saving**: All changes saved to XML files
- **Session Recovery**: Resume where you left off
- **Backup Friendly**: Human-readable XML format

---

## 🔄 Switching Between Stages

### From Dashboard Back to Profile Setup
If you want to reset your profile or start over:
```bash
# Delete user profile to trigger setup wizard
rm data/user_profile.xml
# Restart the application
./mvnw clean javafx:run
```

### Maintaining Data While Testing
```bash
# Backup your current data
cp data/user_profile.xml data/user_profile_backup.xml
cp data/meal_plan.xml data/meal_plan_backup.xml

# Test with fresh setup
rm data/user_profile.xml data/meal_plan.xml
./mvnw clean javafx:run

# Restore data later
mv data/user_profile_backup.xml data/user_profile.xml
mv data/meal_plan_backup.xml data/meal_plan.xml
```

---

## 🎯 Application Flow Summary

1. **First Launch** → Profile Setup Wizard (7 steps) → Dashboard
2. **Subsequent Launches** → Dashboard (if profile exists)
3. **Reset Option** → Delete user data → Profile Setup Wizard
4. **Data Driven**: Application automatically detects user status
5. **Seamless Transition**: No manual mode switching required

### Indonesian Meal Templates
The application includes 20 authentic Indonesian dishes:
- Nasi Gudeg, Rendang Daging, Gado-Gado, Soto Ayam
- Nasi Liwet, Bakso, Ayam Bakar, Pecel Lele
- Nasi Padang, Mie Ayam, Ikan Bakar, Rawon
- Bebek Betutu, Pepes Ikan, Asinan Betawi, Gudeg Yogya
- Tahu Gejrot, Sate Kambing, Opor Ayam, Es Cendol

Each template includes complete nutritional information and detailed ingredient lists with authentic Indonesian ingredients like coconut milk, palm sugar, banana leaves, tempeh, and traditional spices.

## 🔧 Technical Details

### Technologies Used
- **Java 24**: Modern Java with module system
- **JavaFX 24**: Modern desktop application framework
- **Maven**: Dependency management and build automation
- **Google Gemini AI**: Intelligent meal suggestions
- **XStream**: XML data serialization
- **Jackson**: JSON processing for AI responses

### Dependencies
All external libraries are managed through Maven:
- JavaFX Controls & FXML
- Google Gemini AI Client
- Gson for JSON processing
- XStream for XML serialization
- Jackson for advanced JSON handling

### Data Storage
- **Format**: XML files for human-readable data storage
- **Location**: `data/` directory
- **Files**: User profiles, meal plans, meal templates, grocery lists
- **Backup**: All data files are human-readable and can be manually edited if needed

## 🛠️ Troubleshooting

### Common Issues

**1. Application won't start**
- Ensure Java 24 or higher is installed
- Check that JavaFX modules are available
- Verify Maven dependencies are downloaded

**2. "API Key is missing" error**
- Set up the API key using one of the methods in [API Key Setup](#api-key-setup)
- Verify the `.env` file exists and contains the correct key
- Check environment variable spelling: `GEMINI_API_KEY`

**3. JavaFX module issues**
- Ensure you're using JDK 24 or higher
- On some systems, you may need to explicitly specify JavaFX module path
- Try running with: `java --module-path /path/to/javafx --add-modules javafx.controls,javafx.fxml`

**4. Maven build fails**
- Run `./mvnw clean install` to refresh dependencies
- Check internet connection for dependency downloads
- Ensure JDK 24 is properly configured in Maven

**5. Data not persisting**
- Check write permissions in the project directory
- Ensure `data/` folder exists and is writable
- XML files should be readable and properly formatted

**6. App goes directly to Dashboard (skipping Profile Setup)**
- This means user data already exists
- To start fresh: `rm -f data/user_profile.xml data/meal_plan.xml`
- Restart the application to see Profile Setup Wizard

**7. Profile Setup Wizard not showing**
- Verify that `data/user_profile.xml` is deleted or empty
- Check file permissions in the `data/` directory
- If `user_profile.xml` exists but is corrupted, delete it to restart setup

**8. Indonesian meals not loading**
- Ensure `data/meal_templates.xml` exists and is not corrupted
- If missing, restart the app to regenerate the file
- File should contain 20 Indonesian meal templates

**9. Application state seems corrupted**
- **Full Reset**: Delete all data files and restart
  ```bash
  rm -f data/*.xml data/*.properties
  ./mvnw clean javafx:run
  ```
- This will regenerate all default data and restart from Profile Setup

### Performance Notes
- First startup may take longer due to JavaFX initialization
- AI meal generation requires internet connection
- Large meal plans may take a moment to load

### Known Limitations
- AI features require valid API key and internet connection
- Application requires JavaFX runtime environment
- XML data files should not be manually edited while application is running

---
- **Project**: DailyDish - AI-Powered Indonesian Meal Planning Application
