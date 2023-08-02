import { _decorator, Component, Prefab } from 'cc';
import { PlayerData } from '../framework/playerData';
import { AudioManager } from '../framework/audioManager';
import { GobeUtil } from '../core/gobeUtil';
import { UIManager } from '../framework/uiManager';
import { Constant } from '../framework/constant';
const { ccclass, property } = _decorator;

@ccclass('Select')
export class Select extends Component {
    @property([Prefab])
    public backPrefabs: Prefab[] = [];

    start() {
        if(PlayerData.instance.isInit == false){
            GobeUtil.instance.startObs();
            AudioManager.instance.init();
            PlayerData.instance.loadFromCache();
        }
        
        var ownPlayerId:string = GobeUtil.instance.ownPlayerId;
        if(ownPlayerId == ""){
            UIManager.instance.showDialog(Constant.PANEL_NAME.START_GAME);
        }else{
            UIManager.instance.showDialog(Constant.PANEL_NAME.SELECT_GAME);
        }
    }
}

