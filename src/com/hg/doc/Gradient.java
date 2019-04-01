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
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;

public class Gradient {
    public static Paint getPaint(Rectangle rect, int type, int trans, Color c1, Color c2) {
        Paint paint = null;
        if (type == 0) {
            if (trans == 0) {//上下
                paint = new GradientPaint(rect.y, (int) rect.height + rect.y, c1, (int) rect.width + rect.x, (int) rect.height + rect.y, c2, true);
            } else if (trans == 1) {
                paint = new GradientPaint(rect.y, (int) rect.height + rect.y, c2, (int) rect.width + rect.x, (int) rect.height + rect.y, c1, true);
            } else if (trans == 2) {
                paint = new GradientPaint(rect.y, (int) (rect.height / 2 + rect.y), c1, (int) (rect.width / 2 + rect.x), (int) (rect.height / 2 + rect.y), c2, true);
            } else if (trans == 3) {
                paint = new GradientPaint(rect.y, (int) (rect.height / 2 + rect.y), c2, (int) (rect.width / 2 + rect.x), (int) (rect.height / 2 + rect.y), c1, true);
            }
        } else if (type == 1) {//横向
            if (trans == 0) {
                paint = new GradientPaint((int) (rect.width / 2 + rect.x), rect.x, c1, (int) (rect.width / 2 + rect.x), (int) rect.height + rect.y, c2, true);
            } else if (trans == 1) {
                paint = new GradientPaint((int) (rect.width / 2 + rect.x), rect.x, c2, (int) (rect.width / 2 + rect.x), (int) rect.height + rect.y, c1, true);
            } else if (trans == 2) {
                paint = new GradientPaint((int) (rect.width / 2 + rect.x), rect.x, c1, (int) (rect.width / 2 + rect.x), (int) (rect.height / 2 + rect.y), c2, true);
            } else if (trans == 3) {
                paint = new GradientPaint((int) (rect.width / 2 + rect.x), rect.x, c2, (int) (rect.width / 2 + rect.x), (int) (rect.height / 2 + rect.y), c1, true);
            }
        } else if (type == 2) {
            if (trans == 0) {
                paint = new GradientPaint(rect.x, rect.y, c1, (int) rect.width + rect.x, (int) rect.height + rect.y, c2, true);
            } else if (trans == 1) {
                paint = new GradientPaint(rect.x, rect.y, c2, (int) rect.width + rect.x, (int) rect.height + rect.y, c1, true);
            } else if (trans == 2) {
                paint = new GradientPaint(rect.x, rect.y, c1, (int) (rect.width / 2 + rect.x), (int) (rect.height / 2 + rect.y), c2, true);
            } else if (trans == 3) {
                paint = new GradientPaint(rect.x, rect.y, c2, (int) (rect.width / 2 + rect.x), (int) (rect.height / 2 + rect.y), c1, true);
            }
        } else if (type == 3) {
            if (trans == 0) {
                paint = new GradientPaint(rect.y, (int) rect.height + rect.y, c1, (int) rect.width + rect.x, 0, c2, true);
            } else if (trans == 1) {
                paint = new GradientPaint(rect.y, (int) rect.height + rect.y, c2, (int) rect.width + rect.x, 0, c1, true);
            } else if (trans == 2) {
                paint = new GradientPaint(rect.y, (int) rect.height + rect.y, c1, (int) (rect.width / 2 + rect.x), (int) (rect.height / 2 + rect.y), c2, true);
            } else if (trans == 3) {
                paint = new GradientPaint(rect.y, (int) rect.height + rect.y, c2, (int) (rect.width / 2 + rect.x), (int) (rect.height / 2 + rect.y), c1, true);
            }
        }
        if (paint == null) {
            paint = c1;
        }
        return paint;
    }
}
