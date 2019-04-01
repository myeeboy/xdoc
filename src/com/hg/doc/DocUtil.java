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
package com.hg.doc;

import java.awt.Font;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.dom4j.Document;

import com.hg.data.BlkExpression;
import com.hg.data.DbTypes;
import com.hg.data.MathExpression;
import com.hg.data.Parser;
import com.hg.data.RowSet;
import com.hg.util.DateUtil;
import com.hg.util.HgException;
import com.hg.util.MapUtil;
import com.hg.util.StrUtil;
import com.hg.util.XUrl;
import com.hg.util.XmlUtil;

public class DocUtil {
    public static EleBase cloneEle(XDoc doc, EleBase ele) {
        return XDocXml.toDocEle(doc, XDocXml.toXmlEle(ele));
    }
    public static EleRect getRect(XDoc xdoc, String name) {
    	EleRect rect = null;
    	EleBase ele = null;
        if (name.startsWith("<")) {
            try {
                ele = XDocXml.toDocEle(xdoc, XmlUtil.parseText(name).getRootElement());
            } catch (Exception e) {
            }
        }
        if (ele != null && ele instanceof EleRect) {
        	rect = (EleRect) ele;
        }
        return rect;
    }
    public static final float dpi = 96;

    public static void fixHeads(XDoc doc) {
        ArrayList tmpHeads = doc.heads;
        doc.heads = new ArrayList();
        Heading heading, tmpHeading;
        boolean find = false;
        for (int i = 0; i < tmpHeads.size(); i++) {
            heading = (Heading) tmpHeads.get(i);
            find = false;
            for (int j = i - 1; j >= 0; j--) {
                tmpHeading = (Heading) tmpHeads.get(j);
                if (tmpHeading.level() < heading.level()) {
                    if (tmpHeading.cheads == null) {
                        tmpHeading.cheads = new ArrayList();
                        tmpHeading.cheads.add(heading);
                    } else if (!((Heading) tmpHeading.cheads.get(tmpHeading.cheads.size() - 1)).name().equals(heading.name())) {
                        tmpHeading.cheads.add(heading);
                    } else {
                        tmpHeads.remove(i--);
                    }
                    find = true;
                    break;
                }
            }
            if (!find) {
                if (doc.heads.size() == 0 || !((Heading) doc.heads.get(doc.heads.size() - 1)).name().equals(heading.name())) {
                    doc.heads.add(heading);
                } else if (doc.heads.size() > 0) {
                    tmpHeads.remove(i--);
                }
            }
        }
    }
    /**
     * 非格式属性
     */
    public static final String[] noStyleAtt = new String[] {"name", "top", "left", "width", "height", "row", "rowSpan", "col", "colSpan", "line"};
    public static boolean isXDoc(String url) {
        if (url.endsWith(".xdoc")) {
            return true;
        } else if (url.endsWith(".zip")) {
            try {
                ZipInputStream zin = new ZipInputStream((new XUrl(url).getInputStream()));
                ZipEntry en = zin.getNextEntry();
                zin.close();
                return en.getName().endsWith(".xdoc");
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }
    public static void setCRectToView(EleRect rect) {
        EleRect crect;
        Point p = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        for (int i = 0; i < rect.eleList.size(); i++) {
            if (rect.eleList.get(i) instanceof EleRect) {
                crect = (EleRect) rect.eleList.get(i);
                if (crect.left < p.x) {
                    p.x = crect.left;
                }
                if (crect.top < p.y) {
                    p.y = crect.top;
                }
            }
        }
        for (int i = 0; i < rect.eleList.size(); i++) {
            if (rect.eleList.get(i) instanceof EleRect) {
                crect = (EleRect) rect.eleList.get(i);
                crect.left -= p.x;
                crect.top -= p.y;
            }
        }
    }
    public static XDoc errDoc(Throwable t) {
    	return DocUtil.strDoc(HgException.getStackTraceMsg(t));
    }
    public static XDoc strDoc(String str) {
    	XDoc xdoc = new XDoc();
    	ElePara para = new ElePara(xdoc);
    	xdoc.paraList.add(para);
    	EleText txt = new EleText(xdoc);
    	txt.text = str;
    	para.eleList.add(txt);
    	return xdoc;
    }
    public static List toTabs(XDoc xdoc) {
        ArrayList tabs = new ArrayList();
        for (int i = 0; i < xdoc.rectList.size(); i++) {
            tabs.add(toTab((EleBase) xdoc.rectList.get(i)));
        }
        ElePara para;
        for (int i = 0; i < xdoc.paraList.size(); i++) {
            para = (ElePara) xdoc.paraList.get(i);
            for (int j = 0; j < para.eleList.size(); j++) {
                if (para.eleList.size() == 1
                        && para.eleList.get(0) instanceof EleText
                        && ((EleText) para.eleList.get(0)).text.length() == 0) {
                    continue;
                }
                tabs.add(toTab((EleBase) para.eleList.get(j)));
            }
        }
        return tabs;
    }
    private static EleTable toTab(EleBase ele) {
        EleTable tab;
        if (ele instanceof EleTable) {
            tab = (EleTable) ele;
        } else if (ele instanceof EleRect) {
            EleRect rect = (EleRect) ele;
            tab = new EleTable(rect.xdoc);
            tab.rows = String.valueOf(rect.height);
            tab.cols = String.valueOf(rect.width);
            tab.eleList.add(new EleCell(rect));
        } else {
            tab = new EleTable(ele.xdoc);
            if (ele instanceof EleText) {
                EleRect rect = new EleRect(ele.xdoc);
                ElePara para = new ElePara(ele.xdoc);
                para.eleList.add(ele);
                rect.eleList.add(para);
                tab.eleList.add(new EleCell(rect));
            }
        }
        return tab;
    }
    public static EleBase copy(XDoc xdoc, EleBase ele) {
        return XDocXml.toDocEle(xdoc, XDocXml.toXmlEle(ele));
    }
    private static int[] headingSize = new int[] {29,24,21,20,18,16};
    public static int headingFontSize(int n) {
    	return (n == 0 || n > headingSize.length) ? XFont.defaultFontSize : headingSize[n - 1];
    }
    public static int headingSpacing(int n) {
    	int spacing = 0;
    	if (n > 0) {
    		if (n < headingSize.length) {
    			spacing = headingSize[n - 1] / 2 * 2;
    		} else if (n > 0 && n < headingSize.length) {
    			spacing = 4;
    		}
    	}
    	return spacing;
    }
    public static void setHeadingStyle(ElePara para, int n) {
        para.heading = n;
        if (para.eleList.size() == 1 && para.eleList.get(0) instanceof EleText) {
        	EleText txt = ((EleText) para.eleList.get(0));
            txt.fontName = n != 0 ? XFont.titleFontName : XFont.defaultFontName;
            txt.fontSize = headingFontSize(n); 
            txt.valign =  n == 0 ? "bottom" : "center";
        }
        para.lineSpacing = headingSpacing(n);
    }
    public static void autoSize(EleBase ele) {
        for (int i = 0; i < ele.eleList.size(); i++) {
            autoSize((EleBase) ele.eleList.get(i));
        }
        if (ele instanceof EleRect) {
            ((EleRect) ele).autoSize();
        }
    }
    public static boolean isFirstPage(Map paramMap) {
        return  MapUtil.getBool(paramMap, "firstPage", false);
    }
    /**
     * 根据流内容识别格式
     * @param in
     * @return
     * @throws IOException
     * @throws HgException 
     */
    public static String dataFormat(String urlStr) throws Exception {
        //自动识别格式
        XUrl url = new XUrl(urlStr);
        InputStream in = url.getInputStream();
        String format = "txt";
        byte[] buf = new byte[256];
        in.read(buf);
        String str = new String(buf).toLowerCase();
        if (str.startsWith("{\\rtf1")) {
            format = "rtf";
        } else if (str.startsWith("pk")) {
            format = "zip";
        } else if (str.startsWith("<html") || str.startsWith("<!doctype html")) {
            format = "html";
        } else if (str.startsWith("<?xml")) {
            format = "xml";
        } else if (str.startsWith("邢")) {
            format = "doc";
        }
        in.close();
        return format;
    }
    public static Font getDefultFont() {
        return XFont.createFont(XFont.defaultFontName, Font.PLAIN, XFont.defaultFontSize);
    }
    /**
     * 修正为绝对路径
     * @param path
     * @param doc
     * @return
     */
    public static String fixPath(String path, Document doc) {
        if (!path.startsWith("/") && doc != null && doc.getRootElement() != null) {
            path = "/" + doc.getRootElement().getName() + "/" + path;
        }
        return path;
    }
    public static boolean withSource = false;
    public static boolean prettyFormat = false;
	public static boolean isWithSource(Map params) {
		return MapUtil.getBool(params, "_source", withSource);
	}
    private static final String[] fullFormat = new String[] {"xdoc", "json", "zip", "pdf", "docx", "jar", "jpd", "fpd", "epub", "png"};
    public static boolean isFullFormat(String format) {
        for (int i = 0; i < fullFormat.length; i++) {
            if (fullFormat[i].equals(format)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 修改段落文字属性
     * @param e
     * @param fontName
     * @param fontSizeChange
     */
    public static void setPTAtt(EleBase e, String fontName, int fontSizeChange,int lineSpacing, int indentLeft, int indentRight) {
        ElePara para;
        EleText txt;
        EleBase ce;
        for (int i = 0; i < e.eleList.size(); i++) {
            ce = (EleBase) e.eleList.get(i);
            if (ce instanceof EleText) {
                txt = (EleText) ce;
                if (fontName.length() > 0) {
                	txt.fontName = fontName;
                }
                txt.fontSize = txt.fontSize + fontSizeChange;
                if (txt.fontSize < 6) {
                	txt.fontSize = 6;
                }
            } else if (ce instanceof ElePara) {
                para = (ElePara) ce;
                para.indentLeft += indentLeft;
                para.indentLeft += indentRight;
                para.lineSpacing += lineSpacing;
                setPTAtt(ce, fontName, fontSizeChange, lineSpacing, indentLeft, indentRight);
            } else {
                setPTAtt(ce, fontName, fontSizeChange, lineSpacing, indentLeft, indentRight);
            }
        }
    }
	public static int getMaxLoop() {
		return maxLoop;
	}
	/**
	 * 最大循环数
	 */
	private static int maxLoop = -1;
	public static String fixHref(String href, String name) {
		return href.equals("#") ? DocUtil.INNER_HREF_PREFIX + name : href;
	}
	/**
	 * 文档内内部HREF前缀
	 */
	public static final String INNER_HREF_PREFIX = "@:";
	public static ThreadLocal tlUserDocPath = new ThreadLocal();
	public static String serverDocPath = "";
	public static String getUserDocPath() {
		String upath = (String) tlUserDocPath.get();
		return upath != null ? upath : serverDocPath;
	}
	public static boolean isDynamic(String url) {
		return url.startsWith("{")
			|| url.startsWith("<")
			|| url.startsWith("text:")
			|| url.startsWith("data:");
	}
	public static boolean isBlank(EleRect base) {
		return base.name.equals(DocConst.BLANK_RECT) || base.name.startsWith(DocConst.BLANK_RECT_PREFIX);
	}
	public static String fixFileName(String name) {
		return StrUtil.replaceAll(name, "/", "_");
	}
	public static String fixStoreId(String id) {
		String format = "xdat";
		id = id.toLowerCase();
		int pos = id.lastIndexOf('.');
		if (pos >= 0) {
			format = id.substring(pos + 1);
			id = id.substring(0, pos);
		}
		StringBuffer sb = new StringBuffer("_");
		int i = 0;
		if (id.startsWith("a_")) {
			sb.append("a_");
			i = 2;
		}
		char c;
		for (; i < 32; i++) {
			if (i < id.length()) {
				c = id.charAt(i);
				if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z') {
					sb.append(c);
				} else {
					sb.append((char) (((int) c) % 26 + (int) 'a'));
				}
			} else {
				sb.append('0');
			}
		}
		sb.append(".").append(format);
		return sb.toString();
	}
	public static String simplifyIdName(String name) {
		int pos = name.lastIndexOf(".");
		if (pos > 0 && name.startsWith("_")) {
			for (int i = pos - 1; i >= 0; i--) {
				if (name.charAt(i) != '0') {
					name = name.substring(0, i + 1) + name.substring(pos);
					break;
				}
			}
		}
		return name;
	}
    /**
     * 执行打印期表达式
     * @param text
     * @param xdoc
     * @return
     */
    public static String printEval(String text, XDoc xdoc) {
        if (text.length() > 0 && text.indexOf(DocConst.PRINTMARK_PRE) >= 0) {
            String str = text;
            //分解出表达式计算
            StringBuffer sb = new StringBuffer();
            int pos = str.indexOf(DocConst.PRINTMARK_PRE);
            if (pos >= 0) {
                String expStr;
                BlkExpression blkExp = new BlkExpression(null);
                blkExp.varMap.put("PAGE", new Long(xdoc.getViewPage()));
                blkExp.varMap.put("PAGES", new Long(xdoc.getViewPages()));
                blkExp.varMap.put("PAGENO", new Long(xdoc.getViewPage()));
                blkExp.varMap.put("PAGECOUNT", new Long(xdoc.getViewPages()));
                blkExp.varMap.put("HEADING", xdoc.getViewHeading());
                while (pos >= 0) {
                    if (pos > 0) {
                        sb.append(str.substring(0, pos));
                    }
                    str = str.substring(pos + DocConst.PRINTMARK_PRE.length());
                    pos = str.indexOf(DocConst.PRINTMARK_POST);
                    if (pos > 0) {
                        expStr = str.substring(0, pos).trim();
                        if (expStr.length() > 0) {
                        	if (expStr.charAt(0) == '\\') {
                    			sb.append(DocConst.PRINTMARK_PRE).append(str.substring(1, pos)).append(DocConst.PRINTMARK_POST);
                        	} else {
                        		try {
                        			expStr = Parser.pretreat(expStr)[0];
                        			MathExpression mathExp = new MathExpression(blkExp, expStr);
                        			mathExp.eval(null);
                        			if (mathExp.resultDataType == DbTypes.DATE) {
                        				sb.append(DateUtil.toDateTimeString((Date) mathExp.result));
                        			} else {
                        				if (mathExp.result instanceof RowSet) {
                        					RowSet rowSet = (RowSet) mathExp.result;
                        					if (rowSet.size() > 0 && rowSet.fieldSize() > 0) {
                        						sb.append(rowSet.getCellValue(0, 0));
                        					}
                        				} else {
                        					sb.append(mathExp.result);
                        				}
                        			}
                        		} catch (Exception e) {
                        			sb.append(DocConst.PRINTMARK_PRE).append(str.substring(0, pos)).append("/*").append(e.getMessage()).append("*/").append(DocConst.PRINTMARK_POST);
                        		}
                        	}
                        }
                        str = str.substring(pos + DocConst.PRINTMARK_POST.length());
                    } else if (pos == 0) {
                        str = str.substring(1);
                    } else {
                    	str = DocConst.PRINTMARK_PRE + str;
                    	break;
                    }
                	pos = str.indexOf(DocConst.PRINTMARK_PRE);
                }
            }
            sb.append(str);
            return sb.toString();
        } else {
            return text;
        }
    }

}
