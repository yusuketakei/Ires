package com.example.ty_en.ires;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by ty_en on 2016/08/09.
 */
public class MenuItem implements Serializable {
//    private byte[] menuImage ;
    private String menuImageKey ;
    private String menuName ;
    private int menuOrderCount ;
    private String softMovieKey ;
    private String hardMovieKey ;
    private long menuPrice ;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuOrderCount() {
        return menuOrderCount;
    }

    public void setMenuOrderCount(int menuOrderCount) {
        this.menuOrderCount = menuOrderCount;
    }

    public String getMenuImageKey(){ return menuImageKey; }

    public void setMenuImageKey(String menuImageKey){ this.menuImageKey = menuImageKey ; }

    public String getSoftMovieKey() {
        return softMovieKey;
    }

    public void setSoftMovieKey(String softMovieKey) {
        this.softMovieKey = softMovieKey;
    }

    public String getHardMovieKey() {
        return hardMovieKey;
    }

    public void setHardMovieKey(String hardMovieKey) {
        this.hardMovieKey = hardMovieKey;
    }

    public long getMenuPrice() {
        return menuPrice;
    }
    public void setMenuPrice(long menuPrice) {
        this.menuPrice = menuPrice;
    }

}
