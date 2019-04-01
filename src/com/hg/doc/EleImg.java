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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.hg.util.MapUtil;

/**
 * 图形
 * @author whg
 */
public class EleImg extends EleShape {
    protected EleBase copyEle(XDoc xdoc) {
        return new EleImg(xdoc, this.getAttMap());
    }
    public String src;
    public String drawType;
	public String strShape;
    public static String DRAW_TYPE_ZOOM = "zoom";
    public static String DRAW_TYPE_REPEAT = "repeat";
    public static String DRAW_TYPE_CENTER = "center";
    public static String DRAW_TYPE_FACT = "fact";
    public static String DRAW_TYPE_ADJUST = "adjust";
    public static String DRAW_TYPE_9GRID = "9grid";
    public static String DRAW_TYPE_4CORNER = "4corner";
    public EleImg(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public EleImg(XDoc xdoc) {
        super(xdoc);
    }
    protected void init() {
        super.init();
        typeName = "img";
        drawType = DRAW_TYPE_ZOOM;
        strShape = "";
        this.src = "";
        this.color = null;
        this.fillColor = Color.WHITE;
        this.width = 0;
        this.height = 0;
    }
    /**
     * 解析属性map
     */
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        if (map.containsKey("fillColor") 
                || map.containsKey("fillImg") 
                || map.containsKey("src") && !map.get("src").equals(this.src)) {
            this.img = null;
        }
        this.src = MapUtil.getString(map, "src", this.src);
        this.drawType = MapUtil.getString(map, "drawType", this.drawType);
        this.strShape = MapUtil.getString(map, "shape", this.strShape);
    }
    /**
     * 解析属性map
     */
    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("src", this.src);
        map.put("drawType", this.drawType);
        map.put("shape", this.strShape);
        return map;
    }
    public Object clone() {
        return new EleImg(this.xdoc, this.getAttMap());
    }
    protected Shape getShape() {
    	Shape sha;
    	if (strShape.length() > 0) {
    		sha = ShapeUtil.strToShape(this.strShape);
            //缩放
            Rectangle2D bound = sha.getBounds2D();
            AffineTransform af = new AffineTransform();
            af.scale(this.width / bound.getWidth(), 
                    this.height / bound.getHeight());
            sha = af.createTransformedShape(sha);
            //移动
            bound = sha.getBounds2D();
            af = new AffineTransform();
            af.translate(-bound.getX(), -bound.getY());
            sha = af.createTransformedShape(sha);
    	} else {
    		sha = super.getShape();
    	}
    	return sha;
    }
    public BufferedImage img = null;
    public BufferedImage getImg() {
        if (this.img == null && this.src.length() > 0) {
            if (this.src.startsWith("$")) {
                this.src = this.src.substring(1);
            }
            img = ImgUtil.loadImg(this.xdoc, this.src, this.fillColor, this.fillColor2, !Color.WHITE.equals(this.fillColor) || this.fillImg.length() > 0);
            if (img != null && this.width == 0 && this.height == 0) {
            	this.width = img.getWidth();
            	this.height = img.getHeight();
            }
        }
        return img;
    }
    protected void drawOther(Graphics2D g, Shape shape) {
        img = getImg();
        if (img != null) {
            autoSize();
            if (width > 0 && height > 0) {
            	TexturePaint paint = ImgUtil.toPaint(img, drawType, width, height, margin, ImgUtil.isStretch(src));
            	paint = PaintUtil.checkPaint(g, paint);
            	g.setPaint(paint);
            	if (this.margin != 0) {
            		AffineTransform af = new AffineTransform();
            		af = new AffineTransform();
            		af.translate(-this.margin, -this.margin);
            		shape = af.createTransformedShape(shape);
            	}
            	g.fill(shape);
            }
        }
    }
	public void autoSize() {
        if (this.width == 0 && this.height == 0) {
        	this.getImg();
        }
        boolean b = false;
        for (int i = 0; i < this.eleList.size(); i++) {
            if (this.eleList.get(i) instanceof EleRect 
            		&& ((EleRect) this.eleList.get(i)).dock.length() == 0) {
                b = true;
                break;
            }
        }
        if (!b && (this.sizeType.equals(SIZE_AUTOSIZE)
        		|| this.sizeType.equals(SIZE_AUTOWIDTH)
        		|| this.sizeType.equals(SIZE_AUTOHEIGHT)) && !hasTxt(this)) {
            img = getImg();
            if (img != null) {
            	if (this.sizeType.equals(SIZE_AUTOSIZE)
            			|| this.sizeType.equals(SIZE_AUTOWIDTH)) {
            		this.width = img.getWidth();
            	}
            	if (this.sizeType.equals(SIZE_AUTOSIZE)
            			|| this.sizeType.equals(SIZE_AUTOHEIGHT)) {
            		this.height = img.getHeight();
            	}
                this.fixSize();
            } else {
                this.width = this.height;
            }
        } else {
            super.autoSize();
        }
    }
	protected void bindValue(String val) {
		this.src = val;
	}
	public void fixSize() {
        DocPaper p = this.xdoc.getPaper();
        fixSize(this, p.getContentWidth(), p.getContentHeight());
	}
}
