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

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;

public class DocConst {
    public static Graphics2D g;
    public static FontRenderContext frc;
    static {
        g = (Graphics2D) new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB).getGraphics();
        ImgUtil.setRenderHint(g);
        frc = g.getFontRenderContext();
    }
    public static final String ALIGN_LEFT = "left";
    public static final String ALIGN_RIGHT = "right";
    public static final String ALIGN_CENTER = "center";
    public static final String ALIGN_TOP = "top";
    public static final String ALIGN_BOTTOM = "bottom";
    public static final String ALIGN_AROUND = "around";
    public static final String ALIGN_DISTRIBUTE = "distribute";
    public static final String ALIGN_FLOAT = "float";
    /**
     * 空rect
     */
    public static final String BLANK_RECT = "__blank";
    public static final String BLANK_RECT_PREFIX = "__blank:";
    /**
     * 邮件发送人地址
     */
    public static final String MAIL = "xdoc@xdocserver.com";
    public static final String NO_PARA_BREAK = "$_NO_BREAK_$";
    public static final String NO_VALUE = "$null$";
	public static final String PRINTMARK_PRE = "$[";
	public static final String PRINTMARK_POST = "]";
	public static final String BLANK_TEMPLATE = "{}";
}
