# IntelliJ Run Configuration Template

## To create a run configuration in IntelliJ IDEA:

### Step-by-Step Instructions:

1. **Open HelloApplication.java**
   - Navigate to: `src/main/java/org/example/meal_planner/HelloApplication.java`

2. **Create Run Configuration**
   - Right-click on the file → **Run 'HelloApplication.main()'**
   - Or go to **Run** → **Edit Configurations...**

3. **Configure Environment Variables**
   - In the run configuration dialog:
     - **Name**: HelloApplication
     - **Main class**: `org.example.meal_planner.HelloApplication`
     - **Environment variables**: Click the folder icon 📁
     - Add: `GEMINI_API_KEY=your_actual_api_key_here`

4. **Save and Run**
   - Click **OK** to save
   - Click **Run** to start the application

### Run Configuration XML (Optional)
If you want to share run configurations with your team, you can create this file:
`.idea/runConfigurations/HelloApplication.xml`

```xml
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="HelloApplication" type="Application" factoryName="Application">
    <envs>
      <env name="GEMINI_API_KEY" value="your_api_key_here" />
    </envs>
    <module name="Meal_Planner" />
    <option name="MAIN_CLASS_NAME" value="org.example.meal_planner.HelloApplication" />
    <method v="2">
      <option name="Make" enabled="true" />
    </method>
  </configuration>
</component>
```

**Note**: Replace `your_api_key_here` with your actual Gemini API key before running.
