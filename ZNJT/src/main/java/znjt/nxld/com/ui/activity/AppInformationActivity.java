package znjt.nxld.com.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import znjt.nxld.com.R;

public class AppInformationActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_information);
        initView();
    }

    private void initView() {
        back = findViewById(R.id.normal_back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.normal_back:
                finish();
                break;
            default:
                break;
        }
    }
}
