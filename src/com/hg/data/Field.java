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

import java.sql.Types;


/**
 * 字段
 * @author wanghg
 * @version 1.0
 */
public class Field {
    /**
     * 构造器
     */
    public Field() {
    }
    /**
     * 构造器
     * @param name
     * @param type
     */
    public Field(String name) {
        this(name, DbTypes.VARCHAR);
    }
    /**
     * 构造器
     * @param name
     * @param type
     */
    public Field(String name, int type) {
        this.name = name;
        this.type = type;
    }
    /**
     * 名称
     */
    public String name = "";
    /**
     * 数据类型
     */
    public int type = Types.VARCHAR;
    /**
     * 长度
     */
    public int length = 0;
    /**
     * 小数位数
     */
    public int decimal = 0;
    /**
     * 非空
     */
    public boolean notnull = false;
    public String seq = "";
    public String getShortName() {
        String sName = name;
        if (name.indexOf(".") >= 0) {
            sName = name.substring(name.lastIndexOf(".") + 1);
        }
        return sName;
    }
}
