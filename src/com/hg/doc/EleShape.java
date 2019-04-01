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

import java.util.HashMap;

public class EleShape extends EleRect {
	public EleShape(XDoc xdoc) {
		super(xdoc);
	}
	public EleShape(XDoc xdoc, HashMap attMap) {
		super(xdoc, attMap);
	}
    protected void init() {
    	super.init();
    	this.line = LINE_NONE;
        this.width = 120;
        this.height = 120;
    }
    protected boolean isLineShape() {
        return !this.line.equals(LINE_NONE);
	}
}
