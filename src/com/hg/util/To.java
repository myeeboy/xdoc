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

import java.text.DecimalFormat;
import java.util.Date;

import com.hg.data.DbTypes;
import com.hg.data.IntArray;

/**
 * 数据转换
 * @author wanghg
 * @version 1.0
 */
public class To {
    /**
     * 对象转boolean
     * @param obj
     * @return
     */
    public static boolean toBool(Object obj, boolean def) {
        if (obj != null) {
            if (obj instanceof Boolean) {
                return ((Boolean) obj).booleanValue();
            } else if (obj instanceof Integer) {
                return ((Integer) obj).intValue() == 0 ? false : true;
            } else if (obj instanceof Long) {
                return ((Long) obj).longValue() == 0 ? false : true;
            } else if (obj instanceof Double) {
                return ((Double) obj).doubleValue() == 0 ? false : true;
            } else if (obj instanceof Date) {
                return ((Date) obj).getTime() == 0 ? false : true;
            } else {
                String str = obj.toString().toUpperCase();
                return str.equals("FALSE") || str.equals("F") || str.equals("NO") 
                    || str.equals("OFF") || str.equals("0") || str.equals("") ? false : true;
            }
        } else {
            return def;
        }
    }
    /**
     * 对象转boolean
     * @param obj
     * @return
     */
    public static boolean toBool(Object obj) {
        return toBool(obj, false);
    }
    /**
     * 对象转Long
     * @param obj
     * @return
     */
    public static long toLong(Object obj, long def) {
        if (obj != null) {
            if (obj instanceof Long) {
                return ((Long) obj).longValue();
            } else if (obj instanceof Integer) {
                return ((Integer) obj).intValue();
            } else if (obj instanceof Double) {
                return ((Double) obj).longValue();
            } else if (obj instanceof Boolean) {
                return ((Boolean) obj).booleanValue() ? 1 : 0;
            } else if (obj instanceof Date) {
                return ((Date) obj).getTime();
            } else {
                try {
                    return Long.parseLong(obj.toString());
                } catch (Exception e) {
                    return def;
                }
            }
        } else {
            return def;
        }
    }
    /**
     * 对象转Long
     * @param obj
     * @return
     */
    public static long toLong(Object obj) {
        return toLong(obj, (long) 0);
    }
    /**
     * 对象转int
     * @param obj
     * @return
     */
    public static int toInt(Object obj, int def) {
        if (obj != null) {
            if (obj instanceof Integer) {
                return ((Integer) obj).intValue();
            } else if (obj instanceof Short) {
                return ((Short) obj).intValue();
            } else if (obj instanceof Long) {
                return ((Long) obj).intValue();
            } else if (obj instanceof Double) {
                return ((Double) obj).intValue();
            } else if (obj instanceof Boolean) {
                return ((Boolean) obj).booleanValue() ? 1 : 0;
            } else if (obj instanceof Date) {
                return (int) ((Date) obj).getTime();
            } else {
                try {
                    return Integer.parseInt(obj.toString());
                } catch (Exception e) {
                    try {
                        return (new Double(Double.parseDouble(obj.toString()))).intValue();
                    } catch (Exception e2) {
                        return def;
                    }
                }
            }
        } else {
            return def;
        }
    }
    /**
     * 对象转int
     * @param obj
     * @return
     */
    public static int toInt(Object obj) {
        return toInt(obj, 0);
    }
    /**
     * 对象转double
     * @param obj
     * @return
     */
    public static double toDouble(Object obj, double def) {
        if (obj != null) {
            if (obj instanceof Double) {
                return ((Double) obj).doubleValue();
            } else if (obj instanceof Float) {
                return ((Float) obj).doubleValue();
            } else if (obj instanceof Integer) {
                return ((Integer) obj).intValue();
            } else if (obj instanceof Long) {
                return ((Long) obj).doubleValue();
            } else if (obj instanceof Boolean) {
                return ((Boolean) obj).booleanValue() ? 1 : 0;
            } else if (obj instanceof Date) {
                return ((Date) obj).getTime();
            } else {
                try {
                    return Double.parseDouble(obj.toString());
                } catch (Exception e) {
                    return def;
                }
            }
        } else {
            return def;
        }
    }
    /**
     * 对象转double
     * @param obj
     * @return
     */
    public static double toDouble(Object obj) {
        return toDouble(obj, 0);
    }
    /**
     * 对象转日期
     * @param obj
     * @return
     */
    public static Date toDate(Object obj, Date def) {
        if (obj != null) {
            if (obj instanceof Date) {
                return ((Date) obj);
            } else if (obj instanceof Integer) {
                return new Date((long) ((Integer) obj).intValue());
            } else if (obj instanceof Long) {
                return new Date(((Long) obj).longValue());
            } else if (obj instanceof Double) {
                return new Date(((Double) obj).longValue());
            } else if (obj instanceof String) {
        		return DateUtil.toDate((String) obj, def);
            } else {
                return def;
            }
        } else {
            return def;
        }
    }
    public static String toString(Object obj) {
        return toString(obj, "");
    }
    /**
     * 对象转字符串
     * @param obj
     * @return
     */
    public static String objToString(Object obj) {
        return objToString(obj, "");
    }
    /**
     * 对象转字符串
     * @param obj
     * @return
     */
    public static String objToString(Object obj, String def) {
        return toString(obj, def);
    }
    /**
     * 对象转日期
     * @param obj
     * @return
     */
    public static Date objToDate(Object obj) {
        return toDate(obj, new Date());
    }
    /**
     * 字符串转换为对象
     * @param str
     * @param type
     * @return
     */
    public static Object strToObj (String str, int type, Object def) {
        Object obj = null;
        if (type == DbTypes.VARCHAR) {
            obj = str;
        } else if (type == DbTypes.BOOLEAN) {
            obj = Boolean.valueOf(str.equalsIgnoreCase("FALSE") || str.equalsIgnoreCase("F") 
                    || str.equalsIgnoreCase("NO") || str.equalsIgnoreCase("0") ? false : true);
        } else if (type == DbTypes.INTEGER) {
            try {
                obj = Integer.valueOf(str);
            } catch (Exception e) {
                obj = (def == null ? new Integer(0) : def);
            }
        } else if (type == DbTypes.BIGINT) {
            try {
                obj = Long.valueOf(str);
            } catch (Exception e) {
                try {
                    obj = new Long(Double.valueOf(str).longValue());
                } catch (Exception e1) {
                    obj = (def == null ? new Long(0) : def);
                }
            }
        } else if (type == DbTypes.DOUBLE) {
            try {
                obj = Double.valueOf(str);
            } catch (Exception e) {
                obj = (def == null ? new Double(0) : def);
            }
        } else if (type == DbTypes.DATE) {
        	obj = DateUtil.toDate(str);
        } else {
            obj = str;
        }
        return obj;
    }
    /**
     * 字符串转换为对象
     * @param str
     * @param type
     * @return
     */
    public static Object strToObj(String str, int type) {
        return strToObj(str, type, null);
    }
    /**
     * 自动识别数据类型
     * @param str
     * @return
     */
    public static Object autoType(String str) {
        if (str != null && str.length() > 0) {
            char c = str.charAt(0);
            boolean bDouble = false;
            if (c == '0') {
                if (str.length() == 1) {
                    return new Long(0);
                } else if (str.charAt(1) != '.') {
                    return str;
                }
            }
            if (Character.isDigit(c)) {
                for (int i = 1; i < str.length(); i++) {
                    c = str.charAt(i);
                    if (!Character.isDigit(c)) {
                        if (c == '.' && !bDouble) {
                            bDouble = true;
                        } else {
                            return str;
                        }
                    }
                }
                try {
                    if (bDouble) {
                        return new Double(str);
                    } else {
                        return new Long(str);
                    }
                } catch (Exception e) {
                    return str;
                }
            }
        }
        return str;
    }
    public static Object toObj(Object obj, int type) {
        Object resobj;
        if (obj != null) {
            if (type == DbTypes.VARCHAR) {
                resobj = obj.toString();
            } else if (type == DbTypes.BOOLEAN) {
                if (obj instanceof Boolean) {
                    resobj = obj;
                } else {
                    resobj = Boolean.valueOf(toBool(obj));
                }
            } else if (type == DbTypes.INTEGER) {
                if (obj instanceof Integer) {
                    resobj = obj;
                } else {
                    resobj = new Integer(toInt(obj));
                }
            } else if (type == DbTypes.BIGINT) {
                if (obj instanceof Long) {
                    resobj = obj;
                } else {
                    resobj = new Long(toLong(obj));
                }
            } else if (type == DbTypes.DOUBLE) {
                if (obj instanceof Double) {
                    resobj = obj;
                } else {
                    resobj = new Double(toDouble(obj));
                }
            } else if (type == DbTypes.FLOAT) {
                if (obj instanceof Float) {
                    resobj = obj;
                } else {
                    resobj = new Float(toDouble(obj));
                }
            } else if (type == DbTypes.DATE) {
                if (obj instanceof Date) {
                    resobj = obj;
                } else {
                    resobj = objToDate(obj);
                }
            } else if (type == DbTypes.INTARRAY) {
                if (obj instanceof IntArray) {
                    resobj = obj;
                } else {
                    resobj = new IntArray();
                }
            } else if (type == DbTypes.JAVA_OBJECT) {
                resobj = obj;
            } else {
                resobj = obj.toString();
            }
        } else {
            resobj = defValue(type);
        }
        return resobj;
    }
    /**
     * 类型缺省值
     * @param type
     * @return
     */
    public static Object defValue(int type) {
        if (type == DbTypes.BOOLEAN) {
            return new Boolean(false);
        } else if (type == DbTypes.SMALLINT) {
            return new Short((short) 0);
        } else if (type == DbTypes.INTEGER) {
            return new Integer(0);
        } else if (type == DbTypes.BIGINT) {
            return new Long(0);
        } else if (type == DbTypes.FLOAT) {
            return new Float(0);
        } else if (type == DbTypes.DATE) {
            return new Date();
        } else if (type == DbTypes.DOUBLE) {
            return new Double(0);
        } else if (type == DbTypes.JAVA_OBJECT) {
            return new Object();
        } else if (type == DbTypes.INTARRAY) { //特殊支持索引存储
            return new IntArray();
        } else {
            return "";
        }
    }
    public static String toString(Object obj, String def) {
        if (obj != null) {
            if (obj instanceof Date) {
                return DateUtil.toDateTimeString((Date) obj);
            } else if (obj instanceof Float || obj instanceof Double) {
                String str = obj.toString();
                if (str.indexOf("E") > 0) {
                    DecimalFormat df = new DecimalFormat("0.######");
                    if (obj instanceof Float) {
                        str = df.format(((Float) obj).floatValue());
                    } else {
                        str = df.format(((Double) obj).doubleValue());
                    }
                }
                if (str.endsWith(".0")) {
                	str = str.substring(0, str.length() - 2);
                }
                return str;
            } else {
                return obj.toString();
            }
        } else {
            return def;
        }
    }
    public static String toString(double d) {
    	String str = Double.toString(d);
        if (str.indexOf("E") > 0) {
            str = new DecimalFormat("0.######").format(d);
        }
        if (str.endsWith(".0")) {
        	str = str.substring(0, str.length() - 2);
        }
        return str;
    }
}
