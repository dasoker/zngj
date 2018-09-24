package znjt.nxld.com.ui.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import znjt.nxld.com.R;
import znjt.nxld.com.base.BaseLazyLoadFragment;

/**
 * Created by zhengzhihua on 2018/9/15.16:21
 * Update 2018/9/15 16:21
 * Describe
 */

public class Fragments extends BaseLazyLoadFragment {
    private String url;
    private VideoView videoView;
    private View view;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url=getArguments().getString("url");
    }

    @Override
    public void onLazyLoad() {
        videoView.setVideoPath(url);
        videoView.start();

    }

    @Override
    public void onLazyLoads() {
       // videoView.pause();
        if(videoView!=null){
            if(videoView.isPlaying()){
                videoView.pause();
            }
        }

    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
         view = inflater.inflate(R.layout.fragment2, null);

        return view;
    }

    @Override
    public void initEvent() {
        videoView=view.findViewById(R.id.video1_text);
        //设置视频控制器
        videoView.setMediaController(new MediaController(getContext()));

        //播放完成回调
        videoView.setOnCompletionListener(new MyPlayerOnCompletionListener());

        //设置视频路径
        //videoView.setVideoURI(uri);

    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(getContext(), "播放完成了", Toast.LENGTH_SHORT).show();
        }
    }
}
