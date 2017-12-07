package com.example.youxihouzainali.zhihu;

import android.os.Build;

/**
 * Created by youxihouzainali on 2017/12/7.
 */

public class Review {
    private String author;
    private String content;
    private String avatar;
    private String time;
    private String id;
    private String likes;

    public Review(String author, String content, String avatar, String time, String id, String likes) {
        this.author = author;
        this.content = content;
        this.avatar = avatar;
        this.time = time;
        this.id = id;
        this.likes = likes;
    }

    public String getAuthor() {
        return author;
    }
    public String getContent() {
        return content;
    }
    public String getAvatar() {
        return avatar;
    }
    public String getTime() {
        return time;
    }
    public String getId() {
        return id;
    }
    public String getLikes() {
        return likes;
    }
}
