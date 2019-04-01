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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hg.util.HgException;
import com.hg.util.To;

/**
 * 逻辑表达式
 * @author   whg
 */
public class LogicExpression extends Expression {
    /**
     * 操作符类型:或者 or
     */
    private static final int OPERATOR_TYPE_OR = 1;
    /**
     * 操作符类型:并且 and
     */
    private static final int OPERATOR_TYPE_AND = 2;
    /**
     * 操作符类型:非 not
     */
    private static final int OPERATOR_TYPE_NOT = 3;
    /**
     * 操作符类型:等于 =
     */
    public static final int OPERATOR_TYPE_EQUAL = 4;
    /**
     * 操作符类型:不等于 !=
     */
    public static final int OPERATOR_TYPE_NOTEQUAL = 5;
    /**
     * 操作符类型:大于 >
     */
    public static final int OPERATOR_TYPE_MORE = 6;
    /**
     * 操作符类型:大于等于 >=
     */
    public static final int OPERATOR_TYPE_MOREEQUAL = 7;
    /**
     * 操作符类型:小于 <
     */
    public static final int OPERATOR_TYPE_LESS = 8;
    /**
     * 操作符类型:小于等于 <=
     */
    public static final int OPERATOR_TYPE_LESSEQUAL = 9;
    /**
     * 操作符类型:在其中 in
     */
    private static final int OPERATOR_TYPE_IN = 10;
    /**
     * 操作符类型:不在其中 in
     */
    private static final int OPERATOR_TYPE_NOTIN = 11;
    /**
     * 操作符类型:类似 like
     */
    private static final int OPERATOR_TYPE_LIKE = 12;
    /**
     * 操作符类型:不类似 not like
     */
    private static final int OPERATOR_TYPE_NOTLIKE = 13;
    /**
     * 操作符类型:正则表达式类似 rlike
     */
    private static final int OPERATOR_TYPE_RLIKE = 14;
    /**
     * 操作符类型:非正则表达式类似 not rlike
     */
    private static final int OPERATOR_TYPE_NOTRLIKE = 15;
    /**
     * 操作符类型:存在
     */
    private static final int OPERATOR_TYPE_EXISTS = 16;
    /**
     * 操作符类型:between
     */
    private static final int OPERATOR_TYPE_BETWEEN = 17;
    /**
     * 分析结果类型:未分析
     */
    private static final int PARSE_RES_TYPE_NONE = 0;
    /**
     * 分析结果类型:关系表达式
     */
    private static final int PARSE_RES_TYPE_RELATION_EXPRESSION = 1;
    /**
     * 分析结果类型:逻辑表达式
     */
    private static final int PARSE_RES_TYPE_LOGIC_EXPRESSION = 2;
    /**
     * 分析结果类型:逻辑定量表达式
     */
    private static final int PARSE_RES_TYPE_LOGIC_RATION_EXPRESSION = 3;
    /**
     * 分析结果类型:数学表达式
     */
    private static final int PARSE_RES_TYPE_MATH_EXPRESSION = 4;
    /**
     * 分析结果类型:逻辑定量表达式
     */
    private static final int RATION_SOME = 1;
    /**
     * 分析结果类型:逻辑定量表达式
     */
    private static final int RATION_ALL = 2;
    /**
     * 定量
     */
    private int ration;
    /**
     * 分析结果类型
     */
    public int parseResType = PARSE_RES_TYPE_NONE;
    /**
     * 左表达式
     */
    private Expression expA;
    /**
     * 右表达式
     */
    private Expression expB;
    /**
     * 操作符
     */
    private int operator;
    /**
     * 构造函数
     */
    public LogicExpression(BlkExpression blkExp, String str) {
        super(blkExp, str);
        this.resultDataType = DbTypes.BOOLEAN;
    }
    /**
     * 执行
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
            //between开始
            boolean betweenBegin = false;
            while (true) {
                //修改between后的and
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
                    } else if (!strBegin && c == '(') {
                        pars++;
                    } else if (!strBegin && c == ')') {
                        pars--;
                    }
                    if (!strBegin && pars == 0 && tmpExpStr.length() > i + 9 
                            && tmpExpStr.substring(i, i + 9).equals(" BETWEEN ")) {
                        betweenBegin = true;
                        i = i + 8;
                    }
                    if (!strBegin && pars == 0 && betweenBegin && tmpExpStr.length() > i + 5 
                            && tmpExpStr.substring(i, i + 5).equals(" AND ")) {
                        //将and替换为","
                        tmpExpStr = tmpExpStr.substring(0, i) + "  ,  " + tmpExpStr.substring(i + 5);
                        betweenBegin = false;
                        i = i + 4;
                    }
                }
                //用"OR"分割
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
                    }
                    if (!strBegin && pars == 0 && i - 3 > -1 
                            && tmpExpStr.substring(i - 3, i + 1).equals(" OR ")) {
                        this.operator = OPERATOR_TYPE_OR;
                        expStrA = tmpExpStr.substring(0, i - 3);
                        expStrB = tmpExpStr.substring(i + 1);
                        split = true;
                        break;
                    }
                }
                if (!split) {//用"and"分割
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
                        } else if (!strBegin && pars == 0 && i - 4 > -1 
                                && tmpExpStr.substring(i - 4, i + 1).equals(" AND ")) {
                            this.operator = OPERATOR_TYPE_AND;
                            expStrA = tmpExpStr.substring(0, i - 4);
                            expStrB = tmpExpStr.substring(i + 1);
                            split = true;
                            break;
                        }
                    }
                }
                if (!split) {//NOT 分割
                    if (tmpExpStr.length() > 4 && tmpExpStr.substring(0, 4).equals("NOT ")) {
                        //非运算
                        this.operator = OPERATOR_TYPE_NOT;
                        expStrA = tmpExpStr.substring(4, tmpExpStr.length());
                        split = true;
                    }
                }
                if (!split) {//用">,>="等分割
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
                        } else if (!strBegin && c == '(') {
                            pars++;
                        } else if (!strBegin && c == ')') {
                            pars--;
                        }
                        if (!strBegin && pars == 0) {
                            if (tmpExpStr.length() > i + 2 
                                    && tmpExpStr.substring(i, i + 2).equals(">=")) {
                                this.operator = OPERATOR_TYPE_MOREEQUAL;
                                expStrA = tmpExpStr.substring(0, i);
                                expStrB = tmpExpStr.substring(i + 2);
                                split = true;
                                break;
                            } else if (tmpExpStr.length() > i + 2 
                                    && (tmpExpStr.substring(i, i + 2).equals("<>") 
                                            || tmpExpStr.substring(i, i + 2).equals("!="))) {
                                this.operator = OPERATOR_TYPE_NOTEQUAL;
                                expStrA = tmpExpStr.substring(0, i);
                                expStrB = tmpExpStr.substring(i + 2);
                                split = true;
                                break;
                            } else if (tmpExpStr.length() > i + 2 
                                    && tmpExpStr.substring(i, i + 2).equals("<=")) {
                                this.operator = OPERATOR_TYPE_LESSEQUAL;
                                expStrA = tmpExpStr.substring(0, i);
                                expStrB = tmpExpStr.substring(i + 2);
                                split = true;
                                break;
                            } else if (tmpExpStr.length() > i + 1 
                                    && tmpExpStr.substring(i, i + 1).equals(">")) {
                                this.operator = OPERATOR_TYPE_MORE;
                                expStrA = tmpExpStr.substring(0, i);
                                expStrB = tmpExpStr.substring(i + 1);
                                split = true;
                                break;
                            } else if (tmpExpStr.length() > i + 1 
                                    && tmpExpStr.substring(i, i + 1).equals("<")) {
                                this.operator = OPERATOR_TYPE_LESS;
                                expStrA = tmpExpStr.substring(0, i);
                                expStrB = tmpExpStr.substring(i + 1);
                                split = true;
                                break;
                            } else if (tmpExpStr.length() > i + 1 
                                    && tmpExpStr.substring(i, i + 1).equals("=")) {
                                this.operator = OPERATOR_TYPE_EQUAL;
                                expStrA = tmpExpStr.substring(0, i);
                                expStrB = tmpExpStr.substring(i + 1);
                                split = true;
                                break;
                            } else if (tmpExpStr.length() > i + 4 
                                    && tmpExpStr.substring(i, i + 4).equals(" IS ")) {
                                expStrA = tmpExpStr.substring(0, i);
                                expStrB = tmpExpStr.substring(i + 4).trim();
                                if (!expStrB.startsWith("NOT ")) {
                                    this.operator = OPERATOR_TYPE_EQUAL;
                                } else {
                                    expStrB = expStrB.substring(4);
                                    this.operator = OPERATOR_TYPE_NOTEQUAL;                                    
                                }
                                split = true;
                                break;
                            } else if (tmpExpStr.length() > i + 4 
                                    && tmpExpStr.substring(i, i + 4).equals(" IN ")) {
                                expStrA = tmpExpStr.substring(0, i).trim();
                                expStrB = tmpExpStr.substring(i + 4);
                                if (expStrA.endsWith(" NOT")) {
                                    this.operator = OPERATOR_TYPE_NOTIN;
                                    expStrA = expStrA.substring(0, expStrA.length() - 4);
                                } else {
                                    this.operator = OPERATOR_TYPE_IN;
                                }
                                split = true;
                                break;
                            } else if (tmpExpStr.length() > i + 6 
                                    && tmpExpStr.substring(i, i + 6).equals(" LIKE ")) {
                                expStrA = tmpExpStr.substring(0, i).trim();
                                expStrB = tmpExpStr.substring(i + 6);
                                if (expStrA.endsWith(" NOT")) {
                                    this.operator = OPERATOR_TYPE_NOTLIKE;
                                    expStrA = expStrA.substring(0, expStrA.length() - 4);
                                } else {
                                    this.operator = OPERATOR_TYPE_LIKE;
                                }
                                split = true;
                                break;
                            } else if (tmpExpStr.length() > i + 7 
                                    && tmpExpStr.substring(i, i + 7).equals(" RLIKE ")) {
                                expStrA = tmpExpStr.substring(0, i).trim();
                                expStrB = tmpExpStr.substring(i + 7);
                                split = true;
                                if (expStrA.endsWith(" NOT")) {
                                    this.operator = OPERATOR_TYPE_NOTRLIKE;
                                    expStrA = expStrA.substring(0, expStrA.length() - 4);
                                } else {
                                    this.operator = OPERATOR_TYPE_RLIKE;
                                }
                                break;
                            } else if (tmpExpStr.length() > i + 9 
                                    && tmpExpStr.substring(i, i + 9).equals(" BETWEEN ")) {
                                expStrA = tmpExpStr.substring(0, i);
                                expStrB = tmpExpStr.substring(i + 9);
                                this.operator = OPERATOR_TYPE_BETWEEN;
                                split = true;
                                break;
                            }
                        }
                    }
                }
                if (!split) {
                    if (tmpExpStr.charAt(0) == '(' && tmpExpStr.charAt(tmpExpStr.length() - 1) == ')') {
                        //被括号包含,去掉括号
                        tmpExpStr = tmpExpStr.substring(1, tmpExpStr.length() - 1);
                    } else if (tmpExpStr.length() > 7 && tmpExpStr.substring(0, 7).equals("EXISTS ")) {
                        this.operator = OPERATOR_TYPE_EXISTS;
                        expStrA = tmpExpStr.substring(7, tmpExpStr.length());
                        split = true;
                        break;
                    } else {
                        break;
                    }
                } else {
                    //分割完成
                    break;
                }
            }
            if (split) {//算术表达式
                if (this.operator == OPERATOR_TYPE_AND || this.operator == OPERATOR_TYPE_OR) {
                    this.expA = new LogicExpression(blkExp, expStrA);
                    this.expB = new LogicExpression(blkExp, expStrB);
                    this.parseResType = PARSE_RES_TYPE_RELATION_EXPRESSION;
                } else if (this.operator == OPERATOR_TYPE_NOT) {
                    this.expA = new LogicExpression(blkExp, expStrA);
                    this.parseResType = PARSE_RES_TYPE_RELATION_EXPRESSION;
                } else if (this.operator == OPERATOR_TYPE_BETWEEN) {
                    //将between转为>=和<=
                    String[] strs = Parser.split(expStrB, ',');
                    this.expA = new LogicExpression(blkExp, expStrA + ">=" + strs[0]);
                    this.expB = new LogicExpression(blkExp, expStrA + "<=" + strs[1]);
                    this.operator = OPERATOR_TYPE_AND;
                    this.parseResType = PARSE_RES_TYPE_RELATION_EXPRESSION;
                } else {
                    expStrB = expStrB.trim();
                    this.expB = new MathExpression(blkExp, expStrB);
                    this.parseResType = PARSE_RES_TYPE_LOGIC_EXPRESSION;
                    this.expA = new MathExpression(blkExp, expStrA);
                }
                this.expA.parse();
                if (this.expB != null) {
                    this.expB.parse();
                }
            } else {
                this.parseResType = PARSE_RES_TYPE_MATH_EXPRESSION;
                this.expA = new MathExpression(blkExp, tmpExpStr);
                this.expA.parse();
            }
        } else {
            this.parseResType = PARSE_RES_TYPE_MATH_EXPRESSION;
            this.expA = new MathExpression(blkExp, "TRUE");
            this.expA.parse();
        }
        this.parseEval();
        this.parsed = true;
    }
    private void evalExpB(Conn conn) throws HgException {
        if (this.expB != null) {
            this.expB.data = this.data;
            this.expB.dataStyle = this.dataStyle;
            this.expB.eval(conn);
            if (this.expB.resultDataStyle == SqlConstant.DATA_STYLE_ARRAY) {
                ((MathExpression) this.expB).compress();
            }
        }
    }
    /**
     * 编译时执行
     * parseRes保存编译执行结果,为空表示为非常量表达式
     */
    public void parseEval() throws HgException {
        this.parseRes = null;
        if (this.expA != null) this.expA.parse();
        if (this.expB != null) this.expB.parse();
        if (this.parseResType == PARSE_RES_TYPE_MATH_EXPRESSION 
                && ((MathExpression) this.expA).isConst()) {
            this.parseRes = new Boolean(To.toBool(this.expA.parseRes));
        } else if (this.operator == OPERATOR_TYPE_AND) {
            if (this.expA.parseRes != null && !To.toBool(this.expA.parseRes)) {
                //expA为false，不再计算expB
                this.parseRes = Boolean.FALSE;
            } else if (this.expB.parseRes != null && !To.toBool(this.expB.parseRes)) {
                //expB为false
                this.parseRes = Boolean.FALSE;
            }
        } else if (this.operator == OPERATOR_TYPE_OR) {
            if (this.expA.parseRes != null && To.toBool(this.expA.parseRes)) {
                //expA为true，不再计算expB
                this.parseRes = Boolean.TRUE;
            } else if (this.expB.parseRes != null && To.toBool(this.expB.parseRes)) {
                //expB为true
                this.parseRes = Boolean.TRUE;
            }
        } else if (this.operator == OPERATOR_TYPE_NOT) {
            if (this.expA.parseRes != null && To.toBool(this.expA.parseRes)) {
                this.parseRes = Boolean.FALSE; 
            }
        } else if (this.operator != OPERATOR_TYPE_EXISTS 
                && this.parseResType != PARSE_RES_TYPE_LOGIC_RATION_EXPRESSION 
                && ((MathExpression) this.expA).isConst() && ((MathExpression) this.expB).isConst()) {
            int type = getCompareType();
            int n = 0;
            if (this.operator != OPERATOR_TYPE_IN && this.operator != OPERATOR_TYPE_NOTIN) {
                n = ObjectUtil.compare(this.expA.parseRes, this.expB.parseRes, type);
                this.parseRes = Boolean.FALSE;
            }
            if (this.operator == OPERATOR_TYPE_MOREEQUAL && n >= 0
                    || this.operator == OPERATOR_TYPE_MORE && n > 0
                    || this.operator == OPERATOR_TYPE_EQUAL && n == 0
                    || this.operator == OPERATOR_TYPE_NOTEQUAL && n != 0
                    || this.operator == OPERATOR_TYPE_LESSEQUAL && n <= 0
                    || this.operator == OPERATOR_TYPE_LESS && n < 0) {
                this.parseRes = Boolean.TRUE;
            } else if (this.operator == OPERATOR_TYPE_LIKE) {
                if (this.mCharMatch(To.objToString(this.expA.parseRes), 
                        To.objToString(this.expB.parseRes))) {
                    this.parseRes = Boolean.TRUE;
                } else {
                    this.parseRes = Boolean.FALSE;
                }
            } else if (this.operator == OPERATOR_TYPE_RLIKE) {
                String pStr = To.objToString(this.expB.parseRes);
                Pattern p = Pattern.compile(pStr);
                Matcher m = p.matcher(" " + this.expA.parseRes + " ");
                if (m.find()) {
                    this.parseRes = Boolean.TRUE;
                } else {
                    this.parseRes = Boolean.FALSE;
                }
            } else if (this.operator == OPERATOR_TYPE_NOTLIKE) {
                if (this.mCharMatch(To.objToString(this.expA.parseRes), To.objToString(this.expB.parseRes))) {
                    this.parseRes = Boolean.FALSE;
                } else {
                    this.parseRes = Boolean.TRUE;
                }
            } else if (this.operator == OPERATOR_TYPE_NOTRLIKE) {
                String pStr = To.objToString(this.expB.parseRes);
                Pattern p = Pattern.compile(pStr);
                Matcher m = p.matcher(" " + this.expA.parseRes + " ");
                if (m.find()) {
                    this.parseRes = Boolean.FALSE;
                } else {
                    this.parseRes = Boolean.TRUE;
                }
            }
        }
        if (this.parseRes != null) {
            this.parseResType = PARSE_RES_TYPE_MATH_EXPRESSION;
            this.expA = new MathExpression(blkExp, this.parseRes.toString().toUpperCase());
            this.expA.parse();
            this.expB = null;
        }
    }
    private int getCompareType() {
        if (this.expA.resultDataType == DbTypes.BIGINT 
                || this.expA.resultDataType == DbTypes.DOUBLE
                || this.expB.resultDataType == DbTypes.BIGINT
                || this.expB.resultDataType == DbTypes.DOUBLE) {
            return DbTypes.DOUBLE;
        } else {
            return DbTypes.VARCHAR;
        }
    }
    /**
     * 是否恒等
     * @return
     * @throws HgException 
     */
    public boolean isStaticTrue() throws HgException {
        if (!this.parsed) this.parse();
        return this.parseResType == PARSE_RES_TYPE_MATH_EXPRESSION && this.parseRes != null && this.parseRes.equals(Boolean.TRUE);
    }
    /**
     * 是否恒不等
     * @return
     * @throws HgException 
     */
    public boolean isStaticFalse() throws HgException {
        if (!this.parsed) this.parse();
        return this.parseResType == PARSE_RES_TYPE_MATH_EXPRESSION && this.parseRes != null && this.parseRes.equals(Boolean.FALSE);
    }
    /**
     * 执行
     */
    public void eval(Conn conn) throws HgException {
        super.eval(conn);
        this.expA.data = this.data;
        this.expA.dataStyle = this.dataStyle;
        this.expA.eval(conn);
        if (this.expA.resultDataStyle == SqlConstant.DATA_STYLE_ARRAY) {
            ((MathExpression) this.expA).compress();
        }
        if (this.operator != OPERATOR_TYPE_AND && this.operator != OPERATOR_TYPE_OR) {
            //非and和or运算，计算expB
            this.evalExpB(conn);
        }
        if (this.parseResType == PARSE_RES_TYPE_MATH_EXPRESSION) {
            this.result = new Boolean(To.toBool(this.expA.result));
        } else if (this.operator == OPERATOR_TYPE_AND) {
            this.result = Boolean.TRUE;
            if (!To.toBool(this.expA.result)) {
                //expA为false，不再计算expB
                this.result = Boolean.FALSE;
            } else {
                this.evalExpB(conn);
                if (!To.toBool(this.expB.result)) {
                    //expB为false
                    this.result = Boolean.FALSE;
                }
            }
        } else if (this.operator == OPERATOR_TYPE_OR) {
            this.result = Boolean.FALSE;
            if (To.toBool(this.expA.result)) {
                //expA为true，不再计算expB
                this.result = Boolean.TRUE;
            } else {
                this.evalExpB(conn);
                if (To.toBool(this.expB.result)) {
                    //expB为true
                    this.result = Boolean.TRUE;
                }
            }
        } else if (this.operator == OPERATOR_TYPE_NOT) {
            if (To.toBool(this.expA.result)) {
                this.result = Boolean.FALSE; 
            } else {
                this.result = Boolean.TRUE; 
            }
        } else if (this.operator == OPERATOR_TYPE_EXISTS) {
            if (((RowSet) this.expA.result).size() > 0) {
                this.result = Boolean.TRUE;
            } else {
                this.result = Boolean.FALSE;
            }
        } else {
            int type;
            int n = 0;
            if (this.parseResType == PARSE_RES_TYPE_LOGIC_RATION_EXPRESSION) {
                if (this.ration == RATION_SOME) {
                    this.result = Boolean.FALSE;
                } else if (this.ration == RATION_ALL) {
                    this.result = Boolean.TRUE;
                }
                RowSet rowSet = (RowSet) this.expB.result;
                if (rowSet.fieldSize() > 0 && rowSet.size() > 0) {
                    Field field = (Field) rowSet.fieldAt(0);
                    if ((this.expA.resultDataType == DbTypes.BIGINT || this.expA.resultDataType == DbTypes.DOUBLE)
                            && (field.type == DbTypes.BIGINT || field.type == DbTypes.DOUBLE)) {
                        type = DbTypes.DOUBLE;
                    } else {
                        type = DbTypes.VARCHAR;
                    }
                    boolean b;
                    for (int i = 0; i < rowSet.size(); i++) {
                        n = ObjectUtil.compare(this.expA.result, rowSet.getCellValue(i, 0), type);
                        b = false;
                        if (this.operator == OPERATOR_TYPE_MOREEQUAL && n >= 0
                                || this.operator == OPERATOR_TYPE_MORE && n > 0
                                || this.operator == OPERATOR_TYPE_EQUAL && n == 0
                                || this.operator == OPERATOR_TYPE_NOTEQUAL && n != 0
                                || this.operator == OPERATOR_TYPE_LESSEQUAL && n <= 0
                                || this.operator == OPERATOR_TYPE_LESS && n < 0) {
                            b = true;
                        }
                        if (this.ration == RATION_SOME && b) {
                            this.result = Boolean.TRUE;
                            break;
                        }
                        if (this.ration == RATION_ALL && !b) {
                            this.result = Boolean.FALSE;
                            break;
                        }
                    }
                }
            } else {
                type = this.getCompareType();
                if (this.operator != OPERATOR_TYPE_IN && this.operator != OPERATOR_TYPE_NOTIN) {
                    n = ObjectUtil.compare(this.expA.result, this.expB.result, type);
                } else {
                    //in 和 notin 比较类型
                    Field field = (Field) (((RowSet) this.expB.result).fieldAt(0));
                    if ((this.expA.resultDataType == DbTypes.BIGINT || this.expA.resultDataType == DbTypes.DOUBLE)
                            && (field.type == DbTypes.BIGINT || field.type == DbTypes.DOUBLE)) {
                        type = DbTypes.DOUBLE;
                    } else {
                        type = DbTypes.VARCHAR;
                    }
                }
                this.result = Boolean.FALSE;
                if (this.operator == OPERATOR_TYPE_MOREEQUAL && n >= 0
                        || this.operator == OPERATOR_TYPE_MORE && n > 0
                        || this.operator == OPERATOR_TYPE_EQUAL && n == 0
                        || this.operator == OPERATOR_TYPE_NOTEQUAL && n != 0
                        || this.operator == OPERATOR_TYPE_LESSEQUAL && n <= 0
                        || this.operator == OPERATOR_TYPE_LESS && n < 0) {
                    this.result = Boolean.TRUE;
                } else if (this.operator == OPERATOR_TYPE_IN) {
                    RowSet rowSet = (RowSet) this.expB.result;
                    this.result = Boolean.FALSE;
                    for (int i = 0; i < rowSet.size(); i++) {
                        if (ObjectUtil.compare(rowSet.getCellValue(i, 0), this.expA.result, type) == 0) {
                            this.result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (this.operator == OPERATOR_TYPE_LIKE) {
                    if (this.mCharMatch(To.objToString(this.expA.result), 
                            To.objToString(this.expB.result))) {
                        this.result = Boolean.TRUE;
                    } else {
                        this.result = Boolean.FALSE;
                    }
                } else if (this.operator == OPERATOR_TYPE_RLIKE) {
                    String pStr = To.objToString(this.expB.result);
                    Pattern p = Pattern.compile(pStr);
                    Matcher m = p.matcher(" " + this.expA.result + " ");
                    if (m.find()) {
                        this.result = Boolean.TRUE;
                    } else {
                        this.result = Boolean.FALSE;
                    }
                } else if (this.operator == OPERATOR_TYPE_NOTIN) {
                    RowSet rowSet = (RowSet) this.expB.result;
                    this.result = Boolean.TRUE;
                    for (int i = 0; i < rowSet.size(); i++) {
                        if (ObjectUtil.compare(rowSet.getCellValue(i, 0), this.expA.result, type) == 0) {
                            this.result = Boolean.FALSE;
                            break;
                        }
                    }
                } else if (this.operator == OPERATOR_TYPE_NOTLIKE) {
                    if (this.mCharMatch(To.objToString(this.expA.result), To.objToString(this.expB.result))) {
                        this.result = Boolean.FALSE;
                    } else {
                        this.result = Boolean.TRUE;
                    }
                } else if (this.operator == OPERATOR_TYPE_NOTRLIKE) {
                    String pStr = To.objToString(this.expB.result);
                    Pattern p = Pattern.compile(pStr);
                    Matcher m = p.matcher(" " + this.expA.result + " ");
                    if (m.find()) {
                        this.result = Boolean.FALSE;
                    } else {
                        this.result = Boolean.TRUE;
                    }
                }
            }
        }
    }
    /**
     * 是否成立
     */
    public boolean isTrue(Conn conn) throws HgException {
        this.eval(conn);
        return To.toBool(this.result);
    }
    /**
     * 匹配
     * @param str
     * @param rstr
     * @return
     */
    private boolean mCharMatch(String str, String likeStr) {
        if (likeStr.indexOf('%') < 0) {
            //没有％
            if (str.length() != likeStr.length()) {
                return false;
            } else {
                return this.sCharMatch(str, likeStr);
            }
        } else {
            if (likeStr.length() > 0 && likeStr.charAt(0) != '%') { //%前有数据
                String tmpStr = likeStr.substring(0, likeStr.indexOf('%'));
                likeStr = likeStr.substring(likeStr.indexOf('%') + 1);
                if (str.length() < tmpStr.length()) {
                    return false;
                } else {
                    if (!this.sCharMatch(str.substring(0, tmpStr.length()), tmpStr)) {
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
                    if (!this.sCharMatch(str.substring(str.length() - tmpStr.length()), tmpStr)) {
                        return false;
                    }
                    str = str.substring(0, str.length() - tmpStr.length());
                }
            }
        }
        String[] likes = (likeStr).split("%");
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
    private boolean sCharMatch(String str, String likeStr) {
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
    public ArrayList getChildExpList(Conn conn) throws HgException {
        if (!this.parsed) this.parse();
        ArrayList list = new ArrayList();
        if (this.expA != null) list.add(this.expA);
        if (this.expB != null) list.add(this.expB);
        return list;
    }
    public void fillVarList(ArrayList list) throws HgException {
        if (!this.parsed) this.parse();
        if (this.expA != null) this.expA.fillVarList(list);
        if (this.expB != null) this.expB.fillVarList(list);
    }
}
