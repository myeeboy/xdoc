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

import com.hg.util.StrUtil;

public class Heading {
	public ElePara para;
	public Heading(ElePara para) {
        this.para = para;
    }
    public String name() {
    	String str = this.para.toString().trim();
        if (str.indexOf('\n') >= 0) {
            str = StrUtil.replaceAll(str, "\n", "");
        }
        if (str.indexOf('\r') >= 0) {
            str = StrUtil.replaceAll(str, "\r", "");
        }
        return str;
    }
    public int level() {
    	return this.para.heading;
    }
    public int page;
    public int x;
    public int y;
    public ArrayList cheads;
}
