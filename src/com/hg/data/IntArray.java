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
package com.hg.data;

/**
 * 记录集
 * @author whg
 */
public class IntArray extends Array {
    public IntArray() {
        super(DbTypes.INTEGER);
    }
    public void add(int n) {
        super.addObj(new Integer(n));
    }
    public void set(int index, int n) {
        super.setObj(index, new Integer(n));
    }
    public int get(int index) {
        return ((Integer) super.getObj(index)).intValue();
    }
    public void addAll(IntArray ary) {
        super.addAllObj(ary);
    }
}
