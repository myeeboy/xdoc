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
package com.hg.xdoc;

/**
 * 多边形
 * @author xdoc
 */
public class Polygon extends Rect {
    /**
     * 多边形
     * @param n 边数
     */
    public Polygon(int n) {
        super("polygon");
        this.setPoints(n);
    }
    /**
     * 设置边数
     * @param points 边数
     */
    public void setPoints(int points) {
        this.setAttribute("points", points);
    }
}
