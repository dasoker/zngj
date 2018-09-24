package znjt.nxld.com.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import znjt.nxld.com.R;
import znjt.nxld.com.bean.BindCar;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.ui.adapter.Equipment_Aadpter;

/**
 * Created by zhengzhihua on 2018/9/12.22:56
 * Update 2018/9/12 22:56
 * Describe  绑定设备列表
 */

public class Addequipment_Activity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private LinearLayoutManager linearLayoutManager;
    private boolean resum=false;
    private LinearLayout linearLayout;
    private List<BindCar> list=new ArrayList<BindCar>();
    private Handler handler=new Handler(){
        public void handleMessage(Message mes){
            switch (mes.what){
                case 1:
                    String message= (String) mes.obj;
                    try {
                 //       JSONObject jsonObject=new JSONObject(message.toString());
                        JSONArray jsonArray= new JSONArray(message);
                        list=setBindCar(jsonArray);
                        linearLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        Equipment_Aadpter equipment_aadpter=new Equipment_Aadpter(Addequipment_Activity.this,list);
                        recyclerView.setAdapter(equipment_aadpter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    String messages= (String) mes.obj;
                    linearLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(Addequipment_Activity.this,messages,Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bindequipment_activity);
        requestHttp();
        resum=true;
        findviewbyid();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(resum){
            requestHttp();
        }
    }

    protected void findviewbyid(){
        recyclerView=findViewById(R.id.recycleequip_text);
        floatingActionButton=findViewById(R.id.floatingactionbutton_text);
        floatingActionButton.setOnClickListener(floatingActionButtonlistener);
        linearLayout=findViewById(R.id.nodata_layout_text);
        Toolbar toolbar=findViewById(R.id.equip_toolbar);
        setSupportActionBar(toolbar);
        ImageView imageView=findViewById(R.id.normal1_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button button=findViewById(R.id.buttons1_text);
        button.setVisibility(View.GONE);
        linearLayoutManager=new LinearLayoutManager(Addequipment_Activity.this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

    }
    /**
    *  @Description 请求网络接口
    *  @param:
    *  @return
    */
    protected void requestHttp(){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(Addequipment_Activity.this);
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
                Httputil.Okhttppost(GlobalConfig.FINDBINDCAR, jsonObject.toString(), new BackHander() {
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
                        messages.obj="获取失败";
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
                                JSONArray jsonObject2= (JSONArray) data.get("carList");
                                Message messages=handler.obtainMessage();
                                 if(jsonObject2.length()<1){
                                     messages.obj="暂无数据";
                                     messages.what=2;
                                 }else{
                                     messages.obj=jsonObject2.toString();
                                     messages.what=1;
                                 }
                                handler.sendMessage(messages);
                            }else{
                                Message messages=handler.obtainMessage();
                                messages.obj="获取失败";
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

    View.OnClickListener floatingActionButtonlistener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Addequipment_Activity.this,AddEquipDetail_Activity.class));
        }
    };

    protected List<BindCar> setBindCar(JSONArray jsonArray){
        List<BindCar> lists=new ArrayList<BindCar>();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                BindCar bindCar=new BindCar(jsonObject.getString("carNo"),jsonObject.getString("deviceId"));
                lists.add(bindCar);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return lists;
    }

}
