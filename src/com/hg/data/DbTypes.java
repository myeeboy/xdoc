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

import java.sql.Types;
import java.util.Date;


/**
 * 数据类型
 * @author wanghg
 * @version 1.0
 */
public class DbTypes {
    public final static int BIT           = Types.BIT          ;
    public final static int TINYINT       = Types.TINYINT      ;
    public final static int SMALLINT      = Types.SMALLINT     ;
    public final static int INTEGER       = Types.INTEGER      ;
    public final static int BIGINT        = Types.BIGINT       ;
    public final static int FLOAT         = Types.FLOAT        ;
    public final static int REAL          = Types.REAL         ;
    public final static int DOUBLE        = Types.DOUBLE       ;
    public final static int NUMERIC       = Types.NUMERIC      ;
    public final static int DECIMAL       = Types.DECIMAL      ;
    public final static int CHAR          = Types.CHAR         ;
    public final static int VARCHAR       = Types.VARCHAR      ;
    public final static int LONGVARCHAR   = Types.LONGVARCHAR  ;
    public final static int DATE          = Types.DATE         ;
    public final static int TIME          = Types.TIME         ;
    public final static int TIMESTAMP     = Types.TIMESTAMP    ;
    public final static int BINARY        = Types.BINARY       ;
    public final static int VARBINARY     = Types.VARBINARY    ;
    public final static int LONGVARBINARY = Types.LONGVARBINARY;
    public final static int NULL          = Types.NULL         ;
    public final static int OTHER         = Types.OTHER        ;
    public final static int JAVA_OBJECT   = Types.JAVA_OBJECT  ;
    public final static int DISTINCT      = Types.DISTINCT     ;
    public final static int STRUCT        = Types.STRUCT       ;
    public final static int ARRAY         = Types.ARRAY        ;
    public final static int BLOB          = Types.BLOB         ;
    public final static int CLOB          = Types.CLOB         ;
    public final static int REF           = Types.REF          ;
    public final static int DATALINK      = Types.DATALINK     ;
    public final static int BOOLEAN       = Types.BOOLEAN      ;
    /**
     * 记录
     */
    public final static int ROW           = 9000               ;
    /**
     * 数据集
     */
    public final static int ROWSET        = 9001               ;
    /**
     * 整型数组
     */
    public final static int INTARRAY      = 9002               ;
    /**
     * 返回类型字符串表达形式
     * @param type
     * @return
     */
    public static String getTypeStr(int type) {
        String strType = "";
        if (type == BIT) {
            strType = "BIT";
        } else if (type == TINYINT) {
            strType = "TINYINT";
        } else if (type == SMALLINT) {
            strType = "SMALLINT";
        } else if (type == INTEGER) {
            strType = "INTEGER";
        } else if (type == BIGINT) {
            strType = "BIGINT";
        } else if (type == FLOAT) {
            strType = "FLOAT";
        } else if (type == REAL) {
            strType = "REAL";
        } else if (type == DOUBLE) {
            strType = "DOUBLE";
        } else if (type == NUMERIC) {
            strType = "NUMERIC";
        } else if (type == DECIMAL) {
            strType = "DECIMAL";
        } else if (type == CHAR) {
            strType = "CHAR";
        } else if (type == VARCHAR) {
            strType = "VARCHAR";
        } else if (type == LONGVARCHAR) {
            strType = "LONGVARCHAR";
        } else if (type == DATE) {
            strType = "DATE";
        } else if (type == TIME) {
            strType = "TIME";
        } else if (type == TIMESTAMP) {
            strType = "TIMESTAMP";
        } else if (type == BINARY) {
            strType = "BINARY";
        } else if (type == VARBINARY) {
            strType = "VARBINARY";
        } else if (type == LONGVARBINARY) {
            strType = "LONGVARBINARY";
        } else if (type == NULL) {
            strType = "NULL";
        } else if (type == OTHER) {
            strType = "OTHER";
        } else if (type == JAVA_OBJECT) {
            strType = "JAVA_OBJECT";
        } else if (type == DISTINCT) {
            strType = "DISTINCT";
        } else if (type == STRUCT) {
            strType = "STRUCT";
        } else if (type == ARRAY) {
            strType = "ARRAY";
        } else if (type == BLOB) {
            strType = "BLOB";
        } else if (type == CLOB) {
            strType = "CLOB";
        } else if (type == REF) {
            strType = "REF";
        } else if (type == DATALINK) {
            strType = "DATALINK";
        } else if (type == BOOLEAN) {
            strType = "BOOLEAN";
        } else if (type == ROW) {
            strType = "ROW";
        } else if (type == ROWSET) {
            strType = "ROWSET";
        }
        return strType;
    }
    /**
     * 返回类型JAVA字符串表达形式
     * @param type
     * @return
     */
	public static String getJavaTypeStr(int type) {
        String strType = "";
        if (type == BIT) {
            strType = "byte";
        } else if (type == TINYINT) {
            strType = "int";
        } else if (type == SMALLINT) {
            strType = "int";
        } else if (type == BIGINT) {
            strType = "int";
        } else if (type == BIGINT) {
            strType = "long";
        } else if (type == FLOAT) {
            strType = "float";
        } else if (type == REAL) {
            strType = "long";
        } else if (type == DOUBLE) {
            strType = "float";
        } else if (type == NUMERIC) {
            strType = "long";
        } else if (type == DECIMAL) {
            strType = "float";
        } else if (type == CHAR) {
            strType = "char";
        } else if (type == VARCHAR) {
            strType = "String";
        } else if (type == LONGVARCHAR) {
            strType = "String";
        } else if (type == DATE) {
            strType = "Date";
        } else if (type == TIME) {
            strType = "Date";
        } else if (type == TIMESTAMP) {
            strType = "Date";
        /*
        } else if (type == BINARY) {
            strType = "BINARY";
        } else if (type == VARBINARY) {
            strType = "VARBINARY";
        } else if (type == LONGVARBINARY) {
            strType = "LONGVARBINARY";
        } else if (type == NULL) {
            strType = "NULL";
        */
        } else if (type == OTHER) {
            strType = "Object";
        } else if (type == JAVA_OBJECT) {
            strType = "Object";
        /*    
        } else if (type == DISTINCT) {
            strType = "DISTINCT";
        } else if (type == STRUCT) {
            strType = "STRUCT";
        } else if (type == ARRAY) {
            strType = "ARRAY";
        } else if (type == BLOB) {
            strType = "BLOB";
        } else if (type == CLOB) {
            strType = "CLOB";
        } else if (type == REF) {
            strType = "REF";
        } else if (type == DATALINK) {
            strType = "DATALINK";
        */
        } else if (type == BOOLEAN) {
            strType = "boolean";
        } else if (type == ROW) {
            strType = "Row";
        } else if (type == ROWSET) {
            strType = "RowSet";
        } else {
            strType = "Object";
        }
        return strType;
	}
    public static int getObjType(Object obj) {
        int type = VARCHAR;
        if (obj instanceof String) {
            type = VARCHAR;
        } else if (obj instanceof Date) {
            type = DATE;
        } else if (obj instanceof Long) {
            type = BIGINT;
        } else if (obj instanceof Integer) {
            type = INTEGER;
        } else if (obj instanceof Boolean) {
            type = BOOLEAN;
        } else if (obj instanceof RowSet) {
            type = ROWSET;
        } else {
            type = OTHER;
        }
        return type;
    }
}
