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
import java.util.HashMap;

import com.hg.util.HgException;

public class BlkExpression extends Expression {
    public BlkExpression(BlkExpression blkExp) {
        super(blkExp, "");
        this.expList = new ArrayList();
        this.varMap = new HashMap();
    }
    public String name;
    /**
     * 变量表
     */
    public HashMap varMap;
    /**
     * 表达式列表
     */
    public ArrayList expList;
    /**
     * 例外处理表达式列表
     */
    protected ArrayList exceptionExpList;
    public void eval(Conn conn) throws HgException {
        super.eval(conn);
        try {
            evalExpList(conn, expList);
        } catch (Exception e) {
            if (blkExp == null && e instanceof HgException 
                    && (e.getMessage().equals(HgException.SYS_SQL_RETURN) 
                            || e.getMessage().equals(HgException.SYS_SQL_EXIT))) { //最外层块监听返回和退出例外，不处理
            } else if (blkExp != null && e instanceof HgException 
                 && (e.getMessage().equals(HgException.SYS_SQL_RETURN) 
                        || e.getMessage().equals(HgException.SYS_SQL_EXIT))) { //非最外层块监听返回和退出例外，继续抛出
                throw (HgException) e;
            } else if (exceptionExpList != null) {
                this.varMap.put("ERROR", e.getMessage());
                evalExpList(conn, exceptionExpList);
            } else {
                if (e instanceof HgException) {
                    if (this.name != null) {
                        ((HgException) e).stackList.add(this.name);
                    }
                    throw (HgException) e;
                } else {
                    throw new HgException(e);
                }
            }
        }
    }
    private void evalExpList(Conn conn, ArrayList list) throws HgException {
        Expression exp;
        for (int i = 0; i < list.size(); i++) {
            exp = (Expression) list.get(i);
            exp.result = result;
            exp.resultDataStyle = resultDataStyle;
            exp.resultDataType = resultDataType;
            exp.eval(conn);
            this.result = exp.result;
            this.resultDataStyle = exp.resultDataStyle;
            this.resultDataType = exp.resultDataType;
        }
    }
    /**
     * 给变量赋值
     * @param name
     * @param value
     * @return
     * @throws HgException 
     */
    protected boolean setVar(Conn conn, String name, Object value) throws HgException {
        boolean b = false;
        if (varMap.containsKey(name)) { //局部变量
            varMap.put(name, value);
            b = true;
        } else if (name.indexOf('.') > 0) { //记录变量
            String rowName = name.substring(0, name.indexOf('.'));
            if (varMap.containsKey(rowName)) {
                Object obj = varMap.get(rowName);
                if (obj instanceof Row) {
                    String key = name.substring(name.indexOf('.') + 1);
                    int index = ((Row) obj).fieldIndex(key);
                    if (index >= 0) {
                        ((Row) obj).set(index, value);
                        b = true;
                    }
                }
            }
        }
//        if (MathExpression.odv != null && name.startsWith("$")) {
//            MathExpression.odv.set(conn, this, name.substring(1), value);
//            b = true;
//        }
        if (!b && blkExp != null) {
            b = blkExp.setVar(conn, name, value);
        }
        return b;
    }
    public void parse() throws HgException {
        Expression exp;
        for (int i = 0; i < expList.size(); i++) {
            exp = (Expression) expList.get(i);
            exp.parse();
        }
        if (exceptionExpList != null) {
            for (int i = 0; i < exceptionExpList.size(); i++) {
                exp = (Expression) exceptionExpList.get(i);
                exp.parse();
            }
        }
        parsed = true;
    }
    public Object getValue(String str) {
        Object obj = null;
        if (str.equals("RESULT")) {
            obj = result;
        } else if (varMap != null) {
            obj = varMap.get(str);
            if (obj == null) {
                if (str.indexOf('.') > 0) {
                    String varName = str.substring(0, str.indexOf('.'));
                    obj = varMap.get(varName);
                    if (obj != null && obj instanceof Row) {
                        obj = ((Row) obj).get(str.substring(str.indexOf('.') + 1));
                    } else {
                        if (blkExp != null) {
                            obj = blkExp.getValue(str);
                        }
                    }
                } else {
                    if (blkExp != null) {
                        obj = blkExp.getValue(str);
                    }
                }
            }
        }
        return obj;
    }
    /**
     * 将结果返回到最外层
     * @param result
     * @param resultDataStyle
     * @param resultDataType
     */
    protected void rtnResult(Object result, int resultDataStyle, int resultDataType) {
        this.result = result;
        this.resultDataStyle = resultDataStyle;
        this.resultDataType = resultDataType;
        if (blkExp != null) {
            blkExp.rtnResult(result, resultDataStyle, resultDataType);
        }
    }
    public ArrayList getChildExpList(Conn conn) throws HgException {
        ArrayList list = new ArrayList();
        if (!this.parsed) this.parse();
        list.addAll(this.expList);
        if (exceptionExpList != null) {
            list.add(new Expression(null, "<<EXCEPTION>>"));
            list.addAll(this.exceptionExpList);
        }
        return list;
    }
    public String getExpStr() {
        return "BEGIN";
    }
}
