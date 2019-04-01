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

import com.hg.data.RowSet;
import com.hg.util.HgException;
import com.hg.util.MapUtil;

public class EleCell extends EleRect {
    private static final String INNER_RECT = "$innerRect$";
	private EleRect innerRect = null;
    public EleCell(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
        if (attMap.containsKey(INNER_RECT)) { //特殊处理深度克隆问题
            try {
                this.setRect((EleRect) EleBase.deepClone(xdoc, (EleRect) attMap.get(INNER_RECT)));
            } catch (Exception e) {}
        }
    }
    public void autoSize() {
        if (this.innerRect==null) {
            super.autoSize();
        } else {
            this.innerRect.autoSize();
            this.width = this.innerRect.width;
            this.height = this.innerRect.height;
        }
    }
    public EleCell(XDoc xdoc) {
        super(xdoc);
    }
    public EleCell(EleRect innerRect) {
        super(innerRect.xdoc);
        if (innerRect instanceof EleCell) {
            this.setAttMap(innerRect.getAttMap());
            this.eleList.addAll(innerRect.eleList);
        } else {
            this.innerRect = innerRect;
            this.typeName = innerRect.typeName;
            this.row = innerRect.row;
            this.rowSpan = innerRect.rowSpan;
            this.col = innerRect.col;
            this.colSpan = innerRect.colSpan;
            this.width = innerRect.width;
            this.height = innerRect.height;
        }
    }
    public void setRect(EleRect rect) {
        if (rect != this) {
            if (rect instanceof EleCell) {
                this.typeName = "cell";
                this.innerRect = null;
                this.setAttMap(rect.getAttMap());
                this.eleList.clear();
                this.eleList.addAll(rect.eleList);
            } else {
                this.typeName = rect.typeName;
                this.innerRect = rect;
                this.eleList.clear();
                rect.left = 0;
                rect.top = 0;
                rect.width = this.width;
                rect.height = this.height;
                rect.col = this.col;
                rect.colSpan = this.colSpan;
                rect.row = this.row;
                rect.rowSpan = this.rowSpan;
            }
        }
    }
    public EleRect getRect() {
        if (this.innerRect == null) {
            return this;
        } else {
            this.innerRect.col = this.col;
            this.innerRect.row = this.row;
            this.innerRect.colSpan = this.colSpan;
            this.innerRect.rowSpan = this.rowSpan;
            return this.innerRect;
        }
    }
    public void setBounds(int x, int y, int w, int h) {
        this.left = x;
        this.top = y;
        this.width = w;
        this.height = h;
        if (this.innerRect != null) {
            this.innerRect.left = x;
            this.innerRect.top = y;
            this.innerRect.width = w;
            this.innerRect.height = h;
        }
    }
    public int rowCount;
    protected void init() {
        super.init();
        typeName = "cell";
        this.rowSpan = 1;
        this.colSpan = 1;
        this.sql = "";
        this.connName = "";
        this.data = "";
        this.rowCount = 0;
    }
    public String data;
    public String connName;
    public String sql;
    public int direction;
    /**
     * 结果单元格
     * 行数由左单元格决定和列数由上单元格决定
     * 如果有sql，每个值为list，否则为单元格
     */
    public Object[][] resCells;
    public void setAttMap(HashMap map) {
        if (this.innerRect==null) {
            super.setAttMap(map);
            this.connName = MapUtil.getString(map, "conn", this.connName);
            this.sql = MapUtil.getString(map, "sql", this.sql);
            this.data = MapUtil.getString(map, "data", this.data);
            this.rowCount = MapUtil.getInt(map, "rowCount", this.rowCount);
        } else {
            this.innerRect.setAttMap(map);
        }
    }

    public HashMap getAttMap() {
        HashMap map;
        if (this.innerRect==null) {
            map = super.getAttMap();
            map.put("conn", this.connName);
            map.put("sql", this.sql);
            map.put("data", this.data);
            map.put("rowCount", String.valueOf(this.rowCount));
        } else {
            this.innerRect.row = this.row;
            this.innerRect.rowSpan = this.rowSpan;
            this.innerRect.col = this.col;
            this.innerRect.colSpan = this.colSpan;
            map = this.innerRect.getAttMap();
            map.put(INNER_RECT, this.innerRect);
        }
        return map;
    }
    public Object clone() {
        if (this.innerRect != null) {
            this.innerRect.row = row;
            this.innerRect.col = col;
            try {
                return new EleCell((EleRect) EleBase.deepClone(this.innerRect.xdoc, this.innerRect));
            } catch (HgException e) {
                return new EleCell((EleRect) this.innerRect.clone());
            }
        } else {
            return new EleCell(this.xdoc, this.getAttMap());
        }
    }
    public int i = 0;
    public RowSet[][] rowSets;
    /**
     * 重新排版
     */
    public void relayout() {
        this.getRect().lineList = null;
    }
    protected EleBase copyEle(XDoc xdoc) {
        return new EleCell(xdoc, this.getAttMap());
    }
    public int rowIndex = 0;
    public int colIndex = 0;
    public boolean bSelect;
    public EleCell belong;
    public void print(Graphics2D g) {
        if (this.innerRect == null) {
            super.print(g);
        } else {
            this.innerRect.width = this.width;
            this.innerRect.height = this.height;
            this.innerRect.print(g);
        }
    }
    public String toString() {
        return this.innerRect == null ? super.toString() : this.innerRect.toString();
    }
	public boolean isLoop() {
		return this.sql.length() > 0 || this.data.length() > 0;
	}
}
