package com.github.arthurdeka.cedromoderndock.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DockSettingsItemModel implements DockItem{

    private String label = "Settings";
    private String iconPath = "/com/github/arthurdeka/cedromoderndock/icons/settings.png";


    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getPath() {
        return iconPath;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void setPath(String path) {
        this.iconPath = path;
    }

    @Override
    public void performAction() {
        System.out.println("Abrindo janela de configurações...");
    }
}
