package com.jasonko.videolib;

import java.io.Serializable;

/**
 * Created by kolichung on 4/30/15.
 */

public class Video implements Serializable{

    String title;
    String link;
    String thumbnail_small;
    String thumbnail_large;
    String uploadTime;
    int viewCount;
    String duration;
    int likes;
    String description;

    public Video() {
        this("", "", "", "","", 0, "", 0, "");
    }

    public Video(String title, String link, String thumbnail_small, String thumbnail_large, String uploadTime, int viewCount, String duration, int likes, String description) {
        this.title = title;
        this.link = link;
        this.thumbnail_small = thumbnail_small;
        this.thumbnail_large = thumbnail_large;
        this.uploadTime = uploadTime;
        this.viewCount = viewCount;
        this.likes = likes;
        this.duration = duration;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail_small() {
        return thumbnail_small;
    }

    public String getThumbnail_large() { return thumbnail_large;}

    public String getLink() {
        return link;
    }

    public int getViewCount() {
        return viewCount;
    }

    public String getUploadDate() {
        return uploadTime;
    }

    public int getLikes() { return likes; }

    public String getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

}
