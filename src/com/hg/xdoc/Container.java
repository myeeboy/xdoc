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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 容器
 * @author xdoc
 */
public class Container extends Component {
    /**
     * 容器
     * @param tagName
     */
    protected Container(String tagName) {
        super(tagName);
    }
    /**
     * 组件列表
     */
    private List comps = new ArrayList();
    /**
     * 添加组件
     * @param comp
     */
    public void add(Component comp) {
        this.comps.add(comp);
    }
    /**
     * 获取组件列表
     * @return
     */
    public List getComps() {
        return this.comps;
    }
    /**
     * 转换为XML元素
     */
    public Element toElement(Document doc) {
        Element ele = super.toElement(doc);
        Component comp;
        for (int i = 0; i < this.comps.size(); i++) {
            comp = (Component) this.comps.get(i);
            ele.appendChild(comp.toElement(doc));
        }
        return ele;
    }
}
