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
package com.hg.xdoc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.hg.doc.ImgPrint;
import com.hg.doc.PdfPrint;
import com.hg.doc.XDocXml;
import com.hg.util.Version;

/**
 * XDoc
 * @author xdoc
 */
public class XDoc extends Container {
    /**
     * XDoc
     */
    public XDoc() {
        super("xdoc");
        this.setAttribute("version", Version.VERSION);
        this.add(this.meta);
        this.add(this.paper);
        this.add(this.front);
        this.add(this.back);
        this.add(this.body);
    }
    private Meta meta = new Meta();
    private Paper paper = new Paper();
    private Rect front = new Rect("front");
    private Rect back = new Rect("back");
    private Rect body = new Rect("body");
    /**
	 * 获取元数据
	 * @return
	 */
	public Meta getMeta() {
	    return meta;
	}
	/**
	 * 获取纸张
	 * @return
	 */
	public Paper getPaper() {
	    return paper;
	}
	/**
     * 获取背景
     * @return
     */
    public Rect getBack() {
        back.setWidth(this.paper.getWidth());
        back.setHeight(this.paper.getHeight());
        return back;
    }
    /**
	 * 获取前景
	 * @return
	 */
	public Rect getFront() {
	    front.setWidth(this.paper.getWidth());
	    front.setHeight(this.paper.getHeight());
	    return front;
	}
	/**
     * 获取内容
     * @return
     */
    public Rect getBody() {
        return body;
    }
    /**
	 * 转换为W3C Document
	 * @return
	 * @throws Exception 
	 */
	public Document toDocument() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			DocumentBuilder builder = factory.newDocumentBuilder();   
			Document document = builder.newDocument();
			document.appendChild(this.toElement(document));
			return document;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 转换为XML
	 * @throws Exception 
	 */
	public String toXml() {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write(bout);
			return new String(bout.toByteArray(), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 保存
	 * @param out
	 * @throws Exception
	 */
	public void write(OutputStream out) throws IOException {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();   
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(toDocument());   
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");   
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");   
			StreamResult result = new StreamResult(out);   
			transformer.transform(source, result);
			out.flush();
		} catch (TransformerException e1) {
			throw new IOException(e1);
		}   
	}
	/**
	 * 转换
	 * @param file 文件
	 * @throws IOException
	 */
	public void write(File file) throws Exception {
		write(this.toXml(), file);
	}
	/**
	 * 转换
	 * @param out
	 * @param format
	 * @throws IOException
	 */
	public void write(OutputStream out, String format) throws Exception {
		if (format.equals("xdoc")) {
			this.write(out);
		} else {
			write(this.toXml(), out, format);
		}
	}
    /**
	 * 转换为指定文件，文件格式自动识别
	 * @param xml
	 * @param file
	 * @throws IOException
	 */
	public static void write(String xdoc, File file) throws Exception {
		FileOutputStream fout = new FileOutputStream(file);
		write(xdoc, fout, getFormat(file.getName()));
		fout.flush();
		fout.close();
	}
	/**
	 * 以指定格式转换到流中
	 * @param xdoc
	 * @param out
	 * @param format
	 * @throws IOException
	 */
	public static void write(String xdoc, OutputStream out, String format) throws Exception {
		if (format.equals("pdf")) {
			PdfPrint.write(XDocXml.parseXml(xdoc), out);
		} else if (format.equals("png") || format.equals("jpeg")) {
			ImgPrint.write(XDocXml.parseXml(xdoc), out, format);
		} else {
			out.write(xdoc.getBytes("UTF-8"));
		}
	}
	private static String getFormat(String url) {
	    String format = "xdoc";
	    int pos = url.lastIndexOf(".");
	    if (pos > 0) {
	        format = url.substring(pos + 1).toLowerCase();
	    }
	    return format;
	}
}