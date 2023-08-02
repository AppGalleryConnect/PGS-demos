package com.cocos.game;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.cocos.lib.CocosHelper;
import com.cocos.lib.CocosJavascriptJavaBridge;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.EngineCreateParams;
import com.huawei.game.gmme.model.Message;
import com.huawei.game.gmme.model.Room;
import com.huawei.game.gmme.model.VoiceParam;
import com.huawei.game.gmme.model.VolumeInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static void startMediaEndine(String openId){
        LogUtil.d(TAG, "startMediaEndine : " + new Date().getTime());
        if(Permission.getInstanse().isPermissionGranted()) {
            _openId = openId;

            Thread thread = new Thread(){
                @Override
                public void run(){
                    EngineCreateParams params = new EngineCreateParams();
                    params.setOpenId(OPENID + openId); // 玩家ID
                    params.setContext(_activity.getApplicationContext()); // 应用的上下文
                    params.setLogEnable(false); // 开启SDK日志记录
                    params.setAgcAppId("106022889"); // 游戏应用在AGC上注册的APP ID
                    params.setClientId("875101003169398784"); // 客户端ID
                    params.setClientSecret("FA5074F8DBB0A2AC9C10231E53B95FB788EBAE401AFD06473EDA1FB1E0FC0320"); // 客户端ID对应的秘钥
                    params.setApiKey("DAEDANyB5hJ50PQTNRfXKOy9EXhF6xoxjWOSgmpyaU9W3sFWM4B/kgEH3LqDfXwznJg1GiRsUU0QQ5ABrzb0AeMvXJpSEO7btM7a6Q=="); // API秘钥（凭据）

                    mHwRtcEngine = GameMediaEngine.create(params, mHwHandler);

                    mVoiceParam = new VoiceParam();
                    mVoiceParam.languageCodeSet("zh");
                }
            };

            thread.start();
        }
        else{
            CocosHelper.runOnGameThread(new Runnable() {
                @Override
                public void run() {
                    CocosJavascriptJavaBridge.evalString("window.callbackToNoPer()");
                }
            });
        }
    }

    /**
     * 加入多媒体房间
     * @param roomId
     */
    public static void joinTeamRoom(String roomId){
        if(mHwRtcEngine!= null){
            Thread thread = new Thread(){
                @Override
                public void run(){
                    mHwRtcEngine.joinTeamRoom(roomId);
                }
            };

            thread.start();
        }
    }

    /**
     * 开启/关闭玩家自身麦克风
     * @param isOpen
     */
    public static void enableMic(int isOpen){
        if(mHwRtcEngine!= null)
            mHwRtcEngine.enableMic(isOpen == 0);
    }

    /**
     * 离开房间
     * @param roomId
     */
    public static void leaveRoom(String roomId){
        if(mHwRtcEngine!= null){
            Thread thread = new Thread(){
                @Override
                public void run(){
                    mHwRtcEngine.leaveRoom(roomId, "");
                }
            };

            thread.start();
        }
    }

    /**
     * 禁言/解禁其他全部玩家
     * @param roomId
     * @param isMute
     */
    public static void muteAllPlayers(String roomId, int isMute){
        if(mHwRtcEngine!= null)
            mHwRtcEngine.muteAllPlayers(roomId, isMute == 1);
    }

    /**
     * 加入IM聊天群组
     * @param channelId
     */
    public static void joinGroupChannel(String channelId){
        if (mHwRtcEngine != null){
            Thread thread = new Thread(){
                @Override
                public void run(){
                    mHwRtcEngine.joinGroupChannel(channelId);
                }
            };

            thread.start();
        }
    }

    /**
     * 离开IM聊天群组
     * @param channelId
     */
    public static void leaveChannel(String channelId){
        if(mHwRtcEngine!= null){
            Thread thread = new Thread(){
                @Override
                public void run(){
                    mHwRtcEngine.leaveChannel(channelId);
                }
            };

            thread.start();
        }
    }

    /**
     * 发送文本消息
     * @param ChannelId
     * @param content
     */
    public static void sendTextMsg(String ChannelId, String content){
        if(mHwRtcEngine!= null)
            mHwRtcEngine.sendTextMsg(ChannelId, 2, content); // recvId: 接受者ID, 单聊时传入OpenId，群聊时传入ChannelId; type: 1表示单聊, 2表示群聊; content：文本字符串
    }

    /**
     * 开启语音录音
     */
    public static void startRecordAudioToText(){
        if(mHwRtcEngine!= null)
            mHwRtcEngine.startRecordAudioToText(mVoiceParam);
    }

    /**
     * 停止语音录音
     */
    public static void stopRecordAudioToText(){
        if(mHwRtcEngine!= null)
            mHwRtcEngine.stopRecordAudioToText();
    }

    /**
     * 卸载多媒体
     */
    public static void destoryMediaEndine(){
        mHwRtcEngine.destroy();
    }


}
