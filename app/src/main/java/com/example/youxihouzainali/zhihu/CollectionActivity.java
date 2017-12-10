package com.example.youxihouzainali.zhihu;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private MyDatabaseHelper dbHelper;
    private List<Likes> collectionList = new ArrayList<>();
    private String u = null;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tv1;
    private ImageView iv1;
    CollectionAdapter adapter = new CollectionAdapter(collectionList, u);

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        u = intent.getStringExtra("extra_data");
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCollection();
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
        dbHelper = new MyDatabaseHelper(this, "Zhihu.db", null, 1);
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CollectionActivity.this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CollectionAdapter(collectionList, u);
        recyclerView.setAdapter(adapter);
        initCollection();
        if(collectionList.size() == 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CollectionActivity.this);
            dialog.setTitle("提示");
            dialog.setMessage("您的收藏空空如也呢");
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        }
    }

    private void refreshCollection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread( new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        collectionList.clear();
                        initCollection();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initCollection() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String image = null;
        String name = null;
        String description = null;
        String url = null;
        String flag = null;
        String newsid = null;
        Cursor cursor = db.query("Collection", null, "username=?", new String[] {u}, null, null, "flag");
        if(cursor.moveToFirst()) {
            do {
                image = cursor.getString(cursor.getColumnIndex("thumbnail"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                description = cursor.getString(cursor.getColumnIndex("description"));
                url = cursor.getString(cursor.getColumnIndex("url"));
                flag = cursor.getString(cursor.getColumnIndex("flag"));
                newsid = cursor.getString(cursor.getColumnIndex("newsid"));
                Likes collection = new Likes(image, name, description, url, flag, newsid);
                collectionList.add(collection);
            } while (cursor.moveToNext());
        }
        cursor.close();
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
            Intent intent = new Intent(CollectionActivity.this, AlterActivity.class);
            intent.putExtra("extra_data", u);
            intent.putExtra("status", 8);
            startActivity(intent);
        } else if (id == R.id.nav_hot) {
            Intent intent = new Intent(CollectionActivity.this, VitalActivity.class);
            intent.putExtra("extra_data", u);
            startActivity(intent);
        } else if (id == R.id.nav_sections) {
            Intent intent = new Intent(CollectionActivity.this, MainActivity.class);
            intent.putExtra("extra_data", u);
            startActivity(intent);
        } else if (id == R.id.nav_collection) {
            Toast.makeText(CollectionActivity.this, "您当前已在我的收藏页面", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_like) {
            Intent intent = new Intent(CollectionActivity.this, LikesActivity.class);
            intent.putExtra("extra_data", u);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
