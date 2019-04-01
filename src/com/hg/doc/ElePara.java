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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import com.hg.util.ColorUtil;
import com.hg.util.MapUtil;
import com.hg.util.StrUtil;
import com.hg.util.To;

/**
 * 段落，以回车换行结束的一段
 * @author whg
 */
public class ElePara extends EleBase {
    public static final String PREFIX_TAB = "{t}";
    public String headInx;
	protected EleBase copyEle(XDoc xdoc) {
        return new ElePara(xdoc, this.getAttMap());
    }
    public int lineSpacing;
    public String align;
    public boolean breakPage;
    public int heading;
    public String prefix;
    public Color backColor;
    public String backImg;
    //段落缩进
    public int indentLeft;
    public int indentRight;
    public ElePara(XDoc xdoc, HashMap attMap) {
        super(xdoc, attMap);
    }
    public ElePara(XDoc xdoc) {
        super(xdoc);
    }
    /**
	 * @param xdoc
	 * @param preEles
	 */
	public ElePara(XDoc xdoc, ArrayList eleList) {
		this(xdoc);
		this.eleList = eleList;
	}
	protected void init() {
        super.init();
        name = "";
        typeName = "para";
        align = DocConst.ALIGN_LEFT;
        lineSpacing = 0;
        indentLeft = 0;
        indentRight = 0;
        heading = 0;
        breakPage = false;
        prefix = "";
        backColor = null;
        backImg = "";
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.prefix.length() > 0 
                && !this.prefix.startsWith("<") 
                && !this.prefix.startsWith("img:")
                && !this.prefix.startsWith("shape:")) {
            sb.append(this.replaceInx(this.prefix));
        }
        Object obj;
        for (int i = 0; i < this.eleList.size(); i++) {
            obj = this.eleList.get(i);
            if (obj instanceof EleText) {
                sb.append(((EleText) obj).viewText());
            } else {
                sb.append(obj.toString());
            }
        }
        return sb.toString();
    }
    /**
     * 解析属性map
     */
    public void setAttMap(HashMap map) {
        super.setAttMap(map);
        this.lineSpacing = MapUtil.getInt(map, "lineSpacing", this.lineSpacing);
        if (map.containsKey("indent")) {
        	String[] indent = MapUtil.getString(map, "indent", "").split(",");
        	if (indent.length > 0) {
        		this.indentLeft = To.toInt(indent[0]);
        	}
        	this.indentRight = indent.length > 1 ? To.toInt(indent[1]) : 0;
        }
        this.heading = MapUtil.getInt(map, "heading", this.heading);
        this.align = MapUtil.getString(map, "align", this.align);
        this.breakPage = MapUtil.getBool(map, "breakPage", this.breakPage);
        this.prefix = MapUtil.getString(map, "prefix", this.prefix);
        if (map.containsKey("backColor")) {
            this.backColor = ColorUtil.strToColor(MapUtil.getString(map, "backColor", ""), null);
        }
        this.backImg = MapUtil.getString(map, "backImg", this.backImg);
    }
    /**
     * 解析属性map
     */
    public HashMap getAttMap() {
        HashMap map = super.getAttMap();
        map.put("lineSpacing", String.valueOf(this.lineSpacing));
        map.put("indent", this.indentLeft + (this.indentRight != 0 ? ("," + this.indentRight): ""));
        map.put("heading", String.valueOf(this.heading));
        map.put("align", this.align);
        map.put("prefix", this.prefix);
        map.put("breakPage", String.valueOf(this.breakPage));
        map.put("backColor", ColorUtil.colorToStr(this.backColor));
        map.put("backImg", this.backImg);
        return map;
    }
    public Object clone() {
        return new ElePara(xdoc, this.getAttMap());
    }
    public int layWidth = 0;
    public ArrayList getPrefixEles() {
        ArrayList preEles = null;
        if (this.prefix.length() > 0) {
            preEles = new ArrayList();
            HashMap atts;
            int n = XFont.defaultFontSize;
            if (this.eleList.size() > 0) {
                EleBase ele = (EleBase) this.eleList.get(0);
                if (ele instanceof EleText) {
                    n = ((EleText) ele).fontSize;
                } else if (ele instanceof EleRect) {
                    n = ((EleRect) ele).height;
                }
                atts = ele.getAttMap();
                atts.remove("format");
                if (atts.containsKey("fontStyle")) {
                    atts.put("fontStyle", fixPrefixFontStyle((String) atts.get("fontStyle")));
                }
            } else {
                atts = new HashMap();
            }
            String str = this.prefix;
            if (str.indexOf(PREFIX_TAB) >= 0) {
                str = StrUtil.replaceAll(str, PREFIX_TAB, "　　");
            }
            EleText text;
            if (str.startsWith("img:<")) { //xdoc式图片矢量处理
                str = str.substring(4);
            }
            if (str.startsWith("img:")) {
                EleImg img = new EleImg(this.xdoc);
                img.fillColor = null;
                img.src = str.substring(4);
                img.valign = MapUtil.getString(atts, "valign");
                img.sizeType = EleRect.SIZE_AUTOSIZE;
                img.autoSize();
                img.sizeType = EleRect.SIZE_NORMAL;
                double scale = img.height / (double) img.width;
                img.height = n;
                img.width = (int) (n / scale);
                preEles.add(img);
            } else {
                str = replaceInx(str);
                if (str.startsWith("<") && str.endsWith(">")) {
                    EleGroup gshape = new EleGroup(xdoc);
                    gshape.text = str;
                    gshape.color = null;
                    gshape.valign = MapUtil.getString(atts, "valign");
                    gshape.width = n;
                    gshape.height = n;
                    EleRect rect = DocUtil.getRect(this.xdoc, str);
                    if (rect != null) {
                        double scale = rect.height / (double) rect.width;
                        gshape.height = n;
                        gshape.width = (int) (n / scale);
                    }
                    preEles.add(gshape);
                } else if (str.indexOf('{') >= 0) {
                    int pos = 0;
                    while (true) {
                        if (str.indexOf('{', pos) >= 0 && str.indexOf('}', pos) > str.indexOf('{', pos)) {
                            text = new EleText(this.xdoc, atts);
                            text.text = str.substring(pos, str.indexOf('{', pos));
                            preEles.add(text);
                            String tmpStr = str.substring(str.indexOf('{', pos) + 1, str.indexOf('}', pos));
                            if (tmpStr.startsWith(":")) {
                                EleImg img = new EleImg(this.xdoc, atts);
                                img.color = null;
                                img.width = n;
                                img.height = n;
                                img.src = tmpStr.substring(1);
                                preEles.add(img);
                            } else {
                                text = new EleText(this.xdoc, atts);
                                text.text = tmpStr;
                                preEles.add(text);
                            }
                            pos = str.indexOf('}', pos) + 1;
                        } else {
                            if (str.substring(pos).length() > 0) {
                                text = new EleText(this.xdoc, atts);
                                text.text = str.substring(pos);
                                preEles.add(text);
                            }
                            break;
                        }
                    }
                } else if (str.startsWith("shape:")) {
                    atts.put("shape", str.substring(6));
                    preEles.add(new EleCharRect(this.xdoc, atts));
                } else {
                    text = new EleText(this.xdoc, atts);
                    text.text = str;
                    ElePara.fixPrefix(text);
                    preEles.add(text);
                }
            }
            if (!this.prefix.equals(PREFIX_TAB)) {
                text = new EleText(this.xdoc, atts);
                text.text = " ";
                preEles.add(text);
            }
        }
        return preEles;
    }
    public static String fixPrefixFontStyle(String style) {
        String[] styles = style.split(",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < styles.length; i++) {
            if (styles[i].equals("bold")
                    || styles[i].equals("italic")
                    || styles[i].equals("shadow")
                    || styles[i].equals("outline")) {
                if (sb.length() > 0) sb.append(",");
                sb.append(styles[i]);
            }
        }
        return sb.toString();
    }
    protected boolean isBlank() {
        return this.eleList.size() == 0 || this.eleList.size() == 1 && this.eleList.get(0) instanceof EleText && ((EleText) this.eleList.get(0)).text.length() == 0;
    }
    public ArrayList toLineList(int top, int width, ArrayList hrList, boolean h) {
    	this.layWidth = width - this.indentLeft - this.indentRight;
        if (isBlank()) {
            ArrayList lineList = new ArrayList();
            lineList.add(new DocPageLine(this, new ArrayList(), this.lineSpacing + XFont.defaultFontSize, 0));
            return lineList;
        }
        ArrayList preEles = getPrefixEles();
        EleRect prefixRect = null;
        if (preEles != null) {
        	prefixRect = new EleRect(this.xdoc);
        	prefixRect.color = null;
            prefixRect.txtPadding = 0;
            prefixRect.sizeType = EleRect.SIZE_AUTOSIZE;
            if (preEles.size() > 0) {
                if (preEles.get(0) instanceof EleText) {
                	prefixRect.valign = ((EleText) preEles.get(0)).valign;
                } else if (preEles.get(0) instanceof EleRect) {
                	prefixRect.valign = ((EleRect) preEles.get(0)).valign;
                }
            }
            prefixRect.eleList.add(new ElePara(this.xdoc, preEles));
            prefixRect.autoSize();
        }
        width -= this.indentRight;
        if (h) {
        	return toLineListH(top, width, hrList, prefixRect);
        } else {
        	return toLineListV(top, width, hrList, prefixRect);
        }
    }
    private String replaceInx(String prefix) {
    	StringBuffer sb = new StringBuffer();
    	int pos = 0;
    	while (true) {
    		if (prefix.indexOf('{', pos) >= 0 && prefix.indexOf('}', pos) > prefix.indexOf('{', pos)) {
    			sb.append(prefix.substring(pos, prefix.indexOf('{', pos)));
    			String str = prefix.substring(prefix.indexOf('{', pos) + 1, prefix.indexOf('}', pos));
                if (str.startsWith(":")) {
                    sb.append("{").append(str).append("}");
                } else {
                    if (this.headInx != null && this.headInx.length() > 0) {
                        String[] strs = this.headInx.split("\\.");
                        if (str.length() == 1) {
                            sb.append(StrUtil.toStrInt(str.charAt(0), Integer.parseInt(strs[strs.length - 1])));
                        } else if (str.length() > 1) {
                            String sp = str.substring(1);
                            for (int i = 0; i < strs.length; i++) {
                                if (i > 0) {
                                    sb.append(sp);
                                }
                                sb.append(StrUtil.toStrInt(str.charAt(0), Integer.parseInt(strs[i])));
                            }
                        }
                    }
                }
    			pos = prefix.indexOf('}', pos) + 1;
    		} else {
    			sb.append(prefix.substring(pos));
    			break;
    		}
    	}
    	return sb.toString();
	}
    private static final String postSign = ",.!}])?%，、。．；：？！︰…‥′‵～﹐﹑﹒﹔﹕﹖﹗％‰）︶｝︸〕︺】︼》︾〉﹀」﹂』﹄﹚﹜﹞’”〞ˊ";
    private static final String preSign = "{[($￡￥＄（︵｛︷〔︹【︻《︽〈︿「﹁『﹃﹙﹛﹝‘“〝ˋ";
    public static boolean isPreSign(char c) {
        return preSign.indexOf(String.valueOf(c)) >= 0;
    }
    public static boolean isPostSign(char c) {
        return postSign.indexOf(String.valueOf(c)) >= 0;
    }
    private ArrayList toLineListH(int top,int width, ArrayList hrList, EleRect prefixRect) {
        ArrayList lineList = new ArrayList();
        int prefixWidth = prefixRect != null ? prefixRect.width : 0;
        if (width <= prefixWidth) {
            return lineList;
        }
        Object ele = null;
        //行高度,高度计数器
        int lineWidth = this.indentLeft + prefixWidth, lineHeight = 0;
        if (this.isPrefixTab()) {
        	prefixWidth = 0;
        }
        //行元素列表
        ArrayList lineEleList = new ArrayList();
        //元素是否在一行内处理完毕
        boolean eleProcessed = true;
        String tmpText;
        HindRance hr = HindRance.next(hrList, this.indentLeft, top, 10);
        Font font;
        String href = null;
        for (int i = 0; i < this.eleList.size(); i++) {
            if (eleProcessed) {
                ele = this.eleList.get(i);
                if (ele instanceof EleText && ((EleText) ele).href.equals("#")) {
                    EleText txtEle = (EleText) ele;
                    href = txtEle.name.length() > 0 ? txtEle.name : txtEle.text;
                } else {
                	href = null;
                }
            }
            if (ele instanceof EleText) {
                EleText txtEle = (EleText) ele;
                if (eleProcessed) {
                	tmpText = txtEle.viewText();
                } else {
                	tmpText = txtEle.text;
                }
                font = txtEle.getFont(tmpText);
                Rectangle2D bounds = txtEle.getStrBounds(font, tmpText);
                if (!txtEle.valign.equals(DocConst.ALIGN_AROUND) && bounds.getHeight() + this.lineSpacing > lineHeight) {
                    lineHeight = (int) bounds.getHeight() + this.lineSpacing;
                }
                if (((lineWidth + (int) bounds.getWidth()) > width
	                		|| hr != null && lineWidth + bounds.getWidth() > hr.x
	                		|| tmpText.indexOf('\n') >= 0)
	                	&& txtEle.canSplit()) {
                    //超长,截断元素,绘制本行后折行
                    for (int j = 0; j < tmpText.length(); j++) {
                        if (tmpText.charAt(j) != '\n') {
                            bounds = txtEle.getStrBounds(font, String.valueOf(tmpText.charAt(j)));
                            if (hr != null && lineWidth + bounds.getWidth() > hr.x) {
                                //加入前半部分文字
                                EleText txtEle2 = new EleText(txtEle.xdoc, txtEle.getAttMap());
                                if (href != null) {
                                	txtEle2.name = href;
                                }
                                txtEle2.text = tmpText.substring(0, j);
                                lineEleList.add(txtEle2);
                                if (((EleText) ele).valign.equals(DocConst.ALIGN_AROUND)) {
                                    bounds = txtEle2.getBounds();
                                    hrList.add(new HindRance(new Rectangle(lineWidth, top, (int) bounds.getWidth(), (int) bounds.getHeight())));
                                }
                                //加入间隔
                                lineEleList.add(new EleSpace(xdoc, (int) (hr.width + hr.x - lineWidth), 1));
                                lineWidth = this.indentLeft + prefixWidth + hr.x + hr.width;
                                hr = HindRance.next(hrList, lineWidth, top, lineHeight);
                                //修改当前元素
                                txtEle2 = new EleText(txtEle.xdoc, txtEle.getAttMap());
                                if (href != null) {
                                	txtEle2.name = href;
                                }
                                txtEle2.text = tmpText.substring(j);
                                eleProcessed = false;
                                i--;
                                ele = txtEle2;
                                break;
                            }
                            lineWidth += bounds.getWidth();
                        }
                        if (lineWidth >= width || j == tmpText.length() - 1 || tmpText.charAt(j) == '\n') {
                        	if (j > 1 && isPostSign(tmpText.charAt(j))) {
                        		j--;
                        	}
                        	if (j > 0) {
                        		//保持单词和数字完整
                        		if (StrUtil.isLetter(tmpText.charAt(j))) {
	                        		for (int m = j - 1; m >= 0; m--) {
	                        			if (!StrUtil.isLetter(tmpText.charAt(m))) {
	                                        if (j - m <= 18) {
	                                        	lineWidth -= (int) txtEle.getStrBounds(font, tmpText.substring(m, j)).getWidth();
	                                            j = m + 1;
	                                        }
	                        				break;
	                        			}
	                        		}
	                       		} else if (StrUtil.isDigit(tmpText.charAt(j)) || tmpText.charAt(j) == '.') {
	                        		for (int m = j - 1; m >= 0; m--) {
	                        			if (!StrUtil.isDigit(tmpText.charAt(m)) && tmpText.charAt(m) != '.') {
	                                        if (j - m <= 18) {
	                                        	lineWidth -= (int) txtEle.getStrBounds(font, tmpText.substring(m, j)).getWidth();
	                                            j = m + 1;
	                                        }
	                        				break;
	                        			}
	                        		}
	                       		}
                        	}
                        	if (j > 1 && isPreSign(tmpText.charAt(j - 1))) {
                        		j--;
                        	}
                            //超长,截断产生新元素加入
                            EleText txtEle2 = new EleText(txtEle.xdoc, txtEle.getAttMap());
                            if (href != null) {
                            	txtEle2.name = href;
                            }
                            if (j == 0) {
                            	j = 1;
                            }
                            txtEle2.text = tmpText.substring(0, j);
                            if (txtEle2.text.endsWith(" ")) { //去掉末尾最后一个空格
                            	txtEle2.text = txtEle2.text.substring(0, txtEle2.text.length() - 1);
                            }
                            if (j < tmpText.length() && tmpText.charAt(j) == ' ') { //去掉下一行第一个空格
                                j++;
                            }
                            DocPageLine line = new DocPageLine(this, lineEleList, lineHeight, lineWidth);
                            line.enter = (j < tmpText.length() && tmpText.charAt(j) == '\n');
                            lineList.add(line);
                            if (j < tmpText.length() && tmpText.charAt(j) == '\n') {
                                j++;
                            }
                            lineEleList.add(txtEle2);
                            if (((EleText) ele).valign.equals(DocConst.ALIGN_AROUND)) {
                                bounds = txtEle2.getBounds();
                                hrList.add(new HindRance(new Rectangle(lineWidth, top, (int) bounds.getWidth(), (int) bounds.getHeight())));
                            }
                            lineWidth = this.indentLeft + prefixWidth;
                            top += lineHeight;
                            hr = HindRance.next(hrList, lineWidth, top, lineHeight);
                            lineHeight = 0;
                            //后半部分作为折行
                            lineEleList = new ArrayList();
                            if (j < tmpText.length()) {
                                txtEle2 = new EleText(txtEle.xdoc, txtEle.getAttMap());
                                if (href != null) {
                                	txtEle2.name = href;
                                }
                                txtEle2.text = tmpText.substring(j);
                                eleProcessed = false;
                                i--;
                                ele = txtEle2;
                            } else {
                                eleProcessed = true;
                            }
                            break;
                        }
                    }
                } else {
                    if (((EleText) ele).valign.equals(DocConst.ALIGN_AROUND)) {
                        hrList.add(new HindRance(new Rectangle(lineWidth, top, (int) bounds.getWidth(), (int) bounds.getHeight())));
                    }
                    lineWidth += bounds.getWidth();
                    lineEleList.add(ele);
                    eleProcessed = true;
                }
            } else if (ele instanceof EleRect) {
                EleRect rect;
                if (ele instanceof EleLine) {
                    rect = new EleRect(xdoc);
                    EleLine eleLine = (EleLine) ele;
                    Rectangle rectangle = new Rectangle(Math.min(eleLine.startX, eleLine.endX), Math.min(eleLine.startY, eleLine.endY),
                            Math.abs(eleLine.startX - eleLine.endX), Math.abs(eleLine.startY - eleLine.endY));
                    rect.top -= eleLine.strokeWidth / 2;
                    rect.width = rectangle.width;
                    rect.height = rectangle.height + (int) eleLine.strokeWidth;
                    rect.valign = eleLine.valign;
                } else {
                    rect = (EleRect) ele;
                    rect.autoSize();
                    if (rect instanceof EleImg) {
                        EleRect.fixSize(rect, width, Integer.MAX_VALUE);
                    }
                    if (rect.rotate != 0) {
                        rect = new EleRect(rect.xdoc, rect.getAttMap());
                        Point size = rect.viewSize();
                        rect.width = size.x;
                        rect.height = size.y;
                    }
                }
                if (rect.valign.equals(DocConst.ALIGN_AROUND)) {
                    HindRance thr = new HindRance(new Rectangle(lineWidth, top, 
                            rect.width, rect.height));
                    if (this.align.equals(DocConst.ALIGN_CENTER)) {
                    	thr.x = (width - thr.width) / 2;
                    } else if (this.align.equals(DocConst.ALIGN_RIGHT)) {
                    	thr.x = width - thr.width;
                    } else {
                    	thr.x = 0;
                    }
                    rect.left = thr.x;
                    hrList.add(thr);
                } else if (rect.valign.equals(DocConst.ALIGN_FLOAT)) {
                    if (this.align.equals(DocConst.ALIGN_CENTER)) {
                    	rect.left = (width - rect.width) / 2;
                    } else if (this.align.equals(DocConst.ALIGN_RIGHT)) {
                    	rect.left = width - rect.width;
                    } else {
                    	rect.left = 0;
                    }
                }
                if (lineWidth + rect.width > width) {
                    if (lineWidth == 0 && rect.width >= width) { //超长首个元素，放在本行
                        lineEleList.add(ele);
                        lineWidth += rect.width;
                        if (!rect.valign.equals(DocConst.ALIGN_AROUND)
                        		&& !rect.valign.equals(DocConst.ALIGN_FLOAT)
                            		&& rect.height + lineSpacing > lineHeight) {
                            lineHeight = rect.height + lineSpacing;
                        }
                        lineList.add(new DocPageLine(this, lineEleList, lineHeight, lineWidth));
                        lineEleList = new ArrayList();
                        lineWidth = this.indentLeft;
                        lineHeight = this.lineSpacing;
                    } else { //换行
                        lineList.add(new DocPageLine(this, lineEleList, lineHeight, lineWidth));
                    	lineEleList = new ArrayList();
                    	lineEleList.add(ele);
                    	lineWidth = this.indentLeft + rect.width;
                    	lineHeight = this.lineSpacing + rect.height;
                    }
                } else {
                    if (!rect.valign.equals(DocConst.ALIGN_AROUND)
                    		&& !rect.valign.equals(DocConst.ALIGN_FLOAT)
                        		&& rect.height + this.lineSpacing > lineHeight) {
                        lineHeight = rect.height + this.lineSpacing;
                    }
                    lineWidth += rect.width;
                    lineEleList.add(ele);
                }
                eleProcessed = true;
            }
        }
        if (lineEleList.size() > 0) {
            //末尾行
            lineList.add(new DocPageLine(this, lineEleList, lineHeight, lineWidth));
        }
        DocPageLine line, preLine = null;
        EleRect spacePrefix = null;
        for (int i = 0; i < lineList.size(); i++) {
            line = (DocPageLine) lineList.get(i);
            if (prefixRect != null && line.eleList.size() > 0) {
                if (prefix != null && lineList.size() > 0) {
                	if (i == 0 || preLine != null && preLine.enter) {
                		line.eleList.add(0, prefixRect);
                	} else if (!this.isPrefixTab()) {
                		if (i == 1) {
                			spacePrefix = new EleSpace(this.xdoc, prefixRect.width, prefixRect.height);
                		}
                		line.eleList.add(0, spacePrefix);
                	}
                }
            }
            line.align = this.align;
            if (line.align.length() == 0) line.align = DocConst.ALIGN_LEFT;
            line.calWidth();
            if (line.width < this.layWidth) {
	            if (i < lineList.size() - 1 && !line.enter && !(line.eleList.size() == 1 && line.eleList.get(0) instanceof EleRect)) {
            		line.bothDistribute(this.layWidth, prefixRect != null && !this.isPrefixTab() ? 1 : 0);
	            } else {
	            	if (line.align.equals(DocConst.ALIGN_CENTER)) {
	            		line.offset = (this.layWidth - line.width) / 2;
	            		line.offset -= line.offset % 4;
	            	} else if (line.align.equals(DocConst.ALIGN_RIGHT)) {
	            		line.offset = this.layWidth - line.width;
	            	} else if (line.align.equals(DocConst.ALIGN_DISTRIBUTE)) {
	            		line.distribute(this.layWidth, prefixRect != null && !this.isPrefixTab()  ? 1 : 0);
	            	}
	            }
            }
            line.offset += this.indentLeft;
            preLine = line;
        }
        return lineList;
    }
	private ArrayList toLineListV(int top, int width, ArrayList hrList, EleRect prefixRect) {
        int prefixWidth = prefixRect != null ? prefixRect.width : 0;
        ArrayList lineList = new ArrayList();
        Object ele = null;
        //行高度,高度计数器
        int lineWidth = this.indentLeft + prefixWidth, lineHeight = this.lineSpacing;
        if (this.isPrefixTab()) {
        	prefixWidth = 0;
        }
        //行元素列表
        ArrayList lineEleList = new ArrayList();
        String tmpText;
        double cWidth = 0;
        Font font;
        EleText txtEle;
        String href;
        for (int i = 0; i < this.eleList.size(); i++) {
            ele = this.eleList.get(i);
            if (ele instanceof EleText) {
                txtEle = (EleText) ele;
                if (txtEle.href.equals("#")) {
                    href = txtEle.name.length() > 0 ? txtEle.name : txtEle.text;
                } else {
                	href = null;
                }
                Rectangle2D bounds;
                tmpText = txtEle.text;
                font = txtEle.getFont(tmpText);
                for (int j = 0; j < tmpText.length(); j++) {
                    if (tmpText.charAt(j) == '\n') {
                        lineList.add(new DocPageLine(this, lineEleList, lineHeight, lineWidth, true));
                        lineHeight = this.lineSpacing;
                        lineWidth = this.indentLeft + prefixWidth;
                        lineEleList = new ArrayList();
                    } else {
                        bounds = txtEle.getStrBounds(font, String.valueOf(tmpText.charAt(j)), true);
                        if (EleText.isVRotate(tmpText.charAt(j))) { //ascii码
                            cWidth = bounds.getWidth();
                        } else {
                            cWidth = bounds.getHeight();
                        }
                        lineWidth += cWidth;
                        if (lineWidth >= width) { //换行
                            if (lineWidth == width || cWidth >= width - this.indentLeft || isPostSign(tmpText.charAt(j))) {
                                EleText txtEle2 = new EleText(txtEle.xdoc, txtEle.getAttMap());
                                if (href != null) {
                                	txtEle2.name = href;
                                }
                                txtEle2.text = String.valueOf(tmpText.charAt(j));
                                lineEleList.add(txtEle2);
                                if (EleText.isVRotate(tmpText.charAt(j))) { //ascii码
                                    if (bounds.getHeight() + this.lineSpacing > lineHeight) {
                                        lineHeight = (int) bounds.getHeight() + this.lineSpacing;
                                    }
                                } else {
                                    if (bounds.getWidth() + this.lineSpacing > lineHeight) {
                                        lineHeight = (int) bounds.getWidth() + this.lineSpacing;
                                    }
                                }
                                if (j + 1 < tmpText.length() && tmpText.charAt(j + 1) == '\n') {
                                    j++;
                                }
                            } else {
                                j--;
                            }
                            lineList.add(new DocPageLine(this, lineEleList, lineHeight, lineWidth, true));
                            lineHeight = this.lineSpacing;
                            lineWidth = this.indentLeft + prefixWidth;
                            lineEleList = new ArrayList();
                        } else {
                            EleText txtEle2 = new EleText(txtEle.xdoc, txtEle.getAttMap());
                            if (href != null) {
                            	txtEle2.name = href;
                            }
                            txtEle2.text = String.valueOf(tmpText.charAt(j));
                            lineEleList.add(txtEle2);
                            if (EleText.isVRotate(tmpText.charAt(j))) { //ascii码
                                if (bounds.getHeight() + this.lineSpacing > lineHeight) {
                                    lineHeight = (int) bounds.getHeight() + this.lineSpacing;
                                }
                            } else {
                                if (bounds.getWidth() + this.lineSpacing > lineHeight) {
                                    lineHeight = (int) bounds.getWidth() + this.lineSpacing;
                                }
                            }
                        }
                    }
                }
            } else if (ele instanceof EleRect) {
            	EleRect rect;
                rect = (EleRect) ele;
                rect.autoSize();
                if (rect.rotate != 0) {
                	rect = new EleRect(rect.xdoc, rect.getAttMap());
                	Point size = rect.viewSize();
                	rect.width = size.x;
                	rect.height = size.y;
                }
                int rectWidth = ele instanceof EleCharRect ? rect.height : rect.width;
                int rectHeight = ele instanceof EleCharRect ? rect.width : rect.height;
                if (lineWidth + rectWidth > width) {
                    if (rectWidth >= width) {
                        lineEleList.add(ele);
                        lineWidth += rectWidth;
                        if (!rect.valign.equals(DocConst.ALIGN_AROUND)
                        		&& !rect.valign.equals(DocConst.ALIGN_FLOAT)
                        		&& rectWidth + this.lineSpacing > lineHeight) {
                            lineHeight = rectWidth + this.lineSpacing;
                        }
                    }
                    lineList.add(new DocPageLine(this, lineEleList, lineHeight, lineWidth, true));
                    lineEleList = new ArrayList();
                    lineEleList.add(ele);
                    lineWidth = this.indentLeft + prefixWidth + rectWidth;
                    lineHeight = this.lineSpacing + rectHeight;
                    if (rectWidth >= width) {
                        lineList.add(new DocPageLine(this, lineEleList, lineHeight, lineWidth, true));
                        lineEleList = new ArrayList();
                        lineWidth = this.indentLeft;
                        lineHeight = this.lineSpacing;
                    }
                } else {
                    if (!rect.valign.equals(DocConst.ALIGN_AROUND)
                    		&& !rect.valign.equals(DocConst.ALIGN_FLOAT)
                    		&& rectWidth + this.lineSpacing > lineHeight) {
                        lineHeight = rectWidth + this.lineSpacing;
                    }
                    lineWidth += rectWidth;
                    lineEleList.add(ele);
                }
            }
        }
        if (lineEleList.size() > 0) {
            //末尾行
            lineList.add(new DocPageLine(this, lineEleList, lineHeight, lineWidth, true));
        }
        DocPageLine line, preLine = null;
        EleRect spacePrefix = null;
        for (int i = 0; i < lineList.size(); i++) {
            line = (DocPageLine) lineList.get(i);
            if (prefixRect != null && line.eleList.size() > 0) {
                if (prefix != null && lineList.size() > 0) {
                	if (i == 0 || preLine != null && preLine.enter) {
                		line.eleList.add(0, prefixRect);
                	} else if (!this.isPrefixTab()) {
                		if (i == 1) {
                			spacePrefix = new EleSpace(this.xdoc, prefixRect.width, prefixRect.height);
                		}
                		line.eleList.add(0, spacePrefix);
                	}
                }
            }
            line.align = this.align;
            if (line.align.length() == 0) line.align = DocConst.ALIGN_LEFT;
            if (line.width < width) {
            	if (i < lineList.size() - 1 && !line.enter) {
            		line.bothDistribute(width, prefixRect != null && !this.isPrefixTab() ? 1 : 0);
            	} else {
            		if (line.align.equals(DocConst.ALIGN_CENTER)) {
            			line.offset = (width - line.width) / 2;
            			line.offset -= line.offset % 4;
            		} else if (line.align.equals(DocConst.ALIGN_RIGHT)) {
            			line.offset = width - line.width;
            		} else if (line.align.equals(DocConst.ALIGN_DISTRIBUTE)) {
            			line.distribute(width, prefixRect != null && !this.isPrefixTab() ? 1 : 0);
            		}
            	}
            }
            line.offset += this.indentLeft;
            preLine = line;
        }
        return lineList;
    }
    public EleText addText() {
        EleText txt = new EleText(this.xdoc);
        this.eleList.add(txt);
        return txt;
    }
    public void setText(String text) {
    	EleText txt = null;
    	if (this.eleList.size() == 0 || !(this.eleList.get(this.eleList.size() - 1) instanceof EleText)) {
    		txt = new EleText(this.xdoc);
    		this.eleList.add(txt);
    	} else {
    		txt = (EleText) this.eleList.get(this.eleList.size() - 1);
    	}
    	txt.setText(text);
    }
	public static void fixPrefix(EleText text) {
		if (text.text.length() == 1
				&& "●○■□◆◇".indexOf(text.text.charAt(0)) >= 0) {
			text.fontSize = text.fontSize * 4 / 5;
		}
	}
	public static void genHeadInx(ArrayList paras) {
	    int n = 0;
	    String prefix = "";
	    ElePara para;
	    for (int i = 0; i < paras.size(); i++) {
	        para = (ElePara) paras.get(i);
	        if (para.heading == 0) {
	            if (para.isBlank()) {
	            	n = -1;
	            } else if (!prefix.equals(para.prefix)) {
	            	n = 0;
	            }
	            para.headInx = String.valueOf(++n);
	            prefix = para.prefix;
	        } else {
	            n = 0;
	        }
	    }
	}
	/**
	 * 首行缩进
	 * @return
	 */
	public boolean isPrefixTab() {
		return this.prefix.equals(ElePara.PREFIX_TAB);
	}

}
