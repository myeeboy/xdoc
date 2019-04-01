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

import com.hg.util.To;


public class Meta {
    private XDoc xdoc;
	public static final String[] metaNames = new String[] {"Id", "Title", "Author", "Org", "Desc", "View", "Action", "PrintBack", "CreateDate", "ModifyDate"};
	public static final String[] metas = new String[] {"id", "title", "author", "org", "desc", "view", "action", "printBack", "createDate", "modifyDate"};
    public Meta(XDoc xdoc) {
        this.xdoc = xdoc;
    }
    public String getTitle() {
        return this.xdoc.getMeta("title");
    }
    public String getAuthor() {
        return this.xdoc.getMeta("author");
    }
    public String getRunTip() {
        return this.xdoc.getMeta("runTip");
    }
    public String getAction() {
        return this.xdoc.getMeta("action");
    }
    public String getDesc() {
        return this.xdoc.getMeta("desc");
    }
    public String getOrg() {
        return this.xdoc.getMeta("org");
    }
    public String getView() {
        return this.xdoc.getMeta("view");
    }
    public String getId() {
        return this.xdoc.getMeta("id");
    }
	public void setRunTip(String runTip) {
		this.xdoc.metaMap.put("runTip", runTip);
	}
	public double getScale() {
		return To.toDouble(this.xdoc.getMeta("scale", "1"), 1);
	}
}
