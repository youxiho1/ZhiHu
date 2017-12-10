package com.example.youxihouzainali.zhihu;

/**
 * Created by youxihouzainali on 2017/12/10.
 */

public class Likes {
    private String thumbnail;
    private String name;
    private String description;
    private String url;
    private String flag;
    private String newsid;

    public Likes(String thumbnail, String name, String description, String url, String flag, String newsid) {
        this.thumbnail = thumbnail;
        this.description = description;
        this.flag = flag;
        this.url = url;
        this.name = name;
        this.newsid = newsid;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getUrl() {
        return url;
    }
    public String getFlag() {
        return flag;
    }
    public String getNewsid() {
        return newsid;
    }
}
