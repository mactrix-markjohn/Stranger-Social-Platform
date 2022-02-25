package com.mactrixapp.www.stranger.Model;

import android.graphics.Typeface;

public class FontModel {

    private Typeface typeface;
    private int fontres;
    private String fontid;
    private String fonttitle;

    public FontModel(Typeface typeface, int fontres, String fontid, String fonttitle) {
        this.typeface = typeface;
        this.fontres = fontres;
        this.fontid = fontid;
        this.fonttitle = fonttitle;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public int getFontres() {
        return fontres;
    }

    public void setFontres(int fontres) {
        this.fontres = fontres;
    }

    public String getFontid() {
        return fontid;
    }

    public void setFontid(String fontid) {
        this.fontid = fontid;
    }

    public String getFonttitle() {
        return fonttitle;
    }

    public void setFonttitle(String fonttitle) {
        this.fonttitle = fonttitle;
    }
}
