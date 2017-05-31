package com.dotengine.linsir.twopeoplevideo;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dotengine.linsir.loglibrary.LinLog;
import com.dotengine.linsir.loglibrary.LinToast;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.dot.engine.DotEngine;
import cc.dot.engine.DotStream;
import cc.dot.engine.listener.DotEngineListener;
import cc.dot.engine.listener.TokenCallback;
import cc.dot.engine.type.DotEngineErrorType;
import cc.dot.engine.type.DotEngineStatus;
import cc.dot.engine.type.DotEngineWarnType;
import pub.devrel.easypermissions.EasyPermissions;

/**
 *  Created by linSir 
 *  date at 2017/5/22.
 *  describe: 聊天界面
 */

public class ChatActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{


    @BindView(R.id.video_layout) FrameLayout videoLayout;

    private DotEngine mDotEngine;
    private String roomName;
    private String mUserName;

    private int maxHeight;
    private int maxWidth;

    private static boolean muteAudio = true;
    private static boolean muteVideo = true;
    private static boolean speakOnPhone = true;

    private DotStream localStream;

    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);





        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//检查是否获取该权限
            Log.e("lin", "===lin===> 拥有权限");
        } else {

            Log.e("lin", "===lin===> 正在申请权限");

            EasyPermissions.requestPermissions(this, "必要的权限", 0, perms);
        }






        maxHeight = this.getResources().getDisplayMetrics().heightPixels;
        maxWidth = this.getResources().getDisplayMetrics().widthPixels;

        roomName = getIntent().getExtras().getString("roomName");
        mDotEngine = DotEngine.builder()
                .setContext(this.getApplicationContext())
                .setDotEngineListener(mDotEngineListener)
                .build();

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
                                localStream.switchCamera();
                                break;

                            case 1:
                                localStream.muteAudio(muteAudio);
                                muteAudio = !muteAudio;
                                break;

                            case 2:
                                localStream.muteVideo(muteVideo);
                                muteVideo = !muteVideo;
                                break;

                            case 3:
                                mDotEngine.enableSpeakerphone(speakOnPhone);
                                speakOnPhone = !speakOnPhone;
                                break;

                            case 4:
                                mDotEngine.leaveRoom();
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


    private DotEngineListener mDotEngineListener = new DotEngineListener() {
        @Override
        public void onJoined(String userId) {
            if (userId.equalsIgnoreCase(mUserName)) {
                //mCameraView.startCamera();

                DotStream dotStream = DotStream.builder().setAudio(true).setVideo(true).build();
                mDotEngine.addStream(dotStream);


            }

        }

        @Override
        public void onLeave(String userId) {
            if (mUserName.equals(userId)) {
                handler.post(new Runnable() {
                    @Override public void run() {
                        finish();
                    }
                });
            }
        }

        @Override
        public void onOccurError(DotEngineErrorType errorCode) {

        }

        @Override
        public void onWarning(DotEngineWarnType warnCode) {

        }

        @Override
        public void onAddLocalStream(DotStream stream) {
            localStream = stream;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(maxWidth, maxHeight);
            videoLayout.addView(stream.getDotView(), layoutParams);
        }

        @Override
        public void onRemoveLocalStream(DotStream stream) {
            videoLayout.removeView(stream.getDotView());
        }

        @Override
        public void onAddRemoteStream(DotStream stream) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(maxWidth / 3, maxHeight / 3);
            layoutParams.leftMargin = maxWidth / 3 * 2;
            layoutParams.topMargin = maxHeight / 3 * 2;
            stream.getDotView().setZOrder(2);
            videoLayout.addView(stream.getDotView(), layoutParams);
//
        }

        @Override
        public void onRemoveRemoteStream(DotStream stream) {
            videoLayout.removeView(stream.getDotView());
        }

        @Override
        public void onStateChange(DotEngineStatus status) {

        }
    };


//    private DotEngineListener dotEngineListener = new DotEngineListener() {
//        @Override public void onJoined(String userId) {
//
//        }
//
//        @Override public void onLeave(String userId) {
//            if (mUserName.equals(userId)) {
//                handler.post(new Runnable() {
//                    @Override public void run() {
//                        finish();
//                    }
//                });
//            }
//        }
//
//        @Override public void onOccurError(DotEngineErrorType errorCode) {
//
//        }
//
//        @Override public void onWarning(DotEngineWarnType warnCode) {
//
//        }
//
//        @Override public void onAddLocalView(SurfaceView view) {
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(maxWidth, maxHeight);
//            videoLayout.addView(view, layoutParams);
//        }
//
//        @Override public void onAddRemoteView(String userId, SurfaceView view) {
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(maxWidth / 3, maxHeight / 3);
//            layoutParams.leftMargin = maxWidth / 3 * 2;
//            layoutParams.topMargin = maxHeight / 3 * 2;
//            view.setZOrderOnTop(true);
//            videoLayout.addView(view, layoutParams);
////        }
//
//        @Override public void onRemoveLocalView(SurfaceView view) {
//            videoLayout.removeView(view);
//        }
//
//        @Override public void onRemoveRemoteView(String userId, SurfaceView view) {
//            videoLayout.removeView(view);
//        }
//
//        @Override public void onStateChange(DotEngineStatus status) {
//
//        }
//    };

    @Override protected void onDestroy() {
        super.onDestroy();
        mDotEngine.onDestroy();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this,"权限申请异常",Toast.LENGTH_SHORT).show();
    }
}
