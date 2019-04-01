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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.hg.util.HgException;
import com.hg.util.StrUtil;
import com.hg.util.Version;
import com.lowagie.text.Annotation;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfWriter;

public class PdfPrint {
    public static void write(XDoc doc, OutputStream out) throws HgException {
        double oldScale = doc.scale;
        try {
            doc.scale = 0.75;
            DocPrint docPrint = new DocPrint(doc);
            doc.print = docPrint;
            DocPaper docPaper = doc.getPaper();
            int paperWidth = docPaper.viewWidth();
            int paperHeight = docPaper.viewHeight();
            Document pdf = new Document(new Rectangle(0, 0, paperWidth, paperHeight), 0, 0, 0, 0);
            PdfWriter pdfwriter = PdfWriter.getInstance(pdf, out);
            pdfwriter.setCloseStream(false);
            pdf.addAuthor(doc.getMeta().getAuthor());
            pdf.addSubject(doc.getMeta().getDesc());
            pdf.addTitle(doc.getMeta().getTitle());
            pdf.addCreator("XDOC " + Version.VERSION);
            pdf.addKeywords(XDocXml.toZipDataURI(doc));
            pdf.open();
            PdfContentByte pcb = pdfwriter.getDirectContent();
            Graphics2D g2;
            doc.heads.clear();
            byte[] bimg = null;
            if (doc.pages > 0) {
                Image img;
                NameShape ns;
                java.awt.Rectangle rect;
                FontMapper xfm = new XFontMapper();
                Chunk chunk;
                PdfAction action;
            	for (int i = 0; i < doc.pages; i++) {
            		pdf.newPage();
            		g2 = pcb.createGraphics(paperWidth, paperHeight, xfm);
                    ImgUtil.setRenderHint(g2);
                    docPrint.hrefs.clear();
                    docPrint.toolTips.clear();
                    docPrint.inputs.clear();
                	docPrint.print(g2, null, i);
                	clearOverlapTips(docPrint);
                	for (int j = 0; j < docPrint.hrefs.size(); j++) {
                		ns = (NameShape) docPrint.hrefs.get(j);
                		rect = ns.shape.getBounds();
                		if (ns.name.startsWith(DocUtil.INNER_HREF_PREFIX)) {
                			if (bimg == null) {
                				bimg = getAnnotationImg();
                			}
                			img = Image.getInstance(bimg);
                			chunk = new Chunk(img, rect.x, paperHeight - rect.y - rect.height);
                			chunk.setLocalDestination(ns.name.substring(DocUtil.INNER_HREF_PREFIX.length()));
                			pdf.add(chunk);
                		} else {
                			if (ns.name.startsWith("#")) {
                				action = PdfAction.gotoLocalPage(ns.name.substring(1), false);
                			} else {
                				if (ns.name.indexOf('\n') > 0) {
                					ns.name = ns.name.split("\n")[0];
                				}
                				action = new PdfAction(ns.name, false);
                			}
                			pdfwriter.addAnnotation(new PdfAnnotation(pdfwriter, rect.x, paperHeight - rect.y - rect.height, 
                					rect.x + rect.width, paperHeight - rect.y, action));
                		}
                	}
                	for (int j = 0; j < docPrint.toolTips.size(); j++) {
                		ns = (NameShape) docPrint.toolTips.get(j);
                		rect = ns.shape.getBounds();
                		if (bimg == null) {
                			bimg = getAnnotationImg();
                		}
                		img = Image.getInstance(bimg);
                		img.setAnnotation(new Annotation(0, 0, rect.width, rect.height, "Tip:" + StrUtil.URLEncode(ns.name)));
                		pcb.addImage(img, rect.width, 0, 0, rect.height, rect.x, paperHeight - rect.y - rect.height);
                	}
                    g2.dispose();
            	}
            } else {
            	pdf.newPage();
        		g2 = pcb.createGraphicsShapes(paperWidth, paperHeight);
        		g2.dispose();
            }
    		if (doc.heads != null && doc.heads.size() > 0) {
                DocUtil.fixHeads(doc);
    			writePdfHeads(pcb.getRootOutline(), doc.heads, pdfwriter, paperHeight);
    		}
            pdf.close();
        } catch (Exception e) {
            throw new HgException(e);
        } finally {
            doc.scale = oldScale;
        }
    }
	private static void clearOverlapTips(DocPrint docPrint) {
		NameShape ns;
        for (int i = 0; i < docPrint.hrefs.size(); i++) {
            ns = (NameShape) docPrint.hrefs.get(i);
            for (int j = 0; j < docPrint.toolTips.size(); j++) {
            	if (ns.shape.getBounds().equals(((NameShape) docPrint.hrefs.get(j)).shape.getBounds())) {
            		docPrint.toolTips.remove(j);
            		j--;
            	}
            }		
        }
	}
	private static byte[] getAnnotationImg() throws IOException {
	    BufferedImage img2 = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    ImageIO.write(img2, "png", bout);
	    return bout.toByteArray();
	}
    private static void writePdfHeads(PdfOutline parent, ArrayList cheads, PdfWriter writer, int h) {
		PdfOutline child;
		Heading heading;
		for (int i = 0; i < cheads.size(); i++) {
			heading = (Heading) cheads.get(i);
			child = new PdfOutline(parent, PdfAction.gotoLocalPage(heading.page + 1, new PdfDestination(PdfDestination.XYZ, heading.x, h - heading.y /*pdf y坐标由底部算起*/, 0), writer), heading.name());
    		if (heading.cheads != null) {
    			writePdfHeads(child, heading.cheads, writer, h);
    		}
		}
	}
}
class XFontMapper implements FontMapper {
	public BaseFont awtToPdf(Font font) {
		try {
			String path = XFont.getFontFilePath(font);
			if (path != null) {
				return BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true);
			}
		} catch (Exception e) {
		}
		try {
			return BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED, true);
		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	public Font pdfToAwt(BaseFont bfont, int size) {
		return XFont.createFont(XFont.defaultFontName, Font.PLAIN, size);
	}
}
