package znjt.nxld.com.util.imageUtil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import znjt.nxld.com.R;
import znjt.nxld.com.ui.activity.MainActivity;

public class GridViewActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView toolbarTitle;
    private RelativeLayout mBottomLy;
    private GridView gridView;
    private LinearLayout albumSelect;
    private TextView imageSum;
    private TextView album;
    private List<String> mImages;
    private File mCurrentDir;
    private int mMaxCount;
    private List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();
    private ProgressDialog mProgressDialog;
    private static final int DATA_LOAD= 0x110;
    private GridViewAdapter adapter;
    private ListImageDirWindow window;
    private static final int TAKE_PHOTO=1;
    private String imageSelectPath;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==DATA_LOAD){
                mProgressDialog.dismiss();
                DataToView();
                initPopupWindow();
            }
        }
    };
    private ImageButton back;
    private void initPopupWindow() {
        window = new ListImageDirWindow(this,mFolderBeans);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
        window.setOnDirSelectListener(new ListImageDirWindow.OnDirSelectListener() {
            @Override
            public void onSelect(FolderBean folderBean) {
                mCurrentDir = new File(folderBean.getDir());
                mImages= Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        if (s.endsWith(".jpg")
                                ||s.endsWith(".jpeg")
                                ||s.endsWith(".png")){
                            return true;
                        }
                        return false;
                    }
                }));
                adapter = new GridViewAdapter(GridViewActivity.this,
                        mImages,mCurrentDir.getAbsolutePath());
                gridView.setAdapter(adapter);
                gridItemClick(adapter);
                imageSum.setText(mImages.size()+"张");
                album.setText(folderBean.getName());
                window.dismiss();
            }
        });
    }

    /**
     * 内容区变亮
     */
    private void lightOn() {
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha =1.0f;
        getWindow().setAttributes(lp);
    }
    /**
     * 内容区域变亮
     */
    private void lightOff() {
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha =0.3f;
        getWindow().setAttributes(lp);
    }
    /**
     * 将数据绑定到GridView
     */
    private void DataToView() {
        if (mCurrentDir == null){
            Toast.makeText(this,"未扫描到任何图片",Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mImages = Arrays.asList(mCurrentDir.list());
        adapter= new GridViewAdapter(this,mImages,mCurrentDir.getAbsolutePath());
        gridView.setAdapter(adapter);
        gridItemClick(adapter);
        imageSum.setText(mMaxCount+"个");
        album.setText(mCurrentDir.getName());

    }

    private void gridItemClick(GridViewAdapter adapter) {
        adapter.setOnGridViewListItemSelectListener(
                new GridViewAdapter.OnGridViewListItemSelectListener() {
            @Override
            public void onSelect(String path) {
                Log.d("TAG",path);
                if (path.equals("")){
                    Uri imageUri;
                    File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
                    imageSelectPath = outputImage.getAbsolutePath();
                    try {
                        if (outputImage.exists()){
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT>=24){
                        imageUri = FileProvider.getUriForFile(GridViewActivity.this,
                                "com.example.cameraalbumtest.fileprovider",outputImage);
                    }else {
                        imageUri = Uri.fromFile(outputImage);
                    }
                    //启动相机程序
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,TAKE_PHOTO);
                }else {
                    Intent intent = new Intent(GridViewActivity.this,ImageCutActivity.class);
                    intent.putExtra("imagePath",path);
                    startActivityForResult(intent,100);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        initView();
        getImageResources();
        initEvent();
    }

    private void initEvent() {
        albumSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.setAnimationStyle(R.style.dir_popupwindow_anim);
                window.showAsDropDown(mBottomLy,0,0);
                lightOff();
            }
        });
    }

    /**
     * 获取照片资源
     */
    private void getImageResources() {
        //1、检查读取相册的权限是否获取
        getPermission();
    }

    /**
     *读取照片资源
     */
    private void getImageFormSD() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this,"存储卡不可用",Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialog.show(this,null,"正在加载...");
        new Thread(){
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = GridViewActivity.this.getContentResolver();
                Cursor cursor=cr.query(mImageUri,new String[]{MediaStore.Images.Media.DATA},
                        MediaStore.Images.Media.MIME_TYPE +"= ? or "+
                                MediaStore.Images.Media.MIME_TYPE +"= ?",
                        new String[]{"image/jpeg","image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                Set<String> mDirPaths = new HashSet<String>();
                while (cursor.moveToNext()){
                    String path = cursor.getString(cursor.getColumnIndex(
                            MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null){
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    FolderBean folderBean = null;
                    if (mDirPaths.contains(dirPath)){
                        continue;
                    }else {
                        mDirPaths.add(dirPath);
                        folderBean = new FolderBean();
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImaPath(path);
                    }
                    if (parentFile.list()==null){
                        continue;
                    }
                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            if (s.endsWith(".jpg")||
                                    s.endsWith(".jpeg")
                                    ||s.endsWith(".png")) {
                                return true;
                            }
                            return false;
                        }

                    }).length;
                    folderBean.setCount(picSize);
                    mFolderBeans.add(folderBean);
                    if (picSize>mMaxCount){
                        mMaxCount=picSize;
                        mCurrentDir = parentFile;
                    }
                }
                cursor.close();
                //mDirPaths = null;//扫描完成，释放临时变量的内存
                mHandler.sendEmptyMessage(DATA_LOAD);//通知Handler扫描图片完成

            };
        }.start();
    }

    /**
     * 检查读取相册的权限是否获取
     */
    public void getPermission(){
        if (ContextCompat.checkSelfPermission(GridViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有权限，申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(GridViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //以前被拒绝授予权限，而且不再提示
                Toast.makeText(getApplicationContext(),"以前被拒绝授予权限",Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(GridViewActivity.this)
                        .setMessage("app需要开启权限才能使用此功能")
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + GridViewActivity.this.getPackageName()));
                                GridViewActivity.this.startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(GridViewActivity.this,"取消获取权限",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
            } else {
                //申请权限
                Log.i("Alex","准备首次申请权限");
                ActivityCompat.requestPermissions(GridViewActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
            }
        } else {
            //已经拥有权限
        }
        if (ContextCompat.checkSelfPermission(GridViewActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(GridViewActivity.this, new String[]{Manifest.permission.CAMERA}, 2001);
        }
        //2、读取照片资源
        getImageFormSD();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        toolbarTitle = findViewById(R.id.normal_title);
        toolbarTitle.setText("头像选择");
        gridView = findViewById(R.id.gridView_image_list);
        mBottomLy = findViewById(R.id.gridView_bottom);
        albumSelect= findViewById(R.id.album_select);
        imageSum = findViewById(R.id.image_sum);
        album = findViewById(R.id.album_select_text);
        back= findViewById(R.id.normal_back);
        back.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    Intent intent = new Intent(GridViewActivity.this,ImageCutActivity.class);
                    intent.putExtra("imagePath",imageSelectPath);
                    startActivityForResult(intent,100);
                }
                break;
            case 100:
                if (resultCode==101){
                    Intent intent = new Intent(GridViewActivity.this,MainActivity.class);
                    intent.putExtra("url",data.getStringExtra("url"));
                    setResult(11,intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.normal_back:
                finish();
            default:
                break;
        }
    }
}
