# TwoPeopleVideoChatting
> TwoPeopleVideoChatting is a dotEngine android demo. It show two people in the same room and video chatting.



## function

- switchCamera
- muteLocalAudio
- muteLocalVideo
- enableSpeakerphone
- joinRoom
- leaveRoom



## important

```java
compile 'cc.dot.engine:dotengine:1.0.0'
compile 'com.squareup.okhttp3:okhttp:3.5.0'
```



## easy use dotengine sdk

```java
private DotEngine mDotEngine;
mDotEngine = DotEngine.instance(this.getApplicationContext(), listener);
mDotEngine.startLocalMedia();
mDotEngine.generateTestToken(MyApplication.APP_KEY, MyApplication.APP_SECRET, roomName, mUserName, new TokenCallback() {
            @Override
            public void onSuccess(final String token) {
				handler.post(new Runnable() {
                    @Override public void run() {
                        mDotEngine.joinRoom(token);
                    }
                });
            }

            @Override
            public void onFailure() {
              
            }
        });
```

if you already do this and success,you already in the room (webrtc room). You will have a localStream, it will trigger ``onAddLocalView`` , if room have another people or when somebody join in the room will trigger ``onAddRemoteView`` . You can put the two views in a layout. And the two people may talk on the software.If you want to konw another API please read [dot-engine-android-skd's API](http://docs.dot.cc/#/dot-engine-android-sdk). 

 

