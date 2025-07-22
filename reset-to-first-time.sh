#!/bin/bash
# DailyDish - Reset to First Time User Experience
# This script resets the application to show the complete Profile Setup Wizard

echo "🔄 DailyDish - Reset to First Time User"
echo "======================================"
echo ""
echo "This will delete your current user data and reset the app"
echo "to the initial Profile Setup Wizard experience."
echo ""
echo "Files that will be DELETED:"
echo "  ❌ data/user_profile.xml (your profile information)"
echo "  ❌ data/meal_plan.xml (your current meal plan)"
echo "  ❌ data/grocery_list.xml (your grocery list)"
echo "  ❌ data/checkbox_states.properties (UI preferences)"
echo ""
echo "Files that will be KEPT:"
echo "  ✅ data/meal_templates.xml (20 Indonesian meal recipes)"
echo "  ✅ All source code and configuration files"
echo ""

read -p "Do you want to continue? (y/N): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🗑️  Removing user data files..."
    
    # Check if files exist before attempting to delete
    files_deleted=0
    
    if [ -f "data/user_profile.xml" ]; then
        rm "data/user_profile.xml"
        echo "   ✅ Deleted user_profile.xml"
        files_deleted=$((files_deleted + 1))
    fi
    
    if [ -f "data/meal_plan.xml" ]; then
        rm "data/meal_plan.xml"
        echo "   ✅ Deleted meal_plan.xml"
        files_deleted=$((files_deleted + 1))
    fi
    
    if [ -f "data/grocery_list.xml" ]; then
        rm "data/grocery_list.xml"
        echo "   ✅ Deleted grocery_list.xml"
        files_deleted=$((files_deleted + 1))
    fi
    
    if [ -f "data/checkbox_states.properties" ]; then
        rm "data/checkbox_states.properties"
        echo "   ✅ Deleted checkbox_states.properties"
        files_deleted=$((files_deleted + 1))
    fi
    
    echo ""
    if [ $files_deleted -gt 0 ]; then
        echo "✨ Reset complete! Deleted $files_deleted file(s)."
    else
        echo "ℹ️  No user data files found. App is already in first-time user state."
    fi
    
    echo ""
    echo "🚀 Ready to start! Run the application with:"
    echo "   ./mvnw clean javafx:run"
    echo ""
    echo "You will now see the complete 7-step Profile Setup Wizard."
    
else
    echo ""
    echo "❌ Reset cancelled. No files were deleted."
fi
