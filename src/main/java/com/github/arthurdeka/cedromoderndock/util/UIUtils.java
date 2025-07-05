package com.github.arthurdeka.cedromoderndock.util;

import com.github.arthurdeka.cedromoderndock.App;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Classe de utilidade para operações relacionadas à Interface de Usuário (UI).
 */
public final class UIUtils {

    // Construtor privado para impedir a instanciação.
    private UIUtils() {}

    /**
     * Define o ícone padrão da aplicação para um determinado Stage.
     * @param stage A janela (Stage) na qual o ícone será aplicado.
     */
    public static void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(App.class.getResourceAsStream("/com/github/arthurdeka/cedromoderndock/icons/cedro/logo_256.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            // Note que sua classe Logger não está importada aqui, você precisaria adicioná-la
            // Logger.error("Failed to load application icon.", e);
            System.err.println("Failed to load application icon: " + e.getMessage());
        }
    }
}