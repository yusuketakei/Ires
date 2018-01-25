package com.example.ty_en.ires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ボタンにイベント登録
        Button checkinButton = (Button)findViewById(R.id.checkinButton) ;
        checkinButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, QRCodeReaderActivity.class);
        startActivity(intent);
    }

}
