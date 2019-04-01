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
 * 纸张
 * @author xdoc
 */
public class Paper extends Component {
    /**
     * 纸张
     */
    public Paper() {
        super("paper");
        this.setSize(793, 1122);
    }
    /**
     * 设置大小
     * @param size
     */
    public void setSize(String size) {
    	this.setAttribute("size", size);
    }
    /**
     * 获取大小
     */
    public String getSize(String size) {
    	return this.getAttribute("size");
    }
    /**
     * 设置大小
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }
    /**
     * 设置宽度
     * @param width
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
     * 设置外边距
     * @param margin
     */
    public void setMargin(int margin) {
        this.setLeftMargin(margin);
        this.setRightMargin(margin);
        this.setTopMargin(margin);
        this.setBottonMargin(margin);
    }
    /**
     * 设置左边距
     * @param margin
     */
    public void setLeftMargin(int margin) {
        this.setAttribute("leftMargin", margin);
    }
    /**
     * 设置右边距
     * @param margin
     */
    public void setRightMargin(int margin) {
        this.setAttribute("rightMargin", margin);
    }
    /**
     * 设置上边距
     * @param margin
     */
    public void setTopMargin(int margin) {
        this.setAttribute("topMargin", margin);
    }
    /**
     * 设置下边距
     * @param margin
     */
    public void setBottonMargin(int margin) {
        this.setAttribute("bottonMargin", margin);
    }
    /**
     * 获取宽度
     * @return
     */
    public int getWidth() {
        return this.getAttribute("width", 24);
    }
    /**
     * 获取高度
     * @return
     */
    public int getHeight() {
        return this.getAttribute("height", 96);
    }
}
