package com.github.arthurdeka.cedromoderndock.model;

import com.github.arthurdeka.cedromoderndock.util.Logger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@NoArgsConstructor
public class DockWindowsModuleItemModel implements DockItem {

    private String label = "";
    private String iconPath = "";
    @Setter
    @Getter
    private String module = "";

    public DockWindowsModuleItemModel(String label, String module) {


        if (Objects.equals(module, "mypc")) {
            this.iconPath = "/com/github/arthurdeka/cedromoderndock/icons/my_computer.png";

        } else if (Objects.equals(module, "trash")) {
            this.iconPath = "/com/github/arthurdeka/cedromoderndock/icons/trash.png";

        } else if (Objects.equals(module, "ctrlpnl")) {
            this.iconPath = "/com/github/arthurdeka/cedromoderndock/icons/control.png";

        } else if (Objects.equals(module, "pconfig")) {
            this.iconPath = "/com/github/arthurdeka/cedromoderndock/icons/windows_settings.png";

        }

        this.label = label;
        this.module = module;
    }

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
        if (Objects.equals(module, "mypc")) {
            try {
                Process process1 = new ProcessBuilder(
                        "explorer.exe",
                        "::{20D04FE0-3AEA-1069-A2D8-08002B30309D}"
                ).start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if (Objects.equals(module, "trash")) {
            try {
                Process process2 = new ProcessBuilder(
                        "explorer.exe",
                        "::{645FF040-5081-101B-9F08-00AA002F954E}"
                ).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if (Objects.equals(module, "ctrlpnl")) {
            try {
                Process process3 = new ProcessBuilder(
                        "control.exe"
                ).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if (Objects.equals(module, "pconfig")) {
            try {
                Process process4 = new ProcessBuilder(
                        "cmd",
                        "/c",
                        "start",
                        "ms-settings:"
                ).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        Logger.info(label + " Clicked");

    }

}
