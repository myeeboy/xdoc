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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

public class DocPrint implements Printable {
    protected XDoc xdoc;
    public DocPrint(XDoc xdoc) {
        this(xdoc, false);
    }
    public ArrayList pages;
    private static void getHeads(EleBase ele, ArrayList heads) {
    	for (int i = 0; i < ele.eleList.size(); i++) {
    		if (ele.eleList.get(i) instanceof ElePara) {
    			ElePara para = (ElePara) ele.eleList.get(i);
    			if (para.heading > 0) {
    				heads.add(new Heading(para));
    			}
    		}
    		getHeads((EleBase) ele.eleList.get(i), heads);
    	}
    }
    public List toolTips = new ArrayList();
    public List hrefs = new ArrayList();
    public List inputs = new ArrayList();
    public DocPrint(XDoc xdoc, boolean firstOnly) {
        this(xdoc, firstOnly, true);
    }
    /**
     * 构造器
     * @param xdoc xdoc
     * @param firstOnly 仅第一页
     * @param splitPage 分页
     */
    public DocPrint(XDoc xdoc, boolean firstOnly, boolean splitPage) {
        if (xdoc == null) return;
        this.xdoc = xdoc;
        EleRect rect = new EleRect(xdoc, xdoc.bodyRect.getAttMap());
        rect.align = DocConst.ALIGN_TOP;
        if (rect.sizeType.equals(EleRect.SIZE_AUTOWIDTH)
        		|| rect.sizeType.equals(EleRect.SIZE_AUTOSIZE)) {
            rect.sizeType = EleRect.SIZE_AUTOSIZE;
        } else {
            rect.sizeType = EleRect.SIZE_AUTOHEIGHT;
        }
        DocPaper paper = xdoc.getPaper();
        xdoc.frontRect.left = 0;
        xdoc.frontRect.top = 0;
        xdoc.frontRect.width = paper.width;
        xdoc.frontRect.height = paper.height;
        xdoc.backRect.left = 0;
        xdoc.backRect.top = 0;
        xdoc.backRect.width = paper.width;
        xdoc.backRect.height = paper.height;
        rect.width = paper.getContentWidth();
        rect.height = Integer.MAX_VALUE;
        if (this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOFONT)) {
            rect.sizeType = EleRect.SIZE_AUTOFONT;
            rect.height = paper.getContentHeight();
        } else {
            rect.height = Integer.MAX_VALUE;
        }
        ElePara.genHeadInx(xdoc.paraList);
        rect.eleList.addAll(xdoc.paraList);
        rect.eleList.addAll(xdoc.rectList);
        genHead(xdoc, rect); //段落前缀索引使用
        if (this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOWIDTH)
                || this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOHEIGHT)
                || this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOSIZE)
                || this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOFONT)) {
            rect.autoSize();
            paper = this.xdoc.getPaper();
            if (this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOWIDTH) || this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOSIZE)) {
                paper.width = rect.width + paper.getLeftMargin() + paper.getRightMargin();
            }
            if (this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOHEIGHT) || this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOSIZE)) {
                paper.height = rect.height + paper.getTopMargin() + paper.getBottomMargin();
            }
            xdoc.frontRect.width = paper.width;
            xdoc.frontRect.height = paper.height;
            xdoc.backRect.width = paper.width;
            xdoc.backRect.height = paper.height;
            rect.name = DocConst.NO_PARA_BREAK;
        }
        if (this.xdoc.metaMap.containsKey("page")) {
        	rect.height = xdoc.getPaper().getContentHeight();
        }
        if (this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOHEIGHT)
                || this.xdoc.bodyRect.sizeType.equals(EleRect.SIZE_AUTOSIZE)
                || this.xdoc.metaMap.containsKey("page")) {
            pages = new ArrayList();
            pages.add(rect);
        } else {
            if (!splitPage) {
                pages = new ArrayList();
                pages.add(rect);
                //自动尺寸
                rect.rectSize();
            } else {
                ElePara para;
            	if (rect.eleList.size() > 0 && rect.eleList.get(0) instanceof ElePara) {
            		para = (ElePara) rect.eleList.get(0);
            		if (para.eleList.size() == 1 && para.eleList.get(0) instanceof EleTable) {
            			//首行表格不能分页bug暂时处理
            			para = new ElePara(xdoc);
            			EleText txt = para.addText();
            			txt.text = " ";
            			para.lineSpacing = -1;
            			txt.fontSize = 1; //设置为0会导致不能输出pdf
            			rect.eleList.add(0, para);
            		}
            	}
                pages = EleRect.split(rect, xdoc.getPaper().getContentHeight(), xdoc.getPaper().getContentHeight(), firstOnly);
                if (pages.size() == 0) { //至少有一页
                    pages = new ArrayList();
                    pages.add(rect);
                } else {
                    //自动尺寸
                    for (int i = 0; i < pages.size(); i++) {
                        ((EleRect) pages.get(i)).rectSize();
                    }
                }
                for (int i = 0; i < pages.size(); i++) {
                    ((EleRect) pages.get(i)).height = xdoc.getPaper().getContentHeight();
                }
            }
        }
        xdoc.print = this;
        xdoc.pages = pages.size();
    }
	public static void genHead(XDoc xdoc) {
        EleRect rect = new EleRect(xdoc);
        rect.eleList.addAll(xdoc.paraList);
        rect.eleList.addAll(xdoc.rectList);
        genHead(xdoc, rect);
    }
    public static void genHead(XDoc xdoc, EleRect rect) {
        ArrayList tmpHeads = new ArrayList();
        //检索标题
        getHeads(rect, tmpHeads);
        Heading heading, tmpHeading;
        boolean find = false;
        //形成树形结构
        xdoc.heads = new ArrayList();
        for (int i = 0; i < tmpHeads.size(); i++) {
            heading = (Heading) tmpHeads.get(i);
            find = false;
            for (int j = i - 1; j >= 0; j--) {
                tmpHeading = (Heading) tmpHeads.get(j);
                if (tmpHeading.level() < heading.level()) {
                    if (tmpHeading.cheads == null) {
                        tmpHeading.cheads = new ArrayList();
                    }
                    tmpHeading.cheads.add(heading);
                    find = true;
                    break;
                }
            }
            if (!find) {
                xdoc.heads.add(heading);
            }
        }
        //生成索引
        genHeadInx(xdoc.heads, "");
        //xdoc.heads.clear();
	}
	private static void genHeadInx(ArrayList heads, String inxStr) {
    	if (heads != null) {
            int n = 0;
    		for (int i = 0; i < heads.size(); i++) {
                if (!((Heading) heads.get(i)).para.isBlank()) {
                    ((Heading) heads.get(i)).para.headInx = inxStr + (n + 1);
                    genHeadInx(((Heading) heads.get(i)).cheads, inxStr + (n + 1) + ".");
                    n++;
                }
    		}
    	}
	}
	public int getPageCount() {
        return xdoc.pages;
    }
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
        xdoc.page = pageIndex + 1;
        if (pageIndex < xdoc.pages) {
            if (pageIndex == 0) {
                xdoc.heading = "";
            }
            Graphics2D g2 = (Graphics2D) g;
            if (this.xdoc.scale != 1) {
                g2.scale(this.xdoc.scale, this.xdoc.scale);
            }
            xdoc.backRect.print(g2);
            EleRect rect = (EleRect) pages.get(pageIndex);
            g2.translate(xdoc.getPaper().getLeftMargin(), xdoc.getPaper().getTopMargin());
            rect.print(g2);
            g2.translate(-xdoc.getPaper().getLeftMargin(), -xdoc.getPaper().getTopMargin());
            xdoc.frontRect.print(g2);
            return Printable.PAGE_EXISTS;
        } else {
            return Printable.NO_SUCH_PAGE;
        }
    }
    public XDoc getPageDoc(int inx) {
        XDoc pdoc = new XDoc();
        pdoc.setPaper(xdoc.getPaper());
        pdoc.bodyRect = xdoc.bodyRect;
        pdoc.backRect = xdoc.backRect;
        pdoc.bodyRect = new EleRect(pdoc, xdoc.bodyRect.getAttMap());
        pdoc.bodyRect.sizeType = EleRect.SIZE_NORMAL;
        pdoc.metaMap.putAll(xdoc.metaMap);
        pdoc.metaMap.put("page", String.valueOf(inx + 1));
        pdoc.metaMap.put("pages", String.valueOf(xdoc.pages));
        pdoc.metaMap.put("heading", String.valueOf(xdoc.heading));
        pdoc.frontRect = xdoc.frontRect;
        EleRect rect = (EleRect) pages.get(inx);
        for (int i = 0; i < rect.lineList.size(); i++) {
            pdoc.paraList.add(new EleParaLine((DocPageLine) rect.lineList.get(i)));
        }
        pdoc.rectList = rect.eleList;
        return pdoc;
    }
}
