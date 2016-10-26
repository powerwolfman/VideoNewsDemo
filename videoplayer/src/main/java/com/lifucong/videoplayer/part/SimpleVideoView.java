package com.lifucong.videoplayer.part;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lifucong.videoplayer.R;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * Created by Administrator on 2016/10/25.
 */

public class SimpleVideoView extends FrameLayout {

    private static final int PROGRESS_MAX = 1000;//进度条控制（长短，进度）

    private String videoPath;//播放视频的url
    private boolean isPlaying;//是否正在播放
    private boolean isPrepared;//是否准备好

    private MediaPlayer mediaplayer;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageView ivPreView;//预览图
    private ImageButton btnToggle;//播放，暂停按钮
    private ProgressBar progressBar;//进度条
    private ImageButton btnFullScreen;

    public SimpleVideoView(Context context) {
        super(context);
    }

    public SimpleVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isPlaying) {
                //每200毫秒更新一次播放进度
                int progress = (int) (mediaplayer.getCurrentPosition() * PROGRESS_MAX / mediaplayer.getDuration());
                progressBar.setProgress(progress);
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };

    //视图初始化，只在构造方法中调用一次
    private void init() {
        //Vitamio的初始化
        Vitamio.isInitialized(getContext());
        //Inflate初始当前视图内容
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player, this, true);
        initSurfaceView(); // 初始化SurfaceView
        initControllerViews(); // 初始化视频播放控制视图
    }

    // 初始化视频播放控制视图
    private void initControllerViews() {
        //预览图
        ivPreView = (ImageView) findViewById(R.id.ivPreview);
        //播放暂停按钮
        btnToggle = (ImageButton) findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaplayer.isPlaying()) {
                    pauseMediaPlayer();
                } else if (isPrepared) {
                    startMediaPlayer();
                } else {
                    Toast.makeText(getContext(), "Can't play now!", Toast.LENGTH_LONG).show();
                }
            }
        });
        //进度条
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(PROGRESS_MAX);
        //全屏播放按钮
        btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);
        btnFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 全屏未实现
                Toast.makeText(getContext(), "全屏未实现", Toast.LENGTH_LONG).show();
            }
        });
    }

    //点击暂停时调用的方法，更新UI
    private void startMediaPlayer() {
        ivPreView.setVisibility(View.GONE);//预览图隐藏
        btnToggle.setImageResource(R.drawable.ic_pause);
        mediaplayer.start();//开始播放
        isPlaying = true;
        handler.sendEmptyMessage(0);
    }

    //点击暂停时调用的方法，更新UI
    private void pauseMediaPlayer() {
        if (mediaplayer.isPlaying()) {
            mediaplayer.pause();
        }
        isPlaying = false;
        btnToggle.setImageResource(R.drawable.ic_play_arrow);
        handler.sendEmptyMessage(0);
    }

    // 初始化SurfaceView
    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        //设置PixelFormat,防止花屏
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    //对播放资源进行设置
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    // 与Activity状态保持同步
    // 用来初始状态
    public void onResume() {
        initMediaPlayer(); // 初始化MediaPlayer，设置一系列监听器
        prepareMediaPlayer(); // 准备MediaPlayer，同时更新UI状态
    }

    // 准备MediaPlayer，同时更新UI状态
    private void prepareMediaPlayer() {
        try {
            mediaplayer.reset();//重置
            mediaplayer.setDataSource(videoPath);//设置数据源
            mediaplayer.setLooping(true);//循环播放
            mediaplayer.prepareAsync();
            //预览图可见
            ivPreView.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 初始化MediaPlayer，设置一系列监听器
    private void initMediaPlayer() {
        mediaplayer = new MediaPlayer(getContext());
        mediaplayer.setDisplay(surfaceHolder);
        //准备情况的监听
        mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                startMediaPlayer();
            }
        });
        //处理audio监听
        mediaplayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_FILE_OPEN_OK) {
                    mediaplayer.audioInitedOk(mediaplayer.audioTrackInit());
                    return true;
                }
                return false;
            }
        });
        //设置videosize变化监听
        mediaplayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                int layoutWidht = surfaceView.getWidth();
                int layoutHeight = layoutWidht * height / width;
                // 更新surfaceView的size
                ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
                layoutParams.width = layoutWidht;
                layoutParams.height = layoutHeight;
                surfaceView.setLayoutParams(layoutParams);
            }
        });
    }

    // 与Activity状态保持同步
    // 用来释放状态
    public void onPause() {
        pauseMediaPlayer(); // 暂停播放，同时更新UI状态
        releaseMediaPlayer(); // 释放MediaPlayer，同时更新UI状态
    }

    // 释放MediaPlayer，同时更新UI状态
    private void releaseMediaPlayer() {
        mediaplayer.release();//释放
        mediaplayer = null;
        isPrepared = false;
        progressBar.setProgress(0);
    }
}
