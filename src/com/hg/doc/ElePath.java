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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import com.hg.util.MapUtil;

/**
 * 图形
 * @author whg
 */
public class ElePath extends EleShape {
    protected EleBase copyEle(XDoc xdoc) {
        return new ElePath(xdoc, this.getAttMap());
    }

    public ElePath(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public ElePath(XDoc xdoc) {
        super(xdoc);
    }
    public String drawType;
    public static String DRAW_TYPE_ZOOM = "zoom";
    public static String DRAW_TYPE_FACT = "fact";
    public static String DRAW_TYPE_ADJUST = "adjust";

    protected void init() {
        super.init();
        typeName = "path";
        path = "";
        this.drawType = DRAW_TYPE_ZOOM;
        repeat = 1;
    }
    public int repeat;
    public String path;
	protected boolean canFill() {
		return this.path.length() > 0;
	}
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.path = MapUtil.getString(map, "path", this.path);
        this.path = MapUtil.getString(map, "shape", this.path);
        this.drawType = MapUtil.getString(map, "drawType", this.drawType);
        if (MapUtil.getBool(map, "lockRatio", false)) {
        	this.drawType = DRAW_TYPE_ADJUST;
        }
        this.repeat = MapUtil.getInt(map, "repeat", this.repeat);
    }
    
    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("shape", this.path);
        map.put("drawType", this.drawType);
        map.put("repeat", String.valueOf(this.repeat));
        return map;
    }
    public Object clone() {
        return new ElePath(this.xdoc, this.getAttMap());
    }
    protected Shape getShape() {
    	if (this.repeat > 1) {
    		Shape shape = this.getActShape();
    		Rectangle2D bound = shape.getBounds2D();
    		AffineTransform af = new AffineTransform();
    		double n = 1000;
    		af.scale(n * 0.9 / bound.getWidth(), 
    				n * 0.9 / bound.getHeight());
    		shape = af.createTransformedShape(shape);
    		bound = shape.getBounds2D();
    		af = new AffineTransform();
    		af.translate(-bound.getX() + n * 0.05, -bound.getY() + n * 0.05);
    		shape = af.createTransformedShape(shape);
    		if (this.width > this.height) {
    			GeneralPath path = new GeneralPath();
    			af = new AffineTransform();
    			for (int i = 0; i < this.repeat; i++) {
    				path.append(af.createTransformedShape(shape), false);
    				af.translate(n, 0);
    			}
        		af = new AffineTransform();
        		af.scale(1 / (double) this.repeat * this.width / n, this.height / n);
    			return af.createTransformedShape(path);
    		} else if (this.width == this.height) {
    			double rotate = Math.PI * 2 / this.repeat;
    			GeneralPath path = new GeneralPath();
        		af = new AffineTransform();
        		af.translate(n * 2, 0);
        		shape = af.createTransformedShape(shape);
        		af = new AffineTransform();
    			for (int i = 0; i < this.repeat; i++) {
    				path.append(af.createTransformedShape(shape), false);
    				af.rotate(rotate, n * 2.5, n * 2.5);
    			}
        		af = new AffineTransform();
        		af.scale(1 / 5.0 * this.width / n, 1 / 5.0 * this.width / n);
    			return af.createTransformedShape(path);
    		} else {
    			GeneralPath path = new GeneralPath();
        		af = new AffineTransform();
    			for (int i = 0; i < this.repeat; i++) {
    				path.append(af.createTransformedShape(shape), false);
    				af.translate(0, n);
    			}
        		af = new AffineTransform();
        		af.scale(this.width / n, 1 / (double) this.repeat * this.height / n);
    			return af.createTransformedShape(path);
    		}
    	} else {
    		return this.getActShape();
    	}
    }
    private Shape getActShape() {
        if (this.path.length() > 0) {
        	Shape shape = ShapeUtil.strToShape(this.path);
        	if (this.drawType.equals(DRAW_TYPE_FACT)) {
        		return shape;
        	} else if (this.drawType.startsWith(DRAW_TYPE_ADJUST)) {
                double scalex, scaley;
                Rectangle2D bounds = shape.getBounds2D();
                if (bounds.getWidth() / (double) bounds.getHeight() > this.width / (double) this.height) {
                    scalex = (double) this.width / bounds.getWidth();
                } else {
                    scalex = (double) this.height / bounds.getHeight();
                }
                scaley = scalex;
                if (this.width != this.height && this.margin > 0) {
                	if (this.width > this.height) {
                		scalex += (((double) this.margin * 2) * this.height / this.width - this.margin * 2) / this.width;
                	} else {
                		scaley += (((double) this.margin * 2) * this.width / this.height - this.margin * 2) / this.height;
                	}
                }
                int offx = (this.width - (int) (bounds.getWidth() * scalex)) / 2;
                int offy = (this.height - (int) (bounds.getHeight() * scaley)) / 2;
                if (this.drawType.endsWith("left")) {
                    offx = 0;
                } else if (this.drawType.endsWith("top")) {
                    offy = 0;
                } else if (this.drawType.endsWith("right")) {
                    offx *= 2;
                } else if (this.drawType.endsWith("bottom")) {
                    offy *= 2;
                }
                bounds = shape.getBounds2D();
        		AffineTransform af = new AffineTransform();
        		af.scale(scalex, scaley);
        		shape = af.createTransformedShape(shape);
        		//移动
        		bounds = shape.getBounds2D();
        		af = new AffineTransform();
        		af.translate(-bounds.getX() + offx, -bounds.getY() + offy);
        		shape = af.createTransformedShape(shape);
        	} else {
        		//缩放
        		Rectangle2D bound = shape.getBounds2D();
        		AffineTransform af = new AffineTransform();
        		af.scale(this.width / bound.getWidth(), 
        				this.height / bound.getHeight());
        		shape = af.createTransformedShape(shape);
        		//移动
        		bound = shape.getBounds2D();
        		af = new AffineTransform();
        		af.translate(-bound.getX(), -bound.getY());
        		shape = af.createTransformedShape(shape);
        	}
            return shape;
        } else {
            return super.getShape();
        }
    }
	protected void bindValue(String val) {
		this.path = val;
	}
}
