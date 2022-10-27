import Global from '../Global';
import Player from '../Model/Player';

const { ccclass, property } = cc._decorator;

@ccclass
export default class Game extends cc.Component {
  @property(cc.Node)
  playerNode1: cc.Node = null;

  @property(cc.Node)
  playerNode2: cc.Node = null;

  @property(cc.Node)
  gameOverNode: cc.Node = null;

  @property(cc.Node)
  winnerNode: cc.Node = null;

  @property(cc.Node)
  loserNode: cc.Node = null;

  @property(cc.Label)
  ID_value: cc.Label = null;

  @property(cc.Label)
  NickName_value: cc.Label = null;

  static isGameOver = false;

  private player1: Player;

  private player2: Player;

  onLoad() {
    cc.director.getPhysicsManager().enabled = true;
    cc.director.getPhysicsManager().gravity = cc.v2(0, -320);
  }

  start() {
    this.player2 = this.playerNode2.getComponent('Player');
    this.player1 = this.playerNode1.getComponent('Player');

    this.player1.gameResCallback = this.gameResultCallback.bind(this);
    this.player2.gameResCallback = this.gameResultCallback.bind(this);
  }

  /**
   *
   * @param winnerId p1:1,p2:2
   */
  gameResultCallback(winnerId) {
    Game.isGameOver = true;
    this.ID_value.string = Global.openId;
    this.NickName_value.string = Global.nickName;
    if (winnerId === 1) {
      this.gameOverNode.active = true;
      this.winnerNode.active = true;
      this.loserNode.active = false;
    } else {
      this.gameOverNode.active = true;
      this.winnerNode.active = false;
      this.loserNode.active = true;
    }
  }

  onDestroy() {
    // 此处需要销毁广告监听
  }
}
