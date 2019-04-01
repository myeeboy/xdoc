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
import java.awt.Stroke;

/**
 * 文本线形工具类，隔离对TextStroke的声明引用
 * @author wanghg
 *
 */
public class TextStrokeUtil {
    public static Stroke createStroke( String text, Font font, boolean stretchToFit, boolean repeat ) {
        return new TextStroke(text, font, stretchToFit, repeat);
    }
    public static Stroke createStroke( Shape shape, float advance ) {
        return new TextStroke(shape, advance);
    }
}
