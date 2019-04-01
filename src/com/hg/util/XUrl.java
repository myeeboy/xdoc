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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class XUrl {
	public String urlStr;
    public XUrl(String urlStr) {
        this.urlStr = urlStr;
    }
    public InputStream getInputStream() throws HgException, FileNotFoundException {
	    try {
	        if (isFileUrl(this.urlStr)) {
	        	return new FileInputStream(this.urlStr);
	        } else if (urlStr.indexOf(":") > 0) {
	            String protocol = urlStr.substring(0, urlStr.indexOf(":")).toUpperCase();
                if (protocol.equals("CLASS")) {
                    String cls = this.urlStr.substring(8, this.urlStr.indexOf("/", 8));
                    String path = this.urlStr.substring(this.urlStr.indexOf("/", 8) + 1);
                    InputStream in = Class.forName(cls).getResourceAsStream(path);
                    if (in != null) {
                        return in;
                    } else {
                        throw new FileNotFoundException(this.urlStr);
                    }
                } else if (protocol.equals("DATA")) { //dataURI
                    int pos = this.urlStr.indexOf(',');
                    if (pos > 0) {
                    	String prefix = this.urlStr.substring(5, pos);
                    	if (prefix.endsWith(";base64")) {
                    		prefix = prefix.substring(0, prefix.length() - 7);
                    		return new ByteArrayInputStream(StrUtil.fromBase64(this.urlStr.substring(pos + 1)));
                    	} else {
                    		String[] strs = prefix.split(";");
                    		String charset = "utf-8";
                    		for (int i = 1; i < strs.length; i++) {
                    			if (strs[i].startsWith("charset=")) {
                    				charset = strs[i].substring(8);
                    				break;
                    			}
                    		}
                    		String data = URLDecoder.decode(this.urlStr.substring(pos + 1), charset);
                    		return new ByteArrayInputStream(data.getBytes("utf-8"));
                    	}
                    } else {
                        return new ByteArrayInputStream(new byte[0]);
                    }
                } else {
                    return openInputStream(getURLConnection(urlStr));
	            }
	        } else {
                return new ByteArrayInputStream(new byte[0]);
	        }
	    } catch (FileNotFoundException e) {
	    	throw e;
	    } catch (Exception e2) {
	        throw new HgException(e2);
	    }
	}
    public OutputStream getOutputStream() throws HgException {
	    try {
	        URLConnection conn = getURLConnection(urlStr);
	        if (conn instanceof HttpURLConnection) {
	        	HttpURLConnection httpConn = (HttpURLConnection) conn;
	        	httpConn.setRequestMethod("POST");
	        	httpConn.setRequestProperty("Content-Type", "application/octet-stream");
	        	httpConn.setDoOutput(true);
	        }
	        return conn.getOutputStream();
	    } catch (Exception e) {
	        throw new HgException(e);
	    }
	}
    private InputStream openInputStream(URLConnection urlConn) throws IOException {
        return urlConn.getInputStream();
    }
    private static String encodeURI(String str) {
        char[] cs = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] >= 'a' && cs[i] <='z'
                || cs[i] >= 'A' && cs[i] <='Z'
                || cs[i] >= '0' && cs[i] <='9'
                || "-_.!~*'();/?:@&=+$,#".indexOf(cs[i]) >= 0) {
                sb.append(cs[i]);
            } else if (cs[i] == ' ') {
                sb.append('+');
            } else {
                try {
                    sb.append(URLEncoder.encode(String.valueOf(cs[i]), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    sb.append(cs[i]);
                }
            }
        }
        return sb.toString();
    }
	private URLConnection getURLConnection(String urlStr) throws HgException {
        try {
            String propStr = "";
            int propPos = urlStr.indexOf("{");
            if (propPos >= 0 && urlStr.indexOf("}", propPos) > 0) {
                propStr = urlStr.substring(propPos + 1, urlStr.indexOf("}", propPos)).trim();
                urlStr = urlStr.substring(0, propPos) 
                	+ urlStr.substring(urlStr.indexOf("}", propPos) + 1);
            }
            URL url = new URL(encodeURI(urlStr));
            URLConnection urlConn = url.openConnection();
            if (propStr.length() > 0) {
            	String[] props = propStr.split(";");
            	for (int i = 0; i < props.length; i++) {
            		if (url.getProtocol().toLowerCase().equals("http")
            				&& props[i].substring(0, props[i].indexOf("=")).equals("loginUrl")) {
            			HttpURLConnection.setFollowRedirects(false);
            			String cookie = "";
            			URL loginUrl = new URL(props[i].substring(props[i].indexOf("=") + 1));
            			HttpURLConnection httpConn = (HttpURLConnection) loginUrl.openConnection();
            			httpConn.setInstanceFollowRedirects(false);
            			cookie = httpConn.getHeaderField("Set-Cookie");
            			urlConn.addRequestProperty("Cookie", cookie);
            		} else {
            			urlConn.addRequestProperty(props[i].substring(0, props[i].indexOf("=")), 
            					props[i].substring(props[i].indexOf("=") + 1));
            		}
            	}
            }
            if (url.getProtocol().equals("http")) {
                HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                httpConn.setRequestMethod("GET"); 
                setAgent(httpConn);
            }
            return urlConn;
        } catch (Exception e) {
            throw new HgException(e);
        }
    }
    /**
     * 修正文件目录，末尾没有目录分割符，自动补
     * @param urlStr
     * @return
     */
	public static String fixUrl(String urlStr) {
        if (urlStr.length() > 0// && (HgUrl.isFileUrl(urlStr) || urlStr.startsWith("class:")) 
        		&& !urlStr.endsWith("/") && !urlStr.endsWith("\\")) {
            urlStr += "/";
        }
        urlStr = urlStr.replace('\\', '/');
        return urlStr;
	}
    public String getName() {
        String name = urlStr;
        if (name.indexOf('?') >= 0) {
        	name = name.substring(0, name.indexOf('?'));
        }
        if (name.indexOf('/') >= 0) {
        	name = name.substring(name.lastIndexOf('/') + 1);
        }
        if (name.indexOf('\\') >= 0) {
        	name = name.substring(name.lastIndexOf('\\') + 1);
        }
        return name;
    }
    public static void setAgent(HttpURLConnection conn) {
		conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
	}
	private static boolean isFileUrl(String str) {
	    return str.indexOf(":") < 2 || str.startsWith("/");
	}
}

