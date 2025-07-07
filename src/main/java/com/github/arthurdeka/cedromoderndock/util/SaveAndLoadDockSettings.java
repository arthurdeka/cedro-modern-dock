package com.github.arthurdeka.cedromoderndock.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.arthurdeka.cedromoderndock.model.DockModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class used to save and load the Dock's settings
 */
public final class SaveAndLoadDockSettings {

    private static final String CONFIG_FILE_NAME = "config.json";
    private static final Path CONFIG_FILE_PATH = getConfigPath();
    private static final ObjectMapper mapper = createObjectMapper();

    private SaveAndLoadDockSettings() {}

    /**
     * Determines the absolute path for the configuration file in the user's
     * AppData/Roaming directory.
     * @return The absolute Path to the config file.
     */
    private static Path getConfigPath() {
        // Get the AppData\Roaming folder path (standard for user-specific config)
        String appDataPath = System.getenv("APPDATA");
        if (appDataPath == null || appDataPath.isEmpty()) {
            // Fallback to user home directory if APPDATA is not available
            appDataPath = System.getProperty("user.home");
        }

        // Create a dedicated directory for our application to keep things clean
        Path configDir = Paths.get(appDataPath, "CedroModernDock");

        // Ensure the directory exists
        File dir = configDir.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Return the full path to our config file
        return configDir.resolve(CONFIG_FILE_NAME);
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    /**
     * saves the DockModel object in the config file (config.json).
     * @param model DockModel object to be saved.
     */
    public static void save(DockModel model) {
        try {
            mapper.writeValue(CONFIG_FILE_PATH.toFile(), model);
        } catch (IOException e) {
            Logger.error("Error saving the DockModel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method loads the currennt config.json file and returns a DockModel.
     * If the file is corrupted or does not exist, a new one with only a DockSettings item is created and returned
     *
     * @return A DockModel instance, always valid (loaded or default).
     */
    public static DockModel load() {
        File configFile = CONFIG_FILE_PATH.toFile();
        if (configFile.exists() && configFile.length() > 0) { // Check if file is not empty
            try {
                // Tries to read the current file
                DockModel model = mapper.readValue(configFile, DockModel.class);
                Logger.info("[SaveAndLoadDockSettings] Dock config.json loaded successfully from: " + CONFIG_FILE_PATH);
                return model;
            } catch (IOException e) {
                Logger.error("Error reading config.json, creating a new default config file: " + e.getMessage());
                // If reading fails, it creates and returns a new default config dock
                return createAndSaveDefault();
            }
        } else {
            // If the file does not exist, it also creates and returns a new default config dock
            Logger.error("config.json not found or is empty. Creating a new default config file at: " + CONFIG_FILE_PATH);
            return createAndSaveDefault();
        }
    }

    /**
     * Method to create and return a new default DockModel
     * @return A default config DockModel.
     */
    private static DockModel createAndSaveDefault() {
        DockModel model = new DockModel();
        model.loadDefaultItems(); // loads the default items
        save(model); // saves the dock
        return model;
    }
}