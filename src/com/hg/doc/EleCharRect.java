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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

public class EleCharRect extends EleRect {
    protected EleBase copyEle(XDoc xdoc) {
        return new EleCharRect(xdoc, this.getAttMap());
    }

    public EleCharRect(XDoc xdoc, HashMap attMap) {
    	this(xdoc);
    	this.setAttMap(attMap);
    }
    public EleChar eleChar;
    public EleCharRect(XDoc xdoc) {
        super(xdoc);
        this.eleChar = new EleChar(xdoc);
    }
    protected void init() {
        super.init();
        typeName = "char";
    }
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.eleChar.setAttMap(map);
        Rectangle2D bounds = this.eleChar.getBounds();
        this.width = (int) bounds.getWidth();
        this.height = (int) bounds.getHeight();
    }
    
    public HashMap getAttMap() {
    	HashMap map = super.getAttMap();
    	map.putAll(this.eleChar.getAttMap());
    	return map;
    }
    public Object clone() {
        return new EleCharRect(this.xdoc, this.getAttMap());
    }
    public void print(Graphics2D g) {
    	this.eleChar.print(g, 0, 0, this.eleChar.fontSize, 0);
    }
    public String toString() {
    	return this.eleChar.toString();
    }
}
