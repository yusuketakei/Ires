package com.example.ty_en.ires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WaitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        //オーダー画面への遷移ボタンを生成
        Button getSeatButton = (Button)findViewById(R.id.get_seat_button) ;
        getSeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaitActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });
    }
}
