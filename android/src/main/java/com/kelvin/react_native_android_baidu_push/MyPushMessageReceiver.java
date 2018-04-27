package com.kelvin.react_native_android_baidu_push;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class MyPushMessageReceiver extends PushMessageReceiver {


    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {

        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;

        Log.d("baidu push", responseString);
        BGBaiDuPushModule.channelId = channelId;
        BGBaiDuPushModule.userId = userId;

    }

    @Override
    public void onUnbind(Context context, int errorCode, String s) {

    }

    @Override
    public void onSetTags(Context context, int errorCode, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int errorCode, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String message, String customContentString) {
        Log.d("Baidu Push", message);

        try {
            JSONObject data = null;
            data = new JSONObject(message);
            if(!isAppIsInBackground(context)){
                //send forground push
                Log.d("Baidu Forground Push", message);
                BGBaiDuPushModule.myPush.sendMsg(data.getString("title"),data.getString("description"),"{\"url\": \""+data.getString("url")+"\"}",BGBaiDuPushModule.DidReceiveMessage);
//                BGBaiDuPushModule.myPush.sendMsg(data.getString("title"),data.getString("description"),"{\"url\": \""+data.getString("url")+"\"}",BGBaiDuPushModule.DidOpenMessage`);

            } else {
                //send local notification
                Log.d("Baidu Background Push", message);
                displayNotification(context,data.getString("title"),data.getString("description"),data.getString("url"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        BGBaiDuPushModule.myPush.sendMsg(s,s1,"{\"url\": \""+s2+"\"}",BGBaiDuPushModule.DidOpenMessage);
    }

    public void notificationClicked(String s, String s1, String s2) {
        BGBaiDuPushModule.myPush.sendMsg(s,s1,"{\"url\": \""+s2+"\"}",BGBaiDuPushModule.DidOpenMessage);
    }


    public Class getMainActivityClass(Context context) { //get MainActivity.class
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void displayNotification(Context context,String title,String content, String url) {
        Intent notificationIntent = new Intent(context, getMainActivityClass(context));
        notificationIntent.putExtra("url", url);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(context.getResources().getIdentifier("ic_stat_explicit", "mipmap", context.getPackageName()))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName())))
                .setContentTitle(title)
                .setContentText(content);

                notificationBuilder.setContentIntent(contentIntent);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0001, notification);
    }

    @Override
    public void onNotificationArrived(Context context, String title, String description, String customContentString) {
        Log.d("Baidu Push", "recieve message");
        if(!isAppIsInBackground(context)){
            BGBaiDuPushModule.myPush.sendMsg(title,description,customContentString,BGBaiDuPushModule.DidReceiveMessage);
        }
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


}
