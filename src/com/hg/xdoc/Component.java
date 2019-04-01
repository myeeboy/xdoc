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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 元件
 * @author xdoc
 */
public class Component {
    /**
     * 标签
     */
    private String tagName;
    /**
     * 构造器
     * @param tagName
     */
    protected Component(String tagName) {
        this.tagName = tagName;
    }
    /**
     * 获取属性
     * @param name
     * @return
     */
    public String getAttribute(String name) {
    	String val = (String) this.atts.get(name);
    	return val != null ? val : "";
    }
    /**
     * 获取属性
     * @param name
     * @param n
     */
    public int getAttribute(String name, int n) {
    	try {
    		return Integer.parseInt(this.getAttribute(name));
    	} catch (Exception e) {
    		return n;
    	}
    }
    /**
     * 获取属性
     * @param name
     * @param b
     */
    public boolean getAttribute(String name, boolean b) {
    	try {
    		return Boolean.valueOf(this.getAttribute(name)).booleanValue();
    	} catch (Exception e) {
    		return b;
    	}
    }
    /**
     * 获取属性
     * @param name
     * @param c
     */
    public Color getAttribute(String name, Color c) {
        if (this.atts.containsKey(name)) {
            return new Color(this.getAttribute(name));
        } else {
            return c;
        }
    }
    private String text;
    /**
     * 设置文本
     * @param text 文本
     */
    public void setText(String text) {
        this.text = text;
    }
    private Map atts = new HashMap();
    /**
     * 设置属性
     * @param name
     * @param value
     */
    public void setAttribute(String name, String value) {
    	this.atts.put(name, value);
    }
    /**
     * 设置属性
     * @param name
     * @param value
     */
    public void setAttribute(String name, boolean value) {
        this.setAttribute(name, String.valueOf(value));
    }
    /**
     * 设置属性
     * @param name
     * @param value
     */
    public void setAttribute(String name, int value) {
        this.setAttribute(name, String.valueOf(value));
    }
    /**
     * 转换为xml元素
     * @return
     */
    public Element toElement(Document doc) {
        Element ele = doc.createElement(this.tagName);
        Iterator it = atts.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String) it.next();
            ele.setAttribute(key, (String) this.atts.get(key));
        }
        if (this.text != null) {
            ele.setTextContent(this.text);
        }
        return ele;
    }
}
