package com.github.arthurdeka.cedromoderndock.util;

import javafx.scene.paint.Color;

public class ColorManipulation {

    /*
    * This class exists to translate colors between the formats 0xAARRGGBB and RGB
    * Why? Because to apply the dock color, we use the RGB format
    * However, to get and set the color on the JavaFX ColorPicker, it will only accept 0xAARRGGBB
    *
    * if you know any better way to do this, I'm all ears
    *
    * */

    public static String fromRGBAtoRGB(String RGBAColor) {
        Color color = Color.web(RGBAColor);

        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);

        String RGBcolor = red + ", " + green + ", " + blue + ", ";

        return RGBcolor;
    }

    public static Color fromRGBtoRGBA(String RGBColor) {
        // the expected format is String RGBColor = "255, 255, 255, "

        //removing , and extra spaces
        String[] rgbValues = RGBColor.trim().split(",\\s*");

        int red = Integer.parseInt(rgbValues[0]);
        int green = Integer.parseInt(rgbValues[1]);
        int blue = Integer.parseInt(rgbValues[2]);

        // alpha is always 100%
        int alpha = 255;

        Color color = Color.rgb(red, green, blue);

        return color;
    }

}

