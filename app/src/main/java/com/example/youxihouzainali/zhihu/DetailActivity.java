package com.example.youxihouzainali.zhihu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    StringBuilder s = new StringBuilder();
    String timestamp;
    String name;
    String url1;
    private MyDatabaseHelper dbHelper;
    private String u = null;
    private List<Detail> detailList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private TextView tv1;
    private ImageView iv1;
    DetailAdapter adapter = new DetailAdapter(detailList);
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what ==1) {
                TextView time_stamp = (TextView) findViewById(R.id.tv_timestamp);
                TextView time_name = (TextView) findViewById(R.id.tv_name);
                String temp = "时间戳：" + timestamp;
                time_stamp.setText(temp);
                temp = "专栏名：" + name;
                time_name.setText(temp);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                LinearLayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this);
                layoutManager.setOrientation(OrientationHelper.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new DetailAdapter(detailList);
                recyclerView.setAdapter(adapter);
            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        u = intent.getStringExtra("extra_data");
        url1 = intent.getStringExtra("extra_url");
        sendRequestWithHttpURLConnection();
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDetail();
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
    }
    private void refreshDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendRequestWithHttpURLConnection();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initDetail(JSONObject jsonObject) {
        try {
            String date = jsonObject.getString("date");
            String display_date = jsonObject.getString("display_date");
            String id = jsonObject.getString("id");
            String title = jsonObject.getString("title");
            String images = jsonObject.getJSONArray("images").getString(0);
            Detail n = new Detail(id, images, title, date, display_date);
            detailList.add(n);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                detailList.clear();
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(url1);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parseJSON(response.toString());
                }catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void parseJSON(String jsonData) {
        try {
            timestamp = new JSONObject(jsonData).getString("timestamp");
            name = new JSONObject(jsonData).getString("name");
            JSONArray jsonArray = new JSONObject(jsonData).getJSONArray("stories");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                initDetail(jsonObject);
            }
            Message msg=new Message() ;
            msg.what=1;
            handler.sendMessage(msg) ;
        }catch (Exception e) {
            e.printStackTrace();
        }
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
            Toast.makeText(DetailActivity.this, "aaaaaaa", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(DetailActivity.this, AlterActivity.class);
            intent.putExtra("extra_data", u);
            intent.putExtra("status", 3);
            intent.putExtra("url", url1);
            startActivity(intent);
        } else if (id == R.id.nav_hot) {
            Intent intent = new Intent(DetailActivity.this, VitalActivity.class);
            intent.putExtra("extra_data", u);
            startActivity(intent);
        } else if (id == R.id.nav_sections) {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
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
