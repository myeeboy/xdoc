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
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 图片
 * @author xdoc
 */
public class Img extends Rect {
    /**
     * 图片
     * @param data 图片二进制数据
     */
    public Img(byte[] data) {
        this(toBase64(data));
    }
    /**
     * 图片
     * @param file 文件
     * @throws IOException 
     */
    public Img(File file) throws IOException {
    	this("");
    	FileInputStream in = new FileInputStream(file);
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        byte[] buf = new byte[4096];
        while (true) {
            len = in.read(buf);
            if (len > 0) {
                out.write(buf, 0, len);
            } else {
                break;
            }
        }
        in.close();
        out.flush();
        this.setSrc(toBase64(out.toByteArray()));
    }
    /**
     * 图片
     * @param src
     */
    public Img(String src) {
        super("img");
        this.setSrc(src);
        this.setSizeType(SIZE_TYPE_AUTOSIZE);
    }
    /**
     * 设置图片地址，支持datauri
     * @param src
     */
    public void setSrc(String src) {
        this.setAttribute("src", src);
    }
    public static String DRAW_TYPE_ZOOM = "zoom";
    public static String DRAW_TYPE_REPEAT = "repeat";
    public static String DRAW_TYPE_CENTER = "center";
    public static String DRAW_TYPE_FACT = "fact";
    public static String DRAW_TYPE_ADJUST = "adjust";
    public static String DRAW_TYPE_9GRID = "9grid";
    /**
     * 设置绘图类型
     * @param type
     */
    public void setDrawType(String type) {
        this.setAttribute("drawType", type);
    }
    private static String toBase64(final byte[] data) {
    	char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
        final char[] out = new char[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;

            int val = (0xFF & data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        return new String(out);
    }
}
