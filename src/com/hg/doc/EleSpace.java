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
import java.util.HashMap;

public class EleSpace extends EleRect {
    public EleSpace(XDoc xdoc, int width, int height) {
        super(xdoc);
        this.width = width;
        this.height = height;
    }
    /**
	 * @param xdoc
	 * @param attMap
	 */
	public EleSpace(XDoc xdoc, HashMap attMap) {
		super(xdoc, attMap);
	}
	protected void init() {
        super.init();
        this.typeName = "space";
        this.name = "";
        this.color = null;
        this.fillColor = null;
        this.width = 1;
        this.height = 1;
    }
    public void print(Graphics2D g) {}
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
