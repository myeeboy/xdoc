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
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.hg.util.To;
import com.hg.util.XUrl;

public class XFont {
    private static final String[] INNER_FONT_FILES = {"songti.ttf", "biaosong.ttf", "fangsong.ttf", "heiti.ttf", "cuhei.ttf", "xihei.ttf", "kaiti.ttf", "xingkai.ttf", "lishu.ttf", "weibei.ttf"};
    private static final String[] INNER_FONT_NAMES = {"宋体", "标宋", "仿宋", "黑体", "粗黑", "细黑", "楷体", "行楷", "隶书", "魏碑"};
    private static final String[] INNER_FONT_NAMES_EN = {"SongTi", "BiaoSong", "FangSong", "HeiTi", "CuHei", "XiHei", "KaiTi", "XingKai", "LiShu", "WeiBei"};
    private static String[] USER_FONT_FILES = {};
    private static String[] USER_FONT_NAMES = {};
    private static String[] USER_FONT_NAMES_EN = {};
    
    /**
     * 加载字体
     */
    public static void init(String fontPath) {
    	init(fontPath, null);
    }
    /**
     * 初始化加载内置字体
     */
    public static void init(String fontPath, String[] userFonts) {
        fontMap.clear();
        fontPathMap.clear();
        fontPath = XUrl.fixUrl(fontPath);
        File file = new File(fontPath);
        if (file.exists() && file.isDirectory()) {
        	for (int i = 0; i < INNER_FONT_NAMES.length; i++) {
        		loadFont(fontPath, INNER_FONT_FILES[i], INNER_FONT_NAMES[i], INNER_FONT_NAMES_EN[i]);
            }
        	if (userFonts == null) { //自动检测
        		File[] files = file.listFiles();
        		boolean inner = false;
        		ArrayList userFontFileNames = new ArrayList();
        		for (int i = 0; i < files.length; i++) {
        			if (files[i].getName().toLowerCase().endsWith(".ttf")) {
        				inner = false;
        				for (int j = 0; j < INNER_FONT_FILES.length; j++) {
        					if (files[i].getName().equals(INNER_FONT_FILES[j])) {
        						inner = true;
        						break;
        					}
        				}
        				if (!inner) {
        					userFontFileNames.add(files[i].getName());
        				}
        			}
        		}
        		userFonts = new String[userFontFileNames.size()];
        		Collections.sort(userFontFileNames);
        		for (int i = 0; i < userFontFileNames.size(); i++) {
        			userFonts[i] = userFontFileNames.get(i) + ":";
        		}
        	}
        	USER_FONT_FILES = new String[userFonts.length];
        	USER_FONT_NAMES = new String[userFonts.length];
        	USER_FONT_NAMES_EN = new String[userFonts.length];
        	String[] strs;
        	for (int i = 0; i < userFonts.length; i++) {
        		strs = userFonts[i].split(":");
        		USER_FONT_FILES[i] = strs[0];
        		if (strs.length > 1) {
        			USER_FONT_NAMES[i] = strs[1];
        			USER_FONT_NAMES_EN[i] = strs[1];
        		} else {
        			USER_FONT_NAMES[i] = "";
        			USER_FONT_NAMES_EN[i] = "";
        		}
        	}
        	String[] fontName;
        	for (int i = 0; i < USER_FONT_NAMES.length; i++) {
        		fontName = loadFont(fontPath, USER_FONT_FILES[i], USER_FONT_NAMES[i], USER_FONT_NAMES_EN[i]);
        		USER_FONT_NAMES[i] = fontName[0];
        		USER_FONT_NAMES_EN[i] = fontName[1];
            }
        } else {
        	for (int i = 0; i < INNER_FONT_NAMES.length; i++) {
    			registFont(INNER_FONT_NAMES[i], INNER_FONT_NAMES_EN[i], new Font(toWinName(INNER_FONT_NAMES[i]), Font.PLAIN, defaultFontSize));
            }
        	if (userFonts != null) {
        		USER_FONT_FILES = new String[userFonts.length];
        		USER_FONT_NAMES = new String[userFonts.length];
        		USER_FONT_NAMES_EN = new String[userFonts.length];
        		String[] strs;
        		for (int i = 0; i < userFonts.length; i++) {
            		strs = userFonts[i].split(":");
            		USER_FONT_FILES[i] = strs[0];
            		if (strs.length > 1) {
            			USER_FONT_NAMES[i] = strs[1];
            			USER_FONT_NAMES_EN[i] = strs[1];
            		} else {
            			USER_FONT_NAMES[i] = "";
            			USER_FONT_NAMES_EN[i] = "";
            		}
        			registFont(USER_FONT_NAMES[i], USER_FONT_NAMES_EN[i], new Font(toWinName(USER_FONT_NAMES[i]), Font.PLAIN, defaultFontSize));
        		}
        	}
        }
        defaultFontName = "SongTi";
        superFontName = "HeiTi";
        titleFontName = "BiaoSong";
        if (fontMap.containsKey(superFontName)) {
        	superFont = (Font) fontMap.get(superFontName);
        }
    }
	private static String[] loadFont(String fontPath, String fontFile, String fontName, String fontName2) {
		File file = new File(fontPath + fontFile);
		if (file.exists()) {
			Font font = createFont(file);
			if (font != null) {
				if (fontName.length() == 0) {
					fontName = font.getFontName(Locale.CHINA);
					fontName2 = font.getFontName(Locale.ENGLISH);
				}
				registFont(fontName, fontName2, font);
				fontPathMap.put(font.getFontName(Locale.ENGLISH), file.getAbsolutePath());
			}
		}
		if (fontName.length() == 0) {
			if (fontFile.toLowerCase().endsWith(".ttf")) {
				fontFile = fontFile.substring(0, fontFile.length() - 4);
			}
			fontName = fontFile;
			fontName2 = fontFile;
		}
		if (!fontMap.containsKey(fontName)) { //字体加载失败
			Font font = new Font(toWinName(fontName), Font.PLAIN, defaultFontSize);
			registFont(fontName, fontName2, font);
		}
		return new String[] {fontName, fontName2};
	}
	private static void registFont(String logicName, String logicName2, Font font) {
		fontMap.put(logicName, font);
		if (logicName2 != null) {
			fontMap.put(logicName2, font);
		}
		fontMap.put(font.getFontName(Locale.ENGLISH), font);
		fontMap.put(font.getFontName(Locale.CHINA), font);
	}
	public static Font createFont(File file) {
    	Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, file);
		} catch (NoSuchMethodError e) {
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(file));
			} catch (Exception e1) {
				e1.printStackTrace();;
			}
		} catch (Exception e) {
			e.printStackTrace();;
		}
		return font;
	}
    private static Map fontMap, fontPathMap;
    public static String getFontFilePath(Font font) {
    	return (String) fontPathMap.get(font.getFontName(Locale.ENGLISH));
    }
    static {
        fontMap = new HashMap();
        fontPathMap = new HashMap();
    }
    private static Font superFont = new Font("Dialog", Font.PLAIN, XFont.defaultFontSize);
    public static Font createFont(String name, int style, float size) {
    	if (name.length() == 0) {
    		name = XFont.defaultFontName;
    	}
        Font font = (Font) fontMap.get(name);
        if (font != null) {
        	font = font.deriveFont(style, size);
        } else {
        	font = new Font(name, style, (int) size);
        }
        return font;
    }
    public static Font createFont(String fontName, int style, int size, String str) {
    	Font font = createFont(fontName, style, size);
    	if (!canDisplay(font, str)) {
    		font = superFont.deriveFont(style, size);
    	}
    	return font;
    }
    /**
     * 字体是否能显示字符串
     * @param font
     * @param str
     * @return
     */
    public static boolean canDisplay(Font font, String str) {
    	if (str.length() > 0 && !font.getFontName(Locale.ENGLISH).startsWith("Source Han Sans CN")) {
    		for (int i = 0; i < str.length(); i++) {
    			if (!font.canDisplay(str.charAt(i))) {
    				return false;
    			}
    		}
    	}
    	return true;
    }
	/**
	 * 转换为内置字体名称
	 * @param name
	 * @return
	 */
	public static String toInnerName(String name) {
		if (name == null || name.length() == 0) {
			name = XFont.defaultFontName;
		} else if (!fontMap.containsKey(name)) {
			if (name.indexOf("宋") >= 0) {
				if (name.indexOf("仿宋") >= 0) {
					name = "仿宋";
				} else if (name.indexOf("标宋") >= 0 
						|| name.indexOf("粗宋") >= 0
						|| name.indexOf("中宋") >= 0) {
					name = "标宋";
				} else {
					name = "宋体";
				}
			} else if (name.indexOf("黑") >= 0) {
				if (name.indexOf("粗黑") >= 0) {
					name = "粗黑";
				} else if (name.indexOf("细黑") >= 0) {
					name = "细黑";
				} else {
					name = "黑体";
				}
			} else if (name.indexOf("楷") >= 0) {
				if (name.indexOf("行楷") >= 0) {
					name = "行楷";
				} else {
					name = "楷体";
				}
			} else if (name.indexOf("隶") >= 0) {
				name = "隶书";
			} else if (name.indexOf("魏") >= 0) {
				name = "魏碑";
			} else {
				name = XFont.defaultFontName;
			}	
		} 
		return name;
	}
	/**
	 * 转换为windows字体名称
	 * @param name
	 * @return
	 */
	public static String toWinName(String name) {
		if (name.equals("仿宋")) {
			name = "华文仿宋";
		} else if (name.equals("标宋")) {
			name = "华文中宋";
		} else if (name.equals("魏碑")) {
			name = "华文新魏";
		} else if (name.equals("行楷")) {
			name = "华文行楷";
		} else if (name.equals("楷体")) {
			name = "华文楷体";
		} else if (name.equals("细黑")) {
			name = "华文细黑";
		} else if (name.indexOf("黑") >= 0) {
			name = "黑体";
		}
		return name;
	}
	public static String superFontName = "黑体";
	public static String defaultFontName = "宋体";
    public static String titleFontName = "标宋";
	public static int defaultFontSize = 14;
    private static Map sizeMap;
    public static final int[] FONT_SIZE = new int[] {56,48,34,32,29,24,21,20,18,16,14,12,10,8,7,6};
    public static final String[] FONT_SIZE_CN = new String[] {"初号","小初","一号","小一","二号","小二","三号","小三","四号","小四","五号","小五","六号","小六","七号","八号"};
    public static final String[] FONT_SIZE_EN = new String[] {"42pt","36pt","26pt","24pt","22pt","18pt","16pt","15pt","14pt","12pt","10.5pt","9pt","7.5pt","6.5pt","5.5pt","5pt"};
    static {
    	sizeMap = new HashMap();
    	for (int i = 0; i < FONT_SIZE_CN.length; i++) {
    		sizeMap.put(FONT_SIZE_CN[i], new Integer(FONT_SIZE[i]));
    		sizeMap.put(FONT_SIZE_EN[i], new Integer(FONT_SIZE[i]));
    	}
    }
    public static int parseFontSize(String strSize) {
    	int fontSize = defaultFontSize;
    	strSize = strSize.toLowerCase();
        if (sizeMap.containsKey(strSize)) {
        	fontSize = ((Integer) sizeMap.get(strSize)).intValue();
        } else {
        	fontSize = To.toInt(strSize, fontSize);
        }
        if (fontSize <= 0) {
        	fontSize = defaultFontSize;
        }
        return fontSize;
    }
	public static Font createDefaultFont() {
		return createFont(defaultFontName, Font.PLAIN, defaultFontSize);
	}
	public static Font createOSFont(String name, int style, int size) {
		return createOSFont(name, style, size, "");
	}
	public static Font createOSFont(String name, int style, int size, String text) {
		Font font = new Font(name, Font.PLAIN, size);
		for (int i = 0; i < text.length(); i++) {
			if (!font.canDisplay(text.charAt(i))) {
				font = new Font("Dialog", Font.PLAIN, size);
			}
		}
		return font;
	}
}
