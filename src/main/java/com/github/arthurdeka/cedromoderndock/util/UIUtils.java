package com.github.arthurdeka.cedromoderndock.util;

import com.github.arthurdeka.cedromoderndock.App;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Utility class to UI related operations
 */
public final class UIUtils {

    // Construtor privado para impedir a instanciação.
    private UIUtils() {}

    /**
     * Defines a standard app icon to a stage.
     * @param stage The stage which the icon will be applied to
     */
    public static void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(App.class.getResourceAsStream("/com/github/arthurdeka/cedromoderndock/icons/cedro/logo_256.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            Logger.error("Failed to load application icon: " + e.getMessage());
        }
    }
}