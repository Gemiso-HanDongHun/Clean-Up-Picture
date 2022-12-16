package com.example.testgallery.models;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.palette.graphics.Palette;

import com.example.testgallery.App;
import com.example.testgallery.R;
import com.example.testgallery.utility.ColorList;
import com.example.testgallery.utility.ColorName;

public class Image {
    private String path;
    private String thumb;
    private String dateTaken;
    public String getPath() {
        return path;
    }
    private int resId;
    private Bitmap bitmap;
    private String color;
    private String hexadecimal;
    private String RGB;

    private int r, g, b;

    ColorList colors;

    public Image() {
        colors = new ColorList();
    }


    public void setPath(String path) {
        this.path = path;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Image(String path, String thumb) {
        this.path = path;
        this.thumb = thumb;
    }
    public int getResId() {
        return resId;
    }
    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }



    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getColor() {

        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHexadecimal() {
        return hexadecimal;
    }

    public void setHexadecimal(String hexadecimal) {
        this.hexadecimal = hexadecimal;
    }

    public String getRGB() {
        return RGB;
    }

    public void setRGB(String RGB) {
        this.RGB = RGB;
    }

    //Get RGB, Hexadecimal and Name value for dominant color
    public void getDominantColor() {
        Palette p = Palette.from(getBitmap()).generate();
        setHexadecimal(getHexadecimal(p.getDominantSwatch().getRgb()));
        setRGB(getRgbCode(p.getDominantSwatch().getRgb()));

        if (isGray(r, g, b))
            setColor("gray");
        else {
            setColor(getColorName(r, g, b));
        }
    }

    public void getDominantColor1() {
        Palette q = Palette.from(getBitmap()).generate();
        setHexadecimal(getHexadecimal(q.getDominantSwatch().getRgb()));
        setRGB(getRgbCode(q.getDominantSwatch().getRgb()));

        if (isGray(r, g, b))
            setColor("gray");
        else {
            setColor(getColorName(r, g, b));
        }
    }

    //Convert Hexadecimal value to RGB value
    private String getRgbCode(int intColor) {
        String color = String.format("#%06X", (0xFFFFFF & intColor));
        r = Integer.valueOf(color.substring(1, 3), 16);
        g = Integer.valueOf(color.substring(3, 5), 16);
        b = Integer.valueOf(color.substring(5, 7), 16);
        StringBuilder sb = new StringBuilder();
        sb.append("Red: ");
        sb.append(r);
        sb.append(" Green: ");
        sb.append(g);
        sb.append(" Blue: ");
        sb.append(b);
        return sb.toString();
    }

    //Get Hexadecimal
    private String getHexadecimal(int intColor) {
        return String.format("#%06X", (0xFFFFFF & intColor));
    }

    //Check if color is gray
    private boolean isGray(int r, int g, int b) {
        if (r == g && r == b) {
            return true;
        } else {
            return false;
        }
    }

    //If color is not gray check for other colors
    private String getColorName(int r, int g, int b) {
        ColorName closestMatch = null;
        int minMSE = Integer.MAX_VALUE;
        int mse;
        for (ColorName c : colors.getColors()) {
            System.out.println(c.getName());
            mse = c.computeMSE(r, g, b);
            System.out.println(mse);
            if (mse < minMSE) {
                minMSE = mse;
                closestMatch = c;
            }
        }
        if (closestMatch != null) {
            return closestMatch.getName();
        } else {
            return "No matched color name.";
        }
    }
}
