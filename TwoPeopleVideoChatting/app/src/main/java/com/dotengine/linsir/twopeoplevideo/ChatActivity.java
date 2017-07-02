package com.dotengine.linsir.twopeoplevideo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.dot.engine.DotEngine;
import cc.dot.engine.listener.DotEngineListener;
import cc.dot.engine.listener.TokenCallback;
import cc.dot.engine.type.DotEngineErrorType;
import cc.dot.engine.type.DotEngineStatus;
import cc.dot.engine.type.DotEngineWarnType;

/**
 *  Created by linSir 
 *  date at 2017/5/22.
 *  describe: 聊天界面
 */

public class ChatActivity extends AppCompatActivity {


    @BindView(R.id.video_layout) FrameLayout videoLayout;

    private DotEngine mDotEngine;
    private String roomName;
    private String mUserName;

    private int maxHeight;
    private int maxWidth;

    private static boolean muteAudio = true;
    private static boolean muteVideo = true;
    private static boolean speakOnPhone = true;


    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        maxHeight = this.getResources().getDisplayMetrics().heightPixels;
        maxWidth = this.getResources().getDisplayMetrics().widthPixels;

        roomName = getIntent().getExtras().getString("roomName");
        mDotEngine = DotEngine.instance(this.getApplicationContext(), listener);
        mDotEngine.startLocalMedia();

        getTokenAndJoinRoom();
    }

    @OnClick(R.id.show_functions) public void onViewClicked() {
        new MaterialDialog.Builder(this)
                .title("功能")
                .items(R.array.function)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {

                            case 0:
                                mDotEngine.switchCamera();
                                break;

                            case 1:
                                mDotEngine.muteLocalAudio(muteAudio);
                                muteAudio = !muteAudio;
                                break;

                            case 2:
                                mDotEngine.muteLocalVideo(muteVideo);
                                muteVideo = !muteVideo;
                                break;

                            case 3:
                                mDotEngine.enableSpeakerphone(speakOnPhone);
                                speakOnPhone = !speakOnPhone;
                                break;

                            case 4:
                                mDotEngine.leaveRoom();
                                mDotEngine.stopLocalMedia();
                                break;


                        }

                    }
                }).show();
    }


    private void getTokenAndJoinRoom() {

        mUserName = "user" + new Random().nextInt(100000);

        mDotEngine.generateTestToken(MyApplication.APP_KEY, MyApplication.APP_SECRET, roomName, mUserName, new TokenCallback() {
            @Override
            public void onSuccess(final String token) {


                handler.post(new Runnable() {
                    @Override public void run() {
                        String type = getIntent().getExtras().getString("type");
                        mDotEngine.joinRoom(token);
                    }
                });
            }

            @Override
            public void onFailure() {

            }
        });
    }


    private DotEngineListener listener = new DotEngineListener() {
        @Override public void onJoined(String userId) {

        }

        @Override public void onLeave(String userId) {
            if (mUserName.equals(userId)) {
                handler.post(new Runnable() {
                    @Override public void run() {
                        finish();
                    }
                });
            }
        }

        @Override public void onOccurError(DotEngineErrorType errorCode) {

        }

        @Override public void onWarning(DotEngineWarnType warnCode) {

        }

        @Override public void onAddLocalView(SurfaceView view) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(maxWidth, maxHeight);
            videoLayout.addView(view, layoutParams);
        }

        @Override public void onAddRemoteView(String userId, SurfaceView view) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(maxWidth / 3, maxHeight / 3);
            layoutParams.leftMargin = maxWidth / 3 * 2;
            layoutParams.topMargin = maxHeight / 3 * 2;
            view.setZOrderOnTop(true);
            videoLayout.addView(view, layoutParams);
        }

        @Override public void onRemoveLocalView(SurfaceView view) {
            videoLayout.removeView(view);
        }

        @Override public void onRemoveRemoteView(String userId, SurfaceView view) {
            videoLayout.removeView(view);
        }

        @Override public void onStateChange(DotEngineStatus status) {

        }
    };

    @Override protected void onDestroy() {
        super.onDestroy();
        mDotEngine.onDestroy();
    }
}
