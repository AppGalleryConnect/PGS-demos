const { ccclass } = cc._decorator;

@ccclass
export default class Global {
  public static openId = String(Date.now()); // 未登录状态使用当前时间戳作为openId
  public static playerId: string = null;
  public static nickName = '未登录';
  public static playerIconUrl = '';
  public static gameWin = false;

}
