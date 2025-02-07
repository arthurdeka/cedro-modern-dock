package com.github.arthurdeka.cedromoderndock.model;

import java.util.ArrayList;
import java.util.List;

public class DockModel {

    private List<DockItem> items = new ArrayList<>();

    public List<DockItem> getItems() {
        return items;
    }

    public void addItem(DockItem item) {
        items.add(item);

    }

    public void removeItem(DockItem item) {
        items.add(item);

    }

    public void loadDefaultItems() {
        items.add(new DockSettingsItemModel());
    }

}
