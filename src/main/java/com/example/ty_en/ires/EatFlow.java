package com.example.ty_en.ires;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EatFlow extends YouTubeBaseActivity implements View.OnClickListener,YouTubeThumbnailView.OnInitializedListener,YouTubeThumbnailLoader.OnThumbnailLoadedListener{

    //定数API Key
    private static final String API_KEY = "AIzaSyCArAAuKq4xQ9K12NbCwLcXM2A1nWKe2_A" ;

    //サムネイルローダー保持用のMap
    private final Map<YouTubeThumbnailView,YouTubeThumbnailLoader> loaderMap = new HashMap() ;

    //VIDEO ID 保持用のMap
    private Map<String,String> videoIdMap = new HashMap() ;

    //オーダーされたリスト
    private ArrayList<MenuItem> menuItemList ;
    //フロー用の一時リスト
    private ArrayList<MenuItem> flowMenuItemList ;
    private ListView flowListView ;
    private static Bundle imageBundle = new Bundle() ;
    private FlowArrayAdapter flowArrayAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_flow);

        //Intentからオーダーされたリストの取得
        Intent intent = getIntent() ;
        menuItemList = (ArrayList<MenuItem>)intent.getSerializableExtra("menuItemList") ;
        flowMenuItemList = new ArrayList<>(menuItemList) ;

        //ListViewの取得
        flowListView = (ListView)findViewById(R.id.flow_list) ;

        //ArrayAdapterの取得と設定
        flowArrayAdapter = new FlowArrayAdapter(getApplicationContext(),R.id.flow_list,flowMenuItemList) ;
        flowListView.setAdapter(flowArrayAdapter);

        createVideoIdMap() ;

        //Listビューの編集
        //区切り線の消去
        flowListView.setDivider(null);

        //スクロールバーを非表示
        flowListView.setVerticalScrollBarEnabled(false);

        //カード部分をselectorにするため、リストのselectorは透明に
        flowListView.setSelector(android.R.color.transparent);

        //チェックボタンのclick実装
        Button checkButton = (Button)findViewById(R.id.check_button) ;
        checkButton.setOnClickListener(this);
    }
    @Override
    protected void onStop(){
        super.onStop();
//        System.out.println("STOPしたよ") ;
//        flowListView.setAdapter(null);
//        flowArrayAdapter = null;
//        imageBundle = null ;
    }

    //Checkボタンによる画面遷移
    @Override
    public void onClick(View view){
        //会計用のListを渡す
        Intent intent = new Intent(getApplicationContext(),CheckActivity.class) ;
        intent.putExtra("menuItemList",menuItemList) ;

        //Intentによる画面遷移
        startActivity(intent);
    }

    //VIDEO IDをマップに設定
    public void createVideoIdMap(){
        videoIdMap.put("soft1","JNsIKMcAv4I") ;
        videoIdMap.put("soft2","FRq5GZM-VJY") ;
        videoIdMap.put("soft3","Q077tLseHHM") ;
        videoIdMap.put("soft4","") ;
        videoIdMap.put("soft5","") ;
        videoIdMap.put("hard1","") ;
        videoIdMap.put("hard2","FRq5GZM-VJY") ;
        videoIdMap.put("hard3","kY82RQQmvYo") ;
        videoIdMap.put("hard4","") ;
        videoIdMap.put("hard5","") ;
    }

    //ArrayAdapterのカスタマイズ
    private class FlowArrayAdapter extends ArrayAdapter<MenuItem> {
        private LayoutInflater inflater;

        public FlowArrayAdapter(Context context, int resource, List<MenuItem> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //getViewでメニュー名と画像をセット
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            /*ここからメインの行Viewを取得*/
            //ビューの取得
            final View childMainView = inflater.inflate(R.layout.eat_flow_row_layout, null, false);

            //メニュー情報取得
            final MenuItem menuItem = getItem(position) ;

            //メニュー画像
            final ImageView menuImageView = (ImageView)childMainView.findViewById(R.id.menu_image);

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
            TextView menuNameView = (TextView)childMainView.findViewById(R.id.menu_name) ;
            menuNameView.setText(menuItem.getMenuName());

            //ソフトモード動画
            //Video IDがからでなければ取得
            if(videoIdMap.get(menuItem.getSoftMovieKey()) != null && !videoIdMap.get(menuItem.getSoftMovieKey()).isEmpty()){
                YouTubeThumbnailView softYouTubeThumbnailView = (YouTubeThumbnailView)childMainView.findViewById(R.id.soft_mode_movie) ;
                softYouTubeThumbnailView.setTag(videoIdMap.get(menuItem.getSoftMovieKey()));
                softYouTubeThumbnailView.initialize(API_KEY,EatFlow.this);

                //ソフトモード動画にclickイベントを実装（Youtube再生)
                softYouTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),YouTubePlayActivity.class) ;
                        intent.putExtra("videoId",videoIdMap.get(menuItem.getSoftMovieKey())) ;
                        startActivity(intent);
                    }
                });

            }

            //ハードモード動画
            //Video IDがからでなければ取得
            if(videoIdMap.get(menuItem.getHardMovieKey()) != null && !videoIdMap.get(menuItem.getHardMovieKey()).isEmpty()) {
                YouTubeThumbnailView hardYouTubeThumbnailView = (YouTubeThumbnailView) childMainView.findViewById(R.id.hard_mode_movie);
                hardYouTubeThumbnailView.setTag(videoIdMap.get(menuItem.getHardMovieKey()));
                hardYouTubeThumbnailView.initialize(API_KEY, EatFlow.this);

                //ハードモード動画にclickイベントを実装（Youtube再生)
                hardYouTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), YouTubePlayActivity.class);
                        intent.putExtra("videoId", videoIdMap.get(menuItem.getHardMovieKey()));
                        startActivity(intent);
                    }
                });
            }
            /*ここまでメインの行Viewを取得*/

            /*ここからサブ(フリップ後)の行Viewを取得*/
            final View childSubView = inflater.inflate(R.layout.eat_flow_row_flipped_layout, null, false);
            final TextView deleteTextView = (TextView)childSubView.findViewById(R.id.delete_text);

            /*ここからViewFlipperを取得*/
            final View parentView = inflater.inflate(R.layout.eat_flow_row_flipper_layout, null, false);
            final ViewFlipper  viewFlipper = (ViewFlipper)parentView.findViewById(R.id.flipper) ;

            //ViewFlipperにFlip対象のビューを指定
            viewFlipper.addView(childMainView);
            viewFlipper.addView(childSubView);

            //削除時のアニメーションを定義
            final Animation deleteOutAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.delete_out) ;
            final Animation deleteUpAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.delete_up) ;
            deleteOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //アニメーション後リストから要素を削除
                    for(int i=0;i < getCount();i++){
                        View tempView = flowListView.getChildAt(i) ;
                        if(tempView != null && i > getPosition(menuItem)){
                            tempView.startAnimation(deleteUpAnim);
                        }
                    }
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    remove(menuItem);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            deleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //アニメーション後リストから削除
                    parentView.startAnimation(deleteOutAnim);
                }
            });

            /*ここまでサブ(フリップ後)の行Viewを取得*/

            //GestureListenerの生成
            GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
                // X軸最低スワイプ距離
                private static final int SWIPE_MIN_DISTANCE = 50;

                // X軸最低スワイプスピード
                private static final int SWIPE_THRESHOLD_VELOCITY = 200;

                // Y軸の移動距離　これ以上なら横移動を判定しない
                private static final int SWIPE_MAX_OFF_PATH = 250;

                //ViewFlipperのFlip時の動きを指定
                //右からinする動き(右から左にフリック）
                final Animation rightInAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right_in) ;
                //左にoutする動き(右から左にフリック）
                final Animation leftOutAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left_out) ;

                //左からinする動き(左から右にフリック）
                final Animation leftInAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left_in) ;
                //右にoutする動き(左から右にフリック）
                final Animation rightOutAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right_out) ;

                // スクロールイベント
                @Override
                public boolean onScroll(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                    try {
                        if(event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE ){
                            //Toast.makeText(getApplicationContext(),"右から左",Toast.LENGTH_LONG).show();
                            //進んでなければ右から左で進む
                            if(viewFlipper.getDisplayedChild() == 0){
                                //アニメーションの設定
                                viewFlipper.setInAnimation(rightInAnim);
                                viewFlipper.setOutAnimation(leftOutAnim);
                                //次に進む
                                viewFlipper.showNext();
                             }
                        }
                        // 終了位置から開始位置の移動距離が指定値より大きい
                        // X軸の移動速度が指定値より大きい
                        if(event1.getX() - event2.getX() < -1 * SWIPE_MIN_DISTANCE ){
                            //Toast.makeText(getApplicationContext(),"左から右",Toast.LENGTH_LONG).show();
                            //進んでいれば左から右で戻る
                            if(viewFlipper.getDisplayedChild() == 1) {
                                //アニメーションの設定
                                viewFlipper.setInAnimation(leftInAnim);
                                viewFlipper.setOutAnimation(rightOutAnim);
                                //戻る
                                viewFlipper.showPrevious();
                            }
                        }

                    } catch (Exception e) {
                        // TODO
                        e.printStackTrace();
                    }

                    return false;
                }


            };

            //GestureDetectorでスワイプ処理
            final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(),mOnGestureListener) ;

            parentView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            return parentView;
        }

    }
    //サムネイル初期時のリスナー
    @Override
    public void onInitializationFailure(YouTubeThumbnailView thumbnailView,YouTubeInitializationResult result) {
        //初期化失敗時
        if (result.isUserRecoverableError()) {
            //エラー回避が可能な場合
            result.getErrorDialog(this, 1).show();
        } else {
            //エラー回避不可な場合、toastのみ表示
            String errorMsg = "Youtubeの初期化に失敗しました";
            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG);
        }
    }
    @Override
    public void onInitializationSuccess(YouTubeThumbnailView thumbnailView,YouTubeThumbnailLoader thumbnailLoader){
        //Youtubeの動画IDを設定
        thumbnailLoader.setVideo(thumbnailView.getTag().toString());
        //サムネイルローダーをViewに紐つけておく
        loaderMap.put(thumbnailView,thumbnailLoader) ;
    }

    //サムネイルロード後のリスナー
    //サムネイルロード失敗時
    @Override
    public void onThumbnailError(YouTubeThumbnailView thumbnail, YouTubeThumbnailLoader.ErrorReason reason){

    }
    //サムネイルロード成功時
    @Override
    public void onThumbnailLoaded(YouTubeThumbnailView thumbnail, String videoId){
        //サムネイルローダーを取得してリリース
        YouTubeThumbnailLoader loader = loaderMap.get(thumbnail) ;
        loader.release();
        loaderMap.remove(thumbnail) ;
    }

}
