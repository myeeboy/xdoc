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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.hg.util.ColorUtil;
import com.hg.util.DateUtil;
import com.hg.util.HgException;
import com.hg.util.MapUtil;
import com.hg.util.StrUtil;
import com.hg.util.To;
import com.hg.util.XUrl;
import com.hg.util.XmlUtil;

/**
 * xdoc与xml的转换
 * @author wanghg
 *
 */
public class XDocXml {
    /**
     * 读取文档
     * @param src
     * @return
     * @throws HgException 
     */
    public static XDoc read(String urlStr) throws HgException {
        try {
            XDoc xdoc = read((new XUrl(urlStr)).getInputStream());
            xdoc.url = urlStr;
            return xdoc;
        } catch (Exception e) {
            throw new HgException(e);
        }
    }
    /**
     * 读取文档
     * @param src
     * @return
     * @throws HgException 
     */
    public static XDoc parseXmlDoc(Element root) throws HgException {
        XDoc xdoc = new XDoc();
        HashMap attMap;
        Element ele;
        EleBase docEle;
        if (root.getName().equals("xdoc")) {
            Iterator it = root.elementIterator();
            String name;
            Map styles = null;
            while (it.hasNext()) {
                ele = (Element) it.next();
                name = ele.getName();
                if (name.equals("body")) {
                    //处理内容
                    attMap = attMap(ele, styles);
                    docEle = toDocEle(xdoc, ele, styles);
                    EleBase cDocEle;
                    for (int i = 0; i < docEle.eleList.size(); i++) {
                        cDocEle = (EleBase) docEle.eleList.get(i);
                        if (cDocEle instanceof ElePara) {
                            if (cDocEle.eleList.size() == 0) {
                                cDocEle.eleList.add(new EleText(xdoc));
                            }
                            xdoc.paraList.add(cDocEle);
                        } else if (cDocEle instanceof EleText) {
                            ElePara elePara = new ElePara(xdoc);
                            elePara.eleList.add(cDocEle);
                            xdoc.paraList.add(elePara);
                        } else {
                            xdoc.rectList.add(cDocEle);
                        }
                    }
                    xdoc.bodyRect = (EleRect) docEle;
                    xdoc.bodyRect.eleList.clear();
                } else if (name.equals("back")) {
                    //处理背景
                    xdoc.backRect  = (EleRect) toDocEle(xdoc, ele, styles);
                    xdoc.backRect.width = xdoc.getPaper().width;
                    xdoc.backRect.height = xdoc.getPaper().height;
                } else if (name.equals("front")) {
                    xdoc.frontRect  = (EleRect) toDocEle(xdoc, ele, styles);
                    xdoc.frontRect.width = xdoc.getPaper().width;
                    xdoc.frontRect.height = xdoc.getPaper().height;
                } else if (name.equals("meta")) {
                    //元数据
                    xdoc.metaMap.putAll(attMap(ele, styles));
                    xdoc.metaMap.remove(EleBase.ELEMENT_TEXT);
                    if ("slide".equals(xdoc.getMeta("view"))) {
                    	xdoc.metaMap.put("view", XDoc.VIEW_PAGE);
                    }
                    xdoc.scale = To.toDouble(xdoc.getMeta("scale", "1"), 1);
                } else if (name.equals("paper")) {
                    //处理页面设置
                    DocPaper docPaper = xdoc.getPaper();
                    attMap = attMap(ele, styles);
                    docPaper.setMargin(MapUtil.getInt(attMap, "margin", DocPaper.DEFAULT_MARGIN));
                    if (attMap.containsKey("size")) {
                		DocPaper sizePaper = new DocPaper(xdoc, MapUtil.getString(attMap, "size", ""));
                		docPaper.width = sizePaper.width;
                		docPaper.height = sizePaper.height;
                    	docPaper.setMargin(MapUtil.getInt(attMap, "margin", docPaper.getTopMargin()));
                    }
                	docPaper.width = MapUtil.getInt(attMap, "width", docPaper.width);
                	docPaper.height = MapUtil.getInt(attMap, "height", docPaper.height);
                    docPaper.setTopMargin(MapUtil.getInt(attMap, "topMargin", docPaper.getTopMargin()));
                    docPaper.setLeftMargin(MapUtil.getInt(attMap, "leftMargin", docPaper.getLeftMargin()));
                    docPaper.setRightMargin(MapUtil.getInt(attMap, "rightMargin", docPaper.getRightMargin()));
                    docPaper.setBottomMargin(MapUtil.getInt(attMap, "bottomMargin", docPaper.getBottomMargin()));
                } else if (name.equals("styles")) {
                	styles = new HashMap();
                    //样式
                    Map map;
                    String[] styleName;
                    for (Iterator its = ele.elementIterator(); its.hasNext();) {
                        ele = (Element) its.next();
                        map = attMap(ele, styles);
                        styleName = MapUtil.getString(map, "name", "").split(",");
                        for (int i = 0; i < styleName.length; i++) {
                        	if (styleName[i].length() > 0) {
                        		styles.put(styleName[i], map);
                        		map.remove("name");
                        	}
                        }
                    }
                }
            }
        } else {
            if (!root.getName().equals("response")) {//忽略response元素
                docEle = toDocEle(xdoc, root, null);
                if (docEle instanceof EleRect) {
                    if (DocUtil.isBlank((EleRect) docEle)) {
                        xdoc.paraList.addAll(((EleRect) docEle).getParaList());
                        xdoc.rectList.addAll(((EleRect) docEle).getRectList());
                    } else {
                        xdoc.rectList.add((EleRect) docEle);
                    }
                    xdoc.getPaper().setMargin(0);
                    xdoc.bodyRect.sizeType = EleRect.SIZE_AUTOSIZE;
                } else {
                    if (docEle instanceof EleText) {
                        ElePara para = new ElePara(xdoc);
                        para.eleList.add(docEle);
                        docEle = para;
                    }
                    if (docEle instanceof ElePara) {
                        xdoc.paraList.add(docEle);
                    }
                }
            } else if ("false".equals(root.attributeValue("success"))) {
            	String msg = "";
            	String cause = "";
            	if (root.element("error") != null) {
            		msg = root.element("error").getText();
            	}
            	if (root.element("cause") != null) {
            		cause = root.element("cause").getText();
            	}
                throw new HgException(msg, new Exception(cause));
            }
        }
        return xdoc;
    }
	public static XDoc parseXml(String xml) throws HgException {
        Document doc = null;
        try {
            doc = XmlUtil.parseText(xml);
        } catch (DocumentException e) {
            throw new HgException(e);
        }
        return parseXmlDoc(doc.getRootElement());
    }

    public static XDoc read(InputStream in) throws HgException {
        try {
            return parseXmlDoc(XmlUtil.createSAXReader().read(in).getRootElement());
        } catch (Exception e) {
            throw new HgException(e);
        }
    }

    public static XDoc read(Reader reader) throws HgException {
        try {
            return parseXmlDoc(XmlUtil.createSAXReader().read(reader).getRootElement());
        } catch (Exception e) {
            throw new HgException(e);
        }
    }

    public static String toXml(XDoc xdoc) {
    	return toXml(xdoc, false);
    }

    public static String toXml(XDoc xdoc, boolean pretty) {
        return XmlUtil.toXml(toXmlDoc(xdoc), pretty);
    }

    /**
     * 写入文档
     * @return
     * @throws HgException 
     */
    public static void write(XDoc xdoc) throws HgException {
        write(xdoc, xdoc.url);
    }

    public static void write(XDoc xdoc, String urlStr) throws HgException {
        OutputStream out = getOutputStream(urlStr);
        try {
            write(xdoc, out);
        } catch (HgException e) {
            throw e;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

    public static void write(XDoc xdoc, OutputStream out) throws HgException {
        try {
        	XMLWriter writer;
        	if (DocUtil.prettyFormat) {
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setTrimText(false);
        		writer = new XDocXMLWriter(out, format);
        	} else {
        		writer = new XDocXMLWriter(out);
        	}
            writer.write(toXmlDoc(xdoc));
            writer.flush();
            out.flush();
        } catch (Exception e) {
            throw new HgException(e);
        }
    }
    public static void write(XDoc xdoc, Writer writer) throws HgException {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setTrimText(false);
            XMLWriter xmlwriter = new XDocXMLWriter(writer, format);
            xmlwriter.write(toXmlDoc(xdoc));
            xmlwriter.flush();
            writer.flush();
        } catch (Exception e) {
            throw new HgException(e);
        }
    }

    public static void writeZip(XDoc doc, OutputStream out) throws HgException {
        try {
            ZipOutputStream zos = new ZipOutputStream(out);
            zos.putNextEntry(new ZipEntry("xdoc.xdoc"));
            write(doc, zos);
            zos.close();
        } catch (Exception e) {
            throw new HgException(e);
        }
    }
    public static String toZipDataURI(XDoc xdoc) throws HgException {
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
    	writeZip(xdoc, bout);
		return "data:application/zip;base64," + StrUtil.toBase64(bout.toByteArray());
    }
    public static Document toXmlDoc(XDoc xdoc) {
        Document doc = DocumentHelper.createDocument();
        toXmlDoc(xdoc, doc);
        return doc;
    }

    private static void toXmlDoc(XDoc xdoc, Branch doc) {
        Element root = doc.addElement("xdoc");
        if (root.isRootElement()) {
            root.addAttribute("version", com.hg.util.Version.VERSION);
        }
        Element ele;
        String key;
        Element body;
        ele = root.addElement("meta");
        if (xdoc.metaMap.containsKey("createDate")) {
            xdoc.metaMap.put("modifyDate", DateUtil.getDateTimeString());
        } else {
            xdoc.metaMap.put("createDate", DateUtil.getDateTimeString());
        }
        Iterator it = xdoc.metaMap.keySet().iterator();
        while (it.hasNext()) {
            key = (String) it.next();
            if (key.equals(EleBase.ELEMENT_TEXT) 
                    || ((String) xdoc.metaMap.get(key)).length() == 0 
                    || key.equals("scale") && To.toDouble(xdoc.getMeta(key)) == 1) {
                continue;
            }
            addAtt(ele, key, (String) xdoc.metaMap.get(key));
        }
        Element paper = xdoc.getPaper().toEle();
        if (paper != null) {
        	root.add(paper);
        }
        //处理前景背景
        ele = toBFBEle(root, xdoc.frontRect, "front");
        ele = toBFBEle(root, xdoc.backRect, "back");
        body = toBFBEle(root, xdoc.bodyRect, "body");
        //处理内容
        for (int i = 0; i < xdoc.paraList.size(); i++) {
            body.add(toXmlEle((EleBase) xdoc.paraList.get(i)));
        }
        for (int i = 0; i < xdoc.rectList.size(); i++) {
            body.add(toXmlEle((EleBase) xdoc.rectList.get(i)));
        }
    }
	private static Element toBFBEle(Element root, EleRect rect, String name) {
		Element bfb;
		bfb = toXmlEle(rect);
        bfb.setName(name);
        XmlUtil.removeAtts(bfb, "name,top,left,width,height,color");
        if (rect.color != null) {
        	bfb.addAttribute("color", ColorUtil.colorToStr(rect.color));
        }
        if (name.equals("body")) {
        	if (rect.txtPadding == 0) {
        		XmlUtil.removeAtt(bfb, "padding");
        	}
        	if (rect.strokeWidth == 0) {
        		XmlUtil.removeAtt(bfb, "strokeWidth");
        	}
        }
        if (name.equals("body") || bfb.attributeCount() > 0 || bfb.elements().size() > 0) {
            root.add(bfb);
        }
		return bfb;
	}
    /**
     * 文档元素转为xml元素
     * @param docEle
     * @return
     */
    public static Element toXmlEle(EleBase docEle) {
    	if (docEle instanceof EleCharRect) {
    		docEle = ((EleCharRect) docEle).eleChar;
    	}
        Element ele = DocumentHelper.createElement(docEle.typeName);
        HashMap attMap = docEle.getAttMap();
        HashMap stdMap;
        if (docEle instanceof EleCell) {
            docEle = ((EleCell) docEle).getRect();
            attMap.remove("left");
            attMap.remove("top");
            attMap.remove("width");
            attMap.remove("height");
        } else {
            attMap.remove("row");
            attMap.remove("col");
        }
        stdMap = XDocXml.getStdAtts(docEle);
        String key;
        Iterator it = attMap.keySet().iterator();
        while (it.hasNext()) {
        	key = (String) it.next();
        	if (attMap.get(key) != null && !attMap.get(key).equals(stdMap.get(key))) {
                if (attMap.get(key) instanceof String) {
                    addAtt(ele, key, (String) attMap.get(key));
                }
        	}
        }
        if (ele.getName().equals("ext") && ele.attributeValue("type", "").length() > 0) {
        	ele.setName(ele.attributeValue("type", ""));
        	ele.remove(ele.attribute("type"));
        }
        if (docEle instanceof EleTable) {
            EleCell cell;
            for (int i = 0; i < docEle.eleList.size(); i++) {
                if (docEle.eleList.get(i) instanceof EleCell) {
                    cell = (EleCell) docEle.eleList.get(i);
                    if (cell.belong == null) {
                        ele.add(toXmlEle((EleBase) docEle.eleList.get(i)));
                    }
                }
            }
        } else {
        	int paraSize = 0;
        	Element pe = null;
        	Element cele;
            for (int i = 0; i < docEle.eleList.size(); i++) {
            	cele = toXmlEle((EleBase) docEle.eleList.get(i));
            	if (cele.getName().equals("para")) {
            		paraSize++;
            		pe = cele;
            	}
                ele.add(cele);
            }
            if (paraSize == 1 && pe.nodeCount() == 0) {
            	//清除单个空段落
            	ele.remove(pe);
            }
        }
        return ele;
    }
    /**
     * 文档元素转为xml元素
     * @param decode 
     * @param docEle
     * @return
     */
    public static EleBase toDocEle(XDoc doc, Element ele) {
    	return toDocEle(doc, ele, null);
    }
    /**
     * 文档元素转为xml元素
     * @param decode 
     * @param docEle
     * @return
     */
    private static EleBase toDocEle(XDoc doc, Element ele, Map styles) {
        HashMap attMap = attMap(ele, styles);
        String eleName = ele.getName();
        EleBase docEle = null;
        if (eleName.equals("para")) {
            docEle = new ElePara(doc, attMap);
        } else if (eleName.equals("pline")) {
        	docEle = new EleParaLine(doc, attMap);
        } else if (eleName.equals("text")) {
            return new EleText(doc, attMap);
        } else if (eleName.equals("img")) {
        	if (EleRect.SIZE_NORMAL.equals(attMap.get("sizeType"))) {
        		if ("0".equals(attMap.get("width")) && "0".equals(attMap.get("height"))) {
        			attMap.put("sizeType", EleRect.SIZE_AUTOSIZE);
        		} else if ("0".equals(attMap.get("width"))) {
        			attMap.put("sizeType", EleRect.SIZE_AUTOWIDTH);
        		} else if ("0".equals(attMap.get("height"))) {
        			attMap.put("sizeType", EleRect.SIZE_AUTOHEIGHT);
        		}
        	}
        	docEle = new EleImg(doc, attMap);
        } else if (eleName.equals("line")) {
            return new EleLine(doc, attMap);
        } else if (eleName.equals("arc")) {
            docEle = new EleArc(doc, attMap);
        } else if (eleName.equals("polygon")) {
            docEle = new ElePolygon(doc, attMap);
        } else if (eleName.equals("path")) {
            docEle = new ElePath(doc, attMap);
        } else if (eleName.equals("stext")) {
            docEle = new EleSText(doc, attMap);
        } else if (eleName.equals("table")) {
            docEle = new EleTable(doc, attMap);
        } else if (eleName.equals("cell")) {
            docEle = new EleCell(doc, attMap);
        } else if (eleName.equals("group")) {
        	docEle = new EleGroup(doc, attMap);
        } else if (eleName.equals("space")) {
            docEle = new EleSpace(doc, attMap);
        } else if (eleName.equals("rect") || eleName.equals("body") 
        		|| eleName.equals("back") || eleName.equals("front")) {
            docEle = new EleRect(doc, attMap);
            if (eleName.equals("back") || eleName.equals("front") || eleName.equals("body")) {
                if (!attMap.containsKey("color")) {
                    ((EleRect) docEle).color = null;
                }
            }
            if (eleName.equals("body")) {
            	if (!attMap.containsKey("padding")) {
            		((EleRect) docEle).txtPadding = 0;
            	}
            	if (!attMap.containsKey("strokeWidth")) {
            		((EleRect) docEle).strokeWidth = 0;
            	}
                ((EleRect) docEle).name = DocConst.BLANK_RECT;
            }
        } else if (eleName.equals("char")) {
            docEle = new EleCharRect(doc, attMap);
        } else {
            docEle = new EleRect(doc, attMap);
        }
        Element cEle;
        EleBase cDocEle;
        for (Iterator i = ele.elementIterator(); i.hasNext(); ) {
            cEle = (Element) i.next();
            cDocEle = toDocEle(doc, cEle, styles);
            if (docEle instanceof EleTable) {
            	if (cDocEle instanceof EleRect) {
            		if (cDocEle instanceof EleCell) {
            			docEle.eleList.add(cDocEle);
            		} else {
            			docEle.eleList.add(new EleCell((EleRect) cDocEle));
            		}
            	}
            } else if (cDocEle instanceof EleText && !(docEle instanceof ElePara)) {
            	ElePara elePara = new ElePara(doc);
            	elePara.eleList.add(cDocEle);
            	docEle.eleList.add(elePara);
            } else if (docEle instanceof ElePara && cDocEle instanceof ElePara) {
            	docEle.eleList.addAll(cDocEle.eleList);
            } else {
            	docEle.eleList.add(cDocEle);
            }
        }
        if (eleName.equals("table")) {
            EleTable tab = (EleTable) docEle;
            if (!attMap.containsKey("width")) {
                int n = 0;
                if (tab.cols.length() > 0) {
                    String[] cols = tab.cols.split(",");
                    for (int i = 0; i < cols.length; i++) {
                        n += To.toInt(cols[i], EleRect.DEF_WIDTH);
                    }
                } else {
                    n = 0;
                    for (int i = 0; i < tab.eleList.size(); i++) {
                        if (((EleCell) tab.eleList.get(i)).col > n) {
                            n = ((EleCell) tab.eleList.get(i)).col;   
                        }
                    }
                    n = (n + 1) * EleRect.DEF_WIDTH;
                }
                tab.width = n;
            }
            if (!attMap.containsKey("height")) {
                int n = 0;
                if (tab.rows.length() > 0) {
                    String[] rows = tab.rows.split(",");
                    for (int i = 0; i < rows.length; i++) {
                        n += To.toInt(rows[i], EleRect.DEF_HEIGHT);
                    }
                } else {
                    n = 0;
                    for (int i = 0; i < tab.eleList.size(); i++) {
                        if (((EleCell) tab.eleList.get(i)).row > n) {
                            n = ((EleCell) tab.eleList.get(i)).row;   
                        }
                    }
                    n = (n + 1) * EleRect.DEF_HEIGHT;
                }
                tab.height = n;
            }
        }
        if (docEle.eleList.size() == 0
        		&& attMap.containsKey(EleBase.ELEMENT_TEXT)
        		&& !(docEle instanceof EleText)) {
        	String str = (String) attMap.get(EleBase.ELEMENT_TEXT);
        	if (str.length() > 0) {
        		docEle.setText(str);
        	}
        }
        return docEle;
    }
    private static boolean isInvalidChar(char c) {
        return c > 0xFFFD || c < 0x20 && c != '\n' && c != '\r' && c != '\t';
    }
    private static void addAtt(Element ele, String name, String value) {
        if (name.equals(EleBase.ELEMENT_TEXT)) {
            boolean valid = true;
            for (int i = 0; i < value.length(); i++) {
                if (isInvalidChar(value.charAt(i))) {
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                char c;
                //处理无效字符
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < value.length(); i++) {
                    c = value.charAt(i);
                    if (isInvalidChar(c)) {
                        c = ' ';
                    }
                    sb.append(c);
                }
                value = sb.toString();
            }
            ele.setText(value);
    	} else {
    		ele.addAttribute(name, value);
    	}
    }

    private static HashMap attMap(Element ele, Map styles) {
        HashMap attMap = new HashMap();
        if (styles != null && styles.containsKey(ele.getName())) {
        	attMap.putAll((Map) styles.get(ele.getName()));
        }
        Attribute att;
        for (Iterator i = ele.attributeIterator(); i.hasNext(); ) {
            att = (Attribute) i.next();
            attMap.put(att.getName(), att.getValue());
        }
        if (ele.elements().size() == 0) {
            attMap.put(EleBase.ELEMENT_TEXT, ele.getText());
        }
        if (attMap.containsKey("style")) {
        	String[] items = MapUtil.getString(attMap, "style", "").split(";");
        	int pos;
        	for (int i = 0; i < items.length; i++) {
        		pos = items[i].indexOf(":");
        		if (pos > 0) {
        			attMap.put(items[i].substring(0, pos), items[i].substring(pos + 1));
        		} else if (styles != null && styles.containsKey(items[i])) {
        			attMap.putAll((Map) styles.get(items[i]));
        		}
        	}
        	attMap.remove("style");
        }
        return attMap;
    }

    private static OutputStream getOutputStream(String urlStr) throws HgException {
        return (new XUrl(urlStr)).getOutputStream();
    }
    private static HashMap stdAttsMap = new HashMap();
    public static HashMap getStdAtts(EleBase docEle) {
        HashMap map = (HashMap) stdAttsMap.get(docEle.getClass());
        if (map == null) {
            map = new HashMap();
            map.put("name", "");
            if (docEle instanceof EleRect) {
            	map.put("color", "#000000");
            	map.put("strokeWidth", "1.0");
            	map.put("strokeStyle", "0");
            	map.put("strokeImg", "");
            	map.put("align", DocConst.ALIGN_TOP);
            	map.put("zPosition", DocConst.ALIGN_TOP);
            	map.put("valign", DocConst.ALIGN_BOTTOM);
            	map.put("visible", "true");
            	map.put("dock", "");
                map.put("left", "0");
                map.put("top", "0");
                map.put("sizeType", EleRect.SIZE_NORMAL);
                map.put("fillColor", "");
                map.put("fillColor2", "");
                map.put("fillImg", "");
                map.put("line", (docEle instanceof EleShape) ? EleRect.LINE_NONE : EleRect.LINE_RECT);
                map.put("gradual", "");
                map.put("arc", "0");
                map.put("href", "");
                map.put("filter", "");
                map.put("filterTarget", "all");
                map.put("filterParam", "");
                map.put("scale", "");
                map.put("column", "1");
                map.put("rotate", "0");
                map.put("srotate", "0");
                map.put("distort", "");
                map.put("padding", "2");
                map.put("margin", "0");
                map.put("toolTip", "");
                map.put("layoutFlow", "h");
                map.put("layoutLine", "false");
                map.put("fillRatio", "");
                map.put("colSpan", "1");
                map.put("rowSpan", "1");
                map.put("comment", "");
                if (docEle instanceof EleSText) {
                	map.put("width", "200");
                	map.put("height", "100");
                } else if (docEle instanceof EleShape) {
                    map.put("width", "120");
                    map.put("height", "120");
                } else if (docEle instanceof EleImg) {
                    map.put("width", "0");
                    map.put("height", "0");
                } else {
                    map.put("width", String.valueOf(EleRect.DEF_WIDTH));
                    map.put("height", String.valueOf(EleRect.DEF_HEIGHT));
                }
                if (docEle instanceof EleArc) {
                	map.put("type", "open");
                    map.put("start", "0");
                    map.put("extent", "360");
                } else if (docEle instanceof EleSText) {
                    map.put("fontName", XFont.defaultFontName);
                    map.put("text", "");
                    map.put("italic", "false");
                    map.put("bold", "false");
                    map.put("spacing", "0");
                    map.put("format", "");
                } else if (docEle instanceof EleImg) {
                    map.put("src", "");
                    map.put("drawType", "zoom");
                    map.put("shape", "");
                    map.put("color", "");
                    map.put("fillColor", "#ffffff");
                } else if (docEle instanceof EleCell) {
                    map.put("conn", "");
                    map.put("sql", "");
                    map.put("data", "");
                    map.put("rowset", "");
                    map.put("rowCount", "0");
                    map.put("direction", "v");
                } else if (docEle instanceof EleLine) {
                    map.put("startX", "0");
                    map.put("startY", "0");
                    map.put("endX", "20");
                    map.put("endY", "20");
                    map.put("startArrow", "");
                    map.put("endArrow", "");
                    map.put("lineStyle", "line");
                } else if (docEle instanceof EleGroup) {
                    map.put("text", "");
                    map.put("color", "");
                    map.put("drawType", "zoom");
                } else if (docEle instanceof ElePath) {
                    map.put("shape", "");
                    map.put("drawType", "zoom");
                    map.put("repeat", "1");
                } else if (docEle instanceof EleTable) {
                    map.put("color", "");
                    map.put("width", "0");
                    map.put("height", "0");
                    map.put("input", "false");
                    map.put("header", "0");
                    map.put("footer", "0");
                    map.put("data", "");
                    map.put("tarType", "table");
                    map.put("rowCount", "0");
                    map.put("sizeType", EleRect.SIZE_AUTOSIZE);
                }
            } else if (docEle instanceof ElePara) {
                map.put("lineSpacing", "0");
                map.put("indent", "0");
                map.put("heading", "0");
                map.put("align", DocConst.ALIGN_LEFT);
                map.put("prefix", "");
                map.put("breakPage", "false");
                map.put("backColor", "");
                map.put("backImg", "");
                if (docEle instanceof EleParaLine) {
                    map.put("width", "0");
                    map.put("height", String.valueOf(XFont.defaultFontSize));
                    map.put("offset", "0");
                    map.put("vertical", "false");
                }
            } else if (docEle instanceof EleText) {
                map.put("fontColor", "#000000");
                map.put("backColor", "");
                map.put("backImg", "");
                map.put(EleText.ELEMENT_TEXT, "");
                map.put("fontName", XFont.defaultFontName);
                map.put("fontSize", String.valueOf(XFont.defaultFontSize));
                map.put("valign", DocConst.ALIGN_BOTTOM);
                map.put("href", "");
                map.put("toolTip", "");
                map.put("fontStyle", "");
                map.put("format", "");
                map.put("spacing", "0");
                if (docEle instanceof EleChar) {
                	map.put("img", "");
                	map.put("shape", "");
                }
            }
        	stdAttsMap.put(docEle.getClass(), map);
        }
    	return (HashMap) map.clone();
    }
	public static Map attMap(Element ele) {
		return attMap(ele, null);
	}
	public static String toXml(EleBase ele) {
		return XmlUtil.toXml(toXmlEle(ele));
	}
}
