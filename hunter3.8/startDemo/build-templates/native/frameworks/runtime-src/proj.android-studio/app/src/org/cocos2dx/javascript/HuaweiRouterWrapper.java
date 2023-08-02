package org.cocos2dx.javascript;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

//import com.huawei.ars.datamodel.AppRouter;
//import com.huawei.ars.datamodel.DataModelConstants;
//import com.huawei.ars.datamodel.DataModelInputStream;
//import com.huawei.ars.datamodel.DataModelManager;
//import com.huawei.ars.datamodel.IDataModelCallback;
import com.huawei.harmonyos.interwork.arskit.AppRouterConstants;
import com.huawei.harmonyos.interwork.arskit.AppRouterManager;
import com.huawei.harmonyos.interwork.arskit.IAppRouter;
import com.huawei.harmonyos.interwork.arskit.datamodel.DataModelConstants;
import com.huawei.harmonyos.interwork.arskit.datamodel.DataModelInputStream;
import com.huawei.harmonyos.interwork.arskit.datamodel.DataModelManager;
import com.huawei.harmonyos.interwork.arskit.datamodel.IDataModelCallback;
import com.tendcloud.tenddata.TDGAAccount;
import com.tendcloud.tenddata.TalkingDataGA;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HuaweiRouterWrapper {

    private static HuaweiRouterWrapper mInstace = null;

    private Cocos2dxActivity app;
    public String channelId = "";
//    public ArrayList<String> group = new ArrayList<String>();

    public static HuaweiRouterWrapper getInstance() {
        if (null == mInstace) {
            mInstace = new HuaweiRouterWrapper();
        }
        return mInstace;
    }

    public void init (Cocos2dxActivity app) {
        this.app = app;

        HuaweiRouterWrapper.getInstance().initDataModel();

        AppRouterManager.getInstance().init(this.app, new IAppRouter.InitListener() {
            @Override
            public void onInitDone(int i) {
                if (i == 0) {
                    Log.d("HuaweiRouterWrapper", "SDK初始化成功！");

//                    HuaweiRouterWrapper.genQRCode();

                    HuaweiRouterWrapper.getInstance().onInitDone();

//                    HuaweiRouterWrapper.getInstance().initDataModel();
                }
            }
        });

        TalkingDataGA.init(this.app, "7AF629ED0CC946ECAC6348B9112E2082", "test");
        TDGAAccount.setAccount("test");
    }

    public void onInitDone () {
        this.app.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                int result = Cocos2dxJavascriptJavaBridge.evalString("window.HuaweiRouter.onInitDone()");
            }
        });
    }

    public void onGetQrCodeContent (final String str) {
        this.app.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                Log.d("HuaweiRouterWrapper", "二维码图片生成成功!");

                Cocos2dxJavascriptJavaBridge.evalString("window.HuaweiRouter.QRCode = '';");
                int index = 0;
                //由于文件内容较大，需要反复追加赋值
                while (index < str.length()) {
                    int end = (index + 100) < str.length() ? (index + 100) : str.length();
                    String val = str.substring(index, end);
                    Cocos2dxJavascriptJavaBridge.evalString("window.HuaweiRouter.QRCode += '" + val + "'");
                    index = end;
                }

                int result = Cocos2dxJavascriptJavaBridge.evalString("window.HuaweiRouter.onQRCodeLoaded()");

                Log.d("HuaweiRouterWrapper", "将二维码发给js层：" + result);
            }
        });
    }

    public void onGetQrCodeError (final int i) {
        this.app.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString("window.HuaweiRouter.onGenQrCodeError(" + i + ")");
            }
        });
    }

    /**
     * 生成二维码
     */
    public void genCode () {
//        String handleLaunchActivity = "org.cocos2dx.huaweiHandle/org.cocos2dx.javascript.AppActivity";
//        long requiredVersion = 1;
//        String extArgs = "";
//
//        AppRouterManager.getInstance().genQrCode(handleLaunchActivity, requiredVersion, extArgs, new AppRouter.GenQrCallback() {
//            @Override
//            public void onGetQrCodeContent(String s) {
//                Log.d("HuaweiRouterWrapper", "二维码生成成功：" + s);
//
//                //根据字符串生成二维码base64
//                String base64Str = QRCodeUtil.genQRCode(s);
//
//                HuaweiRouterWrapper.getInstance().onGetQrCodeContent(base64Str);
//            }
//
//            @Override
//            public void onError(int i) {
//                HuaweiRouterWrapper.getInstance().onGetQrCodeError(i);
//            }
////            @Override
////            public void onPinCodeTimeout() {
////
////            }
//        });


        // 新的写法
        String activityName = "org.cocosgame.coin.MainAbility";
        long requiredAppVersion = 1L;
        String extArgs = "";
        AlertDialog qrCodeDialog;

        Bundle bundle = new Bundle();
        bundle.putString(AppRouterConstants.AppInfo.LAUNCH_ACTIVITY, activityName);
        bundle.putLong(AppRouterConstants.AppInfo.REQUIRED_APP_VERSION, requiredAppVersion);
//        bundle.putString(AppRouterConstants.AppInfo.START_EXTRA_PARAMETER_KEY, extArgs);
        bundle.putString(AppRouterConstants.AppInfo.REQUIRED_PACKAGE_NAME, "org.cocosgame.coin");
        bundle.putInt(AppRouterConstants.AppInfo.REQUIRED_OS_TYPE,AppRouterConstants.AppInfo.OS_TYPE_HARMONY);
        // 生成二维码
        AppRouterManager.getInstance()
                .genQrCode(bundle, new IAppRouter.GenQrListener() {
                    @Override
                    public void onGetQrCodeContent(String s) {
                        //your Code生成二维码
                        Log.d("HuaweiRouterWrapper", "获取二维码字串成功回调：" + s);
                        //根据字符串生成二维码base64
                        String base64Str = QRCodeUtil.genQRCode(s);

                        HuaweiRouterWrapper.getInstance().onGetQrCodeContent(base64Str);
                    }

                    @Override
                    public void onQrCodeInvalid(int i) {
                        Log.d("HuaweiRouterWrapper", "通知二维码失效回调：" + i);
                        HuaweiRouterWrapper.getInstance().onGetQrCodeError(i);
                    }

                    @Override
                    public void onError(int i) {
                        Log.d("HuaweiRouterWrapper", "获取二维码失败回调：" + i);
                    }
                });
    }

    /**
     * 初始化数据管道
     */
    public void initDataModel () {
        Bundle bundle = this.app.getIntent().getExtras();
        if (bundle != null) {
            bundle.putString(DataModelConstants.Parameter.SERVER_PACKAGE_NAME_KEY, "org.cocos2dx.demo");
        } else {
            bundle = new Bundle();
            bundle.putInt(DataModelConstants.Parameter.MANAGER_TYPE_KEY, DataModelConstants.ManagerType.SERVER);
        }

        Log.d("HuaweiRouterWrapper", "初始化通信通道：" + bundle);

        DataModelManager.getInstance().initDataModel(this.app, bundle, new IDataModelCallback() {
            @Override
            public void onStatusChange(final String channelId, final int status) {
                Log.d("HuaweiRouterWrapper", "onStatusChange:" + channelId + "  " + status);

                if (status == DataModelConstants.Status.CHANNEL_CONNECTED) {
                    //连接成功
                    HuaweiRouterWrapper.getInstance().channelId = channelId;
//                    if (!HuaweiRouterWrapper.getInstance().group.contains(channelId)) {
//                        HuaweiRouterWrapper.getInstance().group.add(channelId);
//                    }

                    //加上统计
//                    Map<String, Object> map = new HashMap<String, Object>();
//                    map.put("channelId", channelId);
//                    TalkingDataGA.onEvent("clientConnect", map);
                } else {
//                    if (HuaweiRouterWrapper.getInstance().channelId.equals(channelId)) {
                    if (HuaweiRouterWrapper.getInstance().channelId == channelId) {
                        HuaweiRouterWrapper.getInstance().channelId = null;
                    }

//                    HuaweiRouterWrapper.getInstance().group.remove(channelId);
                }

                HuaweiRouterWrapper.getInstance().app.runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        String cmd = "window.HuaweiRouter.onStatusChange(\"" + channelId + "\"," + status + ")";
                        Log.d("HuaweiRouterWrapper", cmd);

                        Cocos2dxJavascriptJavaBridge.evalString(cmd);
                    }
                });
            }

            @Override
            public void onSendError(String channelId, String description, int type, int errorCode) {
                Log.d("HuaweiRouterWrapper", "onSendError:" + channelId + "  description:" + description);
            }

            @Override
            public void onReceiveMsg(final String channelId, String description, final Bundle msgInfo) {
                Log.d("HuaweiRouterWrapper", "onReceiveMsg:" + channelId + "  " + description + " " + msgInfo);

                HuaweiRouterWrapper.getInstance().app.runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = msgInfo.getString("msg");
                        Log.d("HuaweiRouterWrapper", "收到指令：" + msg);
                        Cocos2dxJavascriptJavaBridge.evalString("window.HuaweiRouter.onReceiveMsg(\"" + channelId + "\",\"" + msg + "\")");
                    }
                });
            }

            @Override
            public void onReceiveBlob(String channelId, String description, byte[] blobData) {
                Log.d("HuaweiRouterWrapper", "onReceiveBlob:" + channelId + "  " + description);
            }

            @Override
            public void onReceiveFile(String channelId, String description, String destPath) {
                // your code here
            }

            @Override
            public void onReceiveStream(String channelId, String description,
                                        DataModelInputStream dataModelInputStream) {
                // your code here
            }
        });
    }

    public void sendMsg (String str) {

        this.sendMsgByChannelId(this.channelId, str);
    }

    public void sendMsgByChannelId (String channelId, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", str);
        Log.d("HuaweiRouterWrapper", "sendMsgByChannelId:" + channelId + "  " + str);
        DataModelManager.getInstance().sendMessage(channelId, DataModelConstants.SendGroupId.GROUP_ID_MEDIUM, "100", bundle);
    }

    public void boardCastMsg (String str) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", str);
        DataModelManager.getInstance().sendMessage(null, "100", bundle);
    }

    public static void genQRCode () {
        Log.d("HuaweiRouterWrapper", "开始生成二维码");
        HuaweiRouterWrapper.getInstance().genCode();
    }

    public static void sendMessage (String str) {
        HuaweiRouterWrapper.getInstance().sendMsg(str);
    }

    public static void sendMessageByChannelId (String channelId, String str) {
        HuaweiRouterWrapper.getInstance().sendMsgByChannelId(channelId, str);
    }

    public static void boardcastMessage (String str) {
        HuaweiRouterWrapper.getInstance().boardCastMsg(str);
    }

    public static boolean isChannelConnect () {
        return HuaweiRouterWrapper.getInstance().channelId != null;
    }


    public static void useVibrator () {
        HuaweiRouterWrapper.getInstance().vibrator();
    }

    public void vibrator () {
        Vibrator vibrator = (Vibrator)this.app.getSystemService(this.app.VIBRATOR_SERVICE);
        VibrationEffect effect = VibrationEffect.createOneShot(500, 100);
        vibrator.vibrate(effect);
    }
}
