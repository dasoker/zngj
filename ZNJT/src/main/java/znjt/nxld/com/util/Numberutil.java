package znjt.nxld.com.util;

/**
 * Created by zhengzhihua on 2018/9/3.17:18
 * Update 2018/9/3 17:18
 * Describe
 */

public class Numberutil {
    /**
    *  @Description 手机号识别是否为手机号格式
    *  @param:
    *  @return
    */
    public static boolean isValidMobiNumber(String paramString) {
        String regex = "^[1][3,4,5,6,7,8][0-9]{9}$";
        if (paramString.matches(regex)) {
            return true;
        }else{
            return false;
        }
    }
    /**
    *  @Description 判断密码格式是否正确
    *  @param:
    *  @return
    */
    public static boolean isPassWord(String password){
        String regex = "^[A-Za-z0-9]{6,16}$";
        if(password.matches(regex)){
            return true;
        }else{
            return false;
        }
    }
    /**
    *  @Description 判断验证码格式是否正确
    *  @param:
    *  @return
    */
    public static boolean isYzm(String yzm){
        String regex = "^[0-9]{6}$";
        if(yzm.matches(regex)){
            return true;
        }else{
            return false;
        }
    }
    /**
    *  @Description 判断名字是否符合标准
    *  @param:
    *  @return
    */
    public static boolean isName(String name){
        String regex="^[\\u4e00-\\u9fa5]{2,4}$";
        if(name.matches(regex)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 身份证号格式是否正确
     * @param id
     * @return
     */
    public static boolean isIDcardid(String id){
        String regex="(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$)";
        if(id.matches(regex)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 车牌号格式是否正确
     * @param carno
     * @return
     */
    public static boolean isCarNo(String carno){
        String regex="^[\\u4e00-\\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$";
        if(carno.matches(regex)){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isEquipNo(String equipno){
        String regex="^[0-9]{9}$";
        if(equipno.matches(regex)){
            return true;
        }else {
            return false;
        }
    }
}
