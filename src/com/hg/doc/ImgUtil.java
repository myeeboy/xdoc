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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import com.hg.util.ColorUtil;
import com.hg.util.StrUtil;
import com.hg.util.XUrl;

public class ImgUtil {
    public static BufferedImage loadInnerImg(String key) {
        BufferedImage img = ImgLib.getImg(key);
        if (img == null) {
            img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        }
        return img;
    }
    public static void gray(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int rgb, gray;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb = img.getRGB(i, j);
                gray = (rgb >> 16 & 0xFF) * 77 + (rgb >> 8 & 0xFF) * 151 + (rgb & 0xFF) * 28 >> 8;
                img.setRGB(i, j, 255 << 24 | gray << 16 | gray << 8 | gray);
            }
        }
    }
    public static boolean useCache = false;
    private static BufferedImage loadImg(String url) {
    	BufferedImage img = null;
    	try {
    		return ImageIO.read(new XUrl(url).getInputStream());
    	} catch (Throwable e1) {
    		img = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
    	}
    	return img;
    }
    public static BufferedImage loadImg(XDoc xdoc, String url, Color color1, Color color2, boolean alpha) {
        if (url == null || url.length() == 0) {
            return null;
        }
        if (color1 == null) {
            color1 = Color.BLACK;
        }
        if (color2 == null) {
            color2 = Color.WHITE;
        }
    	if (url.startsWith("#")) { //去掉9格标志
    		url = url.substring(1);
    	}
        boolean bin = false;
        if (url.startsWith("$")) {
            url = url.substring(1);
            bin = true;
        }
        BufferedImage img = null;
        if (url.startsWith("@") || url.startsWith("&") || url.startsWith("?")) {
            BufferedImage timg = ImgUtil.loadInnerImg(url.substring(1));
            if (timg != null) {
                if (bin) {
                    timg = toBin(timg, color1, color2);
            	} else {
                    if (alpha) {
                        timg = ImgUtil.alpha(timg, Color.WHITE);
                    } else {
                        timg = toRGB(timg);
                    }
                }
                return timg;
            }
        } else if (url.startsWith("<") || url.startsWith("{")) {
            if (xdoc == null) {
                xdoc = new XDoc();
            }
            EleRect rect = DocUtil.getRect(xdoc, url);
            if (rect != null) {
				img = EleRect.toImg(rect);
            }
        } else if (url.startsWith("data:") || url.startsWith("BASE64:")) {
        	img = ImgUtil.strToImg(url);
        }
        if (img == null && url.length() > 0 && url.indexOf("{%") < 0 && url.indexOf("${") < 0) {
            try {
            	img = loadImg(url);
            } catch (Exception e) {
            }
        }
        if (img != null) {
        	if (bin) {
        		img = toBin(img, color1, color2);
        	} else {
        	    if (alpha) {
        	        img = ImgUtil.alpha(img, Color.WHITE);
        	    } else {
        	        img  = toRGB(img);
                }
        	}
        } 
        return img;
    }
	private static BufferedImage toRGB(BufferedImage img) {
		if (img != null && img.getType() != BufferedImage.TYPE_INT_RGB && img.getType() != BufferedImage.TYPE_INT_ARGB) {
			BufferedImage timg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics g = timg.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.drawImage(img, 0, 0, null);
			img = timg;
		}
		return img;
	}
    public static BufferedImage toBin(BufferedImage img, Color color1, Color color2) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (color1.equals(color2)) {
        	color2 = ColorUtil.invert(color2);
        }
        boolean alpha = (color1.getAlpha() != 255 || color2.getAlpha() != 255);
        BufferedImage img2 = new BufferedImage(w, h, alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        int rgb;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb = img.getRGB(i, j);
                int r = rgb >> 16 & 0xFF;
                int g = rgb >> 8 & 0xFF;
                int b = rgb & 0xFF;
                rgb = r * 77 + g * 151 + b * 28 >> 8;
                img2.setRGB(i, j, rgb < 128 ? color1.getRGB() : color2.getRGB());
            }
        }
        return img2;
    }
    public static BufferedImage replace(BufferedImage img, Color c, Color c2) {
        return replace(img, c, c2, 0);
    }
    public static BufferedImage replace(BufferedImage img, Color c, Color c2, int range) {
        BufferedImage timg;
        if (img.getType() == BufferedImage.TYPE_INT_ARGB) {
            timg = img;
        } else {
            timg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            timg.getGraphics().drawImage(img, 0, 0, null);
        }
        Color tc;
        for (int i = 0; i < timg.getWidth(); i++) {
            for (int j = 0; j < timg.getHeight(); j++) {
                if (range == 0) {
                    if (timg.getRGB(i, j) == c.getRGB()) {
                        timg.setRGB(i, j, c2.getRGB());
                    }
                } else {
                    tc = new Color(timg.getRGB(i, j));
                    if (tc.getRed() > c.getRed() - range && tc.getRed() < c.getRed() + range
                            && tc.getGreen() > c.getGreen() - range && tc.getGreen() < c.getGreen() + range
                            && tc.getBlue() > c.getBlue() - range && tc.getBlue() < c.getBlue() + range) {
                        timg.setRGB(i, j, c2.getRGB());
                    }
                }
            }
        }
        return timg;    
    
    }
    public static BufferedImage alpha(BufferedImage img, Color c) {
        return replace(img, c, new Color(255, 255, 255, 0), 48);
    }
    private static byte[][] toEdgePoints(BufferedImage img, int n) {
        int w = img.getWidth();
        int h = img.getHeight();
        byte[][] r1 = toBinPoints(img, n);
        byte[][] r2 = new byte[w][h];
        //检测边缘
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (r1[i][j] == 1) {
                    if (i == 0 || i == w - 1 || j == 0 || j == h - 1
                        || r1[i - 1][j - 1] == 0
                        || r1[i][j - 1] == 0
                        || r1[i + 1][j - 1] == 0
                        || r1[i - 1][j] == 0
                        || r1[i + 1][j] == 0
                        || r1[i - 1][j + 1] == 0
                        || r1[i][j + 1] == 0
                        || r1[i + 1][j + 1] == 0) {
                        r2[i][j] = 1;
                    } else {
                        r2[i][j] = 0;
                    }
                } else {
                    r2[i][j] = 0;
                }
            }
        }
        return r2;
    }
    private static byte[][] toBinPoints(BufferedImage img, int n) {
        int w = img.getWidth();
        int h = img.getHeight();
        byte[][] r1 = new byte[w][h];
        int rgb,r,g,b;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb = img.getRGB(i, j);
                r = rgb >> 16 & 0xFF;
                g = rgb >> 8 & 0xFF;
                b = rgb & 0xFF;
                rgb = r * 77 + g * 151 + b * 28 >> 8;
                if (rgb > n) {
                    r1[i][j] = 0;
                } else {
                    r1[i][j] = 1;
                }
            }
        }
        return r1;
    }
    private static BufferedImage toImg(byte[][] r, int w, int h, Color color, Color color2) {
        BufferedImage img2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (r[i][j] == 1) {
                    img2.setRGB(i, j, color.getRGB());
                } else {
                    img2.setRGB(i, j, color2.getRGB());
                }
            }
        }
        return img2;
    }
    /**
     * 边缘图片
     * @param img
     * @param n
     * @return
     */
    public static BufferedImage toEdgeImg(BufferedImage img, int n, Color color, Color color2) {
        int w = img.getWidth();
        int h = img.getHeight();
        byte[][] r = toEdgePoints(img, n);
        return toImg(r, w, h, color, color2);
    }
    /**
     * 黑白图
     * @param img
     * @param n
     * @return
     */
    public static BufferedImage toBinImg(BufferedImage img, int n, Color color, Color color2) {
        int w = img.getWidth();
        int h = img.getHeight();
        byte[][] r = toBinPoints(img, n);
        return toImg(r, w, h, color, color2);
    }
    /*
    public static BufferedImage toImg(BufferedImage img, int colors) {
        HashMap map = getColorChangeMap(img, colors);
        int[] colorTab = (int[]) map.get("colorTab");
        byte[] indexedPixels = (byte[]) map.get("indexedPixels");
        BufferedImage timg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < indexedPixels.length; i++) {
            timg.setRGB(i % img.getWidth(), i / img.getWidth(), colorTab[indexedPixels[i] & 0xff]);
        }
        return timg;
    }
    protected static HashMap getColorChangeMap(BufferedImage image, int colors) {
        boolean[] usedEntry = new boolean[256];
        int w = image.getWidth();
        int h = image.getHeight();
        int type = image.getType();
        if (type != BufferedImage.TYPE_3BYTE_BGR) {
            BufferedImage temp =
                new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = temp.createGraphics();
            g.drawImage(image, 0, 0, null);
            image = temp;
        }
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        int len = pixels.length;
        int nPix = len / 3;
        byte[] indexedPixels = new byte[nPix];
        NeuQuant nq = new NeuQuant(pixels, len, 10, colors);
        // initialize quantizer
        byte[] colorTab = nq.process(); // create reduced palette
        int[] cols = new int[colorTab.length / 3];
        for (int i = 0; i < colorTab.length / 3; i++) {
            cols[i] = new Color(colorTab[i * 3 + 2] & 0xff,
                    colorTab[i * 3 + 1] & 0xff,
                    colorTab[i * 3] & 0xff).getRGB();
            usedEntry[i] = false;
        }
        colorTab = null;
        // map image pixels to new palette
        int k = 0;
        for (int i = 0; i < nPix; i++) {
            int index =
                nq.map(pixels[k++] & 0xff,
                       pixels[k++] & 0xff,
                       pixels[k++] & 0xff);
            usedEntry[index] = true;
            indexedPixels[i] = (byte) index;
        }
        HashMap map = new HashMap();
        map.put("colorTab", cols);
        map.put("usedEntry", usedEntry);
        map.put("indexedPixels", indexedPixels);
        return map;
    }
    */
    /**
     * 图片转换为图形字符串
     * @param img
     * @param n
     * @return
     * @throws HgException
     */
//    public static String[] toShapeStrs(BufferedImage img, int colors) throws HgException {
//        try {
//            HashMap map = getColorChangeMap(img, colors);
//            int[] colorTab = (int[]) map.get("colorTab");
//            byte[] indexedPixels = (byte[]) map.get("indexedPixels");
//            boolean[] usedEntry = (boolean[]) map.get("usedEntry");
//            BufferedImage timg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
//            int n = 0;
//            for (int i = 0; i < usedEntry.length; i++) {
//                if (usedEntry[i]) n++;
//            }
//            String[] strs = new String[n * 2 + 2];
//            strs[1] = "M 0 -" + img.getHeight() + " L " + img.getWidth() + " -" + img.getHeight() + " " + img.getWidth() + " 0 0 0 -" + img.getHeight() + " Z";
//            n = 0;
//            AffineTransform af = new AffineTransform();
//            af.translate(0, img.getHeight());
//            for (int i = 0; i < usedEntry.length; i++) {
//                if (usedEntry[i]) {
//                    for (int j = 0; j < indexedPixels.length; j++) {
//                        timg.setRGB(j % img.getWidth(), j / img.getWidth(), indexedPixels[j] == i ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
//                    }
//                    strs[n * 2 + 2] = ColorUtil.colorToStr(new Color(colorTab[i]));
//                    strs[n * 2 + 3] = ImgEdgeForm.toShapeStr(timg, 128);
//                    n++;
//                }
//            }
//            strs[0] = strs[2];
//            return strs;
//        } catch (Exception e) {
//            throw new HgException(e);
//        }
//    }
    /*
    public static Shape toShape(BufferedImage img, int n) {
        GeneralPath path = new GeneralPath();
        int w = img.getWidth();
        int h = img.getHeight();
        byte[][] r1 = new byte[w][h];
        byte[][] r2 = toEdgePoints(img, n);
        //寻找路径
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                r1[i][j] = r2[i][j];
                r2[i][j] = 0;
            }
        }
        ArrayList ps = new ArrayList();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (r1[i][j] == 1 && r2[i][j] == 0) { //有色并且没有处理过
                    r2[i][j] = 1; //标记为处理
                    ps.clear();
                    ps.add(new Point(i, j));
                    findNextPoint(ps, i, j, r1, r2, w, h);
                    if (ps.size() > 1) {
                        addPoints(path, ps);
                        break;
                    }
                }
            }
        }
        return path;
    }
    private static void addPoints(GeneralPath path, ArrayList points) {
        Point prePoint = (Point) points.get(0), curPoint;
        double preRate = Double.MAX_VALUE, curRate;
        //去掉直线中间点
        for(int i = 1; i <  points.size(); i++) {
            curPoint = (Point) points.get(i);
            if (curPoint.x == prePoint.x) {
                curRate = Double.MAX_VALUE;
            } else {
                curRate = (curPoint.y - prePoint.y) / (curPoint.x - prePoint.x);
            }
            if (i > 1) {
                if (curRate == preRate) { //舍前一个点
                    points.set(i - 1, null);
                }
            }
            prePoint = curPoint;
            preRate = curRate;
        }
        Point point = (Point) points.get(0);
        Point point1, point2;
        path.moveTo(point.x, point.y);
        ArrayList tp = new ArrayList();
        boolean lineBegin = false;
        for (int i = 1; i < points.size(); i++) {
            point = (Point) points.get(i);
            if (point == null) {
                addTmpPoints(path, tp);
                lineBegin = true;
            } else {
                if (lineBegin) {
                    path.lineTo(point.x, point.y);
                    lineBegin = false;
                } else {
                    tp.add(points.get(i));
                    if (tp.size() == 3) {
                        point = (Point) tp.get(0);
                        point1 = (Point) tp.get(1);
                        point2 = (Point) tp.get(2);
                        path.curveTo(point.x, point.y, point1.x, point1.y, point2.x, point2.y);
                        tp.clear();
                    }
                }
            }
        }
        addTmpPoints(path, tp);
        //path.closePath();
    }
    private static void addTmpPoints(GeneralPath path, ArrayList tp) {
        Point point, point1, point2;
        if (tp.size() == 1) {
            point1 = (Point) tp.get(0);
            path.lineTo(point1.x, point1.y);
        } else if (tp.size() == 2) {
            point1 = (Point) tp.get(0);
            point2 = (Point) tp.get(1);
            path.quadTo(point1.x, point1.y, point2.x, point2.y);
        } else if (tp.size() == 3) {
            point1 = (Point) tp.get(0);
            point2 = (Point) tp.get(1);
            point = (Point) tp.get(2);
            path.curveTo(point1.x, point1.y, point2.x, point2.y, point.x, point.y);
        }
        tp.clear();
    }
    private static void findNextPoint(ArrayList ps, int i, int j, byte[][] r1, byte[][] r2, int w, int h) {
        int[][] a = new int[][] {new int[] {i - 1, j - 1}, 
                new int[] {i, j - 1},
                new int[] {i + 1, j - 1}, 
                new int[] {i - 1, j}, 
                new int[] {i + 1, j}, 
                new int[] {i - 1, j + 1}, 
                new int[] {i, j + 1}, 
                new int[] {i + 1, j + 1}};
        for (int m = 0; m < a.length; m++) {
            if (a[m][0] >= 0 && a[m][0] < w && a[m][1] >= 0 && a[m][1] < h 
                    && r1[a[m][0]][a[m][1]] == 1 && r2[a[m][0]][a[m][1]] == 0) { //有色并且没有处理过
                r2[a[m][0]][a[m][1]] = 1;
                ps.add(new Point(a[m][0], a[m][1]));
                findNextPoint(ps, a[m][0], a[m][1], r1, r2, w, h);
                break;
            }
        }
    }
    */
    private static final int maxImgSize = 1200;
	public static String imgToStr(Image img) {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    RenderedImage bufImg;
	    if (img instanceof RenderedImage) {
	        bufImg = (RenderedImage) img;
	    } else {
	        bufImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	        ((BufferedImage) bufImg).getGraphics().drawImage(img, 0, 0, null);
	    }
        if (bufImg.getWidth() > maxImgSize || bufImg.getHeight() > maxImgSize) {
            if (!(bufImg instanceof BufferedImage)) {
                BufferedImage tmpImg = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = (Graphics2D) tmpImg.getGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, bufImg.getWidth(), bufImg.getHeight());
                g.drawImage(img, new AffineTransform(), null);
                bufImg = tmpImg;
            }
            bufImg = zoomToSize((BufferedImage) bufImg, 1200);
        }
	    try {
	        ImageIO.write(bufImg, "png", bout);
	    } catch (Exception e) {
	    }
	    return "data:image/png;base64," + StrUtil.toBase64(bout.toByteArray());
	}
    public static Object TEXT_ANTIALIASING = null;
    static {
        try {
            TEXT_ANTIALIASING = RenderingHints.class.getField("VALUE_TEXT_ANTIALIAS_LCD_HRGB").get(RenderingHints.class);
        } catch (Throwable e) {
        }
    }
	public static void setRenderHint(Graphics2D g2) {
		setRenderHint(g2, false);
	}
	public static void setImgRenderHint(Graphics2D g2) {
		setRenderHint(g2, true);
	}
	private static void setRenderHint(Graphics2D g2, boolean img) {
		if (!img && TEXT_ANTIALIASING != null && !PaintUtil.IS_OPEN_JDK) {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, TEXT_ANTIALIASING);
		} else {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	}
	public static BufferedImage strToImg(String str) {
        if (str.indexOf("base64,") > 0) {
            str = str.substring(str.indexOf("base64,") + 7);
        } else if (str.startsWith("BASE64:")) {
            str = str.substring(7);
        } else {
            str = "";
        }
        try {
            return ImageIO.read(new ByteArrayInputStream(StrUtil.fromBase64(str)));
        } catch (Exception e) {
            return new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        }
	}
    public static BufferedImage zoomToSize(BufferedImage img, int size) {
        if (img.getWidth() > size || img.getHeight() > size) {
            double scale;
            if (img.getWidth() > img.getHeight()) {
                scale = (double) size / img.getWidth();
            } else {
                scale = (double) size / img.getHeight();
            }
            BufferedImage timg = new BufferedImage((int) (img.getWidth() * scale), (int) (img.getHeight() * scale), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = (Graphics2D) timg.getGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, timg.getWidth(), timg.getHeight());
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.drawImage(img, 0, 0, timg.getWidth(), timg.getHeight(), null);
            g2.dispose();
            img = timg;
        }
        return img;
    }
    public static BufferedImage adjustImg(BufferedImage img, int w, int h) {
    	BufferedImage timg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g = (Graphics2D) timg.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    	drawAdjustImg(g, img, w, h);
    	g.dispose();
    	return timg;
    }

    public static void drawAdjustImg(Graphics g, BufferedImage img, int w, int h) {
        double scale;
        if (img.getWidth() / (double) img.getHeight() > w / (double) h) {
            scale = (double) w / img.getWidth();
        } else {
            scale = (double) h / img.getHeight();
        }
        g.drawImage(img, (w - (int) (img.getWidth() * scale)) / 2, (h - (int) (img.getHeight() * scale)) / 2, 
                (int) (img.getWidth() * scale), (int) (img.getHeight() * scale), null);
    }
    public static Color getMainColor(BufferedImage img) {
    	Color c = Color.white;
    	int min = 256;
    	int width = img.getWidth();
        int height = img.getHeight();
        int rgb,r,g,b;
        for (int i = 0; i < height; i++) {
        	for (int j = 0; j < width; j++) {
        		rgb = img.getRGB(j, i);
        		r = rgb >> 16 & 0xFF;
        		g = rgb >> 8 & 0xFF;
        		b = rgb & 0xFF;
        		if (r * 77 + g * 151 + b * 28 >> 8 < min) {
        			min = r * 77 + g * 151 + b * 28 >> 8;
        			c = new Color(img.getRGB(j, i));
        		}
        	}
        }
        return c;
    }
    public static BufferedImage toBufImg(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        } else {
            BufferedImage img = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics g = img.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
            g.drawImage(image, 0, 0, null);
            g.dispose();
            return img;
        }
    }
    public static BufferedImage nineGrid(BufferedImage img, int w, int h) {
        Rectangle grect = new Rectangle(0, 0, img.getWidth() / 3, img.getHeight() / 3);
        BufferedImage bimg = new BufferedImage((int) (Math.max(Math.ceil(w / (double) grect.width), 2) * grect.width), 
                (int) (Math.max(Math.ceil(h / (double) grect.height), 2) * grect.height), img.getType());
        w = bimg.getWidth();
        h = bimg.getHeight();
        Graphics2D imgg = (Graphics2D) bimg.getGraphics();
        Graphics2D cg;
        //下方
        imgg.drawImage(img.getSubimage(img.getWidth() - grect.width, img.getHeight() - grect.height, grect.width, grect.height), null, w - grect.width, h - grect.height);
        if (w - grect.width * 2 > 0) {
            cg = (Graphics2D) imgg.create(grect.width, h - grect.height, w - grect.width * 2, grect.height);
            cg.setPaint(new TexturePaint(img.getSubimage(grect.width, img.getHeight() - grect.height, grect.width, grect.height), grect));
            cg.fillRect(0, 0, w - grect.width * 2, grect.height);
        }
        imgg.drawImage(img.getSubimage(0, img.getHeight() - grect.height, grect.width, grect.height), null, 0, h - grect.height);
        //中间
        if (h - grect.height * 2 > 0) {
            cg = (Graphics2D) imgg.create(w - grect.width, grect.height, grect.width, h - grect.height * 2);
            cg.setPaint(new TexturePaint(img.getSubimage(img.getWidth() - grect.width, grect.height, grect.width, grect.height), grect));
            cg.fillRect(0, 0, grect.width, h - grect.height * 2);
        }
        if (w - grect.width * 2 > 0 &&  h - grect.height * 2 > 0) {
            cg = (Graphics2D) imgg.create(grect.width, grect.height, w - grect.width * 2, h - grect.height * 2);
            cg.setPaint(new TexturePaint(img.getSubimage(grect.width, grect.height, grect.width, grect.height), grect));
            cg.fillRect(0, 0, w - grect.width * 2, h - grect.height * 2);
        }
        if (h - grect.height * 2 > 0) {
            cg = (Graphics2D) imgg.create(0, grect.height, grect.width, h - grect.height * 2);
            cg.setPaint(new TexturePaint(img.getSubimage(0, grect.height, grect.width, grect.height), grect));
            cg.fillRect(0, 0, grect.width, h - grect.height * 2);
        }
        //上方
        imgg.drawImage(img.getSubimage(img.getWidth() - grect.width, 0, grect.width, grect.height), null, w - grect.width, 0);
        if (w - grect.width * 2 > 0) {
            cg = (Graphics2D) imgg.create(grect.width, 0, w - grect.width * 2, grect.height);
            cg.setPaint(new TexturePaint(img.getSubimage(grect.width, 0, grect.width, grect.height), grect));
            cg.fillRect(0, 0, w - grect.width * 2, grect.height);
        }
        imgg.drawImage(img.getSubimage(0, 0, grect.width, grect.height), null, 0, 0);
        return bimg;
    }
	public static BufferedImage fliph(BufferedImage img) {
		BufferedImage timg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				timg.setRGB(i, j, img.getRGB(img.getWidth() - i - 1, j));
			}
		}
		return timg;
	}
	public static BufferedImage flipv(BufferedImage img) {
		BufferedImage timg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				timg.setRGB(i, j, img.getRGB(i, img.getHeight() - j - 1));
			}
		}
		return timg;
	}
	public static BufferedImage fliphv(BufferedImage img) {
		return fliph(flipv(img));
	}
	public static boolean isStretch(String imgUrl) {
		return !(imgUrl.startsWith("$@p") 
			|| imgUrl.startsWith("@p")
			|| imgUrl.startsWith("@t")
			|| imgUrl.startsWith("$@t") 
			|| imgUrl.startsWith("&p")
			|| imgUrl.startsWith("$&p") 
			|| imgUrl.startsWith("&t")
			|| imgUrl.startsWith("$&t"));
	}
	public static BufferedImage toXZoomImg(String imgUrl, String drawType, int width, int height) {
		BufferedImage img = loadImg(null, imgUrl, null, null, false);
		TexturePaint paint = toPaint(img, drawType, width, height, 0, isStretch(imgUrl));
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setPaint(paint);
		g2.fillRect(0, 0, width, height);
		g2.dispose();
		return img;
	}
	public static TexturePaint toPaint(BufferedImage img, String drawType, int width, int height, int margin, boolean stretch) {
		TexturePaint paint;
		if (drawType.equals(EleImg.DRAW_TYPE_4CORNER)) {
		    BufferedImage bimg = new BufferedImage(img.getWidth() * 2, img.getHeight() * 2, img.getType());
		    Graphics2D imgg = (Graphics2D) bimg.getGraphics();
		    imgg.drawImage(img, null, 0, 0);
		    imgg.drawImage(fliph(img), null, img.getWidth(), 0);
		    imgg.drawImage(flipv(img), null, 0, img.getHeight());
		    imgg.drawImage(fliphv(img), null, img.getWidth(), img.getHeight());
		    imgg.dispose();
		    img = bimg;
		    drawType = EleImg.DRAW_TYPE_9GRID;
		}
		if (drawType.equals(EleImg.DRAW_TYPE_REPEAT)) {
			if (!stretch) {
				paint = new TexturePaint(img, new Rectangle2D.Double(0, 0, img.getWidth(), img.getHeight()));
			} else {
				int w = width - margin * 2;
				int h = height - margin * 2;
				int n = (int) Math.round(w / (double) img.getWidth());
				if (n <= 0) {
					n = 1;
				}
				double sw = n * img.getWidth() / (double) w;
				n = (int) Math.round(h / (double) img.getHeight());
				if (n <= 0) {
					n = 1;
				}
				double sh = n * img.getHeight() / (double) h;
				paint = new TexturePaint(img, new Rectangle2D.Double(0, 0, img.getWidth() / sw, img.getHeight() / sh));
			}
		} else if (drawType.equals(EleImg.DRAW_TYPE_CENTER)) {
		    if (img.getWidth() > width - margin * 2 || img.getHeight() > height - margin * 2) {
		        paint = ImgUtil.toAdjustPaint(img, drawType, width, height, margin);
		    } else {
		        BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		        bimg.getGraphics().drawImage(img, (width - img.getWidth() - margin * 2) / 2, (height - img.getHeight() - margin * 2) / 2, null);
		        paint = new TexturePaint(bimg, new Rectangle(0, 0, width, height));
		    }
		} else if (drawType.equals(EleImg.DRAW_TYPE_FACT)) {
		    BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		    bimg.getGraphics().drawImage(img, 0, 0, null);
		    paint = new TexturePaint(bimg, new Rectangle(0, 0, width, height));
		} else if (drawType.startsWith(EleImg.DRAW_TYPE_ADJUST)) {
		    paint = ImgUtil.toAdjustPaint(img, drawType, width, height, margin);
		} else if (drawType.equals(EleImg.DRAW_TYPE_9GRID)) {
		    int w = width - margin * 2;
		    int h = height - margin * 2;
		    paint = new TexturePaint(nineGrid(img, w, h), new Rectangle(0, 0, w, h));
		} else {
		    if (width - margin * 2 < img.getWidth() && height - margin * 2 < img.getHeight()) {
		        BufferedImage bimg;
		        try {
		            bimg = new BufferedImage(width - margin * 2, height - margin * 2, img.getType());
		        } catch (Exception e) {
		            bimg = new BufferedImage(1, 1, img.getType());
		        }
		        bimg.getGraphics().drawImage(img, 0, 0, bimg.getWidth(), bimg.getHeight(), null);
		        paint = new TexturePaint(bimg, new Rectangle(0, 0, bimg.getWidth(), bimg.getHeight()));
		    } else {
		        paint = new TexturePaint(img, new Rectangle(0, 0, width - margin * 2, height - margin * 2));
		    }
		}
		return paint;
	}
	private static TexturePaint toAdjustPaint(BufferedImage img, String drawType, int width, int height, int margin) {
	    double scale;
	    if (img.getWidth() / (double) img.getHeight() > width / (double) height) {
	        scale = (double) (width - margin * 2) / img.getWidth();
	    } else {
	        scale = (double) (height - margin * 2) / img.getHeight();
	    }
	    BufferedImage bimg = new BufferedImage(width - margin * 2, height - margin * 2, BufferedImage.TYPE_INT_ARGB);
	    int x = (width - margin * 2 - (int) (img.getWidth() * scale)) / 2;
	    int y = (height - margin * 2 - (int) (img.getHeight() * scale)) / 2;
	    if (drawType.endsWith("left")) {
	        x = 0;
	    } else if (drawType.endsWith("top")) {
	        y = 0;
	    } else if (drawType.endsWith("right")) {
	        x *= 2;
	    } else if (drawType.endsWith("bottom")) {
	        y *= 2;
	    }
	    bimg.getGraphics().drawImage(img, x, y, 
	            (int) (img.getWidth() * scale), (int) (img.getHeight() * scale), null);
	    return new TexturePaint(bimg, new Rectangle(0, 0, width - margin * 2, height - margin * 2));
	}
}
