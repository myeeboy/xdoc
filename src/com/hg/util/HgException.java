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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 例外
 * @author wanghg
 * @version 1.0
 */
public class HgException extends Exception {
    
    public static final String SYS_SQL_EXIT = "$SYS_SQL_EXIT$";
    public static final String SYS_SQL_RETURN = "$SYS_SQL_RETURN$";
    public ArrayList stackList = new ArrayList();
    /**
     * @param message
     */
    public HgException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public HgException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public HgException(Throwable cause) {
        super(cause instanceof HgException ? (cause.getMessage()) : null, cause instanceof HgException ? cause.getCause() : cause);
    }
    /**
     * 消息
     */
    public String getMessage() {
    	String str = super.getMessage();
    	if (str == null) { //解释各种例外
    		Throwable throwable = super.getCause();
            if (throwable instanceof UnknownHostException) {
                str = "不能与服务器" + throwable.getMessage() + "连接";
            } else if (throwable instanceof ConnectException) {
                str = "不能连接到服务器";
            } else if (throwable instanceof HgException) {
                str = throwable.getMessage();
    		} else {
    			str = throwable.toString();
    		}
    	}
        if (this.stackList.size() > 0) {
            for (int i = this.stackList.size(); i > 0; i--) {
                str += "\n" + this.stackList.get(i - 1);
            }
        }
    	return str;
    }

    /**
     * 取得错误信息
     * @param code
     * @param params
     * @return
     */
    public static String getMsg(String code, String[] params) {
        String msg = (String) exceptMap.get(code);
        if (msg != null) {
            if (msg.indexOf("&") >= 0) {
                String[] strs = msg.split("&");
                StringBuffer sb = new StringBuffer();
                int n;
                for (int i = 0; i < strs.length; i++) {
                    if (i % 2 == 1) {
                        n = To.toInt(strs[i]);
                        if (n < params.length) {
                            sb.append(params[n]);
                        }
                    } else {
                        sb.append(strs[i]);
                    }
                }
                msg = sb.toString();
            }
        } else {
            msg = code;
        }
        return msg;
    }
    public String getStackTraceMsg() {
        return getStackTraceMsg(this);
    }
    public static String getStackTraceMsg(Throwable e) {
    	StringWriter sw = new StringWriter();
    	e.printStackTrace(new PrintWriter(sw));
    	return sw.toString();
    }
    public static String getStackMsg(Throwable e) {
    	Throwable cause = e.getCause();
    	String stackMsg = null;
    	if (cause != null) {
    		stackMsg = getStackMsg(cause);
    	}
    	return e.getMessage() + (stackMsg != null ? "\n" + stackMsg : "");
    }
    /**
     * 例外表
     */
    public static HashMap exceptMap = new HashMap();
}
