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
 * 方框
 * @author xdoc
 */
public class Rect extends Container {
    /**
     * 方框
     * @param tagName
     */
    protected Rect(String tagName) {
        super(tagName);
    }
    /**
     * 方框
     */
    public Rect() {
        super("rect");
    }
    /**
     * 方框
     * @param width 宽度
     * @param height 高度
     */
    public Rect(int width, int height) {
        this();
        this.setSize(width, height);
    }
    /**
     * 方框
     * @param left 左
     * @param top 上
     * @param width 宽
     * @param height 高
     */
    public Rect(int left, int top, int width, int height) {
        this();
        this.setBounds(left, top, width, height);
    }
    /**
     * 设置左位置
     * @param left 左边
     */
    public void setLeft(int left) {
        this.setAttribute("left", left);
    }
    /**
     * 设置上位置
     * @param top 上边
     */
    public void setTop(int top) {
        this.setAttribute("top", top);
    }
    /**
     * 设置位置
     * @param left 左
     * @param top 上
     */
    public void setLocation(int left, int top) {
        this.setLeft(left);
        this.setTop(top);
    }
    /**
     * 设置宽度
     * @param width 宽度
     */
    public void setWidth(int width) {
        this.setAttribute("width", width);
    }
    /**
     * 设置高度
     * @param height
     */
    public void setHeight(int height) {
        this.setAttribute("height", height);
    }
    /**
     * 获取宽度
     * @return
     */
    public int getWidth() {
        return this.getAttribute("width", 96);
    }
    /**
     * 获取高度
     * @return
     */
    public int getHeight() {
        return this.getAttribute("height", 24);
    }
    /**
     * 设置大小
     * @param width 宽度
     * @param height 高度
     */
    public void setSize(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }
    /**
     * 设置位置与大小
     */
    public void setBounds(int left, int top, int width, int height) {
        this.setLocation(left, top);
        this.setSize(width, height);
    }
    /**
     * 线宽度
     * @param width
     */
    public void setStrokeWidth(int width) {
        this.setAttribute("strokeWidth", width);
    }
    /**
     * 线形
     * @param strokeStyle
     */
    public void setStrokeStyle(String strokeStyle) {
        this.setAttribute("strokeStyle", strokeStyle);
    }
    /**
     * 线图片
     * @param strokeImg
     */
    public void setStrokeImg(String strokeImg) {
        this.setAttribute("strokeImg", strokeImg);
    }
    /**
     * 设置边线
     * @param line 共8位，分别为：左、上、右、下、左上右下、右上左下、横中、竖中，1为显示，0为不显示，默认值：11110000
     */
    public void setLine(String line) {
        this.setAttribute("line", line);
    }
    /**
     * 设置角弧长
     * @param arc
     */
    public void setArc(int arc) {
        this.setAttribute("arc", arc);
    }
    /**
     * 设置旋转角度
     * @param rotate
     */
    public void setRotate(int rotate) {
        this.setAttribute("rotate", rotate);
    }
    /**
     * 设置图形旋转角度
     * @param srotate
     */
    public void setSRotate(int srotate) {
        this.setAttribute("srotate", srotate);
    }
    /**
     * 设置边框色
     * @param color
     */
    public void setColor(Color color) {
        this.setAttribute("color", Color.toString(color));
    }
    /**
     * 设置填充色
     * @param color
     */
    public void setFillColor(Color color) {
        this.setAttribute("fillColor", Color.toString(color));
    }
    /**
     * 设置渐变
     * @param type 值为0~16，0表示不渐变
     */
    public void setGradual(int type) {
        this.setAttribute("gradual", type);
    }
    /**
     * 设置渐变
     * @param type 值为0~16，0表示不渐变
     * @param clor
     */
    public void setGradual(int type, Color color) {
        this.setGradual(type);
        this.setAttribute("fillColor2", Color.toString(color));
    }
    /**
     * 设置填充图
     * @param img
     */
    public void setFillImg(String img) {
        this.setAttribute("fillImg", img);
    }
    /**
     * 设置滤镜
     * @param filter
     */
    public void setFilter(String filter) {
        this.setAttribute("filter", filter);
    }
    /**
     * 设置滤镜
     * @param filter
     * @param param
     */
    public void setFilter(String filter, String param) {
        this.setFilter(filter);
        this.setAttribute("filterParam", param);
    }
    /**
     * 设置提示
     * @param toolTip
     */
    public void setToolTip(String toolTip) {
        this.setAttribute("toolTip", toolTip);
    }
    /**
     * 设置超链接
     * @param href
     */
    public void setHref(String href) {
        this.setAttribute("href", href);
    }
    /**
     * 设置分栏
     * @param column 分栏
     */
    public void setColumn(int column) {
        this.setAttribute("column", column);
    }
    /**
     * 设置是否显示
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.setAttribute("visible", visible);
    }
    /**
     * 尺寸类型：正常
     */
    public static final String SIZE_TYPE_NORMAL = "normal";
    /**
     * 尺寸类型：自动宽度
     */
    public static final String SIZE_TYPE_AUTOWIDTH = "autowidth";
    /**
     * 尺寸类型：自动高度
     */
    public static final String SIZE_TYPE_AUTOHEIGHT = "autoheight";
    /**
     * 尺寸类型：自动大小
     */
    public static final String SIZE_TYPE_AUTOSIZE = "autosize";
    /**
     * 尺寸类型：自动字体
     */
    public static final String SIZE_TYPE_AUTOFONT = "autofont";
    /**
     * 尺寸类型：自动提示
     */
    public static final String SIZE_TYPE_AUTOTIP = "autotip";
    /**
     * 设置尺寸类型
     * @param sizeType
     */
    public void setSizeType(String sizeType) {
        this.setAttribute("sizeType", sizeType);
    }
    /**
     * 垂直对齐：顶部
     */
    public static final String VALIGN_TOP = "top";
    /**
     * 垂直对齐：居中
     */
    public static final String VALIGN_CENTER = "center";
    /**
     * 垂直对齐：底部
     */
    public static final String VALIGN_BOTTOM = "bottom";
    /**
     * 设置段落中垂直对齐方式
     * @param vAlign 段落中垂直对齐方式
     */
    public void setVAlign(String vAlign) {
        this.setAttribute("valign", vAlign);
    }
    /**
     * 停靠：左
     */
    public static final String DOCK_LEFT = "left";
    /**
     * 停靠：上
     */
    public static final String DOCK_TOP = "top";
    /**
     * 停靠：右
     */
    public static final String DOCK_RIGHT = "right";
    /**
     * 停靠：下
     */
    public static final String DOCK_BOTTOM = "bottom";
    /**
     * 停靠：左上
     */
    public static final String DOCK_LEFT_TOP = "lefttop";
    /**
     * 停靠：左下
     */
    public static final String DOCK_LEFT_BOTTOM = "leftbottom";
    /**
     * 停靠：右上
     */
    public static final String DOCK_RIGHT_TOP = "righttop";
    /**
     * 停靠：右下
     */
    public static final String DOCK_RIGHT_BOTTOM = "leftbottom";
    /**
     * 设置停靠
     * @param dock
     */
    public void setDock(String dock) {
        
    }
}
