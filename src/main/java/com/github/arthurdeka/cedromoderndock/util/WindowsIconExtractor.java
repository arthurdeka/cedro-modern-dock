package com.github.arthurdeka.cedromoderndock.util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.IntByReference;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

public class WindowsIconExtractor {

    /*
    *       Just a note: I don't really know what is going on here
    *       Claude wrote that, if something needs to be changed he is the responsible
    *
     */


    public static Image getExeIcon(String exePath) {
        try {
            File file = new File(exePath);
            if (!file.exists()) {
                return null;
            }

            // Get system icon for the file
            Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);

            // Convert Icon to BufferedImage
            BufferedImage bufferedImage = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );
            icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);

            // Convert BufferedImage to JavaFX Image
            return convertToFxImage(bufferedImage);
        } catch (Exception e) {
            System.out.println("Error trying to getExeIcon");
            e.printStackTrace();
            return null;
        }
    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage writableImage = new WritableImage(
                image.getWidth(),
                image.getHeight()
        );

        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pixelWriter.setArgb(x, y, image.getRGB(x, y));
            }
        }

        return writableImage;
    }
}