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

import com.hg.util.HgException;


public class Parser {
    /**
     * 字符串编码
     * @param str
     * @return
     */
    public static String encode(String str) {
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
    /**
     * 字符串解码
     * @param str
     * @return
     */
    public static String decode(String str) {
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
                    } else if (c == 'u') {
                    	if (i + 6 < str.length()) {
                    		try {
                    			sb.append((char) Integer.decode("0x" + str.substring(i + 2, i + 6)).intValue());
                    		} catch (Exception e) {
                    			sb.append(str.substring(i + 2, i + 6));
                    		}
                    		i += 4;
                    	}
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
     * 分割字符串
     * @param str
     * @return
     */
    public static String[] split(String str, char sepChar) {
        ArrayList strList = new ArrayList();
        //分割串
        if (str.length() > 0) {
            char c;
            //前段位置
            int lastIndex = 0;
            //字符串开始
            boolean strBegin = false;
            //表达式开始
            boolean expBegin = false;
            //单引号开始
            boolean singleQuotBegin = false;
            //括号数量
            int pars = 0;
            //去掉前后空格
            String tmpExpStr = str.trim();
            boolean bSpace = false;
            for (int i = 0; i < tmpExpStr.length(); i++) {
                c = tmpExpStr.charAt(i);
                if (sepChar == ' ') {
                    //如果分割符为' ',多个当一个
                    if (bSpace) {
                        if (c == ' ') {
                            continue;
                        } else {
                            bSpace = false;
                        }
                    }
                    if (c == ' ') {
                        bSpace = true;
                    }
                }
                if (c != '\'' && singleQuotBegin) {
                    strBegin = false;
                    singleQuotBegin = false;
                }
                if (c == '\'') {
                    if (!strBegin) {
                        strBegin = true;
                    } else {
                        if (singleQuotBegin) {
                            singleQuotBegin = false;
                        } else {
                            //可能是单引号的开始,也可能是字符串结束
                            singleQuotBegin = true;
                        }
                    }                    
                } else if (!strBegin && c == '(') {
                    pars++;
                } else if (!strBegin && c == ')') {
                    pars--;
                } else if (!strBegin && pars == 0) { //不在括号和字符串中间
                    if (c == ',') { //2008.8.6
                        expBegin = false;
                    }
                    if (c == '+' || c == '-' || c == '*' || c == '/') {
                        expBegin = true;
                    } else if (c == sepChar) {
                        if (!expBegin) {
                            strList.add(tmpExpStr.substring(lastIndex, i).trim());
                            lastIndex = i + 1;
                        }
                        if (expBegin) {
                            expBegin = false;
                        }
                    }
                }
            }
            if (lastIndex < tmpExpStr.length()) {
                strList.add(tmpExpStr.substring(lastIndex).trim());
            }
        }
        String[] strs = new String[strList.size()];
        for (int i = 0; i < strList.size(); i++) {
            strs[i] = (String) strList.get(i);
        }
        return strs;
    }
    /**
     * 空格分割字符串
     * @param str
     * @return
     */
    public static String[] split(String str) {
        return split(str, ' ');
    }
    /**
     * sql预处理，非字符串转大写，检测字符串和括号是否完整
     * @param sql
     * @return
     * @throws HgException 
     */
    public static String[] pretreat(String sql) throws HgException {
    	if (sql.length() == 0) return new String[] {""};
        ArrayList sqlList = new ArrayList();
        String[] strs;
        StringBuffer sbSql = new StringBuffer();
        sql = sql.trim();
        char c;
        //括号数量
        int pars = 0;
        //字符串开始
        int lastIndex = 0;
        //字符串开始
        boolean strBegin = false;
        //单引号开始
        boolean singleQuotBegin = false;
        //是否处理
        boolean pressed = true;
        for (int i = 0; i < sql.length(); i++) {
            c = sql.charAt(i);
            if (c != '\'' && singleQuotBegin) {
                if (strBegin) {
                    pressed = false;
                }
                strBegin = false;
                singleQuotBegin = false;
            }
            if (c == '\'') {
                if (!strBegin) {
                    if (!strBegin) {
                        pressed = false;
                    }
                    strBegin = true;
                } else {
                    if (singleQuotBegin) {
                        singleQuotBegin = false;
                    } else {
                        //可能是单引号的开始,也可能是字符串结束
                        singleQuotBegin = true;
                    }
                }                    
            } else if (!strBegin && c == '(') {
                pars++;
            } else if (!strBegin && c == ')') {
                pars--;
            }
            if (strBegin && c == '\\' && i + 1 < sql.length()) {
                if (sql.charAt(i + 1) == '\'') {
                    sql = sql.substring(0, i) + '\'' + sql.substring(i + 1);
                    i++;
                    continue;
                } else if (sql.charAt(i + 1) == '\\') {
                    i++;
                    continue;
                }
            }
            if (strBegin && !pressed) {
                sbSql.append(upperIgnoreDQ(sql.substring(lastIndex, i)));
                lastIndex = i;
                pressed = true;
            }
            if (!strBegin && !pressed) {
                sbSql.append(sql.substring(lastIndex, i));
                lastIndex = i;
                pressed = true;
            }
            if (!strBegin && c == '-' && sql.charAt(i + 1) == '-') {
                //单行注释开始,将注释前的字符串放入buffer
                sbSql.append(upperIgnoreDQ(sql.substring(lastIndex, i)));
                if (sql.indexOf('\n', i + 1) >= 0) {
                    //忽略注释
                    i = sql.indexOf('\n', i + 1);
                    lastIndex = i + 1;
                } else { //全部为注释
                    i = sql.length();
                    lastIndex = i;
                }
            }
            if (!strBegin && c == '/' && i + 1 < sql.length() && sql.charAt(i + 1) == '*') {
                //块行注释开始,将注释前的字符串放入buffer
                sbSql.append(upperIgnoreDQ(sql.substring(lastIndex, i)));
                if (sql.indexOf("*/", i + 1) >= 0) {
                    //忽略注释
                    i = sql.indexOf("*/", i + 1) + 1;
                    lastIndex = i + 1;
                } else { //全部为注释
                    i = sql.length();
                    lastIndex = i;
                }
            }
            if (!strBegin && c == ';') {
                if (pars > 0) {
                    throw new HgException("括号没有结束");
                }
                sbSql.append(upperIgnoreDQ(sql.substring(lastIndex, i)));
                lastIndex = i + 1;
                sqlList.add(sbSql.toString());
                sbSql.setLength(0);
            }
            if (!strBegin && c == '$' && i + 1 < sql.length() && sql.charAt(i + 1) == '{') { //嵌入表达式转xv函数
                sbSql.append(upperIgnoreDQ(sql.substring(lastIndex, i)));
            	sbSql.append("XV(");
            	i += 2;
            	StringBuffer sb = new StringBuffer();
            	while (i < sql.length()) {
            		c = sql.charAt(i);
            		if (c == '}') {
            			sbSql.append(encode(sb.toString())).append(")");
            			break;
            		} else {
            			sb.append(c);
            		}
            		i++;
            	}
            	lastIndex = i + 1;
            }
        }
        if (strBegin && !singleQuotBegin) {
            throw new HgException("字符串没有结束");
        }
        if (pars > 0) {
            throw new HgException("括号没有结束");
        }
        if (lastIndex < sql.length()) {
            if (strBegin && singleQuotBegin) {
                sbSql.append(sql.substring(lastIndex));
            } else {
                sbSql.append(upperIgnoreDQ(sql.substring(lastIndex)));
            }
            sqlList.add(sbSql.toString());
            sbSql.setLength(0);
        }
        if (sbSql.length() > 0) {
            sqlList.add(sbSql.toString());
        }
        strs = new String[sqlList.size()];
        for (int i = 0; i < strs.length; i++) {
            strs[i] = transCaseInStr((String) sqlList.get(i));
        }
        return strs;
    }
    /**
     * 忽略双引号中的内容转为大写
     * @param str
     * @return
     */
    private static String upperIgnoreDQ(String str) {
        if (str.indexOf("\"") >= 0) {
        	StringBuffer sb = new StringBuffer();
        	boolean b = false;
        	for (int i = 0; i < str.length(); i++) {
        		if (str.charAt(i) == '\"') {
        			b = !b;
        		} else {
        			sb.append(!b ? Character.toUpperCase(str.charAt(i)) : str.charAt(i));
        		}
        	}
            str = sb.toString();
        } else {
            str = str.toUpperCase();
        }
        return str.replace('\n', ' ').replace('\r', ' ');
    }
    /**
     * 转换字符串中的case语句
     * @param str
     * @return
     * @throws HgException
     */
    private static String transCaseInStr(String str) throws HgException {
        String resStr = str;
        if (str.indexOf("CASE ") >= 0) { //可能存在case语句
            StringBuffer sb = new StringBuffer();
            sb.append(' ').append(str).append(' ');
            str = sb.toString();
            sb.setLength(0);

            //字符串开始
            boolean strBegin = false;
            //case开始
            boolean caseBegin = false;
            //单引号开始
            boolean singleQuotBegin = false;
            //前一个","位置
            int lastCommaIndex = 0;
            //字符
            char c;
            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);
                if (c != '\'' && singleQuotBegin) {
                    strBegin = false;
                    singleQuotBegin = false;
                }
                if (c == '\'') {
                    if (!strBegin) {
                        strBegin = true;
                    } else {
                        if (singleQuotBegin) {
                            singleQuotBegin = false;
                        } else {
                            //可能是单引号的开始,也可能是字符串结束
                            singleQuotBegin = true;
                        }
                    }
                }
                if (!strBegin) {
                    if (!Character.isLetterOrDigit(c) 
                            && i + 6 < str.length() 
                            && str.substring(i + 1, i + 6).equals("CASE ")) { //case开始
                        if (caseBegin) {
                            throw new HgException("case语句不允许嵌套");
                        }
                        caseBegin = true;
                        sb.append(str.substring(lastCommaIndex, i + 1));
                        lastCommaIndex = i + 1;
                        i += 6;
                    } else if (caseBegin && (i + 4 == str.length() 
                            && str.substring(i, i + 4).equals(" END")
                            || i + 4 < str.length() 
                            && str.substring(i, i + 4).equals(" END") 
                            && !Character.isLetterOrDigit(str.charAt(i + 4)))) { //case结束
                        caseBegin = false;
                        sb.append(parseCase(str.substring(lastCommaIndex, i + 4)));
                        lastCommaIndex = i + 4;
                    }
                }
            }
            if (lastCommaIndex < str.length()) {
                sb.append(str.substring(lastCommaIndex));
            }
            resStr = sb.toString();
        }
        return resStr;
    } 
    /**
     * 将case表达式转换为decode或case函数
     * @param str
     * @return
     */
    private static String parseCase(String str) {
        str = str.trim().substring(4).trim();
        StringBuffer sb = new StringBuffer();
        if (str.startsWith("WHEN ")) { //case
            sb.append("CASE(");
            str = str.substring(5);
            //将when和then之间的替换为字符串表达式
            //将when then else替换为“,”
            //字符串开始
            boolean strBegin = false;
            //单引号开始
            boolean singleQuotBegin = false;
            //括号层数
            int pars = 0;
            //前一个","位置
            int lastCommaIndex = 0;
            //字符
            char c;
            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);
                if (c != '\'' && singleQuotBegin) {
                    strBegin = false;
                    singleQuotBegin = false;
                }
                if (c == '\'') {
                    if (!strBegin) {
                        strBegin = true;
                    } else {
                        if (singleQuotBegin) {
                            singleQuotBegin = false;
                        } else {
                            //可能是单引号的开始,也可能是字符串结束
                            singleQuotBegin = true;
                        }
                    }
                } else if (!strBegin && c == '(') {
                    pars++;
                } else if (!strBegin && c == ')') {
                    pars--;
                }
                if (!strBegin && pars == 0) {
                    if (i + 6 < str.length() && (str.substring(i, i + 6).equals(" WHEN ") 
                            || str.substring(i, i + 6).equals(" ELSE "))) { //数学表达式
                        if (sb.length() > 5) sb.append(",");
                        sb.append(str.substring(lastCommaIndex, i));
                        lastCommaIndex = i + 6;
                        i += 5;
                    } else if (i + 6 < str.length() && str.substring(i, i + 6).equals(" THEN ")) { //逻辑表达式
                        if (sb.length() > 5) sb.append(",");
                        sb.append(encode(str.substring(lastCommaIndex, i)));
                        lastCommaIndex = i + 6;
                        i += 5;
                    } else if (i + 4 == str.length() && str.substring(i, i + 4).equals(" END")) {
                        if (sb.length() > 5) sb.append(",");
                        sb.append(str.substring(lastCommaIndex, i));
                        break;
                    }
                }
            }
        } else { //decode
            sb.append("DECODE(");
            //将when then else替换为“,”
            //字符串开始
            boolean strBegin = false;
            //单引号开始
            boolean singleQuotBegin = false;
            //括号层数
            int pars = 0;
            //前一个","位置
            int lastCommaIndex = 0;
            //字符
            char c;
            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);
                if (c != '\'' && singleQuotBegin) {
                    strBegin = false;
                    singleQuotBegin = false;
                }
                if (c == '\'') {
                    if (!strBegin) {
                        strBegin = true;
                    } else {
                        if (singleQuotBegin) {
                            singleQuotBegin = false;
                        } else {
                            //可能是单引号的开始,也可能是字符串结束
                            singleQuotBegin = true;
                        }
                    }
                } else if (!strBegin && c == '(') {
                    pars++;
                } else if (!strBegin && c == ')') {
                    pars--;
                }
                if (!strBegin && pars == 0) {
                    if (i + 6 < str.length() && (str.substring(i, i + 6).equals(" WHEN ")
                        || str.substring(i, i + 6).equals(" THEN ")
                        || str.substring(i, i + 6).equals(" ELSE "))) { //参数用","分割
                        if (sb.length() > 7) sb.append(",");
                        sb.append(str.substring(lastCommaIndex, i));
                        lastCommaIndex = i + 6;
                        i += 5;
                    } else if (i + 4 == str.length() && str.substring(i, i + 4).equals(" END")) {
                        if (sb.length() > 7) sb.append(",");
                        sb.append(str.substring(lastCommaIndex, i));
                        break;
                    }
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }
    /**
     * 查找关键字字符串(可以不包含在括号中间)
     * @param sql
     * @param key
     */
    public static int keyAt(String sql, String key) {
        return keyAt(sql, key, true);
    }
    /**
     * 查找关键字字符串
     * @param sql
     * @param key
     * @param inPar 是否允许包含在括号中间
     */
    public static int keyAt(String sql, String key, boolean inPar) {
        int pos = -1;
        sql = " " + sql + " ";
        //key = " " + key + " ";
        char c;
        //字符串开始
        boolean strBegin = false;
        //单引号开始
        boolean singleQuotBegin = false;
        //括号数量
        int pars = 0;
        //去掉前后空格
        for (int i = 0; i < sql.length(); i++) {
            c = sql.charAt(i);
            if (c != '\'' && singleQuotBegin) {
                strBegin = false;
                singleQuotBegin = false;
            }
            if (c == '\'') {
                if (!strBegin) {
                    strBegin = true;
                } else {
                    if (singleQuotBegin) {
                        singleQuotBegin = false;
                    } else {
                        //可能是单引号的开始,也可能是字符串结束
                        singleQuotBegin = true;
                    }
                }                    
            } else if (!strBegin && c == '(') {
                pars++;
            } else if (!strBegin && c == ')') {
                pars--;
            } else if (!strBegin && (inPar || pars == 0)) { //不在字符串中间
                if (sql.length() > i + key.length() && sql.substring(i, i + key.length()).equals(key)) {
                    pos = i - 1;
                    break;
                }
            }
        }
        return pos;
    }
    /**
     * 关键字符串
     */
    public static final String[] keyStr = new String[] {
        //语句
        "CONNECT", "CREATE", "DROP", "ALTER", "SELECT", "SELECTX", "INSERT", "DELETE", "UPDATE", "TO",
        //对象
        "DATABASE", "USER", "SCHEMA", "TABLE", "VIEW", "PROCEDURE", "TRIGGER", "SEQUENCE", "SYNONYM", "INDEX", "PUBLIC", "SYS", "FIELD", "TYPE",
        //数据类型
        "CHAR", "VARCHAR", "VARCHAR2", "STRING", "INT", "DOUBLE", "NUMBER", "NUMERIC", "DATE", "ROW", "ROWSET",
        //限定
        "ALL", "DISTINCT", "FROM", "WHERE", "GROUP", "BY", "ORDER", "LIMIT", "DESC", "ASC", "HAVING", "AS", "LIKE", "IN",
        "CROSS", "FULL", "INNER", "LEFT", "RIGHT", "OUTER", "JOIN", "ON",
        "SOME", "IN", "SOME", "ANY", "EXISTS", "BETWEEN", 
        //操作符
        "AND", "NOT", "OR", "UNION", "MINUS", "INTERSECT", "NULL",
        //语法
        "REPLACE", "CASE", "WHEN", "THEN", "ELSE", "ELSIF", "BEGIN", "EXCEPTION", "END", "RETURN", "IF", "FOR", "WHILE", "LOOP", "RESULT", "RAISE",
        //保留变量
        "$OUTPUT", "RESULT", "ROWCOUNT", "DATA"
        };
    /**
     * 关键字符
     */
    public static final char[] keyChar = new char[] {
        '+', '-', '*', '/', '|', ' ', ',', '.', '(', ')',
        '=', '!', '>', '<', '%', '^', '@', '#', 
        '\n', '\t', '\'', '\"', '\\', '/', ':', ';', '?', '~'
        };

    /**
     * 分析参数
     * @param str
     * @return
     */
    public static String[] parseParam(String str) {
        String[] strs = Parser.split(str);
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].startsWith("\'") && strs[i].endsWith("\'")) {
                strs[i] = decode(strs[i]);
            } 
        }
        return strs;
    }
    public static String parseSchemaName(String name) {
        String schema = "";
        if (!name.startsWith("//") && !name.startsWith("$")) {
            if (name.indexOf('@') > 0) {
                schema = name.substring(name.indexOf('@') + 1);
                if (schema.length() == 0) {
                    if (name.indexOf(".") > 0) {
                        schema = name.substring(0, name.indexOf("."));
                    }
                }
            } else {
                if (name.indexOf(".") > 0) {
                    schema = name.substring(0, name.indexOf("."));
                }
            }
        }
        return schema;
    }
    public static String parseObjName(String name) {
        String objName = name;
        if (!name.startsWith("//") && !name.startsWith("$")) {
            if (name.indexOf('@') > 0) {
                objName = name.substring(0, name.indexOf('@') + 1);
                if (name.endsWith("@") && name.indexOf(".") > 0) {
                    objName = name.substring(name.indexOf(".") + 1);
                }
            } else {
                if (name.indexOf(".") > 0) {
                    objName = name.substring(name.indexOf(".") + 1);
                }
            }
        }
        return objName;
    }
    /**
     * 分解sql为程序段,分割符为"/"
     * @param sql
     * @return
     */
	public static String[] paraSplit(String sql) {
        ArrayList strList = new ArrayList();
        //分割串
        if (sql.length() > 0) {
            char c;
            //前段位置
            int lastIndex = 0;
            //字符串开始
            boolean strBegin = false;
            //单引号开始
            boolean singleQuotBegin = false;
            //去掉前后空格
            String tmpExpStr = sql;
            for (int i = 0; i < tmpExpStr.length(); i++) {
            	c = tmpExpStr.charAt(i);
                if (c != '\'' && singleQuotBegin) {
                    strBegin = false;
                    singleQuotBegin = false;
                }
                if (c == '\'') {
                    if (!strBegin) {
                        strBegin = true;
                    } else {
                        if (singleQuotBegin) {
                            singleQuotBegin = false;
                        } else {
                            //可能是单引号的开始,也可能是字符串结束
                            singleQuotBegin = true;
                        }
                    }                    
                } else if (!strBegin && c == '\n') { //不在字符串中间
                    if (i == tmpExpStr.length() && tmpExpStr.charAt(i + 1) == '/') {
                    	strList.add(tmpExpStr.substring(lastIndex, i).trim());
                    	lastIndex = i + 1;
                    	i += 1;
                    } else if (i < tmpExpStr.length() - 2 
                    		&& tmpExpStr.charAt(i + 1) == '/'
                    			&& (tmpExpStr.charAt(i + 2) == '\r'
                    				||tmpExpStr.charAt(i + 2) == '\n')) {
                    	strList.add(tmpExpStr.substring(lastIndex, i).trim());
                    	lastIndex = i + 2;
                    	i += 2;
                    }
                }
            }
            if (lastIndex < tmpExpStr.length()) {
                strList.add(tmpExpStr.substring(lastIndex).trim());
            }
        }
        String[] strs = new String[strList.size()];
        for (int i = 0; i < strList.size(); i++) {
            strs[i] = (String) strList.get(i);
        }
        return strs;
	} 
}
