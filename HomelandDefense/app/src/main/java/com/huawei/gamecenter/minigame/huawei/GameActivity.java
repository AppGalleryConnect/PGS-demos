/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.huawei.gamecenter.minigame.huawei;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huawei.gamecenter.minigame.huawei.UI.MyCustomView;
import com.huawei.gamecenter.minigame.huawei.Until.Constant;
import com.huawei.gamecenter.minigame.huawei.Until.HMSLogHelper;
import com.huawei.gamecenter.minigame.huawei.Until.UntilTool;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MiniGame_Game_Act";
    private static String currentId;
    private static String photoUri = null;
    private static Context context;
    private int currentLevel = 0;
    private long timeSecond = 0;
    private int currentScore = 0;
    private MyCustomView gameDrawView;
    private TextView tvScore;
    private TextView tvTime;
    private TextView tvUserName;
    private CircleImageView gamePhoto;
    private TextView gameTopLevel;
    private Button gameOnStart;
    private Button gameOnPause;
    private AlertDialog alertDialog;
    private CountDownTimer countDownTimer;

    /**
     * @param s 弹窗提示，长提示语！
     */
    public static void showToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置为无标题(去掉Android自带的标题栏)，(全屏功能与此无关)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置为全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_view);
        init();
        ExitApplication.getInstance().addActivity(this);
        context = GameActivity.this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentScore = 300;
        initData();
    }

    private void initData() {
        gameLevelSetting(currentLevel);
        gameScoreSetting(currentScore);
    }

    /**
     * MyCustomView.BeatEnemyListener   自定义view界面回调接口
     */
    private void init() {
        tvScore = findViewById(R.id.game_top_score);
        tvTime = findViewById(R.id.game_top_time);
        gameDrawView = findViewById(R.id.mini_game_view);
        gameOnStart = findViewById(R.id.game_onStart);
        gameOnStart.setOnClickListener(this);
        gameOnPause = findViewById(R.id.game_onPause);
        gameOnPause.setOnClickListener(this);
        tvUserName = findViewById(R.id.game_top_username);
        gamePhoto = findViewById(R.id.game_top_avatar);
        gamePhoto.setImageResource(R.mipmap.game_photo_man);
        gameTopLevel = findViewById(R.id.game_top_level);
        gameLevelSetting(currentLevel);
        gameScoreSetting(currentScore);

        gameDrawView.setBeatEnemyListener(new MyCustomView.BeatEnemyListener() {
            @Override
            public void onBeatEnemy(int showMode) {
                currentScore = currentScore + 3;
                gameScoreSetting(currentScore);
            }

            @Override
            public void onFire() {
                if (currentScore <= 1) {
                    cancelTimeCount();
                    gameDrawView.gameSwitch(true);
                    showAlertDialog(Constant.MODE_ONE);
                }
                currentScore--;
                gameScoreSetting(currentScore);
            }

            @Override
            public void gameEnd(int i) {
                cancelTimeCount();
                showAlertDialog(Constant.M0DE_TWO);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.game_onStart) {
            gameDrawView.gameSwitch(false);
            gameOnStart.setVisibility(View.GONE);
            gameOnPause.setVisibility(View.VISIBLE);
            initTimeCount(timeSecond);
        }
        if (v.getId() == R.id.game_onPause) {
            if (!MyCustomView.isRefresh) {
                gameDrawView.gameSwitch(true);
                gameOnStart.setVisibility(View.VISIBLE);
                gameOnPause.setVisibility(View.INVISIBLE);
                updateScoreAndLevel();
                UntilTool.addInfo(this, currentId, currentScore);
                cancelTimeCount();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameDrawView.gameSwitch(true);
        gameOnStart.setVisibility(View.VISIBLE);
        gameOnPause.setVisibility(View.INVISIBLE);
        cancelTimeCount();
        updateScoreAndLevel();
        UntilTool.addInfo(this, currentId, currentScore);
    }

    /**
     * @param clickMode 点击事件分发标识
     */
    private void showAlertDialog(int clickMode) {
        if (clickMode == Constant.MODE_THREE) {
            alertDialog = new AlertDialog.Builder(this)
                    .setView(R.layout.game_continue)
                    .setCancelable(false)
                    .create();
            alertDialog.show();
            alertDialog.findViewById(R.id.btn_click_continue).setOnClickListener(v -> {
                currentLevel = ++currentLevel;
                gameLevelSetting(currentLevel);
                gameDrawView.setEmSpd(Constant.MODE_THREE, currentLevel);
                gameDrawView.gameSwitch(false);
                updateScoreAndLevel();
                // 开启定时器
                initTimeCount(timeSecond);
                alertDialog.dismiss();
            });
        }
        // 积分消耗完毕 是否购买积分
        if (clickMode == Constant.MODE_ONE) {
            alertDialog = new AlertDialog.Builder(this, R.style.simpleDialogStyle)
                    .setCancelable(false)
                    .create();
            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.game_score_use_up, null);
            alertDialog.show();
            alertDialog.getWindow().setContentView(v);
            alertDialog.findViewById(R.id.btn_scoreUseUp).setOnClickListener(v14 -> {
                currentScore = 30 + currentScore;
                showToast(getString(R.string.GameToast_successfulBuySore));
                gameScoreSetting(currentScore);
                gameDrawView.gameSwitch(false);
                updateScoreAndLevel();
                // 开启定时器
                initTimeCount(timeSecond);
                alertDialog.dismiss();
            });
        }
        if (clickMode == Constant.M0DE_TWO) {
            alertDialog = new AlertDialog.Builder(this, R.style.simpleDialogStyle)
                    .setCancelable(false)
                    .create();
            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.game_failed, null);
            alertDialog.show();
            alertDialog.getWindow().setContentView(v);
            alertDialog.findViewById(R.id.btn_startAgain).setOnClickListener(v1 -> {
                gameDrawView.gameSwitch(false);
                gameDrawView.setEmSpd(Constant.M0DE_TWO, currentLevel);
                // 开启定时器
                initTimeCount(timeSecond);
                // 缓存数据，重新开始
                updateScoreAndLevel();
                alertDialog.dismiss();
            });
        }
        // 游戏通关全部，选择重新开始还是  退出游戏
        if (clickMode == Constant.MODE_FOUR) {
            alertDialog = new AlertDialog.Builder(this, R.style.simpleDialogStyle)
                    .setCancelable(false)
                    .create();
            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.game_clearance, null);
            alertDialog.show();
            alertDialog.getWindow().setContentView(v);
            alertDialog.findViewById(R.id.game_Again).setOnClickListener(v12 -> {
                currentLevel = 0;
                updateScoreAndLevel();
                gameLevelSetting(currentLevel);
                gameDrawView.setEmSpd(Constant.MODE_THREE, currentLevel);
                gameDrawView.gameSwitch(false);
                // 开启定时器
                initTimeCount(timeSecond);
                alertDialog.dismiss();
            });
            // 游戏退出
            alertDialog.findViewById(R.id.game_exit).setOnClickListener(v13 -> {
                // 返回上层界面,此处先关闭界面
                GameActivity.this.finish();
                alertDialog.dismiss();
            });
        }
    }

    private void initTimeCount(long time) {
        long timeReset;

        if (time > 0) {
            timeReset = time;
        } else {
            timeReset = 45;
        }
        countDownTimer = new CountDownTimer(1000 * timeReset, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeSecond = millisUntilFinished / 1000;
                HMSLogHelper.getSingletonInstance().debug(TAG, millisUntilFinished + "time : " + timeSecond);
                gameTimeSetting((int) timeSecond);
            }

            @Override
            public void onFinish() {
                HMSLogHelper.getSingletonInstance().debug(TAG, "time finish : " + timeSecond);
                timeSecond = 0;
                showAlertDialog(Constant.MODE_THREE);
                gameDrawView.gameSwitch(true);
            }
        }.start();
    }

    private void cancelTimeCount() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * @param levelNumber 游戏关卡设置显示
     */
    private void gameLevelSetting(int levelNumber) {
        String format = String.format(getString(R.string.GameToast_levelSetting), levelNumber + 1);
        if (levelNumber >= 9) {
            showAlertDialog(Constant.MODE_FOUR);
        } else {
            gameTopLevel.setText(format);
        }
    }

    /**
     * @param score 游戏积分变化设置
     *              后续此处方法可增加存储逻辑
     */
    @SuppressLint("SetTextI18n")
    private void gameScoreSetting(int score) {
        tvScore.setText("         " + score);
    }

    /**
     * @param timeSecond 游戏剩余时间设置
     */
    @SuppressLint("SetTextI18n")
    private void gameTimeSetting(int timeSecond) {
        tvTime.setText(timeSecond + " s");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        HMSLogHelper.getSingletonInstance().debug(TAG, "unknown requestCode in onActivityResult");
        Toast.makeText(this, "unknown requestCode in onActivityResult", Toast.LENGTH_SHORT).show();
    }

    // 用于动态设置用户昵称以及头像图片
    private void setCircleImageView(String url, ImageView imageView) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(GameActivity.this)
                    .load(url)
                    .placeholder(R.mipmap.game_photo_man)
                    .fitCenter()
                    .into(imageView);
        }
    }

    private void updateScoreAndLevel() {
        gameLevelSetting(currentLevel);
        gameScoreSetting(currentScore);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UntilTool.addInfo(this, currentId, currentScore);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UntilTool.addInfo(this, currentId, currentScore);
    }
}