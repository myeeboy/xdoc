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

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * XDocXMLWriter
 * 回车、tab等用数字实体表示
 * @author wanghg
 *
 */
public class XDocXMLWriter extends XMLWriter {
	public XDocXMLWriter(OutputStream out, OutputFormat format)
			throws UnsupportedEncodingException {
		super(out, format);
	}

	public XDocXMLWriter(OutputStream out) throws UnsupportedEncodingException {
		super(out);
	}

	public XDocXMLWriter(Writer out, OutputFormat format) {
		super(out, format);
	}

	public XDocXMLWriter(Writer out) {
		super(out);
	}

	private StringBuffer escapeBuffer = new StringBuffer();

	protected String escapeElementEntities(String text) {
		char[] block = null;
		int i;
		int last = 0;
		int size = text.length();
		for (i = 0; i < size; i++) {
			String entity = null;
			char c = text.charAt(i);
			switch (c) {
			case '<':
				entity = "&lt;";
				break;
			case '>':
				entity = "&gt;";
				break;
			case '&':
				entity = "&amp;";
				break;
			case '\n':
				break;
			case '\t':
				break;
			case '\r':
				break;
			default:
				if ((c < 32) || shouldEncodeChar(c)) {
					entity = "&#" + (int) c + ";";
				}
				break;
			}
			if (entity != null) {
				if (block == null) {
					block = text.toCharArray();
				}
				escapeBuffer.append(block, last, i - last);
				escapeBuffer.append(entity);
				last = i + 1;
			}
		}
		if (last == 0) {
			return text;
		}
		if (last < size) {
			if (block == null) {
				block = text.toCharArray();
			}
			escapeBuffer.append(block, last, i - last);
		}
		String answer = escapeBuffer.toString();
		escapeBuffer.setLength(0);
		return answer;
	}

	protected String escapeAttributeEntities(String text) {
		char quote = this.getOutputFormat().getAttributeQuoteCharacter();
		char[] block = null;
		int i;
		int last = 0;
		int size = text.length();
		for (i = 0; i < size; i++) {
			String entity = null;
			char c = text.charAt(i);
			switch (c) {
			case '<':
				entity = "&lt;";
				break;
			case '>':
				entity = "&gt;";
				break;
			case '\'':
				if (quote == '\'') {
					entity = "&apos;";
				}
				break;
			case '\"':
				if (quote == '\"') {
					entity = "&quot;";
				}
				break;
			case '&':
				entity = "&amp;";
				break;
			default:
				if ((c < 32) || shouldEncodeChar(c)) {
					entity = "&#" + (int) c + ";";
				}
				break;
			}
			if (entity != null) {
				if (block == null) {
					block = text.toCharArray();
				}
				escapeBuffer.append(block, last, i - last);
				escapeBuffer.append(entity);
				last = i + 1;
			}
		}
		if (last == 0) {
			return text;
		}
		if (last < size) {
			if (block == null) {
				block = text.toCharArray();
			}
			escapeBuffer.append(block, last, i - last);
		}
		String answer = escapeBuffer.toString();
		escapeBuffer.setLength(0);
		return answer;
	}
}
