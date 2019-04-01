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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.hg.util.StrUtil;

public class DocPageLine {
    public int index = 0;
    public int top = 0;
    public int left = 0;
    public int height = 20;
    public int width = 0;
    public boolean enter = false;
    public boolean vertical = false;
    public String align = DocConst.ALIGN_LEFT;
    public ArrayList eleList = new ArrayList();
    public ElePara para;
    public DocPageLine(ElePara para, ArrayList eleList, int height, int width, boolean vertical) {
        this.para = para;
        this.eleList = eleList;
        this.height = height;
        this.width = width;
        this.vertical = vertical;
    }
    public DocPageLine(ElePara para, ArrayList eleList, int height, int width) {
        this(para, eleList, height, width, false);
    }
    public int offset = 0;
    public void print(Graphics2D g, int xStart) {
        print(g, xStart, top);
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Object ele;
        for (int i = 0; i < eleList.size(); i++) {
            ele = eleList.get(i);
            if (ele instanceof EleText) {
                sb.append(((EleText) ele).text);
            } 
        }
        return sb.toString().trim();
    }
//    private static Heading getHeading(ArrayList cheads, EleParagraph para) {
//		Heading heading;
//		for (int i = 0; i < cheads.size(); i++) {
//			heading = (Heading) cheads.get(i);
//			if (heading.para == para) {
//				return heading;
//			}
//    		if (heading.cheads != null) {
//    			getHeading(heading.cheads, para);
//    		}
//		}
//		return null;
//	}
    private void print(Graphics2D g, int xStart, int yStart) {
    	if (this.para.backColor != null || this.para.backImg.length() > 0) {
    		if (vertical) {
    			PaintUtil.fill(g, this.para.xdoc,
    					new Rectangle(yStart - height, xStart + this.para.indentLeft, height, this.para.layWidth), 
    					this.para.backColor, this.para.backImg);
    		} else {
    			PaintUtil.fill(g, this.para.xdoc,
    					new Rectangle(this.para.indentLeft + xStart, yStart, this.para.layWidth, height), 
    					this.para.backColor, this.para.backImg);
    		}
    	}
        if (this.eleList.size() == 0) return;
        if (this.para.heading > 0) {
            ArrayList heads = this.para.xdoc.heads;
            boolean b = false;
            if (heads.size() > 0) {
                if (this.para == ((Heading) heads.get(heads.size() - 1)).para) {
                    b = true;
                }
            }
            if (!b) {
                this.para.xdoc.heading = this.toString();
                Heading heading = new Heading(this.para);
                heading.page = this.para.xdoc.page - 1;
                heading.x = (int) g.getTransform().getTranslateX() + xStart;
                heading.y = (int) g.getTransform().getTranslateY() + yStart;
                this.para.xdoc.heads.add(heading);
            }
        }
        if (vertical) {
            printV(g, yStart, xStart);
        } else {
            printH(g, xStart, yStart);
        }
    }
    private void printV(Graphics2D g, int xStart, int yStart) {
        Object ele;
        int y = this.offset + yStart + this.left;
        EleRect rect;
        EleLine line;
        Rectangle rectBounds;
        int adjustHeight = 0;
        for (int i = 0; i < eleList.size(); i++) {
            ele = eleList.get(i);
            if (ele instanceof EleCharRect) {
            	ele = ((EleCharRect) ele).eleChar;
            }
            if (ele instanceof EleText) {
                y += ((EleText) ele).printV((Graphics2D) g, xStart - height, y, height, para.heading == 0 ? para.lineSpacing : 0);
            } else if (ele instanceof EleRect) {
                rect = ((EleRect) ele);
                Point viewSize = rect.viewSize();
                if (rect.valign != null) {
                    if (rect.valign.equals(DocConst.ALIGN_TOP)) {
                        adjustHeight = this.height - viewSize.x - para.lineSpacing;
                    } else if (rect.valign.equals(DocConst.ALIGN_CENTER)) {
                        adjustHeight = (this.height - viewSize.x - para.lineSpacing) / 2;
                    } else {
                        adjustHeight = 0;
                    }
                }
                if (rect.rotate == 0) {
                	if (rect.valign.equals(DocConst.ALIGN_AROUND) 
                			|| rect.valign.equals(DocConst.ALIGN_FLOAT)) {
                        Graphics2D cg = (Graphics2D) g.create(xStart + para.lineSpacing, y, 
                                rect.width + 1, rect.height + 1);
                		rect.print(cg);
                        cg.dispose();
                	} else {
                        Graphics2D cg = (Graphics2D) g.create(xStart - height + adjustHeight, y, 
                                rect.width + 1, rect.height + 1);
                		rect.print(cg);
                        cg.dispose();
                	}
                } else {
                	Point s;
                	if (rect.valign.equals(DocConst.ALIGN_AROUND) || rect.valign.equals(DocConst.ALIGN_FLOAT)) {
                		s = new Point(xStart + para.lineSpacing, y);
                	} else {
                		s = new Point(xStart - viewSize.x + adjustHeight, y);
                	}
                    int d = (int) Math.ceil(Math.pow(Math.pow(rect.width, 2) + Math.pow(rect.height, 2), 0.5));
                    Graphics2D cg = (Graphics2D) g.create(s.x - (d - viewSize.x) / 2,
                            s.y - (d - viewSize.y) / 2, 
                    		d,
                    		d);
                    AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(rect.rotate), d/2, d/2);
                    cg.transform(at);
                    cg.translate((d - rect.width) / 2, (d - rect.height) / 2);
                    rect.print(cg);
                    cg.dispose();
                }
                if (ele instanceof EleLine) {
                    line = ((EleLine) ele);
                    rectBounds = EleLine.getViewBounds(line);
                    if (line.valign != null) {
                        if (line.valign.equals(DocConst.ALIGN_TOP)) {
                            adjustHeight = this.height - rectBounds.height - para.lineSpacing;
                        } else if (line.valign.equals(DocConst.ALIGN_CENTER)) {
                            adjustHeight = (this.height - rectBounds.height - para.lineSpacing) / 2;
                        } else {
                            adjustHeight = 0;
                        }
                    }
                    line.print((Graphics2D) g, xStart - rectBounds.width + adjustHeight, y);
                }
                y += viewSize.y;
            }
        }
    }
    private void printH(Graphics2D g, int xStart, int yStart) {
        Object ele;
        int x = this.offset + xStart + this.left;
        EleRect rect;
        EleLine line;
        Rectangle rectBounds;
        int adjustHeight = 0;
        for (int i = 0; i < eleList.size(); i++) {
            ele = eleList.get(i);
            if (ele instanceof EleCharRect) {
            	ele = ((EleCharRect) ele).eleChar;
            }
            if (ele instanceof EleText) {
                if (((EleText) ele).valign.equals(DocConst.ALIGN_AROUND)) {
                    ((EleText) ele).print((Graphics2D) g, x, yStart + (int) ((EleText) ele).getBounds().getHeight() - height + para.lineSpacing, height, para.lineSpacing);
                } else {
                    ((EleText) ele).print((Graphics2D) g, x, yStart, height, (para.heading == 0 && eleList.size() > 1) ? para.lineSpacing : 0);
                }
                x += ((EleText) ele).getBounds().getWidth();
            } else if (ele instanceof EleRect) {
                if (ele instanceof EleLine) {
                	((EleLine) ele).printLine = false;
                }
                rect = ((EleRect) ele);
                Point viewSize = rect.viewSize();
                if (rect.valign != null) {
                    if (rect.valign.equals(DocConst.ALIGN_TOP)) {
                        adjustHeight = this.height - viewSize.y;
                    } else if (rect.valign.equals(DocConst.ALIGN_CENTER)) {
                        adjustHeight = (this.height - viewSize.y) / 2;
                    } else {
                        adjustHeight = 0;
                    }
                }
                if (rect.rotate == 0) {
                	if (rect.valign.equals(DocConst.ALIGN_AROUND)) {
                        Graphics2D cg = (Graphics2D) g.create(x, yStart + para.lineSpacing, 
                                rect.width + 1, rect.height + 1);
                		rect.print(cg);
                        cg.dispose();
                	} else if (rect.valign.equals(DocConst.ALIGN_FLOAT)) {
                        Graphics2D cg = (Graphics2D) g.create(x, yStart + para.lineSpacing - rect.height, 
                                rect.width + 1, rect.height + 1);
                		rect.print(cg);
                        cg.dispose();
                	} else {
                        Graphics2D cg = (Graphics2D) g.create(x, yStart + this.height - rect.height - adjustHeight, 
                                rect.width + 1, rect.height + 1);
                		rect.print(cg);
                        cg.dispose();
                	}
                } else {
                	Point s;
                	if (rect.valign.equals(DocConst.ALIGN_AROUND)) {
                		s = new Point(x, yStart + para.lineSpacing);
                	} else if (rect.valign.equals(DocConst.ALIGN_FLOAT)) {
                		s = new Point(x, yStart + para.lineSpacing - rect.height);
                	} else {
                		s = new Point(x, yStart + this.height - viewSize.y - adjustHeight);
                	}
                    int d = (int) Math.ceil(Math.pow(Math.pow(rect.width, 2) + Math.pow(rect.height, 2), 0.5));
                    Graphics2D cg = (Graphics2D) g.create(s.x - (d - viewSize.x) / 2,
                            s.y - (d - viewSize.y) / 2, 
                    		d,
                    		d);
                    AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(rect.rotate), d/2, d/2);
                    cg.transform(at);
                    cg.translate((d - rect.width) / 2, (d - rect.height) / 2);
                    rect.print(cg);
                    cg.dispose();
                }
                if (ele instanceof EleLine) {
                    line = ((EleLine) ele);
                    rectBounds = EleLine.getViewBounds(line);
                    if (line.valign != null) {
                        if (line.valign.equals(DocConst.ALIGN_TOP)) {
                            adjustHeight = this.height - rectBounds.height;
                        } else if (line.valign.equals(DocConst.ALIGN_CENTER)) {
                            adjustHeight = (this.height - rectBounds.height) / 2;
                        } else {
                            adjustHeight = 0;
                        }
                    }
                    line.print((Graphics2D) g, x, yStart + this.height - rectBounds.height - adjustHeight - (int)line.strokeWidth/2);
                }
                x += viewSize.x;
            }
        }
    }
	public void distribute(int width, int from) {
		distribute(width, from, false);
	}
	private void distribute(int width, int from, boolean both) {
		if (this.eleList.size() > from) {
			if (!vertical) {
				EleText txt;
				ArrayList tarList = new ArrayList();
				if (from > 0) {
					for (int i = 0; i < from; i++) {
						tarList.add(this.eleList.get(i));
					}
				}
				for (int i = from; i < this.eleList.size(); i++) {
					if (this.eleList.get(i) instanceof EleText) {
						txt = (EleText) this.eleList.get(i);
						if (txt.canSplit()) {
							distributeSplit(tarList, txt, both);
						} else {
							tarList.add(txt);
						}
					} else {
						tarList.add(this.eleList.get(i));
					}
				}
				this.eleList = tarList;
				if (this.eleList.size() == from + 1 && this.eleList.get(from) instanceof EleText) {
					txt = (EleText) this.eleList.get(from);
					if (txt.canSplit()) {
						//分割文字
						this.eleList.remove(from);
						distributeSplit(this.eleList, txt, false);
					}
				}
				double space = (double) (width - this.width) / (this.eleList.size() - from - 1);
				if (space > this.height) {
					//space太大尝试分割汉字
					tarList = new ArrayList();
					if (from > 0) {
						for (int i = 0; i < from; i++) {
							tarList.add(this.eleList.get(i));
						}
					}
					for (int i = from; i < this.eleList.size(); i++) {
						if (this.eleList.get(i) instanceof EleText) {
							txt = (EleText) this.eleList.get(i);
							if (txt.canSplit()) {
								int pos = 0;
								for (int j = 0; j < txt.text.length(); j++) {
									if (txt.text.charAt(j) > 255) {
										if (pos < j) {
											tarList.add(new EleText(txt, txt.text.substring(pos, j)));
										}
										tarList.add(new EleText(txt, String.valueOf(txt.text.charAt(j))));
										pos = j + 1;
									}
								}
								if (pos < txt.text.length()) {
									tarList.add(new EleText(txt, txt.text.substring(pos)));
								}
							} else {
								tarList.add(txt);
							}
						} else {
							tarList.add(this.eleList.get(i));
						}
					}
					this.eleList = tarList;
				}
			}
			if (this.eleList.size() > from + 1) {
				double space = (double) (width - this.width) / (this.eleList.size() - from - 1);
				if (space > 0) {
					EleSpace box = null;
					double count = 0;
					for (int i = from; i < this.eleList.size() - 1; i+=2) {
						count += space - (int) space;
						if ((int) space > 0 || count >= 1) {
							box = new EleSpace(this.para.xdoc, (int) space, 1);
							if (count >= 1) {
								box.width += (int) count;
								count -= (int) count;
							}
							if (vertical) {
								box.setSize(box.height, box.width);
								if (box.height > 0) {
									this.eleList.add(i + 1, box);
								}
							} else {
								this.eleList.add(i + 1, box);
							}
						}
					}
				}
			}
		}
	}
	public void bothDistribute(int width, int from) {
        distribute(width, from, true);
	}
	public void calWidth() {
		int lineWidth = 0;
        Object ele;
        if (vertical) {
            EleText text;
            Rectangle2D rectBounds;
            for (int i = 0; i < eleList.size(); i++) {
                ele = eleList.get(i);
                if (ele instanceof EleCharRect) {
                	ele = ((EleCharRect) ele).eleChar;
                }
                if (ele instanceof EleText) {
                	text = (EleText) ele;
                	if (text.text.length() > 0) {
                		rectBounds = text.getBounds();
                		if (EleText.isVRotate(text.text.charAt(0))) {
                			lineWidth += rectBounds.getWidth();
                		} else {
                			lineWidth += rectBounds.getHeight();
                		}
                	}
                } else if (ele instanceof EleRect) {
            		lineWidth += ((EleRect) ele).viewSize().y;
                }
            }
            this.width = lineWidth;
        } else {
            for (int i = 0; i < eleList.size(); i++) {
            	ele = eleList.get(i);
            	if (ele instanceof EleCharRect) {
            		ele = ((EleCharRect) ele).eleChar;
            	}
            	if (ele instanceof EleText) {
            		lineWidth += ((EleText) ele).getBounds().getWidth();
            	} else if (ele instanceof EleRect) {
            		lineWidth += ((EleRect) ele).viewSize().x;
            	}
            }
            this.width = lineWidth;
        }
	}
	/**
	 * @param txt
	 * @param both
	 * @return
	 */
	private void distributeSplit(ArrayList list, EleText txt, boolean both) {
		if (both) {
			int pos = 0;
			boolean numBegin = false;
			boolean wordBegin = false;
			char c;
			for (int i = 0; i < txt.text.length(); i++) {
				c = txt.text.charAt(i);
				if (!wordBegin && !numBegin && StrUtil.isDigit(c)) {
					if (pos < i) {
						list.add(new EleText(txt, txt.text.substring(pos, i)));
					}
					pos = i;
					numBegin = true;
				} else if (!wordBegin && StrUtil.isLetter(c)) {
					if (pos < i) {
						list.add(new EleText(txt, txt.text.substring(pos, i)));
					}
					pos = i;
					wordBegin = true;
				} else if (numBegin && !StrUtil.isDigit(c) && c != '.' && c != ',') {
					if (pos < i) {
						list.add(new EleText(txt, txt.text.substring(pos, i)));
					}
					numBegin = false;
					pos = i;
				} else if (wordBegin && !StrUtil.isLetter(c) && c != '_' && !StrUtil.isDigit(c)) {
					if (pos < i) {
						list.add(new EleText(txt, txt.text.substring(pos, i)));
					}
					wordBegin = false;
					pos = i;
				} else if (ElePara.isPreSign(c)) {
					if (pos < i) {
						list.add(new EleText(txt, txt.text.substring(pos, i)));
					}
					pos = i;
				} else if (!numBegin && ElePara.isPostSign(c)) {
					if (pos < i + 1) {
						list.add(new EleText(txt, txt.text.substring(pos, i + 1)));
					}
					pos = i + 1;
				}
			}
			if (pos < txt.text.length()) {
				list.add(new EleText(txt, txt.text.substring(pos)));
			}
		} else {
			for (int i = 0; i < txt.text.length(); i++) {
				list.add(new EleText(txt, String.valueOf(txt.text.charAt(i))));
			}
		}
	}
}
