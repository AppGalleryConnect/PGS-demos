import { _decorator, Component, director, game, Prefab } from 'cc';
import { PlayerData } from '../framework/playerData';
import { AudioManager } from '../framework/audioManager';
import { GobeUtil } from '../core/gobeUtil';
import { UIManager } from '../framework/uiManager';
import { Constant } from '../framework/constant';
import { Util } from '../framework/util';
const { ccclass, property } = _decorator;

@ccclass('Select')
export class Select extends Component {
    @property([Prefab])
    public backPrefabs: Prefab[] = [];

    start() {
        if(PlayerData.instance.isInit == false){
            game.frameRate = 30;
            AudioManager.instance.init();
            PlayerData.instance.loadFromCache();

            var playerId:string = PlayerData.instance.playerInfo['playerId'];
            if(playerId == null){
                playerId = "cocos" + (new Date().getTime()).toString().substring(6);
                var staticId:number = Math.floor(Math.random() * 2);
                PlayerData.instance.createPlayerInfo({
                    'playerId': playerId, 
                    "playerName": "", 
                    "score": 0, 
                    "icon": Math.floor(Math.random() * 10),
                    "staticId": staticId
                });
    
                Util.randomName(staticId).then((playerName)=>{
                    PlayerData.instance.updatePlayerInfo("playerName", playerName);
                })
            }

            director.preloadScene(Constant.SCENE_NAME.SLECT, () => {
                // this._loadSceneOver = true;
                // this._transitionOut();
            });
        }
        
        var ownPlayerId:string = GobeUtil.instance.ownPlayerId;
        if(ownPlayerId == ""){
            UIManager.instance.showDialog(Constant.PANEL_NAME.START_GAME);
        }else{
            UIManager.instance.showDialog(Constant.PANEL_NAME.SELECT_GAME);
        }
    }
}

