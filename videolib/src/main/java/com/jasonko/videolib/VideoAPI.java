package com.jasonko.videolib;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by kolichung on 4/30/15.
 */

public class VideoAPI {

    public static final String  TAG   = "Video_API";
    public static final boolean DEBUG = true;

    public static ArrayList<Video> getYoutubeVideos(String query) {
        ArrayList<Video> videos = new ArrayList<Video>();
        if (query.indexOf("(") != -1) {
            String name2 = query.substring(0, query.indexOf("("));
            query = name2;
        }
        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        String url = "https://vimeo.com/api/v2/channel/"+query+"/videos.json";

        String message = getMessageFromServer("GET", null, null, url);

        if (message == null) {
            return null;
        } else {
            try {

                JSONArray videoArray = new JSONArray(message);

                for (int i = 0; i < videoArray.length(); i++) {

                    String title = "null";
                    String link = "null";
                    String thumbnail_small = "null";
                    String thumbnail_large = "null";
                    String description = "null";

                    try {
                        title = videoArray.getJSONObject(i).getString("title");
                        link = videoArray.getJSONObject(i).getString("mobile_url");
                        thumbnail_small = videoArray.getJSONObject(i).getString("thumbnail_medium");
                        thumbnail_large = videoArray.getJSONObject(i).getString("thumbnail_large");
                        description = videoArray.getJSONObject(i).getString("description");
                    } catch (Exception e) {

                    }

                    String duration = "";
                    int viewCount = 0;

                    try {
                        int time = videoArray.getJSONObject(i).getInt("duration");
                        viewCount = videoArray.getJSONObject(i).getInt("stats_number_of_plays");

                        int[] intTime = splitToComponentTimes(time);
                        if(intTime[0]!=0){
                            duration = Integer.toString(intTime[0])+":"+Integer.toString(intTime[1])+":"+Integer.toString(intTime[2]);
                        }else{
                            if(intTime[2]<10){
                                duration = "0"+Integer.toString(intTime[2]);
                            }else{
                                duration = Integer.toString(intTime[2]);
                            }
                            duration = Integer.toString(intTime[1])+":"+duration;
                        }

                    } catch (Exception e) {

                    }

                    int likes = 0;
                    try{
                        likes= videoArray.getJSONObject(i).getInt("stats_number_of_likes");
                    }catch(Exception e){

                    }

                    String uploadTime = "";
                    try {
                        uploadTime = videoArray.getJSONObject(i).getString("upload_date");
                        uploadTime = uploadTime.substring(0,10);
                    }catch(Exception e){

                    }

                    Video video = new Video(title, link, thumbnail_small, thumbnail_large, uploadTime, viewCount, duration, likes, description);
                    videos.add(video);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return videos;

    }


    public static String getMessageFromServer(String requestMethod, String apiPath, JSONObject json, String apiUrl) {
        URL url;
        try {

            url = new URL(apiUrl);

            if (DEBUG)
                Log.d(TAG, "URL: " + url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);

            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            if (requestMethod.equalsIgnoreCase("POST"))
                connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();

            if (requestMethod.equalsIgnoreCase("POST")) {
                OutputStream outputStream;

                outputStream = connection.getOutputStream();
                if (DEBUG)
                    Log.d("post message", json.toString());

                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder lines = new StringBuilder();
            ;
            String tempStr;

            while ((tempStr = reader.readLine()) != null) {
                lines = lines.append(tempStr);
            }
            if (DEBUG)
                Log.d("MOVIE_API", lines.toString());

            reader.close();
            connection.disconnect();

            return lines.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

}
