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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

import com.hg.util.MapUtil;
/**
 * 组合图形
 * @author whg
 */
public class EleGroup extends EleShape {
    protected EleBase copyEle(XDoc xdoc) {
        return new EleGroup(xdoc, this.getAttMap());
    }
    public Object clone() {
        return new EleGroup(this.xdoc, this.getAttMap());
    }
    public EleGroup(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public EleGroup(XDoc xdoc) {
        super(xdoc);
    }
    public String drawType;
    public static String DRAW_TYPE_ZOOM = "zoom";
    public static String DRAW_TYPE_CENTER = "center";
    public static String DRAW_TYPE_FACT = "fact";
    public static String DRAW_TYPE_ADJUST = "adjust";
    protected void init() {
        super.init();
        typeName = "group";
        this.color = null;
        this.text = "";
        this.drawType = DRAW_TYPE_ZOOM;
    }
    public boolean lockRatio;
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        if (map.containsKey("text")) {
            this.text = MapUtil.getString(map, "text", this.text);
        } else if (map.containsKey("gname")) {
            this.text = MapUtil.getString(map, "gname", this.text);
        }
        this.drawType = MapUtil.getString(map, "drawType", this.drawType);
        if (MapUtil.getBool(map, "lockRatio", false)) {
        	this.drawType = DRAW_TYPE_ADJUST;
        }
    }

    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("text", this.text);
        map.put("drawType", this.drawType);
        return map;
    }
    public String text;
    protected void drawOther(Graphics2D g, Shape shape) {
    	EleRect rect = DocUtil.getRect(this.xdoc, this.text);
        if (rect != null) {
			rect.xdoc.scale = this.xdoc.scale;
			g = (Graphics2D) g.create(0, 0, this.width + 1, this.height + 1);
        	if (this.drawType.startsWith(DRAW_TYPE_ADJUST)) {
                double scale;
                if (rect.width / (double) rect.height > this.width / (double) this.height) {
                    scale = (double) (this.width - this.margin*2) / rect.width;
                } else {
                    scale = (double) (this.height - this.margin*2) / rect.height;
                }
                double offx = (this.width - rect.width * scale) / 2;
                double offy = (this.height - rect.height * scale) / 2;
                if (this.drawType.endsWith("left")) {
                    offx = 0;
                } else if (this.drawType.endsWith("top")) {
                    offy = 0;
                } else if (this.drawType.endsWith("right")) {
                    offx *= 2;
                } else if (this.drawType.endsWith("bottom")) {
                    offy *= 2;
                }
                AffineTransform af = AffineTransform.getScaleInstance(scale, scale);
                af.translate(offx / scale, offy / scale);
				g.transform(af);
			} else if (!this.drawType.equals(DRAW_TYPE_FACT)) {
				g.transform(AffineTransform.getScaleInstance((double) (this.width - this.margin*2)/ rect.width, 
						(double) (this.height - this.margin*2) / rect.height));
			}
			rect.print(g);
            g.dispose();
        }
    }
}
