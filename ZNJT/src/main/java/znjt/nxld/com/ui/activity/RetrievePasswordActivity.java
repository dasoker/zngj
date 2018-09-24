package znjt.nxld.com.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import znjt.nxld.com.R;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.util.Numberutil;

/**
*@Author:dingkuirong
*@date:2018/9/3 17:41
*@Description:找回密码
*/
public class RetrievePasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private Button sendMessageToServer;
    private Button sendVerification;
    private ImageButton back;
    private EditText phoneNumber;//电话号码
    private EditText verificationCode;
    private Handler mHandler = new Handler();
    private int TIME_COUNT;
    private  Runnable runnable;
    private TextView backView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        initView();
        initEvent();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        sendMessageToServer.setOnClickListener(this);
        sendVerification.setOnClickListener(this);
        back.setOnClickListener(this);
        backView.setOnClickListener(this);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        sendMessageToServer = findViewById(R.id.send_message);
        sendVerification = findViewById(R.id.send_verification);
        phoneNumber = findViewById(R.id.retrieve_phone_number);
        verificationCode = findViewById(R.id.retrieve_password_verification_code);
        back = findViewById(R.id.normal_back);
        backView = findViewById(R.id.normal_back_text_view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send_message:
               if ((sendVerification.getText().toString()).isEmpty()){
                    Toast.makeText(this,"请先输入验证码",Toast.LENGTH_SHORT).
                            show();
                }else{
                    sendVerificationCodeToServer();
                }
                break;
            case R.id.send_verification:
                mHandler.removeCallbacks(runnable);
                TIME_COUNT=60;
                if ((phoneNumber.getText().toString()).isEmpty()){
                    Toast.makeText(this,"请先输入手机号码",Toast.LENGTH_SHORT).
                            show();
                }else{
                    String num= phoneNumber.getText().toString();
                    boolean isOrNo= Numberutil.isValidMobiNumber(num);
                    if (isOrNo){
                        timeCount();
                        sendPhoneNumber();
                    }else {
                        Toast.makeText(this,"请输入正确的手机号",Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
            case R.id.normal_back:
                finish();
                break;
            case R.id.normal_back_text_view:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 定时器
     */
    private void timeCount() {
       runnable=new Runnable() {
        @Override
        public void run() {
           if (TIME_COUNT>0){
               sendVerification.setEnabled(false);
               sendVerification.setText(TIME_COUNT+"s");
               TIME_COUNT--;
           }
            if (TIME_COUNT==0){
                sendVerification.setText("获取验证码");
                sendVerification.setEnabled(true);
            }
            mHandler.postDelayed(this, 1000);
        }
        };
        mHandler.postDelayed(runnable, 1000);
    }

    /**
     * 传送验证码到服务器
     */
    private void sendVerificationCodeToServer() {
        String verification = verificationCode.getText().toString();
        String num=phoneNumber.getText().toString();
        String url = GlobalConfig.SERVER_ADDRESS+"/mobileFindPassword"+
                GlobalConfig.LAST_WORD;
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum",num);
            json.put("yzm",verification);
            Httputil.Okhttppost(url, json.toString(), new BackHander<String>() {
                @Override
                public void onexception(Exception e) {
                    e.printStackTrace();

                }

                @Override
                public void onFail(String response) {

                }

                @Override
                public void onsuccess(String response) {
                    try {
                        JSONObject rJson= new JSONObject(response.toString());
                        JSONObject body = (JSONObject) rJson.get("body");
                        JSONObject data= (JSONObject) body.get("data");
                        boolean isOrNo = (boolean) data.get("issuccess");
                        if (isOrNo){
                            Intent intent = new Intent(RetrievePasswordActivity.this,RetrievePasswordEditActivity.class);
                            intent.putExtra("phoneNum",phoneNumber.getText().toString());
                            startActivity(intent);
                            finish();
                        }else {
                            mHandler.post(() -> {
                                Toast.makeText(getApplicationContext(),"验证码输入有误",Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送请求到服务器，调取华为短信服务发送验证码给xx手机
     */
    private void sendPhoneNumber() {
        String number = phoneNumber.getText().toString();
        String url= GlobalConfig.SERVER_ADDRESS+"/mobileSMS"+GlobalConfig.LAST_WORD;
        JSONObject json=new JSONObject();
        try {
            json.put("type","1");//找回密码
            json.put("phoneNum",number);
            Httputil.Okhttppost(url, json.toString(), new BackHander<String>() {
                @Override
                public void onexception(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onFail(String response) {

                }

                @Override
                public void onsuccess(String response) {
                    Log.d("TAG",response);
                    try {
                        JSONObject rJson = new JSONObject(response.toString());
                        JSONObject body = (JSONObject) rJson.get("body");
                        JSONObject data = (JSONObject) body.get("data");

                        boolean already = data.getBoolean("issuccess");
                        if (already){
                            mHandler.post(() -> {
                                Toast.makeText(getApplicationContext(),"短信发送成功，请注意查收",Toast.LENGTH_SHORT).show();
                            });
                        }else {
                            mHandler.post(() -> {
                               phoneNumber.setText("");
                               Toast.makeText(getApplicationContext(),"还未注册，请先注册",Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
