package znjt.nxld.com.util.imageUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import znjt.nxld.com.R;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.util.StringAndBitmap;

/**s
*@Author:dingkuirong
*@date:2018/9/15 10:32
*@Description:裁剪页面
*/
public class ImageCutActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private String mImagePath;
    private ImageView mImageView;
    private TextView use;
    private TextView cut;
    private File afterCutImage;
    private Uri resultUri;
    private int DO_CUT=1;
    private ProgressDialog mProgressDialog ;
    private Handler mHandler = new Handler();
    private SharedPreferences pref;
    private ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cut);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        mImagePath = getIntent().getStringExtra("imagePath");
        initView();
        initEvent();
        initResources();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        use.setOnClickListener(this);
        cut.setOnClickListener(this);
    }

    /**
     * 裁剪图片
     */
    private void catImage() {
        DO_CUT=2;
        Uri uri= Uri.fromFile(new File(mImagePath));
        Uri destinationUri = Uri.fromFile(afterCutImage);
        UCrop uCrop = UCrop.of(uri, destinationUri);
        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //是否能调整裁剪框
        // options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);
        uCrop.start(this);
    }

    /**
     * 显示图片
     */
    private void initResources() {
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        mImageView.setImageBitmap(bitmap);
        afterCutImage = new File(getCacheDir(), "myCroppedImage.jpg");
        if (afterCutImage.exists()){
            afterCutImage.delete();
        }
        try {
            afterCutImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (DO_CUT==1){
            new Thread(){
                @Override
                public void run() {
                    File file = new File(mImagePath);
                    afterCutImage=file;
                }
            }.start();
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
        mImageView = findViewById(R.id.cut_image);
        use = findViewById(R.id.cut_image_toolbar_use);
        cut = findViewById(R.id.cut_image_toolbar_do_cut);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cut_image_toolbar_do_cut:
                catImage();
                break;
            case R.id.cut_image_toolbar_use:
                useImage();
                break;
            default:
                break;
        }
    }

    /**
     * 裁剪后使用
     */
    private void useImage() {
        mProgressDialog= new ProgressDialog(ImageCutActivity.this);
        mProgressDialog.setMessage("上传中...");
        mProgressDialog.show();
        new Thread(){
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(afterCutImage.getAbsolutePath());
                //ImageCompress.saveBitmap(bitmap,afterCutImage.getAbsolutePath());
                String imageString = new StringAndBitmap().bitmapToString(bitmap);
                String url = GlobalConfig.SERVER_ADDRESS+"/uploadprofilePhoto"+
                        GlobalConfig.LAST_WORD;
                JSONObject json = new JSONObject();
                String name=pref.getString("name","");
                try {
                    json.put("phoneNum",name);
                    json.put("profilePhoto",imageString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Httputil.Okhttppost(url, json.toString(), new BackHander<String>() {
                    @Override
                    public void onexception(Exception e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"上传失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFail(String response) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"上传失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onsuccess(String response) {
                        Log.d("TAG",response);
                        try {
                            JSONObject rJson = new JSONObject(response);
                            JSONObject body = (JSONObject) rJson.get("body");
                            JSONObject data = (JSONObject) body.get("data");
                            boolean isSuccess = (boolean) data.get("issuccess");
                            if (isSuccess){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"上传成功",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ImageCutActivity.this,GridViewActivity.class);
                                        intent.putExtra("url",afterCutImage.getAbsolutePath());
                                        setResult(101,intent);
                                        finish();
                                    }
                                });
                            }else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"上传失败",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resultUri = UCrop.getOutput(data);
            mImageView.setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
}
