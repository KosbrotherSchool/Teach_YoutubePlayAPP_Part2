package com.jasonko.newyoutubeapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasonko.imageloader.ImageLoader;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


@SuppressLint("SimpleDateFormat")
public class ListVideoAdapter extends BaseAdapter {

    private final Activity   mActivity;
    private final ArrayList<YoutubeVideo> data;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    public ListVideoAdapter(Activity context, ArrayList<YoutubeVideo> d) {
        mActivity = context;
        data = d;
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(mActivity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.item_video_list, null);

        TextView textTitle = (TextView) vi.findViewById(R.id.text_news_list);
        ImageView image = (ImageView) vi.findViewById(R.id.image_news_list);
        TextView textDate = (TextView) vi.findViewById(R.id.text_list_date);
        TextView textViews = (TextView) vi.findViewById(R.id.text_list_views);
        TextView textDuration = (TextView) vi.findViewById(R.id.text_list_duration);
        TextView textLikes = (TextView) vi.findViewById(R.id.text_list_like);

        // set title text
        textTitle.setText(data.get(position).getTitle());

        // set views text
        int views = data.get(position).getViewCount();
        textViews.setText(NumberFormat.getNumberInstance(Locale.US).format(views)+" "+mActivity.getResources().getString(R.string.views));

        // set likes text
        textLikes.setText(data.get(position).getLikes()+" "+mActivity.getResources().getString(R.string.likes));

        // set date text
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        final String dateString = formatter.format(data.get(position).getUploadDate());
        textDate.setText(mActivity.getResources().getString(R.string.launch_time)+": "+dateString);

        // set duration text
        int[] intTime = splitToComponentTimes(data.get(position).getDuration());
        if(intTime[0]!=0){
            textDuration.setText(mActivity.getResources().getString(R.string.time)+":"+Integer.toString(intTime[0])+":"+Integer.toString(intTime[1])+":"+Integer.toString(intTime[2]));
        }else{
            String timeSecond = "";
            if(intTime[2]<10){
                timeSecond = "0"+Integer.toString(intTime[2]);
            }else{
                timeSecond = Integer.toString(intTime[2]);
            }
            textDuration.setText(mActivity.getResources().getString(R.string.time)+":"+Integer.toString(intTime[1])+":"+timeSecond);
        }

        // set image
        imageLoader.DisplayImage(data.get(position).getThumbnail(), image);

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(mActivity, VideoActivity.class);
                String videoId = getVideoID(data.get(position).getLink());
                newIntent.putExtra("VideoId", videoId);
                mActivity.startActivity(newIntent);
            }
        });

        return vi;
    }

    public static int[] splitToComponentTimes(int i)
    {
        long longVal = (long)i;
        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        int[] ints = {hours , mins , secs};
        return ints;
    }

    private String getVideoID (String videoUrl) {
        String id = "";
        if(videoUrl.indexOf("&feature")!= -1){
            id = videoUrl.substring(videoUrl.indexOf("v=")+2, videoUrl.indexOf("&feature"));
        }else{
            id = videoUrl.substring(videoUrl.indexOf("videos/")+7, videoUrl.indexOf("?v=2"));
        }
        return id;
    }

}
