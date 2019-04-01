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

/**
 * 表达式
 * @author   whg
 */
public class Expression {
    /**
     * 内部sql标记
     */
    protected static final String INNER_SQL_TAG = "#";
    /**
     * 处理数据
     */
    public Object data;
    /**
     * 处理数据格式
     */
    public int dataStyle = SqlConstant.DATA_STYLE_ROW;
    /**
     * 结果数据类型
     */
    public int resultDataType = DbTypes.VARCHAR;
    /**
     * 结果数据格式
     */
    public int resultDataStyle = SqlConstant.DATA_STYLE_VALUE;
    /**
     * 结果数据
     */
    public Object result;
    /**
     * 分析结果
     */
    public Object parseRes;
    /**
     * 是否分析完毕
     */
    protected boolean parsed = false;
    /**
     * 分析字符串
     */
    protected String expStr = "";
    /**
     * 分析字符串
     */
    public String getExpStr() {
        return this.expStr;
    }
    /**
     * 构造函数
     */
    public Expression(BlkExpression blkExp, String str) {
        this.blkExp = blkExp;
        this.expStr = str;
    }
    public BlkExpression blkExp;
    /**
     * 分析,分析后将parsed标志改为true
     */
    public void parse() throws HgException {
        this.parsed = true;
    }
    public boolean breaked = false;
    public ArrayList getChildExpList(Conn conn) throws HgException {
        return new ArrayList();
    }
    public String getExpPath(Conn conn, Expression exp) throws HgException {
        ArrayList list = this.getChildExpList(conn);
        Expression curExp;
        String path = null;
        for (int i = 0; i < list.size(); i++) {
            curExp = (Expression) list.get(i);
            if (curExp.equals(exp)) { //找到
                path = String.valueOf(i + 1);
                break;
            } else { //找子结点
                path = curExp.getExpPath(conn, exp);
                if (path != null) {
                    path = (i + 1) + "," + path;
                    break;
                }
            }
        }
        return path;
    }
    /**
     * 执行
     */
    public void eval(Conn conn) throws HgException {
        if (!this.parsed) this.parse();
    }
    /**
     * 将当前表达式中用到的变量或字段名称填入到list中,可以使用该list做查询优化
     * @param list
     * @throws HgException
     */
    public void fillVarList(ArrayList list) throws HgException {
    }
}
