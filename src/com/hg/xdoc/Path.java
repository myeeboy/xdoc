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

/**
 * 路径图形
 * @author xdoc
 */
public class Path extends Rect {
    public static final String xdoc = "M 0 333 Q 61 333 113 250 Q 157 317 217 333 Q 217 367 200 400 L 270 333 Q 383 317 400 233 Q 374 233 374 200 Q 374 167 400 167 Q 391 83 270 67 L 200 0 Q 217 33 217 67 Q 157 83 113 150 Q 61 83 0 67 Q 113 200 0 333 Z M 243 117 Q 296 117 296 200 Q 296 283 243 283 L 243 200 L 243 117 Z M 357 200 Q 357 233 339 233 Q 322 233 322 200 Q 322 167 339 167 Q 357 167 357 200 Z M 217 117 L 217 283 Q 165 283 165 200 Q 165 117 217 117 Z";
    /**
     * 路径
     * @param path 路径
     */
    public Path(String path) {
        super("path");
        this.setPath(path);
    }
    /**
     * 设置图形
     * @param shape 图形
     */
    public void setPath(String shape) {
        this.setAttribute("shape", shape);
    }
}
