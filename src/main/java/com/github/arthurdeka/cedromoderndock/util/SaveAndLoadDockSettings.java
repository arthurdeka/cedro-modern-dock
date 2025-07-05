package com.github.arthurdeka.cedromoderndock.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.arthurdeka.cedromoderndock.model.DockModel;

import java.io.File;
import java.io.IOException;

/**
 * Utility class used to save and load the Dock's settings
 */
public final class SaveAndLoadDockSettings {

    private static final String CONFIG_FILE = "config.json";
    private static final ObjectMapper mapper = createObjectMapper();

    private SaveAndLoadDockSettings() {}

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    /**
     * saves the DockModel objetct in the config file (config.json).
     * @param model DockModel object to be saved.
     */
    public static void save(DockModel model) {
        try {
            mapper.writeValue(new File(CONFIG_FILE), model);
        } catch (IOException e) {
            System.err.println("Error saving the DockModel: " + e.getMessage());
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
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try {
                // Tries to read the current file
                return mapper.readValue(configFile, DockModel.class);
            } catch (IOException e) {
                System.err.println("Error reading config.json, creating a new default config file: " + e.getMessage());
                // If reading fails, it creates and returns a new default config dock
                return createAndSaveDefault();
            }
        } else {
            // If the file does not exist, it also creates and returns a new default config dock
            System.out.println("config.json not found. creating a new default config file.");
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