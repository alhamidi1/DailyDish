# Environment Setup for Meal Planner

## Setting up the Gemini API Key

The application tries to get the API key from multiple sources (in order of priority):
1. System property: `gemini.api.key`
2. Environment variable: `GEMINI_API_KEY`
3. `.env` file in project root

### For Development (Recommended)
1. Copy `.env.template` to `.env`:
   ```bash
   cp .env.template .env
   ```
2. Edit `.env` and add your real API key:
   ```
   GEMINI_API_KEY=your_actual_api_key_here
   ```

### For Shell/Terminal Usage
Add to your shell profile (`~/.zshrc`, `~/.bashrc`, etc.):
```bash
export GEMINI_API_KEY="your_api_key_here"
```

### For VS Code Launch Configuration
The launch.json file is configured to use the environment variable:
```json
"env": {
    "GEMINI_API_KEY": "${env:GEMINI_API_KEY}"
}
```

**Note:** When running from VS Code, if the environment variable isn't available, the application will automatically fall back to reading from the `.env` file.

## Running the Application

### Via Maven
```bash
./mvnw javafx:run
```

### Via VS Code
Use the "HelloApplication" launch configuration - it will work with either environment variables or `.env` file.

## Troubleshooting

### AI Generation Not Working
If the AI meal generation fails:
1. Check if you have a `.env` file with your API key
2. Verify your API key is valid at https://makersuite.google.com/app/apikey
3. Make sure the `.env` file is in the project root directory

### Error Messages
- "API Key is missing" → Create `.env` file with your API key
- "Error reading .env file" → Check file permissions and format

## Security Notes
- The `.env` file is git-ignored for security
- Never commit API keys to version control
- The launch.json file references environment variables, not actual keys
- Always use `.env.template` as reference, not the actual `.env` file
