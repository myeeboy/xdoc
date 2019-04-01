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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

import com.hg.util.MapUtil;
import com.hg.util.StrUtil;
import com.hg.util.To;

public class EleTable extends EleRect {
    public EleTable(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public EleTable(XDoc xdoc) {
        super(xdoc);
    }
    public boolean input;
    public int header;
    public int footer;
    public String data;
    public String tarType;
    public int rowCount;
    protected void init() {
        super.init();
        typeName = "table";
        this.rows = "";
        this.cols = "";
        this.color = null;
        this.input = false;
        this.header = 0;
        this.footer = 0;
        this.data = "";
        this.rowCount = 0;
        this.sizeType = EleRect.SIZE_AUTOSIZE;
        this.tarType = "table";
    }
    public void setRC(int rs, int cs) {
        setRC(rs, cs, EleRect.DEF_HEIGHT, EleRect.DEF_WIDTH);
    }
    protected void ensureRC() {
        EleCell cell;
        int row = 1;
        int col = 1;
        for (int i = 0; i < this.eleList.size(); i++) {
        	if (this.eleList.get(i) instanceof EleCell) {
        		cell = (EleCell) this.eleList.get(i);
        		if (cell.row < 0) {
        			cell.row = 0;
        		}
        		if (cell.rowSpan <= 0) {
        			cell.rowSpan = 1;
        		}
        		if (cell.col < 0) {
        			cell.col = 0;
        		}
        		if (cell.colSpan <= 0) {
        			cell.colSpan = 1;
        		}
        		if (cell.col + cell.colSpan > col) {
        			col = cell.col + cell.colSpan;
        		}
        		if (cell.row + cell.rowSpan > row) {
        			row = cell.row + cell.rowSpan;
        		}
        	}
        }
        this.rows = ensure(this.rows, row, EleRect.DEF_HEIGHT);
        this.cols = ensure(this.cols, col, EleRect.DEF_WIDTH);
    }
    private String ensure(String str, int n, int def) {
        String[] strs = str.split(",");
        if (strs.length < n) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < n; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                if (i < strs.length && strs[i].length() > 0) {
                    sb.append(strs[i]);
                } else {
                    sb.append(def);
                }
            }
            return sb.toString();
        } else {
            return str;
        }
    }
    public void setRC(int rs, int cs, int h, int w) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rs; i++) {
            if (i > 0) sb.append(",");
            sb.append(h);
        }
        this.rows = sb.toString();
        sb.setLength(0);
        for (int i = 0; i < cs; i++) {
            if (i > 0) sb.append(",");
            sb.append(w);
        }
        this.cols = sb.toString();
    }
    public String rows;
    public String cols;
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.rows = MapUtil.getString(map, "rows", this.rows);
        this.cols = MapUtil.getString(map, "cols", this.cols);
        this.header = MapUtil.getInt(map, "header", this.header);
        this.footer = MapUtil.getInt(map, "footer", this.footer);
        this.input = MapUtil.getBool(map, "input", this.input);
        this.data = MapUtil.getString(map, "data", this.data);
        this.tarType = MapUtil.getString(map, "tarType", this.tarType);
        this.rowCount = MapUtil.getInt(map, "rowCount", this.rowCount);
    }

    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("rows", this.rows);
        map.put("cols", this.cols);
        map.put("header", String.valueOf(this.header));
        map.put("footer", String.valueOf(this.footer));
        map.put("input", String.valueOf(this.input));
        map.put("data", this.data);
        map.put("tarType", this.tarType);
        map.put("rowCount", String.valueOf(this.rowCount));
        return map;
    }
    public void autoSize() {
        if (!this.sizeType.equals(SIZE_NORMAL)) {
            ensureRC();
            String[] rowStrs = this.rows.split(",");
            String[] colStrs = this.cols.split(",");
            int[] rs = new int[rowStrs.length];
            int[] cs = new int[colStrs.length];
            for (int i = 0; i < cs.length; i++) {
                cs[i] = To.toInt(colStrs[i]);
            }
            for (int i = 0; i < rs.length; i++) {
                rs[i] = To.toInt(rowStrs[i]);
            }
            //填充二维数组单元格
            EleCell cell;
            for (int i = 0; i < this.eleList.size(); i++) {
                cell = (EleCell) this.eleList.get(i);
                if (cell.col < cs.length && cell.row < rs.length) {
                    cell.getRect().width = cs[cell.col];
                    if (cell.colSpan > 1) {
                        for (int j = 1; j < cell.colSpan && j < cs.length; j++) {
                            cell.getRect().width += cs[j];
                        }
                    }
                    cell.getRect().height = rs[cell.row];
                    if (cell.rowSpan > 1) {
                        for (int j = 1; j < cell.rowSpan && j < rs.length; j++) {
                            cell.getRect().height += rs[j];
                        }
                    }
                    cell.autoSize();
                }
            }
            if (this.sizeType.equals(SIZE_AUTOSIZE) || this.sizeType.equals(SIZE_AUTOWIDTH)) {
                for (int i = 0; i < cs.length; i++) {
                    cs[i] = 8;
                }
                for (int i = 0; i < this.eleList.size(); i++) {
                    cell = (EleCell) this.eleList.get(i);
                    if (cell.col < cs.length && cell.row < rs.length) {
                        if (cell.colSpan <= 1) {
                            cs[cell.col] = Math.max(cs[cell.col], cell.width);
                        }
                    }
                }
                StringBuffer sb = new StringBuffer();
                int n = 0;
                for (int i = 0; i < cs.length; i++) {
                    if (i > 0) sb.append(",");
                    sb.append(cs[i]);
                    n += cs[i];
                }
                this.width = n + (int) this.strokeWidth * 2 + this.margin * 2;
                this.cols = sb.toString();
            }
            if (this.sizeType.equals(SIZE_AUTOSIZE) || this.sizeType.equals(SIZE_AUTOHEIGHT)) {
                for (int i = 0; i < rs.length; i++) {
                    rs[i] = EleRect.DEF_HEIGHT;
                }
                for (int i = 0; i < this.eleList.size(); i++) {
                    cell = (EleCell) this.eleList.get(i);
                    if (cell.col < cs.length && cell.row < rs.length) {
                        if (cell.rowSpan <= 1) {
                            rs[cell.row] = Math.max(rs[cell.row], cell.height);
                        }
                    }
                }
                StringBuffer sb = new StringBuffer();
                int n = 0;
                for (int i = 0; i < rs.length; i++) {
                    if (i > 0) sb.append(",");
                    sb.append(rs[i]);
                    n += rs[i];
                }
                this.height = n + (int) this.strokeWidth * 2 + this.margin * 2;
                this.rows = sb.toString();
            }
        }
    }
    public Object clone() {
        return new EleTable(this.xdoc, this.getAttMap());
    }
    public Rectangle getCellRect(int[] rs, int[] cs, EleCell[][] cells, int row, int col) {
        Rectangle rect = new Rectangle();
        int x = 0, y = 0, w = 0, h = 0;
        x = this.margin + (int) this.strokeWidth;
        y = this.margin + (int) this.strokeWidth;
        for (int i = 0; i < row; i++) {
            y += rs[i];
        }
        for (int i = 0; i < col; i++) {
            x += cs[i];
        }
        
        for (int i = 0; i < cells[row][col].rowSpan; i++) {
            if (row + i < rs.length)
                h += rs[row + i];
        }
        for (int i = 0; i < cells[row][col].colSpan; i++) {
            if (col + i < cs.length)
                w += cs[col + i];
        }
        rect.setFrame(x, y, w, h);
        return rect;
    }
    public void print(Graphics2D g) {
        if (this.isVisible()) {
            print(g, 0, this.height);
        }
    }
    public void checkRC() {
    	if (this.rows.length() == 0 || this.cols.length() == 0) {
    		int rs = 0;
    		int cs = 0;
            EleCell cell;
            for (int i = 0; i < this.eleList.size(); i++) {
                cell = (EleCell) this.eleList.get(i);
                if (cell.row + cell.rowSpan > rs) {
                	rs = cell.row + cell.rowSpan;
                }
                if (cell.col + cell.colSpan > cs) {
                	cs = cell.col + cell.colSpan;
                }
            }
            this.setRC(rs, cs);
    	}
    }
    public void print(Graphics2D g, int pf, int ph) {
        Shape shape = this.fillShape(g);
        checkRC();
        String[] rowStrs = this.rows.split(",");
        String[] colStrs = this.cols.split(",");
        int[] rs = new int[rowStrs.length];
        int[] cs = new int[colStrs.length];
        for (int i = 0; i < rs.length; i++) {
            rs[i] = To.toInt(rowStrs[i], EleRect.DEF_HEIGHT);
        }
        for (int i = 0; i < cs.length; i++) {
            cs[i] = To.toInt(colStrs[i], EleRect.DEF_WIDTH);
        }
        int cw = 0, ch = 0;
        for (int i = 0; i < cs.length; i++) {
            cw += cs[i];
        }
        for (int i = 0; i < rs.length; i++) {
            ch += rs[i];
        }
        int ih = this.height - this.margin * 2 - (int) this.strokeWidth*2;
        int iw = this.width - this.margin * 2 - (int) this.strokeWidth*2;
        for (int i = 0; i < rs.length; i++) {
            rs[i] = (int) ((double) rs[i] / ch * ih);
        }
        for (int i = 0; i < cs.length; i++) {
            cs[i] = (int) ((double) cs[i] / cw * iw);
        }
        
        //修正
        cw = 0;
        ch = 0;
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] < 0) cs[i] = 0;
            cw += cs[i];
        }
        for (int i = 0; i < rs.length; i++) {
            if (rs[i] < 0) rs[i] = 0;
            ch += rs[i];
        }
        rs[rs.length - 1] += ih - ch;
        if (rs[rs.length - 1] < 0) rs[rs.length - 1] = 0;
        cs[cs.length - 1] += iw - cw;
        if (cs[cs.length - 1] < 0) cs[cs.length - 1] = 0;

        
        //填充二维数组单元格
        EleCell[][] cells = new EleCell[rs.length][cs.length];
        EleCell cell;
        for (int i = 0; i < this.eleList.size(); i++) {
        	if (this.eleList.get(i) instanceof EleCell) {
        		cell = (EleCell) this.eleList.get(i);
        		if (cell.row < rs.length && cell.col < cs.length) {
        			cells[cell.row][cell.col] = cell;
        			cell.getRect().width = cs[cell.col];
        			cell.getRect().height = rs[cell.row];
        		}
        	}
        }
        setMerge(rs, cs, cells);
        Rectangle cellRect;
        int ncount = 0;
        Graphics2D cg;
        int inputRow = 1;
        if (this.header > 1) {
            inputRow = this.header;
        }
        for (int i = 0; i < rs.length; i++) {
            if (this.input && i == inputRow) {
                continue;
            }
            if (ncount >= pf && ncount + rs[i] <= pf + ph) {
                for (int j = 0; j < cs.length; j++) {
                    if (cells[i][j] != null && cells[i][j].belong == null) {
                        cellRect = getCellRect(rs, cs, cells, i, j);
                        cell = ((EleCell) cells[i][j]);
                        cell.setBounds(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
                        if (cell.rotate == 0) {
                            cg = (Graphics2D) (g.create(cellRect.x, cellRect.y, 
                                    cellRect.width + 1, cellRect.height + 1));
                        } else {
                            int d = (int) Math.ceil(Math.pow(Math.pow(cell.width, 2) + Math.pow(cell.height, 2), 0.5));
                            cg = (Graphics2D) g.create(cell.left - (d - cell.width) / 2,
                                    (cell.top) - (d - cell.height) / 2, 
                                    d,
                                    d);
                            AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(cell.rotate), d/2, d/2);
                            cg.transform(at);
                            cg.translate((d - cell.width) / 2, (d - cell.height) / 2);
                        }
                        cell.print(cg);
                        cg.dispose();
                    }
                }
            }
            ncount += rs[i];
        }
        this.drawShape(g, shape);
    }
    private void setMerge(int[] rs, int[] cs, EleCell[][] cells) {
        EleCell cell;
        for (int i = 0; i < rs.length && i < cells.length; i++) {
            for (int j = 0; j < cs.length && j < cells[i].length; j++) {
                cell = cells[i][j];
                if (cell != null && cell.belong == null && (cell.rowSpan > 1 || cell.colSpan > 1)) { //合并单元格
                    for (int m = 0; m < cell.rowSpan; m++) {
                        for (int n = 0; n < cell.colSpan; n++) {
                            if (i + m < cells.length && j + n < cells[i + m].length && cells[i + m][j + n] != null) {
                                cells[i + m][j + n].belong = cell;
                            }
                        }
                    }
                    cell.belong = null;
                }
            }
        }
    }
    public EleCell getCell(int row, int col) {
        EleCell cell = null;
        for (int i = 0; i < this.eleList.size(); i++) {
            cell = (EleCell) this.eleList.get(i);
            if (cell.row == row && cell.col == col) {
                break;
            } else {
                cell = null;
            }
        }
        return cell;
    }
    /**
     * 如果比页面宽，自动缩小
     */
    public void adjustWidth() {
        if (width > xdoc.getPaper().getContentWidth()) {
            width = xdoc.getPaper().getContentWidth();
        }
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        checkRC();
        String[] rowStrs = this.rows.split(",");
        String[] colStrs = this.cols.split(",");
        int[] rs = new int[rowStrs.length];
        int[] cs = new int[colStrs.length];
        for (int i = 0; i < rs.length; i++) {
            rs[i] = To.toInt(rowStrs[i], EleRect.DEF_HEIGHT);
        }
        for (int i = 0; i < cs.length; i++) {
            cs[i] = To.toInt(colStrs[i], EleRect.DEF_WIDTH);
        }
        int cw = 0, ch = 0;
        for (int i = 0; i < cs.length; i++) {
            cw += cs[i];
        }
        for (int i = 0; i < rs.length; i++) {
            ch += rs[i];
        }
        int ih = this.height - this.margin * 2 - (int) this.strokeWidth*2;
        int iw = this.width - this.margin * 2 - (int) this.strokeWidth*2;
        for (int i = 0; i < rs.length; i++) {
            rs[i] = (int) ((double) rs[i] / ch * ih);
        }
        for (int i = 0; i < cs.length; i++) {
            cs[i] = (int) ((double) cs[i] / cw * iw);
        }
        
        //修正
        cw = 0;
        ch = 0;
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] < 0) cs[i] = 0;
            cw += cs[i];
        }
        for (int i = 0; i < rs.length; i++) {
            if (rs[i] < 0) rs[i] = 0;
            ch += rs[i];
        }
        rs[rs.length - 1] += ih - ch;
        if (rs[rs.length - 1] < 0) rs[rs.length - 1] = 0;
        cs[cs.length - 1] += iw - cw;
        if (cs[cs.length - 1] < 0) cs[cs.length - 1] = 0;

        
        //填充二维数组单元格
        EleCell[][] cells = new EleCell[rs.length][cs.length];
        EleCell cell;
        for (int i = 0; i < this.eleList.size(); i++) {
        	if (this.eleList.get(i) instanceof EleCell) {
        		cell = (EleCell) this.eleList.get(i);
        		if (cell.row < rs.length && cell.col < cs.length) {
        			cells[cell.row][cell.col] = cell;
        			cell.getRect().width = cs[cell.col];
        			cell.getRect().height = rs[cell.row];
        		}
        	}
        }
        for (int i = 0; i < rs.length; i++) {
            for (int j = 0; j < cs.length; j++) {
                if (cells[i][j] != null) {
                    sb.append(StrUtil.csvEncode(cells[i][j].toString()));
                }
                if (j < cs.length - 1) {
                    sb.append(",");
                }
            }
            if (i < rs.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    public void setText(String text) {
    	EleBase base = null;
    	if (this.eleList.size() > 0) {
    		base = (EleBase) this.eleList.get(0);
    	} else {
    		EleCell cell = new EleCell(this.xdoc);
    		cell.row = 1;
    		cell.col = 1;
    		base = cell;
    	}
    	base.setText(text);
    }
}
