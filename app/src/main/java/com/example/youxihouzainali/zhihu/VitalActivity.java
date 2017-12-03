package com.example.youxihouzainali.zhihu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VitalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    
    private TextView tv1;
    private ImageView iv1;
    String u = null;
    StringBuilder s = new StringBuilder();
    private List<Hot> hotList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    HotAdapter adapter = new HotAdapter(hotList);
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what ==1) {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                LinearLayoutManager layoutManager = new LinearLayoutManager(VitalActivity.this);
                layoutManager.setOrientation(OrientationHelper.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new HotAdapter(hotList);
                recyclerView.setAdapter(adapter);
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        u = intent.getStringExtra("extra_data");
        //tv1 = (TextView)findViewById(R.id.tv_username);
        //tv1.setText(u);
        //iv1 = (ImageView) findViewById(R.id.iv_icon);
        //iv1设置头像
        
        
        
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHot();
            }
        });
        sendRequestWithHttpURLConnection();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void refreshHot() {
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
                        sendRequestWithHttpURLConnection();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initHot(JSONObject jsonObject) {
        try {
            String title = jsonObject.getString("title");
            String news_id = jsonObject.getString("news_id");
            String url = jsonObject.getString("url");
            String thumbnail = jsonObject.getString("thumbnail");
            Hot h = new Hot(news_id, url, thumbnail, title);
            hotList.add(h);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://news-at.zhihu.com/api/3/news/hot");
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
            JSONArray jsonArray = new JSONObject(jsonData).getJSONArray("recent");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                initHot(jsonObject);
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
            Toast.makeText(VitalActivity.this, "aaaaaaa", Toast.LENGTH_SHORT).show();
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
            // Handle the camera action
        } else if (id == R.id.nav_hot) {
            Toast.makeText(VitalActivity.this, "您当前已在热门消息页", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sections) {
            Intent intent = new Intent(VitalActivity.this, MainActivity.class);
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
