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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

import com.hg.util.To;

public class ShapeUtil {
	public static GeneralPath shapeToPath(Shape s) {
		GeneralPath path = new GeneralPath();
	    PathIterator pi = s.getPathIterator(null);
	    float[] coordinates;
	    int type;
	    while (pi.isDone() == false) {
	        coordinates = new float[6];
	        type = pi.currentSegment(coordinates);
	        switch (type) {
	            case PathIterator.SEG_MOVETO:
	            	path.moveTo(coordinates[0], coordinates[1]);
	                break;
	            case PathIterator.SEG_LINETO:
	            	path.lineTo(coordinates[0], coordinates[1]);
	                break;
	            case PathIterator.SEG_QUADTO:
	            	path.quadTo(coordinates[0], coordinates[1],
	            			coordinates[2], coordinates[3]);
	                break;
	            case PathIterator.SEG_CUBICTO:
	            	path.curveTo(coordinates[0], coordinates[1],
	            			coordinates[2], coordinates[3],
	            			coordinates[4], coordinates[5]);
	                break;
	            case PathIterator.SEG_CLOSE:
	            	path.closePath();
	                break;
	            default:
	                break;
	        }
	        pi.next();
	    }
	    return path;
	}
	public static Shape getRectShape(int width, int height, String arc) {
		if (arc.equals("0") || arc.length() == 0) {
	        return new Rectangle(0, 0, width, height);
	    } else {
	        int a = 0;
	        int at = 15;
	        if (arc.indexOf(",") > 0) {
	            a = To.toInt(arc.substring(0, arc.indexOf(",")));
	            at = ShapeUtil.getArcConner(arc);
	        } else {
	            a = To.toInt(arc);
	        }
	        if (a < 0) {
	        	return new Ellipse2D.Double(0, 0, width, height);
	        }
	        if (a > Math.min(width / 2, height / 2)) {
	        	a = Math.min(width / 2, height / 2);
	        }
	        GeneralPath path = new GeneralPath();
	        if ((at & 1) == 1) {
	        	path.moveTo(0, a);
	        	path.quadTo(0, 0, a, 0);
	        } else {
	        	path.moveTo(0, 0);
	        }
	        if ((at & 2) == 2) {
	        	path.lineTo(width - a, 0);
	        	path.quadTo(width, 0, width, a);
	        } else {
	        	path.lineTo(width, 0);
	        }
	        if ((at & 4) == 4) {
	        	path.lineTo(width, height - a);
	        	path.quadTo(width, height, width - a, height);
	        } else {
	        	path.lineTo(width, height);
	        }
	        if ((at & 8) == 8) {
	        	path.lineTo(a, height);
	        	path.quadTo(0, height, 0, height - a);
	        } else {
	        	path.lineTo(0, height);
	        }
	        path.closePath();
	        return path;
	    }
	}

	public static int getArcConner(String arc) {
		int at = 15;
		if (arc.indexOf(',') > 0) {
			String atStr = arc.substring(arc.indexOf(',') + 1);
			if (atStr.length() == 4) {
				at = (atStr.charAt(0) == '1' ? 1 : 0)
						+ (atStr.charAt(1) == '1' ? 2 : 0)
						+ (atStr.charAt(2) == '1' ? 8 : 0)
						+ (atStr.charAt(3) == '1' ? 4 : 0);
			} else {
				at = To.toInt(atStr);
			}
		}
		return at;
	}

	/**
	 * 图形转换为图形字符串
	 * @param s
	 * @return
	 */
	public static String shapeToStr(Shape s) {
	    StringBuffer sb = new StringBuffer();
	    PathIterator pi = s.getPathIterator(null);
	    float[] coordinates;
	    int type;
	    while (pi.isDone() == false) {
	        if (sb.length() > 0) sb.append(" ");
	        coordinates = new float[6];
	        type = pi.currentSegment(coordinates);
	        switch (type) {
	            case PathIterator.SEG_MOVETO:
	                sb.append("M ");
	                sb.append(To.toString(coordinates[0])).append(" ");
	                sb.append(To.toString(coordinates[1]));
	                break;
	            case PathIterator.SEG_LINETO:
	                sb.append("L ");
	                sb.append(To.toString(coordinates[0])).append(" ");
	                sb.append(To.toString(coordinates[1]));
	                break;
	            case PathIterator.SEG_QUADTO:
	                sb.append("Q ");
	                sb.append(To.toString(coordinates[0])).append(" ");
	                sb.append(To.toString(coordinates[1])).append(" ");
	                sb.append(To.toString(coordinates[2])).append(" ");
	                sb.append(To.toString(coordinates[3]));
	                break;
	            case PathIterator.SEG_CUBICTO:
	                sb.append("C ");
	                sb.append(To.toString(coordinates[0])).append(" ");
	                sb.append(To.toString(coordinates[1])).append(" ");
	                sb.append(To.toString(coordinates[2])).append(" ");
	                sb.append(To.toString(coordinates[3])).append(" ");
	                sb.append(To.toString(coordinates[4])).append(" ");
	                sb.append(To.toString(coordinates[5]));
	                break;
	            case PathIterator.SEG_CLOSE:
	                sb.append("Z");
	                break;
	            default:
	                break;
	        }
	        pi.next();
	    }
	    return sb.toString();
	}

	/**
	 * 图形转换为图形字符串
	 * @param s
	 * @return
	 */
	public static String shapeToIntStr(Shape s) {
	    StringBuffer sb = new StringBuffer();
	    PathIterator pi = s.getPathIterator(null);
	    float[] coordinates;
	    int type;
	    while (pi.isDone() == false) {
	        if (sb.length() > 0) sb.append(" ");
	        coordinates = new float[6];
	        type = pi.currentSegment(coordinates);
	        for (int i = 0; i < coordinates.length; i++) {
	        	coordinates[i] = Math.round(coordinates[i]);
	        }
	        switch (type) {
	            case PathIterator.SEG_MOVETO:
	                sb.append("M ");
	                sb.append((int) coordinates[0]).append(" ");
	                sb.append((int) coordinates[1]);
	                break;
	            case PathIterator.SEG_LINETO:
	                sb.append("L ");
	                sb.append((int) coordinates[0]).append(" ");
	                sb.append((int) coordinates[1]);
	                break;
	            case PathIterator.SEG_QUADTO:
	                sb.append("Q ");
	                sb.append((int) coordinates[0]).append(" ");
	                sb.append((int) coordinates[1]).append(" ");
	                sb.append((int) coordinates[2]).append(" ");
	                sb.append((int) coordinates[3]);
	                break;
	            case PathIterator.SEG_CUBICTO:
	                sb.append("C ");
	                sb.append((int) coordinates[0]).append(" ");
	                sb.append((int) coordinates[1]).append(" ");
	                sb.append((int) coordinates[2]).append(" ");
	                sb.append((int) coordinates[3]).append(" ");
	                sb.append((int) coordinates[4]).append(" ");
	                sb.append((int) coordinates[5]);
	                break;
	            case PathIterator.SEG_CLOSE:
	                sb.append("Z");
	                break;
	            default:
	                break;
	        }
	        pi.next();
	    }
	    return sb.toString();
	}

	/**
	 * 图形字符串转换为图形
	 * @param str
	 * @return
	 */
	public static Shape strToShape(String str) {
	    str = str.trim().toUpperCase();
	    GeneralPath path = new GeneralPath();
	    ArrayList data = new ArrayList();
	    StringBuffer sb = new StringBuffer();
	    char type = ' ', c;
	    for (int i = 0; i < str.length(); i++) {
	        c = str.charAt(i);
	        if (c == 'M'
	            || c == 'L' 
	            || c == 'Q'
	            || c == 'C'
	            || c == 'Z') {
	            if (sb.length() > 0)
	                data.add(new Double(To.toDouble(sb.toString())));
	            sb.setLength(0);
	            ShapeUtil.processData(path, type, data);
	            type = c;
	        } else if (Character.isSpaceChar(c) || c == '\n' || c == ',') {
	            if (sb.length() > 0)
	                data.add(new Double(To.toDouble(sb.toString())));
	            sb.setLength(0);
	        } else {
	            sb.append(c);
	        }
	    }
	    if (sb.length() > 0) data.add(new Double(To.toDouble(sb.toString())));
	    ShapeUtil.processData(path, type, data);
	    return path;
	}

	private static void processData(GeneralPath path, char type, ArrayList data) {
	    if (type == 'M') {
	        for (int i = 0; i + 1 < data.size(); i += 2) {
	            path.moveTo(((Double) data.get(i)).floatValue(), ((Double) data.get(i + 1)).floatValue());
	        }
	    } else if (type == 'L') {
	        for (int i = 0; i + 1 < data.size(); i += 2) {
	            path.lineTo(((Double) data.get(i)).floatValue(), ((Double) data.get(i + 1)).floatValue());
	        }
	    } else if (type == 'Q') {
	        for (int i = 0; i + 3 < data.size(); i += 4) {
	            path.quadTo(((Double) data.get(i)).floatValue(), ((Double) data.get(i + 1)).floatValue(),
	                    ((Double) data.get(i + 2)).floatValue(), ((Double) data.get(i + 3)).floatValue());
	        }
	    } else if (type == 'C') {
	        for (int i = 0; i + 5 < data.size(); i += 6) {
	            path.curveTo(((Double) data.get(i)).floatValue(), ((Double) data.get(i + 1)).floatValue(),
	                    ((Double) data.get(i + 2)).floatValue(), ((Double) data.get(i + 3)).floatValue(),
	                    ((Double) data.get(i + 4)).floatValue(), ((Double) data.get(i + 5)).floatValue());
	        }
	    } else if (type == 'Z') {
	        path.closePath();
	    }
	    data.clear();
	}
}
