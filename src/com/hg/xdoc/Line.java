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
 * 线
 * @author xdoc
 */
public class Line extends Rect {
    /**
     * 线
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public Line(int startX, int startY, int endX, int endY) {
        super("line");
        this.setStart(startX, startY);
        this.setEnd(endX, endY);
    }
    private void setEnd(int endX, int endY) {
        this.setAttribute("endX", String.valueOf(endX));
        this.setAttribute("endY", String.valueOf(endY));
    }
    private void setStart(int startX, int startY) {
        this.setAttribute("startX", String.valueOf(startX));
        this.setAttribute("startY", String.valueOf(startY));
    }
    /**
     * 箭头：线
     */
    public static final String ARROW_LINE = "none";
    /**
     * 箭头：斜线
     */
    public static final String ARROW_BIAS = "bias";
    /**
     * 箭头：三角
     */
    public static final String ARROW_TRIANGLE = "triangle";
    /**
     * 箭头：圆
     */
    public static final String ARROW_CIRCLE = "circle";
    /**
     * 箭头：菱形
     */
    public static final String ARROW_RHOMBUS = "rhombus";
    /**
     * 箭头：方框
     */
    public static final String ARROW_RECT = "rect";
    /**
     * 设置开始箭头
     * @param arrow
     */
    public void setStartArrow(int arrow) {
        this.setAttribute("startArrow", arrow);
    }
    /**
     * 设置结束箭头
     * @param arrow
     */
    public void setEndArrow(int arrow) {
        this.setAttribute("endArrow", arrow);
    }
    /**
     * 直线
     */
    public static String LINE_STYLE_LINE = "line";
    /**
     * 折线
     */
    public static String LINE_STYLE_BROKEN = "broken";
    /**
     * 弧
     */
    public static String LINE_STYLE_ARC = "arc";
    /**
     * 双弧
     */
    public static String LINE_STYLE_ARC2 = "arc2";
    /**
     * 设置线类型
     * @param style
     */
    public void setLineStyle(int style) {
        this.setAttribute("lineStyle", style);
    }
}
