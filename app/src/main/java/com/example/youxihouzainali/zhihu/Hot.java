package com.example.youxihouzainali.zhihu;

/**
 * Created by youxihouzainali on 2017/12/3.
 */

public class Hot {
    private String news_id;
    private String url;
    private String thumbnail;
    private String title;

    public Hot(String news_id, String url, String thumbnail, String title) {
        this.news_id = news_id;
        this.url = url;
        this.thumbnail = thumbnail;
        this.title = title;
    }

    public String getNews_id() {
        return news_id;
    }
    public String getUrl() {
        return url;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public String getTitle() {
        return title;
    }
}
