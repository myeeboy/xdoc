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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class StrUtil {
    /**
     * 字符集
     */
    public static String charset = "UTF-8";
    /**
     * 替换字符串
     * @param str
     * @param cstr
     * @param rstr
     * @return
     */
    public static String replaceAll(String str, String cstr, String rstr) {
        return replaceAll(str, new String[] {cstr}, new String[] {rstr}, 0);
    }
    /**
     * 替换字符串
     * @param str
     * @param cstr
     * @param rstr
     * @return
     */
    public static String replaceAll(String str, String cstr, String rstr, int startPos) {
        return replaceAll(str, new String[] {cstr}, new String[] {rstr}, startPos);
    }
    /**
     * 替换字符串
     * @param str
     * @param cstr
     * @param rstr
     * @return
     */
    public static String replaceAll(String str, String[] cstr, String[] rstr) {
        return replaceAll(str, cstr, rstr, 0);
    }
    /**
     * 替换字符串
     * @param str
     * @param cstr
     * @param rstr
     * @return
     */
    public static String replaceAll(String str, String[] cstr, String[] rstr, int startPos) {
        StringBuffer sb = new StringBuffer();
        int lastIndex = 0;
        boolean bReplace;
        for (int i = startPos; i < str.length();) {
            bReplace = false;
            for (int j = 0; j < cstr.length; j++) {
                if (i + cstr[j].length() <= str.length() && str.substring(i, i + cstr[j].length()).equals(cstr[j])) {
                    sb.append(str.substring(lastIndex, i)).append(rstr[j]);
                    lastIndex = i + cstr[j].length();
                    i += cstr[j].length();
                    bReplace = true;
                    break;
                }
            }
            if (!bReplace) i++;
        }
        if (lastIndex < str.length()) {
            sb.append(str.substring(lastIndex));
        }
        return sb.toString();
    }
    /**
     * nStr
     * @param str
     * @param n
     * @return
     */
    public static String strn(String str, int n) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    /**
     * 左补串
     * @param str
     * @param nLen
     * @param pStr
     * @return
     */
    public static String lpad(String str, int nLen, String pStr) {
        if (str.length() < nLen) {
            return strn(pStr, (nLen - str.length()) / pStr.length()) + str;
        } else {
            return str.substring(str.length() - nLen, str.length());
        }
    }
    /**
     * 右补串
     * @param str
     * @param nLen
     * @param pStr
     * @return
     */
    public static String rpad(String str, int nLen, String pStr) {
        if (str.length() < nLen) {
            return str + strn(pStr, (nLen - str.length()) / pStr.length());
         } else {
            return str.substring(0, nLen);
        }
    }
    /**
     * 去掉左空格
     * @param str
     * @return
     */
    public static String ltrim(String str) {
        int len = str.length();
        int st = 0;
        while ((st < len) && (str.charAt(st) <= ' ')) {
            st++;
        }
        return (st > 0) ? str.substring(st) : str;
    }
    /**
     * 去掉右空格
     * @param str
     * @return
     */
    public static String rtrim(String str) {
        int len = str.length();
        while (len > 0 && (str.charAt(len - 1) <= ' ')) {
            len--;
        }
        return (len < str.length()) ? str.substring(0, len) : str;
    }
    /**
     * 分割
     * @param str
     * @param sepStr
     * @return
     */
    public static String[] split(String str, String sepStr) {
        ArrayList list = new ArrayList();
        int last = 0;
        for (int i = 0; i + sepStr.length() <= str.length(); i++) {
            if (str.substring(i, i + sepStr.length()).equals(sepStr)) {
                list.add(str.substring(last, i));
                i = i + sepStr.length() - 1;
                last = i + 1;
            } 
        }
        if (last < str.length()) {
            list.add(str.substring(last));
        }
        String[] strs = new String[list.size()];
        for (int i = 0; i < strs.length; i++) {
            strs[i] = (String) list.get(i);
        }
        return strs;
    }
    public static String[] splitn(String str, int n) {
    	if (n <= 0) {
    		n = 1;
    	}
    	String[] strs = new String[(int) Math.ceil(str.length() / (double) n)];
    	for (int i = 0; i < strs.length; i++) {
    		strs[i] = str.substring(i * n, Math.min(str.length(), (i + 1) * n));
    	}
        return strs;
    }
    public static void trimAry(String[] strs) {
        if (strs != null) {
            for (int i = 0; i < strs.length; i++) {
                strs[i] = strs[i].trim();
            }
        }
    }
    public static String join(Object[] strs) {
    	return join(strs, ",");
    }
    public static String join(Object[] strs, String sep) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strs.length; i++) {
            if (i > 0) sb.append(sep);
            sb.append(strs[i]);
        }
        return sb.toString();
    }
    /**
     * 首字母大写
     * @param str
     */
    public static String capitalize(String str) {
        String tStr = "";
        String[] strSeg = str.split("_");
        for (int i = 0; i < strSeg.length; i++) {
            tStr += strSeg[i].substring(0,1).toUpperCase() + strSeg[i].substring(1).toUpperCase().toLowerCase();
        }
        return tStr;
    }
    public static String constReplace(String str, String splitStr, HashMap map) {
        if (str.indexOf(splitStr) >= 0) {
            String[] strs = str.split(splitStr);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < strs.length; i++) {
                if (i % 2 == 1) {
                    if (map.get(strs[i]) != null) {
                        sb.append(map.get(strs[i]));
                    } else {
                        sb.append(splitStr).append(strs[i]).append(splitStr);
                    }
                } else {
                    sb.append(strs[i]);
                }
            }
            str = sb.toString();
        }
        return str;
    }
    public static long toSecond(String strTime) {
        strTime = strTime.toUpperCase();
        try {
            if (strTime.endsWith("D")) {
                return Long.parseLong(strTime.substring(0, strTime.length() - 1)) * 24 * 60 * 60;
            } else if (strTime.endsWith("H")) {
                return Long.parseLong(strTime.substring(0, strTime.length() - 1)) * 60 * 60;
            } else if (strTime.endsWith("M")) {
                return Long.parseLong(strTime.substring(0, strTime.length() - 1)) * 60;
            } else if (strTime.endsWith("S")) {
                return Long.parseLong(strTime.substring(0, strTime.length() - 1));
            } else {
                return Long.parseLong(strTime);
            }
        } catch (Exception e) {
            return 0;
        }
    }
    /**
     * 分析参数
     * @param args
     * @return
     */
    public static HashMap parseArgs(String[] args) {
    	HashMap map = new HashMap();
    	for (int i = 0; i < args.length; i++) {
    		if (args[i].startsWith("-")) {
    			if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
    				map.put(args[i].substring(1), args[i + 1]);
    				i++;
    			} else {
    				map.put(args[i].substring(1), "");
    			}
    		}
    	}
    	return map;
    }
    /**
     * 匹配
     * @param str
     * @param rstr
     * @return
     */
    public static boolean likeMatch(String str, String likeStr) {
        if (likeStr.indexOf('%') < 0) {
            //没有％
            if (str.length() != likeStr.length()) {
                return false;
            } else {
                return sCharMatch(str, likeStr);
            }
        } else {
            if (likeStr.length() > 0 && likeStr.charAt(0) != '%') { //%前有数据
                String tmpStr = likeStr.substring(0, likeStr.indexOf('%'));
                likeStr = likeStr.substring(likeStr.indexOf('%') + 1);
                if (str.length() < tmpStr.length()) {
                    return false;
                } else {
                    if (!sCharMatch(str.substring(0, tmpStr.length()), tmpStr)) {
                        return false;
                    }
                    str = str.substring(tmpStr.length());
                }
            }
            if (likeStr.length() > 0 && likeStr.charAt(likeStr.length() - 1) != '%') { //%后有数据
                String tmpStr = likeStr.substring(likeStr.lastIndexOf('%') + 1);
                likeStr = likeStr.substring(0, likeStr.lastIndexOf('%'));
                if (str.length() < tmpStr.length()) {
                    return false;
                } else {
                    if (!sCharMatch(str.substring(str.length() - tmpStr.length()), tmpStr)) {
                        return false;
                    }
                    str = str.substring(0, str.length() - tmpStr.length());
                }
            }
        }
        String[] likes = (likeStr).split(String.valueOf('%'));
        int nPos = 0;
        for (int i = 0; i < likes.length; i++) {
            if (likes[i].length() > 0) {
                if (likes[i].indexOf('_') < 0) {
                    nPos = str.indexOf(likes[i], nPos);
                    if (nPos < 0) return false;
                    nPos += likes[i].length();
                } else {
                    //用"_"分割
                    ArrayList strList = new ArrayList();
                    StringBuffer sb = new StringBuffer();
                    for (int j = 0; j < likes[i].length(); j++) {
                        if (likes[i].charAt(j) != '_') {
                            sb.append(likes[i].charAt(j));
                        } else {
                            if (sb.length() > 0) {
                                strList.add(sb.toString());
                                sb.setLength(0);
                            }
                            strList.add(null);
                        }
                    }
                    if (sb.length() > 0) {
                        strList.add(sb.toString());
                    }
                    for (int j = 0; j < strList.size(); j++) {
                        if (strList.get(j) != null) {
                            nPos = str.indexOf((String) strList.get(j), nPos);
                            if (nPos < 0) return false;
                            nPos += ((String) strList.get(j)).length();
                        } else {
                            nPos++;
                        }
                    }
                }
            }
        }
        return true;
    }
    /**
     * 简单匹配，支持单字符“_”
     * @param str
     * @param rstr
     * @return
     */
    private static boolean sCharMatch(String str, String likeStr) {
        if (str.equals(likeStr)) {
            return true;
        } else if (str.length() == likeStr.length()) {
            for (int i = 0; i < str.length(); i++) {
                if (likeStr.charAt(i) != '_' && (str.charAt(i) != likeStr.charAt(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    public static String replace(String text, Map map) {
        if (text.length() > 0 && text.indexOf("#") >= 0) {
            String str = text;
            StringBuffer sb = new StringBuffer();
            int pos = str.indexOf("#");
            String expStr;
            if (pos >= 0) {
                while (pos >= 0) {
                    if (pos > 0) {
                        sb.append(str.substring(0, pos));
                    }
                    str = str.substring(pos + 1);
                    pos = str.indexOf("#");
                    if (pos > 0) {
                        expStr = str.substring(0, pos).trim();
                        if (expStr.length() > 0 && map != null) {
                            sb.append(map.get(expStr));
                        }
                        str = str.substring(pos + 1);
                    } else if (pos == 0) {
                        str = str.substring(1);
                    }
                    pos = str.indexOf("#");
                }
            }
            sb.append(str);
            return sb.toString();
        } else {
            return text;
        }
    }
    /**
     * 段落格式化：去掉空行，自动前缀空格，智能合并行
     * @param str
     * @return
     */
    public static String paraFormat(String str) {
        char[] endChars = new char[] {'.','。', '!', '！'};
        StringBuffer sb = new StringBuffer();
        String[] strs = str.split("\n");
        boolean okEnd = true;
        for (int i = 0; i < strs.length; i++) {
            strs[i] = strs[i].trim();
            if (strs[i].length() > 0) {
                char c = strs[i].charAt(strs[i].length() - 1);
                if (okEnd) {
                    sb.append("\n    ").append(strs[i]);
                } else {
                    sb.append(strs[i]);
                }
                okEnd = false;
                for (int j = 0; j < endChars.length; j++) {
                    if (endChars[j] == c) {
                        okEnd = true;
                        break;
                    }
                } 
            }
        }
        return sb.toString();
    }
    private static char[] seqChar = new char[] {'a', 'A', 'Ⅰ', 'ⅰ', '①', '⑴', StrUtil.CHAR_ONE, '㈠'};
	public static String toStrInt(char type, int n) {
		for (int i = 0; i < seqChar.length; i++) {
			if (type == seqChar[i]) {
				return String.valueOf((char) (seqChar[i] + n - 1));
			}
		}
		String res = String.valueOf(n);
		if (type == '一') {
			res = toCNum(res);
		} else if (type == '壹') {
			res = toCBNum(res);
		}
		return res;
	}
	public static String format(String str, String format) {
		if (str.indexOf(',') >= 0) {
			String[] strs = str.split(",");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < strs.length; i++) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(sformat(strs[i], format));
			}
			return sb.toString();
		} else {
			return sformat(str, format);
		}
	}
	private static String sformat(String str, String format) {
		try {
			if (format.startsWith("d:") || format.equals("cd") || format.equals("cld")) { //日期
                Date date = To.toDate(str, null);
                if (date == null) {
                	return str;
                }
                if (format.startsWith("d:")) {
                    str = (new SimpleDateFormat(format.substring(2))).format(date);
                } else if (format.equals("cd")) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    String[] cn = new String[] {"○", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
                    String year = String.valueOf(cal.get(Calendar.YEAR));
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < year.length(); i++) {
                        sb.append(cn[year.charAt(i) - 48]);
                    }
                    sb.append("年");
                    sb.append(toCNum(String.valueOf(cal.get(Calendar.MONTH) + 1))).append("月");
                    sb.append(toCNum(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)))).append("日");
                    str = sb.toString();
                }
			} else if (format.startsWith("n_") 
					|| format.startsWith("c_")
					|| format.startsWith("p_")) {
				double d = To.toDouble(str, Double.MIN_VALUE);
				if (d != Double.MIN_VALUE) {
					if (format.startsWith("n_")) { //数字
						str = NumberFormat.getNumberInstance(toLocale(format.substring(2))).format(To.toDouble(str));
					} else if (format.startsWith("c_")) { //货币
						str = NumberFormat.getCurrencyInstance(toLocale(format.substring(2))).format(To.toDouble(str));
					} else if (format.startsWith("p_")) { //百分比
						str = NumberFormat.getPercentInstance(toLocale(format.substring(2))).format(To.toDouble(str));
					}				
				}
			} else if (format.equals("n") 
					|| format.equals("c")
					|| format.equals("p")
					|| format.startsWith("n:")) {
				double d = To.toDouble(str, Double.MIN_VALUE);
				if (d != Double.MIN_VALUE) {
					if (format.equals("n")) { //数字
						str = NumberFormat.getNumberInstance(toLocale()).format(To.toDouble(str));
					} else if (format.equals("c")) { //货币
						str = NumberFormat.getCurrencyInstance(toLocale()).format(To.toDouble(str));
					} else if (format.equals("p")) { //百分比
						str = NumberFormat.getPercentInstance(toLocale()).format(To.toDouble(str));
					} else if (format.startsWith("n:")) { //数字
						str = (new DecimalFormat(format.substring(2))).format(To.toDouble(str));
					}				
				}
			} else if (format.equals("u")) { //大写字母
				str = str.toUpperCase();
			} else if (format.equals("l")) { //小写字母
				str = str.toLowerCase();
			} else if (format.equals("t")) { //修剪空格
				str = str.trim();
			} else if (format.equals("cn")) { //中文数字
				str = toCNum(str);
			} else if (format.equals("cbn")) { //中文大写数字
				str = toCBNum(str);
            } else if (format.equals("rmb")) { //人民币
                str = toRMB(str);
            } else if (format.equals("繁")) { //繁体
                str = toFan(str);
            } else if (format.equals("简")) { //简体
                str = toJian(str);
            } else if (format.startsWith("dict:")) {
                str = map(format.substring(5), str);
            } else if (format.startsWith("map:")) {
                str = map(format.substring(4), str);
			}
		} catch (Exception e) {
		}
		return str;
	}
	private static Locale toLocale() {
		return toLocale("zh_CN");
	}
	private static Locale toLocale(String loc) {
		if (loc.equals("zh_CN")) {
			return Locale.CHINA;
		} else if (loc.equals("en")) {
			return Locale.US;
		} else {
			String[] strs = loc.split("_");
			String[] locs = new String[] {"", "", ""};
			for (int i = 0; i < strs.length && i < locs.length; i++) {
				locs[i] = strs[i];
			}
			return new Locale(locs[0] + "_" + locs[1] + "_" + locs[2], locs[0], locs[1]);
		}
	}
	private static String map(String kvStr, String str) {
        String[] kvs = kvStr.split(";");
        boolean num = StrUtil.isNumber(str);
        double d = 0;
        if (num) {
            d = Double.parseDouble(str);
        }
        int pos;
        String itemCode;
        for (int i = 0; i < kvs.length; i++) {
        	pos = kvs[i].indexOf('=');
        	if (pos >= 0) {
        		itemCode = kvs[i].substring(0, pos);
        		if (num && inRange(d, itemCode) 
        				|| inEnum(str, itemCode)
        				|| i == kvs.length - 1 && pos == 0) {
        			return kvs[i].substring(pos + 1);
        		}
        	}
        }
        return str;
    }
    public static String toCNum(String strNum) {
		return toCNum(strNum, false);
	}
	public static String toCBNum(String strNum) {
		return toCNum(strNum, true);
	}
	public static String toRMB(String strNum) {
	    String[] cUnit = new String[] {"", "万", "亿"};
	    String[] cDigit = new String[] {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
	    String[] cDecDigit = new String[] {"", "拾", "佰", "仟", "", "拾", "佰", "仟", "", "拾", "佰", "仟"};
	    String strMinus = "负";
        String strInt, strDec;
        if (strNum.indexOf(".") >= 0) {
            strInt = strNum.substring(0, strNum.indexOf("."));
            strDec = strNum.substring(strNum.indexOf(".") + 1);
            if (strDec.replace('0',' ').trim().length() == 0) {
                strDec = "";
            }
        } else {
            strInt = strNum;
            strDec = "";
        }
        if (strInt.equals("0") && strDec.length() == 0) {
            return cDigit[0] + "元整";
        }
        String cNum = "";
        if (strInt.length() <= 12) {
            int n;
            for (int i = strInt.length() - 1; i > -1; i--) {
                if ((strInt.length() - i - 1) % 4 == 0) {
                    cNum = cUnit[(strInt.length() - i - 1) / 4] + cNum;
                }
                if (strInt.charAt(i) == '-') {
                    cNum = strMinus + cNum;
                } else {
                    n = Integer.parseInt(String.valueOf(strInt.charAt(i)));
                    if (strInt.length() - i >= 0 && n > 0) {
                        cNum = cDigit[n] + cDecDigit[strInt.length() - i - 1] + cNum;
                    } else {
                        if (n > 0 || cNum.length() > 0) {
                            cNum = cDigit[n] + cNum;                            
                        }
                    }
                }
            }
            //去掉重复的0
            while (cNum.indexOf(cDigit[0] + cDigit[0]) >= 0) {
                cNum = StrUtil.replaceAll(cNum, cDigit[0] + cDigit[0], cDigit[0]);
            }
            //去掉头上的0
            if (cNum.startsWith(cDigit[0])) {
                cNum = cNum.substring(1);
            } else if (cNum.startsWith(strMinus + cDigit[0])) {
                cNum = cNum.substring(2);
            }
            if (strDec.length() > 0) {
                if (cNum.equals(cDigit[0])) {
                    cNum = "";
                } else if (cNum.equals(strMinus + cDigit[0])) {
                    cNum = strMinus;
                } else if (cNum.length() > 0) {
                    cNum += "元";
                }
                for (int i = 0; i < strDec.length() && i < 3; i++) {
                    n = Integer.parseInt(String.valueOf(strDec.charAt(i)));
                    cNum += cDigit[n];
                    if (i == 0) {
                        cNum += "角";
                    } else if (i == 1) {
                        cNum += "分";
                    } else if (i == 2) {
                        cNum += "厘";
                    }
                }
            } else {
                if (cNum.length() == 0 || cNum.equals(cDigit[0]) || cNum.equals(strMinus + cDigit[0])) {
                    cNum = "";
                } else {
                    cNum += "元整";
                }
            }
            cNum = cNum.replaceAll(cDigit[0] + cUnit[1], cUnit[1]);
            cNum = cNum.replaceAll(cDigit[0] + cUnit[2], cUnit[2]);
            return cNum;
        } else {
            return "";
        }
	}
	public static String toCNum(String strNum, boolean big) {
	    String[] cUnit = new String[] {"", "万", "亿"};
	    String[] cDigit;
	    String[] cDecDigit;
	    String strMinus = "负";
	    if (big) {
	        cDigit = new String[] {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
	        cDecDigit = new String[] {"", "拾", "佰", "仟", "", "拾", "佰", "仟", "", "拾", "佰", "仟"}; 
	    } else {
	        cDigit = new String[] {"○", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
	        cDecDigit = new String[] {"", "十", "百", "千", "", "十", "百", "千", "", "十", "百", "千"};
	    }
        String strInt, strDec;
        if (strNum.indexOf(".") >= 0) {
            strInt = strNum.substring(0, strNum.indexOf("."));
            strDec = strNum.substring(strNum.indexOf(".") + 1);
            if (strDec.replace('0',' ').trim().length() == 0) {
                strDec = "";
            }
        } else {
            strInt = strNum;
            strDec = "";
        }
        if (strInt.equals("0") && strDec.length() == 0) {
            return cDigit[0];
        }
        String cNum = "";
        if (strInt.length() <= 12) {
            int n;
            for (int i = strInt.length() - 1; i > -1; i--) {
                if ((strInt.length() - i - 1) % 4 == 0) {
                    cNum = cUnit[(strInt.length() - i - 1) / 4] + cNum;
                }
                if (strInt.charAt(i) == '-') {
                    cNum = strMinus + cNum;
                } else {
                    n = Integer.parseInt(String.valueOf(strInt.charAt(i)));
                    if (strInt.length() - i >= 0 && n > 0) {
                        cNum = cDigit[n] + cDecDigit[strInt.length() - i - 1] + cNum;
                    } else {
                        if (n > 0 || cNum.length() > 0) {
                            cNum = cDigit[n] + cNum;                            
                        }
                    }
                }
            }
            //去掉重复的0
            while (cNum.indexOf(cDigit[0] + cDigit[0]) >= 0) {
                cNum = StrUtil.replaceAll(cNum, cDigit[0] + cDigit[0], cDigit[0]);
            }
            //去掉头上的0
            if (cNum.startsWith(cDigit[0])) {
                cNum = cNum.substring(1);
            } else if (cNum.startsWith(strMinus + cDigit[0])) {
                cNum = cNum.substring(2);
            }
            if (strDec.length() > 0) {
                if (cNum.length() == 0) {
                    cNum = cDigit[0];
                }
                if (cNum.equals(strMinus)) {
                    cNum = strMinus + cDigit[0];
                }
                cNum += "点";
                for (int i = 0; i < strDec.length(); i++) {
                    n = Integer.parseInt(String.valueOf(strDec.charAt(i)));
                    cNum += cDigit[n];
                }
            }
            cNum = cNum.replaceAll(cDigit[0] + cUnit[1], cUnit[1]);
            cNum = cNum.replaceAll(cDigit[0] + cUnit[2], cUnit[2]);
            return cNum;
        } else {
            return "";
        }
	}
    private static volatile SecureRandom numberGenerator = null;
    public static String uuid() {
    	long mostSigBits;
    	long leastSigBits;
        SecureRandom ng = numberGenerator;
        if(ng == null) numberGenerator = ng = new SecureRandom();
        byte data[] = new byte[16];
        ng.nextBytes(data);
        data[6] &= 0xf;
        data[6] |= 0x40;
        data[8] &= 0x3f;
        data[8] |= 0x80;
        long msb = 0L;
        long lsb = 0L;
        for(int i = 0; i < 8; i++)
        	msb = msb << 8 | (long)(data[i] & 0xff);
        
        for(int i = 8; i < 16; i++)
        	lsb = lsb << 8 | (long)(data[i] & 0xff);
        
        mostSigBits = msb;
        leastSigBits = lsb;
        return (digits(mostSigBits >> 32, 8) +
                digits(mostSigBits >> 16, 4) +
                digits(mostSigBits, 4) +
                digits(leastSigBits >> 48, 4) +
                digits(leastSigBits, 12));
    }
    private static String digits(long val, int digits) {
        long hi = 1L << digits * 4;
        return Long.toHexString(hi | val & hi - 1L).substring(1);
    }
    public static String toBase64(final byte[] data) {
        final char[] out = new char[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;

            int val = (0xFF & data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        return new String(out);
    }
    public static byte[] fromBase64(String str) {
    	char[] data = str.toCharArray();
        int tempLen = data.length;
        for (int ix = 0; ix < data.length; ix++) {
            if ((data[ix] > 255) || codes[data[ix]] < 0) {
                --tempLen; // ignore non-valid chars and padding
            }
        }
        // calculate required length:
        //  -- 3 bytes for every 4 valid base64 chars
        //  -- plus 2 bytes if there are 3 extra base64 chars,
        //     or plus 1 byte if there are 2 extra.

        int len = (tempLen / 4) * 3;
        if ((tempLen % 4) == 3) {
            len += 2;
        }
        if ((tempLen % 4) == 2) {
            len += 1;
        }

        final byte[] out = new byte[len];


        int shift = 0; // # of excess bits stored in accum
        int accum = 0; // excess bits
        int index = 0;

        // we now go through the entire array (NOT using the 'tempLen' value)
        for (int ix = 0; ix < data.length; ix++) {
            final int value = (data[ix] > 255) ? -1 : codes[data[ix]];

            if (value >= 0) { // skip over non-code
                accum <<= 6; // bits shift up by 6 each time thru
                shift += 6; // loop, with new bits being put in
                accum |= value; // at the bottom.
                if (shift >= 8) { // whenever there are 8 or more shifted in,
                    shift -= 8; // write them out (from the top, leaving any
                    out[index++] = // excess at the bottom for next iteration.
                        (byte) ((accum >> shift) & 0xff);
                }
            }
        }
        if (index != out.length) {
            throw new Error("Miscalculated data length (wrote " 
                + index + " instead of " + out.length + ")");
        }

        return out;
    }
    private static char[] alphabet =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    private static byte[] codes = new byte[256];
    static {
        for (int i = 0; i < 256; i++) {
            codes[i] = -1;
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            codes[i] = (byte) (i - 'A');
        }
        for (int i = 'a'; i <= 'z'; i++) {
            codes[i] = (byte) (26 + i - 'a');
        }
        for (int i = '0'; i <= '9'; i++) {
            codes[i] = (byte) (52 + i - '0');
        }
        codes['+'] = 62;
        codes['/'] = 63;
    }
    /**
     * sql字符串编码
     * @param str
     * @return
     */
    public static String sqlstr(String str) {
        StringBuffer sb = new StringBuffer("'");
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '\'') {
                sb.append("''");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else if (c == '\t') {
                sb.append("\\t");
            } else {
                sb.append(c);
            }
        }
        sb.append("'");
        return sb.toString();
    }
    public static String unSqlstr(String str) {
        str = str.trim();
        if (str.startsWith("'") && str.endsWith("'")) {
            StringBuffer sb = new StringBuffer();
            char c;
            for (int i = 1; i < str.length() - 1; i++) {
                c = str.charAt(i);
                if (c == '\\' && i + 1 < str.length() - 1) {
                    c = str.charAt(i + 1);
                    if (c == 'n') {
                        sb.append('\n');
                    } else if (c == 'r') {
                        sb.append('\r');
                    } else if (c == 't') {
                        sb.append('\t');
                    } else {
                        sb.append(c);
                    }
                    i++;
                } else if (c == '\'' && i + 1 < str.length() - 1 && str.charAt(i + 1) == '\'') {
                    sb.append('\'');
                    i++;
                } else {
                    sb.append(c);
                }
            }
            str = sb.toString();
        }
        return str;
    }
    /**
     * 字符串解码
     * @param str
     * @return
     */
    private static final String jian = "锕皑蔼碍爱嗳嫒瑷暧霭谙铵鹌肮袄奥媪骜鳌坝罢钯摆败呗颁办绊钣帮绑镑谤剥饱宝报鲍鸨龅辈贝钡狈备惫鹎贲锛绷笔毕毙币闭荜哔滗铋筚跸边编贬变辩辫苄缏笾标骠飑飙镖镳鳔鳖别瘪濒滨宾摈傧缤槟殡膑镔髌鬓饼禀拨钵铂驳饽钹鹁补钸财参蚕残惭惨灿骖黪苍舱仓沧厕侧册测恻层诧锸侪钗搀掺蝉馋谗缠铲产阐颤冁谄谶蒇忏婵骣觇禅镡场尝长偿肠厂畅伥苌怅阊鲳钞车彻砗尘陈衬伧谌榇碜龀撑称惩诚骋枨柽铖铛痴迟驰耻齿炽饬鸱冲冲虫宠铳畴踌筹绸俦帱雠橱厨锄雏础储触处刍绌蹰传钏疮闯创怆锤缍纯鹑绰辍龊辞词赐鹚聪葱囱从丛苁骢枞凑辏蹿窜撺错锉鹾达哒鞑带贷骀绐担单郸掸胆惮诞弹殚赕瘅箪当挡党荡档谠砀裆捣岛祷导盗焘灯邓镫敌涤递缔籴诋谛绨觌镝颠点垫电巅钿癫钓调铫鲷谍叠鲽钉顶锭订铤丢铥东动栋冻岽鸫窦犊独读赌镀渎椟牍笃黩锻断缎簖兑队对怼镦吨顿钝炖趸夺堕铎鹅额讹恶饿谔垩阏轭锇锷鹗颚颛鳄诶儿尔饵贰迩铒鸸鲕发罚阀珐矾钒烦贩饭访纺钫鲂飞诽废费绯镄鲱纷坟奋愤粪偾丰枫锋风疯冯缝讽凤沣肤辐抚辅赋复负讣妇缚凫驸绂绋赙麸鲋鳆钆该钙盖赅杆赶秆赣尴擀绀冈刚钢纲岗戆镐睾诰缟锆搁鸽阁铬个纥镉颍给亘赓绠鲠龚宫巩贡钩沟苟构购够诟缑觏蛊顾诂毂钴锢鸪鹄鹘剐挂鸹掴关观馆惯贯诖掼鹳鳏广犷规归龟闺轨诡贵刽匦刿妫桧鲑鳜辊滚衮绲鲧锅国过埚呙帼椁蝈铪骇韩汉阚绗颉号灏颢阂鹤贺诃阖蛎横轰鸿红黉讧荭闳鲎壶护沪户浒鹕哗华画划话骅桦铧怀坏欢环还缓换唤痪焕涣奂缳锾鲩黄谎鳇挥辉毁贿秽会烩汇讳诲绘诙荟哕浍缋珲晖荤浑诨馄阍获货祸钬镬击机积饥迹讥鸡绩缉极辑级挤几蓟剂济计记际继纪讦诘荠叽哜骥玑觊齑矶羁虿跻霁鲚鲫夹荚颊贾钾价驾郏浃铗镓蛲歼监坚笺间艰缄茧检碱硷拣捡简俭减荐槛鉴践贱见键舰剑饯渐溅涧谏缣戋戬睑鹣笕鲣鞯将浆蒋桨奖讲酱绛缰胶浇骄娇搅铰矫侥脚饺缴绞轿较挢峤鹪鲛阶节洁结诫届疖颌鲒紧锦仅谨进晋烬尽劲荆茎卺荩馑缙赆觐鲸惊经颈静镜径痉竞净刭泾迳弪胫靓纠厩旧阄鸠鹫驹举据锯惧剧讵屦榉飓钜锔窭龃鹃绢锩镌隽觉决绝谲珏钧军骏皲开凯剀垲忾恺铠锴龛闶钪铐颗壳课骒缂轲钶锞颔垦恳龈铿抠库裤喾块侩郐哙脍宽狯髋矿旷况诓诳邝圹纩贶亏岿窥馈溃匮蒉愦聩篑阃锟鲲扩阔蛴蜡腊莱来赖崃徕涞濑赉睐铼癞籁蓝栏拦篮阑兰澜谰揽览懒缆烂滥岚榄斓镧褴琅阆锒捞劳涝唠崂铑铹痨乐鳓镭垒类泪诔缧篱狸离鲤礼丽厉励砾历沥隶俪郦坜苈莅蓠呖逦骊缡枥栎轹砺锂鹂疠粝跞雳鲡鳢俩联莲连镰怜涟帘敛脸链恋炼练蔹奁潋琏殓裢裣鲢粮凉两辆谅魉疗辽镣缭钌鹩猎临邻鳞凛赁蔺廪檩辚躏龄铃灵岭领绫棂蛏鲮馏刘浏骝绺镏鹨龙聋咙笼垄拢陇茏泷珑栊胧砻楼娄搂篓偻蒌喽嵝镂瘘耧蝼髅芦卢颅庐炉掳卤虏鲁赂禄录陆垆撸噜闾泸渌栌橹轳辂辘氇胪鸬鹭舻鲈峦挛孪滦乱脔娈栾鸾銮抡轮伦仑沦纶论囵萝罗逻锣箩骡骆络荦猡泺椤脶镙驴吕铝侣屡缕虑滤绿榈褛锊呒妈玛码蚂马骂吗唛嬷杩买麦卖迈脉劢瞒馒蛮满谩缦镘颡鳗猫锚铆贸麽没镁门闷们扪焖懑钔锰梦眯谜弥觅幂芈谧猕祢绵缅渑腼黾庙缈缪灭悯闽闵缗鸣铭谬谟蓦馍殁镆谋亩钼呐钠纳难挠脑恼闹铙讷馁内拟腻铌鲵撵辇鲶酿鸟茑袅聂啮镊镍陧蘖嗫颟蹑柠狞宁拧泞苎咛聍钮纽脓浓农侬哝驽钕诺傩疟欧鸥殴呕沤讴怄瓯盘蹒庞抛疱赔辔喷鹏纰罴铍骗谝骈飘缥频贫嫔苹凭评泼颇钋扑铺朴谱镤镨栖脐齐骑岂启气弃讫蕲骐绮桤碛颀颃鳍牵钎铅迁签谦钱钳潜浅谴堑佥荨悭骞缱椠钤枪呛墙蔷强抢嫱樯戗炝锖锵镪羟跄锹桥乔侨翘窍诮谯荞缲硗跷窃惬锲箧钦亲寝锓轻氢倾顷请庆揿鲭琼穷茕蛱巯赇虮鳅趋区躯驱龋诎岖阒觑鸲颧权劝诠绻辁铨却鹊确阕阙悫让饶扰绕荛娆桡热韧认纫饪轫荣绒嵘蝾缛铷颦软锐蚬闰润洒萨飒鳃赛伞毵糁丧骚扫缫涩啬铯穑杀刹纱铩鲨筛晒酾删闪陕赡缮讪姗骟钐鳝墒伤赏垧殇觞烧绍赊摄慑设厍滠畲绅审婶肾渗诜谂渖声绳胜师狮湿诗时蚀实识驶势适释饰视试谥埘莳弑轼贳铈鲥寿兽绶枢输书赎属术树竖数摅纾帅闩双谁税顺说硕烁铄丝饲厮驷缌锶鸶耸怂颂讼诵擞薮馊飕锼苏诉肃谡稣虽随绥岁谇孙损笋荪狲缩琐锁唢睃獭挞闼铊鳎台态钛鲐摊贪瘫滩坛谭谈叹昙钽锬顸汤烫傥饧铴镗涛绦讨韬铽腾誊锑题体屉缇鹈阗条粜龆鲦贴铁厅听烃铜统恸头钭秃图钍团抟颓蜕饨脱鸵驮驼椭箨鼍袜娲腽弯湾顽万纨绾网辋韦违围为潍维苇伟伪纬谓卫诿帏闱沩涠玮韪炜鲔温闻纹稳问阌瓮挝蜗涡窝卧莴龌呜钨乌诬无芜吴坞雾务误邬庑怃妩骛鹉鹜锡牺袭习铣戏细饩阋玺觋虾辖峡侠狭厦吓硖鲜纤贤衔闲显险现献县馅羡宪线苋莶藓岘猃娴鹇痫蚝籼跹厢镶乡详响项芗饷骧缃飨萧嚣销晓啸哓潇骁绡枭箫协挟携胁谐写泻谢亵撷绁缬锌衅兴陉荥凶汹锈绣馐鸺虚嘘须许叙绪续诩顼轩悬选癣绚谖铉镟学谑泶鳕勋询寻驯训讯逊埙浔鲟压鸦鸭哑亚讶垭娅桠氩阉烟盐严岩颜阎艳厌砚彦谚验厣赝俨兖谳恹闫酽魇餍鼹鸯杨扬疡阳痒养样炀瑶摇尧遥窑谣药轺鹞鳐爷页业叶靥谒邺晔烨医铱颐遗仪蚁艺亿忆义诣议谊译异绎诒呓峄饴怿驿缢轶贻钇镒镱瘗舣荫阴银饮隐铟瘾樱婴鹰应缨莹萤营荧蝇赢颖茔莺萦蓥撄嘤滢潆璎鹦瘿颏罂哟拥佣痈踊咏镛优忧邮铀犹诱莸铕鱿舆鱼渔娱与屿语狱誉预驭伛俣谀谕蓣嵛饫阈妪纡觎欤钰鹆鹬龉鸳渊辕园员圆缘远橼鸢鼋约跃钥粤悦阅钺郧匀陨运蕴酝晕韵郓芸恽愠纭韫殒氲杂灾载攒暂赞瓒趱錾赃脏驵凿枣责择则泽赜啧帻箦贼谮赠综缯轧铡闸栅诈斋债毡盏斩辗崭栈战绽谵张涨帐账胀赵诏钊蛰辙锗这谪辄鹧贞针侦诊镇阵浈缜桢轸赈祯鸩挣睁狰争帧症郑证诤峥钲铮筝织职执纸挚掷帜质滞骘栉栀轵轾贽鸷蛳絷踬踯觯钟终种肿众锺诌轴皱昼骤纣绉猪诸诛烛瞩嘱贮铸驻伫槠铢专砖转赚啭馔颞桩庄装妆壮状锥赘坠缀骓缒谆准着浊诼镯兹资渍谘缁辎赀眦锱龇鲻踪总纵偬邹诹驺鲰诅组镞钻缵躜鳟翱并卜沉丑淀迭斗范干皋硅柜后伙秸杰诀夸里凌么霉捻凄扦圣尸抬涂洼喂污锨咸蝎彝涌游吁御愿岳云灶扎札筑于志注凋讠谫郄勐凼坂垅垴埯埝苘荬荮莜莼菰藁揸吒吣咔咝咴噘噼嚯幞岙嵴彷徼犸狍馀馇馓馕愣憷懔丬溆滟溷漤潴澹甯纟绔绱珉枧桊桉槔橥轱轷赍肷胨飚煳煅熘愍淼砜磙眍钚钷铘铞锃锍锎锏锘锝锪锫锿镅镎镢镥镩镲稆鹋鹛鹱疬疴痖癯裥襁耢颥螨麴鲅鲆鲇鲞鲴鲺鲼鳊鳋鳘鳙鞒鞴齄";
    private static final String fan ="錒皚藹礙愛噯嬡璦曖靄諳銨鵪骯襖奧媼驁鰲壩罷鈀擺敗唄頒辦絆鈑幫綁鎊謗剝飽寶報鮑鴇齙輩貝鋇狽備憊鵯賁錛繃筆畢斃幣閉蓽嗶潷鉍篳蹕邊編貶變辯辮芐緶籩標驃颮飆鏢鑣鰾鱉別癟瀕濱賓擯儐繽檳殯臏鑌髕鬢餅稟撥缽鉑駁餑鈸鵓補鈽財參蠶殘慚慘燦驂黲蒼艙倉滄廁側冊測惻層詫鍤儕釵攙摻蟬饞讒纏鏟產闡顫囅諂讖蕆懺嬋驏覘禪鐔場嘗長償腸廠暢倀萇悵閶鯧鈔車徹硨塵陳襯傖諶櫬磣齔撐稱懲誠騁棖檉鋮鐺癡遲馳恥齒熾飭鴟沖衝蟲寵銃疇躊籌綢儔幬讎櫥廚鋤雛礎儲觸處芻絀躕傳釧瘡闖創愴錘綞純鶉綽輟齪辭詞賜鶿聰蔥囪從叢蓯驄樅湊輳躥竄攛錯銼鹺達噠韃帶貸駘紿擔單鄲撣膽憚誕彈殫賧癉簞當擋黨蕩檔讜碭襠搗島禱導盜燾燈鄧鐙敵滌遞締糴詆諦綈覿鏑顛點墊電巔鈿癲釣調銚鯛諜疊鰈釘頂錠訂鋌丟銩東動棟凍崠鶇竇犢獨讀賭鍍瀆櫝牘篤黷鍛斷緞籪兌隊對懟鐓噸頓鈍燉躉奪墮鐸鵝額訛惡餓諤堊閼軛鋨鍔鶚顎顓鱷誒兒爾餌貳邇鉺鴯鮞發罰閥琺礬釩煩販飯訪紡鈁魴飛誹廢費緋鐨鯡紛墳奮憤糞僨豐楓鋒風瘋馮縫諷鳳灃膚輻撫輔賦復負訃婦縛鳧駙紱紼賻麩鮒鰒釓該鈣蓋賅桿趕稈贛尷搟紺岡剛鋼綱崗戇鎬睪誥縞鋯擱鴿閣鉻個紇鎘潁給亙賡綆鯁龔宮鞏貢鉤溝茍構購夠詬緱覯蠱顧詁轂鈷錮鴣鵠鶻剮掛鴰摑關觀館慣貫詿摜鸛鰥廣獷規歸龜閨軌詭貴劊匭劌媯檜鮭鱖輥滾袞緄鯀鍋國過堝咼幗槨蟈鉿駭韓漢闞絎頡號灝顥閡鶴賀訶闔蠣橫轟鴻紅黌訌葒閎鱟壺護滬戶滸鶘嘩華畫劃話驊樺鏵懷壞歡環還緩換喚瘓煥渙奐繯鍰鯇黃謊鰉揮輝毀賄穢會燴匯諱誨繪詼薈噦澮繢琿暉葷渾諢餛閽獲貨禍鈥鑊擊機積饑跡譏雞績緝極輯級擠幾薊劑濟計記際繼紀訐詰薺嘰嚌驥璣覬齏磯羈蠆躋霽鱭鯽夾莢頰賈鉀價駕郟浹鋏鎵蟯殲監堅箋間艱緘繭檢堿鹼揀撿簡儉減薦檻鑒踐賤見鍵艦劍餞漸濺澗諫縑戔戩瞼鶼筧鰹韉將漿蔣槳獎講醬絳韁膠澆驕嬌攪鉸矯僥腳餃繳絞轎較撟嶠鷦鮫階節潔結誡屆癤頜鮚緊錦僅謹進晉燼盡勁荊莖巹藎饉縉贐覲鯨驚經頸靜鏡徑痙競凈剄涇逕弳脛靚糾廄舊鬮鳩鷲駒舉據鋸懼劇詎屨櫸颶鉅鋦窶齟鵑絹錈鐫雋覺決絕譎玨鈞軍駿皸開凱剴塏愾愷鎧鍇龕閌鈧銬顆殼課騍緙軻鈳錁頷墾懇齦鏗摳庫褲嚳塊儈鄶噲膾寬獪髖礦曠況誆誑鄺壙纊貺虧巋窺饋潰匱蕢憒聵簣閫錕鯤擴闊蠐蠟臘萊來賴崍徠淶瀨賚睞錸癩籟藍欄攔籃闌蘭瀾讕攬覽懶纜爛濫嵐欖斕鑭襤瑯閬鋃撈勞澇嘮嶗銠鐒癆樂鰳鐳壘類淚誄縲籬貍離鯉禮麗厲勵礫歷瀝隸儷酈壢藶蒞蘺嚦邐驪縭櫪櫟轢礪鋰鸝癘糲躒靂鱺鱧倆聯蓮連鐮憐漣簾斂臉鏈戀煉練蘞奩瀲璉殮褳襝鰱糧涼兩輛諒魎療遼鐐繚釕鷯獵臨鄰鱗凜賃藺廩檁轔躪齡鈴靈嶺領綾欞蟶鯪餾劉瀏騮綹鎦鷚龍聾嚨籠壟攏隴蘢瀧瓏櫳朧礱樓婁摟簍僂蔞嘍嶁鏤瘺耬螻髏蘆盧顱廬爐擄鹵虜魯賂祿錄陸壚擼嚕閭瀘淥櫨櫓轤輅轆氌臚鸕鷺艫鱸巒攣孿灤亂臠孌欒鸞鑾掄輪倫侖淪綸論圇蘿羅邏鑼籮騾駱絡犖玀濼欏腡鏍驢呂鋁侶屢縷慮濾綠櫚褸鋝嘸媽瑪碼螞馬罵嗎嘜嬤榪買麥賣邁脈勱瞞饅蠻滿謾縵鏝顙鰻貓錨鉚貿麼沒鎂門悶們捫燜懣鍆錳夢瞇謎彌覓冪羋謐獼禰綿緬澠靦黽廟緲繆滅憫閩閔緡鳴銘謬謨驀饃歿鏌謀畝鉬吶鈉納難撓腦惱鬧鐃訥餒內擬膩鈮鯢攆輦鯰釀鳥蔦裊聶嚙鑷鎳隉蘗囁顢躡檸獰寧擰濘苧嚀聹鈕紐膿濃農儂噥駑釹諾儺瘧歐鷗毆嘔漚謳慪甌盤蹣龐拋皰賠轡噴鵬紕羆鈹騙諞駢飄縹頻貧嬪蘋憑評潑頗釙撲鋪樸譜鏷鐠棲臍齊騎豈啟氣棄訖蘄騏綺榿磧頎頏鰭牽釬鉛遷簽謙錢鉗潛淺譴塹僉蕁慳騫繾槧鈐槍嗆墻薔強搶嬙檣戧熗錆鏘鏹羥蹌鍬橋喬僑翹竅誚譙蕎繰磽蹺竊愜鍥篋欽親寢鋟輕氫傾頃請慶撳鯖瓊窮煢蛺巰賕蟣鰍趨區軀驅齲詘嶇闃覷鴝顴權勸詮綣輇銓卻鵲確闋闕愨讓饒擾繞蕘嬈橈熱韌認紉飪軔榮絨嶸蠑縟銣顰軟銳蜆閏潤灑薩颯鰓賽傘毿糝喪騷掃繅澀嗇銫穡殺剎紗鎩鯊篩曬釃刪閃陜贍繕訕姍騸釤鱔墑傷賞坰殤觴燒紹賒攝懾設厙灄畬紳審嬸腎滲詵諗瀋聲繩勝師獅濕詩時蝕實識駛勢適釋飾視試謚塒蒔弒軾貰鈰鰣壽獸綬樞輸書贖屬術樹豎數攄紓帥閂雙誰稅順說碩爍鑠絲飼廝駟緦鍶鷥聳慫頌訟誦擻藪餿颼鎪蘇訴肅謖穌雖隨綏歲誶孫損筍蓀猻縮瑣鎖嗩脧獺撻闥鉈鰨臺態鈦鮐攤貪癱灘壇譚談嘆曇鉭錟頇湯燙儻餳鐋鏜濤絳討韜鋱騰謄銻題體屜緹鵜闐條糶齠鰷貼鐵廳聽烴銅統慟頭鈄禿圖釷團摶頹蛻飩脫鴕馱駝橢籜鼉襪媧膃彎灣頑萬紈綰網輞韋違圍為濰維葦偉偽緯謂衛諉幃闈溈潿瑋韙煒鮪溫聞紋穩問閿甕撾蝸渦窩臥萵齷嗚鎢烏誣無蕪吳塢霧務誤鄔廡憮嫵騖鵡鶩錫犧襲習銑戲細餼鬩璽覡蝦轄峽俠狹廈嚇硤鮮纖賢銜閑顯險現獻縣餡羨憲線莧薟蘚峴獫嫻鷴癇蠔秈躚廂鑲鄉詳響項薌餉驤緗饗蕭囂銷曉嘯嘵瀟驍綃梟簫協挾攜脅諧寫瀉謝褻擷紲纈鋅釁興陘滎兇洶銹繡饈鵂虛噓須許敘緒續詡頊軒懸選癬絢諼鉉鏇學謔澩鱈勛詢尋馴訓訊遜塤潯鱘壓鴉鴨啞亞訝埡婭椏氬閹煙鹽嚴巖顏閻艷厭硯彥諺驗厴贗儼兗讞懨閆釅魘饜鼴鴦楊揚瘍陽癢養樣煬瑤搖堯遙窯謠藥軺鷂鰩爺頁業葉靨謁鄴曄燁醫銥頤遺儀蟻藝億憶義詣議誼譯異繹詒囈嶧飴懌驛縊軼貽釔鎰鐿瘞艤蔭陰銀飲隱銦癮櫻嬰鷹應纓瑩螢營熒蠅贏穎塋鶯縈鎣攖嚶瀅瀠瓔鸚癭頦罌喲擁傭癰踴詠鏞優憂郵鈾猶誘蕕銪魷輿魚漁娛與嶼語獄譽預馭傴俁諛諭蕷崳飫閾嫗紆覦歟鈺鵒鷸齬鴛淵轅園員圓緣遠櫞鳶黿約躍鑰粵悅閱鉞鄖勻隕運蘊醞暈韻鄆蕓惲慍紜韞殞氳雜災載攢暫贊瓚趲鏨贓臟駔鑿棗責擇則澤賾嘖幘簀賊譖贈綜繒軋鍘閘柵詐齋債氈盞斬輾嶄棧戰綻譫張漲帳賬脹趙詔釗蟄轍鍺這謫輒鷓貞針偵診鎮陣湞縝楨軫賑禎鴆掙睜猙爭幀癥鄭證諍崢鉦錚箏織職執紙摯擲幟質滯騭櫛梔軹輊贄鷙螄縶躓躑觶鐘終種腫眾鍾謅軸皺晝驟紂縐豬諸誅燭矚囑貯鑄駐佇櫧銖專磚轉賺囀饌顳樁莊裝妝壯狀錐贅墜綴騅縋諄準著濁諑鐲茲資漬諮緇輜貲眥錙齜鯔蹤總縱傯鄒諏騶鯫詛組鏃鉆纘躦鱒翺並蔔沈醜澱叠鬥範幹臯矽櫃後夥稭傑訣誇裏淩麽黴撚淒扡聖屍擡塗窪餵汙鍁鹹蠍彜湧遊籲禦願嶽雲竈紮劄築於誌註雕訁譾郤猛氹阪壟堖垵墊檾蕒葤蓧蒓菇槁摣咤唚哢噝噅撅劈謔襆嶴脊仿僥獁麅餘餷饊饢楞怵懍爿漵灩混濫瀦淡寧糸絝緔瑉梘棬案橰櫫軲軤賫膁腖飈糊煆溜湣渺碸滾瞘鈈鉕鋣銱鋥鋶鐦鐧鍩鍀鍃錇鎄鎇鎿鐝鑥鑹鑔穭鶓鶥鸌癧屙瘂臒襇繈耮顬蟎麯鮁鮃鮎鯗鯝鯴鱝鯿鰠鰵鱅鞽韝齇";
    public static String toFan(String str) {
        StringBuffer sb = new StringBuffer();
        int n;
        for (int i = 0; i < str.length(); i++) {
            n = jian.indexOf(str.charAt(i));
            sb.append(n >= 0 ? fan.charAt(n) : str.charAt(i));
        }
        return sb.toString();
    }
    public static String toJian(String str) {
        StringBuffer sb = new StringBuffer();
        int n;
        for (int i = 0; i < str.length(); i++) {
            n = fan.indexOf(str.charAt(i));
            sb.append(n >= 0 ? jian.charAt(n) : str.charAt(i));
        }
        return sb.toString();
    }
    public static String toSymbol(int n) {
        String str = "";
        while (n >= 0) {
            str = (char) (n % 26 + 65) + str;
            n = n / 26 - 1;
        }
        return str;
    }
    public static boolean isUrl(String str) {
        return str.startsWith("http:") || str.startsWith("https:") || str.startsWith("mailto:");
    }
    public static boolean isImg(String str) {
        return str.startsWith("data:image/") 
                || str.endsWith(".png")
                || str.endsWith(".jpg")
                || str.endsWith(".gif");
    }
    public static String htmEncode(String str) {
        StringBuffer sb = new StringBuffer();
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c == ' ') {
                sb.append("&nbsp;");
            } else if (c == '\n') {
                sb.append("<br>");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    public static List csvList(String txt) {
    	if (txt.length() > 0) {
    		ArrayList rows = new ArrayList();
    		ArrayList cols = new ArrayList();
    		rows.add(cols);
    		char c;
    		boolean strBegin = false;
    		StringBuffer sb = new StringBuffer();
    		for (int i = 0; i < txt.length(); i++) {
    			c = txt.charAt(i);
    			if (strBegin) {
    				if (c == '"') {
    					if (i + 1 < txt.length()) {
    						if (txt.charAt(i + 1) == '"') {
    							sb.append(c);
    							i++;
    						} else {
    							strBegin = false;
    						}
    					} else {
    						strBegin = false;
    					}
    				} else {
    					sb.append(c);
    				}
    			} else {
    				if (c == ',') {
    					cols.add(sb.toString());
    					sb.setLength(0);
    				} else if (c == '\n') {
    					cols.add(sb.toString());
    					sb.setLength(0);
    					cols = new ArrayList();
    					rows.add(cols);
    				} else if (c == '"') {
    					strBegin = true;
    				} else if (c != '\r') {
    					sb.append(c);
    				}
    			}
    		}
    		if (sb.length() > 0) {
    			cols.add(sb.toString());
    		}
    		return rows;
    	} else {
    		return new ArrayList();
    	}
    }
    public static String csvEncode(String str) {
    	if (str == null) {
    		return "";
    	}
        boolean b = false;
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == ',' || c == '\n' || c == '"') {
                b = true;
                break;
            }
        }
        if (b) {
            StringBuffer sb = new StringBuffer();
            sb.append('"');
            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);
                sb.append(c);
                if (c == '"') {
                    sb.append('"');
                }
            }
            sb.append('"');
            return sb.toString();
        } else {
            return str;
        }
        
    }
    public static String URLEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }
    public static String URLDecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }
    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean inRange(double d, String str) {
        try {
            int pos = str.indexOf('~');
            if (pos >= 0) {
                String str1 = str.substring(0, pos);
                String str2 = str.substring(pos + 1);
                if (str1.length() > 0) {
                    if (d < Double.parseDouble(str1)) {
                        return false;
                    }
                }
                if (str2.length() > 0) {
                    if (d > Double.parseDouble(str2)) {
                        return false;
                    }
                }
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean inEnum(String item, String items) {
    	if (item.equals(items)) {
    		return true;
    	} else {
    		String[] strs = items.split(",");
    		for (int i = 0; i < strs.length; i++) {
    			if (item.equals(strs[i])) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    public static String replaceHtmlTag(String str) {
        return replaceAll(str, new String[] {"<", ">", " ", "&", "\n"}, new String[] {"&lt;", "&gt;", "&nbsp;", "&amp;", "<br/>"});
    }
	public static final char CHAR_ONE = 0x2776;
	public static final char CHAR_UNCHECK = 0x2610;
	public static final char CHAR_CHECK = 0x2611;
	public static String formatByte(long length) {
		if (length < 1024) {
			return length + "B";
		} else if (length < 1024 * 1024) {
			return String.valueOf(Math.round(length / (double) 10.24) / (double) 100) + "KB";
		} else {
			return String.valueOf(Math.round(length / (double) 10.24 / 1024) / (double) 100) + "MB";
		}
	}
	public static String[] csvSplit(String str) {
		List list = csvList(str);
		if (list.size() > 0) {
			List cols = (List) list.get(0);
			String[] strs = new String[cols.size()];
			for (int i = 0; i < strs.length; i++) {
				strs[i] = (String) cols.get(i);
			}
			return strs;
		} else {
			return new String[0];
		}
	}
    public static String csvJoin(String[] strs) {
        if (strs.length > 0) {
            StringBuffer sb = new StringBuffer();
            try {
            	for (int i = 0; i < strs.length; i++) {
            		if (i > 0) {
            			sb.append(",");
            		}
            		sb.append(StrUtil.csvEncode(strs[i]));
            	}
                return sb.toString();
            } catch (Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }
	public static boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}
	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

}
