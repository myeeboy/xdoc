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
 * 圆弧
 * @author xdoc
 */
public class Arc extends Rect {
    /**
     * 椭圆
     */
    public Arc() {
        super("arc");
    }
    /**
     * 圆弧
     */
    public static String TYPE_OPEN = "open";
    /**
     * 弓形
     */
    public static String TYPE_CHORD = "chord";
    /**
     * 扇形
     */
    public static String TYPE_PIE = "pie";
    /**
     * 圆环
     */
    public static String TYPE_RING = "ring";
    /**
     * 设置弧类型
     * @param arcType
     */
    public void setType(String type) {
        this.setAttribute("type", type);
    }
    /**
     * 设置角度
     * @param start
     * @param extent
     */
    public void setAngle(int start, int extent) {
        this.setAttribute("start", start);
        this.setAttribute("extent", extent);
    }
}
