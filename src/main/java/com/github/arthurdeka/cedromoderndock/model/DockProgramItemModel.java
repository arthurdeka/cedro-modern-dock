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
            executeAndHandleElevation(exePath);
        } catch (IOException e) {
            System.err.println("Failed to open: " + label);
            System.err.println("Path: " + exePath);
            System.err.println("Error: " + e.getMessage());
            // Un-comment the line below to debug the stack trace
            // e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Process interrupted: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void executeAndHandleElevation(String path) throws IOException, InterruptedException {
        try {
            // First try: open the software as normal permission level
            new ProcessBuilder(path).start();
            System.out.println("Executing: " + label);

        } catch (IOException e) {
            /*
            * IF FAILED
            * error for elevation is "error=740".
            * In other words, that means the software requires to be run as administrator
            * So what we'll do is to ask the user permission to run it as administrator
            * */
            if (e.getMessage() != null && e.getMessage().contains("error=740")) {
                // if the code matches, we try again
                System.out.println("Standard execution failed. Requesting elevation...");
                String command = "Start-Process -FilePath '" + path + "' -Verb RunAs";
                new ProcessBuilder("powershell.exe", "-Command", command).start();
                System.out.println("(Elevated) Executing: " + label);
            } else {
                // Error for other reason
                throw e;
            }
        }
    }

}
