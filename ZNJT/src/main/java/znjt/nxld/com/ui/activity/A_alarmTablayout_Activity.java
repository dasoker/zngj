package znjt.nxld.com.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;

import org.json.JSONException;
import org.json.JSONObject;

import znjt.nxld.com.R;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.ui.fragment.FirstFragment;
import znjt.nxld.com.ui.fragment.Fragments;
import znjt.nxld.com.ui.fragment.SecondFragment;
import znjt.nxld.com.ui.fragment.ThirdFragment;

/**
 * Created by zhengzhihua on 2018/9/11.15:26
 * Update 2018/9/11 15:26
 * Describe
 */

public class A_alarmTablayout_Activity extends AppCompatActivity{
    private static final String TAG="A_alarmTablayout_Activity";

    private TabLayout tableLayout;
    private ViewPager viewPager;
    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;
    private String alarmid,latitude,longitude;
    private String[] title = {"图片", "视频", "地图"};
    private Handler handler=new Handler(){
        public void handleMessage(Message mes){
            switch (mes.what){
                case 1:
                    String message= (String) mes.obj;
                    Toast.makeText(A_alarmTablayout_Activity.this,message, Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    String messages= (String) mes.obj;
                    Toast.makeText(A_alarmTablayout_Activity.this,messages,Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());//初始化SDK
        setContentView(R.layout.a_alarm_layout);
        alarmid=getIntent().getStringExtra("id");
        getdata();
        findviewbyid();
    }

    protected void findviewbyid(){
        tableLayout=findViewById(R.id.tab);
        viewPager=findViewById(R.id.vp_content);
        initPager();
    }

    // FIXME: 2018/9/12
    /**
    *  @Description 初始化viewpage
    *  @param:
    *  @return
    */
    private void initPager() {
      /*  viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = new Fragment();
                Bundle bundle=new Bundle();
                    switch (position) {
                        case 0:
                            firstFragment = new FirstFragment();
                            bundle.putString("url","http://114.115.143.146:8080/hsjZfzj/1534742739640-1.png");
                            firstFragment.setArguments(bundle);
                            fragment=(FirstFragment)firstFragment;
                            break;
                        case 1:
                            Fragments fragments = new Fragments();
                            bundle.putString("url","http://114.115.143.146:8080/hsjZfzj/1534495575447.mp4");
                            fragments.setArguments(bundle);
                            fragment=(Fragments)fragments;
                            break;
                        case 2:
                            thirdFragment = new ThirdFragment();
                            bundle.putString("latitude",latitude);
                            bundle.putString("longitude",longitude);
                            thirdFragment.setArguments(bundle);
                            fragment=(ThirdFragment)thirdFragment;
                            break;
                    }
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        tableLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        tableLayout.getTabAt(0).setCustomView(getTabView(0));
        tableLayout.getTabAt(1).setCustomView(getTabView(1));
        tableLayout.getTabAt(2).setCustomView(getTabView(2));

        initTab();
    }
    /**
    *  @Description 初始化tab
    *  @param:
    *  @return
    */
    private void initTab() {
        tableLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView textView = view.findViewById(R.id.txt_tab_task);

                textView.setTextColor(Color.parseColor("#000000"));
                if (textView.getText().toString().equals(title[0])) {

                    viewPager.setCurrentItem(0);
                } else if (textView.getText().toString().equals(title[1])) {

                    viewPager.setCurrentItem(1);
                } else if (textView.getText().toString().equals(title[2])) {

                    viewPager.setCurrentItem(2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView textView = view.findViewById(R.id.txt_tab_task);
                textView.setTextColor(Color.parseColor("#3F51B5"));
                if (textView.getText().toString().equals(title[0])) {

                    viewPager.setCurrentItem(0);
                } else if (textView.getText().toString().equals(title[1])) {

                    viewPager.setCurrentItem(1);
                } else if (textView.getText().toString().equals(title[2])) {

                    viewPager.setCurrentItem(2);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    /**
    *  @Description 初始化时显示第一个tab
    *  @param:
    *  @return
    */
    private View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_task_item, null);
        TextView textView = view.findViewById(R.id.txt_tab_task);

        textView.setText(title[position]);


        if (position == 0) {
            textView.setTextColor(Color.parseColor("#ed8200"));
        }
        return view;
    }
    /**
    *  @Description 通过网络获取服务器数据
    *  @param:
    *  @return
    */
    private void getdata(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("warnId",alarmid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Httputil.Okhttppost(GlobalConfig.ALARMDETAIL, jsonObject.toString(), new BackHander<String>() {
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
                                latitude=data.getString("latitude");
                                longitude=data.getString("longitude");
                                Message messages=handler.obtainMessage();
                                messages.obj=data.get("message");
                                messages.what=1;
                                handler.sendMessage(messages);
                            }else{
                                Message messages=handler.obtainMessage();
                                messages.obj=data.get("message");
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

}
