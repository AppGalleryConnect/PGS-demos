package com.cocos.game;


import com.cocos.lib.CocosHelper;
import com.cocos.lib.CocosJavascriptJavaBridge;
import com.huawei.game.common.utils.CollectionUtil;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.Message;
import com.huawei.game.gmme.model.VolumeInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户实现的统一回调
 */
public class GMMECallbackHandler implements IGameMMEEventHandler {
    private static final String TAG = "GMMECallbackHandler";

    private ArrayList<IGameMMEEventHandler> mHandler = new ArrayList<>();

    @Override
    public void onCreate(int code, String msg) {
        LogUtil.d(TAG, "onCreate : code=" + code + ", msg=" + msg);
        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToGMMCreate(" + code + ", '"+ msg +"')");
            }
        });
    }

    @Override
    public void onMuteAllPlayers(String roomId, List<String> openIds, boolean isMuted, int code, String msg) {
        StringBuilder sb = new StringBuilder("onMuteAllPlayers : ").append("roomId=")
                .append(roomId)
                .append(", openIds=")
                .append(openIds)
                .append(", isMuted=")
                .append(isMuted)
                .append(", code=")
                .append(code)
                .append(", msg=")
                .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onMuteAllPlayers(roomId, openIds, isMuted, code, msg);
        }
    }

    @Override
    public void onMutePlayer(String roomId, String openId, boolean isMuted, int code, String msg) {
        StringBuilder sb = new StringBuilder("onMutePlayer : ").append("roomId=")
                .append(roomId)
                .append(", openId=")
                .append(openId)
                .append(", isMuted=")
                .append(isMuted)
                .append(", code=")
                .append(code)
                .append(", msg=")
                .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onMutePlayer(roomId, openId, isMuted, code, msg);
        }
    }

    @Override
    public void onJoinTeamRoom(String roomId, int code, String msg) {
        StringBuilder sb = new StringBuilder("onJoinTeamRoom : ").append("roomId=")
                .append(roomId)
                .append(", code=")
                .append(code)
                .append(", msg=")
                .append(msg);
        LogUtil.d(TAG, sb.toString());
//        for (IGameMMEEventHandler handler : mHandler) {
//            handler.onJoinTeamRoom(roomId, code, msg);
//        }

        if(code != 0){
            LogUtil.d(TAG, "onCreate : code=" + code + ", msg=" + msg);
        }

        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToGmmJoin(" + code + ", '"+ roomId + "', '" + msg + "')");
            }
        });
    }

    @Override
    public void onJoinNationalRoom(String roomId, int code, String msg) {
        StringBuilder sb = new StringBuilder("onJoinNationalRoom : ").append("roomId=")
                .append(roomId)
                .append(", code=")
                .append(code)
                .append(", msg=")
                .append(msg);
        LogUtil.d(TAG, sb.toString());

        if(code != 0){
            LogUtil.d(TAG, "onCreate : code=" + code + ", msg=" + msg);
        }
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onJoinNationalRoom(roomId, code, msg);
        }
    }

    @Override
    public void onSwitchRoom(String roomId, int code, String msg) {
        StringBuilder sb = new StringBuilder("onSwitchRoom : ").append("roomId=")
                .append(roomId)
                .append(", code=")
                .append(code)
                .append(", msg=")
                .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onSwitchRoom(roomId, code, msg);
        }
    }

    @Override
    public void onDestroy(int code, String message) {
        LogUtil.d(TAG, "onDestroy : msg=" + message);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onDestroy(code, message);
        }
    }

    @Override
    public void onLeaveRoom(String roomId, int code, String msg) {
        LogUtil.d(TAG, "onLeaveRoom : status=" + code + ", msg=" + msg);
//        for (IGameMMEEventHandler handler : mHandler) {
//            handler.onLeaveRoom(roomId, code, msg);
//        }

        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToGmmLeave(" + code + ", '"+ roomId + "', '" + msg + "')");
            }
        });
    }

    @Override
    public void onSpeakersDetection(List<String> openIds) {
        LogUtil.d(TAG, "onSpeakersDetection : openIds=" + openIds);
        if (CollectionUtil.isNotEmpty(openIds)) {
            for (IGameMMEEventHandler handler : mHandler) {
                handler.onSpeakersDetection(openIds);
            }
        }
    }

    @Override
    public void onSpeakersDetectionEx(List<VolumeInfo> userVolumeInfos) {
        LogUtil.d(TAG, "onSpeakersDetectionEx : userVolumeInfos=" + userVolumeInfos);
        if (CollectionUtil.isNotEmpty(userVolumeInfos)) {
            for (IGameMMEEventHandler handler : mHandler) {
                handler.onSpeakersDetectionEx(userVolumeInfos);
            }
        }
    }

    @Override
    public void onForbidAllPlayers(String roomId, List<String> openIds, boolean isForbidden, int code, String msg) {
        StringBuilder sb = new StringBuilder("onForbidAllPlayers : ").append("roomId=")
                .append(roomId)
                .append(", openIds=")
                .append(openIds)
                .append(", isForbidden=")
                .append(isForbidden)
                .append(", code=")
                .append(code)
                .append(", msg=")
                .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onForbidAllPlayers(roomId, openIds, isForbidden, code, msg);
        }
    }

    @Override
    public void onForbidPlayer(String roomId, String openId, boolean isForbidden, int code, String msg) {
        StringBuilder sb = new StringBuilder("onForbidPlayer : ").append("roomId=")
                .append(roomId)
                .append(", openId=")
                .append(openId)
                .append(", isForbidden=")
                .append(isForbidden)
                .append(", code=")
                .append(code)
                .append(", msg=")
                .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onForbidPlayer(roomId, openId, isForbidden, code, msg);
        }
    }

    @Override
    public void onForbiddenByOwner(String roomId, List<String> openIds, boolean isForbidden) {
        StringBuilder sb = new StringBuilder("onForbiddenByOwner : ").append("roomId=")
                .append(roomId)
                .append(", openIds=")
                .append(openIds)
                .append(", isForbidden=")
                .append(isForbidden);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onForbiddenByOwner(roomId, openIds, isForbidden);
        }
    }

    @Override
    public void onVoiceToText(String text, int code, String message) {
//        LogUtil.i(TAG, "onVoiceToText text " + text + "status " + status + "message " + message);
//        for (IGameMMEEventHandler handler : mHandler) {
//            handler.onVoiceToText(text, status, message);
//        }

        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToVT(" + code + ", '"+ text + "', '"+ message +"')");
            }
        });
    }

    @Override
    public void onPlayerOnline(String roomId, String openId) {
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onPlayerOnline(roomId, openId);
        }
    }

    @Override
    public void onPlayerOffline(String roomId, String openId) {
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onPlayerOffline(roomId, openId);
        }
    }

    @Override
    public void onTransferOwner(String roomId, int code, String msg) {
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onTransferOwner(roomId, code, msg);
        }
    }

    @Override
    public void onJoinChannel(String channelId, int code, String msg) {
//        for (IGameMMEEventHandler handler : mHandler) {
//            handler.onJoinChannel(channelId, code, msg);
//        }

        if(code != 0){
            LogUtil.d(TAG, "onCreate : code=" + code + ", msg=" + msg);
        }

        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToJoinChannel(" + code + ", '"+ channelId + "' ,'" + msg + "')");
            }
        });
    }

    @Override
    public void onLeaveChannel(String channelId, int code, String msg) {
//        for (IGameMMEEventHandler handler : mHandler) {
//            handler.onLeaveChannel(channelId, code, msg);
//        }

        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToLeaveChannel(" + code + ", '"+ channelId + "', '" + msg + "')");
            }
        });
    }

    @Override
    public void onSendMsg(Message msg) {
//        for (IGameMMEEventHandler handler : mHandler) {
//            handler.onSendMsg(msg);
//        }

        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToSendMsg('"+ msg.content + "', '" + msg.senderId + "')");
            }
        });
    }

    @Override
    public void onRecvMsg(Message msg) {
//        for (IGameMMEEventHandler handler : mHandler) {
//            handler.onRecvMsg(msg);
//        }

        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToRecvMsg('"+ msg.content + "', '" + msg.senderId + "')");
            }
        });
    }

    public void addHandler(IGameMMEEventHandler handler) {
        LogUtil.i(TAG, "addHandler! ");
        mHandler.add(handler);
    }

    public void removeHandler(IGameMMEEventHandler handler) {
        LogUtil.i(TAG, "removeHandler! ");
        mHandler.remove(handler);
    }

    @Override
    public void onRemoteMicroStateChanged(String roomId, String openId, boolean isMute) {
        LogUtil.i(TAG, "onRemoteMicroStateChanged success! filePath:" + roomId);
//        for (IGameMMEEventHandler handler : mHandler) {
//            handler.onRemoteMicroStateChanged(roomId, openId, isMute);
//        }

        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToGmmMic('"+ roomId + "', '" + openId + "', " + isMute+")");
            }
        });
    }

    /**
     * 录制语音消息回调。
     *
     * @param filePath 待上传的语音文件的地址
     * @param code 响应码
     * @param msg 响应消息
     */
    @Override
    public void onRecordAudioMsg(String filePath, int code, String msg) {
        if (code != 0) {
            LogUtil.e(TAG, "onRecordAudioMsg! filePath:" + filePath + ", code: " + code + ", msg: " + msg);
            return;
        }

        LogUtil.i(TAG, "onRecordAudioMsg success! filePath:" + filePath);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onRecordAudioMsg(filePath, code, msg);
        }
    }

    /**
     * 上传录制语音消息文件回调。
     *
     * @param filePath 待上传的语音文件的地址
     * @param fileId 待下载文件唯一标识
     * @param code 响应码
     * @param msg 响应消息
     */
    @Override
    public void onUploadAudioMsgFile(String filePath, String fileId, int code, String msg) {
        if (code != 0) {
            LogUtil.e(TAG, "onUploadAudioMsgFile! filePath:" + filePath + ", fileId:" + fileId + ", code: " + code
                    + ", msg: " + msg);
            return;
        }

        LogUtil.i(TAG, "onUploadAudioMsgFile success! filePath:" + filePath + ", fileId:" + fileId);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onUploadAudioMsgFile(filePath, fileId, code, msg);
        }
    }

    /**callbackToSendMsg
     * 下载录制语音消息文件回调。
     *
     * @param filePath 待上传的语音文件的地址
     * @param fileId 待下载文件唯一标识
     * @param code 响应码
     * @param msg 响应消息
     */
    @Override
    public void onDownloadAudioMsgFile(String filePath, String fileId, int code, String msg) {
        if (code != 0) {
            LogUtil.e(TAG, "onDownloadAudioMsgFile! filePath:" + filePath + ", fileId:" + fileId + ", code: " + code
                    + ", msg: " + msg);
            return;
        }

        LogUtil.i(TAG, "onDownloadAudioMsgFile success! filePath:" + filePath + ", fileId:" + fileId);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onDownloadAudioMsgFile(filePath, fileId, code, msg);
        }
    }

    /**
     * 播放语音消息文件回调。
     *
     * @param filePath 待上传的语音文件的地址
     * @param code 响应码
     * @param msg 响应消息
     */
    @Override
    public void onPlayAudioMsg(String filePath, int code, String msg) {
        LogUtil.i(TAG, "onPlayAudioMsg! filePath:" + filePath + ", code: " + code + ", msg: " + msg);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onPlayAudioMsg(filePath, code, msg);
        }
    }
}