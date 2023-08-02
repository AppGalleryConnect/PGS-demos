package com.cocos.game;

import android.app.Activity;
import com.cocos.lib.CocosHelper;
import com.cocos.lib.CocosJavascriptJavaBridge;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.model.EngineCreateParams;
import com.huawei.game.gmme.model.VoiceParam;

import java.util.Date;
public class MediaEngine {
    protected static GameMediaEngine mHwRtcEngine;

    protected static VoiceParam mVoiceParam;

    private  static Activity _activity = null;

    private static String TAG = "MediaEngine";

    private static MediaEngine _instanse = null;

    private static String _openId = "";

    private static String OPENID = "cocos";

    /**
     * RTC 引擎回调事件
     */
    private static GMMECallbackHandler mHwHandler = new GMMECallbackHandler();

    public static MediaEngine getInstanse(){
        if(_instanse == null){
            _instanse = new MediaEngine();
        }

        return _instanse;
    }

    public void init(Activity activity){
        this._activity = activity;
    }

    /**
     * 初始化多媒体
     *
     * @param openId
     */
    public static void startMediaEngine(String openId){
 

    }

    /**
     * 加入多媒体房间
     * @param roomId
     */
    public static void joinTeamRoom(String roomId){

    }

    /**
     * 开启/关闭玩家自身麦克风
     * @param isOpen
     */
    public static void enableMic(int isOpen){

    }

    /**
     * 离开房间
     * @param roomId
     */
    public static void leaveRoom(String roomId){

    }

    /**
     * 禁言/解禁其他全部玩家
     * @param roomId
     * @param isMute
     */
    public static void muteAllPlayers(String roomId, int isMute){

    }

    /**
     * 加入IM聊天群组
     * @param channelId
     */
    public static void joinGroupChannel(String channelId){

    }

    /**
     * 离开IM聊天群组
     * @param channelId
     */
    public static void leaveChannel(String channelId){

    }

    /**
     * 发送文本消息
     * @param ChannelId
     * @param content
     */
    public static void sendTextMsg(String ChannelId, String content){

    }

    /**
     * 开启语音录音
     */
    public static void startRecordAudioToText(){

    }

    /**
     * 停止语音录音
     */
    public static void stopRecordAudioToText(){

    }

    /**
     * 卸载多媒体
     */
    public static void destoryMediaEngine(){

    }


}
