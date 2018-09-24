package znjt.nxld.com.ui.activity;

import android.content.Intent;
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
 * Created by zhengzhihua on 2018/9/4.10:38
 * Update 2018/9/4 10:38
 * Describe  完善信息
 */

public class WriteInformatin_Activity extends AppCompatActivity{
    private static final String TAG="WriteInformatin_Activity";
    private static final int REQUESTCODE=1;
    private static final int RESULTCODE=100;
    private EditText name,cardid,cardadress,idsex,idcardbrithdata,deviceid,carid;  //名字、身份证号、地址、性别、出生日期、有效期、设备号、车牌号
    private Button takephoto;                  //拍照按钮
    private TextView title;
    private JSONObject jsonObject;
    private ImageButton commitbutton,backview;
    private Toolbar toolbar;
    private String nametext,cardidtext,cardadresstext,idsextext,idcardbrithdatatext,deviceidtext,caridtext;
    private Handler handler=new Handler(){
        public void handleMessage(Message mes){
            switch (mes.what){
                case 1:
                    String message= (String) mes.obj;
                    Toast.makeText(WriteInformatin_Activity.this,message, Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    String messages= (String) mes.obj;
                    Toast.makeText(WriteInformatin_Activity.this,messages,Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    JSONObject jsob= (JSONObject) mes.obj;
                    jsonObject=new JSONObject();
                    try {
                        jsonObject.put("name",jsob.get("driverName"));
                        jsonObject.put("number",jsob.get("idcardNo"));
                        jsonObject.put("address",jsob.get("linkAddress"));
                        jsonObject.put("sex",jsob.get("driverSex"));
                        jsonObject.put("birth",jsob.get("birthday"));
                        setdata();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_information_layout);
        getFornetdata();
        findviewbyid();
    }
    /**
    *  @Description  绑定控件
    *  @param:
    *  @return
    */
    protected void findviewbyid(){
        name=findViewById(R.id.idname_text);
        cardid=findViewById(R.id.idcardid_text);
        cardadress=findViewById(R.id.idadress_text);
        idsex=findViewById(R.id.idsex_text);
        idcardbrithdata=findViewById(R.id.idbrithdata_text);
//        deviceid=findViewById(R.id.deviceid_text);
//        carid=findViewById(R.id.carid_text);
        takephoto=findViewById(R.id.takephoto_text);
        toolbar=findViewById(R.id.comit_boolbar);
        setSupportActionBar(toolbar);
        commitbutton=findViewById(R.id.comit_imagebutton);
        title=findViewById(R.id.title_texts);
        backview=findViewById(R.id.normals1_back);
        backview.setOnClickListener(backOnclick);
        title.setText("完善信息");
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WriteInformatin_Activity.this,IdcaidPhoto_Activity.class);
                startActivityForResult(intent,1);
            }
        });
        commitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(isEmpty(1)){
                   SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(WriteInformatin_Activity.this);
                   String name=sharedPreferences.getString("name","");
                   Toast.makeText(WriteInformatin_Activity.this, name, Toast.LENGTH_SHORT).show();
                   JSONObject jsonObjects=new JSONObject();
                   try {
                       jsonObjects.put("phoneNum",name);
                       jsonObjects.put("driverName",nametext);
                       jsonObjects.put("idCardNo",cardidtext);
                       jsonObjects.put("linkAddress",cardadresstext);
                       jsonObjects.put("driverSex",idsextext);
                       jsonObjects.put("birthday",idcardbrithdatatext);

                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           Httputil.Okhttppost(GlobalConfig.WRITEINFORMATION_URL, jsonObjects.toString(), new BackHander<String>() {
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
                                   messages.what=2;
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
                                           messages.obj="已完善";
                                           messages.what=1;
                                           handler.sendMessage(messages);
                                       }else{
                                           Message messages=handler.obtainMessage();
                                           messages.obj="请求失败";
                                           messages.what=2;
                                           handler.sendMessage(messages);
                                       }
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                       Message messages=handler.obtainMessage();
                                       messages.obj="请求异常";
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
    *  @Description activity跳转返回的数据进行填入
    *  @param:
    *  @return
    */
    protected void setdata(){
        try {
            name.setText(jsonObject.getString("name"));
            cardid.setText(jsonObject.getString("number"));
            cardadress.setText(jsonObject.getString("address"));
            idsex.setText(jsonObject.getString("sex"));
            idcardbrithdata.setText(jsonObject.getString("birth"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

  /*      deviceid.setText("");
        carid.setText("");*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESTCODE){
            if(resultCode==RESULTCODE){
                try {
                    jsonObject=new JSONObject(data.getStringExtra("message"));
                    setdata();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }
    /**
    *  @Description  判断字符串是否为空
    *  @param:
    *  @return
    */
    protected boolean isEmpty(int i){
        getdata();
        if(i==1) {
            if (!Numberutil.isName(nametext)) {
                Toast.makeText(WriteInformatin_Activity.this, "姓名输入是否正确", Toast.LENGTH_SHORT).show();
                return false;
            }else if(!Numberutil.isIDcardid(cardidtext)){
                Toast.makeText(WriteInformatin_Activity.this, "请输入正确的身份证号", Toast.LENGTH_SHORT).show();
                return false;
            }else if( !"男".equals(idsextext)&& !"女".equals(idsextext)){
                Toast.makeText(WriteInformatin_Activity.this, "请查看性别是否正确", Toast.LENGTH_SHORT).show();
                return false;
            }else if( cardadresstext.equals("")|| idcardbrithdatatext.equals("")){
                Toast.makeText(WriteInformatin_Activity.this, "地址或出生日期存在空请填写", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                return true;
            }
        }else if(i==2){
            if(deviceidtext.equals("") || caridtext.equals("")){
                Toast.makeText(WriteInformatin_Activity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }else {
                return true;
            }
        }else {
            return false;
        }
    }
    /**
    *  @Description 获取数据
    *  @param:
    *  @return
    */
    protected void getdata(){
        nametext=name.getText().toString();
        cardidtext=cardid.getText().toString();
        cardadresstext=cardadress.getText().toString();
        idsextext=idsex.getText().toString();
        idcardbrithdatatext=idcardbrithdata.getText().toString();
   /*     deviceidtext=deviceid.getText().toString();
        caridtext=carid.getText().toString();*/

    }

    protected void getFornetdata(){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(WriteInformatin_Activity.this);
        String name=sharedPreferences.getString("name","");
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phoneNum",name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Httputil.Okhttppost(GlobalConfig.GETUSERMESSAGE, jsonObject.toString(), new BackHander() {
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
                        messages.obj="请求失败";
                        messages.what=2;
                        handler.sendMessage(messages);
                    }

                    @Override
                    public void onsuccess(Object response) {
                        try {
                            JSONObject jsonObject1=new JSONObject(response.toString());
                            JSONObject aa=(JSONObject) jsonObject1.get("body");
                            JSONObject data= (JSONObject) aa.get("data");
                            boolean is=(boolean) data.get("issuccess");
                            if(is){
                                JSONObject jso= (JSONObject) data.get("sysbasePerson");
                                if(jso.length()>0){
                                    Message messages=handler.obtainMessage();
                                    messages.obj=jso;
                                    messages.what=3;
                                    handler.sendMessage(messages);
                                }
                            }else{
                                Message messages=handler.obtainMessage();
                                messages.obj="请求失败";
                                messages.what=2;
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
    /**
    *  @Description 吐司方法
    *  @param:
    *  @return
    */
    private void showToast(int i,String string){
        if(i==1){
            Toast.makeText(WriteInformatin_Activity.this,string,Toast.LENGTH_SHORT).show();
        }else{
        Toast.makeText(WriteInformatin_Activity.this,string,Toast.LENGTH_LONG).show();
    }}
    /**
    *  @Description 返回监听
    *  @param:
    *  @return
    */
    View.OnClickListener backOnclick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
