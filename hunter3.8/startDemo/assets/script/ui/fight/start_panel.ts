import { _decorator, Node, Component, EditBox, Label, Animation, sp, sys, native, JsonAsset, randomRange } from 'cc';
import { UIManager } from '../../framework/uiManager';
import { Constant } from '../../framework/constant';
import { GobeUtil } from '../../core/gobeUtil';
import { PlayerData } from '../../framework/playerData';
import { Util } from '../../framework/util';
import { ResourceUtil } from '../../framework/resourceUtil';
const { ccclass, property } = _decorator;

@ccclass('StartPanel')
export class StartPanel extends Component {
    
    @property(sp.Skeleton)
    startSk:sp.Skeleton = null!;

    @property(EditBox)
    editBox:EditBox = null!;

    @property(Node)
    btnNode:Node = null!;

    @property(Animation)
    loadAni:Animation = null!;

    @property(Label)
    txtId:Label = null!;

    private _isClick:boolean = false;

    show(){
        this.loadAni.node.active = false;
        this.loadAni.stop();

        this.startSk.setAnimation(0, 'start', false);
        this.startSk.addAnimation(0, 'idle', true);

        this.editBox.string = "";
        this.editBox.node.active = false;
        this.btnNode.active = false;

        var playerId:string = PlayerData.instance.playerInfo['playerId'];
        if(playerId == null){
            playerId = "cocos" + (new Date().getTime()).toString().substring(6);
            var staticId:number = Math.floor(Math.random() * 2);
            PlayerData.instance.createPlayerInfo({
                'playerId': playerId, 
                "playerName": "", 
                "score": 0, 
                "icon": "coin"+staticId+".png",
                "staticId": staticId
            });
        }

        this.txtId.string = playerId;
        setTimeout(()=>{
            // this.txtId.node.parent.active = true;
            // this.editBox.node.active = true;
            this.btnNode.active = true;
        }, 1500);
    }

    onInputEdit(){
        var msg:string = this.editBox.string;
        msg = msg.replace(/[^\a-\z\A-\Z0-9]/g,'');

        this.editBox.string = msg;
    }

    /**
     * 开始游戏
     * 
     * @returns 
     */
    public onStartGame(){
        // if( this.editBox.string.length < 2){
        //     UIManager.instance.showTips(Constant.ROOM_TIPS.PLAYER_LENGHT_ERROR);
        //     return;
        // }

        // if(this.editBox.string == ""){
        //     UIManager.instance.showTips("请输入您的账户");
        //     return;
        // }

        if(this._isClick){
            return;
        }

        this._isClick = true;

        this.loadAni.node.active = true;
        this.loadAni.play();

        // 登录
        GobeUtil.instance.initSDK(this.txtId.string, (successInit:boolean)=>{
            if(successInit){
                UIManager.instance.showDialog(Constant.PANEL_NAME.SELECT_GAME);
                UIManager.instance.hideDialog(Constant.PANEL_NAME.START_GAME);
            }

            this._isClick = false;
            this.loadAni.node.active = false;
            this.loadAni.stop();
        });
    }
}

