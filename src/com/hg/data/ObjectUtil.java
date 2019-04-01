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

import java.util.Date;

import com.hg.util.To;
import com.hg.util.HgException;

/**
 * 
 * @author wanghg
 * @version 1.0
 */
public class ObjectUtil {
    /**
     * 比较大小
     * @param obj1
     * @param obj2
     * @param dbType
     * @return
     * @throws HgException 
     */
    public static int compare(Object obj1, Object obj2, int dataType) throws HgException {
        if (dataType == DbTypes.INTEGER || dataType == DbTypes.BIGINT || dataType == DbTypes.DOUBLE) {
            double val1, val2;
            val1 = To.toDouble(obj1);
            val2 = To.toDouble(obj2);
            if (val1 > val2) {
                return 1;
            } else if (val1 < val2) {
                return -1;
            } else {
                return 0;
            }
        } else {
            if (obj1 == null) obj1 = "";
            if (obj2 == null) obj2 = "";
            return To.objToString(obj1).compareTo(To.objToString(obj2));
        }
    }

    /**
     * 对象相加
     * @param res
     * @param result
     * @param dbType
     * @return
     */
    public static Object add(Object obj1, Object obj2, int dataType) {
        if (dataType == DbTypes.INTEGER || dataType == DbTypes.BIGINT) {
            return new Long(To.toLong(obj1) + To.toLong(obj2));
        } else { //if (dataType == DbTypes.DOUBLE) {
            return new Double(To.toDouble(obj1) + To.toDouble(obj2));
        }
    }
    /**
     * 对象相加
     * @param res
     * @param result
     * @param dbType
     * @return
     * @throws HgException 
     */
    public static Object div(Object obj1, Object obj2) throws HgException {
        return new Double(To.toDouble(obj1) / To.toDouble(obj2));
    }
    /**
     * 对象平均
     * @param res
     * @param result
     * @param dbType
     * @return
     */
    public static Object avg(Object obj1, Object obj2) {
        return new Double((To.toDouble(obj1) + To.toDouble(obj2)) / 2);
    }
    /**
     * 对象MAX
     * @param res
     * @param result
     * @param dbType
     * @return
     * @throws HgException 
     */
    public static Object max(Object obj1, Object obj2, int dataType) throws HgException {
        if (compare(obj1, obj2, dataType) > 0) {
            return obj1;
        } else {
            return obj2;
        }
    }
    /**
     * 对象MIN
     * @param res
     * @param result
     * @param dbType
     * @return
     * @throws HgException 
     */
    public static Object min(Object obj1, Object obj2, int dataType) throws HgException {
        if (compare(obj1, obj2, dataType) < 0) {
            return obj1;
        } else {
            return obj2;
        }
    }
    /**
     * 取得对象类型
     * @param obj
     * @return
     */
    public static int type(Object obj) {
        if (obj instanceof Boolean) {
            return DbTypes.BOOLEAN;
        } else if (obj instanceof Integer) {
            return DbTypes.INTEGER;
        } else if (obj instanceof Long) {
            return DbTypes.BIGINT;
        } else if (obj instanceof Double) {
            return DbTypes.DOUBLE;
        } else if (obj instanceof Date) {
            return DbTypes.DATE;
        } else if (obj instanceof Row) {
            return DbTypes.ROW;
        } else if (obj instanceof RowSet) {
            return DbTypes.ROWSET;
        } else {
            return DbTypes.VARCHAR;
        }
    }
}
