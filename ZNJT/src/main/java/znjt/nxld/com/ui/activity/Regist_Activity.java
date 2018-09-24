package znjt.nxld.com.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import znjt.nxld.com.R;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.util.Numberutil;
import znjt.nxld.com.util.PasswordUntil;

/**
 * Created by zhengzhihua on 2018/9/5.12:50
 * Update 2018/9/5 12:50
 * Describe
 */

public class Regist_Activity extends AppCompatActivity implements TextWatcher {
    private static final String TAG="RegistBefore_Activity";
    private EditText password,secendpsw;     //密码，确认密码
    private Button commit;             //提交按钮
    private TextView low,between,high;
    private String passwordtext,secendpswtext,Phone;
    private Handler handler=new Handler(){
      public void handleMessage(Message mes){
          switch (mes.what){
              case 1:
                  String message= (String) mes.obj;
                  Toast.makeText(Regist_Activity.this,message, Toast.LENGTH_LONG).show();
                  break;
              case 2:
                  String messages= (String) mes.obj;
                  Toast.makeText(Regist_Activity.this,messages,Toast.LENGTH_LONG).show();
                  break;

          }
      }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_layout);
        Phone=getIntent().getStringExtra("phone");
        findviewbyid();
    }
    /**
     *  @Description 绑定控件
     *  @param:
     *  @return
     */
    protected void findviewbyid(){
        password=findViewById(R.id.password);
        secendpsw=findViewById(R.id.twopsw);
        low=findViewById(R.id.password_stronger1_red);
        between=findViewById(R.id.password_stronger1_orange);
        high=findViewById(R.id.password_stronger1_green);
        commit=findViewById(R.id.regist_text);
        password.addTextChangedListener(this);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty()){
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("phoneNum",Phone);
                        jsonObject.put("password",passwordtext);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String json=jsonObject.toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Httputil.Okhttppost(GlobalConfig.REGIST_URL, json, new BackHander<String>() {
                                @Override
                                public void onexception(Exception e) {
                                    Message messages=handler.obtainMessage();
                                    messages.obj="请求超时...";
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
                                            messages.obj="注册成功";
                                            messages.what=1;
                                            handler.sendMessage(messages);
                                            Intent intent=new Intent(Regist_Activity.this,LoginActivity.class);
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
                }
            }
        });
    }
    /**
     *  @Description 获取填写的数据
     *  @param:
     *  @return
     */
    protected void getdata(){
        passwordtext=password.getText().toString();
        secendpswtext=secendpsw.getText().toString();
    }
    /**
     *  @Description 判断输入是否为空
     *  @param:
     *  @return
     */
    protected boolean isEmpty(){
        getdata();
        if(passwordtext.length()<6){     //判断密码是否正确
            Toast.makeText(Regist_Activity.this,"密码格式不对",Toast.LENGTH_SHORT).show();
            return false;
        }else if(secendpswtext.equals("")){
            Toast.makeText(Regist_Activity.this,"确认密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (PasswordUntil.password(password.getText().toString())==0){
            low.setVisibility(View.INVISIBLE);
            between.setVisibility(View.INVISIBLE);
            high.setVisibility(View.INVISIBLE);
        }else if (PasswordUntil.password(password.getText().toString()) == 1){
            low.setVisibility(View.VISIBLE);
            between.setVisibility(View.INVISIBLE);
            high.setVisibility(View.INVISIBLE);
        }else if (PasswordUntil.password(password.getText().toString())==2){
            low.setVisibility(View.VISIBLE);
            between.setVisibility(View.VISIBLE);
            high.setVisibility(View.INVISIBLE);
        }else if (PasswordUntil.password(password.getText().toString())==3){
            low.setVisibility(View.VISIBLE);
            between.setVisibility(View.VISIBLE);
            high.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(this,"输入了非法字符",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
