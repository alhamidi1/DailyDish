@echo off
REM DailyDish Application Launcher for Windows
REM This script helps run the DailyDish JAR file with proper JavaFX configuration

echo 🍽️  Starting DailyDish - AI-Powered Meal Planning Application
echo ================================================

REM Check if JAR file exists
if not exist "dist\Meal_Planner-1.0-SNAPSHOT.jar" (
    echo ❌ JAR file not found. Building application...
    mvnw.cmd clean package
    if errorlevel 1 (
        echo ❌ Build failed. Please check the error messages above.
        pause
        exit /b 1
    )
    copy target\Meal_Planner-1.0-SNAPSHOT.jar dist\
)

REM Check for API key
if "%GEMINI_API_KEY%"=="" if not exist ".env" (
    echo ⚠️  Warning: No API key found. AI features will be disabled.
    echo    To enable AI features, either:
    echo    1. Set GEMINI_API_KEY environment variable
    echo    2. Create a .env file with GEMINI_API_KEY=your_key_here
    echo.
)

echo 🚀 Launching DailyDish...

REM Try to run with different JavaFX configurations
java --add-modules javafx.controls,javafx.fxml -jar dist\Meal_Planner-1.0-SNAPSHOT.jar 2>nul

if errorlevel 1 (
    echo 🔄 Trying alternative JavaFX configuration...
    java -jar dist\Meal_Planner-1.0-SNAPSHOT.jar
)

if errorlevel 1 (
    echo ❌ Failed to launch. You can try running manually with:
    echo    java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar dist\Meal_Planner-1.0-SNAPSHOT.jar
    echo.
    echo    Or build and run with Maven:
    echo    mvnw.cmd clean javafx:run
    pause
)
