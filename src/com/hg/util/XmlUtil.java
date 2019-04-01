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
package com.hg.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.hg.data.Row;
import com.hg.data.RowSet;
import com.hg.doc.XDocXMLWriter;

/**
 * xml工具类
 * @author whg
 */
public class XmlUtil {
    public static void save(Document doc, String urlStr, String charset) throws HgException {
        XMLWriter xmlwriter;
        try {
            XUrl url = new XUrl(urlStr);
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setTrimText(false);
            if (charset.length() > 0) {
                format.setEncoding(charset);
            } else {
                format.setEncoding("UTF-8");
            }
            Writer writer;
            if (charset.length() > 0) {
                writer = new OutputStreamWriter(url.getOutputStream(), charset);
            } else {
                writer = new OutputStreamWriter(url.getOutputStream());
            }
            xmlwriter = new XMLWriter(writer, format);
            xmlwriter.write(doc);
            xmlwriter.close();
        } catch (Exception e) {
            throw new HgException(e);
        }
    }
    private static Element toEle(RowSet rowSet) throws HgException {
        Row row;
        String name;
        Element ele = DocumentHelper.createElement("node");
        for (int j = 0; j < rowSet.size(); j++) {
            row = rowSet.get(j);
            name = row.get(0).toString();
            if (name.equals("$name")) {
                ele.setName(row.get(1).toString());
            } else if (name.equals("$text")) {
                ele.setText(row.get(1).toString());
            } else {
                ele.addAttribute(name, row.get(1).toString());
            }
        }
        return ele;
    }
    public static String as(RowSet rowSet) throws HgException {
        return toEle(rowSet).asXML();
    }
    public static String fixName(String name) {
        return fixName(name, "_");
    }
    public static String fixName(String name, String repStr) {
        boolean b = false;
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isLetterOrDigit(name.charAt(i))) {
                b = true;
                break;
            }
        }
        if (b) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < name.length(); i++) {
                if (!Character.isLetterOrDigit(name.charAt(i))) {
                    if (repStr.length() > 0) {
                        sb.append(repStr);
                    }
                } else {
                    sb.append(name.charAt(i));
                }
            }
            name = sb.toString();
            if (repStr.length() > 0) {
                while (name.endsWith(repStr)) {
                    name = name.substring(0, name.length() - repStr.length());
                }
                while (name.startsWith(repStr)) {
                    name = name.substring(repStr.length());
                }
            }
        }
        if (name.length() > 0 && Character.isDigit(name.charAt(0))) {
            name = "X" + name;
        }
        if (name.length() > 20) {
            name = name.substring(0, 20);
        }
        return name;
    }
    public static Document parseText(String text) throws DocumentException {
    	Document result = null;
    	SAXReader reader = createSAXReader();
    	String encoding = getEncoding(text);
    	InputSource source = new InputSource(new StringReader(text));
    	source.setEncoding(encoding);
    	result = reader.read(source);
    	if (result.getXMLEncoding() == null) {
    	    result.setXMLEncoding(encoding);
    	}
    	return result;
    }
    private static String getEncoding(String text) {
        String result = null;
        String xml = text.trim();
        if (xml.startsWith("<?xml")) {
            int end = xml.indexOf("?>");
            String sub = xml.substring(0, end);
            StringTokenizer tokens = new StringTokenizer(sub, " =\"\'");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if ("encoding".equals(token)) {
                    if (tokens.hasMoreTokens()) {
                        result = tokens.nextToken();
                    }
                    break;
                }
            }
        }
        return result;
    }
    public static Document fromCsv(String str) {
    	List rows = StrUtil.csvList(str);
    	Document doc = DocumentHelper.createDocument();
    	Element root = doc.addElement("xdata");
    	Element eleRow, eleCell;
    	if (rows.size() > 1) {
    		List cols = (List) rows.get(0);
    		String[] names = new String[cols.size()];
    		for (int i = 0; i < cols.size(); i++) {
    			names[i] = fixName((String) cols.get(i));
    		}
    		for (int i = 1; i < rows.size(); i++) {
    			cols = (List) rows.get(i);
    			eleRow = root.addElement("row");
    			for (int j = 0; j < names.length && j < cols.size(); j++) {
    				eleCell = eleRow.addElement(names[j]);
    				eleCell.setText((String) cols.get(j));
    			}
    		}
    	}
    	return doc;
	}
	public static Class clsXmlReader = null;
    public static SAXReader createSAXReader() {
    	SAXReader reader = null;
    	if (clsXmlReader != null) {
    		try {
				reader = new SAXReader((XMLReader) clsXmlReader.newInstance());
			} catch (Exception e) {
			}
    	}
    	if (reader == null) {
    		reader = new SAXReader();
    	}
        reader.setEntityResolver(NoOpEntityResolver.er);
        return reader;
    }
    public static String attEncode(String str) {
        if (str == null) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            char c;
            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);
                if (c == '\\') {
                    sb.append("\\\\");
                } else if (c == '\n') {
                    sb.append("\\n");
//                } else if (c == '\r') {
//                    sb.append("\\r");
                } else if (c == '\t') {
                    sb.append("\\t");
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
    }
    public static String attDecode(String str) {
        if (str.indexOf('\\') >= 0) {
            StringBuffer sb = new StringBuffer();
            char c;
            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);
                if (c == '\\' && i + 1 < str.length()) {
                    i++;
                    c = str.charAt(i);
                    if (c == 'n') {
                        c = '\n';
                    } else if (c == 'r') {
                        c = '\r';
                    } else if (c == 't') {
                        c = '\t';
                    }
                }
                sb.append(c);
            }
            str = sb.toString();
        }
        return str;
        
    }
    public static void removeAtt(Element ele, String attName) {
    	if (ele.attribute(attName) != null) {
    		ele.remove(ele.attribute(attName));
    	}
    }
    public static void removeAtts(Element ele, String attNames) {
    	String[] atts = attNames.split(",");
    	for (int i = 0; i < atts.length; i++) {
    		removeAtt(ele, atts[i]);
    	}
    }
    public static void trimText(Element ele) {
        Iterator it = ele.nodeIterator();
        Node node;
        List list = new ArrayList();
        while (it.hasNext()) {
            node = (Node) it.next();
            if (node instanceof org.dom4j.Text && node.getText().trim().length() == 0) {
                list.add(node);
            } 
        }
        for (int i = 0; i < list.size(); i++) {
            ele.remove((org.dom4j.Text) list.get(i));
        }
        List eles = ele.elements();
        for (int i = 0; i < eles.size(); i++) {
            trimText((Element) eles.get(i));
        }
    }
    public static String nodeValue(Node node) {
    	if (node != null) {
    		String val = node.getText();
    		return val == null ? "" : val;
    	} else {
    		return "";
    	}
    }
    public static String toXml(Node node) {
    	return toXml(node, false);
    }
    public static String toXml(Node node, boolean pretty) {
    	StringWriter sw = new StringWriter();
    	XMLWriter writer;
    	if (pretty) {
    		writer = new XDocXMLWriter(sw, OutputFormat.createPrettyPrint());
    	} else {
    		writer = new XDocXMLWriter(sw);
    	}
    	try {
			writer.write(node);
			writer.flush();
		} catch (IOException e) {
		}
    	return sw.toString();
    }
	public static Document toDoc(Throwable e) {
	    Document doc = DocumentHelper.createDocument();
	    Element root = doc.addElement("DocumentException");
	    root.addAttribute("class", e.getClass().toString());
	    root.addAttribute("message", e.getMessage());
	    return doc;
	}
	/**
	 * @param document
	 * @param filePath
	 * @throws HgException 
	 */
	public static void save(Document doc, String url) throws HgException {
		save(doc, url, "utf-8");		
	}
}
class NoOpEntityResolver  implements EntityResolver {
    public static NoOpEntityResolver er = new NoOpEntityResolver();
    public InputSource resolveEntity(String publicId, String systemId) {
        return new InputSource(new StringReader(""));
    }
}
