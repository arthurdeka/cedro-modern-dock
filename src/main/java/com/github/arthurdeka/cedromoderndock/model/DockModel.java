package com.github.arthurdeka.cedromoderndock.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DockModel {

    private List<DockItem> items = new ArrayList<>();
    private Integer iconsSize = 24;

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

}
