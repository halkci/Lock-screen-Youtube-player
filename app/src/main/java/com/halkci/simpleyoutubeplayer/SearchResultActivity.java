package com.halkci.simpleyoutubeplayer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchResultActivity extends AppCompatActivity {

    public final static String VIDEO_ID = "youtubeVideoID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // return button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String searchKey = intent.getStringExtra(MainActivity.SEARCH_KEYWORD);

        if(searchKey != null){

            YoutubeSearchApi youtubeApi = new YoutubeSearchApi(searchKey);
            youtubeApi.execute((Void) null);

            ArrayList<String> resultVideoTitles = youtubeApi.getVideoTitles();
            ArrayList<Drawable> drawables = youtubeApi.getThumbnail();
            ArrayList<String> videoIdList = youtubeApi.getVideoIdList();
            ArrayList<String> channelList = youtubeApi.getChannelList();

            synchronized (youtubeApi){
                if(resultVideoTitles == null){
                    try {
                        youtubeApi.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                resultVideoTitles = youtubeApi.getVideoTitles();
                drawables = youtubeApi.getThumbnail();
                videoIdList = youtubeApi.getVideoIdList();
                channelList = youtubeApi.getChannelList();
            }

            if (resultVideoTitles != null) {
                LinearLayout cardLinear = (LinearLayout) findViewById(R.id.cardLinear);
                cardLinear.removeAllViews();

                Iterator<String> videoTitleIterator = resultVideoTitles.iterator();
                Iterator<Drawable> drawableIterator = drawables.iterator();
                Iterator<String> videoIdIterator = videoIdList.iterator();
                Iterator<String> channelIterator = channelList.iterator();

                while (videoTitleIterator.hasNext()) {
                    String videoTitle = videoTitleIterator.next();
                    Drawable drawable = drawableIterator.next();
                    String videoId = videoIdIterator.next();
                    String channelName = channelIterator.next();

                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.card_content, null);

                    ImageView cardImageView = (ImageView)linearLayout.findViewById(R.id.cardImageView);
                    cardImageView.setImageDrawable(drawable);

                    TextView videoTitleTextView = (TextView) linearLayout.findViewById(R.id.videoTitle);
                    videoTitleTextView.setTextSize(13);
                    videoTitleTextView.setText(videoTitle);

                    TextView channelNameTextView = (TextView) linearLayout.findViewById(R.id.channelName);
                    channelNameTextView.setTextSize(12);
                    channelNameTextView.setText(channelName);

                    CardView cardView = (CardView) linearLayout.findViewById(R.id.cardView);
                    cardView.setTag(videoId);
                    cardView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SearchResultActivity.this, PlayerActivity.class);

                            intent.putExtra(VIDEO_ID,String.valueOf(v.getTag()));
                            startActivity(intent);
                        }
                    });

                    cardLinear.addView(linearLayout);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
