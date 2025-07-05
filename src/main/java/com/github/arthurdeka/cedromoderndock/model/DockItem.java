package com.github.arthurdeka.cedromoderndock.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/*
* Those annotations are needed for the Serialization Library (Jackson) to work properly
* */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DockProgramItemModel.class, name = "programItem"),
        @JsonSubTypes.Type(value = DockWindowsModuleItemModel.class, name = "windowsModuleItem"),
        @JsonSubTypes.Type(value = DockSettingsItemModel.class, name = "settingsItem")
})
public interface DockItem {

    String getLabel();
    String getPath();

    void setLabel(String label);
    void setPath(String path);

    // Action to perform when object is clicked.
    void performAction();

}
