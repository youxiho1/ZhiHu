package com.example.youxihouzainali.zhihu;

/**
 * Created by youxihouzainali on 2017/11/27.
 */

public class News {
    private String id;
    private String thumbnail;
    private String name;
    private String description;

    public News(String id, String thumbnail, String name, String description) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
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
}
