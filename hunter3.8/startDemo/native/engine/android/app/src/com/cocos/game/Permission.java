package com.cocos.game;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permission {
    /**
     * 申请权限请求码
     */
    public static final int REQUEST_PERMISSIONS_CODE = 0X001;

    private  static Activity _activity = null;

    private static String TAG = "Permission";

    private static Permission _instanse = null;

    public static Permission getInstanse(){
        if(_instanse == null){
            _instanse = new Permission();
        }

        return _instanse;
    }

    public void init(Activity activity){
        this._activity = activity;

        checkPermissions();
    }

    /**
     * 所需权限
     */
    private static final String[] NEED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    /**
     * 每个权限是否已授
     *
     * @return
     */
    public boolean isPermissionGranted(){
        if(Build.VERSION.SDK_INT >= 23){
            for(int i = 0; i < NEED_PERMISSIONS.length;i++) {
                int checkPermission = ContextCompat.checkSelfPermission(_activity.getApplicationContext(), NEED_PERMISSIONS[i]);
                /***
                 * checkPermission返回两个值
                 * 有权限: PackageManager.PERMISSION_GRANTED
                 * 无权限: PackageManager.PERMISSION_DENIED
                 */
                if(checkPermission != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            return true;
        }else{
            return true;
        }
    }


    /**
     * 检查权限
     */
    public boolean checkPermissions() {
        if(isPermissionGranted()) {
            return true;
        } else {
            //如果没有设置过权限许可，则弹出系统的授权窗口
            ActivityCompat.requestPermissions(_activity, NEED_PERMISSIONS, REQUEST_PERMISSIONS_CODE);

            return false;
        }
    }
}
