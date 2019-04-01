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
import com.hg.util.To;

/**
 * 数学表达式
 * resultDataType可以指定为ROWSET,要求返回值为rowset
 * @author   whg
 */
public class MathExpression extends Expression {
    /**
     * 分析结果类型:整数
     */
    private static final int PARSE_RES_TYPE_BOOLEAN = 0;
    /**
     * 分析结果类型:整数
     */
    private static final int PARSE_RES_TYPE_LONG = 1;
    /**
     * 分析结果类型:浮点数
     */
    private static final int PARSE_RES_TYPE_DOUBLE = 2;
    /**
     * 分析结果类型:字符串
     */
    private static final int PARSE_RES_TYPE_STRING = 3;
    /**
     * 分析结果类型:变量
     */
    public static final int PARSE_RES_TYPE_VARIABLE = 4;
    /**
     * 分析结果类型:函数
     */
    private static final int PARSE_RES_TYPE_FUNC_EXPRESSION = 5;
    /**
     * 分析结果类型:表达式
     */
    private static final int PARSE_RES_TYPE_MATH_EXPRESSION = 6;
    /**
     * 分析结果类型:子查询
     */
    public static final int PARSE_RES_TYPE_QUERY = 7;
    /**
     * 分析结果类型
     */
    public int parseResType;
    /**
     * 左表达式
     */
    public Expression expA;
    /**
     * 右表达式
     */
    public Expression expB;
    /**
     * 操作符
     */
    private char operator;
    /**
     * 构造函数
     */
    public MathExpression(BlkExpression blkExp, String str) {
        super(blkExp, str);
    }
    public void setExpStr(String str) throws HgException {
    	this.expStr = str;
    	this.parse();
    } 
    /**
     * 分析
     */
    public void parse() throws HgException {
        //分割串
        if (expStr.length() > 0) {
            char c;
            //字符串开始
            boolean strBegin = false;
            //单引号开始
            boolean singleQuotBegin = false;
            //括号数量
            int pars = 0;
            //表示式a
            String expStrA = "";
            //表示式b
            String expStrB = "";
            //去掉前后空格
            String tmpExpStr = expStr.trim();
            //是否分开
            boolean split = false;
            while (true) {
                if (tmpExpStr.length() == 0) {
                	tmpExpStr = "''";
                }
                //用"*"、"/"分割
                for (int i = tmpExpStr.length() - 1; i > -1 ; i--) {
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
                    } else if (!strBegin && c == '(') {
                        pars++;
                    } else if (!strBegin && c == ')') {
                        pars--;
                    } else if (!strBegin && pars == 0 && (c == '+' || c == '-' || c == '|')) {
                        this.operator = c;
                        expStrB = tmpExpStr.substring(i + 1);
                        if (c == '|' && i - 1 >= 0 && tmpExpStr.charAt(i - 1) == '|') {
                        	i--;
                        }
                        expStrA = tmpExpStr.substring(0, i);
                        split = true;
                        break;
                    }
                }
                if (!split) {//用"*"、"/"分割
                    for (int i = tmpExpStr.length() - 1; i > -1 ; i--) {
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
                        } else if (!strBegin && c == '(') {
                            pars++;
                        } else if (!strBegin && c == ')') {
                            pars--;
                        } else if (!strBegin && pars == 0 && (c == '*' || c == '/' || c == '%')) {
                            this.operator = c;
                            expStrA = tmpExpStr.substring(0, i);
                            expStrB = tmpExpStr.substring(i + 1);
                            split = true;
                            break;
                        }
                    }
                }
                if (!split) {
                    if (tmpExpStr.charAt(0) == '(' && tmpExpStr.charAt(tmpExpStr.length() - 1) == ')') {
                        //被括号包含,去掉括号
                        tmpExpStr = tmpExpStr.substring(1, tmpExpStr.length() - 1).trim();
                        if (tmpExpStr.startsWith("SELECT ") || tmpExpStr.startsWith("SELECTX ") || tmpExpStr.startsWith("#")) {
                            //子查询
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    //分割完成
                    break;
                }
            }
            if (!split) {//独立单元
                //判断是否函数
                if (tmpExpStr.charAt(0) == '\'' && tmpExpStr.charAt(tmpExpStr.length() - 1) == '\'') {
                    //字符串
                    this.resultDataType = DbTypes.VARCHAR;
                    this.parseResType = PARSE_RES_TYPE_STRING;
                    this.parseRes = Parser.decode(tmpExpStr);
                } else {
                    if (tmpExpStr.indexOf('.') >= 0) {
                        //可能是小数
                        try {
                            this.parseRes = Double.valueOf(tmpExpStr);
                            this.resultDataType = DbTypes.DOUBLE;
                            this.parseResType = PARSE_RES_TYPE_DOUBLE;
                        } catch (Exception e) {
                        }
                    } else {
                        //可能是整数
                        try {
                            this.parseRes = Long.valueOf(tmpExpStr);
                            this.resultDataType = DbTypes.BIGINT;
                            this.parseResType = PARSE_RES_TYPE_LONG;
                        } catch (Exception e) {
                        }
                    }
                    if (parseRes == null) { //转换为小数\整数失败
                        if (tmpExpStr.equals("NULL")) {
                        	this.parseResType = PARSE_RES_TYPE_STRING;
                        	this.resultDataType = DbTypes.VARCHAR;
                        	this.parseRes = "";
                        } else if (tmpExpStr.equals("TRUE")) {
                        	this.parseResType = PARSE_RES_TYPE_BOOLEAN;
                        	this.resultDataType = DbTypes.BOOLEAN;
                        	this.parseRes = Boolean.TRUE;
                        } else if (tmpExpStr.equals("FALSE")) {
                        	this.parseResType = PARSE_RES_TYPE_BOOLEAN;
                        	this.resultDataType = DbTypes.BOOLEAN;
                        	this.parseRes = Boolean.FALSE;
                        } else {
                        	this.parseResType = PARSE_RES_TYPE_VARIABLE;
                        	this.parseRes = tmpExpStr;
                        }
                    }
                }
            } else {
                this.expA = new MathExpression(blkExp, expStrA);
                this.expB = new MathExpression(blkExp, expStrB);
                this.expA.parse();
                this.expB.parse();
                this.parseResType = PARSE_RES_TYPE_MATH_EXPRESSION;
            }
        } else {
            this.resultDataType = DbTypes.BIGINT;
            this.parseResType = PARSE_RES_TYPE_LONG;
            this.parseRes = new Long(0);
        }
        parseEval();
        this.parsed = true;
    }
    /**
     * 执行常量表达式
     */
    private void parseEval() {
        if (this.parseResType == PARSE_RES_TYPE_MATH_EXPRESSION 
                && ((MathExpression) this.expA).isConst()
                && ((MathExpression) this.expB).isConst()) {
            Object resA = this.expA.parseRes, resB = this.expB.parseRes;
            if (this.operator == '-') {
                if (this.expA.resultDataType == DbTypes.BIGINT && 
                        this.expB.resultDataType == DbTypes.BIGINT) {
                    //整数相减结果为整数
                    this.resultDataType = DbTypes.BIGINT;
                    this.result = new Long(To.toLong(resA) - To.toLong(resB));
                } else {
                    //小数相减结果为小数
                    this.resultDataType = DbTypes.DOUBLE;
                    this.result = new Double(To.toDouble(resA) - To.toDouble(resB));
                }
            } else if (this.operator == '*') {
                if (this.expA.resultDataType == DbTypes.BIGINT && 
                        this.expB.resultDataType == DbTypes.BIGINT) {
                    //整数相乘结果为整数
                    this.resultDataType = DbTypes.BIGINT;
                    this.result = new Long(To.toLong(resA) * To.toLong(resB));
                } else {
                    //小数相乘结果为小数
                    this.resultDataType = DbTypes.DOUBLE;
                    this.result = new Double(To.toDouble(resA) * To.toDouble(resB));
                }
            } else if (this.operator == '/') {
                this.resultDataType = DbTypes.DOUBLE;
                if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE && To.toLong(resB) == 0) {
                    this.result = new Double(Double.MAX_VALUE);
                    //throw new HgException("除数为0");
                } else {
                    this.result = new Double(To.toDouble(resA) / To.toDouble(resB));
                }
            } else if (this.operator == '%') {
                this.resultDataType = DbTypes.BIGINT;
                if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE && To.toLong(resB) == 0) {
                    this.result = new Long(Long.MAX_VALUE);
                } else {
                    this.result = new Long(To.toLong(resA) % To.toLong(resB));
                }
            } else if (this.operator == '+' || this.operator == '|') {
                if (this.operator == '+' && this.expA.resultDataType == DbTypes.BIGINT && 
                        this.expB.resultDataType == DbTypes.BIGINT) {
                    //整数相加结果为整数
                    this.resultDataType = DbTypes.BIGINT;
                    this.result = new Long(To.toLong(resA) + To.toLong(resB));
                } else if (this.operator == '|' || 
                        this.expA.resultDataType == DbTypes.VARCHAR || 
                        this.expB.resultDataType == DbTypes.VARCHAR) {
                    //字符串参加加法运算结果为字符串
                    this.resultDataType = DbTypes.VARCHAR;
                    this.result = To.objToString(resA) + To.objToString(resB);
                } else {
                    //小数相加结果为小数
                    this.resultDataType = DbTypes.DOUBLE;
                    this.result = new Double (To.toDouble(resA) + To.toDouble(resB));
                }
            }
            this.expA = null;
            this.expB = null;
            if (this.resultDataType == DbTypes.BOOLEAN) {
                this.parseResType = PARSE_RES_TYPE_BOOLEAN;
            } else if (this.resultDataType == DbTypes.BIGINT) {
                this.parseResType = PARSE_RES_TYPE_LONG;
            } else if (this.resultDataType == DbTypes.DOUBLE) {
                this.parseResType = PARSE_RES_TYPE_DOUBLE;
            } else if (this.resultDataType == DbTypes.VARCHAR) {
                this.parseResType = PARSE_RES_TYPE_STRING;
            }
            this.parseRes = this.result;
        }
    }
    /**
     * 执行表达式
     * @param conn
     */
    public void eval(Conn conn) throws HgException {
        super.eval(conn);
        if (this.expA != null) {
            this.expA.data = this.data;
            this.expA.dataStyle = this.dataStyle;
        }
        if (this.expB != null) {
            this.expB.data = this.data;
            this.expB.dataStyle = this.dataStyle;
        }
        if (this.parseResType == PARSE_RES_TYPE_BOOLEAN
                || this.parseResType == PARSE_RES_TYPE_LONG
                || this.parseResType == PARSE_RES_TYPE_DOUBLE
                || this.parseResType == PARSE_RES_TYPE_STRING) {
            //数据类型
            if (this.parseResType == PARSE_RES_TYPE_BOOLEAN) {
                this.resultDataType = DbTypes.BOOLEAN;
            } else if (this.parseResType == PARSE_RES_TYPE_LONG) {
                this.resultDataType = DbTypes.BIGINT;
            } else if (this.parseResType == PARSE_RES_TYPE_DOUBLE) {
                this.resultDataType = DbTypes.DOUBLE;
            } else if (this.parseResType == PARSE_RES_TYPE_STRING) {
                this.resultDataType = DbTypes.VARCHAR;
            }
            //值
            if (this.dataStyle == SqlConstant.DATA_STYLE_ROW) {
                this.resultDataStyle = SqlConstant.DATA_STYLE_VALUE;
                this.result = this.parseRes;
            } else {
                this.resultDataStyle = SqlConstant.DATA_STYLE_ARRAY;
                int size = ((RowSet) this.data).size();
                Array objs = new Array(this.resultDataType);
                for (int i = 0; i < size; i++) {
                    objs.addObj(this.parseRes);
                }
                this.result = objs;
            }
        } else if (this.parseResType == PARSE_RES_TYPE_VARIABLE) {
            //变量
            if (this.dataStyle == SqlConstant.DATA_STYLE_ROW) {
                this.resultDataStyle = SqlConstant.DATA_STYLE_VALUE;
                Field field = null;
                if (this.data != null && ((String) this.parseRes).charAt(0) != ':') {
                    Row row = (Row) this.data;
                    field = row.getField((String) this.parseRes);
                    if (field != null) {
                        this.resultDataType = field.type;
                        this.result = row.get((String) this.parseRes);
                    }
                }
                if (field == null) {
                    if (((String) this.parseRes).charAt(0) != ':') {
                        this.result = getByVar((String) this.parseRes);
                    } else {
                        this.result = getByVar(((String) this.parseRes).substring(1));
                    }
                    if (result != null) {
                        if (result instanceof Array) {
                            if (((Array) result).size() > 0) {
                                result = ((Array) result).getObj(0);
                            }
                        }
                        this.resultDataType = getDataType(result);
                    } else {
                        this.resultDataType = DbTypes.VARCHAR;
                        this.result = "";
                    }
                }
            } else {
                this.resultDataStyle = SqlConstant.DATA_STYLE_ARRAY;
                this.result = null;
                RowSet rowSet = (RowSet) this.data;
                Field field = null;
                Array objs;
                if (this.data != null) {
                    Row row;
                    field = rowSet.getField((String) this.parseRes);
                    if (field != null) {
                        this.resultDataType = field.type;
                        objs = new Array(this.resultDataType);
                        for (int i = 0; i < rowSet.size(); i++) {
                            row = rowSet.get(i);
                            objs.addObj(row.get((String) this.parseRes));
                        }
                        this.result = objs;
                    }
                }
                if (field == null) {
                    this.result = getByVar((String) this.parseRes);
                    if (result != null) {
                        this.resultDataType = getDataType(result);
                        objs = new Array(this.resultDataType);
                        for (int i = 0; i < rowSet.size(); i++) {
                            objs.addObj(this.result);
                        }
                    } else {
                        this.resultDataType = DbTypes.NULL;
                        objs = new Array();
                        for (int i = 0; i < rowSet.size(); i++) {
                            objs.addObj("");
                        }
                    }
                    this.result = objs;                        
                }
            }
        } else if (this.parseResType == PARSE_RES_TYPE_MATH_EXPRESSION) {
            this.expA.eval(conn);
            this.expB.eval(conn);
            Object resA = null, resB = null;
            //格式
            if (this.expA.resultDataStyle == SqlConstant.DATA_STYLE_VALUE || 
                    this.expB.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                this.resultDataStyle = SqlConstant.DATA_STYLE_VALUE;
                if (this.expA.resultDataStyle == SqlConstant.DATA_STYLE_ARRAY) {
                    ((MathExpression) this.expA).compress();
                }
                if (this.expB.resultDataStyle == SqlConstant.DATA_STYLE_ARRAY) {
                    ((MathExpression) this.expB).compress();
                }
                resA = this.expA.result;
                resB = this.expB.result;
            } else {
                this.resultDataStyle = SqlConstant.DATA_STYLE_ARRAY;
            }
            if (this.operator == '-') {
                if (this.expA.resultDataType == DbTypes.BIGINT && 
                        this.expB.resultDataType == DbTypes.BIGINT) {
                    //整数相减结果为整数
                    this.resultDataType = DbTypes.BIGINT;
                    if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                        this.result = new Long(To.toLong(resA) - To.toLong(resB));
                    } else {
                        Array objsA = (Array) this.expA.result;
                        Array objsB = (Array) this.expB.result;
                        Array resObjs = new Array(DbTypes.BIGINT);
                        for (int i = 0; i < objsA.size(); i++) {
                            resObjs.addObj(new Long(To.toLong(objsA.getObj(i)) - To.toLong(objsB.getObj(i))));
                        }
                        this.result = resObjs;
                    }
                } else {
                    //小数相减结果为小数
                    this.resultDataType = DbTypes.DOUBLE;
                    if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                        this.result = new Double(To.toDouble(resA) - To.toDouble(resB));
                    } else {
                        Array objsA = (Array) this.expA.result;
                        Array objsB = (Array) this.expB.result;
                        Array resObjs = new Array(DbTypes.DOUBLE);
                        for (int i = 0; i < objsA.size(); i++) {
                            resObjs.addObj(new Double(To.toDouble(objsA.getObj(i)) - To.toDouble(objsB.getObj(i))));
                        }
                        this.result = resObjs;
                    }
                }
            } else if (this.operator == '*') {
                if (this.expA.resultDataType == DbTypes.BIGINT && 
                        this.expB.resultDataType == DbTypes.BIGINT) {
                    //整数相乘结果为整数
                    this.resultDataType = DbTypes.BIGINT;
                    if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                        this.result = new Long(To.toLong(resA) * To.toLong(resB));
                    } else {
                        Array objsA = (Array) this.expA.result;
                        Array objsB = (Array) this.expB.result;
                        Array resObjs = new Array(DbTypes.BIGINT);
                        for (int i = 0; i < objsA.size(); i++) {
                            resObjs.addObj(new Long(To.toLong(objsA.getObj(i)) * To.toLong(objsB.getObj(i))));
                        }
                        this.result = resObjs;
                    }
                } else {
                    //小数相乘结果为小数
                    this.resultDataType = DbTypes.DOUBLE;
                    if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                        this.result = new Double(To.toDouble(resA) * To.toDouble(resB));
                    } else {
                        Array objsA = (Array) this.expA.result;
                        Array objsB = (Array) this.expB.result;
                        Array resObjs = new Array(DbTypes.DOUBLE);
                        for (int i = 0; i < objsA.size(); i++) {
                            resObjs.addObj(new Double(To.toDouble(objsA.getObj(i)) * To.toDouble(objsB.getObj(i))));
                        }
                        this.result = resObjs;
                    }
                }
            } else if (this.operator == '/') {
                this.resultDataType = DbTypes.DOUBLE;
                if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE && To.toDouble(resB) == 0) {
                    //throw new HgException("除数为0");
                    this.result = new Double(Double.MAX_VALUE);
                } else {
                    if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                        this.result = new Double(To.toDouble(resA) / To.toDouble(resB));
                    } else {
                        Array objsA = (Array) this.expA.result;
                        Array objsB = (Array) this.expB.result;
                        Array resObjs = new Array(DbTypes.DOUBLE);
                        for (int i = 0; i < objsA.size(); i++) {
                            if (To.toDouble(objsB.getObj(i)) == 0) {
                                resObjs.addObj(new Double(0));
                            } else {
                                resObjs.addObj(new Double(To.toDouble(objsA.getObj(i)) / To.toDouble(objsB.getObj(i))));
                            }
                        }
                        this.result = resObjs;
                    }
                }
            } else if (this.operator == '%') {
                this.resultDataType = DbTypes.BIGINT;
                if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE && To.toDouble(resB) == 0) {
                    //throw new HgException("除数为0");
                    this.result = new Long(Long.MAX_VALUE);
                } else {
                    if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                        this.result = new Long(To.toLong(resA) % To.toLong(resB));
                    } else {
                        Array objsA = (Array) this.expA.result;
                        Array objsB = (Array) this.expB.result;
                        Array resObjs = new Array(DbTypes.BIGINT);
                        for (int i = 0; i < objsA.size(); i++) {
                            if (To.toDouble(objsB.getObj(i)) == 0) {
                                resObjs.addObj(new Long(0));
                            } else {
                                resObjs.addObj(new Long(To.toLong(objsA.getObj(i)) % To.toLong(objsB.getObj(i))));
                            }
                        }
                        this.result = resObjs;
                    }
                }
            } else if (this.operator == '+' || this.operator == '|') {
                if (this.operator == '+' && this.expA.resultDataType == DbTypes.BIGINT && 
                        this.expB.resultDataType == DbTypes.BIGINT) {
                    //整数相加结果为整数
                    this.resultDataType = DbTypes.BIGINT;
                    if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                        this.result = new Long(To.toLong(resA) + To.toLong(resB));
                    } else {
                        Array objsA = (Array) this.expA.result;
                        Array objsB = (Array) this.expB.result;
                        Array resObjs = new Array(DbTypes.BIGINT);
                        for (int i = 0; i < objsA.size(); i++) {
                            resObjs.addObj(new Long(To.toLong(objsA.getObj(i)) + To.toLong(objsB.getObj(i))));
                        }
                        this.result = resObjs;
                    }
                } else if (this.operator == '|' || 
                        this.expA.resultDataType == DbTypes.VARCHAR || 
                            this.expB.resultDataType == DbTypes.VARCHAR) {
                    //字符串参加加法运算结果为字符串
                    this.resultDataType = DbTypes.VARCHAR;
                    if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                        this.result = To.objToString(resA) + To.objToString(resB);
                    } else {
                        Array objsA = (Array) this.expA.result;
                        Array objsB = (Array) this.expB.result;
                        Array resObjs = new Array();
                        for (int i = 0; i < objsA.size(); i++) {
                            resObjs.addObj(To.objToString(objsA.getObj(i)) + To.objToString(objsB.getObj(i)));
                        }
                        this.result = resObjs;
                    }
                } else {
                    //小数相加结果为小数
                    this.resultDataType = DbTypes.DOUBLE;
                    if (this.resultDataStyle == SqlConstant.DATA_STYLE_VALUE) {
                        this.result = new Double (To.toDouble(resA) + To.toDouble(resB));
                    } else {
                        Array objsA = (Array) this.expA.result;
                        Array objsB = (Array) this.expB.result;
                        Array resObjs = new Array(DbTypes.DOUBLE);
                        for (int i = 0; i < objsA.size(); i++) {
                            resObjs.addObj(new Double(To.toDouble(objsA.getObj(i)) + To.toDouble(objsB.getObj(i))));
                        }
                        this.result = resObjs;
                    }
                }
            }
        } else if (this.parseResType == PARSE_RES_TYPE_FUNC_EXPRESSION) {
            this.expA.eval(conn);
            if (expA.resultDataType == DbTypes.ROWSET) {
                RowSet rowSet;
                rowSet = (RowSet) this.expA.result;
                if (this.resultDataType != DbTypes.ROWSET) {
                    if (rowSet.fieldSize() > 0) {
                        this.resultDataType = ((Field) rowSet.fieldAt(0)).type;
                        if (rowSet.size() > 0) {
                            this.result = rowSet.getCellValue(0, 0);
                        } else {
                            this.result = "";
                        }
                    } else {
                        this.resultDataType = DbTypes.VARCHAR;
                    }
                } else {
                    this.result = rowSet;
                }
            } else if (expA.resultDataType == DbTypes.ROW) {
                Row row = (Row) this.expA.result;
                if (row.fieldSize() > 0) {
                    this.resultDataType = ((Field) row.fieldAt(0)).type;
                    this.result = row.get(((Field) row.fieldAt(0)).name);
                } else {
                    this.resultDataType = DbTypes.VARCHAR;
                }
            } else {
                this.resultDataStyle = expA.resultDataStyle;
                this.resultDataType = expA.resultDataType;
                this.result = this.expA.result;
            }
        } else if (this.parseResType == PARSE_RES_TYPE_QUERY) {
            if (this.data != null) {
                Row dataRow = (Row) this.data;
                String fieldName;
                for (int i = 0; i < dataRow.fieldSize(); i++) {
                    fieldName = dataRow.fullFieldNameAt(i);
                    this.expA.blkExp.varMap.put(fieldName, dataRow.get(fieldName));
                }
            }
            this.expA.eval(conn);
            RowSet rowSet = (RowSet) this.expA.result;
            if (this.resultDataType != DbTypes.ROWSET) {
                if (rowSet.fieldSize() > 0) {
                    this.resultDataType = ((Field) rowSet.fieldAt(0)).type;
                    if (rowSet.size() > 0) {
                        this.result = rowSet.getCellValue(0, 0);
                    } else {
                        this.result = "";
                    }
                } else {
                    this.resultDataType = DbTypes.VARCHAR;
                }
            } else {
                this.result = rowSet;
            }
        }
    }
    private static int getDataType(Object result) {
        int type = DbTypes.VARCHAR;
        if (result instanceof Double) {
            type = DbTypes.DOUBLE;
        } else if (result instanceof Long) {
            type = DbTypes.BIGINT;
        } else if (result instanceof Boolean) {
            type = DbTypes.BOOLEAN;
        } else if (result instanceof Row) {
            type = DbTypes.ROW;
        } else if (result instanceof RowSet) {
            type = DbTypes.ROWSET;
        }
        return type;
    }
    private Object getByVar(String str) {
        if (blkExp != null) {
            return blkExp.getValue(str);
        } else {
            return null;
        }
    }
    /**
     * 压缩值
     * @param objs
     * @return
     * @throws HgException
     */
    public void compress() throws HgException {
        Object obj = null;
        Array objs = (Array) this.result;
        if (objs.size() > 0) {
            obj = objs.getObj(0);
        }
        for (int i = 1; i < objs.size(); i++) {
            if (!objs.getObj(i).equals(obj)) {
                obj = "";
                break;
                //throw new HgException(this.expStr + "不是分组字段");
            } 
        }
        this.result = obj;
        this.resultDataStyle = SqlConstant.DATA_STYLE_VALUE;
    }
    public ArrayList getChildExpList(Conn conn) throws HgException {
        ArrayList list = new ArrayList();
        if (!this.parsed) this.parse();
        if (this.expA != null) list.add(this.expA);
        if (this.expB != null) list.add(this.expB);
        return list;
    }
    /**
     * 是否是常量表达式
     * @return
     */
    public boolean isConst() {
        return this.parseResType == PARSE_RES_TYPE_BOOLEAN
                    || this.parseResType == PARSE_RES_TYPE_LONG
                    || this.parseResType == PARSE_RES_TYPE_DOUBLE
                    || this.parseResType == PARSE_RES_TYPE_STRING;
    }
    public void fillVarList(ArrayList list) throws HgException {
        if (!this.parsed) this.parse();
        if  (this.parseResType == PARSE_RES_TYPE_VARIABLE) {
            if (!list.contains(this.parseRes)) {
                list.add(this.parseRes);
            }
        } else if (this.parseResType == PARSE_RES_TYPE_MATH_EXPRESSION) {
            ((MathExpression) this.expA).fillVarList(list);
            ((MathExpression) this.expB).fillVarList(list);
        }
    }
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MathExpression) {
            MathExpression mathExp = (MathExpression) obj;
            if (!mathExp.parsed)
                try {
                    mathExp.parse();
                } catch (HgException e) {
                }
            if (!this.parsed)
                try {
                    this.parse();
                } catch (HgException e) {
                }
            if (mathExp.parseResType == this.parseResType) {
                if (this.parseResType == PARSE_RES_TYPE_BOOLEAN
                        || this.parseResType == PARSE_RES_TYPE_LONG
                        || this.parseResType == PARSE_RES_TYPE_DOUBLE
                        || this.parseResType == PARSE_RES_TYPE_STRING) {
                    return mathExp.parseRes.equals(this.parseRes);
                } else if (this.parseResType == PARSE_RES_TYPE_VARIABLE) {
                    String str1 = (String) this.parseRes;
                    String str2 = (String) mathExp.parseRes;
                    return str1.equals(str2) 
                        || str1.endsWith("." + str2)
                        || str2.endsWith("." + str1);
                } else if (this.parseResType == PARSE_RES_TYPE_MATH_EXPRESSION) {
                    return this.expA.equals(mathExp.expA) && this.expB.equals(mathExp.expB);
                } else if (this.parseResType == PARSE_RES_TYPE_FUNC_EXPRESSION) {
                    return this.expA.equals(mathExp.expA);
                }
            }
        }
        return false;
    }
    /**
     * 确定数据类型
     * @param conn
     */
    public void ensureDataType(Row row) throws HgException {
        if (!this.parsed) this.parse();
        this.resultDataType = DbTypes.VARCHAR;
        if (this.parseResType == PARSE_RES_TYPE_BOOLEAN
                || this.parseResType == PARSE_RES_TYPE_LONG
                || this.parseResType == PARSE_RES_TYPE_DOUBLE
                || this.parseResType == PARSE_RES_TYPE_STRING) {
            //数据类型
            if (this.parseResType == PARSE_RES_TYPE_BOOLEAN) {
                this.resultDataType = DbTypes.BOOLEAN;
            } else if (this.parseResType == PARSE_RES_TYPE_LONG) {
                this.resultDataType = DbTypes.BIGINT;
            } else if (this.parseResType == PARSE_RES_TYPE_DOUBLE) {
                this.resultDataType = DbTypes.DOUBLE;
            } else if (this.parseResType == PARSE_RES_TYPE_STRING) {
                this.resultDataType = DbTypes.VARCHAR;
            }
        } else if (this.parseResType == PARSE_RES_TYPE_VARIABLE) {
            //变量
            Field field = null;
            field = row.getField((String) this.parseRes);
            if (field != null) {
                this.resultDataType = field.type;
            }
        } else if (this.parseResType == PARSE_RES_TYPE_MATH_EXPRESSION) {
            if (this.operator == '-') {
                if (this.expA.resultDataType == DbTypes.BIGINT && this.expB.resultDataType == DbTypes.BIGINT) {
                    //整数相减结果为整数
                    this.resultDataType = DbTypes.BIGINT;
                } else {
                    //小数相减结果为小数
                    this.resultDataType = DbTypes.DOUBLE;
                }
            } else if (this.operator == '*') {
                if (this.expA.resultDataType == DbTypes.BIGINT && this.expB.resultDataType == DbTypes.BIGINT) {
                    //整数相乘结果为整数
                    this.resultDataType = DbTypes.BIGINT;
                } else {
                    //小数相乘结果为小数
                    this.resultDataType = DbTypes.DOUBLE;
                }
            } else if (this.operator == '/') {
                this.resultDataType = DbTypes.DOUBLE;
            } else if (this.operator == '+' || this.operator == '|') {
                if (this.operator == '+' && this.expA.resultDataType == DbTypes.BIGINT && 
                        this.expB.resultDataType == DbTypes.BIGINT) {
                    //整数相加结果为整数
                    this.resultDataType = DbTypes.BIGINT;
                } else if (this.operator == '|' || 
                        this.expA.resultDataType == DbTypes.VARCHAR || 
                            this.expB.resultDataType == DbTypes.VARCHAR) {
                    //字符串参加加法运算结果为字符串
                    this.resultDataType = DbTypes.VARCHAR;
                } else {
                    //小数相加结果为小数
                    this.resultDataType = DbTypes.DOUBLE;
                }
            }
        }
    }
}
