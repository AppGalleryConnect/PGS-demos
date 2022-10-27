const { ccclass, property } = cc._decorator;

@ccclass
export default class BoomerangPool extends cc.Component {
  @property(cc.Integer)
  public contentCount = 10;

  @property(cc.Prefab)
  public boomerangPrefab: cc.Prefab = null;

  boomerangPool: cc.NodePool;

  start() {
    this.boomerangPool = new cc.NodePool();
    for (let index = 0; index < this.contentCount; index++) {
      const boomerang = cc.instantiate(this.boomerangPrefab);
      boomerang.getComponent('Boomerang').boomerangPool = this;
      this.boomerangPool.put(boomerang);
    }
  }

  // 获取对象
  createBoomerang() {
    let boomerang = null;
    if (this.boomerangPool.size() > 0) {
      boomerang = this.boomerangPool.get();
    } else {
      boomerang = cc.instantiate(this.boomerangPrefab);
    }
    return boomerang;
  }

  // 返回对象
  freeBoomerang(boomerang) {
    this.boomerangPool.put(boomerang);
  }
}
