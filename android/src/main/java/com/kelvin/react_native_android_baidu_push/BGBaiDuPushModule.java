package com.kelvin.react_native_android_baidu_push;

import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.android.pushservice.BasicPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.baidu.android.pushservice.PushNotificationBuilder;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.packagerconnection.NotificationOnlyHandler;
import com.facebook.react.uimanager.IllegalViewOperationException;

import java.util.HashMap;
import java.util.Map;

public class BGBaiDuPushModule extends ReactContextBaseJavaModule {

    static public BGBaiDuPushModule myPush;
    static public String DidReceiveMessage = "DidReceiveMessage";
    static public String DidOpenMessage = "DidOpenMessage";
    static public String channelId = "";
    static public String userId = "";
    public BGBaiDuPushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        myPush = this;
        this.initialise();
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DidReceiveMessage, DidReceiveMessage);
        constants.put(DidOpenMessage, DidOpenMessage);
        return constants;
    }

    @Override
    public String getName() {
        return "RCTBaiDuPushManager";
    }

    public void initialise(){
        PushManager.startWork(getReactApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,Utils.getMetaValue(getReactApplicationContext(), "api_key"));
    }

    //send message to react native
    public void sendMsg(String title,String description,String customContentString,String type){
        WritableMap params = Arguments.createMap();
        params.putString("title",title);
        params.putString("description",description);
        params.putString("customContentString",customContentString);

        if(type.equals(DidReceiveMessage)){
            sendEvent(getReactApplicationContext(), DidReceiveMessage, params);
        }else{
            sendEvent(getReactApplicationContext(), DidOpenMessage, params);
        }
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @ReactMethod
    public void notificationClicked(String title, String message, String url){
        BGBaiDuPushModule.myPush.sendMsg(title,message,"{\"url\": \""+url+"\"}",BGBaiDuPushModule.DidOpenMessage);
    }

    @ReactMethod
    public void getChannelId(Promise promise){
        try {
            Log.d("baidu push", BGBaiDuPushModule.channelId);
            promise.resolve(BGBaiDuPushModule.channelId);
        } catch (IllegalViewOperationException e) {
            promise.reject(e.getMessage());
        }
    }

    @ReactMethod
    public void getUserId(Promise promise){
        try {
            promise.resolve(BGBaiDuPushModule.userId);
        } catch (IllegalViewOperationException e) {
            promise.reject(e.getMessage());
        }
    }

    @ReactMethod
    public void testPrint(String name) {
        Log.i("testing", name);
    }




}
