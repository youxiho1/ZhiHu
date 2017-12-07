package com.example.youxihouzainali.zhihu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class HotDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private String u = null;
    private String url = null;
    private TextView tv1;
    private ImageView iv1;
    StringBuilder s = new StringBuilder();
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        u = intent.getStringExtra("extra_data");
        url = intent.getStringExtra("extra_url");
        dbHelper = new MyDatabaseHelper(this, "Zhihu.db", null, 1);
        Button btn_long = (Button) findViewById(R.id.btn_long_comments);
        btn_long.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotDetailActivity.this, ReviewActivity.class);
                intent.putExtra("extra_data", u);
                intent.putExtra("extra_url", url+"/long-comments");
                startActivity(intent);
            }
        });
        Button btn_short = (Button) findViewById(R.id.btn_short_comments);
        btn_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotDetailActivity.this, ShortReviewActivity.class);
                intent.putExtra("extra_data", u);
                intent.putExtra("extra_url", url+"/short-comments");
                startActivity(intent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0) ;
        tv1 = (TextView) headerview .findViewById(R.id.tv_username) ;
        tv1.setText(u);
        iv1 = (ImageView) headerview.findViewById(R.id.iv_icon);
        String image = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("User", null, "username=?", new String[] {u}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                image = cursor.getString(cursor.getColumnIndex("icon"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        if(image != null)
            Glide.with(this).load(image).into(iv1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vital, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //加settings
            return true;
        } else if (id == R.id.action_quit) {
            ActivityCollector.finishAll();
            android.os.Process.killProcess(android.os.Process.myPid());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_alter) {
            Intent intent = new Intent(HotDetailActivity.this, AlterActivity.class);
            intent.putExtra("extra_data", u);
            intent.putExtra("extra_url", url);
            intent.putExtra("status", 6);
            startActivity(intent);
        } else if (id == R.id.nav_hot) {
            Intent intent = new Intent(HotDetailActivity.this, VitalActivity.class);
            intent.putExtra("extra_data", u);
            startActivity(intent);
        } else if (id == R.id.nav_sections) {
            Intent intent = new Intent(HotDetailActivity.this, MainActivity.class);
            intent.putExtra("extra_data", u);
            startActivity(intent);
        } else if (id == R.id.nav_collection) {

        } else if (id == R.id.nav_like) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
