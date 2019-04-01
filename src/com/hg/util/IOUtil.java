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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLDecoder;

public class IOUtil {
    private static final int bufferSize = 4096;
    public static void pipe(InputStream in, OutputStream out) throws IOException {
        pipe(in, out, false);
    }
    public static void pipe(InputStream in, OutputStream out, boolean close) throws IOException {
        pipe(in, new OutputStream[] {out}, close);
    }
    public static void pipe(InputStream in, OutputStream[] out) throws IOException {
        pipe(in, out, false);
    }
    public static void pipe(InputStream in, OutputStream[] out, boolean close) throws IOException {
        int len;
        byte[] buf = new byte[bufferSize];
        while (true) {
            len = in.read(buf);
            if (len > 0) {
                for (int i = 0; i < out.length; i++) {
                    if (out[i] != null) {
                        out[i].write(buf, 0, len);
                    }
                }
            } else {
                break;
            }
            /*
            if (in.available() == 0) {
                break;
            }
            */
        }
        for (int i = 0; i < out.length; i++) {
            if (out[i] != null) {
                out[i].flush();
                if (close) {
                    out[i].close();
                }
            }
        }
        if (close) in.close();
    }
    public static String toString(Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        pipe(reader, writer);
        return writer.toString();
    }
    public static void pipe(Reader reader, Writer writer) throws IOException {
        String str;
        BufferedReader bReader = new BufferedReader(reader);
        boolean first = true;
        while ((str = bReader.readLine()) != null) {
            if (!first) {
                writer.write("\r\n");
            }
            writer.write(str);
            first = false;
        }
    }
    public static String toBase64(InputStream in) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	pipe(in, out);
    	return StrUtil.toBase64(out.toByteArray());
    }
	public static InputStream toBIN(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		pipe(in, out, true);
		return new ByteArrayInputStream(out.toByteArray());
	}
	public static byte[] readDataURI(String datauri) throws IOException {
		byte[] bytes = new byte[0];
    	if (datauri.startsWith("data:")) {
    	    int pos = datauri.indexOf(',');
    	    if (pos > 0) {
    	    	String prefix = datauri.substring(5, pos);
    	    	if (prefix.endsWith(";base64")) {
    	    		prefix = prefix.substring(0, prefix.length() - 7);
    	    		bytes = StrUtil.fromBase64(datauri.substring(pos + 1));
    	    	} else {
    	    		String[] strs = prefix.split(";");
    	    		String charset = "utf-8";
    	    		for (int i = 1; i < strs.length; i++) {
    	    			if (strs[i].startsWith("charset=")) {
    	    				charset = strs[i].substring(8);
    	    				break;
    	    			}
    	    		}
    	    		bytes =  URLDecoder.decode(datauri.substring(pos + 1), charset).getBytes("utf-8");
    	    	}
    	    }
    	}
    	return bytes;
	}
}
