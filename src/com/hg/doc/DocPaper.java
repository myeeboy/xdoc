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

import java.awt.Dimension;
import java.util.HashMap;

import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hg.util.To;

public class DocPaper {
    public String name;
    public int width;
    public int height;
    private int topMargin;
    private int leftMargin;
    private int rightMargin;
    private int bottomMargin;
    
    private XDoc xdoc;
    public int viewWidth() {
        return xdoc.intScale(this.width);
    }
    public int viewHeight() {
        return xdoc.intScale(this.height);
    }
    /**
     * 内容区宽度
     * @return
     */
    public int getContentWidth() {
        return Math.max(width - getLeftMargin() - getRightMargin(), XFont.defaultFontSize);
    }
    /**
     * 内容区高度
     * @return
     */
    public int getContentHeight() {
        return Math.max(height - getTopMargin() - getBottomMargin(), XFont.defaultFontSize);
    }
    /**
     * 内容区尺寸
     * @return
     */
    public Dimension getContentSize() {
        return new Dimension(this.getContentWidth(), this.getContentHeight());
    }
    public static int DEFAULT_MARGIN = (int) DocUtil.dpi;
    public static int DEFAULT_WIDTH = (int) (MediaSize.ISO.A4.getX(Size2DSyntax.INCH) * DocUtil.dpi);
    public static int DEFAULT_HEIGHT = (int) (MediaSize.ISO.A4.getY(Size2DSyntax.INCH) * DocUtil.dpi);
    public DocPaper(XDoc xdoc) {
        this.xdoc = xdoc;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.setMargin(DEFAULT_MARGIN);
    }
    public DocPaper(XDoc xdoc, String paper) {
    	this.xdoc = xdoc;
        String[] strs = paper.split(",");
        strs[0] = strs[0].toUpperCase();
        if (strs[0].indexOf("*") >= 0) {
        	int pos = strs[0].indexOf("*");
            this.width = To.toInt(strs[0].substring(0, pos));
            this.height = To.toInt(strs[0].substring(pos + 1));
            if (this.width == 0 && this.height == 0) {
            	this.width = DEFAULT_WIDTH;
            	this.height = DEFAULT_HEIGHT;
            	xdoc.bodyRect.sizeType = EleRect.SIZE_AUTOSIZE;
            } else if (this.width == 0) {
            	this.width = DEFAULT_WIDTH;
            	xdoc.bodyRect.sizeType = EleRect.SIZE_AUTOWIDTH;
            } else if (this.height == 0) {
            	this.height = DEFAULT_HEIGHT;
            	xdoc.bodyRect.sizeType = EleRect.SIZE_AUTOHEIGHT;
            } else {
            	xdoc.bodyRect.sizeType = EleRect.SIZE_NORMAL;
            }
        	if (strs.length > 1) {
        		this.setMargin(To.toInt(strs[1]));
        	}
        } else {
        	if (paperMap.get(strs[0]) != null) {
        		Dimension d = (Dimension) paperMap.get(strs[0]);
        		this.width = d.width;
        		this.height = d.height;
        	} else if (paperMap.get("ISO-" + strs[0]) != null) {
        		Dimension d = (Dimension) paperMap.get("ISO-" + strs[0]);
        		this.width = d.width;
        		this.height = d.height;
        	}
        	if (strs.length > 1
        			&& this.width < this.height
        			&& (strs[1].toUpperCase().equals("H")
        					|| strs[1].toUpperCase().equals("L"))) {
        		int n = this.width;
        		this.width = this.height;
        		this.height = n;
        	}
        	if (strs.length > 2) {
        		this.setMargin(To.toInt(strs[2]));
        	}
        }
    }
    private static HashMap paperMap;
    static {
        paperMap = new HashMap();
        MediaSizeName[] mss = new MediaSizeName[] {MediaSizeName.ISO_A0,
                MediaSizeName.ISO_A1,
                MediaSizeName.ISO_A2,
                MediaSizeName.ISO_A3,
                MediaSizeName.ISO_A4,
                MediaSizeName.ISO_A5,
                MediaSizeName.ISO_A6,
                MediaSizeName.ISO_A7,
                MediaSizeName.ISO_A8,
                MediaSizeName.ISO_A9,
                MediaSizeName.ISO_A10,
                MediaSizeName.ISO_B0,
                MediaSizeName.ISO_B1,
                MediaSizeName.ISO_B2,
                MediaSizeName.ISO_B3,
                MediaSizeName.ISO_B4,
                MediaSizeName.ISO_B5,
                MediaSizeName.ISO_B6,
                MediaSizeName.ISO_B7,
                MediaSizeName.ISO_B8,
                MediaSizeName.ISO_B9,
                MediaSizeName.ISO_B10,
                MediaSizeName.ISO_C0,
                MediaSizeName.ISO_C1,
                MediaSizeName.ISO_C2,
                MediaSizeName.ISO_C3,
                MediaSizeName.ISO_C4,
                MediaSizeName.ISO_C5,   
                MediaSizeName.ISO_C6};
        MediaSize ms;
        for (int i = 0; i < mss.length; i++) {
            ms = MediaSize.getMediaSizeForName(mss[i]);
            if (ms != null) {
                paperMap.put(ms.getMediaSizeName().toString().toUpperCase(), new Dimension((int) (ms.getX(Size2DSyntax.INCH) * DocUtil.dpi),
                        (int) (ms.getY(Size2DSyntax.INCH) * DocUtil.dpi)));
            }
        }
    }
	public void set(DocPaper paper) {
        this.name = paper.name;
        this.width = paper.width;
        this.height = paper.height;
        this.topMargin = paper.topMargin;
        this.bottomMargin = paper.bottomMargin;
        this.leftMargin = paper.leftMargin;
        this.rightMargin = paper.rightMargin;
    }
    public int getBottomMargin() {
        return this.bottomMargin;
    }
    public int getLeftMargin() {
        return this.leftMargin;
    }
    public int getRightMargin() {
        return this.rightMargin;
    }
    public int getTopMargin() {
        return this.topMargin;
    }
    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }
    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }
    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }
    public Element toEle() {
        Element paper = DocumentHelper.createElement("paper");
        if (width != (int) (MediaSize.ISO.A4.getX(Size2DSyntax.INCH) * DocUtil.dpi)) {
        	paper.addAttribute("width", String.valueOf(width));
        }
        if (height != (int) ((int) (MediaSize.ISO.A4.getY(Size2DSyntax.INCH) * DocUtil.dpi))) {
        	paper.addAttribute("height", String.valueOf(height));
        }
        if (topMargin != (int) DocUtil.dpi) {
        	paper.addAttribute("topMargin", String.valueOf(topMargin));
        }
        if (leftMargin != (int) DocUtil.dpi) {
        	paper.addAttribute("leftMargin", String.valueOf(leftMargin));
        }
        if (rightMargin != (int) DocUtil.dpi) {
        	paper.addAttribute("rightMargin", String.valueOf(rightMargin));
        }
        if (bottomMargin != (int) DocUtil.dpi) {
        	paper.addAttribute("bottomMargin", String.valueOf(bottomMargin));
        }
        if (paper.attributeCount() > 0) {
        	return paper;
        } else {
        	return null;
        }
    }
    public void setMargin(int margin) {
        this.topMargin = margin;
        this.bottomMargin = margin;
        this.leftMargin = margin;
        this.rightMargin = margin;
    }
}
