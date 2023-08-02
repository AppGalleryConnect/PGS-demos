package com.example.cocosdemo;

import com.cocos.lib.CocosHelper;
import com.cocos.lib.CocosJavascriptJavaBridge;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.obs.services.ObsClient;
import com.obs.services.model.ObsObject;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ObsControl {
    private static ObsClient _obsClient = null;

    public static void initObs(){
        if(_obsClient == null){
            String endPoint = "obs.cn-north-4.myhuaweicloud.com";
            String ak = "PFN6H4T1JVEF9NGABJ6A";
            String sk = "Wm6FMV8H2mKbpPDuHAXOybDmzrrPGUlXZrufmGO8";
            // 创建ObsClient实例
            _obsClient = new ObsClient(ak, sk, endPoint);
            CocosHelper.runOnGameThread(new Runnable() {
                @Override
                public void run() {
                    CocosJavascriptJavaBridge.evalString("window.callbackObsSuccess()");
                }
            });
        }
    }

    /**
     * 设置排行榜
     *
     * @param info
     * @throws IOException
     */
    public static void putRanks(String info){
        if(_obsClient == null){
            return;
        }
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    ObsControl._putRanks(info);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * 查询 排行榜
     *
     * @return
     * @throws IOException
     */
    @Nullable
    private static List<RankInfo> getRanks() throws IOException {
        if(_obsClient == null){
            return new ArrayList<>();
        }

        ObsObject obsObject = _obsClient.getObject("huawei-hunter", "test.json");
        InputStream content = obsObject.getObjectContent();
        if (content != null)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String ranks = reader.readLine();
            reader.close();
            if(ranks != null){
                List<RankInfo> rankList = new ArrayList<>();
                ZSONArray zsonArray = ZSONArray.stringToZSONArray(ranks);
                for(int index = 0; index < zsonArray.size(); index ++){
                    ZSONObject rankOne = zsonArray.getZSONObject(index);
                    RankInfo rankInfo = new RankInfo();
                    rankInfo.id = rankOne.getString("playerId");
                    rankInfo.score = rankOne.getInteger("score");
                    rankInfo.name = rankOne.getString("name");
                    rankInfo.icon = rankOne.getString("icon");
                    rankInfo.staticId = rankOne.getInteger("staticId");

                    rankList.add(rankInfo);
                }
                return rankList;
            }
        }

        return new ArrayList<>();
    }

    /**
     * 排行榜查询
     *
     * @param info
     * @throws IOException
     */
    private static void _putRanks(String info) throws IOException {
        ZSONObject rankOne = ZSONObject.stringToZSON(info);
        RankInfo rankInfo = new RankInfo();
        rankInfo.id = rankOne.getString("playerId");
        rankInfo.score = rankOne.getInteger("score");
        rankInfo.name = rankOne.getString("name");
        rankInfo.icon = rankOne.getString("icon");
        rankInfo.staticId = rankOne.getInteger("staticId");

        List<RankInfo> rankList = getRanks();
        for(int index = 0; index < rankList.size(); index ++){
            if(rankList.get(index).id.equals(rankInfo.id)){
                // 分数大 就去删除后插入
                if(rankInfo.score >= rankList.get(index).score){
                    rankList.remove(index);

                    break;
                }
                else {
                    // 分数小 就不插入
                    return;
                }
            }
        }

        boolean isSetIn = false;
        // 查找排行榜中分数大的插入
        for(int index = 0; index < rankList.size(); index ++){
            if(rankInfo.score >= rankList.get(index).score){
                rankList.add(index, rankInfo);
                isSetIn = true;
                break;
            }
        }

        // 超过30 删掉
        if(rankList.size() > 30){
            rankList.remove(rankList.size() - 1);
        }else {
            if(!isSetIn){
                rankList.add(rankInfo);
            }
        }

        String json = ZSONObject.toZSONString(rankList);
        _obsClient.putObject(
            "huawei-hunter",
            "test.json",
            new ByteArrayInputStream(json.getBytes())
        );
    }

}
