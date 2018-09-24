package znjt.nxld.com.util;

import java.util.Calendar;

/**
 * 时间获取的工具类
 */
public class TimeUtil {
    private static Calendar now = Calendar.getInstance();
    private static  Calendar a = Calendar.getInstance();
    /**
     *获取系统时间当天
     * @return
     */
    public static int getToday(){
        int day =  now.get(Calendar.DAY_OF_MONTH);
        return day;
    }
    /**
     *获取系统时间当年
     * @return
     */
    public static int getYear(){
        int year=now.get(Calendar.YEAR);
        return year;
    }

    /**
     * 获取系统时间当月
     * @return
     */
    public static int getMonth(){
        int month=now.get(Calendar.MONTH) + 1;
        return month;
    }

    /**
     * 取得当月最大天数
     * */
    public static int getCurrentMonthLastDay(int year,int month)
    {
        int day = 1;
        Calendar cal = Calendar.getInstance();
        cal.set(year,month - 1,day);
        return cal.getActualMaximum(Calendar.DATE);
    }
}
