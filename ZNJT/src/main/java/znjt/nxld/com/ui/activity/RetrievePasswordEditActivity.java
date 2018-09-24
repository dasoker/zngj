package znjt.nxld.com.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import znjt.nxld.com.util.PasswordUntil;

/**
*@Author:dingkuirong
*@date:2018/9/6 11:21
*@Description:设置新密码页面
*/
public class RetrievePasswordEditActivity extends AppCompatActivity implements TextWatcher ,View.OnClickListener{
    private EditText retrievePasswordEditFirst;
    private EditText retrievePasswordEditTwo;
    private TextView red;
    private TextView orange;
    private TextView green;
    private Button changePassword;
    private ImageButton back;
    private TextView backView;
    private String num;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password_edit);
        Intent intent = getIntent();
        num =intent.getStringExtra("phoneNum");
        initView();
        initEvent();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        retrievePasswordEditFirst.addTextChangedListener(this);
        changePassword.setOnClickListener(this);
        back.setOnClickListener(this);
        backView.setOnClickListener(this);
    }

    /**
     * 初始化
     */
    private void initView() {
        retrievePasswordEditFirst = findViewById(R.id.retrieve_password_edit_first);
        retrievePasswordEditTwo = findViewById(R.id.retrieve_password_edit_two);
        red = findViewById(R.id.password_stronger_red);
        orange = findViewById(R.id.password_stronger_orange);
        green = findViewById(R.id.password_stronger_green);
        changePassword = findViewById(R.id.change_password_ok);
        back = findViewById(R.id.normal_back);
        backView = findViewById(R.id.normal_back_text_view);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String str = retrievePasswordEditFirst.getText().toString().trim();
        int type=PasswordUntil.password(str);
        if (type==0){
            red.setVisibility(View.INVISIBLE);
            orange.setVisibility(View.INVISIBLE);
            green.setVisibility(View.INVISIBLE);
        }else if (type == 1){
            red.setVisibility(View.VISIBLE);
            orange.setVisibility(View.INVISIBLE);
            green.setVisibility(View.INVISIBLE);
        }else if (type==2){
            red.setVisibility(View.VISIBLE);
            orange.setVisibility(View.VISIBLE);
            green.setVisibility(View.INVISIBLE);
        }else if (type==3){
            red.setVisibility(View.VISIBLE);
            orange.setVisibility(View.VISIBLE);
            green.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(this,"输入了非法字符",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.change_password_ok:
                if ((retrievePasswordEditFirst.getText().toString())
                        .equals(retrievePasswordEditTwo.getText().toString())){
                    sendNewPassword();
                }else {
                    Toast.makeText(this,"两次密码输入不一样",Toast.LENGTH_SHORT)
                            .show();
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
     * 设置新的密码
     */
    private void sendNewPassword() {
        String url = GlobalConfig.SERVER_ADDRESS+"/mobileModifyPassword"+GlobalConfig.LAST_WORD;
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum",num);
            json.put("password",retrievePasswordEditFirst.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Httputil.Okhttppost(url, json.toString(), new BackHander() {
            @Override
            public void onexception(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onFail(Object response) {

            }

            @Override
            public void onsuccess(Object response) {
                try {
                    JSONObject rJson = new JSONObject(response.toString());
                    JSONObject body = (JSONObject) rJson.get("body");
                    JSONObject data = (JSONObject) body.get("data");
                    boolean already = data.getBoolean("issuccess");
                    if (already){
                        mHandler.post(() -> {
                            Toast.makeText(getApplicationContext(),"密码重置成功",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RetrievePasswordEditActivity.this,LoginActivity.class));
                            finish();
                        });
                    }else {
                        mHandler.post(() -> {
                            Toast.makeText(getApplicationContext(),"重置发生错误",Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
