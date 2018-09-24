package znjt.nxld.com.ui.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import znjt.nxld.com.R;

/**
 * Created by zhengzhihua on 2018/9/11.17:45
 * Update 2018/9/11 17:45
 * Describe
 */

public class SecondFragment extends Fragment {
    private String url;
    private  VideoView videoView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url=getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, null);
         videoView=view.findViewById(R.id.video1_text);
        //设置视频控制器
        videoView.setMediaController(new MediaController(getContext()));

        //播放完成回调
        videoView.setOnCompletionListener( new MyPlayerOnCompletionListener());

        //设置视频路径
        //videoView.setVideoURI(uri);
        videoView.setVideoPath(url);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //开始播放视频
        videoView.start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            videoView.pause();
        }else{
            videoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    //    videoView.pause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(getContext(), "播放完成了", Toast.LENGTH_SHORT).show();
        }
    }
}
