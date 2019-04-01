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

/**
 * 表格
 * @author xdoc
 */
public class Table extends Rect {
    private static final int DEF_COL_WIDTH = 96;
    private static final int DEF_ROW_HEIGHT = 24;
    private ArrayList rowList = new ArrayList();
    private ArrayList colList = new ArrayList();
    /**
     * 表格
     */
    public Table(int rows, int cols) {
        super("table");
        ensureSize(rowList, rows, DEF_ROW_HEIGHT);
        ensureSize(colList, cols, DEF_COL_WIDTH);
        this.setAttribute("rows", join(rowList));
        calHeight();
        this.setAttribute("cols", join(colList));
        calWidth();
    }
    /**
     * 设置行高
     * @param rows
     */
    public void setRowHeight(int row, int height) {
        ensureSize(rowList, row + 1, DEF_ROW_HEIGHT);
        rowList.set(row, String.valueOf(height));
        this.setAttribute("rows", join(rowList));
        calHeight();
    }
    private void ensureSize(ArrayList list, int size, int n) {
        if (list.size() < size) {
            for (int i = list.size(); i < size; i++) {
                list.add(String.valueOf(n));
            }
        }
    }
    /**
     * 设置列宽
     * @param width
     */
    public void setColWidth(int col, int width) {
        ensureSize(colList, col + 1, DEF_COL_WIDTH);
        colList.set(col, String.valueOf(width));
        this.setAttribute("cols", join(colList));
        calWidth();
    }
    /**
     * 设置表头行数
     * @param header
     */
    public void setHeader(int header) {
        this.setAttribute("header", header);
    }
    /**
     * 设置表尾行数
     * @param footer
     */
    public void setFooter(int footer) {
        this.setAttribute("footer", footer);
    }
    private void calWidth() {
        int n = 0;
        for (int i = 0; i < this.colList.size(); i++) {
            n += toInt(String.valueOf(this.colList.get(i)), DEF_COL_WIDTH);
        }
        this.setWidth(n);
    }
    private int toInt(String str, int def) {
    	try {
    		return Integer.parseInt(str);
    	} catch (Exception e) {
    		return def;
    	}
	}
	private void calHeight() {
        int n = 0;
        for (int i = 0; i < this.rowList.size(); i++) {
            n += toInt(String.valueOf(this.rowList.get(i)), DEF_ROW_HEIGHT);
        }
        this.setHeight(n);
    }
    private String join(ArrayList ns) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ns.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(ns.get(i));
        }
        return sb.toString();
    }
    /**
     * 添加单元格
     * @param comp
     * @param row
     * @param col
     */
    public void add(Component comp, int row, int col) {
        this.add(comp, row, col, 1, 1);
    }
    /**
     * 添加单元格
     * @param comp
     * @param row
     * @param col
     * @param rowSpan
     * @param colSpan
     */
    public void add(Component comp, int row, int col, int rowSpan, int colSpan) {
        if (comp instanceof Text || comp instanceof Para) {
            Rect rect = new Rect();
            rect.add(comp);
            comp = rect;
        }
        comp.setAttribute("row", row + 1);
        comp.setAttribute("col", col + 1);
        comp.setAttribute("rowSpan", rowSpan);
        comp.setAttribute("colSpan", colSpan);
        super.add(comp);
    }
}
