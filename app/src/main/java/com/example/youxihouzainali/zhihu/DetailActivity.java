package com.example.youxihouzainali.zhihu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

public class DetailActivity extends AppCompatActivity {

    StringBuilder s = new StringBuilder();
    String timestamp;
    String name;
    String url1;
    private List<Detail> detailList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    DetailAdapter adapter = new DetailAdapter(detailList);
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what ==1) {
                /*TextView time_stamp = (TextView) findViewById(R.id.tv_timestamp);
                TextView time_name = (TextView) findViewById(R.id.tv_name);
                time_stamp.setText(timestamp);
                time_name.setText(name);
                */
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
        Intent intent = getIntent();
        url1 = intent.getStringExtra("extra_data");
        sendRequestWithHttpURLConnection();
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDetail();
            }
        });

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
}
