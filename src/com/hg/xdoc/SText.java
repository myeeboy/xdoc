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
 * 图形文字
 * @author xdoc
 */
public class SText extends Rect {
    /**
     * 图形文字
     * @param text 
     */
    public SText(String text) {
        super("stext");
        this.setText(text);
    }
    /**
     * 设置文本
     * @param text 文本
     */
    public void setText(String text) {
        this.setAttribute("text", text);
    }
    /**
     * 设置字体名称
     * @param name 字体名称
     */
    public void setFontName(String name) {
        this.setAttribute("fontName", name);
    }
    /**
     * 设置是否粗体
     * @param bold 是否粗体
     */
    public void setBold(boolean bold) {
        this.setAttribute("bold", bold);
    }
    /**
     * 设置是否斜体
     * @param italic 是否斜体
     */
    public void setItalic(boolean italic) {
        this.setAttribute("italic", italic);
    }
    /**
     * 设置字间距
     * @param spacing 字间距:0~100
     */
    public void setSpacing(int spacing) {
        this.setAttribute("spacing", spacing);
    }
    /**
     * 设置格式
     * @param format
     */
    public void setFormat(String format) {
        this.setAttribute("format", format);
    }
}
