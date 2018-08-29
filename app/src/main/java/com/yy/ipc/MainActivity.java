package com.yy.ipc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yy.ipc.demo1.MessengerActivity;
import com.yy.ipc.demo2.AIDLActivity;
import com.yy.ipc.demo3.SocketActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                startActivity(new Intent(this, MessengerActivity.class));
                break;
            case R.id.btn2:
                startActivity(new Intent(this,AIDLActivity.class));
                break;
            case R.id.btn3:
                startActivity(new Intent(this,SocketActivity.class));
                break;
        }
    }
}
