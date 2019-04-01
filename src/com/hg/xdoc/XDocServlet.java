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
package com.hg.xdoc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hg.doc.DocUtil;
import com.hg.doc.ImgPrint;
import com.hg.doc.PdfPrint;
import com.hg.doc.XDocXml;
import com.hg.doc.XFont;
import com.hg.util.StrUtil;
import com.hg.util.XUrl;

/**
 * XDocServlet
 * @author wanghg
 */
public class XDocServlet extends HttpServlet {
    /**
     * 参数
     */
    public void init() throws ServletException {
    	super.init();
        String realPath = null;
        if (this.getServletContext() != null) {
            realPath = this.getServletContext().getRealPath("/");   
        }
        if (realPath == null || realPath.length() == 0) {
            String path = XDocServlet.class.getResource("XDocServlet.class").getPath();
            if (path.startsWith("file:")) {
            	try {
					path = new URI(path).getPath();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
            }
            realPath = path.substring(0, path.lastIndexOf("WEB-INF"));
        }
        realPath = XUrl.fixUrl(realPath);
        XFont.init(realPath + "WEB-INF/font");
	}
	/**
     * doGet
     * @throws IOException 
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
    /**
     * doPost
     * @throws IOException 
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
    	ServletOutputStream out = response.getOutputStream();
    	response.setContentType("application/pdf");
    	String xdoc = null;
    	try {
    		xdoc = request.getParameter("_xdoc");
    		if ("true".equals(request.getParameter("_de"))) {
    			xdoc = StrUtil.URLDecode(xdoc);
    		}
		} catch (Exception e) {
			xdoc = XDocXml.toXml(DocUtil.errDoc(e));
		}
		try {
			String format = request.getParameter("_format");
			if (format == null) {
				format = "pdf";
			} else if (format.equals("jpg")) {
				format = "jpeg";
			}
			if (format.equals("pdf")) {
				response.setContentType("application/pdf");
			} else if (format.equals("png") || format.equals("jpeg")) {
				response.setContentType("image/" + format);
			} else {
				response.setContentType("text/xml;charset=utf-8");
			}
			XDoc.write(xdoc, out, format);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	out.flush();
    	out.close();
    }
}
