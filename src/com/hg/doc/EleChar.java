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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.hg.util.MapUtil;

public class EleChar extends EleText {
	public String strImg;
	public EleChar(XDoc xdoc) {
		super(xdoc);
	}
    public EleChar(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    protected void init() {
    	super.init();
        this.typeName = "char";
        this.strShape = "";
        this.strImg = "";
    }
	public boolean attEquals(EleText txt) {
		return false;
	}
	protected EleBase copyEle(XDoc xdoc) {
        return new EleChar(xdoc, this.getAttMap());
    }
    public Object clone() {
        return new EleChar(this.xdoc, this.getAttMap());
    }
	public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("shape", this.strShape);
        map.put("img", this.strImg);
        return map;
	}
    protected void actDrawString(Graphics2D g, String str, int x, int y, boolean vertical) {
    	int fontSize = g.getFont().getSize();
    	int spacing = this.getSpacing(fontSize) / 2;
    	if (this.shape != null) {
    		g.fill(getShape(str, g.getFont(), x, y, vertical));
    	}
        if (this.strImg.length() > 0) {
    		if (this.strImg.startsWith("<") || this.strImg.startsWith("{")) {
    			EleRect rect = DocUtil.getRect(xdoc, this.strImg);
    			if (rect != null) {
    				Graphics2D cg = (Graphics2D) g.create(x + spacing, (int) (y - fontSize * 0.8), 
    						(int) (rect.width * ((double) fontSize / rect.height)), fontSize);
    				cg.scale((double) fontSize / rect.height, (double) fontSize / rect.height);
    				rect.print(cg);
    				cg.dispose();
    				return;
    			}
    		}
    		BufferedImage bimg = this.getImg();
    		if (bimg != null) {
	    		int w = (int) (bimg.getWidth() * ((double) fontSize / bimg.getHeight()));
	    		g.drawImage(bimg, x + spacing, 
	    				(int) (y - fontSize * 0.8), w, fontSize, null);
        	}
        }
    }
    protected Shape getShape(String str, Font font, int x, int y, boolean vertical) {
    	int fontSize = font.getSize();
    	int spacing = this.getSpacing(font.getSize()) / 2;
    	if (this.shape != null) {
        	Shape sha = this.shape;
    		//缩放
        	Rectangle2D bound = sha.getBounds2D();
    		AffineTransform af = new AffineTransform();
    		af.scale(fontSize / bound.getHeight() - fontSize * 0.02 / bound.getHeight(), fontSize * 0.9 / bound.getHeight());
    		sha = af.createTransformedShape(sha);
    		//移动
    		bound = sha.getBounds2D();
    		af = new AffineTransform();
    		af.translate(-bound.getX() + x + fontSize * 0.02 + spacing, -bound.getY() + y - fontSize * 0.8);
    		sha = af.createTransformedShape(sha);
        	return sha;
        } else if (this.strImg.length() > 0) {
        	BufferedImage bimg = this.getImg();
        	if (bimg != null) {
        		return new Rectangle(x + spacing, (int) (y - fontSize * 0.8), (int) (bimg.getWidth() * ((double) fontSize / bimg.getHeight())), fontSize);
        	}
        }
    	return new Rectangle(x + spacing, (int) (y - fontSize * 0.8), fontSize, fontSize);
    }
	private BufferedImage getImg() {
		if (img == null && this.strImg.length() > 0) {
			this.img = ImgUtil.loadImg(xdoc, this.strImg, Color.black, Color.white, true);
		}
		return img;
	}
	public Rectangle2D getBounds() {
		return getBounds(this.fontSize);
	}
	private Rectangle2D getBounds(int fontSize) {
    	int spacing = this.getSpacing(fontSize);
        if (this.shape != null) {
        	Rectangle2D bounds = this.shape.getBounds2D();
        	return new Rectangle(0, 0,
        			(int) (fontSize * bounds.getWidth() / bounds.getHeight() + spacing), fontSize);
        } else if (this.strImg.length() > 0) {
        	BufferedImage bimg = this.getImg();
        	if (bimg != null) {
        		return new Rectangle(0, 0, (int) (bimg.getWidth() * ((double) fontSize / bimg.getHeight()) + spacing), fontSize);
        	}
        }
        return new Rectangle(0, 0, fontSize + spacing, fontSize);
	}
	public String viewText() {
		return "C";
	}
	public void setShape(String strShape) {
		this.strShape = strShape;
		if (strShape.length() > 0) {
			this.shape = ShapeUtil.strToShape(this.strShape);
		} else {
			this.shape = null;
		}
	}
	private String strShape = "";
	private Shape shape = null;
	private BufferedImage img;
	public void setAttMap(HashMap map) {
		super.setAttMap(map);
		this.text = "C";
		if (map.containsKey("shape")) {
			this.setShape(MapUtil.getString(map, "shape", this.strShape));
		}
		this.strImg = MapUtil.getString(map, "img", this.strImg);
		if (map.containsKey("img")) {
			this.img = null;
		}
	}
    public Rectangle2D getStrBounds(Font font, String str, boolean vertical) {
    	Rectangle2D bounds = this.getBounds(font.getSize());
    	bounds.setFrame(bounds.getX(), -bounds.getHeight() * 0.8,
    			bounds.getWidth(),
    			bounds.getHeight());
    	return bounds;
    }
    public String toString() {
        return this.text;
    }
}
