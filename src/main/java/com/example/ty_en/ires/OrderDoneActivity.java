package com.example.ty_en.ires;

import android.content.Context;
import android.content.Intent;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderDoneActivity extends AppCompatActivity  implements View.OnClickListener{

    private MenuItemAdapter menuItemAdapter ;
    private ArrayList<MenuItem> menuItemList ;
    private GridView orderGridView ;
    private static Bundle imageBundle = new Bundle() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_done);

        //Intentからメニューリストを取得
        Intent intent = getIntent() ;
        menuItemList = (ArrayList<MenuItem>)intent.getSerializableExtra("menuItemList") ;

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
        Button orderDoneButton = (Button)findViewById(R.id.ordered_button) ;
        orderDoneButton.setOnClickListener(this) ;
    }
    @Override
    protected void onStop(){
        super.onStop();
        System.out.println("STOPしたよ") ;
        orderGridView.setAdapter(null);
        menuItemAdapter = null;
        orderGridView = null ;
        imageBundle = null ;
    }

    @Override
    public void onClick(View v) {
        //Intentにリストを登録
        Intent intent = new Intent(getApplicationContext(),EatFlow.class) ;
        intent.putExtra("menuItemList",menuItemList) ;

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
            View view = inflater.inflate(R.layout.order_confirm_row_layout, null, false);

            //メニュー情報取得
            final MenuItem menuItem = getItem(position) ;

            //メニュー画像
            final ImageView menuImageView = (ImageView)view.findViewById(R.id.menu_image);
            //menuImageView.setImageBitmap((Bitmap)imageBundle.getParcelable(menuItem.getMenuImageKey()));
            //メニュー画像取得スレッド後に画像設定Listener

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
            final TextView menuNameView = (TextView)view.findViewById(R.id.menu_name) ;
            menuNameView.setText(menuItem.getMenuName());

            //メニューオーダー数
            TextView menuOrderCountView = (TextView)view.findViewById(R.id.menu_order_count) ;
            menuOrderCountView.setText(String.valueOf(menuItem.getMenuOrderCount()));

            //メニュー金額
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance() ;
            TextView menuPriceView = (TextView)view.findViewById(R.id.menu_price) ;
            menuPriceView.setText(numberFormat.format(menuItem.getMenuPrice()));

            return view;
        }

    }

}
