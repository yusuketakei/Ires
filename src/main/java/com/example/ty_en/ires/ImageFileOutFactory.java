package com.example.ty_en.ires;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.FileOutputStream;


/**
 * Created by ty_en on 2016/08/30.
 */
public class ImageFileOutFactory {
    private Context context ;
    private String fileName ;
    private Bitmap bitmap ;
    public ImageFileOutFactory(Context context,String menuImageKey,Bitmap bitmap){
        this.context = context ;
        this.fileName = menuImageKey + ".png" ;
        this.bitmap = bitmap ;
    }
    public boolean saveBitmap(){

        //bitmapからOutputStreamを取得
        try{
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE) ;
            bitmap.compress(Bitmap.CompressFormat.PNG,50,fos) ;
            fos.flush();
            fos.close();
            return true ;
        }catch (Exception e){
            e.printStackTrace();
            return false ;
        }
    }
}
