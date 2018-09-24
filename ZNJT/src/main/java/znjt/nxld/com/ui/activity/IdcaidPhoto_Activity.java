package znjt.nxld.com.ui.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import znjt.nxld.com.R;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.ui.camera.CameraActivity;
import znjt.nxld.com.ui.dialog.Dialogprogress_fragment;
import znjt.nxld.com.util.StringAndBitmap;

/**
 * Created by zhengzhihua on 2018/9/4.11:59
 * Update 2018/9/4 11:59
 * Describe
 */

public class IdcaidPhoto_Activity extends AppCompatActivity{
    private static final String TAG="IdcaidPhoto_Activity";
    private static final int RESULETCODE=100;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;
    private ImageView frontimage,backimage;
    private Button frontimagebutton,backimagebutton;
    private Toolbar toolbar;
    private Dialogprogress_fragment dial= Dialogprogress_fragment.newInstance();
    private ImageButton commitbutton,backview;
    private String path="",paths="";
    private TextView title;
    private List<String> mPermissionList = new ArrayList<>();
    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private Handler handler=new Handler(){
        public void handleMessage(Message mes){
            dial.dismiss();
            switch (mes.what){
                case 1:
                    JSONObject jsonObject= (JSONObject) mes.obj;
                    Intent intent=new Intent();
                    intent.putExtra("message",jsonObject.toString());
                    setResult(RESULETCODE,intent);
                    finish();
                    break;
                case 2:
                    String messages= (String) mes.obj;
                    Toast.makeText(IdcaidPhoto_Activity.this,messages,Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idcard_layout);
      if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.N){
          permissionForM();
      }
        findviewbyid();
    }

    protected void findviewbyid(){
        frontimage=findViewById(R.id.frontcard_text);
//        backimage=findViewById(R.id.backcard_text);
        frontimagebutton=findViewById(R.id.frontcardbutton_text);
//        backimagebutton=findViewById(R.id.backcardbutton_text);
        toolbar=findViewById(R.id.comit_boolbar);
        setSupportActionBar(toolbar);
        commitbutton=findViewById(R.id.comit_imagebutton);
        title=findViewById(R.id.title_texts);
        backview=findViewById(R.id.normals1_back);
        backview.setOnClickListener(backOnclick);
        frontimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(1,"请耐心等一下");
            CameraActivity.navToCamera(IdcaidPhoto_Activity.this, CameraActivity.TYPE_ID_CARD_FRONT);
            }
        });

        commitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!"".equals(path)){
                dial.show(getFragmentManager(),"progress");
                StringAndBitmap stringAndBitmap=new StringAndBitmap();
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("imageString", stringAndBitmap.bitmapToString(BitmapFactory.decodeFile(path)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Httputil.Okhttppost(GlobalConfig.GETPHOTO_URL, jsonObject.toString(), new BackHander<String>() {
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
                                        String jsonObject=data.getString("IdCard");
                                        JSONObject jsonObject2=new JSONObject(jsonObject);
                                        if(jsonObject2.length()>0)
                                        {
                                            String ss= jsonObject2.getString("result");
                                            JSONObject jsonObject3=new JSONObject(ss);
                                            Message messages=handler.obtainMessage();
                                            messages.obj=jsonObject3;
                                            messages.what=1;
                                            handler.sendMessage(messages);
                                        }else {
                                            Message messages = handler.obtainMessage();
                                            messages.obj = "请规范拍照";
                                            messages.what = 2;
                                            handler.sendMessage(messages);
                                        }
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

            }else{

            }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  /*      if (requestCode == CameraActivity.REQUEST_CODE && resultCode == CameraActivity.RESULT_CODE) {
            //获取图片路径，显示图片
            if((int)data.getSerializableExtra("type")==1){
                  path = CameraActivity.getImagePath(data);
                if (!TextUtils.isEmpty(path)) {
                    frontimage.setImageBitmap(BitmapFactory.decodeFile(path));
                }
            }else if((int)data.getSerializableExtra("type")==2){
                 paths = CameraActivity.getImagePath(data);
                if (!TextUtils.isEmpty(paths)) {
                    backimage.setImageBitmap(BitmapFactory.decodeFile(paths));
                }
            }
        }*/
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CameraActivity.REQUEST_CODE) {
            //获取文件路径，显示图片
            if (data != null) {
                 path = data.getStringExtra("result");
                if (!TextUtils.isEmpty(path)) {
                    frontimage.setImageBitmap(BitmapFactory.decodeFile(path));
                }
            }
        }
    }
    private void permissionForM() {
        List<String> permissionList = new ArrayList<>();
        for(int i=0;i<permissions.length;i++){
        if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(permissions[i]);
        }}
        if(permissionList.size()>0) {
            String[] strings=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, strings, 1);
        }else{
            findviewbyid();
        }

     /*   if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0x12);
            return;
        }
        findviewbyid();*/
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissions.length>0 && requestCode==1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults.length > 0) {//安全写法，如果小于0，肯定会出错了
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        switch (grantResult){
                            case PackageManager.PERMISSION_GRANTED://同意授权0
                                break;
                            case PackageManager.PERMISSION_DENIED://拒绝授权-1
                                break;
                        }
                    }

                }else{
                    findviewbyid();
                }
            }
        }


    }


        private void showToast(int i,String string){
        if(i==MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            Toast.makeText(IdcaidPhoto_Activity.this, string, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(IdcaidPhoto_Activity.this, string, Toast.LENGTH_LONG).show();
        }
        }
    /**
    *  @Description  返回监听
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
