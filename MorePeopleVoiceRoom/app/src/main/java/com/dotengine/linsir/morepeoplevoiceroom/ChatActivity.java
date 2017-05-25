package com.dotengine.linsir.morepeoplevoiceroom;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cc.dot.engine.DotEngine;
import cc.dot.engine.DotStream;
import cc.dot.engine.listener.DotEngineListener;
import cc.dot.engine.listener.DotStreamListener;
import cc.dot.engine.listener.TokenCallback;
import cc.dot.engine.type.DotEngineErrorType;
import cc.dot.engine.type.DotEngineStatus;
import cc.dot.engine.type.DotEngineWarnType;

/**
 *  Created by linSir 
 *  date at 2017/5/25.
 *  describe: chatActivity
 */

public class ChatActivity extends AppCompatActivity {

    private TextView peoples;
    private Button exit, mute, close_mic, onSpeaker;
    private RecyclerView recyclerView;
    private QuickAdapter mAdapter;

    private DotEngine mDotEngine;
    private DotStream localStream;
    private ArrayList<Img> imgList = new ArrayList<Img>();
    private HashMap<String, DotStream> remoteStreams = new HashMap<String, DotStream>();


    private boolean muteLocalAudio = true;
    private boolean muteRemoteAudio = true;
    private boolean enableSpeakerphone = true;

    private String mUserName;
    private String mRoomName;

    private static final String BASE_AVATAR_URL = "https://api.adorable.io/avatars/200/dotengine";

    private int peopleCount = 0;

    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.setNewData(imgList);
        }
    };

    private Handler mHandler2 = new Handler() {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            peoples.setText("peoples: " + peopleCount);
        }
    };

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        initView();

        initOnclickListener();

        initDotEngine();

    }


    private void initDotEngine() {
        mUserName = ("dot" + new Random().nextInt(100000));
        mRoomName = getIntent().getExtras().getString("roomName");
        mDotEngine = DotEngine.builder()
                .setContext(this.getApplicationContext())
                .setDotEngineListener(dotEngineListener)
                .build();

        DotEngine.generateTestToken(MyApplication.APP_KEY, MyApplication.APP_SECRET, mRoomName, mUserName, new TokenCallback() {
            @Override
            public void onSuccess(String token) {
                mDotEngine.joinRoom(token);
            }

            @Override
            public void onFailure() {
            }
        });
    }


    private void initView() {
        peoples = (TextView) findViewById(R.id.peoples);
        exit = (Button) findViewById(R.id.exit);
        peoples = (TextView) findViewById(R.id.peoples);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mute = (Button) findViewById(R.id.mute);
        close_mic = (Button) findViewById(R.id.close_mic);
        onSpeaker = (Button) findViewById(R.id.tab_speaker);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new QuickAdapter();
        recyclerView.setAdapter(mAdapter);

    }


    private void initOnclickListener() {
        exit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mDotEngine.removeStream(localStream);
                for (DotStream stream : remoteStreams.values()) {
                    mDotEngine.removeStream(stream);
                }
                mDotEngine.leaveRoom();
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                for (DotStream dotStream : remoteStreams.values()) {
                    dotStream.muteAudio(muteRemoteAudio);
                }
                muteRemoteAudio = !muteRemoteAudio;

            }
        });

        close_mic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                localStream.muteAudio(muteLocalAudio);
                muteLocalAudio = !muteLocalAudio;


            }
        });

        onSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mDotEngine.enableSpeakerphone(enableSpeakerphone);
                enableSpeakerphone = !enableSpeakerphone;
            }
        });
    }


    private DotEngineListener dotEngineListener = new DotEngineListener() {
        @Override public void onJoined(String s) {

            if (mUserName.equals(s)) {
                DotStream dotStream = DotStream.builder().setAudio(true).build();
                mDotEngine.addStream(dotStream);
            }

        }

        @Override public void onLeave(String s) {
            if (s.equals(mUserName)) {
                finish();
            }

        }

        @Override public void onOccurError(DotEngineErrorType dotEngineErrorType) {

        }

        @Override public void onWarning(DotEngineWarnType dotEngineWarnType) {

        }

        @Override public void onAddLocalStream(DotStream dotStream) {
            peopleCount++;
            mHandler2.sendEmptyMessageDelayed(1, 0);

            final Img img = new Img(dotStream.getStreamId(), BASE_AVATAR_URL + dotStream.getStreamId(), true);
            imgList.add(img);
            mHandler.sendEmptyMessageDelayed(1, 0);
            localStream = dotStream;


            dotStream.setStreamListener(new DotStreamListener() {
                @Override public void onCameraError(DotStream dotStream, String s) {

                }

                @Override public void onVideoMuted(DotStream dotStream, boolean b) {

                }

                @Override public void onAudioMuted(DotStream dotStream, boolean b) {
                    int position = findStreamIdPosition(dotStream.getStreamId());
                    imgList.get(position).setTalking(!b);
                    mHandler.sendEmptyMessageDelayed(1, 0);
                }
            });
        }

        @Override public void onRemoveLocalStream(DotStream dotStream) {
            int position = findStreamIdPosition(dotStream.getStreamId());
            if (position != -1) {
                imgList.remove(position);
                mHandler.sendEmptyMessageDelayed(1, 0);
            }
        }

        @Override public void onAddRemoteStream(DotStream dotStream) {
            peopleCount++;
            mHandler2.sendEmptyMessageDelayed(1, 0);

            Img img = new Img(dotStream.getStreamId(), BASE_AVATAR_URL + dotStream.getStreamId(), true);
            imgList.add(img);
            mHandler.sendEmptyMessageDelayed(1, 0);

            remoteStreams.put(dotStream.getStreamId(), dotStream);

            dotStream.setStreamListener(new DotStreamListener() {
                @Override public void onCameraError(DotStream dotStream, String s) {

                }

                @Override public void onVideoMuted(DotStream dotStream, boolean b) {

                }

                @Override public void onAudioMuted(DotStream dotStream, boolean b) {
                    int position = findStreamIdPosition(dotStream.getStreamId());
                    imgList.get(position).setTalking(!b);
                    mHandler.sendEmptyMessageDelayed(1, 0);
                }
            });
        }

        @Override public void onRemoveRemoteStream(DotStream dotStream) {
            peopleCount--;
            mHandler2.sendEmptyMessageDelayed(1, 0);

            int position = findStreamIdPosition(dotStream.getStreamId());
            if (position != -1) {
                imgList.remove(position);
                mHandler.sendEmptyMessageDelayed(1, 0);
            }
            remoteStreams.remove(dotStream.getStreamId());
        }

        @Override public void onStateChange(DotEngineStatus dotEngineStatus) {

        }
    };


    private int findStreamIdPosition(String id) {
        for (int i = 0; i < imgList.size(); i++) {
            if (imgList.get(i).getName().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mDotEngine.onDestroy();
    }
}

















