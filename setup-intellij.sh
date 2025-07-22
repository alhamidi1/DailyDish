#!/bin/bash
# IntelliJ IDEA Setup Script for Meal Planner

echo "🚀 Setting up Meal Planner for IntelliJ IDEA..."

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "📝 Creating .env file..."
    echo "# Add your Gemini API key here" > .env
    echo "# Get your key from: https://aistudio.google.com/app/apikey" >> .env
    echo "GEMINI_API_KEY=your_api_key_here" >> .env
    echo "✅ .env file created! Please edit it with your actual API key."
else
    echo "✅ .env file already exists."
fi

# Check if Maven wrapper is executable
if [ ! -x "./mvnw" ]; then
    echo "🔧 Making Maven wrapper executable..."
    chmod +x ./mvnw
    echo "✅ Maven wrapper is now executable."
fi

# Test Maven compilation
echo "🔨 Testing Maven compilation..."
./mvnw clean compile -q

if [ $? -eq 0 ]; then
    echo "✅ Maven compilation successful!"
else
    echo "❌ Maven compilation failed. Please check your Java version (requires Java 21+)."
    exit 1
fi

echo ""
echo "🎉 Setup complete!"
echo ""
echo "Next steps for IntelliJ IDEA:"
echo "1. Open this project in IntelliJ IDEA"
echo "2. Edit the .env file with your actual Gemini API key"
echo "3. Follow the instructions in INTELLIJ_SETUP.md"
echo "4. Run the HelloApplication class with environment variables set"
echo ""
echo "📚 For detailed instructions, see: INTELLIJ_SETUP.md"
