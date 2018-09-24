package znjt.nxld.com.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import znjt.nxld.com.R;
import znjt.nxld.com.bean.BindCar;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;

/**
 * Created by zhengzhihua on 2018/9/13.10:14
 * Update 2018/9/13 10:14
 * Describe   解绑设备车辆
 */

public class EquipmentDetail_Activity extends AppCompatActivity{

    private BindCar bindCar;
    private TextView carview,equipview;
    private Handler handler=new Handler(){
        public void handleMessage(Message mes){
            switch (mes.what){
                case 1:
                    String message= (String) mes.obj;
                    Toast.makeText(EquipmentDetail_Activity.this,message, Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 2:
                    String messages= (String) mes.obj;
                    Toast.makeText(EquipmentDetail_Activity.this,messages,Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindCar= (BindCar) getIntent().getSerializableExtra("bindcar");
        setContentView(R.layout.equipmentdetail_layout);
        findviewbyid();

    }

    protected void findviewbyid(){
        carview=findViewById(R.id.car2_text);
        equipview=findViewById(R.id.equips2_text);
        Toolbar toolbar=findViewById(R.id.equip_toolbar);
        setSupportActionBar(toolbar);
        Button button=findViewById(R.id.buttons1_text);
        binddata();
        ImageView imageView=findViewById(R.id.normal1_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EquipmentDetail_Activity.this);

            //    builder.setIcon(R.drawable.adress);
                builder.setTitle("解绑");
                builder.setMessage("确定解绑吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                binddata();
                                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(EquipmentDetail_Activity.this);
                                String name=sharedPreferences.getString("name","");
                                JSONObject jsonObject=new JSONObject();
                                try {
                                    jsonObject.put("phoneNum",name);
                                    jsonObject.put("carNo",bindCar.getCarNo());
                                    jsonObject.put("deviceId",bindCar.getDeviceId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Httputil.Okhttppost(GlobalConfig.UNITECAR, jsonObject.toString(), new BackHander() {
                                            @Override
                                            public void onexception(Exception e) {
                                                Message messages=handler.obtainMessage();
                                                messages.obj="请求超时...";
                                                messages.what=2;
                                                handler.sendMessage(messages);
                                            }

                                            @Override
                                            public void onFail(Object response) {
                                                Message messages=handler.obtainMessage();
                                                messages.obj="解绑失败";
                                                messages.what=2;
                                                handler.sendMessage(messages);
                                            }

                                            @Override
                                            public void onsuccess(Object response) {
                                                try {
                                                    JSONObject jsonObject1=new JSONObject(response.toString());
                                                    JSONObject aa=(JSONObject) jsonObject1.get("body");
                                                    JSONObject data= (JSONObject) aa.get("data");
                                                    boolean is= (boolean) data.get("issuccess");
                                                    if(is){
                                                        Message messages=handler.obtainMessage();
                                                        messages.obj=data.get("message");
                                                        messages.what=1;
                                                        handler.sendMessage(messages);
                                                    }else{
                                                        Message messages=handler.obtainMessage();
                                                        messages.obj=data.get("message");
                                                        messages.what=2;
                                                        handler.sendMessage(messages);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Message messages=handler.obtainMessage();
                                                    messages.obj="出错";
                                                    messages.what=2;
                                                    handler.sendMessage(messages);
                                                }
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

    }

    protected void binddata(){
        carview.setText(bindCar.getCarNo());
        equipview.setText(bindCar.getDeviceId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
