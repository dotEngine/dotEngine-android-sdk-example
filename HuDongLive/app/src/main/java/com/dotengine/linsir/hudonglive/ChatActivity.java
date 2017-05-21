package com.dotengine.linsir.hudonglive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dotengine.linsir.loglibrary.LinLog;
import com.dotengine.linsir.loglibrary.LinToast;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.dot.engine.DotEngine;
import cc.dot.engine.listener.DotEngineListener;
import cc.dot.engine.listener.TokenCallback;
import cc.dot.engine.type.DotEngineErrorType;
import cc.dot.engine.type.DotEngineStatus;
import cc.dot.engine.type.DotEngineVideoProfileType;
import cc.dot.engine.type.DotEngineWarnType;

/**
 *  Created by linSir 
 *  date at 2017/5/12.
 *  describe:      
 */

public class ChatActivity extends AppCompatActivity implements View.OnTouchListener {

    @BindView(R.id.video_layout) FrameLayout videoLayout;

    private int lastX, lastY;
    private static final int ZHU_BO = 100;
    private static final int LIAN_MAI = 101;
    private static final int GUAN_KAN = 102;

    private int mIdentity;
    private String roomName, mUserName;

    private static boolean muteAudio = true;
    private static boolean muteVideo = true;
    private static boolean speakOnPhone = true;

    private DotEngine mDotEngine;

    private boolean isFirst = true;

    private int maxHeight;
    private int maxWidth;

    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        maxHeight = this.getResources().getDisplayMetrics().heightPixels;
        maxWidth = this.getResources().getDisplayMetrics().widthPixels;

        mDotEngine = DotEngine.instance(this.getApplicationContext(), listener);
        roomName = getIntent().getExtras().getString("room");
        LinLog.lLog("roomName   " + roomName);
        String type = getIntent().getExtras().getString("type");
        if (type.equals("zhubo")) {
            mIdentity = ZHU_BO;
            mDotEngine.setupVideoProfile(DotEngineVideoProfileType.DotEngine_VideoProfile_480P);
            mDotEngine.startLocalMedia();
        } else if (type.equals("lianmai")) {
            mIdentity = LIAN_MAI;
            mDotEngine.setupVideoProfile(DotEngineVideoProfileType.DotEngine_VideoProfile_240P);
            mDotEngine.startLocalMedia();
        } else {
            mIdentity = GUAN_KAN;
        }

        getTokenAndJoinRoom();
    }

    private DotEngineListener listener = new DotEngineListener() {
        @Override public void onJoined(String s) {

        }

        @Override public void onLeave(String s) {
            if (mUserName.equals(s)) {
                handler.post(new Runnable() {
                    @Override public void run() {
                        finish();
                    }
                });
            }
        }

        @Override public void onOccurError(DotEngineErrorType dotEngineErrorType) {

        }

        @Override public void onWarning(DotEngineWarnType dotEngineWarnType) {

        }

        @Override public void onAddLocalView(SurfaceView view) {
            view.setTag("me");

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(maxWidth, maxHeight);

            if (mIdentity == LIAN_MAI) {
                layoutParams = new FrameLayout.LayoutParams(maxWidth / 3, maxHeight / 3);
                layoutParams.leftMargin = maxWidth / 3 * 2;
                layoutParams.topMargin = maxHeight / 3 * 2;
                view.setZOrderOnTop(true);

                view.setOnTouchListener(ChatActivity.this);

            }
            videoLayout.addView(view, layoutParams);

        }

        @Override public void onAddRemoteView(String s, SurfaceView view) {
            view.setTag(s);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(maxWidth, maxHeight);

            boolean isZhuBo = s.split("qqqqqqqqqq")[0].equals("zhubo");

            if (!isZhuBo){
                layoutParams = new FrameLayout.LayoutParams(maxWidth / 3, maxHeight / 3);
                layoutParams.leftMargin = maxWidth / 3 * 2;
                layoutParams.topMargin = maxHeight / 3 * 2;
                SurfaceView temp = (SurfaceView) videoLayout.getChildAt(0);
                if (temp != null){
                    temp.setZOrderOnTop(false);
                }
                view.setZOrderOnTop(true);
                view.setOnTouchListener(ChatActivity.this);

            }

            videoLayout.addView(view, layoutParams);







        }

        @Override public void onRemoveLocalView(SurfaceView surfaceView) {
            videoLayout.removeView(surfaceView);
        }

        @Override public void onRemoveRemoteView(String s, SurfaceView surfaceView) {
            videoLayout.removeView(surfaceView);
        }

        @Override public void onStateChange(DotEngineStatus dotEngineStatus) {

        }
    };


    private void getTokenAndJoinRoom() {

        if (mIdentity == ZHU_BO) {
            mUserName = "zhuboqqqqqqqqqq" + new Random().nextInt(100000);
        }else if (mIdentity == LIAN_MAI){
            mUserName = "lianmaiqqqqqqqqqq" +new Random().nextInt(100000);
        }else {
            mUserName = "guankanqqqqqqqqqq" +new Random().nextInt(100000);
        }


        mDotEngine.generateTestToken(MyApplication.APP_KEY, MyApplication.APP_SECRET, roomName, mUserName, new TokenCallback() {
            @Override
            public void onSuccess(final String token) {


                handler.post(new Runnable() {
                    @Override public void run() {
                        String type = getIntent().getExtras().getString("type");
                        LinLog.lLog("type   " + type);
                        mDotEngine.joinRoom(token);
                    }
                });
            }

            @Override
            public void onFailure() {

                LinToast.showToast("获取token失败");
            }
        });
    }


    @OnClick(R.id.show_functions) public void onViewClicked() {

        if (mIdentity == ZHU_BO || mIdentity == LIAN_MAI) {

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

        } else {

            new MaterialDialog.Builder(this)
                    .title("功能")
                    .items(R.array.function)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                            switch (which) {

                                case 0:
                                    mDotEngine.enableSpeakerphone(speakOnPhone);
                                    speakOnPhone = !speakOnPhone;
                                    break;

                                case 1:
                                    mDotEngine.stopLocalMedia();
                                    mDotEngine.leaveRoom();
                                    break;
                            }


                        }


                    }).show();


        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        videoLayout.removeAllViews();
        mDotEngine.onDestroy();
    }

    @Override public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                int left = view.getLeft() + dx;
                int top = view.getTop() + dy;
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.height = maxHeight / 3;
                layoutParams.width = maxWidth / 3;
                layoutParams.leftMargin = left;
                layoutParams.topMargin = top;
                view.setLayoutParams(layoutParams);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
        }
        videoLayout.invalidate();
        return true;
    }
}















