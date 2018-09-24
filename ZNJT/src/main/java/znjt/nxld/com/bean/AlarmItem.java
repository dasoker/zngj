package znjt.nxld.com.bean;

/**
*@Author:dingkuirong
*@date:2018/9/4 11:55
*@Description:报警模型
*/

public class AlarmItem {
    private String type;//类型
    private String startTime;//开始时间
    private String endTime;//结束时间
    private String speed;//速度
    private String carNum;

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    private String id;//id标识

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
