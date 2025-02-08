package com.github.arthurdeka.cedromoderndock.model;

public interface DockItem {

    String getLabel();
    String getPath();

    void setLabel(String label);
    void setPath(String path);

    // Action to perform when object is clicked.
    void performAction();

}
