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
import java.util.Date;
import java.util.List;

import com.hg.util.HgException;
import com.hg.util.To;
/**
 * 记录集
 * @author whg
 */
public class RowSet {
    /**
     * 别名
     */
    public String alise = "";
    /**
     * 分组统计后行号
     */
    public int rowno;
    /**
     * 字段列表
     */
    private ArrayList fields;
    /**
     * 得到指定index的字段
     * @param index
     * @return
     */
    public Field fieldAt(int index) {
        return (Field) this.fields.get(index);
    }
    /**
     * 列数量
     * @return
     */
    public int fieldSize() {
        return this.fields.size();
    }
    /**
     * 得到指定名称的字段
     * @param index
     * @return
     */
    public int fieldIndex(String fieldName) {
        //匹配查找
        String name;
        for (int i = 0; i < fieldSize(); i++) {
            name = (this.alise.length() > 0 ? this.alise + "." : "") + fieldAt(i).name;
            if (name.equalsIgnoreCase(fieldName)
                    || name.indexOf(".") >= 0 && (name.endsWith('.' + fieldName) || name.toLowerCase().endsWith('.' + fieldName.toLowerCase()))) {
                return i;
            }
        }
        return -1;
    }
    /**
     * 得到指定名称的字段
     * @param index
     * @return
     */
    public Field getField(String fieldName) {
        int n = fieldIndex(fieldName);
        return n >= 0 ? this.fieldAt(n) : null;
    }
    /**
     * 构造器
     * @param fields
     */
    public RowSet() {
        this(new ArrayList());
    }
    /**
     * 构造器
     * @param fields
     */
    public RowSet(ArrayList fields) {
        this.fields = fields;
    }
    /**
	 * 构造器
	 * @param fields
	 * @param fileName
	 * @throws HgException 
	 */
	public RowSet(ArrayList fields, String fileName) {
	    this(fields);
	}
	private List data = new ArrayList();
    /**
     * 缺省记录数据
     * @return
     */
    private Object[] defRowData() {
        Object[] data = new Object[this.fieldSize()];
        Field field;
        for (int i = 0; i < this.fieldSize(); i++) {
            field = this.fieldAt(i);
            if (field.type == DbTypes.BOOLEAN) {
                data[i] = new Boolean(false);
            } else if (field.type == DbTypes.SMALLINT) {
                data[i] = new Short((short) 0);
            } else if (field.type == DbTypes.INTEGER) {
                data[i] = new Integer(0);
            } else if (field.type == DbTypes.BIGINT) {
                data[i] = new Long(0);
            } else if (field.type == DbTypes.FLOAT) {
                data[i] = new Float(0);
            } else if (field.type == DbTypes.DATE) {
                data[i] = new Date();
            } else if (field.type == DbTypes.DOUBLE) {
                data[i] = new Double(0);
            } else if (field.type == DbTypes.INTARRAY) { //特殊支持索引存储
                data[i] = new IntArray();
            } else {
                data[i] = "";
            }
        }
        return data;
    }
    /**
     * 数据转换为记录
     * @param data
     * @return
     */
    private Row dataToRow(Object[] data) {
        return new Row(this, this.fields, data);
    }
    /**
     * 添加记录
     * @param row
     * @throws HgException
     */
    public Row add() {
        return this.add(this.size(), null);
    }
    /**
     * 添加记录
     * @param row
     * @throws HgException
     */
    public Row add(int index) {
        return this.add(index, null);
    }
    /**
     * 添加记录
     * @param row
     * @throws HgException
     */
    public Row add(Row row) {
        return this.add(this.size(), row);
    }
    /**
     * 添加记录
     * @param index
     * @param row
     * @throws HgException
     */
    public Row add(int index, Row row) {
        return dataToRow((Object[]) this.addObj(index, row != null ? row.data : null));
    }
    /**
	 * 添加记录
	 * @param index
	 * @param row
	 * @throws HgException
	 */
	public Row set(int index, Row row) {
	    return dataToRow((Object[]) this.setObj(index, row != null ? row.data : null));
	}
	public void addObj(Object obj) {
		this.addObj(this.size(), obj);
	}
	public Object addObj(int index, Object obj) {
    	obj = this.validate(obj);
		this.data.add(index, obj);
		return obj;
	}
	private Object setObj(int index, Object obj) {
    	obj = this.validate(obj);
    	this.data.set(index, obj);
    	return obj;
	}
	/**
     * 添加
     * @param tmpSet
     * @throws HgException
     */
    public void addAll(RowSet tmpSet) {
        for (int i = 0; i < tmpSet.size(); i++) {
            this.addObj(tmpSet.getObj(i));
        }
    }
    public Object getObj(int index) {
		return this.data.get(index);
	}
	/**
     * 取得记录
     * @param rowIndex
     * @return
     * @throws HgException
     */
    public Row get(int rowIndex) {
        return dataToRow((Object[]) this.getObj(rowIndex));
    }
    /**
     * 取得记录
     * @param rowIndex
     * @return
     * @throws HgException
     */
    public Object[] getData(int rowIndex) {
        return (Object[]) this.getObj(rowIndex);
    }
    /**
     * 取单元格值
     * @param rowIndex
     * @param colIndex
     * @return
     * @throws HgException
     */
    public Object getCellValue(int rowIndex, int colIndex) {
        return ((Object[]) this.getObj(rowIndex))[colIndex];
    }
    /**
     * 修改单元格值
     * @param rowIndex
     * @param colIndex
     * @param obj
     * @throws HgException
     */
    public void setCellValue(int rowIndex, int colIndex, Object obj) {
        ((Object[]) this.getObj(rowIndex))[colIndex] = To.toObj(obj, fieldAt(colIndex).type);
    }
    /**
     * 校验数据
     * @param obj
     * @return
     */
    private Object validate(Object obj) {
        Object[] data = defRowData();
        if (obj != null && obj instanceof Object[]) {
            Object[] data2 = (Object[]) obj;
            for (int i = 0; i < data.length && i < data2.length; i++) {
                data[i] = To.toObj(data2[i], fieldAt(i).type);
            }
        }
        return data;
    }
    public int size() {
		return this.data.size();
	}
	/**
     * 创建基于当前记录集的记录
     * @return
     */
    public Row createRow() {
        return this.dataToRow(this.defRowData());
    }
    /**
     * 创建基于当前记录集的记录集
     * @return
     */
    public RowSet createRowSet() {
        RowSet rowSet = new RowSet(this.fields);
        rowSet.alise = this.alise;
        return rowSet;
    }
    public ArrayList toList() {
        ArrayList list = new ArrayList();
        for (int i = 0; i < this.size(); i++) {
            list.add(this.get(i).toMap());
        }
        return list;
    }
    public String toString() {
        return "ROWSET";
    }
	public void remove(int index) {
		this.data.remove(index);
	}
	public void clear() {
		this.data.clear();
	}
    protected void setDataChange() {
    }
	public void writeData() {}
	public void dropFully() {}
}
