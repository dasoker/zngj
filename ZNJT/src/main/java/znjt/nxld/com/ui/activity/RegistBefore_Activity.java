package znjt.nxld.com.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import znjt.nxld.com.R;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.util.Numberutil;


/**
 * Created by zhengzhihua on 2018/9/3.11:49
 * Update 2018/9/3 11:49
 * Describe
 */

public class RegistBefore_Activity extends AppCompatActivity{
    private static final String TAG="Regist_activity";
    private EditText photoid,yzm;   //手机号、密码、确认密码、验证码
    private Button nextbutton,gainnumber;
    private int TIME_COUNT;
    private Handler mHandler=new Handler();
    private  Runnable runnable;//提交按钮、获取验证码按钮
    private String photoid_text,yzm_text;
    private Handler handler=new Handler(){
       public void handleMessage(Message msg){
           switch (msg.what){
               case 1:
                   String message= (String) msg.obj;
                   Toast.makeText(RegistBefore_Activity.this,message, Toast.LENGTH_LONG).show();
                   break;
               case 2:
                   String messages= (String) msg.obj;
                   Toast.makeText(RegistBefore_Activity.this,messages,Toast.LENGTH_LONG).show();
                   break;
               case 3:
                   String messages1= (String) msg.obj;
                   Toast.makeText(RegistBefore_Activity.this,messages1,Toast.LENGTH_LONG).show();
                   break;
           }
       }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);
        findviewbyid();
    }
    /**
     *  @Description  将创建的实例与布局文件中的控件绑定
     *  @param:
     *  @return
     */
    protected void findviewbyid(){
        photoid=findViewById(R.id.photoid_text);
        yzm=findViewById(R.id.yzm_text);
        nextbutton=findViewById(R.id.next_text);
        gainnumber=findViewById(R.id.gainnumber_text);
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(determineempty()){
                   JSONObject jsonObject=new JSONObject();
                   try {
                       jsonObject.put("phoneNum",photoid_text);
                       jsonObject.put("yzm",yzm_text);
                       jsonObject.put("type","2");
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           Httputil.Okhttppost(GlobalConfig.YZM_URL, jsonObject.toString(), new BackHander<String>() {
                               /*Httputil.Okhttpget(GlobalConfig.VERIFICATION_PHOTO_URL, map, new BackHander<String>() {*/
                               @Override
                               public void onexception(Exception e) {
                                   Message messages=handler.obtainMessage();
                                   messages.obj="请求超时";
                                   messages.what=2;
                                   handler.sendMessage(messages);
                               }

                               @Override
                               public void onFail(String response) {
                                   Message messages=handler.obtainMessage();
                                   messages.obj="请求失败";
                                   messages.what=1;
                                   handler.sendMessage(messages);
                               }

                               @Override
                               public void onsuccess(String response) {
                                   try {
                                       JSONObject jsonObject1=new JSONObject(response.toString());
                                       JSONObject aa=(JSONObject) jsonObject1.get("body");
                                       JSONObject data= (JSONObject) aa.get("data");
                                       boolean is= (boolean) data.get("issuccess");
                                       if(is){
                                           Intent intent=new Intent(RegistBefore_Activity.this,Regist_Activity.class);
                                           intent.putExtra("phone",photoid_text);
                                           startActivity(intent);
                                           finish();
                                       }else{
                                           Message messages=handler.obtainMessage();
                                           messages.obj="请求失败";
                                           messages.what=1;
                                           handler.sendMessage(messages);
                                       }
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }

                               }
                           });
                       }
                   }).start();
                   /*Intent intent=new Intent(RegistBefore_Activity.this,Regist_Activity.class);
                   intent.putExtra("phone",photoid_text);
                         startActivity(intent);
                   finish();*/
                }
            }
        });
        gainnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(runnable);
                TIME_COUNT=60;
                photoid_text=photoid.getText().toString();
                if(!Numberutil.isValidMobiNumber(photoid_text)){
                    Toast.makeText(RegistBefore_Activity.this,"手机号格式不对",Toast.LENGTH_SHORT).show();
                }else{
                    timeCount();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("phoneNum",photoid_text);
                        jsonObject.put("type","2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Httputil.Okhttppost(GlobalConfig.VERIFICATION_PHOTO_URL, jsonObject.toString(), new BackHander<String>() {
                                 /*Httputil.Okhttpget(GlobalConfig.VERIFICATION_PHOTO_URL, map, new BackHander<String>() {*/
                                @Override
                                public void onexception(Exception e) {
                                    Message messages=handler.obtainMessage();
                                    messages.obj="请求超时";
                                    messages.what=2;
                                    handler.sendMessage(messages);
                                }

                                @Override
                                public void onFail(String response) {
                                    Message messages=handler.obtainMessage();
                                    messages.obj="请求失败";
                                    messages.what=1;
                                    handler.sendMessage(messages);
                                }

                                @Override
                                public void onsuccess(String response) {
                                    try {
                                        JSONObject jsonObject1=new JSONObject(response.toString());
                                        JSONObject aa=(JSONObject) jsonObject1.get("body");
                                        JSONObject data= (JSONObject) aa.get("data");
                                        boolean is= (boolean) data.get("issuccess");
                                        if(is){
                                            Message messages=handler.obtainMessage();
                                            messages.obj="准备填写验证码";
                                            messages.what=3;
                                            handler.sendMessage(messages);
                                        }else{
                                            Message messages=handler.obtainMessage();
                                            messages.obj=data.get("message");
                                            messages.what=1;
                                            handler.sendMessage(messages);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
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
     * 定时器
     */
    private void timeCount() {
        runnable=new Runnable() {
            @Override
            public void run() {
                if (TIME_COUNT>0){
                    gainnumber.setEnabled(false);
                    gainnumber.setText(TIME_COUNT+"s");
                    TIME_COUNT--;
                }
                if (TIME_COUNT==0){
                    gainnumber.setText("获取验证码");
                    gainnumber.setEnabled(true);
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(runnable, 1000);
    }

    /**
     *  @Description 初始化界面
     *  @param:
     *  @return
     */
    protected void initview(){
        photoid_text=photoid.getText().toString();
        yzm_text=yzm.getText().toString();
    }
    /**
     *  @Description  判断注册条件是否成立
     *  @param:
     *  @return  成立返回true，反之返回false
     */
    private boolean determineempty(){
             initview();
        if(!Numberutil.isValidMobiNumber(photoid_text)){   //判断手机号格式是否正确
            Toast.makeText(RegistBefore_Activity.this,"手机号格式不对",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Numberutil.isYzm(yzm_text)){   //判断验证码格式是否正确
            Toast.makeText(RegistBefore_Activity.this,"验证码格式不对",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }

    }
    /**
     *  @Description 注册的具体实现，调接口
     *  @param:
     *  @return
     */
    private void nextActivity(){
        initview();
        if(determineempty()){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("yzm",yzm_text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Httputil.Okhttppost(GlobalConfig.YZM_URL, jsonObject.toString(), new BackHander<String>() {
                @Override
                public void onexception(Exception e) {

                }

                @Override
                public void onFail(String response) {

                }

                @Override
                public void onsuccess(String response) {

                }
            });
        }else{
            return;
        }

    }
}