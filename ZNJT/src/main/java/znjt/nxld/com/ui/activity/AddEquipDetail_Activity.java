package znjt.nxld.com.ui.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import znjt.nxld.com.R;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.listener.EditChangedListener;
import znjt.nxld.com.util.Numberutil;

/**
 * Created by zhengzhihua on 2018/9/13.10:53
 * Update 2018/9/13 10:53
 * Describe  绑定设备车辆
 */

public class AddEquipDetail_Activity extends AppCompatActivity {

    private EditText carTV,equipTV;
    private String carid,equipid;
    private Handler handler=new Handler(){
        public void handleMessage(Message mes){
            switch (mes.what){
                case 1:
                    String message= (String) mes.obj;
                    Toast.makeText(AddEquipDetail_Activity.this,message, Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 2:
                    String messages= (String) mes.obj;
                    Toast.makeText(AddEquipDetail_Activity.this,messages,Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addequipdetail_layout);
        findviewbyid();
    }

    protected void findviewbyid(){
        carTV=findViewById(R.id.car3_text);
        equipTV=findViewById(R.id.equip3_text);
        Toolbar toolbar=findViewById(R.id.equip_toolbar);
        setSupportActionBar(toolbar);
        Button commitbutton=findViewById(R.id.buttons1_text);
        ImageView imageView=findViewById(R.id.normal1_back);
        carTV.addTextChangedListener(new EditChangedListener(carTV));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        commitbutton.setText("绑定");
        commitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(AddEquipDetail_Activity.this);   //获取存在SharedPreferences文件中的数据
                String name=sharedPreferences.getString("name","");
                if(isempt()){
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("phoneNum",name);
                        jsonObject.put("carNo",carid);
                        jsonObject.put("deviceId",equipid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Httputil.Okhttppost(GlobalConfig.BINDCAR, jsonObject.toString(), new BackHander() {      //请求接口
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
                                    messages.obj="绑定失败";
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
            }
        });

    }
   /**
   *  @Description 判断输入是否为空
   *  @param:
   *  @return
   */
    protected boolean isempt(){
        getdata();
        if(!Numberutil.isCarNo(carid)){
            Toast.makeText(AddEquipDetail_Activity.this,"车牌号格式不对",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Numberutil.isEquipNo(equipid)){
            Toast.makeText(AddEquipDetail_Activity.this,"设备号格式不对",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
    /**
    *  @Description 获取相应控件的数据
    *  @param:
    *  @return
    */
    protected void getdata(){
        carid=carTV.getText().toString();
        equipid=equipTV.getText().toString();
    }
}
