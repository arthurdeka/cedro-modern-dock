package com.github.arthurdeka.cedromoderndock.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DockModel {

    private List<DockItem> items = new ArrayList<>();
    private int iconsSize = 24;
    private int spacingBetweenIcons = 0;
    private double dockTransparency = 0.7;
    private int dockBorderRounding = 10;
    private String dockColorRGB = "50, 50, 50 ";

    public List<DockItem> getItems() {
        return items;
    }

    public void addItem(DockItem item) {
        items.add(item);

    }

    public void removeItem(int index) {
        items.remove(index);

    }

    public void loadDefaultItems() {
        items.add(new DockSettingsItemModel());
    }

    public void swapItems(int firstItemIdx, int secondItemIdx) {
        Collections.swap(items, firstItemIdx, secondItemIdx);
    }

    public Integer getIconsSize() {
        return iconsSize;
    }

    public void setIconsSize(Integer iconsSize) {
        this.iconsSize = iconsSize;
    }

    public Integer getSpacingBetweenIcons() {
        return spacingBetweenIcons;
    }

    public void setSpacingBetweenIcons(Integer spacingBetweenIcons) {
        this.spacingBetweenIcons = spacingBetweenIcons;
    }

    public Double getDockTransparency() {
        return dockTransparency;
    }

    public void setDockTransparency(Double dockTransparency) {
        this.dockTransparency = dockTransparency;
    }

    public int getDockBorderRounding() {
        return dockBorderRounding;
    }

    public void setDockBorderRounding(int dockBorderRounding) {
        this.dockBorderRounding = dockBorderRounding;
    }

    public String getDockColorRGB() {
        return dockColorRGB;
    }

    public void setDockColorRGB(String dockColorRGB) {
        this.dockColorRGB = dockColorRGB;
    }

}
