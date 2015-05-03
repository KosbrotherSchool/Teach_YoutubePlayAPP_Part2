package com.jasonko.newyoutubeapp;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kolichung on 4/30/15.
 */
public class VideoAPI {

    public static final String  TAG   = "Video_API";
    public static final boolean DEBUG = true;

    public static ArrayList<YoutubeVideo> getYoutubeVideos(String query, int page) {
        ArrayList<YoutubeVideo> videos = new ArrayList<YoutubeVideo>();
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

        String url = "http://gdata.youtube.com/feeds/api/videos?q=" + query + "&start-index=" + (page * 12 + 1)
                + "&max-results=12&v=2&alt=json&fields=entry[link/@rel='http://gdata.youtube.com/schemas/2007%23mobile']";
        String message = getMessageFromServer("GET", null, null, url);

        if (message == null) {
            return null;
        } else {
            try {
                JSONObject object = new JSONObject(message);
                JSONObject feedObject = object.getJSONObject("feed");
                JSONArray videoArray = feedObject.getJSONArray("entry");
                for (int i = 0; i < videoArray.length(); i++) {

                    String title = "null";
                    String link = "null";
                    String thumbnail = "null";

                    try {
                        title = videoArray.getJSONObject(i).getJSONObject("title").getString("$t");
                        link = videoArray.getJSONObject(i).getJSONArray("link").getJSONObject(0).getString("href");
                        thumbnail = videoArray.getJSONObject(i).getJSONObject("media$group").getJSONArray("media$thumbnail").getJSONObject(0).getString("url");
                    } catch (Exception e) {

                    }

                    int duration = 0;
                    int viewCount = 0;

                    try {
                        duration = videoArray.getJSONObject(i).getJSONObject("media$group").getJSONObject("yt$duration").getInt("seconds");
                        viewCount = videoArray.getJSONObject(i).getJSONObject("yt$statistics").getInt("viewCount");
                    } catch (Exception e) {

                    }

                    int dislikes = 0;
                    int likes = 0;
                    try{
                        dislikes= videoArray.getJSONObject(i).getJSONObject("yt$rating").getInt("numDislikes");
                        likes= videoArray.getJSONObject(i).getJSONObject("yt$rating").getInt("numLikes");
                    }catch(Exception e){

                    }

                    // int dislikes = videoArray.getJSONObject(i).getJSONObject("yt$rating").getInt("numDislikes");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    Date uploadTime = null;
                    try {
                        uploadTime = sdf.parse(videoArray.getJSONObject(i).getJSONObject("published").getString("$t"));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    YoutubeVideo video = new YoutubeVideo(title, link, thumbnail, uploadTime, viewCount, duration, likes, dislikes, "");
                    videos.add(video);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return videos;

    }

    public static YoutubeVideo getYoutubeVideoByID(String id){
        YoutubeVideo theVieo = new YoutubeVideo();
        String url = "https://gdata.youtube.com/feeds/api/videos/"+id+"?v=2&alt=json";
        String message = getMessageFromServer("GET", null, null, url);

        if (message == null) {
            return null;
        } else {
            try {
                JSONObject object = new JSONObject(message);
                String description = object.getJSONObject("entry").getJSONObject("media$group").getJSONObject("media$description").getString("$t");
                String title = object.getJSONObject("entry").getJSONObject("title").getString("$t");
                String pic = object.getJSONObject("entry").getJSONObject("media$group").getJSONArray("media$thumbnail").getJSONObject(2).getString("url");
                String videoLink = object.getJSONObject("entry").getJSONArray("link").getJSONObject(0).getString("href");

                theVieo.description = description;
                theVieo.title = title;
                theVieo.thumbnail = pic;
                theVieo.link = videoLink;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return theVieo;
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

}
