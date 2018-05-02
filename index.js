'use strict';
import React, {
    PropTypes,
    Component
} from 'react';

import {
    DeviceEventEmitter,
    NativeModules,
    Platform,
    NativeAppEventEmitter,
    AppState
} from 'react-native'

// const {
//     RNReactNativeAndroidBaiduPush
// } = NativeModules;

var PushObj = NativeModules.BaiDuPushManager;

class BdPush {

    constructor() {
        this.ReceiveMessageObj = null;
        this.BackstageMessageObj = null;
    }

    monitorReceiveMessage(callBack) {
        if (Platform.OS == 'ios') {
            this.ReceiveMessageObj = NativeAppEventEmitter.addListener(
                PushObj.DidReceiveMessage, callBack);
        } else {
            this.ReceiveMessageObj = DeviceEventEmitter.addListener(
                PushObj.DidReceiveMessage, (data) => {
                    let obj = {};
                    obj.title = data.title;
                    obj.description = data.description;
                    obj.customContentString = JSON.parse(data.customContentString);
                    callBack(obj);
                });
        }
    }

    monitorBackstageOpenMessage(callBack) {

        if (Platform.OS == 'ios') {
            this.BackstageMessageObj = NativeAppEventEmitter.addListener(
                PushObj.DidOpenMessage, callBack);
        } else {
            this.BackstageMessageObj = DeviceEventEmitter.addListener(
                PushObj.DidOpenMessage, (data) => {
                    let obj = {};
                    obj.title = data.title;
                    obj.description = data.description;
                    obj.customContentString = JSON.parse(data.customContentString);
                    callBack(obj);
                });
        }

    }

    monitorMessageCancel() {

        if (this.ReceiveMessageObj) {
            this.ReceiveMessageObj.remove();
        }

        if (this.BackstageMessageObj) {
            this.BackstageMessageObj.remove();
        }

    }

    startWork(apiKey) {
        PushObj.startWork(apiKey);
    }

    async getChannelId() {
        try {
            return await PushObj.getChannelId();
        } catch (e) {
            return null;
        }
    }

    async getUserId() {
        try {
            return await PushObj.getUserId();
        } catch (e) {
            return null;
        }
    }

    testSend() {
        PushObj.testPrint("hello world");
    }

    notificationClicked(title, msg, url) {
        PushObj.notificationClicked(title, msg, url);
    }

}
export default new BdPush();