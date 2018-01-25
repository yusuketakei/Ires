package com.example.ty_en.ires;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.youtube.player.YouTubeThumbnailView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckActivity extends AppCompatActivity {

    //消費税率
    private static final double TAX_RATE = 0.08 ;

    //オーダーされたメニューのリスト
    private ArrayList<MenuItem> menuItemList ;

    //リストビュー
    private ListView  checkListView ;

    //リストビューアダプタ
    private CheckArrayAdapter checkArrayAdapter ;

    //イメージ格納用のBundle
    static final Bundle imageBundle = new Bundle() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        //オーダーされたメニューリストの取得
        menuItemList = (ArrayList<MenuItem>)getIntent().getSerializableExtra("menuItemList") ;
        System.out.println(menuItemList) ;

        //オーダーされたメニューリストを表示するListViewの生成
        ListView checkListView = (ListView)findViewById(R.id.check_list) ;

        //ArrayAdapterの取得と設定
        checkArrayAdapter = new CheckArrayAdapter(getApplicationContext(),R.id.flow_list,menuItemList) ;
        checkListView.setAdapter(checkArrayAdapter);

        //Listビューの編集
        //区切り線の消去
        checkListView.setDivider(null);

        //スクロールバーを非表示
        checkListView.setVerticalScrollBarEnabled(false);

        //合計行の設定
        TextView subTotalTextView = (TextView)findViewById(R.id.sub_total) ;
        TextView taxTextView = (TextView)findViewById(R.id.tax) ;
        TextView totalTextView = (TextView)findViewById(R.id.total) ;

        //小計の計算
        long subTotal = 0;
        for(MenuItem menuItem:menuItemList){
            subTotal += menuItem.getMenuPrice() * menuItem.getMenuOrderCount() ;
        }
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance() ;
        subTotalTextView.setText(numberFormat.format(subTotal));

        //消費税
        long tax = (long)Math.floor(subTotal * TAX_RATE) ;
        taxTextView.setText(numberFormat.format(tax));

        //合計の計算
        long total = subTotal + tax ;
        totalTextView.setText(numberFormat.format(total));
    }

    //ArrayAdapterのカスタマイズ
    private class CheckArrayAdapter extends ArrayAdapter<MenuItem> {
        private LayoutInflater inflater;

        public CheckArrayAdapter(Context context, int resource, List<MenuItem> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //getViewでメニュー名と画像をセット
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            /*ここからメインの行Viewを取得*/
            //ビューの取得
            View view = inflater.inflate(R.layout.check_row_layout, null, false);

            //メニュー情報取得
            final MenuItem menuItem = getItem(position) ;

            //メニュー画像
            final ImageView menuImageView = (ImageView)view.findViewById(R.id.menu_image);
            //設定すべき画像がなければメニュー画像を別スレッドで取得
            if(imageBundle.get(menuItem.getMenuImageKey()) == null){
                System.out.println(menuItem.getMenuImageKey()+"Load") ;
                ImageFileInFactory imageFileInFactory = new ImageFileInFactory(getApplicationContext(),menuItem.getMenuImageKey()) ;
                (new AsyncTask<ImageFileInFactory,Void,Bitmap>(){
                    @Override
                    protected Bitmap doInBackground(ImageFileInFactory... ifofs){
                        return ifofs[0].readBitmap() ;
                    }
                    protected void onPostExecute(Bitmap resultBitMap){
                        imageBundle.putParcelable(menuItem.getMenuImageKey(),resultBitMap);
                        menuImageView.setImageBitmap(resultBitMap);
                        resultBitMap = null ;
                        cancel(true);
                    }
                }).execute(imageFileInFactory) ;
            }
            menuImageView.setImageBitmap((Bitmap)imageBundle.getParcelable(menuItem.getMenuImageKey()));

            //メニュー名
            TextView menuNameTextView = (TextView)view.findViewById(R.id.menu_name) ;
            menuNameTextView.setText(menuItem.getMenuName());

            //メニューオーダー数
            TextView menuOrderCountView = (TextView)view.findViewById(R.id.menu_order_count) ;
            menuOrderCountView.setText(String.valueOf(menuItem.getMenuOrderCount()));

            //メニュー金額
            NumberFormat numberFormat = NumberFormat.getNumberInstance() ;
            TextView menuPriceView = (TextView)view.findViewById(R.id.menu_price) ;
            menuPriceView.setText(numberFormat.format(menuItem.getMenuPrice()));

            return view ;
        }

    }

}
