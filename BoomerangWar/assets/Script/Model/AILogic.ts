import Game from '../Scenes/Game';
import Player from './Player';

const { ccclass, property } = cc._decorator;

@ccclass
export default class AILogic extends cc.Component {
  @property(cc.Boolean)
  AISwitch = true;

  @property(cc.Float)
  fireDuration = 3;

  @property(cc.Float)
  jumpDuration = 5;

  private player: Player;

  jumpPassTime = 0;

  firePassTime = 0;

  start() {
    this.player = this.node.getComponent('Player');
  }

  update(dt) {
    if (Game.isGameOver) {
      this.AISwitch = false;
      return;
    }

    this.jumpPassTime += dt;
    this.firePassTime += dt;
    if (this.AISwitch && this.jumpPassTime >= this.jumpDuration) {
      this.player.jump();
      this.jumpPassTime = 0;
    }
    if (this.AISwitch && this.firePassTime >= this.fireDuration) {
      this.player.fire();
      this.firePassTime = 0;
    }
  }
}
