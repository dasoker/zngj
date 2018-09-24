package znjt.nxld.com.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import znjt.nxld.com.R;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.ui.view.MyImageView;
import znjt.nxld.com.util.TimeUtil;
import znjt.nxld.com.util.imageUtil.GridViewActivity;


/**
*@Author:dingkuirong
*@date:2018/9/4 9:33
*@Description:主页面
*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton menu;//个人信息菜单
    private DrawerLayout drawerLayout;
    private PieChart pieChart;//图表
    private LinearLayout toDayData;
    private LinearLayout mouthData;
    private LinearLayout yearData;
    private Button toAlarmDetails;
    private LineChart lineChart;
    private ViewPager adViewPager;
    private PagerAdapter viewAdapter;
    private List<View> mView=new ArrayList<View>();
    private Handler mHandler = new Handler();
    private TextView completeInformation;
    private int GO_OUT=1;
    private Runnable runnable;
    private String phoneNo;
    private TextView alarmSum;
    private TextView maxAlarmSumByType;
    private String sum;
    private JSONArray alarmTypeList ;
    private CircleImageView bt_add_photo;
    private TextView bt_add_photo_textView;
    private MyImageView advertiseImageViewOne;
    private MyImageView advertiseImageViewTwo;
    private MyImageView advertiseImageViewThree;
    private String adImageUrl1="http://114.115.143.146:8080/hsjZfzj/1534742739640-1.png";
    private String adImageUrl2="http://114.115.143.146:8080/hsjZfzj/1534742739640-1.png";
    private String adImageUrl3="http://114.115.143.146:8080/hsjZfzj/1534742739640-1.png";
    private String adUrl1="https://www.baidu.com";
    private String adUrl2="https://www.hupu.com";
    private String adUrl3="https://www.sina.com";
    private Spinner carSelect;
    private List<CharSequence> carList = null;
    private ArrayAdapter<CharSequence> carAdapter = null;
    private TextView mainBindCar;
    private SharedPreferences pref;
    private String advertisements;
    private TextView mainName;
    private TextView mainTelNum;
    private LinearLayout mMainAboutApp;
    private LinearLayout mMainSupport;
    private LinearLayout mFaceDetect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        initView();
        mainName.setText(pref.getString("name",""));
        mainTelNum.setText(pref.getString("name",""));
        Intent intent = getIntent();
        phoneNo = intent.getStringExtra("phoneNo");
        advertisements = intent.getStringExtra("advertisements");
        initAdvertisement();
        initCarData();
    }

    /**
     * 读取绑定的车辆信息
     */
    private void initCarData() {
        new Thread(){
            @Override
            public void run() {
                String url=GlobalConfig.SERVER_ADDRESS+"/findCarList"+GlobalConfig.LAST_WORD;
                JSONObject json = new JSONObject();
                try {
                    String name=pref.getString("name","");
                    json.put("phoneNum",name);
                    Httputil.Okhttppost(url, json.toString(), new BackHander() {
                        @Override
                        public void onexception(Exception e) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                     Toast.makeText(getApplicationContext(),"从服务器获取数据失败",
                                             Toast.LENGTH_SHORT).show();
                                }
                            });
                            e.printStackTrace();
                        }

                        @Override
                        public void onFail(Object response) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"服务器发生错误",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onsuccess(Object response) {
                            try {
                                JSONObject rJson = new JSONObject(response.toString());
                                JSONObject body = (JSONObject) rJson.get("body");
                                JSONObject data = (JSONObject) body.get("data");
                                JSONArray carNoList = (JSONArray) data.get("carNoList");
                                carList = new ArrayList<CharSequence>();
                                for(int i =0;i<carNoList.length();i++){
                                    JSONObject car = (JSONObject) carNoList.get(i);
                                    carList.add((CharSequence) car.get("carNo"));
                                }
                                carAdapter = new ArrayAdapter<CharSequence>(MainActivity.this,android.R.layout.simple_spinner_item,carList);
                                carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        carSelect.setAdapter(carAdapter);
                                        carSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    initUserData();
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取登录用户信息
     * @param
     */
    private void initUserData() {
       new Thread(){
           @Override
           public void run() {
               String url = GlobalConfig.SERVER_ADDRESS+"/initUserData";
               JSONObject json = new JSONObject();
               try {
                   String name=pref.getString("name","");
                   String carNo = (String) carSelect.getSelectedItem();
                   json.put("carNo",carNo);
                   json.put("phoneNum",name);
                  if(carNo==null){
                      mHandler.post(new Runnable() {
                          @Override
                          public void run() {
                              Toast.makeText(getApplicationContext(),
                                      "没有绑定车辆，无法获取到数据",
                                      Toast.LENGTH_SHORT).show();
                          }
                      });
                  }else {
                      Httputil.Okhttppost(url, json.toString(), new BackHander<String>() {
                          @Override
                          public void onexception(Exception e) {
                              mHandler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(MainActivity.this
                                              ,"未连接到服务器，获取车辆详细数据失败",
                                              Toast.LENGTH_SHORT).show();
                                  }
                              });
                              e.printStackTrace();
                          }

                          @Override
                          public void onFail(String response) {
                              mHandler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(MainActivity.this
                                              ,".服务器发生错误，获取车辆详细数据失败",
                                              Toast.LENGTH_SHORT).show();
                                  }
                              });
                          }


                          @Override
                          public void onsuccess(String response) {
                              try {
                                  JSONObject rJson = new JSONObject(response.toString());
                                  JSONObject body = (JSONObject) rJson.get("body");
                                  JSONObject data = (JSONObject) body.get("data");
                                  sum = data.get("warnSum").toString();
                                  alarmTypeList = data.getJSONArray("sumList");
                                  mHandler.post(new Runnable() {
                                      @Override
                                      public void run() {
                                          alarmSum .setText(sum+"个");
                                          initChart(1);
                                      }
                                  });
                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }
                              //  alarmSum.setText();
                          }
                      });
                  }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }.start();
    }

    /**
     * 初始化图表
     */
    private void initChart(int type) {
        initLinearLayout();
        initChartType();
        switch (type){
            case 1:
                getThisDayData();
                break;
            case 2:
                getThisMouthData();
                break;
            case 3:
               getThisYearData();
                break;
            default:
                break;
        }
    }

    /**
     * 获取当前天的数据
     */
    private void getThisDayData() {
        pieChart.setVisibility(View.VISIBLE);
        toDayData.setBackgroundColor(Color.GRAY);
        List<PieEntry> entries = new ArrayList<PieEntry>();
        int maxSum=0;
        String maxType="";
        if (alarmTypeList==null){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"此车辆今日没有相关数据",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            for (int i = 0 ;i<alarmTypeList.length();i++){
                String typeSum = "";
                String warnType="";
                try {
                    warnType = (String) alarmTypeList.getJSONObject(i).get("warntype");
                    typeSum = (String) alarmTypeList.getJSONObject(i).get("sum");
                    if (Integer.parseInt(typeSum)>maxSum){
                        maxSum=Integer.parseInt(typeSum);
                        maxType = warnType;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                entries.add(new PieEntry(Float.parseFloat(typeSum),warnType));
            }
            maxAlarmSumByType.setText(maxType+"/"+maxSum+"个");
            PieDataSet dataSet = new PieDataSet(entries,"报警类型");
            dataSet.setColors(new int[] { Color.GREEN, Color.YELLOW, Color.BLUE,Color.RED,
                    Color.GRAY ,Color.CYAN,Color.DKGRAY,Color.LTGRAY,Color.MAGENTA},255);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(20f);
            PieData pieData = new PieData(dataSet);
            pieChart.setData(pieData);
            Description description=new Description();
            description.setText("本日每种类型的数量");
            pieChart.setDescription(description);
            pieChart.setDrawEntryLabels(true);
            pieChart.setNoDataText("未获取到相关数据");
            pieChart.invalidate(); // refresh刷新
        }
    }

    /**
     * 获取当前年份的数据
     */
    private void getThisYearData() {
        lineChart.setVisibility(View.VISIBLE);
        List<Entry> entriesLineTwo = new ArrayList<Entry>();
        for (int i = 0 ; i < 12;i++){
            entriesLineTwo.add(new Entry(i+1, (float) (Math.random()*100)));
        }
        LineDataSet dataSetLineTwo = new LineDataSet(entriesLineTwo, "月报警数量总数"); // add entries to dataset
        dataSetLineTwo.setColor(Color.BLUE);
        dataSetLineTwo.setValueTextColor(Color.BLACK);
        LineData lineDataTwo = new LineData(dataSetLineTwo);
        lineChart.setData(lineDataTwo);
        Description description=new Description();
        description.setText("本年度每月的报警数量总和");
        lineChart.setDescription(description);
        lineChart.invalidate();
        lineChart.setVisibility(View.VISIBLE);
        yearData.setBackgroundColor(Color.GRAY);
    }

    /**
     * 获取本月数据
     */
    private void getThisMouthData() {
        mouthData.setBackgroundColor(Color.GRAY);
        new Thread(){
            @Override
            public void run() {
                String url = GlobalConfig.SERVER_ADDRESS+"/initUserMonth"
                        +GlobalConfig.LAST_WORD;
                JSONObject json=new JSONObject();
                try {
                    String name=pref.getString("name","");
                    String carNo = (String) carSelect.getSelectedItem();
                    json.put("carNo",carNo);
                    json.put("phoneNum",name);
                    json.put("time",TimeUtil.getYear()+"-"+TimeUtil.getMonth()+"-"
                    +TimeUtil.getToday());
                    Httputil.Okhttppost(url, json.toString(), new BackHander<String>() {
                        @Override
                        public void onexception(Exception e) {
                            e.printStackTrace();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,
                                            "无法获取到月数据，链接不到服务器",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFail(String response) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,
                                            "无法获取到月数据，服务器发生错误",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onsuccess(String response) {
                            Log.d("tag",response);
                            try {
                                JSONObject rJson = new JSONObject(response);
                                JSONObject body = (JSONObject) rJson.get("body");
                                JSONObject data = (JSONObject) body.get("data");
                                JSONArray sumList = data.getJSONArray("sumList");
                                List<Entry> entriesLine = new ArrayList<Entry>();
                                for (int i = 0 ; i < sumList.length();i++){
                                    JSONObject item = sumList.getJSONObject(i);
                                    entriesLine.add(new Entry(i+1, Float.parseFloat(String.valueOf(item.get("sum")))));
                                }
                                LineDataSet dataSetLine = new LineDataSet(entriesLine, "日报警数量总数"); // add entries to dataset
                                dataSetLine.setColor(Color.BLUE);
                                dataSetLine.setValueTextColor(Color.BLACK);
                                final LineData lineData = new LineData(dataSetLine);
                                mHandler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       lineChart.setVisibility(View.VISIBLE);
                                       lineChart.setData(lineData);
                                       Description description=new Description();
                                       description.setText("本月每天的报警数量总和");
                                       lineChart.setDescription(description);
                                       lineChart.invalidate();
                                   }
                               });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void initChartType() {
        pieChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.GONE);
    }

    private void initLinearLayout() {
        toDayData.setBackgroundColor(Color.WHITE);
        mouthData.setBackgroundColor(Color.WHITE);
        yearData.setBackgroundColor(Color.WHITE);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        mFaceDetect = findViewById(R.id.main_face_detect);
        mFaceDetect.setOnClickListener(this);
        mMainSupport = findViewById(R.id.main_support);
        mMainSupport.setOnClickListener(this);
        mMainAboutApp = findViewById(R.id.main_about_app);
        mMainAboutApp.setOnClickListener(this);
        mainName = findViewById(R.id.main_name);
        mainTelNum = findViewById(R.id.main_tel_number);
        mainBindCar  =findViewById(R.id.main_bind_car);
        mainBindCar.setOnClickListener(this);
        carSelect = findViewById(R.id.main_select_car);
        menu = findViewById(R.id.main_toolbar_menu);
        menu.setOnClickListener(this);
        bt_add_photo = findViewById(R.id.profile_photo);
        bt_add_photo.setOnClickListener(this);
        bt_add_photo_textView = findViewById(R.id.profile_photo_text);
        bt_add_photo_textView.setOnClickListener(this);
        drawerLayout = findViewById(R.id.main_drawer_layout);
        pieChart = findViewById(R.id.pie_chart);
        lineChart = findViewById(R.id.line_chart);
        toDayData = findViewById(R.id.today_chart);
        toDayData.setOnClickListener(this);
        mouthData = findViewById(R.id.mouth_chart);
        mouthData.setOnClickListener(this);
        yearData = findViewById(R.id.year_chart);
        yearData.setOnClickListener(this);
        toAlarmDetails = findViewById(R.id.to_alarm_detail);
        toAlarmDetails.setOnClickListener(this);
        adViewPager = findViewById(R.id.main_ad_view_pager);
        alarmSum = findViewById(R.id.alarm_item_sum);
        maxAlarmSumByType = findViewById(R.id.alarm_item_max_sum);
        LayoutInflater mInflater=LayoutInflater.from(this);
        View ad1=mInflater.inflate(R.layout.advertisement_content_one,null);
        View ad2=mInflater.inflate(R.layout.advertisement_content_two,null);
        View ad3=mInflater.inflate(R.layout.advertisement_content_three,null);
        advertiseImageViewOne = ad1.findViewById(R.id.advertise_one);
        advertiseImageViewOne.setOnClickListener(this);
        advertiseImageViewTwo = ad2.findViewById(R.id.advertise_two);
        advertiseImageViewTwo.setOnClickListener(this);
        advertiseImageViewThree = ad3.findViewById(R.id.advertise_three);
        advertiseImageViewThree.setOnClickListener(this);
        mView.add(ad1);
        mView.add(ad2);
        mView.add(ad3)  ;
        viewAdapter=new PagerAdapter() {
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view=mView.get(position );
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(mView.get(position));
            }

            @Override
            public int getCount() {
                return mView.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view==object;
            }
        };
        adViewPager.setAdapter(viewAdapter);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentPosition = adViewPager.getCurrentItem();

                if(currentPosition == adViewPager.getAdapter().getCount() - 1){
                    // 最后一页
                    adViewPager.setCurrentItem(0);
                }else{
                    adViewPager.setCurrentItem(currentPosition + 1);
                }
                // 一直给自己发消息
                mHandler.postDelayed(this,5000);
            }
        },5000);
        completeInformation = findViewById(R.id.main_complete_information);
        completeInformation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_toolbar_menu:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.main_about_app:
                startActivity(new Intent(MainActivity.this,AppInformationActivity.class));
                break;
            case R.id.main_support:
                startActivity(new Intent(MainActivity.this,SupportActivity.class));
                break;
            case R.id.today_chart:
                initChart(1);
                break;
            case R.id.mouth_chart:
                initChart(2);
                break;
            case R.id.year_chart:
                initChart(3);
                break;
            case R.id.to_alarm_detail:
                if ((String) carSelect.getSelectedItem()!=null) {
                    Intent intent = new Intent(MainActivity.this, AlarmDetailsActivity.class);
                    intent.putExtra("carNo", (String) carSelect.getSelectedItem());
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"未绑定车辆数据",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.main_complete_information:
                startActivity(new Intent(MainActivity.this,WriteInformatin_Activity.class));
                break;
           case R.id.profile_photo:
                Intent intent1 = new Intent(this,GridViewActivity.class);
                startActivityForResult(intent1,10);
                break;
            case R.id.profile_photo_text:
                Intent intent2 = new Intent(this,GridViewActivity.class);
                startActivityForResult(intent2,10);
                break;
            case R.id.advertise_one:
                Uri uri1 = Uri.parse(adUrl1);
                Intent intentBrowser1 = new Intent(Intent.ACTION_VIEW, uri1);
                //intent.setClassName("com.UCMobile","com.uc.browser.InnerUCMobile");//打开UC浏览器
                //intent.setClassName("com.tencent.mtt","com.tencent.mtt.MainActivity");//打开QQ浏览器
                startActivity(intentBrowser1);
                break;
            case R.id.advertise_two:
                Uri uri2 = Uri.parse(adUrl2);
                Intent intentBrowser2 = new Intent(Intent.ACTION_VIEW, uri2);
                startActivity(intentBrowser2);
                break;
            case R.id.advertise_three:
                Uri uri3 = Uri.parse(adUrl3);
                Intent intentBrowser3 = new Intent(Intent.ACTION_VIEW, uri3);
                startActivity(intentBrowser3);
                break;
            case R.id.main_bind_car:
                startActivity(new Intent(MainActivity.this,Addequipment_Activity.class));
                break;
            case R.id.main_face_detect:
                startActivity(new Intent(MainActivity.this,FaceDetectActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (GO_OUT==1){
            mHandler.removeCallbacks(runnable);
            Toast.makeText(getApplicationContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
            timeCount();
            GO_OUT++;
        }else if (GO_OUT==2){
            finish();
        }
    }
    /**
     * 定时器
     */
    private void timeCount() {
        runnable=new Runnable() {
            @Override
            public void run() {
               GO_OUT=1;
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(runnable, 1000);
    }
  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Alex","mainActivity的onActivityResult req="+requestCode+"    result="+requestCode);
        if(data == null || resultCode != SelectPhotoActivity.SELECT_PHOTO_OK)return;
        boolean isFromCamera = data.getBooleanExtra("isFromCamera",false);
        ArrayList<SelectPhotoAdapter.SelectPhotoEntity> selectedPhotos = data.getParcelableArrayListExtra("selectPhotos");
        Log.i("Alex","选择的图片是"+selectedPhotos);
    }
*/

    @Override
    protected void onRestart() {
        initCarData();
        initUserData();
        super.onRestart();
    }

    public void initAdvertisement(){
        try {
            JSONArray ads = new JSONArray(advertisements);
            for (int i = 0;i<ads.length();i++){
                JSONObject ad = ads.getJSONObject(i);
                if (i==0){
                    adImageUrl1 = GlobalConfig.SERVER+ad.getString("adImagePath");
                    adUrl1 = ad.getString("adLinks");
                }
                if (i==1){
                    adImageUrl2 = GlobalConfig.SERVER+ad.getString("adImagePath");
                    adUrl2 = ad.getString("adLinks");
                }
                if (i==2){
                    adImageUrl3 = GlobalConfig.SERVER+ad.getString("adImagePath");
                    adUrl3 = ad.getString("adLinks");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyImageView myImageView1 = (MyImageView) advertiseImageViewOne;
        MyImageView myImageView2 = (MyImageView) advertiseImageViewTwo;
        MyImageView myImageView3 = (MyImageView) advertiseImageViewThree;
        myImageView1.setImageURL(adImageUrl1);
        myImageView2.setImageURL(adImageUrl2);
        myImageView3.setImageURL(adImageUrl3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 10:
                if (resultCode==11){
                    File file = new File(data.getStringExtra("url"));
                    Uri uri = Uri.fromFile(file);
                    bt_add_photo.setImageURI(uri);
                }
             break;
         default:
             break;
        }
    }
}
