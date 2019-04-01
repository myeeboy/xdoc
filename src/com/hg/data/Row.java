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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hg.util.HgException;
import com.hg.util.To;

/**
 * 记录
 * @author wanghg
 * @version 1.0
 */
public class Row {
    /**
     * 所属记录集
     */
    private RowSet rowSet;
    /**
     * 字段列表
     */
    private ArrayList fields;
    /**
     * 记录号
     */
    public int rowno;
    /**
     * 得到指定index的字段
     * @param index
     * @return
     */
    public Field fieldAt(int index) {
        return (Field) this.fields.get(index);
    }
    /**
     * 得到指定index的字段完整名称，带alise
     * @param index
     * @return
     */
    public String fullFieldNameAt(int index) {
        String name = fieldAt(index).name;
        if (this.rowSet != null && this.rowSet.alise.length() > 0) {
            name = this.rowSet.alise + "." + name;
        } 
        return name;
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
        if (this.rowSet != null) {
            return this.rowSet.fieldIndex(fieldName);
        } else {
            String name;
            for (int i = 0; i < fieldSize(); i++) {
                name = fieldAt(i).name;
                if (name.equalsIgnoreCase(fieldName)
                        || name.indexOf(".") >= 0 && (name.endsWith('.' + fieldName) || name.toLowerCase().endsWith('.' + fieldName.toLowerCase()))) {
                    return i;
                }
            }
            return -1;
        }
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
     */
    public Row(Map map) {
        this.fields = new ArrayList();
        this.data = new Object[map.size()];
        String key;
        int n = 0;
        for (Iterator i = map.keySet().iterator(); i.hasNext();) {
            key = (String) i.next();
            this.data[n] = map.get(key);
            this.fields.add(new Field(key, DbTypes.getObjType(this.data[n])));
            n++;
        }
    }
    /**
     * 构造器
     * @param list
     * @param valMap
     */
    public Row(ArrayList fields) {
        this.fields = fields;
        this.data = defRowData();
    }
    /**
     * 构造器
     * @param rowSet
     */
    public Row(RowSet rowSet, ArrayList fields, Object[] data) {
        this.rowSet = rowSet;
        this.fields = fields;
        this.data = data;
    }
    /**
     * 转换为hashMap
     * @return
     */
    public HashMap toMap() {
        HashMap map = new HashMap();
        for (int i = 0; i < fields.size(); i++) {
            map.put(((Field) fields.get(i)).name, this.data[i]);
        }
        return map;
    }
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
            } else if (field.type == DbTypes.JAVA_OBJECT) {
                data[i] = new Object();
            } else {
                data[i] = "";
            }
        }
        return data;
    }
    /**
     * 数据
     */
    public Object[] data;
    /**
     * 取得数据
     * @param key
     * @return
     */
    public Object get(String key) {
        return this.get(key, null);
    }
    /**
     * 取得数据
     * @param key
     * @return
     */
    public Object get(String key, Object def) {
        Object val = null;
        int index = this.fieldIndex(key);
        if (index >= 0) {
            val = this.data[index];
        }
        return val == null ? def : val;
    }
    /**
     * 设置数据
     * @param key
     * @param val
     */
    public void set(String key, Object val) {
        int index = this.fieldIndex(key);
        if (index >= 0) {
            this.set(index, val);
        }
    }
    /**
     * 取得数据
     * @param key
     * @return
     * @throws HgException 
     */
    public Object get(int index) {
        return this.data[index];
    }
    /**
     * 取得数据
     * @param key
     * @return
     * @throws HgException 
     */
    public Object get(int index, Object def) {
        return this.data[index] == null ? def : this.data[index];
    }
    /**
     * 设置数据
     * @param key
     * @param val
     */
    public void set(int index, Object val) {
        this.data[index] = To.toObj(val, ((Field) this.fields.get(index)).type);
        if (this.rowSet != null) {
            this.rowSet.setDataChange();
        }
    }
    public String getString(String key) {
        return this.getString(key, null);
    }
    public String getString(String key, String def) {
        Object obj = this.get(key);
        if (obj != null) {
            return To.objToString(obj);
        } else {
            return def;
        }
    }
    public long getLong(String key) {
        return this.getLong(key, 0);
    }
    public long getLong(String key, long def) {
        Object obj = this.get(key);
        if (obj != null) {
            if (obj instanceof Long) {
                return ((Long) obj).longValue();
            } else if (obj instanceof String) {
                try {
                    return Long.parseLong((String) obj);
                } catch (Exception e) {
                    return def;
                }
            } else {
                return def;
            }
        } else {
            return def;
        }
    }
    public int getInt(String key) {
        return this.getInt(key, 0);
    }
    public int getInt(String key, int def) {
        return (int) this.getLong(key, def);
    }
    public void setData(Map map) {
        String key;
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            key = (String) it.next();
            this.set(key, map.get(key));
        }
    }
    public void clear() {
    	this.data = this.defRowData();
    }
    /**
     * 转换为字符串
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Field field;
        for (int i = 0; i < this.fields.size(); i++) {
            field = (Field) this.fields.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append(field.getShortName()).append("=").append(this.getString(field.name, ""));
        }
        return sb.toString();
    }
}
