package com.example.youxihouzainali.zhihu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by youxihouzainali on 2017/12/1.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper{
    public static final String CREATE_USER = "create table User("
            + "id integer primary key autoincrement, "
            + "username text, "
            + "password text, "
            + "telnumber text, "
            + "icon text)";

    public static final String CREATE_LIKES  = "create table Likes("
            + "id integer primary key autoincrement, "
            + "username text, "
            + "name text, "
            + "description text, "
            + "thumbnail text, "
            + "flag text, "
            + "newsid text, "
            + "url text)";

    public static final String CREATE_COLLECTION  = "create table Collection("
            + "id integer primary key autoincrement, "
            + "username text, "
            + "name text, "
            + "description text, "
            + "thumbnail text, "
            + "flag text, "
            + "newsid text, "
            + "url text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_LIKES);
        db.execSQL(CREATE_COLLECTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        db.execSQL("drop table if exists Likes");
        db.execSQL("drop table if exists Collection");
        onCreate(db);

    }

}
