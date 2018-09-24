package znjt.nxld.com.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
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
*@date:2018/9/3 17:04
*@Description:登录
*/
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener, TextWatcher {
    private Button signIn;//登录按钮
    private TextView forgetPassword;//密码找回
    private TextView register;//注册按钮
    private EditText userName;
    private EditText password;
    private ImageButton nameClean;
    private ImageButton passwordClean;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Handler handler=new Handler();
    private ImageButton seePassword;
    private int IS_REQUEST_SERVER=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        writeNameAndPassword();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        signIn = findViewById(R.id.sign_in);
        signIn.setOnClickListener(this);
        forgetPassword = findViewById(R.id.forget_password);
        forgetPassword.setOnClickListener(this);
        register = findViewById(R.id.login_register);
        register.setOnClickListener(this);
        userName = findViewById(R.id.login_name);
        userName.addTextChangedListener(this);
        password = findViewById(R.id.login_password);
        password.addTextChangedListener(this);
        nameClean = findViewById(R.id.login_name_cancel);
        nameClean.setOnClickListener(this);
        passwordClean = findViewById(R.id.login_password_cancel);
        passwordClean.setOnClickListener(this);
        seePassword = findViewById(R.id.login_see_password);
        seePassword.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.login_register:
            startActivity(new Intent(LoginActivity.this,RegistBefore_Activity.class));
            break;
        case R.id.forget_password:
            Intent intent = new Intent(LoginActivity.this,RetrievePasswordActivity.class);
            intent.putExtra("userName",userName.getText());
            startActivity(intent);
            break;
        case R.id.sign_in:
            String Name=userName.getText().toString();
            String pwd=password.getText().toString();
            if ((Name.equals(""))&&(pwd.equals(""))){
                Toast.makeText(this,"手机号和密码不能为空",Toast.LENGTH_SHORT).show();
            }else {
                boolean isOrNo = Numberutil.isValidMobiNumber(Name);
                if (isOrNo){
                    rememberNameAndPassword();
                    //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    if (IS_REQUEST_SERVER==1){
                        IS_REQUEST_SERVER=0;
                        login();
                    }
                }else {
                    Toast.makeText(this,"手机号格式不正确",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        case R.id.login_name_cancel:
            userName.setText("");
            break;
        case R.id.login_password_cancel:
            password.setText("");
            break;
        default:
            break;
    }
    }

    /**
     * 登录
     */
    private void login() {
    //    startActivity(new Intent(LoginActivity.this,MainActivity.class));
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phoneNum",userName.getText().toString());
            jsonObject.put("password",password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String maps= String.valueOf(jsonObject);
        Httputil.Okhttppost(GlobalConfig.LOGIN_URL,maps, new BackHander<String>() {
            @Override
            public void onexception(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        IS_REQUEST_SERVER=1;
                        Toast.makeText(getApplicationContext(),"未连接到服务器...",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onFail(String response) {
                try {
                    JSONObject jsonObject1=new JSONObject(response.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            IS_REQUEST_SERVER=1;
                            Toast.makeText(getApplicationContext(),"请求服务器发生异常，联系服务人员",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onsuccess(String response) {
                IS_REQUEST_SERVER=1;
                Log.d("TAG",response);
                try {
                    JSONObject jsonObject1=new JSONObject(response.toString());
                    JSONObject aa=(JSONObject) jsonObject1.get("body");
                    JSONObject data= (JSONObject) aa.get("data");
                    boolean is= (boolean) data.get("issuccess");
                    if(is){
                        String advertisements =data.get("advertList").toString();
                        JSONObject sysBasePerson = (JSONObject) data.get("sysbasePerson");
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("advertisements", advertisements);
                        intent.putExtra("phoneNo", (String) sysBasePerson.get("phoneNum"));
                        startActivity(intent);
                        finish();
                    }else{
                        if (data.get("message").toString().equals("该用户未注册")){
                            handler.post(() -> Toast.makeText(getApplicationContext(),"用户未注册",Toast.LENGTH_SHORT)
                                    .show());
                        }else {
                            handler.post(() -> Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT)
                                    .show());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id= view.getId();
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (id==R.id.login_see_password){
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    seePassword.setBackgroundResource(R.drawable.eyes_gray);
                }
               return true;
            case MotionEvent.ACTION_UP:
                if (id==R.id.login_see_password){
                    password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    seePassword.setBackgroundResource(R.drawable.eyes_white);
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String name=userName.getText().toString();
        String pwd=password.getText().toString();
        if (!name.equals("")){
            nameClean.setVisibility(View.VISIBLE);
        }else {
            nameClean.setVisibility(View.GONE);
        }
        if (!pwd.equals("")){
            passwordClean.setVisibility(View.VISIBLE);
            seePassword.setVisibility(View.VISIBLE);
        }else {
            passwordClean.setVisibility(View.GONE);
            seePassword.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
    public void rememberNameAndPassword(){
        editor=pref.edit();
        editor.putString("name",userName.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.apply();
    }
    public void writeNameAndPassword(){
        String name=pref.getString("name","");
        String pwd=pref.getString("password","");
        if (!name.equals("")){
            userName.setText(name);
        }
        if (!pwd.equals("")){
            password.setText(pwd);
        }
    }
}
