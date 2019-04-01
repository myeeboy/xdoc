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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import com.hg.data.BlkExpression;
import com.hg.data.LogicExpression;
import com.hg.data.Parser;
import com.hg.util.ColorUtil;
import com.hg.util.MapUtil;
import com.hg.util.StrUtil;
import com.hg.util.To;

public class EleRect extends EleBase {
    public static final String SIZE_NORMAL = "normal";
    public static final String SIZE_AUTOWIDTH = "autowidth";
    public static final String SIZE_AUTOHEIGHT = "autoheight";
    public static final String SIZE_AUTOSIZE = "autosize";
    public static final String SIZE_AUTOFONT = "autofont";
    public static final String SIZE_AUTOTIP = "autotip";
    public static final String SIZE_AUTOOUT = "autoout";
    public static final String LINE_NONE = "00000000";
    public static final String LINE_RECT = "11110000";
    public static final String LINE_BOTTOM = "00010000";
    public int left;
    public int top;
    public int width;
    public int height;
    public String arc;
    public Color fillColor;
    public String fillImg;
    public Color fillColor2;
    public String sizeType;
    public int rotate;
    public int srotate;
    public String gradual;
    public String href;
    public String line;
    public String filter;
    public String filterTarget;
    public String filterParam;
    public int column;
    public int txtPadding;
    public int margin;
    public String layoutFlow;
    public boolean layoutLine;
    public String toolTip;
    public int row;
    public int col;
    public int rowSpan;
    public int colSpan;
    public String fillRatio;
    public String scale;
    public Color color;
    //对齐
    public String align;
    //段落中垂直对齐
    public String valign;
    //相对文字位置
    public String zPosition;
    public double strokeWidth;
    public String strokeStyle;
    public String strokeImg;
    public String visible;
    public String distort;
    public String dock;
    public String comment;
    protected EleBase copyEle(XDoc xdoc) {
        return new EleRect(xdoc, this.getAttMap());
    }
    public Object clone() {
        return new EleRect(xdoc, this.getAttMap());
    }
    public EleRect(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public EleRect(XDoc xdoc) {
        super(xdoc);
    }
    protected void init() {
        super.init();
        typeName = "rect";
        this.fillColor = null;
        this.fillImg = "";
        this.align = DocConst.ALIGN_TOP;
        this.sizeType = SIZE_NORMAL;
        this.left = 0;
        this.arc = "0";
        this.top = 0;
        this.width = DEF_WIDTH;
        this.height = DEF_HEIGHT;
        this.rotate = 0;
        this.txtPadding = 2;
        this.layoutFlow = "h";
        this.href = "";
        this.toolTip = "";
        this.line = LINE_RECT;
        this.column = 1;
        this.gradual = "";
        this.filter = "";
        this.filterTarget = "all";
        this.filterParam = "";
        this.margin = 0;
        this.colSpan = 1;
        this.rowSpan = 1;
        this.layoutLine = false;
        this.fillRatio = "";
        this.scale = "";
        this.color = Color.BLACK;
        this.strokeWidth = 1;
        this.strokeStyle = "0";
        this.align = DocConst.ALIGN_TOP;
        this.strokeImg = "";
        this.zPosition = DocConst.ALIGN_TOP;
        this.valign = DocConst.ALIGN_BOTTOM;
        this.distort = "";
        this.visible = "true";
        this.dock = "";
        this.comment = "";
    }
    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("left", String.valueOf(this.left));
        map.put("top", String.valueOf(this.top));
        map.put("sizeType", this.sizeType);
        map.put("width", String.valueOf(this.width));
        map.put("height", String.valueOf(this.height));
        map.put("fillColor", this.fillColor == null ? "" : ColorUtil.colorToStr(this.fillColor));
        map.put("fillColor2", this.fillColor2 == null ? "" : ColorUtil.colorToStr(this.fillColor2));
        map.put("fillImg", this.fillImg);
        map.put("line", this.line);
        if (this.gradual.equals("0")) {
        	this.gradual = "";
        }
        map.put("gradual", this.gradual);
        map.put("arc", this.arc);
        map.put("href", this.href);
        map.put("filter", this.filter);
        map.put("filterTarget", this.filterTarget);
        map.put("filterParam", this.filterParam);
        map.put("column", String.valueOf(this.column));
        map.put("rotate", String.valueOf(this.rotate));
        map.put("srotate", String.valueOf(this.srotate));
        map.put("padding", String.valueOf(this.txtPadding));
        map.put("margin", String.valueOf(this.margin));
        map.put("toolTip", this.toolTip);
        map.put("layoutFlow", this.layoutFlow);
        map.put("row", String.valueOf(this.row+1));
        map.put("rowSpan", String.valueOf(this.rowSpan));
        map.put("col", String.valueOf(this.col+1));
        map.put("colSpan", String.valueOf(this.colSpan));
        map.put("layoutLine", String.valueOf(this.layoutLine));
        map.put("fillRatio", this.fillRatio);
        map.put("scale", this.scale);
        map.put("color", this.color == null ? "" : ColorUtil.colorToStr(this.color));
        map.put("strokeWidth", String.valueOf(this.strokeWidth));
        map.put("strokeStyle", this.strokeStyle);
        map.put("strokeImg", this.strokeImg);
        map.put("align", this.align);
        map.put("zPosition", this.zPosition);
        map.put("valign", this.valign);
        map.put("visible", this.visible);
        map.put("distort", this.distort);
        map.put("dock", this.dock);
        map.put("comment", this.comment);
        return map;
    }
    /**
     * 解析属性map
     */
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.left = MapUtil.getInt(map, "left", this.left);
        this.top = MapUtil.getInt(map, "top", this.top);
        this.sizeType = MapUtil.getString(map, "sizeType", this.sizeType).toLowerCase();
        this.width = MapUtil.getInt(map, "width", this.width);
        this.height = MapUtil.getInt(map, "height", this.height);
        this.arc = MapUtil.getString(map, "arc", this.arc);
        if (map.containsKey("fillColor")) {
            this.fillColor = ColorUtil.strToColor(MapUtil.getString(map, "fillColor", ""), null);
        }
        if (map.containsKey("fillColor2")) {
            this.fillColor2 = ColorUtil.strToColor(MapUtil.getString(map, "fillColor2", ""), null);
        }
        this.gradual = MapUtil.getString(map, "gradual", this.gradual);
        this.fillImg = MapUtil.getString(map, "fillImg", this.fillImg);
        this.line = MapUtil.getString(map, "line", this.line);
        this.href = MapUtil.getString(map, "href", this.href);
        this.filter = MapUtil.getString(map, "filter", this.filter);
        this.filterTarget = MapUtil.getString(map, "filterTarget", this.filterTarget);
        this.filterParam = MapUtil.getString(map, "filterParam", this.filterParam);
        this.column = MapUtil.getInt(map, "column", this.column);
        this.rotate = MapUtil.getInt(map, "rotate", this.rotate);
        this.srotate = MapUtil.getInt(map, "srotate", this.srotate);
        this.txtPadding = MapUtil.getInt(map, "padding", this.txtPadding);
        this.margin = MapUtil.getInt(map, "margin", this.margin);
        this.layoutFlow = MapUtil.getString(map, "layoutFlow", this.layoutFlow);
        this.toolTip = MapUtil.getString(map, "toolTip", this.toolTip);
        this.row = MapUtil.getInt(map, "row", this.row+1)-1;
        this.rowSpan = MapUtil.getInt(map, "rowSpan", this.rowSpan);
        this.col = MapUtil.getInt(map, "col", this.col+1)-1;
        this.colSpan = MapUtil.getInt(map, "colSpan", this.colSpan);
        this.layoutLine = MapUtil.getBool(map, "layoutLine", this.layoutLine);
        this.fillRatio = MapUtil.getString(map, "fillRatio", this.fillRatio);
        this.scale = MapUtil.getString(map, "scale", this.scale);
        if (map.containsKey("color")) {
            this.color = ColorUtil.strToColor(MapUtil.getString(map, "color", ""), null);
        }
        this.strokeWidth = MapUtil.getDouble(map, "strokeWidth", this.strokeWidth);
        if (map.containsKey("weight")) { //兼容9.2.6
            this.strokeWidth = MapUtil.getDouble(map, "weight", this.strokeWidth);
        }
        this.strokeStyle = MapUtil.getString(map, "strokeStyle", this.strokeStyle);
        if (map.containsKey("dashStyle")) { //兼容9.2.6
            this.strokeStyle = MapUtil.getString(map, "dashStyle", this.strokeStyle);
        }
        this.strokeImg = MapUtil.getString(map, "strokeImg", this.strokeImg);
        this.align = MapUtil.getString(map, "align", this.align);
        this.zPosition = MapUtil.getString(map, "zPosition", this.zPosition);
        this.valign = MapUtil.getString(map, "valign", this.valign);
        this.visible = MapUtil.getString(map, "visible", this.visible);
        this.distort = MapUtil.getString(map, "distort", this.distort);
        this.dock = MapUtil.getString(map, "dock", this.dock);
        this.comment = MapUtil.getString(map, "comment", this.comment);
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.eleList.size(); i++) {
            if (i > 0) sb.append("\n");
            sb.append(this.eleList.get(i));
        }
        return sb.toString();
    }
    public void setText(String text) {
    	setText(this, text);
    }
    public static void setText(EleRect rect, String str) {
        ElePara para = null;
        EleText txt = null;
        Object obj;
        for (int i = 0; i < rect.eleList.size(); i++) {
            obj = rect.eleList.get(i);
            if (obj instanceof ElePara) {
                para = (ElePara) obj;
                if (para.eleList.size() > 0 && para.eleList.get(0) instanceof EleText) {
                    txt = (EleText) para.eleList.get(0);
                } else {
                    txt = new EleText(rect.xdoc);
                    para.eleList.add(0, txt);
                }
                break;
            }
        }
        if (txt == null) {
            para = new ElePara(rect.xdoc);
            txt = new EleText(rect.xdoc);
            para.eleList.add(txt);
            rect.eleList.add(para);
        }
        txt.setText(str);
        rect.relayout();
    }
    public void print(Graphics2D g) {
        if (this.isVisible()) {
            if (this.xdoc.print != null) {
            	Shape fshape = null;
                if (this.href.length() > 0) {
                	if (fshape == null) {
                		fshape = this.getFillShape();
                	}
                    this.xdoc.print.hrefs.add(new NameShape(DocUtil.fixHref(this.href, this.name), g.getTransform().createTransformedShape(fshape)));
                }
                if (this.toolTip.length() > 0) {
                	if (fshape == null) {
                		fshape = this.getFillShape();
                	}
                    this.xdoc.print.toolTips.add(new NameShape(this.toolTip, g.getTransform().createTransformedShape(fshape)));
                }
            }
            actPrint(g);
        }
    }
	protected void actPrint(Graphics2D g) {
    	if (this.scale.length() > 0) {
    		String[] scales = this.scale.split(",");
    		double xscale = 1, yscale = 1;
    		if (scales.length > 0) {
    			xscale = To.toDouble(scales[0], 1);
    			if (xscale > 1) {
    				xscale -= (int) xscale;
    			}
    			yscale = xscale;
    		}
    		if (scales.length > 1) {
    			yscale = To.toDouble(scales[1], yscale);
    			if (yscale > 1) {
    				yscale -= (int) yscale;
    			}
    		}
    		String sb = "c";
    		if (scales.length > 2) {
    			sb = scales[2].toLowerCase();
    		}
    		if (xscale <= 0 || yscale <= 0) {
    			return;
    		}
    		double x;
    		double y;
    		double w = this.width * xscale;
    		double h = this.height * yscale;
    		if (sb.equals("nw")) {
    			x = 0;
    			y = 0;
    		} else if (sb.equals("n")) {
    			x = 0;
    			y = 0;
    		} else if (sb.equals("ne")) {
    			x = this.width - w;
    			y = 0;
    		} else if (sb.equals("w")) {
    			x = 0;
    			y = (this.height - h) / 2;
    		} else if (sb.equals("e")) {
    			x = this.width - w;
    			y = (this.height - h) / 2;
    		} else if (sb.equals("sw")) {
    			x = 0;
    			y = this.height - h;
    		} else if (sb.equals("s")) {
    			x = (this.width - w) / 2;
    			y = this.height - h;
    		} else if (sb.equals("se")) {
    			x = this.width - w;
    			y = this.height - h;
    		} else {
    			x = (this.width - w) / 2;
    			y = (this.height - h) / 2;
    		}
    		g = (Graphics2D) g.create((int) Math.round(x), (int) Math.round(y), (int) Math.round(w) + 1, (int) Math.round(h) + 1);
    		g.scale(xscale, yscale);
    	}
    	printSelf(g);
    	printContent(g);
    }
    protected void printSelf(Graphics2D g) {
    	drawShape(g, fillShape(g));
    }
    protected void printContent(Graphics2D g) {
        printRect(g, true);
        printText(g);
        printRect(g, false);
	}
	protected Rectangle getBound() {
        return new Rectangle(this.left, this.top, this.width, this.height);
    }
    protected void printText(Graphics2D g) {
        DocPageLine line;
        ArrayList lineList = getLineList();
        int lineTop = 0;
        int lineHeight = 0;
        int txtLeft = this.getTextLeft();
        int txtWidth = 0;
        boolean vflow = this.layoutFlow.equals("v");
        if (vflow) {
            txtWidth = this.getTextHeight();
        } else {
            txtWidth = this.getTextWidth();
        }
        for (int i = 0; i < lineList.size(); i++) {
            line = (DocPageLine) lineList.get(i);
            if (this.layoutLine && this.color != null) {
                this.setStroke(g);
                lineHeight = line.height;
                if (vflow) {
                    lineTop = line.top - lineHeight;
                    g.draw(new Line2D.Double(lineTop, txtLeft, lineTop, txtLeft + txtWidth));
                } else {
                    lineTop = line.top + lineHeight;
                    g.draw(new Line2D.Double(txtLeft, lineTop - 1, txtLeft + txtWidth, lineTop - 1));
                }
                PaintUtil.resetStroke(g);
            }
            line.print(g, this.getTextLeft());
        }
        if (this.layoutLine && this.color != null) {
            this.setStroke(g);
            if (vflow) {
                lineTop -= lineHeight;
                while (lineTop > txtLeft) {
                    g.draw(new Line2D.Double(lineTop + 1, txtLeft, lineTop + 1, txtLeft + txtWidth));
                    lineTop -= lineHeight;
                }
            } else {
                lineTop += lineHeight;
                while (lineTop < this.height - txtLeft) {
                    g.draw(new Line2D.Double(txtLeft, lineTop - 1, txtLeft + txtWidth, lineTop - 1));
                    lineTop += lineHeight;
                }
            }
            PaintUtil.resetStroke(g);
        }
    }
    protected void textAutosize() {
        boolean vertical = this.layoutFlow.equals("v");
        if (vertical && this.sizeType.equals(SIZE_AUTOWIDTH) 
                || !vertical && this.sizeType.equals(SIZE_AUTOHEIGHT)) {
            ArrayList list;
            DocPageLine line = null;
            int top = this.getTextTop();
            ArrayList hrList = new ArrayList();
            for (int i = 0; i < this.eleList.size(); i++) {
                if (this.eleList.get(i) instanceof EleRect) {
                    EleRect rect = (EleRect) this.eleList.get(i);
                    if (rect.zPosition.equals(DocConst.ALIGN_AROUND)) {
                        HindRance hr = new HindRance(new Rectangle(rect.left, rect.top, 
                                rect.width, rect.height));
                        hrList.add(hr);
                    }
                }
            }
            if (column <= 1) {
                if (vertical) {
                    top = this.width - this.getTextTop();
                }
                for (int i = 0; i < this.eleList.size(); i++) {
                    if (this.eleList.get(i) instanceof ElePara) {
                        if (this.layoutFlow.equals("v")) {
                            list = ((ElePara) this.eleList.get(i)).toLineList(top, this.getTextHeight(), hrList, false);
                        } else {
                            list = ((ElePara) this.eleList.get(i)).toLineList(top, this.getTextWidth(), hrList, true);
                        }
                        for (int j = 0; j < list.size(); j++) {
                            line = (DocPageLine) list.get(j);
                            if (vertical) {
                                top -= line.height;
                            } else {
                                top += line.height;
                            }
                        }
                    }
                }
                if (vertical && this.sizeType.equals(SIZE_AUTOWIDTH)) {
                    this.width -= top - getTextTop();
                } else if (!vertical && this.sizeType.equals(SIZE_AUTOHEIGHT)) {
                    this.height = top + getTextTop();
                }
            } else {
                int w = (this.width - txtPadding() * 2 * column) / column;
                for (int i = 0; i < this.eleList.size(); i++) {
                    if (this.eleList.get(i) instanceof ElePara) {
                        list = ((ElePara) this.eleList.get(i)).toLineList(top, w, hrList, true);
                        for (int j = 0; j < list.size(); j++) {
                            line = (DocPageLine) list.get(j);
                            top += line.height;
                        }
                    }
                }
                this.height = top + getTextTop() + (line != null ? line.height : 0) / column;
            }
        }
    }

    public int txtPadding() {
        return this.txtPadding + this.margin;
    }
    protected int getTextTop() {
        return txtPadding() + (int) this.strokeWidth;
    }
    protected int getTextLeft() {
        return getTextTop();
    }
    protected int getTextHeight() {
        return Math.abs(this.height - txtPadding() * 2 - (int) this.strokeWidth - (int) this.strokeWidth);
    }
    protected int getTextWidth() {
        return Math.abs(this.width - txtPadding() * 2 - (int) this.strokeWidth - (int) this.strokeWidth);
    }
    public void setStroke(Graphics2D g2) {
        BufferedImage imgStroke = ImgUtil.loadImg(this.xdoc, this.strokeImg, this.color, null, false);
        if (imgStroke != null) {
            TexturePaint paint = new TexturePaint(imgStroke, new Rectangle(0, 0, imgStroke.getWidth(), imgStroke.getHeight()));
            paint = PaintUtil.checkPaint(g2, paint);
            g2.setPaint(paint);
        } else {
            g2.setColor(this.color);
        }
        Stroke b = null;
        double sw = this.strokeWidth;
        if (this.strokeStyle.length() == 0 || this.strokeStyle.equals("0")) {
            b = new BasicStroke((float) sw, BasicStroke.CAP_ROUND, 
                    BasicStroke.JOIN_MITER, 20.0f);
        } else {
            char c = this.strokeStyle.charAt(0);
            if (c == '~' || c == '@' || c == '#' || c == '$') {
                if (sw > 1) {
                	if (c == '~') {
                		b = TextStrokeUtil.createStroke(ShapeUtil.strToShape(this.strokeStyle.substring(1)), (float) sw);
                	} else {
                		boolean stretchToFit = c == '$';
                		boolean repeat = c == '@';
                		String str = this.strokeStyle.substring(1);
                		String fontName = XFont.defaultFontName;
                		if (str.startsWith("(") && str.indexOf(")") > 0) {
                			fontName = str.substring(1, str.indexOf(")"));
                			str = str.substring(str.indexOf(")") + 1);
                		}
                		b = TextStrokeUtil.createStroke(str, XFont.createFont(fontName, Font.PLAIN, (int) Math.ceil(sw), str), stretchToFit, repeat);
                	}
                } else {
                    b = new BasicStroke((float) sw, BasicStroke.CAP_BUTT, 
                            BasicStroke.JOIN_MITER, 20.0f);
                }
            } else {
                String[] str = this.strokeStyle.split(",");
                float[] dash = new float[str.length];
                for (int i = 0; i < str.length; i++) {
                    try {
                        dash[i] = (float) (Double.parseDouble(str[i]));
                        if (dash[i] < 1) dash[i] = 1;
                    } catch (Exception e) {
                        dash[i] = 0;
                    }
                }
                try {
                    b = new BasicStroke((float) sw,
                            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 20.0f,
                            dash, 1);
                } catch (Exception e) {
                    b = new BasicStroke((float) sw, BasicStroke.CAP_BUTT, 
                            BasicStroke.JOIN_MITER, 20.0f);
                }
            }
        }
        try {
            g2.setStroke(b);
        } catch (Exception e) {
        }
    }
    public ArrayList getParaList() {
        ArrayList paras = new ArrayList();
        for (int i = 0; i < this.eleList.size(); i++) {
            if (this.eleList.get(i) instanceof ElePara) {
                paras.add(this.eleList.get(i));
            }
        }
        return paras;
    }
    public ArrayList getRectList() {
        ArrayList rects = new ArrayList();
        for (int i = 0; i < this.eleList.size(); i++) {
            if (!(this.eleList.get(i) instanceof ElePara)) {
                rects.add(this.eleList.get(i));
            }
        }
        return rects;
    }
    public ArrayList lineList;
    private boolean hasPrintExp = false;
    public ArrayList getLineList() {
        if (lineList == null || hasPrintExp) {
            genLineList(null, 0);
        }
        return lineList;
    }
    public static boolean isBlank(EleRect rect) {
        return rect.eleList.size() == 0 || rect.eleList.size() == 1 
                && rect.eleList.get(0) instanceof ElePara 
                && (((ElePara) rect.eleList.get(0)).eleList.size() == 0 || ((ElePara) rect.eleList.get(0)).eleList.size() == 1
                && ((ElePara) rect.eleList.get(0)).eleList.get(0) instanceof EleText
                && ((EleText) ((ElePara) rect.eleList.get(0)).eleList.get(0)).text.length() == 0);
    }
    public static boolean hasTxt(EleRect shape) {
        boolean has = false;
        int npara = 0;
        for (int i = 0; i < shape.eleList.size(); i++) {
            if (shape.eleList.get(i) instanceof ElePara) {
                npara++;
                if (npara > 1) {
                    has = true;
                    break;
                }
                if (((ElePara) shape.eleList.get(i)).eleList.size() > 1 
                        || ((ElePara) shape.eleList.get(i)).eleList.size() == 1 
                        && (!(((ElePara) shape.eleList.get(i)).eleList.get(i) instanceof EleText) || 
                                (((ElePara) shape.eleList.get(i)).eleList.get(i) instanceof EleText) && ((EleText) ((ElePara) shape.eleList.get(i)).eleList.get(i)).text.length() > 0)) {
                    has = true;
                    break;
                }
            }
        }
        return has;
    }
    public static boolean hasOneTxt(EleRect rect) {
        return rect.eleList.size() == 1 
            && rect.eleList.get(0) instanceof ElePara 
            && ((ElePara) rect.eleList.get(0)).eleList.size() == 1
            && ((ElePara) rect.eleList.get(0)).eleList.get(0) instanceof EleText;
    }
    protected int getArc() {
        int a = 0;
        if (this.arc.indexOf(",") > 0) {
            a = To.toInt(this.arc.substring(0, this.arc.indexOf(",")));
        } else {
            a = To.toInt(this.arc);
        }
        return a;
    }
    public Point viewSize() {
        if (this.rotate == 0) {
        	return new Point(this.width, this.height);
        } else {
        	double r = (double) this.rotate / 180 * Math.PI;
        	return new Point((int) Math.abs(Math.ceil(this.width * Math.cos(r) + this.height * Math.sin(r))), (int) Math.abs(Math.ceil(this.width * Math.sin(r) + this.height * Math.cos(r))));
        }
    }
    protected void printRect(Graphics2D g, boolean bottom) {
        EleRect eleRect;
        EleLine eleLine;
        for (int i = 0; i < this.eleList.size(); i++) {
            if (this.eleList.get(i) instanceof EleRect) {
                eleRect = (EleRect) this.eleList.get(i);
                eleRect.autoSize();
                dock(eleRect);
                if (bottom && eleRect.zPosition.equals(DocConst.ALIGN_BOTTOM) || !bottom && !eleRect.zPosition.equals(DocConst.ALIGN_BOTTOM)) {
                    if (eleRect.rotate == 0) {
                        if (eleRect.left < this.width && eleRect.top < this.height) {
                            Graphics2D cg = (Graphics2D) g.create(eleRect.left, eleRect.top, 
                                    eleRect.width + 1, eleRect.height + 1);
                            eleRect.print(cg);
                            cg.dispose();
                        }
                    } else {
                        int d = (int) Math.ceil(Math.pow(Math.pow(eleRect.width, 2) + Math.pow(eleRect.height, 2), 0.5));
                        if (eleRect.left - (d - eleRect.width) / 2 < this.width 
                                && (eleRect.top) - (d - eleRect.height) / 2 < this.height) {
                            Graphics2D cg = (Graphics2D) g.create(eleRect.left - (d - eleRect.width) / 2,
                                    (eleRect.top) - (d - eleRect.height) / 2, 
                                    d,
                                    d);
                            AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(eleRect.rotate), d/2, d/2);
                            cg.transform(at);
                            cg.translate((d - eleRect.width) / 2, (d - eleRect.height) / 2);
                            eleRect.print(cg);
                            cg.dispose();
                        }
                    }
                }
                if (this.eleList.get(i) instanceof EleLine) {
                    eleLine = (EleLine) this.eleList.get(i);
                    if (bottom && eleLine.zPosition.equals(DocConst.ALIGN_BOTTOM) || !bottom && !eleLine.zPosition.equals(DocConst.ALIGN_BOTTOM)) {
                        Rectangle rectangle =  EleLine.getViewBounds(eleLine);
                        eleLine.print((Graphics2D) g, rectangle.x, rectangle.y);
                    }
                }
            }
        }
    }
    public void dock(EleRect rect) {
        if (rect.dock.length() > 0) {
        	String[] strs = rect.dock.split(",");
        	int padding = 0;
        	String rectDock = strs[0];
        	if (strs.length > 1) {
        		padding = To.toInt(strs[1]);
        	}
        	
            if (rectDock.indexOf("center") >= 0) {
                rect.left = (this.width - rect.width) / 2;
                rect.top = (this.height - rect.height) / 2;
            }
            if (rectDock.indexOf("top") >= 0) {
                rect.top = this.margin + padding;
            }
            if (rectDock.indexOf("bottom") >= 0) {
                rect.top = this.height - rect.height - this.margin - padding;
            }
            if (rectDock.indexOf("left") >= 0) {
                rect.left = this.margin + padding;
            }
            if (rectDock.indexOf("right") >= 0) {
                rect.left = this.width - rect.width - this.margin - padding;
            }
        }
    }
    protected BufferedImage imgFill;
    public static final int DEF_HEIGHT = 24;
    public static final int DEF_WIDTH = 96;
    public Shape getOutline() {
        Shape shape = getFillShape();
        if (isLineShape()) {
        	shape = getLineShape(this.strokeWidth);
        }
        return shape;
    }
    public Shape fillShape(Graphics2D g) {
        Shape shape = getFillShape();
        if (this.canFill()) {
        	Shape fillShape = shape;
        	if (this.fillRatio.length() > 0) {
        		Area area = new Area(fillShape);
        		String strfr = this.fillRatio;
        		if (strfr.indexOf(DocConst.PRINTMARK_PRE) >= 0) {
        			strfr = DocUtil.printEval(strfr, this.xdoc);
        		}
        		double actFillRatio = To.toDouble(strfr, 1);
        		if (actFillRatio > 1) {
        			actFillRatio -= (int) actFillRatio;
        		}
        		if (actFillRatio != 1) {
        			Rectangle bounds = fillShape.getBounds();
        			Area interArea;
        			if (actFillRatio > 0) {
        				if (this.width > this.height) {
        					interArea = new Area(new Rectangle(bounds.x, bounds.y, (int) (bounds.width * actFillRatio), bounds.height));
        				} else if (this.width == this.height) {
        					double d = Math.pow(Math.pow(bounds.width, 2) * 2, 0.5);
        					interArea = new Area(new Arc2D.Double(bounds.x + (bounds.width - d) / 2, bounds.y + (bounds.width - d) / 2, d, d, 90, -actFillRatio * 360, Arc2D.PIE));
        				} else {
        					int h = (int) (bounds.height * actFillRatio);
        					interArea = new Area(new Rectangle(bounds.x, bounds.y + bounds.height - h, bounds.width, h));
        				}
        			} else {
        				actFillRatio = -actFillRatio;
        				if (this.width > this.height) {
        					int w = (int) (bounds.width * actFillRatio);
        					interArea = new Area(new Rectangle(bounds.x + bounds.width - w, bounds.y, w, bounds.height));
        				} else if (this.width == this.height) {
        					interArea = new Area(new Arc2D.Double(bounds.x, bounds.y, bounds.width, bounds.height, 90, actFillRatio * 360, Arc2D.PIE));
        				} else {
        					interArea = new Area(new Rectangle(bounds.x, bounds.y, bounds.width, (int) (bounds.height * actFillRatio)));
        				}
        			}
        			area.intersect(interArea);
        			fillShape = area;
        		}
        	}
        	PaintUtil.fill(g, this.xdoc, fillShape, this.fillColor, this.fillImg, this.gradual, this.fillColor2);
        }
        if (this.margin != 0) {
        	g = (Graphics2D) g.create(this.margin, this.margin, this.width - this.margin * 2, this.height - this.margin * 2);
        }
        try {
            drawOther(g, shape);
        } finally {
            if (this.margin != 0) {
                g.dispose();
            }
        }
        if (isLineShape()) {
        	shape = getLineShape(this.strokeWidth);
        	AffineTransform af = new AffineTransform();
        	af.translate(0.5, 0.5);
        	shape = af.createTransformedShape(shape);
        }
        return shape;
    }
	protected boolean canFill() {
		return true;
	}
	protected boolean isLineShape() {
        return !this.line.equals(EleRect.LINE_RECT);
	}
	private Shape getLineShape(double sw) {
        String tline = StrUtil.rpad(this.line, 8, "0");
        if (tline.equals(EleRect.LINE_RECT)) {
        	return this.getRectShape();
        }
        Rectangle rect = new Rectangle(0, 0, this.width, this.height);
        GeneralPath path = new GeneralPath();
        int n = 0;
        if (tline.charAt(n++) == '1') {
            path.moveTo(0, (float) rect.getHeight());
            path.lineTo(0, 0);
        } 
        if (tline.charAt(n++) == '1') {
            path.moveTo(0, 0);
            path.lineTo((float) rect.getWidth(), 0);
        } 
        if (tline.charAt(n++) == '1') {
            path.moveTo((float) rect.getWidth(), 0);
            path.lineTo((float) rect.getWidth(), (float) rect.getHeight());
        } 
        if (tline.charAt(n++) == '1') {
            path.moveTo((float) rect.getWidth(), (float) rect.getHeight());
            path.lineTo(0, (float) rect.getHeight());
        }
        if (tline.charAt(n++) == '1') {
            path.moveTo(0, 0);
            path.lineTo((float) rect.getWidth(), (float) rect.getHeight());
        }
        if (tline.charAt(n++) == '1') {
            path.moveTo((float) rect.getWidth(), 0);
            path.lineTo(0, (float) rect.getHeight());
        }
        if (tline.charAt(n++) == '1') {
            path.moveTo(0, (float) rect.getHeight() / 2);
            path.lineTo((float) rect.getWidth(), (float) rect.getHeight() / 2);
        }
        if (tline.charAt(n++) == '1') {
            path.moveTo((float) rect.getWidth() / 2, 0);
            path.lineTo((float) rect.getWidth() / 2, (float) rect.getHeight());
        }
        Shape shape = path;
        if (this.margin != 0 || sw > 1) {
            AffineTransform af;
            //缩放
            af = new AffineTransform();
            af.scale((this.width - sw - this.margin * 2) / this.width
                    , (this.height - sw - this.margin * 2) / this.height);
            shape = af.createTransformedShape(shape);
            //移动
            af = new AffineTransform();
            af.translate(sw / 2 + this.margin, sw / 2 + this.margin);
            shape = af.createTransformedShape(shape);
        }
        return shape;
    }
    public void drawShape(Graphics2D g, Shape shape) {
    	if (this.strokeWidth > 0) {
    		if (this.strokeWidth >= 1 && this.strokeImg != null && this.strokeImg.length() > 0) {
    			BufferedImage imgStroke = ImgUtil.loadImg(this.xdoc, this.strokeImg, this.color, null, false);
    			if (imgStroke != null) {
    				imgStroke = ImgUtil.alpha(imgStroke, Color.WHITE);
    				Shape fshape = this.getFillShape(1);
			        if (isLineShape()) {
			        	fshape = getLineShape(1);
			        }
    				drawImageShape(g, imgStroke, fshape, (int) this.strokeWidth);
    			}
    		} else if (this.color != null) {
    			if (this.strokeWidth > 1 && (this.strokeStyle.startsWith("g") || this.strokeStyle.startsWith("s") || this.strokeStyle.startsWith("d"))) {
    				String style = this.strokeStyle;
    				if (style.equals("g1") || style.equals("g2")) {
    					g.setStroke(new BasicStroke(2));
    					Color toColor = new Color(255, 255, 255, 0);
    					int sw = (int) (this.strokeWidth * 2);
    					for (int i = 1; i <= sw; i++) {
    						shape = this.getFillShape(i + 1);
    				        if (isLineShape()) {
    				        	shape = getLineShape(i + 1);
    				        }
    						if (style.equals("g1")) {
    							g.setColor(ColorUtil.mix(this.color, toColor, i / (float) sw));
    						} else if (style.equals("g2")) {
    							g.setColor(ColorUtil.mix(this.color, toColor, (sw - i) / (float) sw));
    						}
    						g.draw(shape);
    					}
    				} else if (style.equals("g3") || style.equals("g4")) {
    					g.setStroke(new BasicStroke(2));
    					Color toColor = new Color(255, 255, 255, 0);
    					int sw = (int) (this.strokeWidth * 2);
    					for (int i = 1; i < sw; i++) {
    						shape = this.getFillShape(i + 1);
    				        if (isLineShape()) {
    				        	shape = getLineShape(i + 1);
    				        }
    						if (style.equals("g3")) {
    							if (i <= sw / 2) {
    								g.setColor(ColorUtil.mix(this.color, toColor, i / ((float) sw / 2)));
    							} else {
    								g.setColor(ColorUtil.mix(this.color, toColor, (sw - i) / ((float) sw / 2)));
    							}
    						} else if (style.equals("g4")) {
    							if (i <= sw / 2) {
    								g.setColor(ColorUtil.mix(this.color, toColor, (sw / 2 - i) / ((float) sw / 2)));
    							} else {
    								g.setColor(ColorUtil.mix(this.color, toColor, (i - sw / 2) / ((float) sw / 2)));
    							}
    						}
    						g.draw(shape);
    					}
    				} else if (style.equals("s1") || style.equals("s2") || style.equals("s3") || style.equals("s4")) {
    					AffineTransform af = new AffineTransform();
    					int n = Integer.parseInt(style.substring(1));
    					if (n == 1) {
    						af.translate(-this.strokeWidth / 3, -this.strokeWidth / 3);
    					} else if (n == 2) {
    						af.translate(this.strokeWidth / 3, -this.strokeWidth / 3);
    					} else if (n == 3) {
    						af.translate(-this.strokeWidth / 3, this.strokeWidth / 3);
    					} else if (n == 4) {
    						af.translate(this.strokeWidth / 3, this.strokeWidth / 3);
    					}
        				g.setStroke(new BasicStroke((float) this.strokeWidth / 2));
        				g.setColor(ColorUtil.mix(this.color, new Color(255, 255, 255, 0), 0.5f));
        				g.draw(af.createTransformedShape(shape));
        				g.setColor(this.color);
        				g.draw(shape);
    				} else if (style.equals("d1") || style.equals("d2") || style.equals("d3") || style.equals("d4")) {
    					g.setColor(this.color);
    					float sw = (float) (this.strokeWidth * 2);
    					float ow, iw;
    					if (style.equals("d1")) {
    						ow = sw / 16;
    						iw = sw / 16;    						
    					} else if (style.equals("d2")) {
    						ow = sw / 8;
    						iw = sw / 8;    						
    					} else if (style.equals("d3")) {
    						ow = sw / 8;
    						iw = sw / 16;    						
    					} else {
    						ow = sw / 16;
    						iw = sw / 8;    						
    					}
    					g.setStroke(new BasicStroke(ow));
    					shape = this.getFillShape(sw / 4);
    					if (isLineShape()) {
    						shape = getLineShape(sw / 4);
    					}
    					g.draw(shape);
    					g.setStroke(new BasicStroke(iw));
    					shape = this.getFillShape(sw * 3 / 4);
    					if (isLineShape()) {
    						shape = getLineShape(sw * 3 / 4);
    					}
    					g.draw(shape);
    				} else {
    					g.setColor(this.color);
        				g.setStroke(new BasicStroke((float) this.strokeWidth));
        				g.draw(shape);
    				}
    			} else {
    				setStroke(g);
    				g.draw(shape);
    				PaintUtil.resetStroke(g);
    			}
    		}
    	}
    }
	protected void drawImageShape(Graphics2D g, BufferedImage img, Shape shape, int strokeWidth) {
		if (strokeWidth >= 1) {
			int width = (int) Math.round(img.getWidth() / (double) img.getHeight() * strokeWidth);
			if (width < 1) {
				width = 1;
			}
			BufferedImage timg = new BufferedImage(width, strokeWidth, BufferedImage.TYPE_INT_ARGB);
			Graphics2D tg = (Graphics2D) timg.getGraphics();
	        tg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			tg.drawImage(img, 0, 0, width, strokeWidth, null);
			tg.dispose();
			BufferedImage[] imgs = new BufferedImage[width];
			for (int i = 0; i < imgs.length; i++) {
				imgs[i] = timg.getSubimage(i, 0, 1, strokeWidth);
			}
			Rectangle bounds = shape.getBounds();
			img = new BufferedImage(bounds.x + bounds.width + strokeWidth, bounds.y + bounds.height + strokeWidth, BufferedImage.TYPE_INT_ARGB);
			tg = (Graphics2D) img.getGraphics();

			PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), 1);
	    	float moveX = 0, moveY = 0;
	    	float lastX = 0, lastY = 0;
	    	float thisX = 0, thisY = 0;
	    	float points[] = new float[6];
	    	int type = 0;
    		int imgInx = 0;
    	    AffineTransform af = new AffineTransform();
     		while (!it.isDone() ) {
    			type = it.currentSegment( points );
    			switch( type ){
    			case PathIterator.SEG_MOVETO:
    				moveX = lastX = points[0];
    				moveY = lastY = points[1];
    				break;
    			case PathIterator.SEG_CLOSE:
    				points[0] = moveX;
    				points[1] = moveY;
    			case PathIterator.SEG_LINETO:
	    			thisX = points[0];
	    			thisY = points[1];
	    			float dx = thisX-lastX;
	    			float dy = thisY-lastY;
	    			float distance = (float)Math.sqrt( dx*dx + dy*dy );
	    			float r = 1/distance;
	    			float angle = (float)Math.atan2( dy, dx );
	    			for (int i = 0; i < distance; i++) {
	    				float x = lastX + i*dx*r;
	    				float y = lastY + i*dy*r;
	    				af.setToTranslation(x, y);
	    				af.rotate(angle);
	    				tg.drawImage(imgs[imgInx], af, null);
	    				imgInx++;
	    				imgInx %= imgs.length;
	    			}
	    			lastX = thisX;
	    			lastY = thisY;
    				break;
    			}
    			it.next();
    		}
     		tg.dispose();
     		g.drawImage(img, null, 0, 0);
		}
	}
    public void drawShape(Graphics2D g) {
    	this.drawShape(g, this.fillShape(g));
    }
    protected void drawOther(Graphics2D g, Shape shape) {
    }
    public void printRect(Graphics2D g) {
        drawShape(g);
        printRect(g, true);
        printRect(g, false);
    }
    /**
     * 分页
     * @param g
     * @param rect
     * @param fh
     * @param h
     * @return
     */
    public static ArrayList split(EleRect rect, int fh, int h, boolean firstOnly) {
        if (rect instanceof EleTable) {
            return splitTable((EleTable) rect, fh, h, firstOnly);
        } else {
            return splitRect(rect, fh, h, firstOnly);
        }
    }
    private static ArrayList splitRect(EleRect rect, int fh, int h, boolean firstOnly) {
        ArrayList pages = new ArrayList();
        int pageSize = Integer.MAX_VALUE;
        if (rect.sizeType.equals(EleRect.SIZE_NORMAL)) {
            int hcount = rect.height;
            EleRect trect = (EleRect) rect.clone();
            trect.height = fh;
            pages.add(trect);
            hcount -= fh;
            pageSize = 1;
            while (hcount > 0) {
                hcount -= h - trect.txtPadding * 2;
                trect = (EleRect) rect.clone();
                if (hcount > 0) {
                    trect.height = h;
                } else {
                    trect.height = hcount + h + trect.txtPadding;
                }
                pages.add(trect);
                if (firstOnly && pages.size() > 0) {
                    break;
                }
                pageSize++;
            }
        }
        //分割图形
        Rectangle sbound;
        int pageno = 0, pageno2 = 0;
        EleRect shape;
        for (int i = 0; i < rect.eleList.size(); i++) {
            if (rect.eleList.get(i) instanceof EleRect) {
                shape = (EleRect) ((EleRect) rect.eleList.get(i)).clone();
                shape.eleList.addAll(((EleRect) rect.eleList.get(i)).eleList);
                sbound = shape.getBound();
                if (sbound.x + sbound.width < 0 || sbound.x > rect.width) { //忽略横向超长可视范围
                    continue;
                }
                if (sbound.y < fh) { //第一页
                    pageno = 0;
                } else {
                    pageno = (int) Math.ceil((sbound.y - fh) / h) + 1;
                }
                if (sbound.y + sbound.height <= fh) { //第一页
                    pageno2 = 0;
                } else {
                    pageno2 = (int) Math.ceil((sbound.y + sbound.height - fh) / h) + 1;
                }
                if (firstOnly && pageno > 0) {
                    continue;
                }
                if (pageno != pageno2 
                        && (shape instanceof EleRect 
                                && ((EleRect) shape).getLineList().size() > 1
                                || shape instanceof EleTable 
                                && ((EleTable) shape).rows.length() > 1)) { //分割包含文本跨页的图形
                    ArrayList cpages = split((EleRect) shape, pageno == 0 ? fh - sbound.y : h - (sbound.y - fh) % h, h, firstOnly);
                    for (int j = 0; j < cpages.size(); j++) {
                        if (pageno + j > pageSize - 1) {
                            break;
                        }
                        if (j > 0) {
                            ((EleRect) cpages.get(j)).top = 0;
                        } else if (pageno > 0) {
                            ((EleRect) cpages.get(j)).top = (sbound.y - fh) % h;
                        }
                        if (j == 0) {
                            ((EleRect) cpages.get(j)).height = pageno == 0 ? fh - sbound.y : h - (sbound.y - fh) % h;
                        } else if (j == cpages.size() - 1) {
                            ((EleRect) cpages.get(j)).height = 
                            	(sbound.y + sbound.height - fh + ((EleRect) cpages.get(j)).getTextTop() * cpages.size() * 2) % h;
                        } else {
                            ((EleRect) cpages.get(j)).height = h;
                        }
                        addToPage(pages, rect, cpages.get(j), pageno + j);
                    }
                } else {
                    if (pageno > 0) {
                        if (shape instanceof EleLine) {
                            EleLine line = (EleLine) shape;
                            line.startY = (line.startY - fh) % h;
                            line.endY = (line.endY - fh) % h;
                        } else {
                            ((EleRect) shape).top = (sbound.y - fh) % h;
                        }
                    }
                    if (pageno < pageSize) {
                        addToPage(pages, rect, shape, pageno);
                    }
                }
            }
        }
        //分割段落
        ArrayList paras = rect.getParaList();
		int n = 0;
		EleRect trect;
		while (paras != null && paras.size() > 0) {
			if (n >= pageSize) {
				break;
			}
			if (n > pages.size() - 1) {
				trect = (EleRect) rect.clone();
				pages.add(trect);
			} else {
				trect = (EleRect) pages.get(n);
			}
            if (firstOnly && pages.size() > 1) {
                break;
            }
            if (!rect.sizeType.equals(EleRect.SIZE_NORMAL)) {
                if (n == 0) {
                    trect.height = fh;
                } else {
                    trect.top = 0;
                    trect.height = h;
                }
            }
			paras = trect.genLineList(paras, h);
            if (paras == null && (rect.sizeType.equals(EleRect.SIZE_AUTOHEIGHT) || rect.sizeType.equals(EleRect.SIZE_AUTOSIZE))) {
                trect = (EleRect) pages.get(pages.size() - 1);
                if (trect.lineList != null && trect.lineList.size() > 0) {
                    DocPageLine line = (DocPageLine) trect.lineList.get(trect.lineList.size() - 1);
                    trect.height = line.top + line.height + trect.txtPadding;
                    for (int i = 0; i < trect.eleList.size(); i++) {
                        if (trect.eleList.get(i) instanceof EleRect) {
                            EleRect crect = (EleRect) trect.eleList.get(i);
                            if (trect.height < crect.top + crect.height) {
                                trect.height = crect.top + crect.height;
                            }
                        }
                    }
                }
            }
			n++;
		}
	    if (pages.size() == 0) {
    		pages.add(rect);
    	}
        return pages;
    }
    private static EleTable pageTable(EleTable tab, int[] rs, int fromRow, int toRow) {
        if (fromRow > toRow) {
            //只有头尾，忽略
            return new EleTable(tab.xdoc);
        }
        EleTable ttab = new EleTable(tab.xdoc, tab.getAttMap());
        ttab.header = 0;
        ttab.footer = 0;
        ttab.height = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tab.header; i++) {
            if (sb.length() > 0) sb.append(",");
            sb.append(rs[i]);
            ttab.height += rs[i];
        }
        for (int i = fromRow; i <= toRow; i++) {
            if (sb.length() > 0) sb.append(",");
            sb.append(rs[i]);
            ttab.height += rs[i];
        }
        for (int i = rs.length - tab.footer; i < rs.length; i++) {
            if (sb.length() > 0) sb.append(",");
            sb.append(rs[i]);
            ttab.height += rs[i];
        }
        ttab.height += (int) ttab.strokeWidth * 2 + ttab.margin * 2;
        ttab.rows = sb.toString();
        EleCell cell, cell2;
        for (int i = 0; i < tab.eleList.size(); i++) {
            cell = (EleCell) tab.eleList.get(i);
            if (cell.row >= 0 && cell.row < tab.header) {
                cell2 = (EleCell) cell.clone();
                cell2.eleList.addAll(cell.eleList);
                ttab.eleList.add(cell2);
            }
            if (cell.row >= fromRow && cell.row <= toRow) {
                cell2 = (EleCell) cell.clone();
                cell2.row = tab.header + cell.row - fromRow;
                cell2.eleList.addAll(cell.eleList);
                ttab.eleList.add(cell2);
            }
            if (cell.row >= rs.length - tab.footer && cell.row < rs.length) {
                cell2 = (EleCell) cell.clone();
                cell2.row = (tab.header + toRow - fromRow + 1) + (cell.row - (rs.length - tab.footer));
                cell2.eleList.addAll(cell.eleList);
                ttab.eleList.add(cell2);
            }
        }
        return ttab;
    }
    private static ArrayList splitTable(EleTable table, int fh, int h, boolean firstOnly) {
        ArrayList pages = new ArrayList();
        String[] rowStrs = table.rows.split(",");
        int[] rs = new int[rowStrs.length];
        boolean firstPage = true;
        for (int i = 0; i < rs.length; i++) {
            rs[i] = To.toInt(rowStrs[i], DEF_HEIGHT);
        }
        //修正header、footer
        if (table.header < 0) {
            table.header = 0;
        }
        if (table.header > rs.length) {
            table.header = rs.length;
        }
        if (table.footer < 0) {
            table.footer = 0;
        }
        if (table.footer > (rs.length - table.header)) {
            table.footer = rs.length - table.header;
        }
        int startRow = table.header;
        //表头、表尾高度
        int hfcount = 0;
        for (int i = 0; i < table.header; i++) {
            hfcount += rs[i];
        }
        for (int i = rs.length - table.footer; i < rs.length; i++) {
            hfcount += rs[i];
        }
        int hcount = hfcount;
        for (int i = table.header; i < rs.length - table.footer; i++) {
            if (firstPage) {
                if (hcount + rs[i] > fh) { //换页
                    firstPage = false;
                    if (!(hcount == hfcount && fh == h)) {//空页放入超高行
                        i--;
                    }
                    //生成page
                    pages.add(pageTable(table, rs, startRow, i));
                    startRow = i + 1;
                    hcount = hfcount;
                    if (firstOnly) {
                        return pages;
                    }
                } else {
                    hcount += rs[i];
                }
            } else {
                if (hcount + rs[i] > h) { //换页
                    firstPage = false;
                    if (!(hcount == hfcount)) {//空页放入超高行
                        i--;
                    }
                    //生成page
                    pages.add(pageTable(table, rs, startRow, i));
                    startRow = i + 1;
                    hcount = hfcount;
                } else {
                    hcount += rs[i];
                }
            }
        }
        if (startRow <= rs.length - table.footer - 1) {
            //未满页行
            pages.add(pageTable(table, rs, startRow, rs.length - table.footer - 1));
        }
        if (pages.size() == 0) {
            pages.add(table);
        }
        //水平分页
        if (table.xdoc.getMeta().getView().equals(XDoc.VIEW_TABLE)) {
            int cw = table.xdoc.getPaper().getContentWidth();
            for (int i = 0; i < pages.size(); i++) {
                if (((EleTable) pages.get(i)).width > cw) {
                    table = (EleTable) pages.remove(i);
                    pages.addAll(i, splitTableW(table, cw));
                }
            }
        }
        return pages;
    }
    /**
     * 表格横向分页
     * @param table
     * @param cw
     * @return
     */
    private static ArrayList splitTableW(EleTable table, int cw) {
        ArrayList pages = new ArrayList();
        String[] colStrs = table.cols.split(",");
        int[] cols = new int[colStrs.length];
        for (int i = 0; i < colStrs.length; i++) {
            cols[i] = Integer.parseInt(colStrs[i]);
        }
        int start = 0;
        int widthCount = 0;
        EleCell cell, cell2;
        EleTable ttab;
        StringBuffer sbCol = new StringBuffer();
        for (int i = 0; i < cols.length; i++) {
            widthCount += cols[i];
            if (start < i && widthCount > cw) {
                //分页
                widthCount -= cols[i];
                ttab = (EleTable) table.clone();
                pages.add(ttab);
                ttab.rows = table.rows;
                for (int j = 0; j < table.eleList.size(); j++) {
                    cell = (EleCell) table.eleList.get(j);
                    if (cell.col >= start && cell.col < i) {
                        cell2 = (EleCell) cell.clone();
                        cell2.col -= start;
                        cell2.eleList.addAll(cell.eleList);
                        ttab.eleList.add(cell2);
                    }
                }
                ttab.width = widthCount;
                ttab.cols = sbCol.toString();
                start = i;
                widthCount = cols[i];
                sbCol.setLength(0);
            }
            if (sbCol.length() > 0) {
                sbCol.append(",");
            }
            sbCol.append(cols[i]);
        }
        if (widthCount > 0) {
            ttab = (EleTable) table.clone();
            pages.add(ttab);
            ttab.rows = table.rows;
            for (int j = 0; j < table.eleList.size(); j++) {
                cell = (EleCell) table.eleList.get(j);
                if (cell.col >= start) {
                    cell2 = (EleCell) cell.clone();
                    cell2.col -= start;
                    cell2.eleList.addAll(cell.eleList);
                    ttab.eleList.add(cell2);
                }
            }
            ttab.width = widthCount;
            ttab.cols = sbCol.toString();
        }
        return pages;
    }
    private static void addToPage(ArrayList pages, EleRect rect, Object shape, int pageno) {
        if (pages.size() <= pageno) {
            for (int i = pages.size(); i <= pageno; i++) {
                pages.add(rect.clone());
            }
        }
        ((EleRect) pages.get(pageno)).eleList.add(shape);
    }
    private ArrayList hrList() {
        ArrayList hrList = new ArrayList();
        for (int i = 0; i < this.eleList.size(); i++) {
            if (this.eleList.get(i) instanceof EleRect) {
                EleRect rect = (EleRect) this.eleList.get(i);
                if (rect.zPosition.equals(DocConst.ALIGN_AROUND)) {
                    HindRance hr = new HindRance(new Rectangle(rect.left, rect.top, 
                            rect.width, rect.height));
                    hrList.add(hr);
                }
            }
        }
        return hrList;
    }
    private static final int minFontSize = 4;
    public void autoSize() {
        if (!this.sizeType.equals(SIZE_NORMAL) && !this.sizeType.equals(SIZE_AUTOTIP)) {
            if (this.sizeType.equals(SIZE_AUTOFONT)) {
                Point size = textSize();
                //是否最小字体
                boolean bmin = false;
                ElePara para;
                EleText txt;
                Point tsize = new Point(this.width, this.height);
                while (!bmin && (size.x > tsize.x || size.y > tsize.y)) {
                    //缩小字体
                    bmin = true;
                    for (int i = 0; i < this.eleList.size(); i++) {
                        if (eleList.get(i) instanceof ElePara) {
                            para = (ElePara) eleList.get(i);
                            for (int j = 0; j < para.eleList.size(); j++) {
                                if (para.eleList.get(j) instanceof EleText) {
                                    txt = (EleText) para.eleList.get(j);
                                    if (txt.fontSize > minFontSize) {
                                        txt.fontSize--;
                                        if (txt.fontSize > minFontSize) {
                                            bmin = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    size = textSize();
                }
            } else {
                Point rsize = rectSize();
                Point tsize = textSize();
                this.width = Math.max(rsize.x, tsize.x);
                this.height = Math.max(rsize.y, tsize.y);
            }
        }
    }
    public Point rectSize() {
        if (this.lineList != null) {
            return new Point(this.getTextTop() * 2, this.getTextLeft() * 2);
        }
        Point p = new Point();
        if (this.sizeType.equals(SIZE_AUTOWIDTH)) {
            p.y = this.height;
        }
        if (this.sizeType.equals(SIZE_AUTOHEIGHT)) {
            p.x = this.width;
        }
        int nw = 0, nh = 0;
        int n = 0;
        EleBase ele;
        boolean b = true;
        EleRect rect;
        for (int i = 0; i < this.eleList.size(); i++) {
            ele = (EleBase) this.eleList.get(i);
            if (ele instanceof EleRect) {
            	rect = (EleRect) ele;
                rect.autoSize();
                if (rect.dock.length() > 0) {
                	rect.top = 0;
                	rect.left = 0;
                }
                n = rect.top + rect.height;
                if (n > nh) {
                    nh = n;
                }
                n = rect.left + rect.width;
                if (n > nw) {
                    nw = n;
                }
                b = false;
            }
        }
        if (!b) {
            if (this.sizeType.equals(SIZE_AUTOWIDTH) || this.sizeType.equals(SIZE_AUTOSIZE)) {
                p.x = nw + this.txtPadding * 2;
            }
            if (this.sizeType.equals(SIZE_AUTOHEIGHT) || this.sizeType.equals(SIZE_AUTOSIZE)) {
                p.y = nh + this.txtPadding * 2;
            }
        }
        return p;
    }    
    public Point textSize() {
        if (isBlank(this)) {
            return new Point(this.txtPadding * 2, this.txtPadding * 2);
        }
        Point p = new Point();
        ElePara para;
        ArrayList paras = this.getParaList();
//        for (int i = 0; i < paras.size(); i++) {
//            para = (ElePara) paras.get(i);
//            for (int j = 0; j < para.eleList.size(); j++) {
//                if (para.eleList.get(j) instanceof EleRect) {
//                    ((EleRect) para.eleList.get(j)).rectSize();
//                }
//            }
//        }
        boolean vertical = this.layoutFlow.equals("v");
        if (sizeType.equals(SIZE_AUTOWIDTH)) {
            p.y = this.height;
        }
        if (sizeType.equals(SIZE_AUTOHEIGHT)) {
            p.x = this.width;
        }
        int top;
        if (vertical) {
            top = this.width - this.getTextTop();
        } else {
            top = this.getTextTop();
        }
        ArrayList hrList = hrList();
        int textWidth = 0;
        if (vertical && (sizeType.equals(SIZE_AUTOWIDTH) || sizeType.equals(SIZE_AUTOFONT))) {
            textWidth = this.getTextHeight();
        } else if (!vertical && (sizeType.equals(SIZE_AUTOHEIGHT) || sizeType.equals(SIZE_AUTOFONT))) {
            textWidth = this.getTextWidth();
        } else {
            textWidth = Integer.MAX_VALUE;
        }
        ArrayList list;
        DocPageLine line = null;
        for (int i = 0; i < paras.size(); i++) {
            para = (ElePara) paras.get(i);
            if (vertical) {
                list = para.toLineList(top, textWidth, hrList, false);
                for (int j = 0; j < list.size(); j++) {
                    line = (DocPageLine) list.get(j);
                    if (!sizeType.equals(SIZE_AUTOWIDTH) && p.y < line.width) {
                        p.y = line.width;
                    }
                    if (!sizeType.equals(SIZE_AUTOHEIGHT)) {
                        p.x += line.height;
                    }
                }
            } else {
                list = para.toLineList(top, textWidth, hrList, true);
                for (int j = 0; j < list.size(); j++) {
                    line = (DocPageLine) list.get(j);
                    if (!sizeType.equals(SIZE_AUTOHEIGHT) && p.x < line.width) {
                        p.x = line.width;
                    }
                    if (!sizeType.equals(SIZE_AUTOWIDTH)) {
                        p.y += line.height;
                    }
                }
            }
        }
        if (!sizeType.equals(SIZE_AUTOHEIGHT)) {
            p.x += this.getTextTop() * 2 + 1;
        }
        if (!sizeType.equals(SIZE_AUTOWIDTH)) {
            p.y += this.getTextTop() * 2 + 1;
        }
        return p;
    }
    /**
     * 分页断行
     * @param g
     * @param paras
     * @param h
     * @return
     */
    protected ArrayList genLineList(ArrayList paras, int h) {
        int wColSplit = txtPadding() * 2;
        if (wColSplit <= 0) {
            wColSplit = 8;
        }
        ElePara para;
        ArrayList overParas = null;
        boolean ownPara = false;
        if (paras == null) {
            ownPara = true;
            paras = getParaList();
            ElePara.genHeadInx(paras);
            if (!hasPrintExp) {
                for (int i = 0; i < paras.size(); i++) {
                    para = (ElePara) paras.get(i);
                    for (int j = 0; j < para.eleList.size(); j++) {
                        if (para.eleList.get(j) instanceof EleText && ((EleText) para.eleList.get(j)).text.indexOf("#") >= 0) {
                            hasPrintExp = true;
                            break;
                        }
                    }
                }
            }
        }
        lineList = new ArrayList();
        ArrayList list;
        DocPageLine line = null;
        boolean vertical = this.layoutFlow.equals("v");
        int top;
        if (vertical) {
            top = this.width - this.getTextTop();
        } else {
            top = this.getTextTop();
        }
        ArrayList hrList = hrList();
        if (column < 1) column = 1;
        int textWidth = 0;
        if (vertical) {
            textWidth = this.getTextHeight() / column - wColSplit * (column - 1);
        } else {
            textWidth = this.getTextWidth() / column - wColSplit * (column - 1);
        }
        
        boolean stop = false;
        for (int i = 0; i < paras.size(); i++) {
            para = (ElePara) paras.get(i);
            list = para.toLineList(top, textWidth, hrList, !vertical);
            for (int j = 0; j < list.size(); j++) {
                line = (DocPageLine) list.get(j);
                line.index = lineList.size();
                line.top = top;
                if (vertical) {
                    top -= line.height;
                } else {
                    top += line.height;
                }
                if (para instanceof EleParaLine) {
                	this.lineList.add(line);
                	continue;
                }
                if ((i > 0 || j > 0) /*保证第一行输出*/ && vertical && top <  this.getTextLeft() - this.getTextWidth() * (column - 1) 
                        || (i > 0 || j > 0) && !vertical && (top > this.height - this.getTextLeft() + this.getTextHeight() * (column - 1)
                        		|| isTableWidthOver(list, line))
                        ||  para.breakPage && j == list.size() - 1 && !this.name.equals(DocConst.NO_PARA_BREAK)) { //溢出
                    if (!ownPara) {
                        overParas = new ArrayList();
                        if (list.size() == 1 
                                && line.eleList.size() == 1 
                                && (line.eleList.get(0) instanceof EleRect 
                                && ((EleRect) line.eleList.get(0)).getLineList().size() > 1
                                || line.eleList.get(0) instanceof EleTable 
                                && ((EleTable) line.eleList.get(0)).rows.indexOf(",") > 0) && !para.breakPage) { //有文本的独立段落图形分割
                            int fh = 0;
                            if (vertical && top < -this.getTextWidth() * (column - 1)) {
                                fh = line.height + top - this.getTextWidth() * (column - 1);
                            } else if (!vertical && top > this.height + this.getTextHeight() * (column - 1)) {
                                fh = line.height + this.height + this.getTextHeight() * (column - 1) - top; 
                            }
                            if (isTableWidthOver(list, line)) {
                                h = ((EleTable) line.eleList.get(0)).height;
                                fh = h;
                            }
                            boolean firstToNextPage = false;
                            if (fh <= EleRect.DEF_HEIGHT && h > EleRect.DEF_HEIGHT) {
                                fh = h;
                                //剩下高度太小，跳到下一页
                                firstToNextPage = true;
                            }
                            ArrayList pages = split((EleRect) line.eleList.get(0), fh, h, false);
                            ElePara rpara;
                            //首个段落加入本页
                            HashMap atts = null;
                            if (pages.size() > 0) {
                            	atts = para.getAttMap();
                            }
                            if (pages.size() == 1 || pages.size() > 0 && firstToNextPage) {
                                rpara = new ElePara(this.xdoc, atts);
                                rpara.breakPage = true;
                                rpara.eleList.add(pages.get(0));
                                overParas.add(rpara);
                            } else if (pages.size() > 1) {
                            	if (vertical) {
                            		top += line.height;
                            	} else {
                            		top -= line.height;
                            	}
                            	rpara = new ElePara(this.xdoc, atts);
                                rpara.breakPage = true;
                            	rpara.eleList.add(pages.get(0));
                            	line = new DocPageLine(para, rpara.eleList, ((EleRect) pages.get(0)).height + rpara.lineSpacing, textWidth, vertical);
                            	line.top = top;
                            	line.align = para.align;
                            	line.offset = ((DocPageLine) rpara.toLineList(top, width, hrList, !vertical).get(0)).offset;
                            	lineList.add(line);
                            }
                            for (int k = 1; k < pages.size(); k++) {
                                rpara = new ElePara(this.xdoc, atts);
                                rpara.breakPage = true;
                                rpara.eleList.add(pages.get(k));
                                overParas.add(rpara);
                            }
                        } else if (j < list.size()) {
                            if (para.breakPage && j == list.size() - 1) {
                                lineList.add(line);
                            } else if (line.height > h) {
                                lineList.add(line);
                                ElePara overPara = (ElePara) para.clone();
                                for (int k = j + 1; k < list.size(); k++) {
                                    line = (DocPageLine) list.get(k);
                                    overPara.eleList.addAll(line.eleList);
                                }
                                overParas.add(overPara);
                            } else {
                                ElePara overPara = (ElePara) para.clone();
                                overPara.prefix = "";
                                overPara.headInx = para.headInx;
                                for (int k = j; k < list.size(); k++) {
                                    line = (DocPageLine) list.get(k);
                                    overPara.eleList.addAll(line.eleList);
                                }
                                overParas.add(overPara);
                            }
                        }
                        for (int k = i + 1; k < paras.size(); k++) {
                            overParas.add(paras.get(k));
                        }
                    } else {
                        if (this.sizeType.equals(SIZE_AUTOTIP)) {
                            if ((i < paras.size() - 1 || j < list.size() - 1 ) 
                                    && lineList.size() > 0) {
                                line = (DocPageLine) lineList.get(lineList.size() - 1);
                                if (line.eleList.size() > 0 && line.eleList.get(line.eleList.size() - 1) instanceof EleText) {
                                    EleText txt = (EleText) line.eleList.get(line.eleList.size() - 1);
                                    if (txt.text.length() > 2) {
                                        txt = new EleText(txt.xdoc, txt.getAttMap());
                                        txt.text = txt.text.substring(0, txt.text.length() - 2) + "...";
                                        line.eleList.set(line.eleList.size() - 1, txt);
                                    }
                                }
                                if (this.xdoc.print != null) {
                                    this.xdoc.print.toolTips.add(new NameShape(this.toString(), DocConst.g.getTransform().createTransformedShape(this.getFillShape())));
                                }
                            }
                        }
                    }
                    stop = true;
                    break;
                }
                lineList.add(line);
            }
            if (stop) break;
        }
        if (column > 1) {
            int yOff = 0;
            int xOff = 0;
            //行高计数器
            int hcount = 0;
            //列计数器
            int colCount = 0;
            for (int i = 0; i < lineList.size(); i++) {
                line = (DocPageLine) lineList.get(i);
                if (colCount > 0 && hcount == 0) {
                    if (!vertical) {
                        yOff = line.top - this.getTextTop();
                    } else {
                        yOff = line.top - this.width + this.getTextTop();
                    }
                }
                hcount += line.height;
                line.top -= yOff;
                line.left += xOff;
                if (!vertical && hcount > this.height - txtPadding() * 2 || vertical && hcount > this.width - txtPadding() * 2) { //折行
                    if (hcount != line.height) {
                        i--;
                        line.top += yOff;
                        line.left -= xOff;
                    }
                    hcount = 0;
                    colCount++;
                    xOff = textWidth * colCount + wColSplit * colCount;
                    if (colCount >= this.column) {
                        break;
                    }
                }
            }
        }
/*
        if (!ownPara && overParas == null && column == 1 && this.height != Integer.MAX_VALUE) {
            if (vertical && top > 0) {
                this.width = this.width - top + this.getTextTop();
            } else if (!vertical && top < this.height) {
                this.height = top + this.getTextTop();
            }
        }
*/
        //不分栏，对齐偏移
        if (column == 1 && !this.align.equals(DocConst.ALIGN_TOP)) {
            int topOffset = 0;
            if (vertical && top > 0) {
                if (this.align.equals(DocConst.ALIGN_CENTER)) {
                    topOffset = top / 2;
                } else if (this.align.equals(DocConst.ALIGN_BOTTOM)) {
                    topOffset = top;
                }
                for (int i = 0; i < lineList.size(); i++) {
                    line = (DocPageLine) lineList.get(i);
                    if (vertical) {
                        line.top -= topOffset;
                    } else {
                        line.top += topOffset;
                    }
                }
            } else if (!vertical && top < this.height) {
                if (this.align.equals(DocConst.ALIGN_CENTER)) {
                    topOffset = (this.height - top) / 2 - this.getTextTop() / 2;
                } else if (this.align.equals(DocConst.ALIGN_BOTTOM)) {
                    topOffset = this.height - top - (int) this.strokeWidth;
                }
                for (int i = 0; i < lineList.size(); i++) {
                    line = (DocPageLine) lineList.get(i);
                    line.top += topOffset;
                }
            }
        }
        return overParas;
    }
	private boolean isTableWidthOver(ArrayList list, DocPageLine line) {
        return this.xdoc.getMeta().getView().equals(XDoc.VIEW_TABLE)
                && list.size() == 1 
                && line.eleList.size() == 1 
                && line.eleList.get(0) instanceof EleTable 
                && ((EleTable) line.eleList.get(0)).cols.split(",").length > 1
                && ((EleTable) line.eleList.get(0)).rows.split(",").length > 1
                && ((EleTable) line.eleList.get(0)).width > this.getTextWidth()
                && ((EleTable) line.eleList.get(0)).height <= this.getTextHeight();
    }
    protected static void fixSize(EleRect rect, int w, int h) {
        if (rect.width > w || rect.height > h) {
            double scale;
            if (rect.width / (double) rect.height > w / (double) h) {
                scale = (double) w / rect.width;
            } else {
                scale = (double) h / rect.height;
            }
            rect.width = (int) (rect.width * scale);
            rect.height = (int) (rect.height * scale);
            rect.sizeType = EleRect.SIZE_NORMAL;
        }
    }
    /**
     * 重新排版
     */
    public void relayout() {
        this.lineList = null;
    }
    protected Shape getShape() {
    	return getRectShape();
    }
    private Shape getRectShape() {
    	return ShapeUtil.getRectShape(this.width, this.height, this.arc);
	}
	private Shape getFillShape() {
		return getFillShape(this.strokeWidth);
	}
	private Shape getFillShape(double sw) {
		Shape shape = this.getShape();
		if (this.srotate != 0) {
			//旋转
			AffineTransform af;
			af = new AffineTransform();
			Rectangle2D bound = shape.getBounds();
			af.rotate(this.srotate * Math.PI / 180, bound.getCenterX(), bound.getCenterY());
			shape = af.createTransformedShape(shape);
			//缩放
			bound = shape.getBounds2D();
			af = new AffineTransform();
			af.scale(this.width / bound.getWidth(), 
					this.height / bound.getHeight());
			shape = af.createTransformedShape(shape);
			//移动
			bound = shape.getBounds2D();
			af = new AffineTransform();
			af.translate(-bound.getX(), -bound.getY());
			shape = af.createTransformedShape(shape);
		}
		if ((this.color != null || this.fillImg != null && this.fillImg.length() > 0) || this.margin != 0) {
			AffineTransform af;
			if (sw > 1 || this.margin != 0) {
				//缩放
				af = new AffineTransform();
				af.scale((this.width - sw - this.margin * 2) / this.width
						, (this.height - sw - this.margin * 2) / this.height);
				shape = af.createTransformedShape(shape);
			}
			//移动
			af = new AffineTransform();
			af.translate(sw / 2 + this.margin, sw / 2 + this.margin);
			shape = af.createTransformedShape(shape);
		}
		return shape;
	}
	public void setBounds(Rectangle rect) {
        this.left = rect.x;
        this.top = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }
    /**
     * 相同格式文本进行合并
     * @param rect
     */
    public static void concatText(EleRect rect) {
        EleBase base;
        ElePara para;
        EleText txt1 = null, txt2;
        for (int i = 0; i < rect.eleList.size(); i++) {
            base = (EleBase) rect.eleList.get(i);
            if (base instanceof ElePara) {
                para = (ElePara) base;
                txt1 = null;
                for (int j = 0; j < para.eleList.size(); j++) {
                    base = (EleBase) para.eleList.get(j);
                    if (base instanceof EleRect) {
                        txt1 = null;
                        concatText((EleRect) base);
                    } else if (base instanceof EleText) {
                        if (txt1 == null) {
                            txt1 = (EleText) base;
                        } else {
                            txt2 = (EleText) base;
                            //尝试与txt1连接
                            if (txt1.attEquals(txt2)) {
                                txt1.text += txt2.text;
                                para.eleList.remove(j);
                                j--;
                            } else {
                                txt1 = txt2;
                            }
                        }
                    }
                }
            } else if (base instanceof EleRect) {
                concatText((EleRect) base);
            }
        }
    }
    public void clearContent() {
        this.eleList.clear();
        this.lineList = null;
    }
    public boolean isVisible() {
	    boolean visible = true;
	    if (this.visible.length() > 0) {
	        String vstr = this.visible;
	        if (vstr.equalsIgnoreCase("false")) {
	            visible = false;
	        } else if (!vstr.equalsIgnoreCase("true")) {
	            try {
	                BlkExpression blkExp = new BlkExpression(null);
	                blkExp.varMap.put("PAGENO", new Long(this.xdoc.getViewPage()));
	                blkExp.varMap.put("PAGECOUNT", new Long(this.xdoc.getViewPages()));
	                blkExp.varMap.put("PAGE", new Long(this.xdoc.getViewPage()));
	                blkExp.varMap.put("PAGES", new Long(this.xdoc.getViewPages()));
	                LogicExpression mathExp = new LogicExpression(blkExp, Parser.pretreat(vstr)[0]);
	                visible = mathExp.isTrue(null);
	            } catch (Exception e) {
	                visible = true;
	            }
	        }
	    }
	    return visible;
	}
	private static int maxImgHeight = 1200;
    private static int maxImgWidth = 1200;
    public static BufferedImage toImg(EleRect shape) {
        return toImg(shape, false);
    }
    public static BufferedImage toImg(EleRect rect, boolean alpha) {
        BufferedImage bufImg;
        Graphics2D g;
        if (rect instanceof EleLine) {
        	EleLine line = (EleLine) rect;
        	Rectangle bounds = line.getBound();
        	bounds.width += line.strokeWidth * 2;
        	bounds.height += line.strokeWidth * 2;
            if (bounds.width > maxImgWidth) {
                bounds.width = maxImgWidth;
            }
            if (bounds.height > maxImgHeight) {
                bounds.height = maxImgHeight;
            }
        	if (bounds.width == 0 || bounds.height == 0) {
        		bounds = new Rectangle(0, 0, 1, 1);
        	}
    		bufImg = new BufferedImage(line.xdoc.intScale(bounds.width), line.xdoc.intScale(bounds.height), alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    		g = (Graphics2D) bufImg.getGraphics();
            ImgUtil.setRenderHint(g);
            if (!alpha) {
                g.setColor(Color.white);
                g.fillRect(0, 0, bufImg.getWidth(), bufImg.getHeight());
            }
    		line.scalePrint(g, (int) line.strokeWidth, (int) line.strokeWidth);
    		g.dispose();
        } else {
            rect.autoSize();
            if (rect.width > maxImgWidth) {
                rect.width = maxImgWidth;
            }
            if (rect.height > maxImgHeight) {
                rect.height = maxImgHeight;
            }
            if (rect.width <= 0) {
                rect.width = 1;
            }
            if (rect.height <= 0) {
                rect.height = 1;
            }
        	if (rect.rotate == 0) {
        		bufImg = new BufferedImage(rect.xdoc.intScale(rect.width), rect.xdoc.intScale(rect.height), alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        		g = (Graphics2D) bufImg.getGraphics();
                if (!alpha) {
                    g.setColor(Color.white);
                    g.fillRect(0, 0, bufImg.getWidth(), bufImg.getHeight());
                }
        	} else {
        		int d = (int) Math.ceil(Math.pow(Math.pow(rect.xdoc.intScale(rect.width), 2) + Math.pow(rect.xdoc.intScale(rect.height), 2), 0.5));
        		bufImg = new BufferedImage(d, d, alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        		g = (Graphics2D) bufImg.getGraphics();
                if (!alpha) {
                    g.setColor(Color.white);
                    g.fillRect(0, 0, bufImg.getWidth(), bufImg.getHeight());
                }
        		AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(rect.rotate), d/2, d/2);
        		g.transform(at);
        		g.translate((d - rect.width) / 2, (d - rect.height) / 2);
        	}
        	g.scale(rect.xdoc.scale, rect.xdoc.scale);
        	ImgUtil.setImgRenderHint(g);
        	String v = rect.visible;
        	rect.visible = "true";
        	try {
        		rect.print(g);
        	} finally {
        		rect.visible = v;
        	}
        	g.dispose();
        }
        return bufImg;
    }
}
