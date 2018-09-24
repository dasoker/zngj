package znjt.nxld.com.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import znjt.nxld.com.R;
    /**
    *@Author:dingkuirong
    *@date:2018/9/19 12:54
    *@Description:
    */
public class SupportActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mSupportSendRelativeLayout;
    private ImageView mShowSendRelative;
    private ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        initView();
    }

    private void initView() {
        mSupportSendRelativeLayout = findViewById(R.id.support_send_relative_layout);
        mShowSendRelative = findViewById(R.id.show_send_relative);
        mShowSendRelative.setOnClickListener(this);
        back = findViewById(R.id.normal_back);
        back.setOnClickListener(this);
    }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.show_send_relative:
                    if (mSupportSendRelativeLayout.getVisibility()==View.GONE){
                        mSupportSendRelativeLayout.setVisibility(View.VISIBLE);
                        mShowSendRelative.setImageResource(R.drawable.anniushouqi);
                    }else if (mSupportSendRelativeLayout.getVisibility()==View.VISIBLE){
                        mSupportSendRelativeLayout.setVisibility(View.GONE);
                        mShowSendRelative.setImageResource(R.drawable.anniuzhankai);
                    }
                    break;
                case R.id.normal_back:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
