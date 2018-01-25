package com.example.ty_en.ires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YouTubePlayActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    //定数API Key
    private static final String API_KEY = "AIzaSyCArAAuKq4xQ9K12NbCwLcXM2A1nWKe2_A" ;

    //ビデオID
    String videoId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_play);

        Intent intent = getIntent() ;
        videoId = intent.getStringExtra("videoId") ;
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.player) ;
        youTubePlayerView.initialize(API_KEY,this);
    }
    //Youtube初期化リスナー
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result){
        //初期化失敗時
        if(result.isUserRecoverableError()){
            //エラー回避が可能な場合
            result.getErrorDialog(this,1).show() ;
        }
        else{
            //エラー回避不可な場合、toastのみ表示
            String errorMsg = "Youtubeの初期化に失敗しました" ;
            Toast.makeText(this,errorMsg,Toast.LENGTH_LONG) ;
        }
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,YouTubePlayer player,boolean wasRestored){
        //Youtubeの動画IDを設定
        if(!wasRestored){
            player.cueVideo(videoId);
        }
    }
}
