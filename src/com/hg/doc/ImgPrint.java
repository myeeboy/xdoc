package com.hg.doc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

public class ImgPrint {
	public static void write(XDoc doc, OutputStream out, String format) throws Exception {
		DocPrint docPrint = new DocPrint(doc, !(out instanceof ZipOutputStream));
		DocPaper docPaper = doc.getPaper();
		int pageWidth = docPaper.viewWidth();
		int pageHeight = docPaper.viewHeight();
		if (pageWidth == 0) pageWidth = 1;
		if (pageHeight == 0) pageHeight = 1;
		doc.print = docPrint;
		BufferedImage img;
		Graphics2D g2;
		ByteArrayOutputStream bout;
		NameShape ns;
		docPrint.hrefs.clear();
		docPrint.toolTips.clear();
		docPrint.inputs.clear();
		img = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_RGB);
		g2 = img.createGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, pageWidth, pageHeight);
		ImgUtil.setImgRenderHint(g2);
		docPrint.print(g2, null, 0);
		g2.dispose();
		ImageIO.write(img, format, out);
		out.close();
	}
}
