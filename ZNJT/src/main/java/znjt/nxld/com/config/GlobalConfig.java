package znjt.nxld.com.config;

/**
 * Created by zhengzhihua on 2018/8/31.14:09
 * Update 2018/8/31 14:09
 * Describe  接口配置类
 *       全局变量
 */

public final class GlobalConfig {
    /**
     * 方法后缀
     */
    public final static String LAST_WORD="";
    /**
     * 服务器
     */
//    public final static String SERVER="http://10.10.10.122:8081";
    public final static String SERVER="http://192.168.64.4:8888";
    /**
     * 服务器的地址
     */
    public final static String SERVER_ADDRESS="http://192.168.64.4:8888/znjt_Consumer";
   // public final static String SERVER_ADDRESS="http://10.10.10.122:8081/znjt_Consumer";
    /**
     * 接口地址
     */
   public final static String URL_URL="http://192.168.64.4:8888/znjt_Consumer";
 //  public final static String URL_URL="http://10.10.10.122:8081/znjt_Consumer";
  //  public final static String URL_URL="http://10.10.10.102:8081/znjt_Consumer";
    /**
     *登录
     */
    public final static String LOGIN_URL=URL_URL+"/mobileLogin";
    /**
     *验证用户是否存在接口
     */
    public final static String VERIFICATION_PHOTO_URL=URL_URL+"/mobileSMS";
    /**
     *注册接口
     */
    public final static String REGIST_URL=URL_URL+"/mobilePassword";
    /**
     *验证码是否正确接口
     */
    public final static String YZM_URL=URL_URL+"/mobileRegister";
    /**
    *  获取用户信息
    */
    public final static String GETUSERMESSAGE=URL_URL+"/lookPsersonMessage";
    /**
     *完善信息接口
     */
    public final static String WRITEINFORMATION_URL=URL_URL+"/insertPsersonMessage";
    /**
     *提交身份证照片
     */
    public final static String GETPHOTO_URL=URL_URL+"/mobileIDCard";
    /**
     * 获取报警详情findBindCar
     */
    public final static String ALARMDETAIL=URL_URL+"/mobileInformationForm";
    /**
     * 查询绑定车辆
     */
    public final static String FINDBINDCAR=URL_URL+"/findBindCar";
    /**
     * 解除绑定车辆
     */
    public final static String UNITECAR=URL_URL+"/uniteCar";
    /**
     * 绑定车辆
     */
    public final static String BINDCAR=URL_URL+"/bindCar";
}
