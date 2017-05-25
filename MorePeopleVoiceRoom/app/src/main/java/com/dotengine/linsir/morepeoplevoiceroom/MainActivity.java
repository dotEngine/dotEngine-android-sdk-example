package com.dotengine.linsir.morepeoplevoiceroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText roomName;
    private Button joinRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        roomName = (EditText) findViewById(R.id.room_name);
        joinRoom = (Button) findViewById(R.id.join);


        joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("roomName", roomName.getText().toString().trim());
                startActivity(intent);
            }
        });


    }
}
