# IntelliJ IDEA Setup Guide for Meal Planner

## Setting up Environment Variables in IntelliJ IDEA

### Method 1: Run Configuration (Recommended)
1. **Open the project** in IntelliJ IDEA
2. **Find the main class**: Navigate to `src/main/java/org/example/meal_planner/HelloApplication.java`
3. **Create/Edit Run Configuration**:
   - Right-click on `HelloApplication.java` → **Run 'HelloApplication.main()'**
   - Or go to **Run** → **Edit Configurations...**
   - Select the **HelloApplication** configuration (or create a new one)
4. **Add Environment Variables**:
   - In the **Environment variables** field, click the folder icon 📁
   - Click the **+** button to add a new variable
   - Set:
     - **Name**: `GEMINI_API_KEY`
     - **Value**: `your_api_key_here` (replace with actual key)
   - Click **OK**
5. **Save the configuration** and run the application

### Method 2: IDE-wide Environment Variables
1. **Go to File** → **Settings** (or **IntelliJ IDEA** → **Preferences** on macOS)
2. **Navigate to** Build, Execution, Deployment → **Build Tools** → **Maven** → **Runner**
3. **In Environment variables field**, add:
   ```
   GEMINI_API_KEY=your_api_key_here
   ```
4. **Apply** and **OK**

### Method 3: Using .env File (with plugin)
1. **Install the .env plugin**:
   - Go to **File** → **Settings** → **Plugins**
   - Search for "**.env files support**" or "**EnvFile**"
   - Install and restart IntelliJ
2. **Use the .env file**: The plugin will automatically load environment variables from the `.env` file in your project root

## Running the Application

### Option 1: Direct Java Run
- Right-click on `HelloApplication.java` → **Run 'HelloApplication.main()'**
- Use the run configuration with environment variables set

### Option 2: Maven Integration
1. **Open the Maven tool window** (View → Tool Windows → Maven)
2. **Navigate to** Plugins → javafx → javafx:run
3. **Double-click** to run
4. **Note**: Make sure your system environment variable is set for Maven execution

### Option 3: Terminal within IntelliJ
1. **Open the terminal** in IntelliJ (View → Tool Windows → Terminal)
2. **Set the environment variable**:
   ```bash
   export GEMINI_API_KEY="your_api_key_here"
   ```
3. **Run the application**:
   ```bash
   ./mvnw javafx:run
   ```

## Project Import Instructions

### Initial Setup
1. **Clone the repository**:
   ```bash
   git clone https://github.com/TR-Mohd/Meal-Planner.git
   cd Meal-Planner
   ```

2. **Create your .env file**:
   ```bash
   echo "GEMINI_API_KEY=your_api_key_here" > .env
   ```
   *(Replace `your_api_key_here` with your actual Gemini API key)*

3. **Open in IntelliJ**:
   - **File** → **Open** → Select the `Meal-Planner` folder
   - IntelliJ should automatically detect it as a Maven project

### Project Configuration
1. **Set Project SDK**:
   - **File** → **Project Structure** → **Project**
   - Set **Project SDK** to Java 21 or higher
   - Set **Project language level** to match

2. **Maven Configuration**:
   - IntelliJ should automatically import Maven dependencies
   - If not, go to **View** → **Tool Windows** → **Maven** → Click refresh button

## Troubleshooting

### Common Issues:

1. **"API Key is missing" error**:
   - Check that `GEMINI_API_KEY` is set in your run configuration
   - Verify the environment variable value is correct

2. **JavaFX module issues**:
   - Ensure you're using Java 21 or higher
   - Check that JavaFX dependencies are properly loaded

3. **Maven execution problems**:
   - Set the environment variable in your system/shell profile
   - Or use the IntelliJ terminal with the export command

### Verification Steps:
1. **Check environment variable** in IntelliJ terminal:
   ```bash
   echo $GEMINI_API_KEY
   ```
2. **Test the application** by running the main class
3. **Try the "Generate New Meals" button** to verify AI integration

## Security Reminders
- **Never commit** your actual API key to version control
- **Each team member** needs their own Gemini API key
- **The .env file is git-ignored** for security
- **Keep your API key confidential**

## Getting a Gemini API Key
1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Sign in with your Google account
3. Create a new API key
4. Copy the key and use it in your environment setup

## Support
If you encounter any issues:
1. Check this guide first
2. Verify your environment variable setup
3. Test with a simple Maven run in terminal
4. Contact the team lead if problems persist
