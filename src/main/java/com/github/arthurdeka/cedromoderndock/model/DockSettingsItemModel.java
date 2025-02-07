package com.github.arthurdeka.cedromoderndock.model;

public class DockSettingsItemModel implements DockItem{

    private String label = "Settings";
    private String iconPath = "/com/github/arthurdeka/cedromoderndock/icons/settings.png";;


    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getIconPath() {
        return iconPath;
    }

    @Override
    public void setLabel(String label) {

    }

    @Override
    public void setIconPath(String iconPath) {

    }

    @Override
    public void performAction() {
        System.out.println("Abrindo janela de configurações...");
    }
}
