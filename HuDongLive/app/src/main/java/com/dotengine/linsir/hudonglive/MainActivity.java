package com.dotengine.linsir.hudonglive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.dotengine.linsir.loglibrary.LinLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.room_name) EditText roomName;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        intent = new Intent(MainActivity.this, ChatActivity.class);
        LinLog.lLog("room name  " + roomName.getText().toString());
    }

    @OnClick(R.id.zhu_bo) public void onZhuBoClicked() {
        intent.putExtra("room", roomName.getText().toString());
        intent.putExtra("type", "zhubo");
        startActivity(intent);
    }

    @OnClick(R.id.lian_mai) public void onLianMaiClicked() {
        intent.putExtra("room", roomName.getText().toString());
        intent.putExtra("type", "lianmai");
        startActivity(intent);
    }

    @OnClick(R.id.guan_kan) public void onGuanKanClicked() {
        intent.putExtra("room", roomName.getText().toString());
        intent.putExtra("type", "guankan");
        startActivity(intent);
    }
}
