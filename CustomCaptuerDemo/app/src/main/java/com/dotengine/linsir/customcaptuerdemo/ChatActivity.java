package com.dotengine.linsir.customcaptuerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dotengine.linsir.loglibrary.LinLog;
import com.dotengine.linsir.loglibrary.LinToast;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.dot.engine.DotEngine;
import cc.dot.engine.DotStream;
import cc.dot.engine.DotView;
import cc.dot.engine.capturer.DotCustomCapturer;
import cc.dot.engine.capturer.VideoFrameBuffer;
import cc.dot.engine.listener.DotEngineListener;
import cc.dot.engine.listener.DotStreamListener;
import cc.dot.engine.listener.TokenCallback;
import cc.dot.engine.type.DotEngineErrorType;
import cc.dot.engine.type.DotEngineStatus;
import cc.dot.engine.type.DotEngineWarnType;

import static cc.dot.engine.capturer.VideoFrameBuffer.VideoFrameType.NV21_TYPE;

/**
 *  Created by linSir 
 *  date at 2017/5/26.
 *  describe:      
 */

public class ChatActivity extends AppCompatActivity {


    private static final int VIDEO_WIDTH = 240;
    private static final int VIDEO_HEIGHT = 320;

    private DotEngine mDotEngine;
    private DotCustomCapturer mCustomCapture;
    private String mUserName;
    private String mRoomName;


    @BindView(R.id.frame_layout) FrameLayout videoLayout;
    @BindView(R.id.camera_view) CameraView mCameraView;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        ButterKnife.bind(this);

        mRoomName = getIntent().getExtras().getString("roomName");

        mUserName = ("dot" + new Random().nextInt(100000));
        mDotEngine = DotEngine.builder()
                .setContext(this.getApplicationContext())
                .setDotEngineListener(dotEngineListener)
                .build();


        mCameraView.setPreviewResolution(VIDEO_WIDTH, VIDEO_HEIGHT);


        mCameraView.setPreviewCallback(new CameraView.PreviewCallback() {
            @Override public void onGetYuvFrame(byte[] data) {

                VideoFrameBuffer videoFrameBuffer = new VideoFrameBuffer(data, VIDEO_WIDTH, VIDEO_HEIGHT, NV21_TYPE);
                mCustomCapture.onByteBufferFrame(videoFrameBuffer, 270);


            }
        });

        mCustomCapture = new DotCustomCapturer() {
            @Override public void init() {

            }

            @Override public int start() {
                return 0;
            }

            @Override public int stop() {
                mCameraView.stopCamera();

                return 0;
            }

            @Override public void destroy() {

            }

            @Override public boolean isCaptureStarted() {
                return false;
            }

            @Override public CaptureSettings getCaptureSettings() {
                return null;
            }

            @Override public void onByteBufferFrame(VideoFrameBuffer videoFrameBuffer, int rotation) {
                super.onByteBufferFrame(videoFrameBuffer, rotation);
            }
        };
        mCustomCapture.startCapture(VIDEO_WIDTH, VIDEO_HEIGHT, 0);

    }

    @OnClick(R.id.join_room) public void onJoinRoomClicked() {
        DotEngine.generateTestToken(MyApplication.APP_KEY, MyApplication.APP_SECRET, mRoomName, mUserName, new TokenCallback() {
            @Override
            public void onSuccess(String token) {
                mDotEngine.joinRoom(token);
                LinLog.lLog("获取token成功");

            }

            @Override
            public void onFailure() {
                LinLog.lLog("获取token成功");
                LinToast.showToast("获取token失败");
            }
        });
    }

    @OnClick(R.id.leave_room) public void onLeaveRoomClicked() {
        mDotEngine.leaveRoom();
        finish();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mDotEngine.onDestroy();
        mCustomCapture = null;
        mDotEngine = null;
    }


    private DotEngineListener dotEngineListener = new DotEngineListener() {
        @Override public void onJoined(String userId) {
            if (userId.equalsIgnoreCase(mUserName)) {
                mCameraView.startCamera();

                DotStream dotStream = DotStream.builder().setAudio(true).setVideo(true).setCapturer(mCustomCapture).build();
                mDotEngine.addStream(dotStream);


            }


        }

        @Override public void onLeave(String userId) {
        }

        @Override public void onOccurError(DotEngineErrorType errorCode) {

        }

        @Override public void onWarning(DotEngineWarnType warnCode) {

        }

        @Override public void onAddLocalStream(DotStream stream) {
            addVideo(stream.getStreamId(), stream.getDotView());




        }

        @Override public void onRemoveLocalStream(DotStream stream) {
            DotView view = stream.getDotView();

            videoLayout.removeView(view);

            updateFrameLayout();
        }

        @Override public void onAddRemoteStream(DotStream stream) {

            addVideo(stream.getStreamId(), stream.getDotView());


            stream.setStreamListener(new DotStreamListener() {
                @Override public void onCameraError(DotStream stream, String error) {

                }

                @Override public void onVideoMuted(DotStream stream, boolean muted) {
                }

                @Override public void onAudioMuted(DotStream stream, boolean muted) {
                }

                @Override public void onAudioLevel(DotStream stream, int audioLevel) {
                }
            });


        }

        @Override public void onRemoveRemoteStream(DotStream stream) {
            DotView view = stream.getDotView();

            videoLayout.removeView(view);

            updateFrameLayout();
        }

        @Override public void onStateChange(DotEngineStatus status) {

        }
    };

    private void addVideo(String user, View view) {
        //九宫格布局
        view.setTag(user);
        for (int i = videoLayout.getChildCount() - 1; i >= 0; i--) {
            View childAt = videoLayout.getChildAt(i);
            if (("" + user).equals(childAt.getTag() + "")) {
                ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                videoLayout.removeView(childAt);
                videoLayout.addView(view, layoutParams);
                return;
            }
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int size = videoLayout.getChildCount();
        int pw = (int) (displayMetrics.widthPixels * 0.333f);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(pw, pw);

        layoutParams.leftMargin = (size % 3) * pw;
        layoutParams.topMargin = (size / 3) * pw;
        videoLayout.addView(view, layoutParams);

        view.requestLayout();

    }

    private void updateFrameLayout() {

        //九宫格布局
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int size = videoLayout.getChildCount();
        int pw = (int) (displayMetrics.widthPixels / 3.0f);

        for (int i = 0; i < size; i++) {
            View view = videoLayout.getChildAt(i);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(pw, pw);

            layoutParams.leftMargin = (i % 3) * pw;
            layoutParams.topMargin = (i / 3) * pw;

            videoLayout.updateViewLayout(view, layoutParams);

        }

    }

}











































