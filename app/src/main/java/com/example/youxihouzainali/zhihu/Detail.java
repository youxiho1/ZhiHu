package com.example.youxihouzainali.zhihu;

/**
 * Created by youxihouzainali on 2017/11/27.
 */

public class Detail {
    private String id;
    private String images;
    private String date;
    private String display_date;
    private String title;

    public Detail(String id, String images, String title, String date, String display_date) {
        this.date = date;
        this.id = id;
        this.display_date = display_date;
        this.title = title;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public String getImages() {
        return images;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplay_date() {
        return display_date;
    }

    public String getDate() {
        return date;
    }
}
