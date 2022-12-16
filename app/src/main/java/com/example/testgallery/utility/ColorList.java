package com.example.testgallery.utility;

import com.example.testgallery.App;
import com.example.testgallery.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avukelic on 25-Jul-18.
 */
public class ColorList {

    private List<ColorName> colors = new ArrayList<>();

    public ColorList() {
        colors.add(new ColorName("Black", 0x00, 0x00, 0x00));
        colors.add(new ColorName("White", 0xff, 0xff, 0xff));
        colors.add(new ColorName("Red", 0xFF, 0x00, 0x00));
        colors.add(new ColorName("Blue", 0x00, 0x00, 0xFF));
        colors.add(new ColorName("yellow", 0xFF, 0xFF, 0x00));
        colors.add(new ColorName("purple", 0xff, 0x00, 0xff));
        colors.add(new ColorName("Green", 0x00, 0x80, 0x00));
        colors.add(new ColorName("Orange", 0xFF, 0xA5, 0x00));
    }

    public List<ColorName> getColors() {
        return colors;
    }
}
