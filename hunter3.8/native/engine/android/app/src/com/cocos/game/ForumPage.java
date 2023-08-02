package com.cocos.game;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;

//import com.huawei.game.dev.gdp.android.sdk.api.PgsMoment;
//import com.huawei.game.dev.gdp.android.sdk.api.bean.MomentInitParam;
//import com.huawei.game.dev.gdp.android.sdk.api.bean.Response;
//import com.huawei.game.dev.gdp.android.sdk.api.callback.PgsInitCallback;
//import com.huawei.game.dev.gdp.android.sdk.api.callback.PgsOpenCallback;

public class ForumPage {

    private  static Activity _activity = null;

    private static String TAG = "ForumPage";

    private static ForumPage _instanse = null;

    public static ForumPage getInstanse(){
        if(_instanse == null){
            _instanse = new ForumPage();
        }

        return _instanse;
    }

    public void init(Activity activity){
        this._activity = activity;
    }


    /**
     * 初始化社区
     */
    public static void startPgs(){
//        Thread thread = new Thread(){
//            @Override
//            public void run(){
//                // 应用APP ID
//                String appId = "100173201";
//                // 游戏应用在AGC项目中的客户端ID
//                String clientId = "209505680044852224";
//                // 游戏应用在AGC项目中的客户端密钥
//                String clientSecret = "FF15879BD16235BB92C943324D3A842B1C59254CD0BADBE1F87DB28E88AF9AE8";
//                // 开发者AGC认证凭证
//                // String cpAccessToken = "eyJh***************************************************hyhA";
//                // 内嵌社区的打开方向，此处以竖屏方向为例
//                int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                PgsMoment.init(new MomentInitParam(_activity.getApplicationContext(), appId, clientId, clientSecret, null, orientation),
//                        new PgsInitCallback() {
//                            @Override
//                            public void onSuccess(Response rsp) {
//                                // 初始化成功
//                                Log.e(TAG, "RtnCode: " + rsp.getRtnCode() + "; Msg: " + rsp.getMsg());
//                            }
//                            @Override
//                            public void onFailure(Response rsp) {
//                                // 初始化失败
//                                Log.e(TAG, "RtnCode: " + rsp.getRtnCode() + "; Msg: " + rsp.getMsg());
//                            }
//                        });
//            }
//        };
//
//        thread.start();
    }

    /**
     * 打开内嵌社区
     */
    public static void openForumPage() {
//        PgsMoment.open(new PgsOpenCallback() {
//            @Override
//            public void onSuccess(Response rsp) {
//                // 成功打开内嵌社区
//                Log.e(TAG, "RtnCode: " + rsp.getRtnCode() + "; Msg: " + rsp.getMsg());
//            }
//            @Override
//            public void onFailure(Response rsp) {
//                // 打开内嵌社区失败
//                Log.e(TAG, "RtnCode: " + rsp.getRtnCode() + "; Msg: " + rsp.getMsg());
//            }
//        });
    }
}

