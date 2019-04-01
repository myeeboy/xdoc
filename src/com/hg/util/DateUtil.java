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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期通用方法
 * @author wanghg
 * @version 1.0
 */
public class DateUtil {
    /**
     * 日期格式
     */
    public static String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 时间格式
     */
    public static String TIME_FORMAT = "HH:mm:ss";
    /**
     * 日期时间格式
     */
    public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 格式化日期
     * @param date
     * @param format
     * @return
     */
    public static String toString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    /**
     * 格式化为日期型字符串
     * @param date
     * @return
     */
    public static String toDateString(Date date) {
        return toString(date, DATE_FORMAT);
    }
    /**
     * 格式化为日期时间型字符串
     * @param date
     * @return
     */
    public static String toTimeString(Date date) {
        return toString(date, TIME_FORMAT);
    }
    /**
     * 格式化为日期时间型字符串
     * @param date
     * @return
     */
    public static String toDateTimeString(Date date) {
        return toString(date, DATE_TIME_FORMAT);
    }
    /**
	 * 格式化为日期型字符串
	 * @param date
	 * @return
	 * @throws HgException 
	 */
	public static Date toDate(String str) {
	    return toDate(str, (Date) null);
	}
	/**
     * 格式化日期
     * @param date
     * @param format
     * @return
     * @throws HgException 
     */
    public static Date toDate(String str, String format) {
    	return toDate(str, format, null);
    }
    public static Date toDate(String str, Date def) {
		int n = str.length();
		String format;
		if (n == 4) {
			format = "yyyy";
		} else if (n == 7) {
			format = "yyyy-MM";
		} else if (n == 13) {
			format = "yyyy-MM-dd HH";
		} else if (n == 16) {
			format = "yyyy-MM-dd HH:mm";
		} else if (n == 19) {
			format = "yyyy-MM-dd HH:mm:ss";
		} else if (n == 5) {
			format = "HH:mm";
		} else if (n == 9) {
			format = "HH:mm:ss";
		} else {
			format = "yyyy-MM-dd";
		}
		return toDate(str, format, def);
	}
	/**
     * 格式化日期
     * @param date
     * @param format
     * @return
     * @throws HgException 
     */
    public static Date toDate(String str, String format, Date def) {
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (Exception e) {
        	return def != null ? def : new Date();
        }
    }
    /**
     * 格式化为日期型字符串
     * @param date
     * @return
     */
    public static String getDateString() {
        return toDateString(new Date());
    }
    /**
     * 格式化为日期时间型字符串
     * @param date
     * @return
     */
    public static String getTimeString() {
        return toTimeString(new Date());
    }
    /**
     * 格式化为日期时间型字符串
     * @param date
     * @return
     */
    public static String getDateTimeString() {
        return toDateTimeString(new Date());
    }
    /**
     * 日期相减(毫秒数)
     * @param d1
     * @param d2
     * @return
     */
    public static long sub(Date d1, Date d2) {
        Calendar cal = Calendar.getInstance();
        long n1,n2;
        cal.setTime(d1);
        n1 = cal.getTimeInMillis();
        cal.setTime(d2);
        n2 = cal.getTimeInMillis();
        return n1 - n2;
    }
    /**
     * 字符串日期相减(毫秒数)
     * @param d1
     * @param d2
     * @return
     */
    public static long sub(String str1, String str2) {
        return sub(toDate(str1), toDate(str2));
    }
    /**
     * 转换为GMT格式
     * @param date
     * @return
     */
    public static String toGMT(Date date) {
        DateFormat gmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);   
        gmt.setTimeZone(TimeZone.getTimeZone( "GMT")); 
        return gmt.format(date);
    }
}
