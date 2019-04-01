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
package com.hg.doc;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hg.util.HgException;
import com.hg.util.MapUtil;

/**
 * 基本元素
 * @author whg
 */
public class EleBase implements Cloneable {
    /**
     * 元素文本内容属性名称
     */
    public static final String ELEMENT_TEXT = "TEXT";
    public XDoc xdoc;
    /**
     * 元素名称
     */
    public String name;
    /**
     * 子元素
     */
    public ArrayList eleList = new ArrayList();
    public Object clone() {
        return new EleBase(this.xdoc, this.getAttMap());
    }
    /**
     * 类型名称,用于标识对应xml元素名称
     */
    public String typeName;
    public EleBase(XDoc xdoc) {
        this.xdoc = xdoc;
        init();
    }
    public EleBase(XDoc xdoc, HashMap attMap) {
        this(xdoc);
        this.setAttMap(attMap);
    }
    protected void init() {
        this.name = "";
    }
    public void setXDoc(XDoc xdoc) {
        this.xdoc = xdoc;
        for (int i = 0; i < this.eleList.size(); i++) {
            ((EleBase) this.eleList.get(i)).setXDoc(xdoc);
        }
    }
	protected EleBase copyEle(XDoc xdoc) {
        return new EleBase(xdoc, this.getAttMap());
    }
    /**
     * 设置属性
     * @param key
     * @param object
     */
    public void setAttribute(String key, Object object) {
        HashMap map = new HashMap();
        map.put(key, object);
        this.setAttMap(map);
    }
    /**
     * 取得属性map,先取所有当前值,然后用属性map覆盖不确定的值
     * @return
     */
    public HashMap getAttMap() {
        HashMap map = new HashMap();
        map.put("name", this.name);
        return map;
    }
    /**
     * 设置属性map,是表达式放入表达式map中,否则从表达式map中去掉
     * @param map
     */
    public void setAttMap(HashMap map) {
        this.name = MapUtil.getString(map, "name", this.name);
    }
    /**
     * 取得属性值
     * @param key
     * @return
     */
    public Object getAttribute(String key) {
        return getAttMap().get(key);
    }
    /**
     * 取得属性值
     * @param key
     * @return
     */
    protected Object getAttribute(String key, Object defValue) {
        Map map = getAttMap();
        Object obj = map.get(MapUtil.igKey(map, key));
        return obj != null ? obj : defValue;
    }
    public String toString() {
        return this.name;
    }
    public static EleBase deepClone(XDoc doc, EleBase ele) throws HgException {
        try {
            Constructor cons = ele.getClass().getConstructor(
                    new Class[] { doc.getClass(), HashMap.class });
            EleBase cloneEle = (EleBase) cons.newInstance(new Object[] { doc,
                    ele.getAttMap() });
            for (int i = 0; i < ele.eleList.size(); i++) {
                cloneEle.eleList.add(deepClone(doc,
                        (EleBase) ele.eleList.get(i)));
            }
            return cloneEle;
        } catch (Exception e) {
            throw new HgException(e);
        }
    }
    protected EleBase resEle;
    public void setText(String text) {}
}
