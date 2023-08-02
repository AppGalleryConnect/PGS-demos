package com.cocos.game;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.cocos.lib.CocosHelper;
import com.cocos.lib.CocosJavascriptJavaBridge;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.AntiAddictionCallback;
import com.huawei.hms.jos.AppParams;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.JosStatusCodes;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.GamesStatusCodes;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AccountAuthResult;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.utils.ResourceLoaderUtil;

import org.json.JSONException;

import java.util.List;

public class JosAppControl {

    private static Activity _activity = null;

    private static String TAG = "JosAppControl";

    private static JosAppControl _instanse = null;

    public static int SIGN_IN_INTENT = 20230719;

    private static int SIGN_IN_SUCCESS = 0;

    private static int INIT_SUCCESS = 10000;

    private static int INIT_UNDER_AGE = 10001;

    private static int INIT_ERROR = 10002;

    private static int SIGN_IN_ERROR = 10003;

    private static int NO_HUAWEI = 10004;

    public static JosAppControl getInstanse(){
        if(_instanse == null){
            _instanse = new JosAppControl();
        }

        return _instanse;
    }

    public void init(Activity activity){
        this._activity = activity;
    }

    /**
     * 查询手机内非系统应用
     * @return
     */
    private static boolean checkIsHumApp() {
        String manufacturer = Build.MANUFACTURER;
        if ("HUAWEI".equalsIgnoreCase(manufacturer)) {
            return true;
        }

        return false;
    }

    /**
     * 初始化
     */
    public static void initHuawei() {
        if(!checkIsHumApp()){
            return;
        }

        AccountAuthParams params = AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME;
        JosAppsClient appsClient = JosApps.getJosAppsClient(_activity);
        Task<Void> initTask;
        ResourceLoaderUtil.setmContext(_activity.getApplicationContext());  // 设置防沉迷提示语的Context，此行必须添加
        AppParams appParams = new AppParams(params, new AntiAddictionCallback() {
            @Override
            public void onExit() {
                Log.i(TAG, "has reject the protocol");
                // 未成年登录
                JosAppControl.getInstanse()._initCallback(INIT_UNDER_AGE, "");
            }
        });

        initTask = appsClient.init(appParams);
        initTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // 游戏初始化成功后需要调用一次浮标显示接口
                Games.getBuoyClient(_activity).showFloatWindow();
                // 必须在init成功后，才可以实现登录功能
                JosAppControl.getInstanse()._initCallback(INIT_SUCCESS, "");
            }
        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            JosAppControl.getInstanse()._initCallback(INIT_ERROR, statusCode + "");
                        }
                    }
                });
    }

    /**
     * js通知
     * @param code
     * @param msg
     */
    private void _initCallback(int code, String msg){
        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CocosJavascriptJavaBridge.evalString("window.callbackToJosInit(" + code + ",'" + msg+ "')");
            }
        });
    }

    public static AccountAuthParams getHuaweiIdParams() {
        return new AccountAuthParamsHelper().setId().setAuthorizationCode().createParams();
    }

    /**
     * 登录
     */
    public static void signIn() {
        // 必须在init成功后，才可以继续调用华为帐号静默授权接口
        Task<AuthAccount> authAccountTask = AccountAuthManager.getService(_activity, getHuaweiIdParams()).silentSignIn();
        authAccountTask.addOnSuccessListener(
                new OnSuccessListener<AuthAccount>() {
                    @Override
                    public void onSuccess(AuthAccount authAccount) {
                        getGamePlayer();
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                JosAppControl.getInstanse()._initCallback(SIGN_IN_ERROR, "login error");
//                                if (e instanceof ApiException) {
//                                    Intent intent = AccountAuthManager.getService(_activity, getHuaweiIdParams()).getSignInIntent();
//                                    _activity.startActivityForResult(intent, SIGN_IN_INTENT);
//                                }
                            }
                        });
    }

    public static void getGamePlayer() {
        // 调用getPlayersClient方法初始化
        PlayersClient client = Games.getPlayersClient(_activity);
        // 执行游戏登录
        Task<Player> task = client.getGamePlayer();
        task.addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                String unionId = player.getUnionId();
                JosAppControl.getInstanse()._initCallback(SIGN_IN_SUCCESS, unionId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "" + ((ApiException) e).getStatusCode();
                    JosAppControl.getInstanse()._initCallback(SIGN_IN_ERROR, result);
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (SIGN_IN_INTENT == requestCode) {
            if (null == data) {
                _initCallback(SIGN_IN_ERROR, "");
                return;
            }
            String jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT");
            if (TextUtils.isEmpty(jsonSignInResult)) {
                _initCallback(SIGN_IN_ERROR, "");
                return;
            }
            try {
                AccountAuthResult signInResult = new AccountAuthResult().fromJson(jsonSignInResult);
                if (0 == signInResult.getStatus().getStatusCode()) {
                    String unionId = signInResult.getAccount().getUnionId();
                    _initCallback(SIGN_IN_SUCCESS, unionId);
                } else {
                    _initCallback(SIGN_IN_ERROR, "");
                }
            } catch (JSONException var7) {
                _initCallback(SIGN_IN_ERROR, "");
            }
        }
    }

}
