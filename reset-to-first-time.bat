@echo off
REM DailyDish - Reset to First Time User Experience
REM This script resets the application to show the complete Profile Setup Wizard

echo 🔄 DailyDish - Reset to First Time User
echo ======================================
echo.
echo This will delete your current user data and reset the app
echo to the initial Profile Setup Wizard experience.
echo.
echo Files that will be DELETED:
echo   ❌ data\user_profile.xml (your profile information)
echo   ❌ data\meal_plan.xml (your current meal plan)
echo   ❌ data\grocery_list.xml (your grocery list)
echo   ❌ data\checkbox_states.properties (UI preferences)
echo.
echo Files that will be KEPT:
echo   ✅ data\meal_templates.xml (20 Indonesian meal recipes)
echo   ✅ All source code and configuration files
echo.

set /p answer=Do you want to continue? (y/N): 

if /i "%answer:~,1%" EQU "Y" (
    echo 🗑️  Removing user data files...
    
    set files_deleted=0
    
    if exist "data\user_profile.xml" (
        del "data\user_profile.xml"
        echo    ✅ Deleted user_profile.xml
        set /a files_deleted+=1
    )
    
    if exist "data\meal_plan.xml" (
        del "data\meal_plan.xml"
        echo    ✅ Deleted meal_plan.xml
        set /a files_deleted+=1
    )
    
    if exist "data\grocery_list.xml" (
        del "data\grocery_list.xml"
        echo    ✅ Deleted grocery_list.xml
        set /a files_deleted+=1
    )
    
    if exist "data\checkbox_states.properties" (
        del "data\checkbox_states.properties"
        echo    ✅ Deleted checkbox_states.properties
        set /a files_deleted+=1
    )
    
    echo.
    if %files_deleted% GTR 0 (
        echo ✨ Reset complete! Deleted %files_deleted% file(s).
    ) else (
        echo ℹ️  No user data files found. App is already in first-time user state.
    )
    
    echo.
    echo 🚀 Ready to start! Run the application with:
    echo    mvnw.cmd clean javafx:run
    echo.
    echo You will now see the complete 7-step Profile Setup Wizard.
    
) else (
    echo.
    echo ❌ Reset cancelled. No files were deleted.
)

pause
