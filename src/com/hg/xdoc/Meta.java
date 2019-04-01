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
 * 元数据
 * @author xdoc
 */
public class Meta extends Component {
    /**
     * 元数据
     */
    public Meta() {
        super("meta");
    }
    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title) {
        this.setAttribute("title", title);
    }
    /**
     * 设置作者
     * @param author
     */
    public void setAuthor(String author) {
        this.setAttribute("author", author);
    }
    /**
     * 设置单位
     * @param org
     */
    public void setOrg(String org) {
        this.setAttribute("org", org);
    }
    /**
     * 设置描述
     * @param desc
     */
    public void setDesc(String desc) {
        this.setAttribute("desc", desc);
    }
    /**
     * 设置缩略图
     * @param thumb
     */
    public void setthumb(String thumb) {
        this.setAttribute("thumb", thumb);
    }
    /**
     * 视图：文字
     */
    public static final String VIEW_TEXT = "text";
    /**
     * 视图：表格
     */
    public static final String VIEW_TABLE = "table";
    /**
     * 视图：幻灯片
     */
    public static final String VIEW_SLIDE = "slide";
    /**
     * 视图：网页
     */
    public static final String VIEW_WEB = "web";
    /**
     * 设置视图
     * @param view
     */
    public void setView(String view) {
        this.setAttribute("view", view);
    }
}
