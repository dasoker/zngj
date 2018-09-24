package znjt.nxld.com.util;

/**
*@Author:dingkuirong
*@date:2018/9/6 12:01
*@Description:判断密码强弱工具类
*/

public class PasswordUntil {
    /**
     * 判断密码强弱
     * @param str
     * @return
     */
    public static int password(String str){
        //输入框为0
        if (str.length() == 0)
        {
           return 0;
        }
        //输入的纯数字为弱
        if (str.matches ("^[0-9]+$"))
        {
          return 1;
        }
        //输入的纯小写字母为弱
        else if (str.matches ("^[a-z]+$"))
        {
            return 1;
        }
        //输入的纯大写字母为弱
        else if (str.matches ("^[A-Z]+$"))
        {
            return 1;
        }
        //输入的大写字母和数字，输入的字符小于7个密码为弱
        else if (str.matches ("^[A-Z0-9]{1,5}"))
        {
            return 1;
        }
        //输入的大写字母和数字，输入的字符大于7个密码为中
        else if (str.matches ("^[A-Z0-9]{6,16}"))
        {
            return 2;
        }
        //输入的小写字母和数字，输入的字符小于7个密码为弱
        else if (str.matches ("^[a-z0-9]{1,5}"))
        {
            return 1;
        }
        //输入的小写字母和数字，输入的字符大于7个密码为中
        else if (str.matches ("^[a-z0-9]{6,16}"))
        {
            return 2;
        }
        //输入的大写字母和小写字母，输入的字符小于7个密码为弱
        else if (str.matches ("^[A-Za-z]{1,5}"))
        {
            return 1;
        }
        //输入的大写字母和小写字母，输入的字符大于7个密码为中
        else if (str.matches ("^[A-Za-z]{6,16}"))
        {
            return 2;
        }
        //输入的大写字母和小写字母和数字，输入的字符小于5个个密码为弱
        else if (str.matches ("^[A-Za-z0-9]{1,5}"))
        {
            return 1;
        }
        //输入的大写字母和小写字母和数字，输入的字符大于6个个密码为中
        else if (str.matches ("^[A-Za-z0-9]{6,8}"))
        {
            return 2;
        }
        //输入的大写字母和小写字母和数字，输入的字符大于8个密码为强
        else if (str.matches ("^[A-Za-z0-9]{9,16}"))
        {
            return 3;
        }else if(str.matches ("^([A-Z]{1}|@|_|.|#){1,5}")){//弱
            return 1;
        }else if(str.matches ("^([a-z]{1}|@|_|.|#){1,5}")) {//弱
            return 1;
        }else if(str.matches ("^([0-9]{1}|@|_|.|#){1,5}")) {//弱
            return 1;
        }else if(str.matches ("^([A-Z0-9]{1}|@|_|.|#){1,5}")) {//弱
            return 1;
        }else if(str.matches ("^([a-z0-9]{1}|@|_|.|#){1,5}")) {//弱
            return 1;
        }else if(str.matches ("^([a-zA-Z]{1}|@|_|.|#){1,5}")) {//弱
            return 1;
        }else if(str.matches ("^([a-zA-Z0-9]{1}|@|_|.|#){1,5}")) {//弱
            return 1;
        }else if(str.matches ("^([a-zA-Z0-9]{1}|@|_|.|#){6,8}")) {//中
            return 2;
        }else if(str.matches ("^([a-zA-Z0-9]{1}|@|_|.|#){9,15}")) {//强
            return 3;
        }else if(str.matches ("^([A-Z]{1}|@|_|.|#){6,8}")){//中
            return 2;
        }else if(str.matches ("^([a-z]{1}|@|_|.|#){6,8}")) {//中
            return 2;
        }else if(str.matches ("^([0-9]{1}|@|_|.|#){6,8}")) {//中
            return 2;
        }else if(str.matches ("^([A-Z]{1}|@|_|.|#){9,15}")){//中
            return 3;
        }else if(str.matches ("^([a-z]{1}|@|_|.|#){9,15}")) {//中
            return 3;
        }else if(str.matches ("^([0-9]{1}|@|_|.|#){9,15}")) {//中
            return 3;
        }else if(str.matches ("^([A-Z0-9]{1}|@|_|.|#){6,8}")) {//中
            return 2;
        }else if(str.matches ("^([a-z0-9]{1}|@|_|.|#){6,8}")) {//中
            return 2;
        }else if(str.matches ("^([a-zA-Z]{1}|@|_|.|#){6,8}")) {//中
            return 2;
        }else if(str.matches ("^([A-Z0-9]{1}|@|_|.|#){9,15}")) {//强
            return 3;
        }else if(str.matches ("^([a-z0-9]{1}|@|_|.|#){9,15}")) {//强
            return 3;
        }else if(str.matches ("^([a-zA-Z]{1}|@|_|.|#){9,15}")) {//强
            return 3;
        }
        return -1;
    }
}
