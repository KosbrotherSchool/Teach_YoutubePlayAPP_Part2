package com.jasonko.newyoutubeapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasonko.videolib.ImageLoader;
import com.jasonko.videolib.Video;


/**
 * Created by kolichung on 5/1/15.
 */


public class VideoActivity extends Activity {

    Video mYoutubeVideo;

    ImageView yImage;
    TextView yTitle;
    TextView yDesc;

    Button btnShare;
    Button btnToYoutube;

    ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video);
        mYoutubeVideo = (Video) getIntent().getSerializableExtra("video");

        yImage = (ImageView) findViewById(R.id.youtube_imageview);
        yTitle = (TextView) findViewById(R.id.youtube_text_title);
        yDesc = (TextView) findViewById(R.id.youtube_text_description);
        btnShare = (Button) findViewById(R.id.button_share);
        btnToYoutube = (Button) findViewById(R.id.button_youtube);

        mImageLoader = new ImageLoader(this);

        new DownloadVideoTask().execute();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "分享影片給您:" + mYoutubeVideo.getLink());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        btnToYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri youtubeUri = Uri.parse(mYoutubeVideo.getLink());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,youtubeUri);
                startActivity(browserIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadVideoTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
//            mYoutubeVideo = VideoAPI.getYoutubeVideoByID(videoId);
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {

            mImageLoader.DisplayImage(mYoutubeVideo.getThumbnail_large(), yImage);
            yTitle.setText(mYoutubeVideo.getTitle());
            yDesc.setText(Html.fromHtml(mYoutubeVideo.getDescription()));

        }
    }
}
