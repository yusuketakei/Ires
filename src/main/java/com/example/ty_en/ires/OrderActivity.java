package com.example.ty_en.ires;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener{

    private MenuItemAdapter menuItemAdapter ;
    private ArrayList<MenuItem> menuItemList ;
    private GridView orderGridView ;
    private Bundle imageBundle ;
    private SaveMenuImageAsyncTask saveMenuImageAsyncTask ;
    private ImageFileOutFactory ifof1 ;
    private ImageFileOutFactory ifof2 ;
    private ImageFileOutFactory ifof3 ;
    private ImageFileOutFactory ifof4 ;
    private ImageFileOutFactory ifof5 ;
    //確認用のmenuItemList
    private ArrayList<MenuItem> confirmMenuItemList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        //メニューリストの生成
        menuItemList = new ArrayList() ;

        //メニューリストにサンプルデータ投入
        getMenuData();

        //GridView用のAdapter生成
        menuItemAdapter = new MenuItemAdapter(getApplicationContext(),R.id.order_list,menuItemList) ;

        //Adapter設定
        orderGridView = (GridView)findViewById(R.id.order_list) ;
        orderGridView.setAdapter(menuItemAdapter);

        //Gridビューの編集
        //スクロールバーを非表示
        orderGridView.setVerticalScrollBarEnabled(false);

        //カード部分をselectorにするため、リストのselectorは透明に
        orderGridView.setSelector(android.R.color.transparent);

        //DoneButton
        Button orderDoneButton = (Button)findViewById(R.id.order_done_button) ;
        orderDoneButton.setOnClickListener(this) ;

    }
    @Override
    protected void onStop(){
        super.onStop();
        System.out.println("STOPしたよ") ;
        orderGridView.setAdapter(null);
        menuItemAdapter = null ;
        orderGridView = null ;
        imageBundle = null ;
        saveMenuImageAsyncTask.cancel(false) ;
        saveMenuImageAsyncTask = null ;
        ifof1 = null;
        ifof2 = null;
        ifof3 = null;
        ifof4 = null;
        ifof5 = null;

    }
    @Override
    public void onClick(View v) {
        //オーダーがあるメニューのみ入れ替え
        confirmMenuItemList = new ArrayList<>() ;
        for(MenuItem item:menuItemList){
            if(item.getMenuOrderCount() > 0){
                confirmMenuItemList.add(item);
            }
        }
        //Intentにリストを登録
        Intent intent = new Intent(getApplicationContext(),OrderDoneActivity.class) ;
        intent.putExtra("menuItemList",confirmMenuItemList) ;
        startActivity(intent) ;
    }

    private class MenuItemAdapter extends ArrayAdapter<MenuItem> {
        private LayoutInflater inflater;

        public MenuItemAdapter(Context context, int resource, List<MenuItem> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            //ビューの取得
            View view = inflater.inflate(R.layout.menu_layout, null, false);

            //メニュー情報取得
            MenuItem menuItem = getItem(position) ;

            //メニュー画像
            ImageView menuImageView = (ImageView)view.findViewById(R.id.menu_image);
            menuImageView.setImageBitmap((Bitmap)imageBundle.getParcelable(menuItem.getMenuImageKey()));

            //メニュー名
            final TextView menuNameView = (TextView)view.findViewById(R.id.menu_name) ;
            menuNameView.setText(menuItem.getMenuName());

            //メニューオーダー数
            TextView menuOrderCountView = (TextView)view.findViewById(R.id.menu_order_count) ;
            menuOrderCountView.setText(String.valueOf(menuItem.getMenuOrderCount()));

            //メニュー金額
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance() ;
            TextView menuPriceView = (TextView)view.findViewById(R.id.menu_price) ;
            menuPriceView.setText(numberFormat.format(menuItem.getMenuPrice()));

            //追加ボタンにリスナー登録
            Button addButton = (Button)view.findViewById(R.id.add_button) ;
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //タグから対象オブジェクトを特定し、オーダー数の加算
                    MenuItem tempMenuItem = menuItemList.get((Integer) v.getTag()) ;
                    int menuOrderCount = tempMenuItem.getMenuOrderCount();

                    //件数が最大値未満であれば追加させる
                    if(menuOrderCount < Integer.MAX_VALUE){
                        tempMenuItem.setMenuOrderCount(++menuOrderCount);
                    }

                    //親ビューを特定し、加算後のオーダー数で更新
                    View parentView = (View)v.getParent() ;
                    TextView menuOrderCountView = (TextView)parentView.findViewById(R.id.menu_order_count) ;
                    menuOrderCountView.setText(String.valueOf(tempMenuItem.getMenuOrderCount()));
                }
            });

            //減少ボタンにリスナー登録
            Button decButton = (Button)view.findViewById(R.id.dec_button) ;
            decButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //タグから対象オブジェクトを特定し、オーダー数の減少
                    MenuItem tempMenuItem = menuItemList.get((Integer) v.getTag()) ;
                    int menuOrderCount = tempMenuItem.getMenuOrderCount();

                    //件数が１以上であれば減少させる
                    if(menuOrderCount > 0){
                        tempMenuItem.setMenuOrderCount(--menuOrderCount);
                    }

                    //親ビューを特定し、減少後のオーダー数で更新
                    View parentView = (View)v.getParent() ;
                    TextView menuOrderCountView = (TextView)parentView.findViewById(R.id.menu_order_count) ;
                    menuOrderCountView.setText(String.valueOf(menuOrderCount));
                }
            });

            //タグの設定（OnClickListener呼び出し時に使う）
            addButton.setTag(position);
            decButton.setTag(position);

            return view;
        }

    }

    private void getMenuData() {
        MenuItem item1 = new MenuItem();
        MenuItem item2 = new MenuItem();
        MenuItem item3 = new MenuItem();
        MenuItem item4 = new MenuItem();
        MenuItem item5 = new MenuItem();

        Bitmap image1;
        Bitmap image2;
        Bitmap image3;
        Bitmap image4;
        Bitmap image5;

        //設定先のImageViewを取得
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.menu_layout, null, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.menu_image) ;

        //サンプルサイズを決定
        int imageViewWidth = imageView.getLayoutParams().width ;
        int imageViewHeight = imageView.getLayoutParams().height ;

        //Optionsを用いて画像のメモリ展開を抑制
        BitmapFactory.Options options1 = new BitmapFactory.Options() ;
        options1.inJustDecodeBounds = true ;
        BitmapFactory.Options options2 = new BitmapFactory.Options() ;
        options2.inJustDecodeBounds = true ;
        BitmapFactory.Options options3 = new BitmapFactory.Options() ;
        options3.inJustDecodeBounds = true ;
        BitmapFactory.Options options4 = new BitmapFactory.Options() ;
        options4.inJustDecodeBounds = true ;
        BitmapFactory.Options options5 = new BitmapFactory.Options() ;
        options5.inJustDecodeBounds = true ;

        //画像を取得
        image1 = BitmapFactory.decodeResource(getResources(), R.raw.img_bagna_cauda,options1);
        image2 = BitmapFactory.decodeResource(getResources(), R.raw.img_beer,options2);
        image3 = BitmapFactory.decodeResource(getResources(), R.raw.img_edamame_web,options3);
        image4 = BitmapFactory.decodeResource(getResources(), R.raw.img_dassai_web,options4);
        image5 = BitmapFactory.decodeResource(getResources(), R.raw.img_pasta,options5);

        //リサイズ
        options1.inJustDecodeBounds = false ;
        options2.inJustDecodeBounds = false ;
        options3.inJustDecodeBounds = false ;
        options4.inJustDecodeBounds = false ;
        options5.inJustDecodeBounds = false ;
        options1.inPreferredConfig = Bitmap.Config.RGB_565 ;
        options2.inPreferredConfig = Bitmap.Config.RGB_565 ;
        options3.inPreferredConfig = Bitmap.Config.RGB_565 ;
        options4.inPreferredConfig = Bitmap.Config.RGB_565 ;
        options5.inPreferredConfig = Bitmap.Config.RGB_565 ;
        calculateInSampleSize(options1,imageViewWidth,imageViewHeight) ;
        image1 = BitmapFactory.decodeResource(getResources(), R.raw.img_bagna_cauda,options1);
        calculateInSampleSize(options2,imageViewWidth,imageViewHeight) ;
        image2 = BitmapFactory.decodeResource(getResources(), R.raw.img_beer,options2);
        calculateInSampleSize(options3,imageViewWidth,imageViewHeight) ;
        image3 = BitmapFactory.decodeResource(getResources(), R.raw.img_edamame_web,options3);
        calculateInSampleSize(options4,imageViewWidth,imageViewHeight) ;
        image4 = BitmapFactory.decodeResource(getResources(), R.raw.img_dassai_web,options4);
        calculateInSampleSize(options5,imageViewWidth,imageViewHeight) ;
        image5 = BitmapFactory.decodeResource(getResources(), R.raw.img_pasta,options5);

        //画像格納用のBundleを生成
        imageBundle = new Bundle();

        //リソースファイルのインプットストリームを取得し設定
        item1.setMenuName("Bagna cauda");
        item1.setMenuImageKey("image1");
        imageBundle.putParcelable(item1.getMenuImageKey(),image1);
        item1.setMenuImageKey("image1");
        item1.setSoftMovieKey("soft1");
        item1.setHardMovieKey("hard1");
        item1.setMenuPrice(1000);
        item1.setMenuOrderCount(0);

        item2.setMenuName("Japanese Beer");
        item2.setMenuImageKey("image2");
        imageBundle.putParcelable(item2.getMenuImageKey(),image2);
        item2.setMenuImageKey("image2");
        item2.setSoftMovieKey("soft2");
        item2.setHardMovieKey("hard2");
        item2.setMenuOrderCount(0);
        item2.setMenuPrice(450);

        item3.setMenuName("Edamame");
        item3.setMenuImageKey("image3");
        imageBundle.putParcelable(item3.getMenuImageKey(),image3);
        item3.setMenuImageKey("image3");
        item3.setSoftMovieKey("soft3");
        item3.setHardMovieKey("hard3");
        item3.setMenuOrderCount(0);
        item3.setMenuPrice(500);

        item4.setMenuName("Nihonshu Dassai");
        item4.setMenuImageKey("image4");
        imageBundle.putParcelable(item4.getMenuImageKey(),image4);
        item4.setMenuImageKey("image4");
        item4.setSoftMovieKey("soft4");
        item4.setHardMovieKey("hard4");
        item4.setMenuOrderCount(0);
        item4.setMenuPrice(1200);

        item5.setMenuName("Seafood Pasta");
        item5.setMenuImageKey("image5");
        imageBundle.putParcelable(item5.getMenuImageKey(),image5);
        item5.setSoftMovieKey("soft5");
        item5.setHardMovieKey("hard5");
        item5.setMenuImageKey("image5");
        item5.setMenuOrderCount(0);
        item5.setMenuPrice(2000);

        menuItemList.add(item1);
        menuItemList.add(item2);
        menuItemList.add(item3);
        menuItemList.add(item4);
        menuItemList.add(item5);

        //画像を他アクティビティで取り出せるように内部ストレージに保存
        ifof1 = new ImageFileOutFactory(getApplicationContext(),item1.getMenuImageKey(),image1);
        ifof2 = new ImageFileOutFactory(getApplicationContext(),item2.getMenuImageKey(),image2);
        ifof3 = new ImageFileOutFactory(getApplicationContext(),item3.getMenuImageKey(),image3);
        ifof4 = new ImageFileOutFactory(getApplicationContext(),item4.getMenuImageKey(),image4);
        ifof5 = new ImageFileOutFactory(getApplicationContext(),item5.getMenuImageKey(),image5);
        saveMenuImageAsyncTask = new SaveMenuImageAsyncTask() ;
        saveMenuImageAsyncTask.execute(ifof1,ifof2,ifof3,ifof4,ifof5) ;
    }
    //画像のサイズ縮小率を計算
    public static void calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // 画像の元サイズ
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = (int)Math.floor((double)height / (double)reqHeight);
            } else {
                inSampleSize = (int)Math.floor((double)width / (double)reqWidth);
            }
        }
        options.inSampleSize = inSampleSize ;
    }

    //別タスクで画像を保存
    public class SaveMenuImageAsyncTask extends AsyncTask<ImageFileOutFactory,Void,Boolean> {
        @Override
        protected Boolean doInBackground(ImageFileOutFactory... ifofs){
            for(ImageFileOutFactory ifof : ifofs){
                ifof.saveBitmap() ;
                ifof = null ;
            }
            return true ;
        }
        @Override
        protected void onPostExecute(Boolean result){
            cancel(true) ;
        }
    }

}
