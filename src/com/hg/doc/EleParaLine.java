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

import java.util.ArrayList;
import java.util.HashMap;

import com.hg.util.MapUtil;

public class EleParaLine extends ElePara {
	public int width;
    public int height;
    public int offset;
    public boolean vertical;
    public EleParaLine(XDoc xdoc, HashMap attMap) {
		super(xdoc, attMap);
	}
    public EleParaLine(DocPageLine line) {
    	super(line.para.xdoc, line.para.getAttMap());
        this.width = line.width;
        this.height = line.height;
        this.offset = line.offset;
        this.vertical = line.vertical;
        this.eleList = line.eleList;
	}
	protected void init() {
        super.init();
        this.typeName = "pline";
        this.width = 0;
        this.height = XFont.defaultFontSize;
        this.offset = 0;
        this.vertical = false;
    }
    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("width", String.valueOf(this.width));
        map.put("height", String.valueOf(this.height));
        map.put("offset", String.valueOf(this.offset));
        map.put("vertical", String.valueOf(this.vertical));
        map.put("lineSpacing", "0");
        map.put("heading", "0");
        map.put("align", DocConst.ALIGN_LEFT);
        map.put("prefix", "");
        map.put("breakPage", "false");
        return map;
    }
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.width = MapUtil.getInt(map, "width", this.width);
        this.height = MapUtil.getInt(map, "height", this.height);
        this.offset = MapUtil.getInt(map, "offset", this.offset);
        this.vertical = MapUtil.getBool(map, "vertical", this.vertical);
    }
    public ArrayList toLineList(int top, int width, ArrayList hrList, boolean h) {
    	this.layWidth = width - this.indentLeft;
    	ArrayList lineList = new ArrayList();
    	DocPageLine line = new DocPageLine(this, this.eleList, this.height, this.width, this.vertical);
    	line.offset = this.offset;
    	lineList.add(line);
    	return lineList;
    }
}
