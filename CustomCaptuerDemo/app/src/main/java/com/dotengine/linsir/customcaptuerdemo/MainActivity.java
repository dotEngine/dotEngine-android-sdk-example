package com.dotengine.linsir.customcaptuerdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.dotengine.linsir.loglibrary.LinToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.input_room_name) BootstrapEditText inputRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.join_room) public void onViewClicked() {
        if (inputRoomName.getText().toString().trim().equals("")) {
            LinToast.showToast("房间名字不可以为空");
            return;
        }

        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("roomName", inputRoomName.getText().toString().trim());
        startActivity(intent);


    }
}