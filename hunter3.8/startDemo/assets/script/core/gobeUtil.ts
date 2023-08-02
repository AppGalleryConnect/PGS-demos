import { Asset, _decorator, log, native, resources, sys, utils} from 'cc';
import {ClientEvent} from '../framework/clientEvent';
import { Constant } from '../framework/constant';
import { FrameInfo, RecvFrameMessage, RecvFromServerInfo, Room, RoomInfo } from '../libs/GOBE';
import { PlayerData } from '../framework/playerData';
import { UIManager } from '../framework/uiManager';
import { AudioManager } from '../framework/audioManager';
import { Util } from '../framework/util';

export enum PLAYER_TYPE{
    READY = 0,
    START = 1,
    END = 2
}

export enum ROOM_TYPE{
    READY = "ready",
    START = "start",
    END = "end"
}

export class MessageInfo{
    public playerId:string = "";

    public msg:string = "";
}

export class GobeUtil {
    private static CLIENT_ID : string = '1029720586034629696';
    private static CLIENT_SECRET : string = '7467F73949AD7D9711C4B9E98D2AB104AFB0B7BC4CA4C6315A0D1A729F5A77570';
    private static APP_ID: string = '243650030996396453';

    private static _instance: GobeUtil = null!;
    private _openId: string = "";
    public get openId(){
        return this._openId;
    }

    private _client:GOBE.Client = null!;

    private _room: GOBE.Room = null;
    public get room(){
        return this._room;
    }

    private _ownPlayer: GOBE.Player = null!;

    private _ownPlayerId:string = "";
    public get ownPlayerId(){
        return this._ownPlayerId;
    }

    private _roomInfos:RoomInfo[] = [];
    public get roomPlayers(){
        if(this._room){
            return this._room.players;
        }
        else{
            return [];
        }
    }

    public static get instance () {
        if (this._instance) {
            return this._instance;
        }

        this._instance = new GobeUtil();
        return this._instance;
    }

    private _cacertNativeUrl: string = "";

    private _lastRoomId:string = null;
    public get lastRoomId (){
        return this._lastRoomId;
    }

    public set lastRoomId (value:any){
        this._lastRoomId = value;
    }

    private _isInitMedia:boolean = false;

    public get isInitMedia (){
        return this._isInitMedia;
    }

    public set isInitMedia (value:boolean){
        this._isInitMedia = value;
    }

    private _isChannelId:boolean = false;
    public get isChannelId (){
        return this._isChannelId;
    }

    public set isChannelId (value:boolean){
        this._isChannelId = value;
    }

    private _msgLst:object[] = [];
    public get msgLst (){
        return this._msgLst;
    }

    private _isStartFS:Boolean = false;

    private _isStartGame:boolean = false;
    private _isOtherStartGame:boolean = false;

    private _time:number = 0;

    public get time(){
        return this._time;
    }
    
    private _isAi:boolean = false;
    public get isAi(){
        return this._isAi;
    }

    // private _hammerIndexs:number[][] = [];

    private _hammerIndex:number  = 0;
    public set hammerIndex(v:number){
        this._hammerIndex = v;
    }

    private _isRoomOwnIn:boolean = false;
    public get isRoomOwnIn(){
        return this._isRoomOwnIn;
    }

    public roomType:ROOM_TYPE = ROOM_TYPE.READY;

    private _isDisJoin:boolean = false;
    public get isDisJoin(){
        return this._isDisJoin;
    }

    private _recvMap:Map<number, FrameInfo[]> = new Map();
    public get recvMap(){
        return this._recvMap;
    }

    private _currFrame:number = 0;
    public get currFrame(){
        return this._currFrame;
    }

    /**
     * 初始化Mgobe
     * @param openId 玩家唯一标示符
     * @param name 玩家昵称
     * @param headUrl 玩家头像
     * @param callback 回调函数
     * @returns 
     */
    public initSDK(openId: string, callback: Function) {
        this._openId = openId;
        this._getToken(callback);
    }

    /**
     * 获得房间列表
     */
    private _updateAvailableRooms(callback:Function){
        let getAvailableRoomsConfig = {
            offset: '0', // 偏移量
            limit: 10, // 单次请求获取的房间数量
            sync: true, // 是否返回帧同步中的房间
        };
        this._client.getAvailableRooms(getAvailableRoomsConfig).then((infos) => {
            // 查询房间列表成功
            this._roomInfos = infos.rooms;
            callback && callback();

           console.log('查询房间列表成功', this._roomInfos);
        }).catch((e) => {
            // 查询房间列表失败
            this._roomInfos = [];

           console.log('查询房间列表失败', e);
        });
    }
    /**
     * 查询
     */
    public updateAvailableRooms(){
        this._updateAvailableRooms(()=>{
            
        });
    }

    /**
     * 检查是否是房主
     */
    public checkIsRoomOwner (id: string) {
        if (!this._room) return false;
        return this._room.ownerId === id;
    }

     /**
     * 检查是否是玩家自己
     */
    public isOwnPlayer(playerId:string){
        return GobeUtil.instance.ownPlayerId == playerId;
    }

    /**
     * 发送帧数据
     * @param info 帧数据
     */
    public sendFrame (info: any) {
        if (!this._room ) return;
        this._room.sendFrame(JSON.stringify(info));
    }

    /**
     * 获取token
     * @param callback 
     */
    private _getToken(callback:Function){
        var url:string = "https://connect-drcn.hispace.hicloud.com/agc/apigw/oauth2/v1/token";
        const xhr = new XMLHttpRequest();
        xhr.open('POST', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json;charset=utf-8');
        xhr.onload = () => {
            if (xhr.status !== 200) {
                return;
            }
            
            var info = JSON.parse(xhr.response);
            this._initMgobe(info["access_token"], callback);
        };

        var data = {};
        data = {
            'client_id' : '1029720586034629696',
            'client_secret' : '7467F73949AD7D9711C4B9E98D2AB104AFB0B7BC4CA4C6315A0D1A729F5A7757',
            'grant_type' : 'client_credentials',
            'useJwt' : 0
        }

        xhr.send(JSON.stringify(data)); 
    }

    /**
     * 获取证书 url
     * @param token 
     * @param callback 
     */
    private _loadCert(token:string, callback:Function){
        resources.load("/endpoint-cert", Asset, (err, asset) => {
            console.log("加载证书结束 " + (!err));
            if (err) {
                return;
            }
            
            this._cacertNativeUrl = asset.nativeUrl;
            this._initMgobe(token, callback);
        });
    }

    /**
     * 初始化Mgobe
     * @param callback 回调函数 
     */
    private _initMgobe(token:string, callback:Function) {
        if(sys.Platform.ANDROID == sys.platform
            || sys.Platform.OHOS == sys.platform){
            if(this._cacertNativeUrl == ""){
                this._loadCert(token, callback);

                return;
            }

            this._client = new GOBE.Client({
                appId: GobeUtil.APP_ID, // 应用ID
                openId: this._openId, // 玩家ID，区别不同用户
                clientId: GobeUtil.CLIENT_ID, // 客户端ID
                clientSecret:  GobeUtil.CLIENT_SECRET, // 客户端密钥
                accessToken: token, // AGC接入凭证(推荐)
                platform: GOBE.PlatformType.CC_ANDROID,
                cerPath: this._cacertNativeUrl
            });
        }else {
            this._client = new GOBE.Client({
                appId: GobeUtil.APP_ID, // 应用ID
                openId: this._openId, // 玩家ID，区别不同用户
                clientId: GobeUtil.CLIENT_ID, // 客户端ID
                clientSecret:  GobeUtil.CLIENT_SECRET, // 客户端密钥
                accessToken: token, // AGC接入凭证(推荐)
            });
        }
        
        this._client.onInitResult((resultCode)=>{
            if(resultCode == 0){
                if(this._lastRoomId){
                    GobeUtil.instance.joinRoom(
                        this._lastRoomId, 
                        ()=>{
                            if(this._room.customRoomProperties == ROOM_TYPE.READY){
                                this.leaveRoom();
                                this._lastRoomId = null;
                            }else{
                                var info:object = JSON.parse(this._room.customRoomProperties);
                                if(info["type"] == ROOM_TYPE.END){
                                    this.leaveRoom();
                                    this._lastRoomId = null;
                                }else{
                                    var time:number = info["time"];
                                    if(time + Constant.GAME_TIME * 1000 > new Date().getTime()){
                                        UIManager.instance.showDialog(Constant.PANEL_NAME.READY, null, ()=>{}, true);
                                        UIManager.instance.showTips(Constant.ROOM_TIPS.JOIN_ROOM_SUCCESS);
                                        UIManager.instance.hideDialog(Constant.PANEL_NAME.JOIN_ROOM_PANEL);
                                        this._isDisJoin = true;
                                    }else{
                                        this.leaveRoom();
                                        this._lastRoomId = null;
                                    }
                                }
                            }

                            console.log("---------------_lastRoomId reconnect success");
                        }, (error:any)=>{
                            console.log("---------------_lastRoomId reconnect fail", error);
                        }
                    );
                }
            }
        });

        // 调用Client.init方法进行初始化
        this._client.init().then((client) => {
            // 已完成初始化请求，具体初始化结果通过onInitResult回调获取
            this._ownPlayerId = client.playerId;
            this._lastRoomId = this._client.lastRoomId;
            // 上次断线的房间ID 先退出
            
            console.log("init playerid", client.playerId);
            callback && callback(true);
        }).catch((err) => {
            // 初始化请求失败，重新初始化或联系华为技术支持
            console.log("调用Client.init方法进行初始化error：" , err);
            callback && callback(false);
        });
    }

    /**
     * 开始游戏
     */
    public startGame(){
        if(this._isDisJoin){
            this._isDisJoin = false;
            ClientEvent.dispatchEvent(Constant.EVENT_NAME.ON_GAME_START);
        }else{
            this._hammerIndex = 0;
            console.log("---------------sendToServer start");
            if(this._room)
                this._room.sendToServer(JSON.stringify({'msg': 'startGame', 'playerId': this._ownPlayerId}));
        }
    }

    /**
     * 创建锤子
     */
    public createHammer(){
        if(this._hammerIndex >= Constant.HAMMER_POS.length){
            return;
        }

        if(this._room){
            this.sendFrame({
                'A' : Constant.ACTION.CREATE_HAMMER, 
                'V' : this._hammerIndex, 
                'X' : Constant.HAMMER_POS[this._hammerIndex][0], 
                'Y' : Constant.HAMMER_POS[this._hammerIndex][1], 
                'Z' : Constant.HAMMER_POS[this._hammerIndex][2]});
        }
    }

    /**
     * 创建金币
     */
    public createCoin(pos:number[][]){
        if(this._room){
            this.sendFrame({
                'A' : Constant.ACTION.CREATE_ICON,
                'I' : JSON.stringify({'coin_pos': pos})
            })
        }
            // this._room.sendToServer(JSON.stringify({'msg': 'create_coin', 'coin_pos': pos}));
    }

    /**
     * 结束游戏
     */
    public finishGame(){
        if(this._room.ownerId == this._ownPlayerId
        || (!this._isRoomOwnIn && this._room.ownerId != this._ownPlayerId)){
            if(this._isStartFS){
                this._isStartFS = false;
                this._room.stopFrameSync();
            }

            console.log("-------finishGame------")
            this._room.updateRoomProperties({
                customRoomProperties : JSON.stringify({"type": ROOM_TYPE.END, "time":0})
            });

            if(!this._isRoomOwnIn){
                ClientEvent.dispatchEvent(Constant.EVENT_NAME.ON_GAME_END);
            }
        }
    }

    /**
     * 房间监听信息
     */
    private _enabledEventRoom(){
        this._isStartGame = false;
        this._isOtherStartGame = false;
        this._room.onJoin((playerInfo) => {
            // 加入房间成功，做相关游戏逻辑处理
            console.log("---------------onJoin 加入", this._room.ownerId, playerInfo.playerId);
            if(this._room.ownerId != playerInfo.playerId){
                ClientEvent.dispatchEvent(Constant.EVENT_NAME.ON_OTHER_JOIN_ROOM, playerInfo.playerId);
                this._otherPlayerId = playerInfo.playerId;
            }

            if(this._room.ownerId == playerInfo.playerId){
                this._isRoomOwnIn = true;
            }

            if(this._room && playerInfo.playerId == this._ownPlayerId
            && this._room.customRoomProperties){
                if(this._room.customRoomProperties == ROOM_TYPE.READY){
                    this.roomType = ROOM_TYPE.READY;
                    this._time = 0;
                }else{
                    var info:object = JSON.parse(this._room.customRoomProperties);
                    this.roomType = info["type"];
                    this._time = info["time"];
                }
            }
        });

        // 加入房间失败
        this._room.onJoinFailed((error) => {
            console.log("---------------onJoinFailed 加入失败", error);
        });

        // 离开房间监听
        this._room.onLeave((player)=>{
               console.log("---------------onLeave 离开", player.playerId);
                if(player.playerId != this._ownPlayerId){
                    this.updateRoom();
                }
                else{
                    this.room.removeAllListeners();
                }

                if(this._room.ownerId == player.playerId){
                    this._isRoomOwnIn = false;
                }
            }
        );

        this._room.onDisconnect((playerInfo) => {
           console.log("---------------Disconnect 掉线", playerInfo.playerId);
            // 当前玩家断线
            if(playerInfo.playerId === this._room.playerId){
                var interval = setInterval(()=>{
                    this._room.reconnect().then(()=>{
                        clearInterval(interval);
                        console.log("---------------reconnect success");
                    }).catch(()=>{
                        console.log("---------------reconnect fail");
                    });
                }, 100);
            }else{
                this.updateRoom();
            }

            if(this._room.ownerId == playerInfo.playerId){
                this._isRoomOwnIn = false;
            }
        });

        this._room.onRoomPropertiesChange((roomInfo:RoomInfo)=>{
            console.log("---------------onRoomPropertiesChange 状态更改", roomInfo.customRoomProperties);
            var info:object = JSON.parse(roomInfo.customRoomProperties);
            this.roomType = info["type"];
            this._time = info["time"];
            if(info["type"] == ROOM_TYPE.START){
                // 游戏开始
                ClientEvent.dispatchEvent(Constant.EVENT_NAME.ON_GAME_START);
            }else if(info["type"] == ROOM_TYPE.END){
                // 游戏结束
                ClientEvent.dispatchEvent(Constant.EVENT_NAME.ON_GAME_END);
            }else if(info["type"] == ROOM_TYPE.READY){
                // 游戏结束
            }

            this.updateRoom();
        });

        this._room.onStartFrameSync(()=>{
            this._isStartFS = true;
            if(!this._isAi){
                this.joinTeamRoom(this._room.ownerId);
                this.joinGroupChannel(this._room.ownerId);
            }
        });

        this._room.onStopFrameSync(()=>{
            console.log("---------------onStopFrameSync结束帧同步");
            this._isStartFS = false;
            this._currFrame = 0;
            this._recvMap = new Map();
            GobeUtil.instance.mediaLeaveRoom();
            GobeUtil.instance.leaveChannel();
            UIManager.instance.hideDialog(Constant.PANEL_NAME.MEDIA_PANEL);
        });

        this._room.onRecvFromServer((recvFromServerInfo: RecvFromServerInfo)=>{
            console.log("---------------onRecvFromServer 服务端数据", recvFromServerInfo.msg);
            var msg:string = recvFromServerInfo.msg;
            var info = JSON.parse(msg);
            if(info["msg"] == 'startGame'){
                if(info["playerId"] != this._ownPlayerId){
                    this._isOtherStartGame = true;
                }else {
                    this._isStartGame = true;
                }

                if(this._room.ownerId == this._ownPlayerId){
                    if((this._isOtherStartGame || this._isAi) && this._isStartGame){
                        this._room.startFrameSync();
                        this._room.updateRoomProperties({
                            customRoomProperties : JSON.stringify({"type": ROOM_TYPE.START, "time":new Date().getTime()})
                        });
                    }
                }
            }else if(info["msg"] == 'create_hammer'){
                ClientEvent.dispatchEvent(Constant.EVENT_NAME.CREATE_HAMMER, info["hammer_id"]);
                this._hammerIndex ++;
            }else if(info["msg"] == 'create_coin'){
                ClientEvent.dispatchEvent(Constant.EVENT_NAME.CREATE_COIN, info["coin_pos"]);
            }
        });

        this._room.onRecvFrame((msg)=>{
            if(msg instanceof Array){
                for(var index:number = 0; index < msg.length; index++){
                    this._time = msg[index].time;
                    if(msg[index].frameInfo){
                        this._recvMap.set(msg[index].currentRoomFrameId, msg[index].frameInfo);
                    }
                }

                this._currFrame = msg[msg.length - 1].currentRoomFrameId;
                ClientEvent.dispatchEvent(Constant.EVENT_NAME.ON_RECV_SYNC);
                console.log("..................1");
            }
            else{
                this._time = msg.time;
                if(msg.frameInfo){
                    this._recvMap.set(msg.currentRoomFrameId, msg.frameInfo);
                }

                this._currFrame = msg.currentRoomFrameId;
            }
        });
    }

    /**
     * 创建房间
     * @param callback 创建房间回调函数
     * @returns 
     */
    public createRoomAI(callback:Function, errorCallback:Function) {
        this._isAi = true;
        console.log("---------------createRoom 创建房间");
        this._client.createRoom({
                maxPlayers: 1
            },{customPlayerStatus: 0, customPlayerProperties: ''}).then((room) => {
                this._room = room;
                this._lastRoomId = room.roomId;
                this._ownPlayer = room.player;
    
                this._room.players.push({
                    playerId : "ai00000",
                    customPlayerProperties : Util.createRandomName(),
                })

                // this._ownPlayer.updateCustomProperties(this.openId);
                this._enabledEventRoom();

                console.log("-------READY------")
                this._room.updateRoomProperties({
                    customRoomProperties : JSON.stringify({"type": ROOM_TYPE.READY, "time":0})
                });
                
                callback && callback();
                console.log("---------------创建房间成功");
            }).catch((e) => {
                errorCallback && errorCallback();

                console.log("---------------创建房间失败 错误", e);
            }
        );
    }

    /**
     * 创建房间
     * @param callback 创建房间回调函数
     * @returns 
     */
    public createRoom(callback:Function, errorCallback:Function) {
        this._isAi = false;
        console.log("---------------createRoom 创建房间");
        this._client.createRoom({
                maxPlayers: 2
            },{customPlayerStatus: 0, customPlayerProperties: PlayerData.instance.playerInfo["playerName"]}).then((room) => {
                this._room = room;
                this._lastRoomId = room.roomId;
                this._ownPlayer = room.player;
                
                // this._ownPlayer.updateCustomProperties(this.openId);
                this._enabledEventRoom();

                console.log("-------READY------")
                this._room.updateRoomProperties({
                    customRoomProperties : JSON.stringify({"type": ROOM_TYPE.READY, "time":0})
                });
                
                callback && callback();
                console.log("---------------创建房间成功");
            }).catch((e) => {
                errorCallback && errorCallback();

                console.log("---------------创建房间失败 错误", e);
            }
        );
    }

    /**
     * 加入房间
     * @param roomId 房间号
     */
    public joinRoom(roomId:string, callback:Function,  errorCallback:Function) {
        this._isAi = false;
        console.log("---------------joinRoom 加入房间");
        this._client.joinRoom(roomId,
            {customPlayerStatus: 0, customPlayerProperties: PlayerData.instance.playerInfo["playerName"]}).then((room) => {
                // 加入房间中
                this._room = room;
                this._ownPlayer = room.player;
                this._lastRoomId = room.roomId;

                // this._ownPlayer.updateCustomProperties(this.openId);
                this._enabledEventRoom();

                console.log("加入房间成功");
                callback && callback();
            }).catch((e) => {
                console.log("申请加入房间 错误", e);
                errorCallback && errorCallback(e);
            }
        );
    }

    /**
     * 开始匹配房间
     */
    public matchRoom(callback:Function, errCallback:Function){
        this._isAi = false;
        console.log("---------------matchRoom 开始匹配房间");
        this._client.matchRoom({
            matchParams: {},
            maxPlayers: 2,
            customRoomProperties: JSON.stringify({"type": ROOM_TYPE.READY, "time":0})
        },{customPlayerStatus: 0, customPlayerProperties: PlayerData.instance.playerInfo["playerName"]}).then((room:Room)=>{
            console.log("---------------matchRoom success");
            this._room = room;
            this._ownPlayer = room.player;
            this._lastRoomId = room.roomId;

            // this._ownPlayer.updateCustomProperties(this.openId);
            this._enabledEventRoom();

            if(this._room.ownerId == this._ownPlayerId){
                console.log("-------matchRoom ready--------");
                this._room.updateRoomProperties({
                    customRoomProperties : JSON.stringify({"type": ROOM_TYPE.READY, "time":0})
                });
            }

            callback && callback();
        }).catch((e)=>{
            errCallback && errCallback();
            console.log("---------------matchRoom error", e)
        });
    }

    /**
     * 更新房间信息
     * 
     * @returns 
     */
    public updateRoom(){
        if(this._room == null){
            return;
        }

        this._room.update().then((room) => {
            this._room = room;

            console.log(room);
        }).catch((e) => {
            // 获取玩家房间最新信息失败
           console.log("更新房间信息 error", e)
        });
    }

    /**
     * 离开房间
     */
    public leaveRoom(callback?:Function, errorCallback?:Function, isLeaveMedia:boolean = true){
        if(this._lastRoomId && this._client){
            this._client.leaveRoom().then((client)=>{
                console.log("离开房间 成功")
                this._client = client;
                this._client.removeAllListeners();
                this._room && this._room.removeAllListeners();
                this._room = null;
                callback && callback();
            }).catch((e)=>{
                errorCallback && errorCallback(e);
    
               console.log("离开房间 error", e)
            });

            if(isLeaveMedia){
                this.leaveChannel();
                this.mediaLeaveRoom();
                UIManager.instance.hideDialog(Constant.PANEL_NAME.MEDIA_PANEL);
            }
        }else{
            callback && callback();
        }
    }

    /**
     * 判断是否初始化
     * @returns 
     */
    public isInited() {
        // 初始化成功后才有玩家ID
        return !!this._ownPlayerId;
    }

    /**
     * 离开游戏
     */
    public leaveGame(){
        if(this._client){
            this._client.destroy();
            this._client = null;
            
            this._room = null;
            this._ownPlayerId = "";

            this.leaveChannel();
            this.mediaLeaveRoom();
            this.destoryMedia();
            UIManager.instance.hideDialog(Constant.PANEL_NAME.MEDIA_PANEL);

            PlayerData.instance.isInit = false;
        }
    }


    private _isOpenMedia:boolean = false;
    public get isOpenMedia(){
        return this._isOpenMedia;
    }

    public set isOpenMedia(value:boolean){
        this._isOpenMedia = value;
    }

    private _isRemoveMedia:boolean = false;
    public get isRemoveMedia(){
        return this._isRemoveMedia;
    }

    public set isRemoveMedia(value:boolean){
        this._isRemoveMedia = value;
    }

    private _isRemoveChannel:boolean = false;
    public get isRemoveChannel(){
        return this._isRemoveChannel;
    }

    public set isRemoveChannel(value:boolean){
        this._isRemoveChannel = value;
    }

    /**
     * 开启 Media
     */
    public startMedia(openId:string){
        if(sys.platform == sys.Platform.ANDROID){
            console.log("time startMedia:" + new Date().getTime());
            native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "startMediaEndine", "(Ljava/lang/String;)V", openId); 
        }
    }

    /**
     * destory Media
     */
    public destoryMedia(){
        if(sys.platform == sys.Platform.ANDROID){
            console.log("time destoryMedia:" + new Date().getTime());
            native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "destoryMediaEndine", "()V"); 
        }
    }

     /**
     * 开启 Media
     */
    public joinTeamRoom(roomId:string){
        if(this.isInitMedia && sys.platform == sys.Platform.ANDROID){
            console.log("time joinTeamRoom:" + new Date().getTime());
            native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "joinTeamRoom", "(Ljava/lang/String;)V", roomId); 
        }
    }

    public mediaLeaveRoom(){
        this._msgLst = [];

        if(sys.platform == sys.Platform.ANDROID){
            var obj:object = PlayerData.instance.getSetting("TeamRoom");
            if(obj){
                var roomId = obj[GobeUtil.instance.openId];
                if(roomId && roomId != ""){
                    console.log("time mediaLeaveRoom:" + new Date().getTime());
                    native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "leaveRoom", "(Ljava/lang/String;)V", roomId); 
                }
            }

            this._isOpenMedia = false;
        }
    }

    public mediaMuteAllPlayers(isOpen:boolean){
        if(sys.platform == sys.Platform.ANDROID){
            var obj:object = PlayerData.instance.getSetting("TeamRoom");
            if(obj){
                var roomId = obj[GobeUtil.instance.openId];
                if(roomId && roomId != ""){
                    console.log("time mediaMuteAllPlayers:" + new Date().getTime());
                    native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "muteAllPlayers", "(Ljava/lang/String;I)V", roomId, isOpen? 0 : 1); 
                }
            }
        }
    }

    public mediaEnableMic(isOpen:boolean){
        if(sys.platform == sys.Platform.ANDROID){
            console.log("time mediaEnableMic:" + new Date().getTime());
            native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "enableMic", "(I)V", isOpen? 0 : 1); 
        }
    }

    public joinGroupChannel(roomId:string){
        if(sys.platform == sys.Platform.ANDROID){
            console.log("time joinGroupChannel:" + new Date().getTime());
            native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "joinGroupChannel", "(Ljava/lang/String;)V", roomId); 
        }
    }

    public leaveChannel(){
        if(sys.platform == sys.Platform.ANDROID){
            var obj:object = PlayerData.instance.getSetting("JoinChannel");
            if(obj){
                var roomId = obj[GobeUtil.instance.openId];
                if(roomId && roomId != ""){
                    console.log("time leaveChannel:" + new Date().getTime());
                    native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "leaveChannel", "(Ljava/lang/String;)V", roomId); 
                }
            }
        }
    }

    public sendTextMsg(msg:string){
        if(sys.platform == sys.Platform.ANDROID){
            var obj:object = PlayerData.instance.getSetting("JoinChannel");
            var roomId = obj[GobeUtil.instance.openId];
            if(roomId && roomId != ""){
                console.log("time sendTextMsg:" + new Date().getTime());
                native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "sendTextMsg", "(Ljava/lang/String;Ljava/lang/String;)V", roomId, msg); 
            }
        }
    }

    public startRecordAudioToText(){
        if(sys.platform == sys.Platform.ANDROID){
            console.log("time startRecordAudioToText:" + new Date().getTime());
            native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "startRecordAudioToText", "()V"); 
        }
    }
    
    public stopRecordAudioToText(){
        if(sys.platform == sys.Platform.ANDROID){
            console.log("time stopRecordAudioToText:" + new Date().getTime());
            native.reflection.callStaticMethod('com/cocos/game/MediaEngine', "stopRecordAudioToText", "()V"); 
        }
    }

    public startForumPage(){
        if(sys.platform == sys.Platform.ANDROID){
            console.log("time startForumPage:" + new Date().getTime());
            native.reflection.callStaticMethod('com/cocos/game/ForumPage', "startPgs", "()V"); 
        }
    }

    /**
     * 开启 Media
     */
    public openForumPage(){
        if(sys.platform == sys.Platform.ANDROID){
            console.log("time openForumPage:" + new Date().getTime());
            native.reflection.callStaticMethod('com/cocos/game/ForumPage', "openForumPage", "()V"); 
        }
    }

    /**
     * 检查media是否上次掉线未关闭
     * @returns 
     */
    public checkInitMedia(){
        var obj:object = PlayerData.instance.getSetting("TeamRoom");
        if(obj && obj[GobeUtil.instance.openId]){
            var roomId = obj[GobeUtil.instance.openId];
            if(roomId != '' && roomId != Constant.WORLD_ID){
                GobeUtil.instance.isRemoveMedia = true;
                GobeUtil.instance.joinTeamRoom(roomId);

                return;
            }
        }

        ClientEvent.dispatchEvent(Constant.EVENT_NAME.INIT_MEDIA);
    }

    /**
     * 检查channel是否上次掉线未关闭
     * @returns 
     */
    public checkInitChannel(){
        var obj:object = PlayerData.instance.getSetting("JoinChannel");
        if(obj && obj[GobeUtil.instance.openId]){
            var roomId = obj[GobeUtil.instance.openId];
            if(roomId != '' && roomId != Constant.WORLD_ID){
                GobeUtil.instance.joinGroupChannel(roomId);
                GobeUtil.instance.isRemoveChannel = true;

                return;
            }
        }

        ClientEvent.dispatchEvent(Constant.EVENT_NAME.INIT_CHANNEL);
    }

    /**
     * 初始化obs
     */
    public startObs(){
        if(sys.platform == sys.Platform.OHOS){
            console.log("time startMedia:" + new Date().getTime());
            native.reflection.callStaticMethod('com/example/cocosdemo/ObsControl', "initObs", "()V"); 
        }
    }

    /**
     * 上传排行榜
     */
    public inputRank(){
        if(sys.platform == sys.Platform.OHOS){
            var json:string = JSON.stringify(PlayerData.instance.playerInfo);
            native.reflection.callStaticMethod('com/example/cocosdemo/ObsControl', "putRanks", "(Ljava/lang/String;)V", json); 
        }
    }

}

// 语音识别
window["callbackToGMMCreate"] = (code:number, msg:string) => {
    console.log("time callbackToGMMCreate:" + new Date().getTime());
    if(code == 0){
        GobeUtil.instance.isInitMedia = true;
        GobeUtil.instance.checkInitMedia();
        GobeUtil.instance.checkInitChannel();
    }

    console.log("-------callbackToGMMCreate", code, msg);
}

window["callbackToGmmJoin"] = (code:number, roomId:string, msg:string) =>{
    console.log("time callbackToGmmJoin:" + new Date().getTime());
    if(code == 0){
        var obj:object = PlayerData.instance.getSetting("TeamRoom");
        if(!obj){
            obj = {};
        }
        obj[GobeUtil.instance.openId] = roomId;
        PlayerData.instance.setSetting("TeamRoom", obj);

        GobeUtil.instance.isOpenMedia = true;
        GobeUtil.instance.mediaEnableMic(false);
        GobeUtil.instance.mediaMuteAllPlayers(false);

        if(GobeUtil.instance.isRemoveMedia){
            GobeUtil.instance.mediaLeaveRoom();

            return;
        }
    }else{
        UIManager.instance.showTips("实时语音开启异常：" + code);
    }

    console.log("------callbackToGmmJoin", code, msg);
    ClientEvent.dispatchEvent(Constant.EVENT_NAME.OPEN_MEDIA);
}

window["callbackToGmmMic"] = (roomId:number, openId:string, isMute:boolean) =>{
    console.log("time callbackToGmmMic:" + new Date().getTime());
    console.log("callbackToGmmMic", roomId, openId, isMute);
}

window["callbackToGmmLeave"] = (code:number, roomId:string, msg:string) =>{
    console.log("time callbackToGmmLeave:" + new Date().getTime());
    if(code == 0){
        var obj:object = PlayerData.instance.getSetting("TeamRoom");
        obj[GobeUtil.instance.openId] = "";
        PlayerData.instance.setSetting("TeamRoom", obj);

        // UIManager.instance.showTips("语音关闭");

        if(GobeUtil.instance.isRemoveMedia){
            GobeUtil.instance.isRemoveMedia = false;
            ClientEvent.dispatchEvent(Constant.EVENT_NAME.INIT_MEDIA);
            return;
        }
    }

    console.log("---------callbackToGmmLeave", code, msg);
}

window["callbackToJoinChannel"] = (code:number, roomId:string, msg:string) =>{
    console.log("time callbackToJoinChannel:" + new Date().getTime());
    if(code == 0){
        var obj:object = PlayerData.instance.getSetting("JoinChannel");
        if(!obj){
            obj = {};
        }

        obj[GobeUtil.instance.openId] = roomId;
        PlayerData.instance.setSetting("JoinChannel", obj);

        GobeUtil.instance.isChannelId = true;

        if(GobeUtil.instance.isRemoveChannel){
            GobeUtil.instance.leaveChannel();
            return;
        }
    }else{
        UIManager.instance.showTips("文本语音开启异常：" + code);
    }

    console.log("---------callbackToJoinChannel", code, msg);
    ClientEvent.dispatchEvent(Constant.EVENT_NAME.OPEN_CHANNEL);
}

window["callbackToLeaveChannel"] = (code:number, roomId:string, msg:string) =>{
    console.log("time callbackToLeaveChannel:" + new Date().getTime());
    if(code == 0){
        var obj:object = PlayerData.instance.getSetting("JoinChannel");
        obj[GobeUtil.instance.openId] = "";
        PlayerData.instance.setSetting("JoinChannel", obj);

        GobeUtil.instance.isChannelId = false;

        if(GobeUtil.instance.isRemoveChannel){
            GobeUtil.instance.isRemoveChannel = false;
            ClientEvent.dispatchEvent(Constant.EVENT_NAME.INIT_CHANNEL);
        }
    }

    console.log("---------callbackToLeaveChannel", code, msg);
}

window["callbackToSendMsg"] = (content:string, sendId:string) =>{
    console.log("time callbackToSendMsg:" + new Date().getTime());
    GobeUtil.instance.msgLst.push({isOwn:true, content:content, sendId:sendId.substring(5)});
    ClientEvent.dispatchEvent(Constant.EVENT_NAME.SEND_MSG);
}

window["callbackToRecvMsg"] = (content:string, sendId:string) =>{
    console.log("time callbackToRecvMsg:" + new Date().getTime());
    GobeUtil.instance.msgLst.push({isOwn:false, content:content, sendId:sendId.substring(5)});
    ClientEvent.dispatchEvent(Constant.EVENT_NAME.SEND_MSG);

    console.log("callbackToRecvMsg", content);
}

window["callbackToVT"] = (code:number, msg:string) =>{
    console.log("time callbackToVT:" + new Date().getTime());
    if(code == 0){
        ClientEvent.dispatchEvent(Constant.EVENT_NAME.SEND_VT, msg);
    }else{
        ClientEvent.dispatchEvent(Constant.EVENT_NAME.SEND_VT, "");
    }

    console.log("callbackToVT", msg);
}

window["callbackToNoPer"] = () =>{
    console.log("time callbackToNoPer:" + new Date().getTime());
    UIManager.instance.showTips(Constant.ROOM_TIPS.MEDIA_FAIL);
}

window["callbackObsSuccess"] = () =>{
    
}

