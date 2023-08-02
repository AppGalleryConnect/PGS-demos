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

    }

    @Override
    public void onMuteAllPlayers(String roomId, List<String> openIds, boolean isMuted, int code, String msg) {

    }

    @Override
    public void onMutePlayer(String roomId, String openId, boolean isMuted, int code, String msg) {

    }

    @Override
    public void onJoinTeamRoom(String roomId, int code, String msg) {

    }

    @Override
    public void onJoinNationalRoom(String roomId, int code, String msg) {

    }

    @Override
    public void onSwitchRoom(String roomId, int code, String msg) {

    }

    @Override
    public void onDestroy(int code, String message) {

    }

    @Override
    public void onLeaveRoom(String roomId, int code, String msg) {

    }

    @Override
    public void onSpeakersDetection(List<String> openIds) {

    }

    @Override
    public void onSpeakersDetectionEx(List<VolumeInfo> userVolumeInfos) {

    }

    @Override
    public void onForbidAllPlayers(String roomId, List<String> openIds, boolean isForbidden, int code, String msg) {

    }

    @Override
    public void onForbidPlayer(String roomId, String openId, boolean isForbidden, int code, String msg) {

    }

    @Override
    public void onForbiddenByOwner(String roomId, List<String> openIds, boolean isForbidden) {

    }

    @Override
    public void onVoiceToText(String text, int code, String message) {

    }

    @Override
    public void onPlayerOnline(String roomId, String openId) {

    }

    @Override
    public void onPlayerOffline(String roomId, String openId) {

    }

    @Override
    public void onTransferOwner(String roomId, int code, String msg) {

    }

    @Override
    public void onJoinChannel(String channelId, int code, String msg) {

    }

    @Override
    public void onLeaveChannel(String channelId, int code, String msg) {

    }

    @Override
    public void onSendMsg(Message msg) {

    }

    @Override
    public void onRecvMsg(Message msg) {

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

}