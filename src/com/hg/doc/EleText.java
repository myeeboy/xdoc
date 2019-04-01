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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;

import com.hg.util.ColorUtil;
import com.hg.util.MapUtil;
import com.hg.util.StrUtil;
import com.hg.util.To;

/**
 * 文本元素
 * @author whg
 */
public class EleText extends EleBase {
    protected EleBase copyEle(XDoc xdoc) {
        return new EleText(xdoc, this.getAttMap());
    }
    public Color fontColor;
    public Color backColor;
    public String backImg;
    public String fontName;
    public int fontSize;
    public int spacing;
    public String text;
    public int underline;
    public boolean bThroughline;
    public boolean bAboveline;
    public boolean bBold;
    public boolean bItalic;
    /**
     * 圆
     */
    public boolean bCircle;
    /**
     * 方框
     */
    public boolean bRect;
    /**
     * 阴影
     */
    public boolean bShadow;
    /**
     * 描边
     */
    public boolean bOutline;
    /**
     * 空心
     */
    public boolean bBlank;
    /**
     * 着重点
     */
    public boolean bStress;
    public String valign;
    public String href;
    public String toolTip;
    public String format;
    public Color underlineColor;
    public EleText(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public EleText(XDoc xdoc) {
        super(xdoc);
    }
    /**
	 * @param txt
	 * @param str
	 */
	public EleText(EleText txt, String str) {
		this(txt.xdoc, txt.getAttMap());
		this.setText(str);
	}
	protected void init() {
        super.init();
        name = "";
        this.text = "";
        this.typeName = "text";
        this.fontName = XFont.defaultFontName;
        this.fontSize = XFont.defaultFontSize;
        fontColor = Color.BLACK;
        backColor = null;
        underline = 0;
        bBold = false;
        bItalic = false;
        bCircle = false;
        bRect = false;
        bShadow = false;
        bStress = false;
        bOutline = false;
        bAboveline = false;
        bBlank = false;
        valign = DocConst.ALIGN_BOTTOM;
        href = "";
        toolTip = "";
        format = "";
        this.backImg = "";
        this.underlineColor = null;
        this.spacing = 0;
    }
    /**
     * 解析属性map
     */
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.fontColor = ColorUtil.strToColor(MapUtil.getString(map, "fontColor", ""), this.fontColor);
        if (map.containsKey("backColor")) {
            this.backColor = ColorUtil.strToColor(MapUtil.getString(map, "backColor", ""), null);
        }
        this.text = MapUtil.getString(map, ELEMENT_TEXT, this.text);
        this.text = MapUtil.getString(map, "text", this.text);
        this.spacing = MapUtil.getInt(map, "spacing", this.spacing);
        this.valign = MapUtil.getString(map, "valign", this.valign);
        this.href = MapUtil.getString(map, "href", this.href);
        this.toolTip = MapUtil.getString(map, "toolTip", this.toolTip);
        this.format = MapUtil.getString(map, "format", this.format);
        String strStyle = MapUtil.getString(map, "fontStyle", (String) this.getAttribute("fontStyle"));
        if (strStyle.indexOf("underline") >= 0) {
            int pos = strStyle.indexOf("underline") + 9;
            String ul;
            if (strStyle.indexOf(',', pos) < 0) {
                ul = strStyle.substring(pos);
            } else {
                ul = strStyle.substring(pos, strStyle.indexOf(',', pos));
            }
            if (ul.length() > 0) {
                String[] uls = ul.split("_");
                if (uls.length > 0) {
                    underline = To.toInt(uls[0]);
                }
                if (uls.length > 1) {
                    this.underlineColor = ColorUtil.strToColor(uls[1], null);
                }
            } else {
                underline = 1;
            }
            if (underline <= 0) {
                underline = 1;
            }
        } else {
            this.underline = 0;
        }
        bBold = strStyle.indexOf("bold") >= 0;
        bItalic = strStyle.indexOf("italic") >= 0;
        bThroughline = strStyle.indexOf("throughline") >= 0;
        bAboveline = strStyle.indexOf("aboveline") >= 0;
        bCircle = strStyle.indexOf("circle") >= 0;
        bRect = strStyle.indexOf("rect") >= 0;
        bShadow = strStyle.indexOf("shadow") >= 0;
        bStress = strStyle.indexOf("stress") >= 0;
        bOutline = strStyle.indexOf("outline") >= 0;
        bBlank = strStyle.indexOf("blank") >= 0;
        if (map.containsKey("fontSize")) {
        	this.fontSize = XFont.parseFontSize(MapUtil.getString(map, "fontSize", ""));
        }
        this.fontName = MapUtil.getString(map, "fontName", this.fontName);
        this.backImg = MapUtil.getString(map, "backImg", this.backImg);
    }
    /**
     * 解析属性map
     */
    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("fontColor", ColorUtil.colorToStr(this.fontColor));
        map.put("backColor", ColorUtil.colorToStr(this.backColor));
        map.put(ELEMENT_TEXT, this.text);
        map.put("fontName", this.fontName);
        map.put("fontSize", String.valueOf(this.fontSize));
        map.put("spacing", String.valueOf(this.spacing));
        map.put("valign", this.valign);
        map.put("href", this.href);
        map.put("toolTip", this.toolTip);
        StringBuffer style = new StringBuffer();
        if (this.bBold) {
            style.append("bold");
        }
        if (this.bItalic) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("italic");
        }
        if (this.underline > 0) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("underline");
            if (this.underline > 1) {
                style.append(this.underline);
            }
            if (this.underlineColor != null) {
                style.append("_").append(ColorUtil.colorToStr(this.underlineColor));
            }
        }
        if (this.bThroughline) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("throughline");
        }
        if (this.bAboveline) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("aboveline");
        }
        if (this.bCircle) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("circle");
        }
        if (this.bRect) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("rect");
        }
        if (this.bShadow) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("shadow");
        }
        if (this.bStress) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("stress");
        }
        if (this.bOutline) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("outline");
        }
        if (this.bBlank) {
            if (style.length() > 0) {
                style.append(",");
            }
            style.append("blank");
        }
        map.put("fontStyle", style.toString());
        map.put("format", format);
        map.put("backImg", this.backImg);
        return map;
    }
    public Object clone() {
        return new EleText(this.xdoc, this.getAttMap());
    }
    public boolean canSplit() {
    	boolean b = true;
    	if (this.text.length() > 1) {
    		if (this.format.length() == 0) {
    			int pos = this.text.indexOf(DocConst.PRINTMARK_PRE);
    			if (pos >= 0 && this.text.indexOf(DocConst.PRINTMARK_POST) > pos) {
    				b = false;
    			}
    		} else {
    			b = false;
    		}
    	}
    	return b;
    }
    public String viewText() {
        String str = DocUtil.printEval(this.text, this.xdoc);
        if (str.length() > 0) {
        	if (str.indexOf("\t") >= 0) {
        		str = StrUtil.replaceAll(str, "\t", tabStr());
        	}
        	if (this.format.length() > 0) {
        		str = StrUtil.format(str, this.format);
        	}
        }
        return str;
    }
    public void print(Graphics2D g, int x, int pos, int lineHeight, int lineSpacing) {
        print(g, x, pos, lineHeight, lineSpacing, true);
    }
    private void print(Graphics2D g, int x, int pos, int lineHeight, int lineSpacing, boolean checkFont) {
    	String str = this.viewText();
    	if (str.length() > 0) {
            if (checkFont) {
            	Font font = XFont.createFont(fontName, getFontStyle(), fontSize);
            	if (!XFont.canDisplay(font, str)) {
            		//分隔为多个显示
            		StringBuffer sb = new StringBuffer();
            		EleText tmp;
            		HashMap attMap = this.getAttMap();
            		boolean canDisplay = true;
            		for (int i = 0; i < str.length(); i++) {
            			if (!XFont.canDisplay(font, String.valueOf(str.charAt(i)))) {
            				if (sb.length() > 0 && canDisplay) {
            					tmp = new EleText(this.xdoc, attMap);
            					tmp.text = sb.toString();
            					tmp.format = "";
            					tmp.print(g, x, pos, lineHeight, lineSpacing, false);
            					x += tmp.getBounds().getWidth();
            					sb.setLength(0);
            				}
            				sb.append(str.charAt(i));
            				canDisplay = false;
            			} else {
            				if (sb.length() > 0 && !canDisplay) {
            					tmp = new EleText(this.xdoc, attMap);
            					tmp.text = sb.toString();
            					tmp.format = "";
            					tmp.print(g, x, pos, lineHeight, lineSpacing, false);
            					x += tmp.getBounds().getWidth();
            					sb.setLength(0);
            				}
            				sb.append(str.charAt(i));
            				canDisplay = true;
            			}
            		}
            		if (sb.length() > 0) {
            			tmp = new EleText(this.xdoc, attMap);
            			tmp.text = sb.toString();
            			tmp.format = "";
            			tmp.print(g, x, pos, lineHeight, lineSpacing, false);
            		}
            		return;
            	}
            }
    		g.setFont(getFont());
    		//背景色
    		Rectangle2D boundsRect = getStrBounds(g.getFont(), str);
    		if (!this.bOutline && !this.bBlank  && !this.bShadow && (this.backColor != null || this.backImg.length() > 0)) {
        		PaintUtil.fill(g, this.xdoc,
        				new Rectangle(x, pos, (int) boundsRect.getWidth(), lineHeight), 
        				this.backColor, this.backImg);
    		}
    		int descent = (int) (boundsRect.getHeight() + boundsRect.getY());
    		int adjustHeight = 0;
    		if (this.valign != null) {
    			if (this.valign.equals(DocConst.ALIGN_TOP)) {
    				adjustHeight = lineHeight - (int) boundsRect.getHeight() - lineSpacing;
    			} else if (this.valign.equals(DocConst.ALIGN_CENTER)) {
    				adjustHeight = (lineHeight - (int) boundsRect.getHeight() - lineSpacing) / 2;
    			}
    		}

        	int n = 0;
        	int offy = pos + lineHeight - descent - adjustHeight;
        	drawString(g, str, x, offy, false);
        	if (this.bStress || this.bCircle || this.bRect) {
        		int stressWidth = g.getFont().getSize() / 5;
        		Stroke stroke = this.getLineStroke();
        		for (int i = 0; i < str.length(); i++) {
        		    Rectangle2D bounds = getStrBounds(g.getFont(), String.valueOf(str.charAt(i)));
                    if (str.charAt(i) != ' ') {
                        if (this.bStress) {
                        	drawShape(g, new Ellipse2D.Double(x + n + (int) bounds.getWidth() / 2 - stressWidth / 2, offy + (int) (bounds.getHeight() + bounds.getY() - this.getULWeight()), stressWidth, stressWidth), stroke, this.fontColor, true);
                        }
                        if (this.bCircle) {
                        	drawShape(g, new Ellipse2D.Double(x + n, offy + (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight()), stroke, this.fontColor);
                        }
                        if (this.bRect) {
                        	drawShape(g, new Rectangle(x + n, offy + (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight()), stroke, this.fontColor);
                        }
                    }
        			n += bounds.getWidth();
        		}
        	}
    		if (this.underline > 0) {
    			drawLine(g, x, pos + lineHeight - adjustHeight - this.getULWeight(),
    					x + (int) boundsRect.getWidth(), getLineStroke(this.underline, getLineWeight()),
    					this.getULColor());
                PaintUtil.resetStroke(g);
    		}
            if (this.bThroughline) {
                drawLine(g, x, pos + lineHeight - (int) boundsRect.getHeight() / 2 - adjustHeight,
                        x + (int) boundsRect.getWidth(), getLineStroke(),
                        this.getFontColor());
            }
            if (this.bAboveline) {
                drawLine(g, x, pos,
                        x + (int) boundsRect.getWidth(), getLineStroke(),
                        this.getFontColor());
            }
    	}
    }
	private int getULWeight() {
		return (int) (Math.round(this.getLineWeight() * 2));
	}
    private Color getULColor() {
    	return this.underlineColor != null ? this.underlineColor : this.getFontColor();
	}
	private Color getFontColor() {
    	return this.fontColor != null ? this.fontColor : Color.BLACK;
	}
	public static boolean isVRotate(char c) {
        return c < 256 || "…～‖｜–︱—︳︴﹏（）｛｝〔〕【】《》〈〉「」『』﹙﹚﹛﹜﹝﹞".indexOf(c) >= 0;
    }
    public double printV(Graphics2D g, int x, int pos, int lineHeight, int lineSpacing) {
    	//单个字符
    	double cHeight = 0;
    	String str = this.viewText();
    	if (str.length() > 0) {
    		boolean vrotate = isVRotate(str.charAt(0));
    		g.setFont(this.getFont(str));
            //背景色
            Rectangle2D boundsRect = getStrBounds(g.getFont(), str, true);
            if (vrotate) {
                cHeight = boundsRect.getWidth();
            } else {
                cHeight = boundsRect.getHeight();
            }
            if (!this.bOutline && !this.bBlank  && !this.bShadow && this.backColor != null) {
        		PaintUtil.fill(g, this.xdoc,
        				new Rectangle(x, pos, lineHeight, (int) cHeight), 
        				this.backColor, this.backImg);
            }
            int adjustHeight = 0;
            if (this.valign != null) {
                if (this.valign.equals(DocConst.ALIGN_TOP)) {
                    adjustHeight = lineHeight - (int) boundsRect.getWidth() - lineSpacing;
                } else if (this.valign.equals(DocConst.ALIGN_CENTER)) {
                    adjustHeight = (lineHeight - (int) boundsRect.getWidth() - lineSpacing) / 2;
                }
            }
            int offx = x + adjustHeight;
            int offy = (int) (pos - boundsRect.getY());
            g.setColor(this.fontColor);
            int stressWidth = g.getFont().getSize() / 5;
            if (vrotate) {
            	Graphics2D cg = (Graphics2D) g.create(offx, offy - g.getFont().getSize() * 5 / 6, (int) boundsRect.getHeight(), (int) boundsRect.getWidth());
            	cg.rotate(90 * Math.PI / 180, 0, 0);
            	drawString(cg, str, spacing / 2, -g.getFont().getSize() / 6, true);
            	cg.dispose();
            	if (str.charAt(0) != ' ') {
            		Stroke stroke = getLineStroke();
            		if (this.bStress) {
                    	drawShape(g, new Ellipse2D.Double(offx - stressWidth, (int) (offy + boundsRect.getY() + boundsRect.getWidth() / 2), stressWidth, stressWidth), stroke, this.fontColor, true);
            		}
                    if (this.bCircle) {
                    	drawShape(g, new Ellipse2D.Double(offx, offy + (int) boundsRect.getY(), (int) boundsRect.getHeight(), (int) boundsRect.getWidth()), stroke, this.fontColor);
                    }
                    if (this.bRect) {
                    	drawShape(g, new Rectangle(offx, offy + (int) boundsRect.getY(), (int) boundsRect.getHeight(), (int) boundsRect.getWidth()), stroke, this.fontColor);
                    }
            	}
            } else {
            	boolean trans = "，。；：、？！".indexOf(str.charAt(0)) >= 0;
            	if (trans) {
            		g.translate(boundsRect.getWidth() / 2, -boundsRect.getHeight() / 4);
            	}
            	drawString(g, str, offx, offy + spacing / 2, true);
            	if (trans) {
            		g.translate(-boundsRect.getWidth() / 2,  boundsRect.getHeight() / 4);
            	}
            	Stroke stroke = getLineStroke();
            	if (this.bStress) {
                	drawShape(g, new Ellipse2D.Double(offx - stressWidth, (int) (offy + boundsRect.getY() / 2 + stressWidth / 2), stressWidth, stressWidth), stroke, this.fontColor, true);
            	}
            	if (this.bCircle) {
                	drawShape(g, new Ellipse2D.Double(offx, offy + (int) boundsRect.getY(), (int) boundsRect.getWidth(), (int) boundsRect.getHeight()), stroke, this.fontColor);
            	}
                if (this.bRect) {
                	drawShape(g, new Rectangle(offx, offy + (int) boundsRect.getY(), (int) boundsRect.getWidth(), (int) boundsRect.getHeight()), stroke, this.fontColor);
                }
            }
            if (this.underline > 0) {
                drawLineV(g, offx, pos,
                		pos + (int) (vrotate ? boundsRect.getWidth() : boundsRect.getHeight()), getLineStroke(this.underline, getLineWeight()), this.getULColor());
                PaintUtil.resetStroke(g);
            }
            if (this.bThroughline) {
                drawLineV(g, offx + (int) boundsRect.getHeight() / 2 + adjustHeight, pos,
                		pos + (int) (vrotate ? boundsRect.getWidth() : boundsRect.getHeight()), getLineStroke(), this.getFontColor());
            }
            if (this.bAboveline) {
                drawLineV(g, offx + (int) boundsRect.getHeight() + adjustHeight, pos,
                        pos + (int) (vrotate ? boundsRect.getWidth() : boundsRect.getHeight()), getLineStroke(), this.getFontColor());
            }
        }
        return cHeight;
    }
	private Stroke getLineStroke() {
		return getLineStroke(1, getLineWeight());
	}
    private void drawString(Graphics2D g, String str, int x, int y, boolean v) {
        if (this.xdoc.print != null) {
            if (this.href.length() > 0) {
                Rectangle2D rect = g.getFont().getStringBounds(str, g.getFontRenderContext());
                rect.setFrame(rect.getX() + x, rect.getY() + y, rect.getWidth(), rect.getHeight());
                this.xdoc.print.hrefs.add(new NameShape(getHrefName(), g.getTransform().createTransformedShape(rect)));
            }
            if (this.toolTip.length() > 0) {
                Rectangle2D rect = g.getFont().getStringBounds(str, g.getFontRenderContext());
                rect.setFrame(rect.getX() + x, rect.getY() + y, rect.getWidth(), rect.getHeight());
                this.xdoc.print.toolTips.add(new NameShape(this.toolTip, g.getTransform().createTransformedShape(rect)));
            }
        }
        if (this.bShadow) {
        	PaintUtil.setPaint(g, this.xdoc, this.backColor != null ? this.backColor : Color.LIGHT_GRAY, this.backImg);
        	int offset = this.getShadowOffset();
        	actDrawString(g, str, x + offset, y + offset, v);
        }
        if (this.bOutline || this.bBlank) {
        	Shape shape = getShape(str, g.getFont(), x, y, v);
        	if (this.bOutline) {
            	PaintUtil.setPaint(g, this.xdoc, this.backColor != null ? this.backColor : Color.YELLOW, this.backImg);
        		g.setStroke(new BasicStroke(g.getFont().getSize2D() / 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        		g.draw(shape);
        		g.setStroke(new BasicStroke());
        	}
        	if (this.bBlank) {
        		g.setStroke(new BasicStroke(this.getBlankWeight(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        		if (this.backColor != null || this.backImg.length() > 0) {
                	PaintUtil.fill(g, this.xdoc, shape, this.backColor, this.backImg);
        		}
        		g.setColor(this.fontColor != null ? this.fontColor : Color.BLACK);
        		g.draw(shape);
        		g.setStroke(new BasicStroke());
        	}
        }
        if (!this.bBlank) {
        	g.setColor(this.fontColor != null ? this.fontColor : Color.BLACK);
        	actDrawString(g, str, x, y, v);
        }
    }
    private String getHrefName() {
    	return DocUtil.fixHref(this.href, this.name.length() > 0 ? name : this.text);
	}
	protected Shape getShape(String str, Font font, int x, int y, boolean vertical) {
    	return PaintUtil.getOutline(str, font, x, y, vertical ? 0 : this.getSpacing(font.getSize()));
	}
	public Rectangle2D getBounds() {
    	String str = this.viewText();
    	if (str.length() > 0) {
    		return getStrBounds(this.getFont(str), str);
    	} else {
    		return new Rectangle2D.Double();
    	}
    }
    public static final String tabStr() {
        return tabStr(null);
    }
    public static final String tabStr(Font font) {
        return "　　";
    }
    public Rectangle2D getStrBounds(Font font, String str) {
    	return getStrBounds(font, str, false);
    }
    public Rectangle2D getStrBounds(Font font, String str, boolean vertical) {
		Rectangle2D rect = null;
    	if (!vertical || str.length() == 1 && isVRotate(str.charAt(0))) {
    		if (str.indexOf("\t") >= 0) {
    			str = StrUtil.replaceAll(str, "\t", tabStr(font));
    		}
    		rect = font.getStringBounds(str, DocConst.frc);
    		if (rect.getHeight() < font.getSize()) {
    			rect.setFrame(rect.getX(), rect.getY(), rect.getWidth(), font.getSize() + 1);
    		}
    		if (spacing != 0 && str.length() > 0) {
    			rect.setFrame(rect.getX(), rect.getY(),
    					rect.getWidth() + str.length() * getSpacing(font.getSize()),
    					rect.getHeight());
    		}
    	} else {
    		if (str.indexOf("\t") >= 0) {
    			str = StrUtil.replaceAll(str, "\t", tabStr(font));
    		}
    		rect = font.getStringBounds(str, DocConst.frc);
    		if (rect.getHeight() < font.getSize()) {
    			rect.setFrame(rect.getX(), rect.getY(), rect.getWidth(), font.getSize() + 1);
    		}
    		if (spacing != 0 && str.length() > 0) {
    			rect.setFrame(rect.getX(), rect.getY(),
    					rect.getWidth(),
    					rect.getHeight() + str.length() * getSpacing(font.getSize()));
    		}
    	}
        return rect;
    }
    protected int getSpacing(int fontSize) {
    	return (int) (this.spacing * ((double) fontSize / this.fontSize));
    }
    public int getShadowOffset() {
    	return this.getLineWeight() + 1;
    }
    public int getLineWeight() {
    	int weight = this.fontSize / 14;
    	if (weight == 0) {
    		weight = 1;
    	}
    	return weight;
	}
	public static Stroke getLineStroke(int type, float weight) {
        Stroke stroke = null;
        if (type == 1) {
            stroke = new BasicStroke(weight, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 20.0f);
        } else if (type == 2) { //双线
            stroke = TextStrokeUtil.createStroke(ShapeUtil.strToShape("M 0 0 L 10 0 L 10 5 L 0 5 L 0 0 Z M 0 25 L 10 25 L 10 30 L 0 30 L 0 25 Z"), 4 * weight);
        } else if (type == 3) { //粗线
            stroke = new BasicStroke(weight * 2);
        } else if (type == 4) { //虚线1
            stroke = new BasicStroke(weight,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 20.0f,
                    new float[] {weight * 2}, weight);
        } else if (type == 5) { //虚线2
            stroke = new BasicStroke(weight,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 20.0f,
                    new float[] {5 * weight}, weight);
        } else if (type == 6) { //虚线3
            stroke = new BasicStroke(weight,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 20.0f,
                    new float[] {10 * weight,weight * 2,weight * 2,weight * 2}, weight);
        } else if (type == 7) { //虚线4
            stroke = new BasicStroke(weight,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 20.0f,
                    new float[] {10 * weight,weight * 2,weight * 2,weight * 2,weight * 2,weight * 2}, weight);
        } else if (type == 8) { //波浪线
            stroke = TextStrokeUtil.createStroke(ShapeUtil.strToShape("M 0 0 L 15 20 L 30 0 L 30 10 L 15 30 L 0 10 Z"), 3 * weight);
        } else if (type == 9) { //上粗下细
            stroke = TextStrokeUtil.createStroke(ShapeUtil.strToShape("M 0 0 L 10 0 L 10 15 L 0 15 L 0 0 Z M 0 25 L 10 25 L 10 30 L 0 30 L 0 25 Z"), 4 * weight);
        } else if (type == 10) { //下粗上细
            stroke = TextStrokeUtil.createStroke(ShapeUtil.strToShape("M 0 0 L 10 0 L 10 5 L 0 5 L 0 0 Z M 0 15 L 10 15 L 10 30 L 0 30 L 0 15 Z"), 4 * weight);
        } else {
            stroke = new BasicStroke(weight, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 20.0f);
        }
        return stroke;
    }
    private void drawLine(Graphics2D g, int x, int y, int x2, Stroke stroke, Color lineColor) {
        if (this.bOutline || this.bShadow) {
            if (this.bOutline) {
            	PaintUtil.setPaint(g, this.xdoc, this.backColor != null ? this.backColor : Color.YELLOW, this.backImg);
                g.setStroke(new BasicStroke(g.getFont().getSize2D() / 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 20.0f));
                g.draw(stroke.createStrokedShape(new Line2D.Float(x, y, x2, y)));
            }
            if (this.bShadow) {
            	PaintUtil.setPaint(g, this.xdoc, this.backColor != null ? this.backColor : Color.LIGHT_GRAY, this.backImg);
                g.setStroke(stroke);
        		int offset = this.getShadowOffset();
                g.draw(new Line2D.Float(x + offset, y + offset, x2 + offset, y + offset));
            }
        }
        g.setColor(lineColor);
        g.setStroke(stroke);
        g.draw(new Line2D.Float(x, y, x2, y));
    }
    private void drawLineV(Graphics2D g, int x, int y, int y2, Stroke stroke, Color lineColor) {
        if (this.bOutline || this.bShadow) {
            if (this.bOutline) {
            	PaintUtil.setPaint(g, this.xdoc, this.backColor != null ? this.backColor : Color.YELLOW, this.backImg);
                g.setStroke(new BasicStroke(g.getFont().getSize2D() / 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 20.0f));
                g.draw(new Line2D.Float(x, y, x, y2));
            }
            if (this.bShadow) {
            	PaintUtil.setPaint(g, this.xdoc, this.backColor != null ? this.backColor : Color.LIGHT_GRAY, this.backImg);
                g.setStroke(stroke);
        		int offset = this.getShadowOffset();
                g.draw(new Line2D.Float(x + offset, y + offset, x + offset, y2 + offset));
            }
        }
        g.setColor(lineColor);
        g.setStroke(stroke);
        g.draw(new Line2D.Float(x, y, x, y2));
    }
    private void drawShape(Graphics2D g, Shape shape, Stroke stroke, Color lineColor) {
    	drawShape(g, shape, stroke, lineColor, false);
    }
    private void drawShape(Graphics2D g, Shape shape, Stroke stroke, Color lineColor, boolean fill) {
        if (this.bOutline || this.bShadow) {
            if (this.bOutline) {
            	PaintUtil.setPaint(g, this.xdoc, this.backColor != null ? this.backColor : Color.YELLOW, this.backImg);
                g.setStroke(new BasicStroke(g.getFont().getSize2D() / 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 20.0f));
                g.draw(shape);
            }
            if (this.bShadow) {
            	PaintUtil.setPaint(g, this.xdoc, this.backColor != null ? this.backColor : Color.LIGHT_GRAY, this.backImg);
                g.setStroke(stroke);
        		int offset = this.getShadowOffset();
        		g.translate(offset, offset);
                if (fill) {
                	g.fill(shape);
                } else {
                	g.draw(shape);
                }
                g.translate(-offset, -offset);
            }
        }
        g.setColor(lineColor);
        if (fill) {
        	g.fill(shape);
        } else {
        	g.setStroke(stroke);
        	g.draw(shape);
        }
    }
    public String toString() {
        return this.viewText();
    }
    public boolean attEquals(EleText txt) {
        HashMap map1 = this.getAttMap();
        HashMap map2 = txt.getAttMap();
        map1.remove(ELEMENT_TEXT);
        Iterator it = map1.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String) it.next();
            if (!map1.get(key).equals(map2.get(key))) {
                return false;
            }
        }
        return true;
    }
    public void setUnknownText(String str) {
        StringBuffer sb = new StringBuffer();
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '\r') {
            	continue;
            } else if (!Character.isDefined(c)) {
            	sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        this.text = sb.toString();
    }
    public Stroke getUnderlineStroke(double scale) {
        return getLineStroke(this.underline, (float) (this.getLineWeight() * scale));
    }
    public void setText(String text) {
        this.setAttribute(ELEMENT_TEXT, text);
    }
	public float getBlankWeight() {
		return Math.max(this.getLineWeight() / 4f, 0.5f);
	}
	public int getFontStyle() {
		int style = Font.PLAIN;
		if (this.bBold) {
			style |= Font.BOLD;
		}
		if (this.bItalic) {
			style |= Font.ITALIC;
		}
		return style;
	}
	public Font getFont() {
		return getFont(this.text);
	}
	public Font getFont(String text) {
		return XFont.createFont(fontName, getFontStyle(), fontSize, text);
	}
    protected void actDrawString(Graphics2D g, String str, int x, int y) {
    	actDrawString(g, str, x, y, false);
    }
    protected void actDrawString(Graphics2D g, String str, int x, int y, boolean vertical) {
    	PaintUtil.drawString(g, str, x, y, vertical ? 0 : this.getSpacing(g.getFont().getSize()));
    }
    public String getText() {
    	return this.text;
    }
}
