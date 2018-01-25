package com.example.ty_en.ires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EntranceActivity extends AppCompatActivity implements View.OnClickListener  {

    //人数選択のArray
    private ArrayAdapter<List<String>> askPeopleArrayAdapter ;

    //人数選択のArray用List
    private List<String> askPeopleArrayList ;

    //店舗許容人数の最大数
    static int peopleMax = 12 ;

    //喫煙選択のArray
    private ArrayAdapter<List<String>> askSmokeArrayAdapter ;

    //喫煙選択のArray用List
    private List<String> askSmokeArrayList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
        Intent intent = getIntent() ;

        //QRコードから読み取った文字列の取得
        //String qrText =intent.getStringExtra("qr_text") ;
        //TextView qrTextView = (TextView)findViewById(R.id.qr_text) ;
        //qrTextView.setText(qrText) ;

        // 人数選択のSpineer設定
        Spinner askPeopleSpinner = (Spinner)findViewById(R.id.ask_people_spinner) ;
        askPeopleArrayList = new ArrayList<String>() ;
        getPeopleArrayListContents() ;
        askPeopleArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,askPeopleArrayList) ;
        askPeopleArrayAdapter.setDropDownViewResource(R.layout.common_dropdown);
        askPeopleSpinner.setAdapter(askPeopleArrayAdapter);

        // 喫煙選択のSpineer設定
        Spinner askSmokeSpinner = (Spinner)findViewById(R.id.ask_smoke_spinner) ;
        askSmokeArrayList = new ArrayList<String>() ;
        getSmokeArrayListContents() ;
        askSmokeArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,askSmokeArrayList) ;
        askSmokeArrayAdapter.setDropDownViewResource(R.layout.common_dropdown);
        askSmokeSpinner.setAdapter(askSmokeArrayAdapter);

        //ボタンのリスナー登録
        Button okButton = (Button)findViewById(R.id.entrance_ok) ;
        okButton.setOnClickListener(this);
    }

    //人数選択用の選択肢を作成
    public void getPeopleArrayListContents(){
        //最大人数まで選択肢を作成
        for(int i=1;i<=peopleMax;i++){
            askPeopleArrayList.add(String.valueOf(i)) ;
        }
        System.out.println(askPeopleArrayList);
    }

    //喫煙選択用の選択肢を作成
    public void getSmokeArrayListContents(){
        askSmokeArrayList.add("Yes") ;
        askSmokeArrayList.add("No") ;
        askSmokeArrayList.add("Whichever") ;
    }

    //ボタンクリックイベント(Wait画面に遷移）
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, WaitActivity.class);
        startActivity(intent);
    }
}
