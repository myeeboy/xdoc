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
 * 文字
 * @author xdoc
 */
public class Text extends Component {
    /**
     * 文字
     */
    public Text() {
        super("text");
    }
    /**
     * 构造器
     * @param text 文本
     */
    public Text(String text) {
        this();
        this.setText(text);
    }
    /**
     * 设置字体
     * @param name 名称
     * @param size 大小
     */
    public void setFont(String name, int size) {
        this.setFontName(name);
        this.setFontSize(size);
    }
    /**
     * 设置字体名称
     * @param name 字体名称，内置：宋体、标宋、仿宋、楷体、行楷、隶书、黑体、魏碑
     */
    public void setFontName(String name) {
        this.setAttribute("fontName", name);
    }
    /**
     * 设置字体大小
     * @param size 字体大小
     */
    public void setFontSize(int size) {
        this.setAttribute("fontSize", String.valueOf(size));
    }
    /**
     * 设置字体颜色
     * @param color 颜色
     */
    public void setFontColor(Color color) {
        this.setAttribute("fontColor", Color.toString(color));
    }
    /**
     * 设置背景色
     * @param color 背景色
     */
    public void setBackColor(Color color) {
        this.setAttribute("backColor", Color.toString(color));
    }
    /**
     * 设置字体属性
     * @param style 属性，如：bold,italic
     */
    public void setFontStyle(String style) {
        this.setAttribute("fontStyle", style);
    }
    private void setStyle(String name, boolean value) {
        String style = this.getAttribute("fontStyle");
        String[] strs = style.split(",");
        boolean find = false;
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equals(name)) {
                if (value) {
                    return;
                } else {
                    find = true;
                    strs[i] = null;
                    break;
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strs.length; i++) {
            if (strs[i] != null) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(strs[i]);
            }
        }
        if (!find && value) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(name);
        }
        this.setFontStyle(sb.toString());
    }
    /**
     * 设置是否粗体
     * @param bold 是否粗体
     */
    public void setBold(boolean bold) {
        this.setStyle("bold", bold);
    }
    /**
     * 设置是否斜体
     * @param italic 是否斜体
     */
    public void setItalic(boolean italic) {
        this.setStyle("italic", italic);
    }
    /**
     * 设置是否带删除线
     * @param throughline 是否带删除线
     */
    public void setThroughline(boolean throughline) {
        this.setStyle("throughline", throughline);
    }
    /**
     * 设置是否显示阴影
     * @param shadow
     */
    public void setShadow(boolean shadow) {
        this.setStyle("shadow", shadow);
    }
    /**
     * 设置是否带圆圈
     * @param circle 是否带圆圈
     */
    public void setCircle(boolean circle) {
        this.setStyle("circle", circle);
    }
    /**
     * 设置是否带着重号
     * @param stress 是否带着重号
     */
    public void setStress(boolean stress) {
        this.setStyle("stress", stress);
    }
    /**
     * 设置是否描边
     * @param outline 是否描边
     */
    public void setOutline(boolean outline) {
        this.setStyle("outline", outline);
    }
    private void setUnderline(String underline) {
        String style = this.getAttribute("fontStyle");
        boolean find = false;
        String[] strs = style.split(",");
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].startsWith("underline")) {
                if (underline.length() > 0) {
                    strs[i] = underline;
                } else {
                    strs[i] = null;
                }
                find = true;
                break;
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strs.length; i++) {
            if (strs[i] != null) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(strs[i]);
            }
        }
        if (!find && underline.length() > 0) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(underline);
        }
        this.setFontStyle(sb.toString());
    
    }
    /**
     * 设置是否带下划线
     * @param underline 是否带下划线
     */
    public void setUnderline(boolean underline) {
        this.setUnderline(underline ? "underline" : "");
    }
    /**
     * 设置下划线
     * @param type 类型
     */
    public void setUnderline(int type) {
        setUnderline("underline" + type);
    }
    /**
     * 设置下划线
     * @param type 类型
     * @param color 颜色
     */
    public void setUnderline(int type, Color color) {
        setUnderline("underline" + type + "_" + color);
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
     * 设置超链接
     * @param href
     */
    public void setHref(String href) {
        this.setAttribute("href", href);
    }
    /**
     * 设置格式
     * @param format
     */
    public void setFormat(String format) {
        this.setAttribute("format", format);
    }
}
