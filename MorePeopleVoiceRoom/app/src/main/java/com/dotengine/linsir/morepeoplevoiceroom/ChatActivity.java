package com.dotengine.linsir.morepeoplevoiceroom;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

    private DotEngine mDotEngine;
    private DotStream localStream;
    private HashMap<String, DotStream> remoteStreams = new HashMap<String, DotStream>();


    private FrameLayout frameLayout;

    private boolean muteLocalAudio = true;
    private boolean muteRemoteAudio = true;
    private boolean enableSpeakerphone = true;

    private String mUserName;
    private String mRoomName;

    private static final String BASE_AVATAR_URL = "https://api.adorable.io/avatars/200/dotengine";

    private int peopleCount = 0;


    private Handler mHandler3 = new Handler() {

        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                flick(msg.getData().getString("user"), true);

            } else {
                flick(msg.getData().getString("user"), false);

            }


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
        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mute = (Button) findViewById(R.id.mute);
        close_mic = (Button) findViewById(R.id.close_mic);
        onSpeaker = (Button) findViewById(R.id.tab_speaker);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        //recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        //mAdapter = new QuickAdapter();
        //recyclerView.setAdapter(mAdapter);

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

            localStream = dotStream;

            addVideo(dotStream.getStreamId());


            dotStream.setStreamListener(new DotStreamListener() {
                @Override public void onCameraError(DotStream dotStream, String s) {

                }

                @Override public void onVideoMuted(DotStream dotStream, boolean b) {

                }

                @Override public void onAudioMuted(DotStream dotStream, boolean b) {

                }

                @Override public void onAudioLevel(DotStream dotStream, int i) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("user", dotStream.getStreamId());
                    message.what = 1;
                    message.setData(bundle);
                    mHandler3.sendMessageDelayed(message, 0);


                    Message message2 = new Message();
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("user", dotStream.getStreamId());
                    message2.what = 2;
                    message2.setData(bundle2);
                    mHandler3.sendMessageDelayed(message2, 250);


                }
            });
        }

        @Override public void onRemoveLocalStream(DotStream dotStream) {
        }

        @Override public void onAddRemoteStream(DotStream dotStream) {
            peopleCount++;
            mHandler2.sendEmptyMessageDelayed(1, 0);

            addVideo(dotStream.getStreamId());


            remoteStreams.put(dotStream.getStreamId(), dotStream);

            dotStream.setStreamListener(new DotStreamListener() {
                @Override public void onCameraError(DotStream dotStream, String s) {

                }

                @Override public void onVideoMuted(DotStream dotStream, boolean b) {

                }

                @Override public void onAudioMuted(DotStream dotStream, boolean b) {

                }

                @Override public void onAudioLevel(DotStream dotStream, int i) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("user", dotStream.getStreamId());
                    message.what = 1;
                    message.setData(bundle);
                    mHandler3.sendMessageDelayed(message, 0);


                    Message message2 = new Message();
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("user", dotStream.getStreamId());
                    message2.what = 2;
                    message2.setData(bundle2);
                    mHandler3.sendMessageDelayed(message2, 250);


                }

            });
        }

        @Override public void onRemoveRemoteStream(DotStream dotStream) {
            peopleCount--;
            mHandler2.sendEmptyMessageDelayed(1, 0);

            remoteStreams.remove(dotStream.getStreamId());
        }

        @Override public void onStateChange(DotEngineStatus dotEngineStatus) {

        }
    };


    @Override protected void onDestroy() {
        super.onDestroy();
        mDotEngine.onDestroy();
    }


    private void addVideo(String user) {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int pw = (int) (displayMetrics.widthPixels / 4.0f);

        RelativeLayout relativeLayout = new RelativeLayout(this);

        ImageView imgView = new ImageView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(pw, pw);
        relativeLayout.addView(imgView, layoutParams);
        Glide.with(this).load(BASE_AVATAR_URL + user).fitCenter().error(Color.RED).into(imgView);


        ImageView imageView2 = new ImageView(this);
        imageView2.setBackgroundColor(Color.GREEN);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(20, 20);
        relativeLayout.addView(imageView2, layoutParams2);

        FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(pw, pw);

        int size = frameLayout.getChildCount();

        layoutParams3.leftMargin = (size % 4) * pw;
        layoutParams3.topMargin = (size / 4) * pw;

        relativeLayout.setTag(user);
        frameLayout.addView(relativeLayout, layoutParams3);

    }


    private void flick(String user, boolean light) {


        int size = frameLayout.getChildCount();
        for (int i = 0; i < size; i++) {

            if (frameLayout.getChildAt(i).getTag().equals(user)) {
                RelativeLayout relativeLayout = (RelativeLayout) frameLayout.getChildAt(i);


                if (light) {
                    relativeLayout.getChildAt(1).setBackgroundColor(Color.GREEN);
                } else {
                    relativeLayout.getChildAt(1).setBackgroundColor(Color.WHITE);

                }

            }

        }

    }


}

















