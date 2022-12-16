package com.example.testgallery.utility;

import android.util.Log;

/**
 * Created by avukelic on 25-Jul-18.
 */
public class ColorName {
    private int r, g, b;
    private String name;

    public ColorName(String name, int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.name = name;
    }

    public int computeMSE(int pixR, int pixG, int pixB) {
        return  (((pixR - r) * (pixR - r) + (pixG - g) * (pixG - g) + (pixB - b)
                * (pixB - b)) / 3);
    }

    public String getName() {
        return name;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}
