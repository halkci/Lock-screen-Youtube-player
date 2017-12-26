package com.halkci.simpleyoutubeplayer;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.WindowManager;

import java.io.Serializable;

public class PlayerActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {

    private static final String API_KEY = "your api key";
    private YouTubePlayerView video;

    private String youtubeId;

    private YouTubePlayer _youtubePlayer;

    /** TODO 小窓実験 */
    private WindowManager windowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        youtubeId = intent.getStringExtra(SearchResultActivity.VIDEO_ID);

        findView();
    }

    /**
     * viewの生成
     */
    private void findView() {
        video = (YouTubePlayerView) findViewById(R.id.playerView);
        video.initialize(API_KEY, this);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        windowManager.addView(video,params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        video = null;
        if(video != null){
            windowManager.removeViewImmediate(video);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(this._youtubePlayer != null){

            _youtubePlayer.loadVideo(youtubeId,_youtubePlayer.getCurrentTimeMillis());
            _youtubePlayer.play();
        }
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            youTubePlayer.loadVideo(youtubeId);
            youTubePlayer.play();
            this._youtubePlayer = youTubePlayer;
        }
    }

    /**
     * YouTubeプレーヤーの初期化失敗
     *
     * @param provider
     * @param errorMessage
     */
    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorMessage) {
        Log.d("errorMessage:", errorMessage.toString());
    }
}
