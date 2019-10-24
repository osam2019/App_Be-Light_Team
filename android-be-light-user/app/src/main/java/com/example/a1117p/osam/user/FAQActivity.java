package com.example.a1117p.osam.user;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;

public class FAQActivity extends YouTubeBaseActivity {
    final String VIDEO_URL = "https://be-light.store/dummy-video.mp4";
/*    YouTubePlayer.OnInitializedListener listener;
    private YouTubePlayerView youtubeViewer;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        //youtubeViewer = findViewById(R.id.youtubeViewer);

        /*listener = new YouTubePlayer.OnInitializedListener(){
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                *//* 초기화 성공하면 유튜브 동영상 ID를 전달하여 동영상 재생*//*
                youTubePlayer.loadVideo("mrAIqeULUL0"); // 동영상 ID는 URL 상단의 마지막 부분이다.
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(FAQActivity.this, "재생 실패", Toast.LENGTH_LONG).show();
            }
        };
        youtubeViewer.initialize(getResources().getString(R.string.youtube), listener);*/
        final VideoView videoView = findViewById(R.id.video);
        MediaController controller = new MediaController(FAQActivity.this);
        videoView.setMediaController(controller);
        videoView.setVideoURI(Uri.parse(VIDEO_URL));
        final ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_UP);
                videoView.seekTo( 1 );
            }
        });


    }
}
