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

import java.util.ArrayList;
import java.util.List;

public class HeadPara {
    public ElePara para;
    public ArrayList paras = new ArrayList();
    public HeadPara(ElePara elePara) {
        this.para = elePara;
    }
    public static HeadPara getHeader(List headParas, int[] inx) {
        return getHeader(headParas, inx, 0);
    }
    private static HeadPara getHeader(List headParas, int[] inx, int level) {
        HeadPara headPara = (HeadPara) headParas.get(inx[level]);
        if (level + 1 < inx.length) {
            level++;
            return getHeader(headPara.paras, inx, level);
        } else {
            return headPara;
        }
    }
    public static ArrayList toHeadParas(XDoc doc) {
        ArrayList headParas = new ArrayList();
        ArrayList paras = new ArrayList();
        ElePara elePara;
        for (int i = 0; i < doc.paraList.size(); i++) {
            elePara = (ElePara) doc.paraList.get(i);
            if (elePara.heading != 0) {
                addHeadPara(headParas, paras, new HeadPara(elePara));
            }
        }
        return headParas;
    }
    private static void addHeadPara(ArrayList headParas, ArrayList paras, HeadPara para) {
        HeadPara tmpPara;
        boolean find = false;
        for (int i = paras.size() - 1; i >= 0; i--) {
            tmpPara = (HeadPara) paras.get(i);
            if (tmpPara.para.heading < para.para.heading) {
                tmpPara.paras.add(para);
                find = true;
                break;
            }
        }
        if (!find) {
            headParas.add(para);
        }
        paras.add(para);
    }
}
