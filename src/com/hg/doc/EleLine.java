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
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.hg.util.MapUtil;

public class EleLine extends EleShape {
    public int startX;
    public int startY;
    public int endX;
    public int endY;
    protected EleBase copyEle(XDoc xdoc) {
        return new EleLine(xdoc, this.getAttMap());
    }
    public EleLine(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public EleLine(XDoc xdoc) {
        super(xdoc);
    }
    protected void init() {
        super.init();
        typeName = "line";
        startX = 0;
        startY = 0;
        endX = 20;
        endY = 20;
        this.startArrow = ARROW_TYPE_NONE;
        this.endArrow = ARROW_TYPE_NONE;
        this.lineStyle = STYLE_LINE;
    }
    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("startX", String.valueOf(this.startX));
        map.put("startY", String.valueOf(this.startY));
        map.put("endX", String.valueOf(this.endX));
        map.put("endY", String.valueOf(this.endY));
        map.put("startArrow", this.startArrow);
        map.put("endArrow", this.endArrow);
        map.put("lineStyle", this.lineStyle);
        map.remove("left");
        map.remove("top");
        map.remove("width");
        map.remove("height");
        return map;
    }
    protected void printSelf(Graphics2D g) {
    	if (this.isLineShape()) {
    		super.printSelf(g);
    	}
    }
    /**
     * 解析属性map
     */
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.startX = MapUtil.getInt(map, "startX", this.startX);
        this.startY = MapUtil.getInt(map, "startY", this.startY);
        this.endX = MapUtil.getInt(map, "endX", this.endX);
        this.endY = MapUtil.getInt(map, "endY", this.endY);
        Rectangle rect = getViewBounds(this);
        this.width = rect.width;
        this.height = rect.height;
        this.left = rect.x;
        this.top = rect.y;
        this.startArrow = MapUtil.getString(map, "startArrow", this.startArrow);
        this.endArrow = MapUtil.getString(map, "endArrow", this.endArrow);
        this.lineStyle = MapUtil.getString(map, "lineStyle", this.lineStyle);
    }
    public Object clone() {
        return new EleLine(this.xdoc, this.getAttMap());
    }
    public static Rectangle getViewBounds(EleLine line) {
        return new Rectangle(Math.min(line.startX, line.endX), Math.min(line.startY, line.endY),
                Math.abs(line.startX - line.endX), Math.abs(line.startY - line.endY));
    }
    protected Rectangle getBound() {
        return getViewBounds(this);
    }
    public static final String ARROW_TYPE_NONE = "";
    public static final String ARROW_TYPE_BIAS = "bias";
    public static final String ARROW_TYPE_TRIANGLE = "triangle";
    public static final String ARROW_TYPE_CIRCLE = "circle";
    public static final String ARROW_TYPE_RHOMBUS = "rhombus";
    public static final String ARROW_TYPE_RECT = "rect";
    public String startArrow;
    public String endArrow;
    public static String STYLE_LINE = "line";
    public static String STYLE_BROKEN = "broken";
    public static String STYLE_ARC = "arc";
    public static String STYLE_ARC2 = "arc2";
    public String lineStyle;
    public void scalePrint(Graphics2D g, int offX, int offY) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.scale(this.xdoc.scale, this.xdoc.scale);
        print(g2, this.xdoc.unintScale(offX), this.xdoc.unintScale(offY));
        g2.dispose();
    }
    private static final int nw_se = 0;
    private static final int se_nw = 1;
    private static final int sw_ne = 2;
    private static final int ne_sw = 3;
    public boolean printLine = true;
    protected void actPrint(Graphics2D g) {
        super.actPrint(g);
        if (printLine) {
        	print(g, 0, 0);
        }
    }
    public void print(Graphics2D g, int offX, int offY) {
        if (this.lineStyle.length() == 0) {
            this.lineStyle = STYLE_LINE;
        }
        if (this.color != null && this.isVisible()) {
            Rectangle rect = getViewBounds(this);
            GeneralPath path = new GeneralPath();
            int direction = nw_se;
            if (startX <= endX && startY <= endY) {
                direction = nw_se;
            } else if (startX >= endX && startY >= endY) {
                direction = se_nw;
            } else if (startX <= endX && startY >= endY) {
                direction = sw_ne;
            } else if (startX >= endX && startY <= endY) {
                direction = ne_sw;
            }

            
            if (direction == nw_se || direction == se_nw) {
                path.moveTo(0, 0);
                if (lineStyle.equals(STYLE_ARC)) {
                    if (direction == nw_se) {
                        path.quadTo(0, rect.height, rect.width, rect.height);
                    } else {
                        path.quadTo(rect.width, 0, rect.width, rect.height);
                    }
                } else if (lineStyle.equals(STYLE_ARC2)) {
                    path.curveTo(rect.width / 2, 0, rect.width / 2, rect.height, rect.width, rect.height);
                } else if (lineStyle.equals(STYLE_BROKEN)) {
                    path.lineTo(rect.width / 2, 0);
                    path.lineTo(rect.width / 2, rect.height);
                    path.lineTo(rect.width, rect.height);
                } else {
                    path.lineTo(rect.width, rect.height);
                }
            } else {
                path.moveTo(0, rect.height);
                if (lineStyle.equals(STYLE_ARC)) {
                    if (direction == sw_ne) {
                        path.quadTo(0, 0, rect.width, 0);
                    } else {
                        path.quadTo(rect.width, rect.height, rect.width, 0);
                    }
                } else if (lineStyle.equals(STYLE_ARC2)) {
                    path.curveTo(rect.width / 2, rect.height, rect.width / 2, 0, rect.width, 0);
                } else if (lineStyle.equals(STYLE_BROKEN)) {
                    path.lineTo(rect.width / 2, rect.height);
                    path.lineTo(rect.width / 2, 0);
                    path.lineTo(rect.width, 0);
                } else {
                    path.lineTo(rect.width, 0);
                }
            }
            AffineTransform af = new AffineTransform();
            af.translate(offX, offY);
            Shape shape = path;
            shape = af.createTransformedShape(shape);
        	if (this.strokeWidth > 0) {
        		if (this.strokeWidth >= 1 && this.strokeImg != null && this.strokeImg.length() > 0) {
        			BufferedImage imgStroke = ImgUtil.loadImg(this.xdoc, this.strokeImg, this.color, null, false);
        			if (imgStroke != null) {
        				imgStroke = ImgUtil.alpha(imgStroke, Color.WHITE);
        				drawImageShape(g, imgStroke, shape, (int) this.strokeWidth);
        				return;
        			}
        		}
        		setStroke(g);
        		g.draw(shape);
        		PaintUtil.resetStroke(g);
        	}
        	
            String tmpStartArrow = this.startArrow;
            String tmpEndArrow = this.endArrow;
            if (direction == se_nw || direction == ne_sw) {
                tmpStartArrow = this.endArrow;
                tmpEndArrow = this.startArrow;
            }
            if (!tmpStartArrow.equals(ARROW_TYPE_NONE)) {
                double rotate = 0;
                if (lineStyle.equals(STYLE_ARC)) {
                    if (direction == nw_se && this.startY != this.endY || direction == se_nw && this.startX == this.endX) {
                        rotate = -Math.PI / 2;
                    } else if (direction == sw_ne && this.startX != this.endX ) {
                        rotate = Math.PI / 2;
                    }
                }
                shape = getSAShape(tmpStartArrow, direction, rotate);
                if (shape != null) {
                    shape = af.createTransformedShape(shape);
                    g.draw(shape);
                    g.fill(shape);
                }
            }
            if (!tmpEndArrow.equals(ARROW_TYPE_NONE) && !tmpEndArrow.equals("0")) {
                double rotate = 0;
                if (lineStyle.equals(STYLE_ARC)) {
                    if (direction == ne_sw && (this.startX != this.endX)) {
                        rotate = -Math.PI / 2;
                    } else if (direction == se_nw && this.startY != this.endY || direction == nw_se && this.startX == this.endX) {
                        rotate = Math.PI / 2;
                    }
                }
                shape = getEAShape(tmpEndArrow, direction, rotate);
                if (shape != null) {
                    shape = af.createTransformedShape(shape);
                    g.draw(shape);
                    g.fill(shape);
                }
            }
        }
    }
    public static Shape baseArrow(String arrowType, double weight) {
        Shape shape = null;
        int len = (int) Math.round(8 * weight / 2);
        if (len < 8) len = 8;
        if (arrowType.equals(ARROW_TYPE_BIAS) || arrowType.equals("1")) {
            GeneralPath path = new GeneralPath();
            path.append(new Line2D.Double(0, 0, len * Math.cos(30 * Math.PI / 180), len * Math.sin(30 * Math.PI / 180)), true);
            path.append(new Line2D.Double(0, 0, len * Math.cos(-30 * Math.PI / 180), len * Math.sin(-30 * Math.PI / 180)), true);
            path.closePath();
            shape = path;
        } else if (arrowType.equals(ARROW_TYPE_TRIANGLE) || arrowType.equals("2")) {
            shape = new Polygon(new int[] {0, (int) (len * Math.cos(30 * Math.PI / 180)), (int) (len * Math.cos(-30 * Math.PI / 180))},
                    new int[] {0, (int) (len * Math.sin(30 * Math.PI / 180)), (int) (len * Math.sin(-30 * Math.PI / 180))}, 3);
        } else if (arrowType.equals(ARROW_TYPE_CIRCLE) || arrowType.equals("3")) {
            shape = new Ellipse2D.Double(-len/4, -len/4, len, len);
            AffineTransform af = new AffineTransform();
            af.rotate(-45 * Math.PI / 180, 0, 0);
            shape = af.createTransformedShape(shape);
        } else if (arrowType.equals(ARROW_TYPE_RHOMBUS) || arrowType.equals("4")) {
            shape = new Rectangle2D.Double(0, 0, len, len);
            AffineTransform af = new AffineTransform();
            af.rotate(-45 * Math.PI / 180, 0, 0);
            shape = af.createTransformedShape(shape);
        } else if (arrowType.equals(ARROW_TYPE_RECT) || arrowType.equals("5")) {
            shape = new Rectangle(0, -len/2, len, len);
        }
        return shape;
    }
    private Shape getSAShape(String type, int direction, double rotate2) {
        Shape shape = baseArrow(type, this.strokeWidth);
        if (shape != null) {
            Rectangle rect = getViewBounds(this);
            boolean b = false; // “\”
            if (direction == nw_se || direction == se_nw) {
                b = true;
            }
            if (shape != null) {
                AffineTransform af = new AffineTransform();
                if (this.lineStyle.equals(STYLE_LINE)) {
                    double rotate = Math.atan((double) rect.height / rect.width);
                    if (b) {
                        af.rotate(rotate, 0, 0);
                    } else {
                        af.rotate(-rotate, 0, 0);
                    }
                    shape = af.createTransformedShape(shape);
                }
                if (rotate2 != 0) {
                    af = new AffineTransform();
                    af.rotate(-rotate2, 0, 0);
                    shape = af.createTransformedShape(shape);
                }
                if (!b) {
                    af = new AffineTransform();
                    af.translate(0, rect.height);
                    shape = af.createTransformedShape(shape);
                }
            }
        }
        return shape;
    }
    private Shape getEAShape(String type, int direction, double rotate2) {
        Shape shape = baseArrow(type, this.strokeWidth);
        if (shape != null) {
            Rectangle rect = getViewBounds(this);
            boolean b = false; // “\”
            if (direction == nw_se || direction == se_nw) {
                b = true;
            }
            if (shape != null) {
                AffineTransform af = new AffineTransform();
                double rotate = 0;
                if (this.lineStyle.equals(STYLE_LINE)) {
                    rotate = Math.atan((double) rect.height / rect.width);
                }
                if (b) {
                    af.rotate(Math.PI + rotate + rotate2, 0, 0);
                } else {
                    af.rotate(Math.PI - rotate + rotate2, 0, 0);
                }
                shape = af.createTransformedShape(shape);
                af = new AffineTransform();
                if (b) {
                    af.translate(rect.width, rect.height);
                } else {
                    af.translate(rect.width, 0);
                }
                shape = af.createTransformedShape(shape);
            }
        }
        return shape;
    }
}
