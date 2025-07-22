#!/bin/bash
# DailyDish Application Launcher
# This script helps run the DailyDish JAR file with proper JavaFX configuration

echo "🍽️  Starting DailyDish - AI-Powered Meal Planning Application"
echo "================================================"

# Check if JAR file exists
if [ ! -f "dist/Meal_Planner-1.0-SNAPSHOT.jar" ]; then
    echo "❌ JAR file not found. Building application..."
    ./mvnw clean package
    if [ $? -ne 0 ]; then
        echo "❌ Build failed. Please check the error messages above."
        exit 1
    fi
    cp target/Meal_Planner-1.0-SNAPSHOT.jar dist/
fi

# Check for API key
if [ -z "$GEMINI_API_KEY" ] && [ ! -f ".env" ]; then
    echo "⚠️  Warning: No API key found. AI features will be disabled."
    echo "   To enable AI features, either:"
    echo "   1. Set GEMINI_API_KEY environment variable"
    echo "   2. Create a .env file with GEMINI_API_KEY=your_key_here"
    echo ""
fi

echo "🚀 Launching DailyDish..."

# Try to run with different JavaFX configurations
java --add-modules javafx.controls,javafx.fxml -jar dist/Meal_Planner-1.0-SNAPSHOT.jar 2>/dev/null

if [ $? -ne 0 ]; then
    echo "🔄 Trying alternative JavaFX configuration..."
    java -jar dist/Meal_Planner-1.0-SNAPSHOT.jar
fi

if [ $? -ne 0 ]; then
    echo "❌ Failed to launch. You can try running manually with:"
    echo "   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar dist/Meal_Planner-1.0-SNAPSHOT.jar"
    echo ""
    echo "   Or build and run with Maven:"
    echo "   ./mvnw clean javafx:run"
fi
