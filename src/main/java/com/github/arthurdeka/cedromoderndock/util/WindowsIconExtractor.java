package com.github.arthurdeka.cedromoderndock.util;

import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    public static Image getExeIcon(String exePath) {
        Image cached = CACHE.get(exePath);
        if (cached != null) return cached;

        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return null;
        }

        try {
            Path pngTemp = Files.createTempFile("dockIcon-", ".png");
            pngTemp.toFile().deleteOnExit();

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

            Process proc = new ProcessBuilder(
                    "powershell", "-NoProfile", "-Command", psScript)
                    .redirectErrorStream(true)
                    .start();

            // --- debug logging ---
            StringBuilder psOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    psOutput.append(line).append(System.lineSeparator());
                }
            }

            proc.waitFor(3, TimeUnit.SECONDS);

            // --- debug log ---
            if (psOutput.length() > 0) {
                Logger.info("[PowerShell Output] " + psOutput);
            }

            if (Files.size(pngTemp) == 0) {
                Logger.error("Temp file for " + exePath + " is empty. PowerShell likely failed.");
                return null;
            }

            Image fxImg = new Image(pngTemp.toUri().toString(), 0, 0, true, true);
            CACHE.put(exePath, fxImg);
            return fxImg;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Logger.error("WindowsIconExtractor error: " + e.getMessage());
            return null;
        }
    }
}