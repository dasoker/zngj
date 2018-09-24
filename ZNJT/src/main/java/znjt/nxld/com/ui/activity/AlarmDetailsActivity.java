package znjt.nxld.com.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import znjt.nxld.com.R;
import znjt.nxld.com.bean.AlarmItem;
import znjt.nxld.com.config.GlobalConfig;
import znjt.nxld.com.config.Httputil;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.ui.adapter.AlarmAdapter;
import znjt.nxld.com.ui.view.CircleDisplay;
import znjt.nxld.com.util.TimeUtil;

/**
*@Author:dingkuirong
*@date:2018/9/10 11:17
*@Description:告警类型详情页面
*/
public class AlarmDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private List<AlarmItem> alarmItemList = new ArrayList<AlarmItem>();
    private ArrayList<HashMap<String, Object>> listItem;
    private String carNo;
    private DatePickerDialog datePickerDialog;
    private LinearLayout Calendar_commit,totaldate_view;
    private Handler mHandler = new Handler();
    private AlarmAdapter mAlarmAdapter;
    private boolean mIsScrolling=false;
    private TextView title,totaldata_text;
    private LinearLayoutManager linearLayoutManager;
    private TextView yearview,monthview,dayview;   //年view、月view、日view
    private String yeartext,monthtext,daytext;
    private Button frontbutton,nextbutton;    //前一天view、后一天view
    private TextView back;
    private ImageButton imageButtonBack;
    private CircleDisplay circleDisplay;
    private LinearLayout detailsTitle;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        carNo = getIntent().getStringExtra("carNo");
        initView();
        initData();
    }

    /**
     * 获取详情信息
     */
    private void initData() {

        getDate();
        new Thread(){
            @Override
            public void run() {
                String url= GlobalConfig.SERVER_ADDRESS+"/findWarnByCarNo"
                        +GlobalConfig.LAST_WORD;
                JSONObject json = new JSONObject();
                try {
                    String name=pref.getString("name","");
                    json.put("carNo",carNo);
                    json.put("phoneNum",name);
                    json.put("time",yeartext+"-"+monthtext+"-"+daytext);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Httputil.Okhttppost(url, json.toString(), new BackHander() {
                    @Override
                    public void onexception(Exception e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AlarmDetailsActivity.this,"请求错误",
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
                                Toast.makeText(AlarmDetailsActivity.this,"服务器发生错误",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onsuccess(Object response) {
                        try {
                            JSONObject rJson = new JSONObject(response.toString());
                            JSONObject body = (JSONObject) rJson.get("body");
                            JSONObject data= (JSONObject) body.get("data");
                            JSONArray jsonArray = data.getJSONArray("warnList");
                            for (int i = 0;i<jsonArray.length();i++){
                                AlarmItem  alarmItem = new AlarmItem();
                                alarmItem.setId(jsonArray.getJSONObject(i)
                                        .getString("warnid"));
                                alarmItem.setType(jsonArray.getJSONObject(i)
                                        .getString("warnName"));
                                alarmItem.setStartTime(jsonArray.getJSONObject(i)
                                        .getString("begintime"));
                                alarmItem.setCarNum(carNo);
                                alarmItem.setSpeed(jsonArray.getJSONObject(i)
                                        .getString("carspeed"));
                                alarmItemList.add(alarmItem);

                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    initEvent();
                                    totaldata_text.setText(String.valueOf(alarmItemList.size()));
                                    detailsTitle.setVisibility(View.VISIBLE);
                                    circleDisplay.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
//        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        linearLayoutManager=new LinearLayoutManager(AlarmDetailsActivity.this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAlarmAdapter=new AlarmAdapter(alarmItemList);
        mAlarmAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAlarmAdapter);
        mAlarmAdapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String id = alarmItemList.get(position).getId();
                Intent intent = new Intent(AlarmDetailsActivity.this,A_alarmTablayout_Activity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

    }

    /**
     * 初始化页面
     */
    private void initView() {
        title = findViewById(R.id.normal_title);
        title.setText("adas详情界面");
        back = findViewById(R.id.normal_back_text_view);
        back.setOnClickListener(this);
        Calendar_commit=findViewById(R.id.Calendar_commit);
        imageButtonBack = findViewById(R.id.normal_back);
        imageButtonBack.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.alarm_item_recycleView);
        detailsTitle = findViewById(R.id.details_title);
        totaldate_view=findViewById(R.id.totaldata_view);
        totaldata_text=findViewById(R.id.totaldata_text);
        //日历头绑定view  yearview,monthview,dayview  frontbutton,nextbutton
        yearview=findViewById(R.id.date_year_text);
        monthview=findViewById(R.id.date_month_text);
        dayview=findViewById(R.id.date_day_text);
        frontbutton=findViewById(R.id.front_day_text);
        nextbutton=findViewById(R.id.next_day_text);
        Calendar_commit.setOnClickListener(this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {     //recycleview 滑动监听
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_SETTLING){
                    //屏幕惯性滚动
                    mIsScrolling = true;
                }else if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    //手指触摸滚动
                    mIsScrolling = false;
                }else{//这个idle状态在数据刷新后再调用，导致无法及时更新，所以在这调用多一次notifydatachange
                    mIsScrolling = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(mIsScrolling){
                if(dy>0){  //向上滑动
                    System.out.print(dx);
                        totaldate_view.setVisibility(View.GONE);
                    }else
                    if(dy<0){   //向下滑动
                        totaldate_view.setVisibility(View.VISIBLE);
                    }
            }}
        });

        setDate();
        if((TimeUtil.getToday()-1)<1){
            frontbutton.setEnabled(false);
        }else{
            frontbutton.setEnabled(true);
        }
        if((Integer.parseInt((String) dayview.getText())+1)>(TimeUtil.getCurrentMonthLastDay(TimeUtil.getYear(),Integer.parseInt(monthview.getText().toString())))){
            nextbutton.setEnabled(false);
        }else{
            nextbutton.setEnabled(true);
        }
        frontbutton.setOnClickListener(this);
        nextbutton.setOnClickListener(this);

        circleDisplay = findViewById(R.id.alarm_item_circle_display);
        circleDisplay.setColor(Color.GRAY);
        circleDisplay.setAnimDuration(5000);
        circleDisplay.setValueWidthPercent(55f);
        circleDisplay.setTextSize(36f);
        circleDisplay.setDrawText(true);
        circleDisplay.setDrawInnerCircle(true);
        circleDisplay.setFormatDigits(1);
        circleDisplay.setTouchEnabled(true);
        //circleDisplay.setSelectionListener(this);
        circleDisplay.setUnit("%");
        circleDisplay.setStepSize(0.5f);
        // circleDisplay.setCustomText(...); // sets a custom array of text
        circleDisplay.showValue(100f, 100f, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.normal_back:
                finish();
                break;
            case R.id.normal_back_text_view:
                finish();
                break;
            case R.id.front_day_text:

                if((Integer.parseInt(dayview.getText().toString())-1)<1){
                        frontbutton.setEnabled(false);
                    }else{
                    dayview.setText(String.valueOf(Integer.parseInt(dayview.getText().toString())-1));
                        frontbutton.setEnabled(true);
                    alarmItemList.clear();
                    initData();
                    mAlarmAdapter.notifyDataSetChanged();
                    }
                if((Integer.parseInt(monthview.getText().toString()))== TimeUtil.getMonth()) {
                    if ((Integer.parseInt(dayview.getText().toString())) >= TimeUtil.getToday()) {
                        nextbutton.setEnabled(false);
                    } else if ((Integer.parseInt((String) dayview.getText()) + 1) > (TimeUtil.getCurrentMonthLastDay(TimeUtil.getYear(), TimeUtil.getMonth()))) {
                        nextbutton.setEnabled(false);
                    } else {
                        nextbutton.setEnabled(true);
                    }
                }else if ((Integer.parseInt((String) dayview.getText()) + 1) > (TimeUtil.getCurrentMonthLastDay(TimeUtil.getYear(), Integer.parseInt(monthview.getText().toString())))) {
                nextbutton.setEnabled(false);
            } else {
                nextbutton.setEnabled(true);
            }

                break;
            case R.id.next_day_text:

        if((Integer.parseInt(monthview.getText().toString()))== TimeUtil.getMonth()) {
            if ((Integer.parseInt(dayview.getText().toString())) >= TimeUtil.getToday()) {
                nextbutton.setEnabled(false);
            } else if ((Integer.parseInt((String) dayview.getText()) + 1) > (TimeUtil.getCurrentMonthLastDay(TimeUtil.getYear(), TimeUtil.getMonth()))) {
                nextbutton.setEnabled(false);
            } else {
                dayview.setText(String.valueOf(Integer.parseInt(dayview.getText().toString()) + 1));
                nextbutton.setEnabled(true);
                alarmItemList.clear();
                initData();
                mAlarmAdapter.notifyDataSetChanged();
            }
        }else if ((Integer.parseInt((String) dayview.getText()) + 1) > (TimeUtil.getCurrentMonthLastDay(TimeUtil.getYear(),Integer.parseInt(monthview.getText().toString())))) {
            nextbutton.setEnabled(false);
        } else {
            dayview.setText(String.valueOf(Integer.parseInt(dayview.getText().toString()) + 1));
            nextbutton.setEnabled(true);
            alarmItemList.clear();
            initData();
            mAlarmAdapter.notifyDataSetChanged();
        }
                if((Integer.parseInt(dayview.getText().toString())-1)<1){
                    frontbutton.setEnabled(false);
                }else{
                    frontbutton.setEnabled(true);
                }
                break;
            case R.id.Calendar_commit:

                datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        yearview.setText(String.valueOf(year));
                        monthview.setText(String.valueOf(month+1));
                        dayview.setText(String.valueOf(dayOfMonth));
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                alarmItemList.clear();
                                initData();
                                mAlarmAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                }, TimeUtil.getYear(), TimeUtil.getMonth()-1, TimeUtil.getToday());
                datePickerDialog.show();

            default:
                break;
        }
    }
    /**
    *  @Description   设置时间
    *  @param:
    *  @return
    */
    protected void setDate(){
        yearview.setText(String.valueOf(TimeUtil.getYear()));
        monthview.setText(String.valueOf(TimeUtil.getMonth()));
        dayview.setText(String.valueOf(TimeUtil.getToday()));
    }
    /**
    *  @Description  获取时间
    *  @param:
    *  @return
    */
    protected void getDate(){
        yeartext=yearview.getText().toString();
        monthtext=monthview.getText().toString();
        daytext=dayview.getText().toString();
    }
}
