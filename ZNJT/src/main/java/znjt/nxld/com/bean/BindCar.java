package znjt.nxld.com.bean;

import java.io.Serializable;

/**
 * Created by zhengzhihua on 2018/9/13.15:35
 * Update 2018/9/13 15:35
 * Describe
 */

public class BindCar implements Serializable {
    private String carNo;     //车牌号
    private String deviceId;   //设备号

    public BindCar(String carNo,String deviceId){
        this.carNo=carNo;
        this.deviceId=deviceId;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
