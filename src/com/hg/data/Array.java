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

import java.util.ArrayList;
import java.util.List;

import com.hg.util.To;
/**
 * 记录集
 * @author whg
 */
public class Array {
    /**
     * 类型
     */
    private int type = DbTypes.VARCHAR;
    private List data = new ArrayList();
	/**
     * 构造器
     * @param fields
     */
    public Array(int type) {
        this.type = type;
    }
    /**
     * 构造器
     * @param fields
     */
    public Array() {
        this.type = DbTypes.VARCHAR;
    }
    /**
	 * 返回类型
	 * @return
	 */
	public int getType() {
	    return type;
	}
	public Object getObj(int index) {
		return this.data.get(index);
	}
	/**
	 * 添加记录
	 * @param index
	 * @param row
	 * @throws HgException
	 */
	public Object addObj(Object obj) {
	    return this.addObj(this.size(), obj);
	}
	/**
	 * 添加记录
	 * @param index
	 * @param row
	 * @throws HgException
	 */
	public Object addObj(int index, Object obj) {
		obj = this.validate(obj);
		this.data.add(index, obj);
		return obj;
	}
    /**
	 * 添加
	 * @param tmpSet
	 * @throws HgException
	 */
	public void addAllObj(Array ary) {
	    for (int i = 0; i < ary.size(); i++) {
	        this.addObj(ary.getObj(i));
	    }
	}
	public void setObj(int index, Object obj) {
		this.data.set(index, this.validate(obj));
	}
	/**
     * 校验数据
     * @param obj
     * @return
     */
    private Object validate(Object obj) {
        return To.toObj(obj, this.type);
    }

	public void remove(int index) {
		this.data.remove(index);
	}
	public void clear() {
		this.data.clear();
	}
	public int size() {
		return this.data.size();
	}
}
