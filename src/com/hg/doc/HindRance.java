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

import java.awt.Rectangle;
import java.util.ArrayList;

public class HindRance extends Rectangle {
    
    public HindRance(Rectangle rect) {
        super(rect);
    }
    public static boolean intersects(ArrayList hrList, Rectangle r) {        
        return false;
    }
    public static HindRance next(ArrayList hrList, int x, int y, int h) {
        HindRance hr = null, hr2;
        for (int i = 0; i < hrList.size(); i++) {
            hr2 = (HindRance) hrList.get(i);
            if (hr2.x >= x  && hr2.y < y + h && hr2.y + hr2.height > y && (hr == null || hr.x > hr2.x)) {
                hr = hr2;
            }
            if (hr2.y + hr2.height < y) {
                hrList.remove(i);
                i--;
            }
        }
        return hr;
    }
}
