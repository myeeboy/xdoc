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

import java.awt.Shape;

public class NameShape {
    public Shape shape;
    public int page;
    public String name;
    public NameShape(String name, Shape shape) {
        this(name, shape, 0);
    }
    public NameShape(String name, Shape shape, int page) {
        this.shape = shape;
        this.page = page;
        this.name = name;
    }
}
