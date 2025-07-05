package com.github.arthurdeka.cedromoderndock.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DockProgramItemModel implements DockItem{

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
    }
}
