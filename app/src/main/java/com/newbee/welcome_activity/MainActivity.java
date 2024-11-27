package com.newbee.welcome_activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;


public class MainActivity extends ComponentActivity {
    private TextView tv1,tv2,tv3,tv4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1=findViewById(R.id.tv_1);
        tv2=findViewById(R.id.tv_2);
        tv3=findViewById(R.id.tv_3);
        tv4=findViewById(R.id.tv_4);
        tv1.setText("RandomUtil.getInstance().getRandomInt(2)");
        tv2.setText("RandomUtil.getInstance().getRandomString(13)");
        tv3.setText("RandomUtil.getInstance().getRandomStringUseCustomStrings(44,lixiao_");
        tv4.setText("RandomUtil.getInstance().getRandomLongByLength(10)+---");



    }
}
