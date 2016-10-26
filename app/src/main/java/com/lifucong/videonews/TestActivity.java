package com.lifucong.videonews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lifucong.videoplayer.part.SimpleVideoView;

public class TestActivity extends AppCompatActivity {

    private SimpleVideoView simpleVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_acitivity);
        simpleVideoView= (SimpleVideoView) findViewById(R.id.test_svv);
        simpleVideoView.setVideoPath(VideoUrlRes.getTestVideo1());
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleVideoView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleVideoView.onPause();
    }
}
