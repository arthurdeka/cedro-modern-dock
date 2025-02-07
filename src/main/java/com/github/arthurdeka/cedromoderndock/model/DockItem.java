package com.github.arthurdeka.cedromoderndock.model;

public interface DockItem {

    String getLabel();
    String getIconPath();

    void setLabel(String label);
    void setIconPath(String iconPath);

    // Action to perform when object is clicked.
    void performAction();

}
