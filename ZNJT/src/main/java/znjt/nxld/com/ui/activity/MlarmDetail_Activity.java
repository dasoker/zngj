package znjt.nxld.com.ui.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import znjt.nxld.com.R;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.ui.view.MyImageView;

/**
 * Created by zhengzhihua on 2018/9/10.11:24
 * Update 2018/9/10 11:24
 * Describe
 */

public class MlarmDetail_Activity extends AppCompatActivity {
    private static final String TAG="MlarmDetail_Activity";
    private String alarmid;
    private MyImageView alarmimage;
    private VideoView malarmvideo;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      mlocationlistener=new LocationClient(getApplicationContext());
   //     mlocationlistener.registerLocationListener(new MylocationListener());
        SDKInitializer.initialize(getApplicationContext());//初始化SDK
        setContentView(R.layout.alarmdetail_layout);
        alarmid=getIntent().getStringExtra("id");
        getdata();
        findviewbyid();
    }

    protected void findviewbyid(){
        alarmimage=findViewById(R.id.image_text);
        malarmvideo=findViewById(R.id.video_text);
        mMapView =(MapView)findViewById(R.id.mmaps);
        mBaiduMap =mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        MyLocationData locData = new MyLocationData.Builder()
                .latitude(38)//
                .longitude(106)//
                .build();
        // 设置定位数据

        mBaiduMap.setMyLocationData(locData);//给地图设置定位数据，这样地图就显示位置了
        LatLng ll=new LatLng(38,106);
        MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(update);
        initview();
    }

    protected void initview(){
        alarmimage.setImageURL("http://114.115.143.146:8080/hsjZfzj/1534742739640-1.png");
        //设置视频控制器
        malarmvideo.setMediaController(new MediaController(this));

        //播放完成回调
        malarmvideo.setOnCompletionListener( new MyPlayerOnCompletionListener());

        //设置视频路径
        //videoView.setVideoURI(uri);
        malarmvideo.setVideoPath("http://streamdl.ydstatic.com/private/xuetang/pushstation.5335831a4707_screen_merge_49eba987.mp4");

        //开始播放视频
        malarmvideo.start();
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText( MlarmDetail_Activity.this, "播放完成了", Toast.LENGTH_SHORT).show();
        }
    }

      private void getdata(){
          JSONObject jsonObject=new JSONObject();
          try {
              jsonObject.put("warnId",alarmid);             //---------------------------------------------------------------
          } catch (JSONException e) {
              e.printStackTrace();
          }
          new Thread(new Runnable() {
            @Override
            public void run() {
                Httputil.Okhttppost(GlobalConfig.ALARMDETAIL, jsonObject.toString(), new BackHander() {
                    @Override
                    public void onexception(Exception e) {

                    }

                    @Override
                    public void onFail(Object response) {

                    }

                    @Override
                    public void onsuccess(Object response) {

                    }
                });
            }
        }).start();

      }

}
