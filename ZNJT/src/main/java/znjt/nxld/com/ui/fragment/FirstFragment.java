package znjt.nxld.com.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import znjt.nxld.com.R;
import znjt.nxld.com.ui.view.MyImageView;

/**
 * Created by zhengzhihua on 2018/9/11.17:42
 * Update 2018/9/11 17:42
 * Describe
 */

public class FirstFragment extends Fragment {
    private String url;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url=getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, null);
        MyImageView myImageView=view.findViewById(R.id.image);
        //设置视频控制器
        myImageView.setImageURL(url);
        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
