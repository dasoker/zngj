package znjt.nxld.com.listener;

/**
 * Created by zhengzhihua on 2018/8/31.13:39
 * Update 2018/8/31 13:39
 * Describe
 */

public interface BackHander<T> {

    public void onexception(Exception e);
    public void onFail(T response);
    public void onsuccess(T response);
}
