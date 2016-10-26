package com.lifucong.videoplayer.full;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lifucong.videoplayer.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Administrator on 2016/10/26.
 */

public class VideoViewActivity extends AppCompatActivity {
    private static final String KEY_VIDEO_PATH = "video_path";

    private VideoView videoView;
    private ImageView ivLoading;//缓冲图像
    private TextView tvBufferInfo;//缓冲信息（35%   300kb/s）

    private MediaPlayer mediaPlayer;
    private int bufferPercent;//缓冲百分比
    private int downloadSpeed;//下载速度


    //启动当前Activity
    public static void open(Context context, String videoPath){
        Intent intent = new Intent(context,VideoViewActivity.class);
        intent.putExtra(KEY_VIDEO_PATH,videoPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置背景色
        getWindow().setBackgroundDrawableResource(android.R.color.black);

        setContentView(R.layout.activity_video_view);

        //缓冲视图，视频视图
        initBufferView();
        initVideoView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //videoPath设置给videoview
        videoView.setVideoPath(getIntent().getStringExtra(KEY_VIDEO_PATH));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //VideoView释放掉
        videoView.stopPlayback();
        //videoview = mediaplayer + surfaceview
    }

    //初始化缓冲视图
    private void initBufferView(){
        tvBufferInfo = (TextView) findViewById(R.id.tvBufferInfo);
        ivLoading = (ImageView) findViewById(R.id.ivLoading);
        tvBufferInfo.setVisibility(View.INVISIBLE);
        ivLoading.setVisibility(View.INVISIBLE);
    }

    //初始化视频视图
    private void initVideoView(){
        Vitamio.isInitialized(this);//初始化vitamio!!!!
        videoView = (VideoView) findViewById(R.id.videoView);
        //设置视图控制器
        videoView.setMediaController(new MediaController(this));
        videoView.setKeepScreenOn(true);//视屏常亮
        videoView.requestFocus();//拿到焦点
        //缓冲监听三步走~
        //第一步,设置缓冲大小
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                mediaPlayer.setBufferSize(512 * 1024);
            }
        });
        //第二步，设置信息监听
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    //开始缓冲
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //显示缓冲视图
                        showBufferView();
                        if (videoView.isPlaying()){
                            videoView.pause();
                        }
                        return true;
                    //缓冲结束
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //隐藏缓冲视图
                        hideBufferView();
                        videoView.start();
                        return true;
                    //缓冲时，更新下载速率
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        downloadSpeed = extra;
                        //更新缓冲视图
                        updataBufferView();
                        return true;
                }
                return false;
            }
        });
        //第三步，缓冲更新监听（得到缓冲百分比）
        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                bufferPercent = percent;
                //更新缓冲视图
                updataBufferView();
            }
        });
    }

    //显示缓冲视图
    private void showBufferView(){
        tvBufferInfo.setVisibility(View.VISIBLE);
        ivLoading.setVisibility(View.VISIBLE);
        downloadSpeed = 0;
        bufferPercent = 0;
    }

    //隐藏缓冲视图
    private void hideBufferView(){
        tvBufferInfo.setVisibility(View.INVISIBLE);
        ivLoading.setVisibility(View.INVISIBLE);
    }

    //更新缓冲视图
    private void updataBufferView(){
        String info = bufferPercent + "%  " + downloadSpeed + "kb/s";
        tvBufferInfo.setText(info);
    }

}
