const { ccclass } = cc._decorator;

@ccclass
export default class Start extends cc.Component {

  start() {
      
  }

  goToGame() {
    cc.director.loadScene('Game');
  }
}
