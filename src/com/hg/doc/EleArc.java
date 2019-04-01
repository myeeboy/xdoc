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
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.HashMap;

import com.hg.util.MapUtil;
import com.hg.util.To;

/**
 * 圆弧
 * @author whg
 */
public class EleArc extends EleShape {
    protected EleBase copyEle(XDoc xdoc) {
        return new EleArc(xdoc, this.getAttMap());
    }

    public EleArc(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public EleArc(XDoc xdoc) {
        super(xdoc);
    }
    protected void init() {
        super.init();
        typeName = "arc";
        this.arcType = ARC_TYPE_OPEN;
        this.angleStart = 0;
        this.angleExtent = 360;
    }
    public int angleStart;
    public int angleExtent;
	protected void bindValue(String val) {
		this.angleExtent = To.toInt(val);
	}
    public String arcType;
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.arcType = MapUtil.getString(map, "arcType", this.arcType);
        this.arcType = MapUtil.getString(map, "type", this.arcType).toLowerCase();
        this.angleStart = MapUtil.getInt(map, "angleStart", this.angleStart);
        this.angleStart = MapUtil.getInt(map, "start", this.angleStart);
        this.angleExtent = MapUtil.getInt(map, "angleExtent", this.angleExtent);
        this.angleExtent = MapUtil.getInt(map, "extent", this.angleExtent);
    }

    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("type", this.arcType);
        map.put("start", String.valueOf(this.angleStart));
        map.put("extent", String.valueOf(this.angleExtent));
        return map;
    }
    public Object clone() {
        return new EleArc(this.xdoc, this.getAttMap());
    }
    public static String ARC_TYPE_OPEN = "open";
    public static String ARC_TYPE_CHORD = "chord";
    public static String ARC_TYPE_PIE = "pie";
    public static String ARC_TYPE_RING = "ring";
    public static String ARC_TYPE_THIN_RING = "thinring";
    public static String ARC_TYPE_THICK_RING = "thickring";
    protected Shape getShape() {
        Arc2D arc = new Arc2D.Double();
        int extent = this.angleExtent;
        if (extent == 0) {
            extent = 360;
        }
        int start = -this.angleStart + 90;
        arc.setAngleStart(start);
        extent = -extent;
        setArcType(arc);
        arc.setAngleExtent(extent);
        arc.setFrame(0, 0, this.width, this.height);
        if (arcType.toLowerCase().endsWith("ring")) {
            Arc2D arc2 = new Arc2D.Double();
            arc2.setAngleStart(start + extent);
            setArcType(arc2);
            arc2.setAngleExtent(-extent);
            if (arcType.equalsIgnoreCase("thinring")) {
            	arc2.setFrame(this.width * 0.125, this.height * 0.125, this.width * 0.75, this.height * 0.75);
            } else if (arcType.equalsIgnoreCase("thickring")) {
            	arc2.setFrame(this.width * 0.375, this.height * 0.375, this.width * 0.25, this.height * 0.25);
            } else {
            	arc2.setFrame(this.width * 0.25, this.height * 0.25, this.width * 0.5, this.height * 0.5);
            }
            GeneralPath path = new GeneralPath();
            path.append(arc, false);
            if (extent % 360 != 0) {
                path.append(new Line2D.Double(arc.getEndPoint(), arc2.getStartPoint()), true);
            }
            path.append(arc2, extent % 360 != 0);
            if (extent % 360 != 0) {
                path.append(new Line2D.Double(arc2.getEndPoint(), arc.getStartPoint()), true);
            }
            return path;
        } else {
            return arc;
        }
    }
    private void setArcType(Arc2D arc) {
        if (this.arcType != null) {
            if (arcType.equals(ARC_TYPE_CHORD)) {
                arc.setArcType(Arc2D.CHORD);
            } else if (arcType.equals(ARC_TYPE_PIE) && this.angleExtent % 360 != 0) {
                arc.setArcType(Arc2D.PIE);
            } else {
                arc.setArcType(Arc2D.OPEN);
            }
        }
    }
}
