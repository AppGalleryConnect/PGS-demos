const { ccclass, property } = cc._decorator;

@ccclass
export default class Player extends cc.Component {
  @property(cc.Prefab)
  BoomerangPrefab: cc.Prefab = null;

  @property(cc.Float)
  fireDuration = 1;

  // 玩家落地状态
  isStand = false;

  // 玩家可射击状态
  isAvailableFire = true;

  firePassTime = 0;

  rBody: cc.RigidBody;

  // 游戏结果回调
  gameResCallback: Function;

  onLoad() {
    this.rBody = this.node.getComponent(cc.RigidBody);
  }

  protected update(dt: number): void {
    !this.isAvailableFire && (this.firePassTime += dt);
    if (this.firePassTime >= this.fireDuration) {
      this.isAvailableFire = true;
      this.firePassTime = 0;
    }
  }

  jump() {
    if (!this.isStand) {
      return;
    }
    this.rBody.applyForceToCenter(new cc.Vec2(0, 320 * 8000), true);
    this.isStand = false;
  }

  fire() {
    if (!this.isAvailableFire) {
      return;
    }
    const boomerang = this.node.parent.getComponent('BoomerangPool').createBoomerang();
    boomerang.setParent(this.node);
    boomerang.getComponent('Boomerang').init();
    this.isAvailableFire = false;
  }

  // 碰撞检测
  onBeginContact(contact: cc.PhysicsContact, selfCollider: cc.PhysicsCollider, otherCollider: cc.PhysicsCollider) {
    // 只有飞镖碰到玩家才进行结果认定
    if (otherCollider.tag === 0) {
      cc.log(this.node.name + '：被击中了');

      // 此处进行游戏结果认定
      if (this.node.name === 'p2') {
        // p2被击中，p1胜，单机模式，玩家默认就是p1
        this.gameResCallback(1);
      } else {
        this.gameResCallback(2);
      }
      this.node.destroy();
    }

    // 落地，大地tag为100
    if (otherCollider.tag === 100) {
      this.isStand = true;
    }
  }
}
