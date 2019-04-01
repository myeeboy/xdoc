/*
 * ===========================================================
 * XDOC mini : a free XML Document Engine
 * ===========================================================
 *
 * (C) Copyright 2004-2015, by WangHuigang.
 *
 * Project Info:  http://myxdoc.sohuapps.com
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 */
package com.hg.doc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ImgLib {
    private static HashMap map;
    private static String[] keys;
    static {
        try {
            keys = new String[120 + 46 + 48 + 40];
            map = new HashMap();
            int n = 0;
            for (int i = 1; i <= 120; i++) {
                keys[n] = "t" + i;
                map.put(keys[n], i + ".gif");
                n++;
            }
            for (int i = 1; i <= 46; i++) {
                keys[n] = "b" + i;
                map.put(keys[n], "b" + i + ".png");
                n++;
            }
            for (int i = 1; i <= 48; i++) {
                keys[n] = "p" + i;
                n++;
            }
            for (int i = 1; i <= 40; i++) {
                keys[n] = "f" + i;
                map.put(keys[n], "f" + i + ".gif");
                n++;
            }
        } catch (Throwable e) {
            keys = new String[] {};
        }
    }
    private static byte[] patternSeed = { -86, 85, -86, 85, -86, 85, -86, 85,
        -69, -18, -69, -18, -69, -18, -69, -18, 34, -120, 34, -120, 34,
        -120, 34, -120, 0, -1, -1, 0, 0, -1, -1, 0, 102, 102, 102, 102,
        102, 102, 102, 102, 56, 28, 14, 7, -125, -63, -32, 112, 7, 14, 28,
        56, 112, -32, -63, -125, 102, 102, -103, -103, 102, 102, -103,
        -103, -103, -1, 102, -1, -103, -1, 102, -1, 0, -1, 0, 0, 0, -1, 0,
        0, 68, 68, 68, 68, 68, 68, 68, 68, 17, -120, 68, 34, 17, -120, 68,
        34, 34, 68, -120, 17, 34, 68, -120, 17, -120, -120, -120, -1, -120,
        -120, -120, -1, 68, -86, 17, -86, 68, -86, 17, -86, 0, 34, 0, -120,
        0, 34, 0, -120, 0, -128, 0, 8, 0, -128, 0, 8, 0, 0, 0, -128, 0, 0,
        0, 8, 21, -86, 85, -86, 81, -86, 85, -86, -86, -35, -86, 119, -86,
        -35, -86, 119, -1, -18, -1, -69, -1, -18, -1, -69, -1, -3, -1, -33,
        -1, -3, -1, -33, -2, -1, -1, -1, -17, -1, -1, -1, -16, 15, 15, 15,
        15, -16, -16, -16, -128, -1, -128, -128, -128, -128, -128, -128, 0,
        -86, 0, 32, 0, 32, 0, 32, 16, -1, 1, 1, 1, -1, 16, 16, 64, -128, 1,
        3, -124, 72, 48, 32, -86, -86, -86, -86, -86, -86, -86, -86, 0, -1,
        0, -1, 0, -1, 0, -1, 8, -128, -128, -128, -128, 8, 8, 8, 0, 0, 0,
        -16, 0, 0, 0, 15, -103, -52, 102, 51, -103, -52, 102, 51, 102, -52,
        -103, 51, 102, -52, -103, 51, 17, 0, 0, 0, 0, -120, 68, 34, 0, 0,
        0, 17, 34, 68, -120, 0, 0, 16, 56, 124, -2, 124, 56, 16, 68, -126,
        1, -126, 68, 40, 16, 40, 0, -120, 0, 2, 0, -120, 0, 32, -40, 27, 3,
        48, -79, -115, 12, -64, 16, 2, 64, 8, -128, 4, 32, 1, 72, -124, 3,
        48, 72, -124, 3, 48, 32, 64, 32, 0, 4, 2, 4, 0, 24, 0, 3, -92, 24,
        0, 3, -92, 12, 3, -128, 64, 64, -64, 33, 18, -126, 68, 42, 17,
        -118, 68, -88, 17, 30, 85, -86, 85, -86, 30, 30, 30, 62, -35, 98,
        -29, -29, -35, 38, 62 };
    public static BufferedImage createPattern(int n) {
        if ((n < 0) || (n > 47))
            n = 0;
        BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
        int m = 8 * n;
        int black = Color.BLACK.getRGB();
        int white = Color.WHITE.getRGB();
        for (int i = 0; i < 8; ++i) {
            int n1 = patternSeed[(m + i)];
            int n2 = 7;
            for (int n3 = 0; n3 < 8; ++n3)
                img.setRGB(n2--, i, ((n1 & 1 << n3) != 0) ? black : white);
        }
        return img;
    }
    public static String[] getImgs() {
        return keys;
    }
    public static BufferedImage getImg(String key) {
        BufferedImage img = null;
        if (key.startsWith("p")) {
            img = createPattern(Integer.parseInt(key.substring(1)) - 1);
        }
        return img;
    }
}
