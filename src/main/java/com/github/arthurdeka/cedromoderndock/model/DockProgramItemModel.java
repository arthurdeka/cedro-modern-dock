package com.github.arthurdeka.cedromoderndock.model;

import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class DockProgramItemModel implements DockItem {

    private String label = "";
    private String exePath = "";

    public DockProgramItemModel(String label, String exePath) {
        this.label = label;
        this.exePath = exePath;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getPath() {
        return exePath;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void setPath(String path) {
        this.exePath = path;
    }

    @Override
    public void performAction() {
        System.out.println(label + " Clicked");

        // validation: checks if the path is null
        if (exePath == null || exePath.trim().isEmpty()) {
            System.err.println("Executable path not defined for: " + label);
            return;
        }

        // tries to open the software
        try {
            ProcessBuilder pb = new ProcessBuilder(exePath);
            pb.start();
            System.out.println("Executing: " + label);

        } catch (IOException e) {
            System.err.println("Failed to open: " + label);
            System.err.println("Path: " + exePath);
            System.err.println("Error: " + e.getMessage());
            // Un-comment the line below to debug the stack trace
            // e.printStackTrace();
        }
    }
}
