package com.example.youxihouzainali.zhihu;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class HotDetailActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private String u = null;
    private String url = null;
    private String id1 = null;
    private String popularity = null;
    private String long_comments = null;
    private String short_comments = null;
    private String comments = null;
    private String body = null;
    private String image_source = null;
    private String title = null;
    private String image = null;
    private String css = null;
    private TextView tv1;
    private ImageView iv1;
    private SwipeRefreshLayout swipeRefresh;
    StringBuilder s = new StringBuilder();
    private MyDatabaseHelper dbHelper;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what ==1) {
                TextView tv_longcomments = (TextView) findViewById(R.id.tv_long_comments);
                TextView tv_shortcomments = (TextView) findViewById(R.id.tv_short_comments);
                TextView tv_comments = (TextView) findViewById(R.id.tv_comments);
                TextView tv_popularity = (TextView) findViewById(R.id.tv_popularity);
                TextView tv_imagesource = (TextView) findViewById(R.id.image_source);
                TextView tv_title = (TextView) findViewById(R.id.title);
                WebView wv = (WebView) findViewById(R.id.wv);
                ImageView iv = (ImageView) findViewById(R.id.image);
                String temp = "评论数:" + comments;
                tv_comments.setText(temp);
                temp = "长评数:" + long_comments;
                tv_longcomments.setText(temp);
                temp = "热度:" + popularity;
                tv_popularity.setText(temp);
                temp = "短评数:" + short_comments;
                tv_shortcomments.setText(temp);
                tv_title.setText(title);
                temp = "图片来源:" + image_source;
                tv_imagesource.setText(temp);
                Glide.with(HotDetailActivity.this).load(image).into(iv);
                String html = "<html>"
                        + "<head>" + "<link rel=\"stylesheet\" type=\"text/css\" href=\""+css+"\"/> " + "</head>"
                        + "<body>" + body + "</body>" + "</html>";
                wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                wv.getSettings().setJavaScriptEnabled(true);
                wv.setWebChromeClient(new WebChromeClient());
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        u = intent.getStringExtra("extra_data");
        url = intent.getStringExtra("extra_url");
        id1 = intent.getStringExtra("id");
        dbHelper = new MyDatabaseHelper(this, "Zhihu.db", null, 1);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHotDetail();
            }
        });
        sendRequestWithHttpURLConnection();
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
        /*final Button btn_likes = (Button) findViewById(R.id.likes);
        btn_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button btn_collection = (Button) findViewById(R.id.collection);
        btn_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
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
        String image1 = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("User", null, "username=?", new String[] {u}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                image1 = cursor.getString(cursor.getColumnIndex("icon"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        if(image1 != null)
            Glide.with(this).load(image1).into(iv1);

    }
    private void refreshHotDetail() {
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
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initHotDetail(JSONObject jsonObject, JSONObject jsonObject1) {
        try {
            popularity = jsonObject.getString("popularity");
            long_comments = jsonObject.getString("long_comments");
            short_comments = jsonObject.getString("short_comments");
            comments = jsonObject.getString("comments");
            title = jsonObject1.getString("title");
            image = jsonObject1.getString("image");
            image_source = jsonObject1.getString("image_source");
            body = jsonObject1.getString("body");
            int lengthv = body.length();
            int flag = 0;
            for(int i = 0; i < lengthv; i++) {
                String temp = body.substring(i, i+36);
                if(temp.equals("<div class=\"img-place-holder\"></div>")) {
                    body = body.substring(0, i-1) + body.substring(i+37,lengthv);
                    break;
                }
            }
            JSONArray jsonArray = jsonObject1.getJSONArray("css");
            css = jsonArray.getString(0);
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
                    URL url1 = new URL("https://news-at.zhihu.com/api/4/news-extra/"+id1);
                    URL url2 = new URL("https://news-at.zhihu.com/api/4/news/"+id1);
                    connection = (HttpURLConnection) url1.openConnection();
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
                    connection = (HttpURLConnection) url2.openConnection();
                    //connection.setRequestMethod("GET");
                    //connection.setConnectTimeout(8000);
                    //connection.setReadTimeout(8000);
                    InputStream in2 = connection.getInputStream();
                    reader = null;
                    reader = new BufferedReader(new InputStreamReader(in2));
                    StringBuilder response2 = new StringBuilder();
                    String line2;
                    while((line2 = reader.readLine()) != null) {
                        response2.append(line2);
                    }
                    parseJSON(response.toString(), response2.toString());
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
    private void parseJSON(String jsonData, String jsonData2) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject jsonObject1 = new JSONObject(jsonData2);
            initHotDetail(jsonObject, jsonObject1);
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
            intent.putExtra("id", id1);
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
            Intent intent = new Intent(HotDetailActivity.this, CollectionActivity.class);
            intent.putExtra("extra_data", u);
            startActivity(intent);
        } else if (id == R.id.nav_like) {
            Intent intent = new Intent(HotDetailActivity.this, LikesActivity.class);
            intent.putExtra("extra_data", u);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
