package com.github.arthurdeka.cedromoderndock.util;

import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * Extrai o ícone de executáveis (.exe) em alta resolução
 * chamando a própria Shell do Windows (PowerShell +.NET).
 *
 * Zero dependências externas — só requer PowerShell, presente
 * desde o Windows 7.  O resultado é cacheado em memória para
 * evitar lançar o PowerShell toda vez.
 */
public final class WindowsIconExtractor {

    private WindowsIconExtractor() {}           // utilitário estático

    // cache simples [pathExe → Image]; thread-safe
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    /**
     * @param exePath caminho completo do executável.
     * @return ícone em JavaFX Image ou {@code null} se falhar/fora do Windows.
     */
    public static Image getExeIcon(String exePath) {

        /* 0) Checa cache --------------------------------------------------- */
        Image cached = CACHE.get(exePath);
        if (cached != null) return cached;

        /* 1) Garante que estamos no Windows -------------------------------- */
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return null;
        }

        try {
            /* 2) Arquivo PNG temporário onde o PowerShell gravará o ícone */
            Path pngTemp = Files.createTempFile("dockIcon-", ".png");
            pngTemp.toFile().deleteOnExit();

            /* 3) Script PowerShell (.NET) — salva o maior ícone disponível   */
            String psScript = String.join(";", List.of(
                    "$exe  = '" + exePath.replace("'", "''") + "'",
                    "$out  = '" + pngTemp.toString().replace("'", "''") + "'",
                    "Add-Type -AssemblyName System.Drawing",
                    "$ico  = [System.Drawing.Icon]::ExtractAssociatedIcon($exe)",
                    "if ($ico) {",
                    "  $bmp = $ico.ToBitmap()",
                    "  $bmp.Save($out, [System.Drawing.Imaging.ImageFormat]::Png)",
                    "}"
            ));

            /* 4) Executa o PowerShell (timeout 3 s máx.) */
            Process proc = new ProcessBuilder(
                    "powershell", "-NoProfile", "-Command", psScript)
                    .redirectErrorStream(true)
                    .start();

            proc.waitFor(3, TimeUnit.SECONDS);

            if (Files.size(pngTemp) == 0) {      // ícone não salvo
                return null;
            }

            /* 5) Carrega o PNG no JavaFX e coloca em cache ----------------- */
            Image fxImg = new Image(pngTemp.toUri().toString(),
                    0, 0,    // requestedWidth/Height = 0 → full size
                    true,    // preserveRatio
                    true);   // smooth
            CACHE.put(exePath, fxImg);
            return fxImg;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Logger.error("WindowsIconExtractor error: " + e.getMessage());
            return null;
        }
    }
}
