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
 * 段落
 * @author xdoc
 */
public class Para extends Container {
    /**
     * 段落
     */
    public Para() {
        super("para");
    }
    /**
     * 段落
     * @param comp
     */
    public Para(Component comp) {
        this();
        this.add(comp);
    }
    /**
     * 段落
     * @param comp
     */
    public Para(String text) {
        this(new Text(text));
    }
    /**
     * 段落
     * @param comp
     */
    public Para(String text, int heading) {
        this();
        Text txt = new Text(text);
        txt.setBold(heading > 0);
        txt.setFontSize((heading == 0 || heading > 7) ? 14 : (28 - heading * 2));
        int spacing = (heading > 7 ? 4 : (20 - heading * 2));
        this.setHeading(heading);
        if (heading == 0) spacing = 0;
        this.setLineSpacing(spacing);
        this.add(txt);
    }
    /**
     * 左对齐
     */
    public static final String ALIGN_LEFT = "left";
    /**
     * 右对齐
     */
    public static final String ALIGN_RIGHT = "right";
    /**
     * 居中对齐
     */
    public static final String ALIGN_CENTER = "center";
    /**
     * 设置对齐
     * @param align 对齐
     */
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }
    /**
     * 设置换页
     * @param breakPage 换页
     */
    public void setBreakPage(boolean breakPage) {
        this.setAttribute("breakPage", breakPage);
    }
    /**
     * 设置行间距
     * @param lineSpacing 行间距
     */
    public void setLineSpacing(int lineSpacing) {
        this.setAttribute("lineSpacing", lineSpacing);
    }
    /**
     * 设置标题
     * @param heading 标题，0表示正文
     */
    public void setHeading(int heading) {
        this.setAttribute("heading", heading);
    }
    /**
     * 设置背景色
     * @param color 背景色
     */
    public void setBackColor(Color color) {
        this.setAttribute("backColor", Color.toString(color));
    }
    /**
     * 设置缩进
     * @param indent 缩进
     */
    public void setIndent(int indent) {
        this.setAttribute("indent", indent);
    }
    /**
     * 前缀：实心圆
     */
    public static final String PREFIX_CIRCLE = "●";
    /**
     * 前缀：空心圆
     */
    public static final String PREFIX_CIRCLE_BLANK = "●";
    /**
     * 前缀：实心方框
     */
    public static final String PREFIX_RECT = "■";
    /**
     * 前缀：空心方框
     */
    public static final String PREFIX_RECT_BLANK = "□";
    /**
     * 前缀
     * @param prefix 前缀
     */
    public void setPrefix(String prefix) {
        this.setAttribute("prefix", prefix);
    }
}
