package com.github.arthurdeka.cedromoderndock.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Extracts the icon from executables (.exe) and saves it to a persistent cache folder in AppData.
 * The extraction uses PowerShell and is only executed if the icon is not already in the cache.
 */
public final class WindowsIconHandler {

    private WindowsIconHandler() {
    } // utilitário estático

    // Diretório de cache persistente em %APPDATA%/CedroModernDock/iconsCache
    private static final Path CACHE_DIR = getCacheDirectory();


    /**
     * Get the cached icon path
     *
     * @param exePath complete path to the .exe. (take it from the value in the config.json file)
     * @return The path to the icon in the cache folder
     */
    public static Path getCachedIconPath(String exePath) {
        try {
            String fileName = getHashedFileName(exePath) + ".png";
            return CACHE_DIR.resolve(fileName);
        } catch (NoSuchAlgorithmException e) {
            Logger.error("getCachedIconPath - Failed to generate hashed file name for " + exePath + e);
            return null;
        }
    }


    /**
     * Extracts the icon from executables
     *
     * @param exePath complete path to the .exe.
     * @return The path to the icon in the cache folder
     */
    public static Path extractAndCacheIcon(String exePath) {
        try {
            // Generates a unique and deterministic filename based on the hash of the executable path
            String fileName = getHashedFileName(exePath) + ".png";
            Path cachedIconPath = CACHE_DIR.resolve(fileName);

            // If the icon already exists, return its path immediately.
            if (Files.exists(cachedIconPath)) {
                Logger.info("Icon for " + exePath + " found in cache.");
                return cachedIconPath;
            }

            String psScript = buildPowerShellScript(exePath, cachedIconPath.toString());

            Process proc = new ProcessBuilder("powershell", "-NoProfile", "-Command", psScript)
                    .redirectErrorStream(true)
                    .start();

            // possible error handling
            if (!proc.waitFor(3, TimeUnit.SECONDS) || Files.notExists(cachedIconPath) || Files.size(cachedIconPath) == 0) {
                Logger.error("Failed to extract icon for " + exePath + ". PowerShell likely failed or timed out.");
                Files.deleteIfExists(cachedIconPath); // Clean up empty or corrupted file
                return null;
            }

            Logger.info("[extractAndCacheIcon] Icon for " + exePath + " successfully extracted and cached at iconsCache");
            return cachedIconPath; // Return the path of the newly saved icon.

        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            Logger.error("WindowsIconExtractor error on extractAndCacheIcon: " + e.getMessage() + e);
            return null;
        }
    }

    /**
     * Builds the PowerShell script to extract and save the icon.
     */
    private static String buildPowerShellScript(String exePath, String outputPath) {
        return String.join(";", List.of(
                "$exe  = '" + exePath.replace("'", "''") + "'",
                "$out  = '" + outputPath.replace("'", "''") + "'",
                "Add-Type -AssemblyName System.Drawing",
                "$ico  = [System.Drawing.Icon]::ExtractAssociatedIcon($exe)",
                "if ($ico) {",
                "  $bmp = $ico.ToBitmap()",
                "  $bmp.Save($out, [System.Drawing.Imaging.ImageFormat]::Png)",
                "  $ico.Dispose()",
                "  $bmp.Dispose()",
                "}"
        ));
    }

    /**
     * Returns the icons cache directory (creating AppData/CedroModernDock/iconsCache if it does not exist)
     */
    private static Path getCacheDirectory() {
        try {
            String appData = System.getenv("APPDATA");
            if (appData == null || appData.isEmpty()) {
                Logger.error("AppData Not found.");
            }
            Path cacheDir = Paths.get(appData, "CedroModernDock", "iconsCache");
            Files.createDirectories(cacheDir); // Creates the folder structure if it doesn't exist
            return cacheDir;
        } catch (IOException e) {
            Logger.error("Failed to create icon cache directory." + e);
            throw new RuntimeException("Could not create cache directory.", e);
        }
    }


    /**
     * Generates a safe filename using a SHA-256 hash of the executable path.
     */
    private static String getHashedFileName(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, hash).toString(16);
    }

}