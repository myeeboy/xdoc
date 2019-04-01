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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class TextStroke implements Stroke {
    public String text;
    public Font font;
    public boolean stretchToFit = false;
    public boolean repeat = false;
    private AffineTransform t = new AffineTransform();
    private static final float FLATNESS = 1;
    public float advance;
    public Shape strokeShape = null;
    public int shapeInx = -1;
	public TextStroke( int shapeInx, float strokeWidth ) {
		this.advance = strokeWidth;
		this.shapeInx = shapeInx;
	}
	public TextStroke( Shape shape, float strokeWidth ) {
		this.advance = strokeWidth;
		if (shape != null) {
			Rectangle2D bounds = shape.getBounds2D();
			double scale = strokeWidth / bounds.getHeight();
			this.advance = (float) (bounds.getWidth() * scale);
			if (this.advance <= 0) {
				this.advance = 1;
			}
			t.scale(scale, scale);
			t.translate(-bounds.getX(), -bounds.getY() - bounds.getHeight() / 2);
			this.strokeShape = t.createTransformedShape(shape);
			t = new AffineTransform();
		}
	}
    public TextStroke( String text, Font font, boolean stretchToFit, boolean repeat ) {
        this.text = text;
        this.font = font;
        this.stretchToFit = stretchToFit;
        this.repeat = repeat;
    }
    public Shape createStrokedShape( Shape shape ) {
    	GeneralPath result = new GeneralPath();
    	PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
    	float points[] = new float[6];
    	float moveX = 0, moveY = 0;
    	float lastX = 0, lastY = 0;
    	float thisX = 0, thisY = 0;
    	int type = 0;
    	float next = 0;
    	if (strokeShape == null) { //文本路径
    		FontRenderContext frc = new FontRenderContext(null, true, true);
    		GlyphVector glyphVector = font.createGlyphVector(frc, text);
    		int currentChar = 0;
    		int length = glyphVector.getNumGlyphs();
    		if (length == 0) return result;
    		float factor = stretchToFit ? measurePathLength(shape) / (float) glyphVector.getLogicalBounds().getWidth() : 1.0f;
    		float nextAdvance = 0;
    		while (currentChar < length && !it.isDone() ) {
    			type = it.currentSegment( points );
    			switch( type ){
    			case PathIterator.SEG_MOVETO:
    				moveX = lastX = points[0];
    				moveY = lastY = points[1];
    				result.moveTo( moveX, moveY );
    				nextAdvance = glyphVector.getGlyphMetrics(currentChar).getAdvance() * 0.5f;
    				next = nextAdvance + this.font.getSize2D() * factor / 4;
    				break;
    			case PathIterator.SEG_CLOSE:
    				points[0] = moveX;
    				points[1] = moveY;
    			case PathIterator.SEG_LINETO:
    				thisX = points[0];
    				thisY = points[1];
    				float dx = thisX-lastX;
    				float dy = thisY-lastY;
    				float distance = (float)Math.sqrt( dx*dx + dy*dy );
    				if (distance >= next) {
    					float r = 1.0f/distance;
    					float angle = (float)Math.atan2( dy, dx );
    					while (currentChar < length && distance >= next) {
    						Shape glyph = glyphVector.getGlyphOutline(currentChar);
    						Point2D p = glyphVector.getGlyphPosition(currentChar);
    						float px = (float)p.getX();
    						float py = (float)p.getY();
    						float x = lastX + next*dx*r;
    						float y = lastY + next*dy*r;
    						float advance = nextAdvance;
    						nextAdvance = currentChar < length-1 ? glyphVector.getGlyphMetrics(currentChar+1).getAdvance() * 0.5f : advance;
    						t.setToTranslation( x, y );
    						t.rotate( angle );
    						t.translate( -px-advance, -py+font.getSize()*0.5);
    						result.append( t.createTransformedShape( glyph ), false );
    						next += (advance+nextAdvance) * factor;
    						currentChar++;
    						if (repeat) currentChar %= length;
    					}
    				}
    				next -= distance;
    				lastX = thisX;
    				lastY = thisY;
    				break;
    			}
    			it.next();
    		}
    	} else { //图形路径
     		while (!it.isDone() ) {
    			type = it.currentSegment( points );
    			switch( type ){
    			case PathIterator.SEG_MOVETO:
    				moveX = lastX = points[0];
    				moveY = lastY = points[1];
    				result.moveTo( moveX, moveY );
    				next = 0;
    				break;

    			case PathIterator.SEG_CLOSE:
    				points[0] = moveX;
    				points[1] = moveY;
    			case PathIterator.SEG_LINETO:
    				thisX = points[0];
    				thisY = points[1];
    				float dx = thisX-lastX;
    				float dy = thisY-lastY;
    				float distance = (float)Math.sqrt( dx*dx + dy*dy );
    				if ( distance >= next ) {
    					float r = 1.0f/distance;
    					float angle = (float)Math.atan2( dy, dx );
    					while (distance >= next ) {
    						float x = lastX + next*dx*r;
    						float y = lastY + next*dy*r;
    						t.setToTranslation( x, y );
    						t.rotate( angle );
    						result.append( t.createTransformedShape(strokeShape), false );
    						next += advance;
    					}
    				}
    				next -= distance;
    				lastX = thisX;
    				lastY = thisY;
    				break;
    			}
    			it.next();
    		}
    	}
    	return result;
    }
    public float measurePathLength( Shape shape ) {
        PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
        float points[] = new float[6];
        float moveX = 0, moveY = 0;
        float lastX = 0, lastY = 0;
        float thisX = 0, thisY = 0;
        int type = 0;
        float total = 0;
        while ( !it.isDone() ) {
            type = it.currentSegment(points);
            switch( type ) {
            case PathIterator.SEG_MOVETO:
                moveX = lastX = points[0];
                moveY = lastY = points[1];
                break;
            case PathIterator.SEG_CLOSE:
                points[0] = moveX;
                points[1] = moveY;
            case PathIterator.SEG_LINETO:
                thisX = points[0];
                thisY = points[1];
                float dx = thisX-lastX;
                float dy = thisY-lastY;
                total += (float)Math.sqrt( dx*dx + dy*dy );
                lastX = thisX;
                lastY = thisY;
                break;
            }
            it.next();
        }
        return total;
    }

}
