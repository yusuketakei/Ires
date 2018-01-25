package com.example.ty_en.ires;

import android.content.Intent;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.List;

public class QRCodeReaderActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView ;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurfaceView = new SurfaceView(this) ;
        mSurfaceView.setOnClickListener(onClickListener);
        setContentView(mSurfaceView);
    }

    protected void onResume(){
        super.onResume();
        SurfaceHolder holder = mSurfaceView.getHolder() ;
        holder.addCallback(callback);

    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //生成されたとき
            mCamera = Camera.open() ;
            try {
                //プレビューをセット
                mCamera.setPreviewDisplay(holder);
                setCameraDisplayOrientation(0);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder,int format,int width, int height){
            //変更時
            Camera.Parameters parameters = mCamera.getParameters() ;
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes() ;
            Camera.Size previewSize = previewSizes.get(0) ;
            parameters.setPreviewSize(previewSize.width,previewSize.height);

            //width,heightを変更
            mCamera.setParameters(parameters);
            mCamera.startPreview();

        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder){
            //破棄時
            mCamera.release();
            mCamera = null ;
        }
    } ;

    private View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            if(mCamera  != null){
                mCamera.autoFocus(autoFocusCallback);
            }
        }
    } ;

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean success,Camera camera){
            if(success){
                //現在のプレビューをデータに変換
                camera.setOneShotPreviewCallback(previewCallBack);
            }
        }
    } ;

    private Camera.PreviewCallback previewCallBack = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            //読み込む範囲
            int previewWidth = camera.getParameters().getPreviewSize().width ;
            int previewHeight = camera.getParameters().getPreviewSize().height ;

            //プレビューデータからBinaryBitmapを生成
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                    data,previewWidth,previewHeight,0,0,previewWidth,previewHeight,false) ;
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source)) ;

            //バーコードを読み込む
            Reader reader = new MultiFormatReader() ;
            Result result = null ;
            try {
                result = reader.decode(bitmap) ;
                String text = result.getText();
                //Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
                //店舗の画面に遷移
                Intent intent = new Intent(QRCodeReaderActivity.this, EntranceActivity.class);
                intent.putExtra("qr_text", text);
                startActivity(intent);
            } catch(Exception e){
                Toast.makeText(getApplicationContext(),"not found",Toast.LENGTH_SHORT).show();
            }
        }
    } ;

    // ディスプレイの向き設定
    public void setCameraDisplayOrientation(int cameraId) {
        // カメラの情報取得
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        // ディスプレイの向き取得
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0; break;
            case Surface.ROTATION_90:
                degrees = 90; break;
            case Surface.ROTATION_180:
                degrees = 180; break;
            case Surface.ROTATION_270:
                degrees = 270; break;
        }
        // プレビューの向き計算
        int result;
        if (cameraInfo.facing == cameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        }
        else {// back-facing
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        // ディスプレイの向き設定
        mCamera.setDisplayOrientation(result);
    }
}
