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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.hg.util.ColorUtil;
import com.hg.util.To;

/**
 * 绘图工具类
 * @author wanghg
 */
public class PaintUtil {
	public static boolean IS_OPEN_JDK = false;
	public static boolean TEXT_AS_SHAPE = false;
	public static boolean PDF_EMBED_FONT = true;
	static {
		try {
			IS_OPEN_JDK = System.getProperty("java.runtime.name", "").startsWith("OpenJDK");
			if (IS_OPEN_JDK) {
				//SINA.OPENJDK BUG
				TEXT_AS_SHAPE = true;
			}
		} catch (Throwable e) {
		}
	}
	public static void drawString(Graphics2D g, String str, int x, int y) {
		drawString(g, str, x, y, 0);
	}
	public static void drawString(Graphics2D g, String str, int x, int y, int spacing) {
		if (spacing != 0) {
			drawShapeString(g, str, x, y, spacing);
		} else if (isPdfGraphics(g)) {
			Font font = g.getFont();
			if (!PDF_EMBED_FONT 
					|| font.isBold() //粗体stroke可能出现尖锐，itext输出粗体描边可能错误
					|| XFont.getFontFilePath(font) == null) {
				drawShapeString(g, str, x, y, 0);
			} else {
				g.drawString(str, x, y);
			}
		} else if (TEXT_AS_SHAPE) {
			drawShapeString(g, str, x, y, 0);
		} else {
			g.drawString(str, x, y);
		}
	}
	private static boolean isPdfGraphics(Graphics2D g) {
		return g.getClass().getName().equals("com.lowagie.text.pdf.PdfGraphics2D");
	}
	private static void drawShapeString(Graphics2D g, String str, int x, int y, int spacing) {
		g.fill(getOutline(str, g.getFont(), x, y, spacing));
	}
	public static Shape getOutline(String str, Font font) {
		return getOutline(str, font, 0, 0, 0);
	}
	public static Shape getOutline(String str, Font font, int x, int y) {
		return getOutline(str, font, x, y, 0);
	}
	public static Shape getOutline(String str, Font font, int x, int y, int spacing) {
		if (spacing != 0) {
			if (IS_OPEN_JDK) {
				int n = str.length();
				GeneralPath path = new GeneralPath();
				AffineTransform af = new AffineTransform();
				af.translate(x + spacing / 2, y);
				Shape shape;
				for (int i = 0; i < n; i++) {
					shape = new TextLayout(str.substring(i, i + 1), font, DocConst.frc).getOutline(af);
					path.append(shape, false);
					af.translate(spacing + shape.getBounds2D().getWidth(), 0);
				}
				return path;
			} else {
				GlyphVector glyphVector = font.createGlyphVector(DocConst.frc, str);
				int n = glyphVector.getNumGlyphs();
				GeneralPath path = new GeneralPath();
				for (int i = 0; i < n; i++) {
					path.append(glyphVector.getGlyphOutline(i, x + i * spacing + spacing / 2, y), false);
				}
				return path;
			}
		} else if (IS_OPEN_JDK) {
    		TextLayout tl = new TextLayout(str, font, DocConst.frc);
    		if (x == 0 && y == 0) {
    			return tl.getOutline(null);
    		} else {
    			AffineTransform at = new AffineTransform();
    			at.translate(x, y);
    			return tl.getOutline(at);
    		}
		} else {
			return font.createGlyphVector(DocConst.frc, str).getOutline(x, y);
		}
	}
	public static void drawError(Graphics2D g, Throwable t) {
		drawError(g, String.valueOf(t.getMessage()));
		g.setColor(Color.RED);
		drawOutlineString(g, t.getClass().getName(), 2, g.getFont().getSize() * 2 + 2, Color.WHITE);
	}
	public static void drawError(Graphics2D g, String msg) {
		g.setFont(DocUtil.getDefultFont());
		g.setColor(Color.RED);
		drawOutlineString(g, msg, 2, g.getFont().getSize() + 2, Color.WHITE);
	}
	public static void drawOutlineString(Graphics2D g2, String value, int x, int y) {
		drawOutlineString(g2, value, x, y, null);
	}
	public static void drawOutlineString(Graphics2D g2, String value, int x, int y, int spacing) {
		drawOutlineString(g2, value, x, y, null, spacing);
	}
	public static void drawOutlineString(Graphics2D g2, String value, int x, int y, Color color) {
		drawOutlineString(g2, value, x, y, color, 0);
	}
	public static void drawOutlineString(Graphics2D g2, String value, int x, int y, Color color, int spacing) {
		Color c = g2.getColor();
		Font valueFont = g2.getFont();
		
		g2.setColor(color != null ? color : ColorUtil.invert(c));
		g2.setStroke(new BasicStroke(valueFont.getSize2D() / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.draw(getOutline(value, valueFont, x, y, spacing));
		g2.setStroke(new BasicStroke());
		
		g2.setColor(c);
		drawString(g2, value, x, y, spacing);
	}
    public static void drawOutlineShape(Graphics2D g, GeneralPath p) {
    	Stroke s = g.getStroke();
    	Color c = g.getColor();
		g.setColor(ColorUtil.invert(c));
		g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(p);
        g.setStroke(s);
        g.setColor(c);
        g.fill(p);
	}
	public static void setPaint(Graphics2D g, XDoc xdoc, Color color, String img) {
	    BufferedImage imgFill = ImgUtil.loadImg(xdoc, img, color, Color.WHITE, false);;
	    if (imgFill != null) {
			TexturePaint paint = new TexturePaint(imgFill, new Rectangle(0, 0, imgFill.getWidth(), imgFill.getHeight()));
	    	paint = PaintUtil.checkPaint(g, paint);
	    	g.setPaint(paint);
		} else if (color != null) {
	        g.setPaint(color);
	    }
	}
	public static void fill(Graphics2D g, XDoc xdoc, Shape shape,
			Color fillColor, String fillImg) {
		PaintUtil.fill(g, xdoc, shape, fillColor, fillImg, "", null);
	}
	public static void fill(Graphics2D g, XDoc xdoc, Shape shape,
			Color fillColor, String fillImg, String gradual, Color fillColor2) {
		if ((fillImg.startsWith("<") || fillImg.startsWith("["))) {
	        EleRect rect = DocUtil.getRect(xdoc, fillImg);
	        if (rect != null) {
	            Rectangle bounds = shape.getBounds();
	            Graphics cg = g.create(bounds.x, bounds.y, bounds.width, bounds.height);
	            AffineTransform af = new AffineTransform();
	            af.translate(-bounds.x, -bounds.y);
	            Shape shape2 = af.createTransformedShape(shape);
	            cg.setClip(shape2);
	            int xn = (int) Math.round(bounds.width / (double) rect.width);
	            int yn = (int) Math.round(bounds.height / (double) rect.height);
	            if (xn <= 0) {
	            	xn = 1;
	            }
	            if (yn <= 0) {
	            	yn = 1;
	            }
	            double sw = bounds.width / (xn * (double) rect.width);
	            double sh = bounds.height / (yn * (double) rect.height);
	            Graphics2D cg2;
	            for (int i = 0; i < xn; i++) {
	                for (int j = 0; j < yn; j++) {
	                    cg2 = (Graphics2D) cg.create((int) (i * rect.width * sw), (int) (j * rect.height * sh), (int) (rect.width * sw), (int) (rect.height * sh));
	                    cg2.scale(sw, sh);
	                    rect.print(cg2);
	                    cg2.dispose();
	                }
	            }
	            cg.dispose();
	        }
	    } else {
	    	boolean nineGrid = fillImg.startsWith("#");
	        BufferedImage imgFill = ImgUtil.loadImg(xdoc, fillImg, fillColor, fillColor2, false);
	        if (imgFill != null) {
	            PaintUtil.fillImg(g, imgFill, shape,
	            		nineGrid, 
	            		ImgUtil.isStretch(fillImg));
	        } else if (fillColor != null) {
	            if (gradual.length() > 0 && !gradual.equals("0")) {
	                if (fillColor2 == null) fillColor2 = Color.WHITE;
	                if (gradual.endsWith("%")) {
	                    g.setColor(ColorUtil.mix(fillColor, fillColor2, To.toDouble(gradual.substring(0, gradual.length() - 1), 100) / 100));
	                    g.fill(shape);
	                } else {
	                    int ng = To.toInt(gradual);
	                    PaintUtil.paintGradient(g, (ng - 1) / 4,  (ng - 1) % 4, shape, fillColor, fillColor2);
	                }
	            } else {
	                g.setColor(fillColor);
	                g.fill(shape);
	            }
	        }
	    }
	}
	public static void fillImg(Graphics2D g, BufferedImage img, Shape shape, boolean nineGrid, boolean stretch) {
		TexturePaint paint;
		Rectangle rect = shape.getBounds();
		if (!nineGrid) {
			if (!stretch) {
				paint = new TexturePaint(img, new Rectangle2D.Double(rect.x, rect.y, img.getWidth(), img.getHeight()));
			} else {
				int n = (int) Math.round(rect.width / (double) img.getWidth());
				if (n <= 0) {
					n = 1;
				}
				double sw = n * img.getWidth() / (double) rect.width;
				n = (int) Math.round(rect.height / (double) img.getHeight());
				if (n <= 0) {
					n = 1;
				}
				double sh = n * img.getHeight() / (double) rect.height;
				paint = new TexturePaint(img, new Rectangle2D.Double(rect.x, rect.y, img.getWidth() / sw, img.getHeight() / sh));
			}
		} else {
	        paint = new TexturePaint(ImgUtil.nineGrid(img, rect.width, rect.height), rect);
		}
	    paint = PaintUtil.checkPaint(g, paint);
	    g.setPaint(paint);
	    g.fill(shape);
	}
	public static void paintGradient(Graphics2D g2, int type, int trans, Shape shape, Color c1, Color c2) {
	    Rectangle rect = shape.getBounds();
	    Graphics2D cg = (Graphics2D) g2.create(rect.x, rect.y, rect.width, rect.height);
	    double x = rect.x;
	    double y = rect.y;
	    Paint paint = Gradient.getPaint(rect, type, trans, c1, c2);
	    if (paint != null) {
	    	if (type < 4 && (trans == 2 || trans == 3) && isPdfGraphics(g2)) {
	    		//处理itext渐变bug
	    		BufferedImage img = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
	    		Graphics2D imgg = (Graphics2D) img.getGraphics();
	            imgg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	    		imgg.setPaint(paint);
	    		imgg.fillRect(0, 0, rect.width, rect.height);
	    		imgg.dispose();
	    		paint = new TexturePaint(img, rect);
	    	}
	        cg.translate(-x, -y);
	        cg.setPaint(paint);
	        cg.fill(shape);
	        cg.dispose();
	    }
	}
	public static void resetStroke(Graphics2D g) {
		if (!(g.getStroke() instanceof BasicStroke)) {
			//输出swf时非BasicStroke错误
			g.setStroke(new BasicStroke());
		}
	}
	public static TexturePaint checkPaint(Graphics2D g2, TexturePaint paint) {
		return checkPaint(g2, paint, false);
	}
	public static TexturePaint checkPaint(Graphics2D g2, TexturePaint paint, boolean force) {
	    if (force) {
	        int minSize = 48;
	        if (paint.getImage().getWidth() < minSize || paint.getImage().getHeight() < minSize) {
	            int ws = (int) Math.ceil(((double) minSize / paint.getImage().getWidth()));
	            int hs = (int) Math.ceil(((double) minSize / paint.getImage().getHeight()));
	            BufferedImage img = new BufferedImage(paint.getImage().getWidth() * ws, paint.getImage().getHeight() * hs, BufferedImage.TYPE_INT_ARGB);
	            Graphics2D g = (Graphics2D) img.getGraphics();
	            g.drawImage(paint.getImage(), 0, 0, img.getWidth(), img.getHeight(), null);
	            g.dispose();
	            paint = new TexturePaint(img, paint.getAnchorRect());
	        }
	    }
	    return paint;
	}

}
