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


import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import com.hg.util.MapUtil;
import com.hg.util.StrUtil;
import com.hg.util.To;

/**
 * 图形文本
 * @author whg
 */
public class EleSText extends EleShape {
    protected EleBase copyEle(XDoc xdoc) {
        return new EleSText(xdoc, this.getAttMap());
    }

    public EleSText(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public EleSText(XDoc xdoc) {
        super(xdoc);
    }
    protected void init() {
        super.init();
        typeName = "stext";
        spacing = "0";
        this.fontName = XFont.defaultFontName;
        this.format = "";
        this.width = 200;
        this.height = 100;
    }
    public String fontName;
    public String text;
    public boolean bold;
    public boolean italic;
    public String spacing;
    public String format;
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.fontName = MapUtil.getString(map, "fontName", this.fontName);
        this.text = MapUtil.getString(map, "text", this.text);
        this.bold = MapUtil.getBool(map, "bold", this.bold);
        this.italic = MapUtil.getBool(map, "italic", this.italic);
        this.spacing = MapUtil.getString(map, "spacing", this.spacing);
        this.format = MapUtil.getString(map, "format", this.format);
    }
    
    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("fontName", this.fontName);
        map.put("text", this.text);
        map.put("bold", String.valueOf(this.bold));
        map.put("italic", String.valueOf(this.italic));
        map.put("spacing", this.spacing);
        map.put("format", this.format);
        return map;
    }
    public Object clone() {
        return new EleSText(this.xdoc, this.getAttMap());
    }
	protected boolean canFill() {
		return this.text.length() > 0;
	}
    protected Shape getShape() {
        if (this.text.length() > 0) {
            int style = 0;
            style |= (this.bold ? Font.BOLD : Font.PLAIN);
            if (this.italic) {
                style |= Font.ITALIC;
            }
            Shape shape;
            String txt = DocUtil.printEval(this.text, this.xdoc);
            if (this.format.length() > 0) {
                txt = StrUtil.format(txt, this.format);
            }
            Font f = XFont.createFont(fontName, style, 100, txt);
            AffineTransform af;
            int tmpSpacing = 0;
            if (spacing.equals("auto")) {
                if (txt.length() > 1) {
                    int sn = txt.length() - 1;
                    if (this.layoutFlow.equals("v")) {
                        tmpSpacing = (int) (100 * this.height / (double) this.width - this.height) / sn / 2;
                    } else {
                        tmpSpacing = (int) (100 * this.width / (double) this.height - this.width) / sn / 2;
                    }
                    if (tmpSpacing < 0) {
                        tmpSpacing = 0;
                    }
                }
            } else {
                tmpSpacing = To.toInt(spacing);
            }
            if (this.layoutFlow.equals("v")) {
                GeneralPath path = new GeneralPath();
                int n = 0;
                for (int i = 0; i < txt.length(); i++) {
                    shape = PaintUtil.getOutline(String.valueOf(txt.charAt(i)), f);
                    af = new AffineTransform();
                    af.translate(0, n);
                    n += shape.getBounds().getHeight() + 6 + tmpSpacing;
                    shape = af.createTransformedShape(shape);
                    path.append(shape, false);
                }
                shape = path;
            } else {
            	if (tmpSpacing == 0) {
            		shape = PaintUtil.getOutline(txt, f);
            	} else {
                    GeneralPath path = new GeneralPath();
                    for (int i = 0; i < txt.length(); i++) {
                        path.append(PaintUtil.getOutline(String.valueOf(txt.charAt(i)), f, (100 + tmpSpacing) * i, 0), false);
                    }
                    shape = path;
            	} 
            }
            //缩放
            Rectangle2D bound = shape.getBounds2D();
            af = new AffineTransform();
            af.scale(this.width / bound.getWidth(), 
                    this.height / bound.getHeight());
            shape = af.createTransformedShape(shape);
            //移动
            bound = shape.getBounds2D();
            af = new AffineTransform();
            af.translate(-bound.getX(), -bound.getY());
            shape = af.createTransformedShape(shape);
            return shape;
        } else {
            return super.getShape();
        }
    }
    public void autoSize() {
        if (isBlank(this)) {
            if (this.layoutFlow.equals("v")) {
                if (this.sizeType.equals(SIZE_AUTOHEIGHT) || this.sizeType.equals(SIZE_AUTOSIZE)) {
                    this.height = (int) (txtBound(this.width).getWidth() * (1 + To.toInt(this.spacing) / 100));
                } else {
                    super.autoSize();
                }
            } else {
                if (this.sizeType.equals(SIZE_AUTOWIDTH) || this.sizeType.equals(SIZE_AUTOSIZE)) {
                    this.width = (int) (txtBound(this.height).getWidth() * (1 + To.toInt(this.spacing) / 100));
                } else {
                    super.autoSize();
                }
            }
        } else {
            super.autoSize();
        }
    }

    private Rectangle2D txtBound(int fontSize) {
        int style = 0;
        style |= (this.bold ? Font.BOLD : Font.PLAIN);
        if (this.italic) {
            style |= Font.ITALIC;
        }
        String txt = DocUtil.printEval(this.text, this.xdoc);
        Font f = XFont.createFont(fontName, style, fontSize);
        for (int i = 0; i < txt.length(); i++) {
            if (!Character.isSpaceChar(txt.charAt(i)) && !f.canDisplay(txt.charAt(i))) {
                f = XFont.createFont("", f.getStyle(), f.getSize());
                break;
            }
        }
        Rectangle2D rect = f.getStringBounds(txt, DocConst.g.getFontRenderContext());
        if (rect.getHeight() < f.getSize()) {
            rect.setFrame(rect.getX(), rect.getY(), rect.getWidth(), f.getSize() + 1);
        }
        return rect;
    }
    public EleText toText() {
        EleText txt = new EleText(this.xdoc);
        txt.bBold = this.bold;
        txt.bItalic = this.italic;
        txt.fontName = fontName;
        txt.fontSize = this.height;
        txt.text = this.text;
        txt.format = this.format;
        txt.fontColor = this.fillColor;
        txt.href = href;
        return txt;
    }
	protected void bindValue(String val) {
		this.text = val;
	}
}
