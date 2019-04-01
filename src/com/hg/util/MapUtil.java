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
package com.hg.util;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * map处理类
 * @author whg
 */
public class MapUtil {
    /**
     * 取得String
     * @param map
     * @param key
     * @param defVal
     * @return
     */
    public static Object getObj(Map map, String key, Object defVal) {
        Object val = (Object) map.get(key);
        if (val != null) {
            return val;
        } else {
            return defVal;
        }
    }
    /**
     * 取得String
     * @param map
     * @param key
     * @param defVal
     * @return
     */
    public static String getString(Map map, String key, String defVal) {
        return To.objToString(getObj(map, key, defVal));
    }
    /**
     * 取得String
     * @param map
     * @param key
     * @param defVal
     * @return
     */
    public static String getString(Map map, String key) {
        return To.objToString(getObj(map, key, ""));
    }
    /**
     * 取得int
     * @param map
     * @param key
     * @param defVal
     * @return
     */
    public static int getInt(Map map, String key, int defVal) {
        return To.toInt(getObj(map, key, new Integer(defVal)));
    }
    /**
     * 取得int
     * @param map
     * @param key
     * @param defVal
     * @return
     */
    public static double getDouble(Map map, String key, double defVal) {
        return To.toDouble(getObj(map, key, new Double(defVal)));
    }
    /**
     * 取得int
     * @param map
     * @param key
     * @param defVal
     * @return
     */
    public static boolean getBool(Map map, String key, boolean defVal) {
        return To.toBool(getObj(map, key, Boolean.valueOf(defVal)));
    }
    /**
     * 取得日期
     * @param map
     * @param key
     * @param defDate
     * @return
     */
    public static Date getDate(Map map, String key, Date defDate) {
        return To.objToDate(getObj(map, key, defDate));
    }
    public static long getLong(Map map, String key, long n) {
        return To.toLong(getObj(map, key, new Long(n)));
    }
    public static String igKey(Map map, String key) {
        if (!map.containsKey(key)) {
            if (!map.containsKey(key)) {
                Iterator it = map.keySet().iterator();
                String mkey;
                while (it.hasNext()) {
                    mkey = it.next().toString();
                    if (mkey.equalsIgnoreCase(key)) {
                        key = mkey;
                        break;
                    }
                }
            }
        }
        return key;
    }
}
