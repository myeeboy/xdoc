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
import com.hg.util.To;

/**
 * 多边形
 * @author whg
 */
public class ElePolygon extends EleShape {
    protected EleBase copyEle(XDoc xdoc) {
        return new ElePolygon(xdoc, this.getAttMap());
    }

    public ElePolygon(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public ElePolygon(XDoc xdoc) {
        super(xdoc);
    }
    protected void init() {
        super.init();
        typeName = "polygon";
        this.points = "";
    }
    public String points;
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        points = MapUtil.getString(map, "points", this.points);
    }

    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("points", points);
        return map;
    }
    public Object clone() {
        return new ElePolygon(this.xdoc, this.getAttMap());
    }
    protected Shape getShape() {
        int n = To.toInt(this.points, 3);
        if (n > 2) {
            double d = -90;
            if (n % 2 == 0) {
                d += 180 / n;
            }
            GeneralPath path = new GeneralPath();
            path.moveTo((float) Math.cos(Math.toRadians(d)), (float) Math.sin(Math.toRadians(d)));
            for (int i = 1; i <= n; i++) {
                path.lineTo((float) Math.cos(Math.toRadians(360.0 / n * i + d)), (float) Math.sin(Math.toRadians(360.0 / n * i + d)));
            }
            path.closePath();
            Shape shape = path;
            AffineTransform af = new AffineTransform();
            Rectangle2D bound = shape.getBounds2D();
            af = new AffineTransform();
            af.scale(this.width / bound.getWidth(), this.height / bound.getHeight());
            shape = af.createTransformedShape(shape);
            bound = shape.getBounds2D();
            af = new AffineTransform();
            af.translate(-bound.getX(), -bound.getY());
            shape = af.createTransformedShape(shape);
            return shape;
        } else {
            return super.getShape();
        }
    }
	protected void bindValue(String val) {
		this.points = val;
	}
}
