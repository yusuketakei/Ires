package com.example.ty_en.ires;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * Created by ty_en on 2016/08/30.
 */
public class ImageFileInFactory {
    private Context context ;
    private String fileName ;
    public ImageFileInFactory(Context context, String menuImageKey){
        this.context = context ;
        this.fileName = menuImageKey + ".png" ;
    }
    public Bitmap readBitmap(){
        //inputstreamを取得
        try{
            FileInputStream fis = context.openFileInput(fileName) ;
            Bitmap bitmap = BitmapFactory.decodeStream(fis) ;
            fis.close();
            return bitmap ;
        }catch (Exception e){
            e.printStackTrace();
            return null ;
        }
    }
}
