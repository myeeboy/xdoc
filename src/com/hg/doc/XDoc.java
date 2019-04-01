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
import java.util.Date;
import java.util.HashMap;

import com.hg.util.MapUtil;
import com.hg.util.StrUtil;

/**
 * XDOC
 * @author whg
 */
public class XDoc {
    public XDoc() {
        this("");
    }
    public XDoc(String url) {
        this.url = url;
        this.paper = new DocPaper(this);
        this.backRect = new EleRect(this);
        this.backRect.color = null;
        this.frontRect = new EleRect(this);
        this.frontRect.color = null;
        this.bodyRect = new EleRect(this);
        this.bodyRect.color = null;
        this.bodyRect.txtPadding = 0;
        this.bodyRect.strokeWidth = 0;
        this.metaMap.put("id", StrUtil.uuid());
    }
    public static final String VIEW_TEXT = "text";
    public static final String VIEW_TABLE = "table";
    public static final String VIEW_PAGE = "page";
    public static final String VIEW_WEB = "web";
    public String getView() {
        String view = this.getMeta("view");
        if (view.length() == 0) {
            view = VIEW_TEXT;
        }
        return view;
    }
    public void setView(String view) {
        this.metaMap.put("view", view);
    }
    public DocPrint print;
    /**
     * url
     */
    public String url;
    /**
     * 缩放比例
     */
    public double scale = 1;
    public int intScale(int n) {
        return (int) (n * scale);
    }
    public int unintScale(int n) {
        return (int) (n / scale);
    }
    /**
     * 元数据map
     */
    public HashMap metaMap = new HashMap();
    /**
     * 段落列表
     */
    public ArrayList paraList = new ArrayList();
    /**
     * 图形列表
     */
    public ArrayList rectList = new ArrayList();
    public EleRect backRect;
    public EleRect frontRect;
    public EleRect bodyRect;
    private DocPaper paper;
    public DocPaper getPaper() {
        return this.paper;
    }
    public void setPaper(DocPaper docPaper) {
        this.paper.set(docPaper);
        if (docPaper.width == 0 && docPaper.height == 0) {
        	this.bodyRect.sizeType = EleRect.SIZE_AUTOSIZE;
        } else if (docPaper.width == 0) {
        	this.bodyRect.sizeType = EleRect.SIZE_AUTOWIDTH;
        } else if (docPaper.height == 0) {
        	this.bodyRect.sizeType = EleRect.SIZE_AUTOHEIGHT;
        }
    }
    public int pages = 1;
    public int page = 1;
    public String heading;
    public ArrayList heads = new ArrayList();
    /**
     * 创建时间
     */
    public Date createTime;
    
    public boolean runHead = false;
    public boolean runInput = false;
    public String getMeta(String name, String defValue) {
        return MapUtil.getString(this.metaMap, MapUtil.igKey(this.metaMap, name), defValue);
    }
    public String getMeta(String name) {
        return getMeta(name, "");
    }
    private Meta meta = new Meta(this);
    public Meta getMeta() {
        return this.meta;
    }
    public int getViewPage() {
        return MapUtil.getInt(this.metaMap, "page", this.page);
    }
    public int getViewPages() {
        return MapUtil.getInt(this.metaMap, "pages", this.pages);
    }
    public String getViewHeading() {
        return MapUtil.getString(this.metaMap, "heading", this.heading);
    }
    public boolean isPrintBack() {
        return !this.getMeta("printBack").equals("false");
    }
    public String getName() {
        return this.getMeta("title");
    }
    public void genId() {
        this.metaMap.put("id", StrUtil.uuid());
    }
	public boolean hasContent() {
		if (this.rectList.size() > 0 || this.paraList.size() > 1) {
			return true;
		}
		if (this.paraList.size() == 0) {
			return false;
		}
		ElePara para = (ElePara) this.paraList.get(0);
		if (para.eleList.size() > 1) {
			return true;
		} else if (para.eleList.size() == 0) {
			return false;
		} else if (para.eleList.get(0) instanceof EleText) {
			return ((EleText) para.eleList.get(0)).text.length() > 0;
		} else {
			return true;
		}
	}
}
