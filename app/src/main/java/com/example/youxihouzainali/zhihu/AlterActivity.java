package com.example.youxihouzainali.zhihu;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlterActivity extends BaseActivity {
    private MyDatabaseHelper dbHelper;
    public static final int CHOOSE_PHOTO = 2;
    private Button mHelp;
    private Button mBack;
    private String username = null;
    private String imagepath = null;
    private String telnumber = null;
    private String id1 = null;
    private String url = null;
    private int status = 0;
    private ImageView iv1;
    private CircleImageView iv2;
    public void alert(String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AlterActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter);
        dbHelper = new MyDatabaseHelper(this, "Zhihu.db", null, 1);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.hide();
        Intent intent = getIntent();
        username = intent.getStringExtra("extra_data");
        status = intent.getIntExtra("status", 0);
        if(status == 3 || status == 4 || status == 5 || status == 6)
            url = intent.getStringExtra("extra_url");
        if(status == 6)
            id1 = intent.getStringExtra("id");
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("User", null, "username=?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                imagepath = cursor.getString(cursor.getColumnIndex("icon"));
                telnumber = cursor.getString(cursor.getColumnIndex("telnumber"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        mBack = (Button) findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mHelp = (Button) findViewById(R.id.help);
        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "1、帮助\n2、帮助\n3、blablabla", Snackbar.LENGTH_INDEFINITE)
                        .setAction("知道了", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        });
        TextView tv1 = (TextView) findViewById(R.id.textview_username);
        final TextView tv2 = (TextView) findViewById(R.id.textview_tel);
        iv1 = (ImageView) findViewById(R.id.imageview);
        iv2 = (CircleImageView) findViewById(R.id.circleimageview);
        tv1.setText(username);
        tv2.setText(telnumber);
        if (imagepath != null) {
            Glide.with(this).load(imagepath).into(iv1);
            Glide.with(this).load(imagepath).into(iv2);
        }
        Button btn_alterpass = (Button) findViewById(R.id.alter_password);
        btn_alterpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlterActivity.this, AlterPasswordActivity.class);
                intent.putExtra("extra_data", username);
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });
        Button btn_altertel = (Button) findViewById(R.id.alter_telephone);
        btn_altertel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(AlterActivity.this);
                new AlertDialog.Builder(AlterActivity.this).setTitle("请输入您的新手机号")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String input = et.getText().toString();
                                if(input.equals("")) {
                                    Toast.makeText(AlterActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    telnumber = input;
                                }
                                int id = -1;
                                Cursor cursor = db.query("User", null, "telnumber=?", new String[] {telnumber}, null, null, null);
                                if (cursor.moveToFirst()) {
                                    do {
                                        id = cursor.getInt(cursor.getColumnIndex("id"));
                                    } while (cursor.moveToNext());
                                }
                                if(id != -1) {
                                    alert("提示", "该手机号已被注册");
                                    return;
                                }
                                cursor.close();
                                tv2.setText(telnumber);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        Button alter_enter = (Button) findViewById(R.id.enter);
        alter_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("icon", imagepath);
                values.put("telnumber", telnumber);
                db.update("User", values, "username=?", new String[]{username});
                Toast.makeText(AlterActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                switch (status) {
                    case 1:
                        Intent intent = new Intent(AlterActivity.this, MainActivity.class);
                        intent.putExtra("extra_data", username);
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent1 = new Intent(AlterActivity.this, VitalActivity.class);
                        intent1.putExtra("extra_data", username);
                        startActivity(intent1);
                        break;
                    case 3:
                        Intent intent2 = new Intent(AlterActivity.this, DetailActivity.class);
                        intent2.putExtra("extra_data", username);
                        intent2.putExtra("extra_url", url);
                        startActivity(intent2);
                        break;
                    case 4:
                        Intent intent3 = new Intent(AlterActivity.this, ReviewActivity.class);
                        intent3.putExtra("extra_data", username);
                        intent3.putExtra("extra_url", url);
                        startActivity(intent3);
                        break;
                    case 5:
                        Intent intent4 = new Intent(AlterActivity.this, ShortReviewActivity.class);
                        intent4.putExtra("extra_data", username);
                        intent4.putExtra("extra_url", url);
                        startActivity(intent4);
                        break;
                    case 6:
                        Intent intent5 = new Intent(AlterActivity.this, HotDetailActivity.class);
                        intent5.putExtra("extra_data", username);
                        intent5.putExtra("extra_url", url);
                        intent5.putExtra("id", id1);
                        startActivity(intent5);
                        break;
                    case 7:
                        Intent intent6 = new Intent(AlterActivity.this, LikesActivity.class);
                        intent6.putExtra("extra_data", username);
                        startActivity(intent6);
                        break;
                    case 8:
                        Intent intent7 = new Intent(AlterActivity.this, CollectionActivity.class);
                        intent7.putExtra("extra_data", username);
                        startActivity(intent7);
                        break;
                }
                finish();
            }
        });
        Button btn_altericon = (Button) findViewById(R.id.alter_icon);
        btn_altericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AlterActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AlterActivity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });
    }
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }
                else {
                    Toast.makeText(this, "您拒绝了我们的权限请求", Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    }
                    else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagepath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagepath = getImagePath(uri, null);
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                imagepath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imagepath = uri.getPath();
            }
            displayImage();
        }
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagepath = getImagePath(uri, null);
        displayImage();
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage() {
        if(imagepath != null) {
            Glide.with(this).load(imagepath).into(iv1);
            Glide.with(this).load(imagepath).into(iv2);
        }
        else {
            Toast.makeText(AlterActivity.this, "没有找到图片", Toast.LENGTH_SHORT).show();
        }
    }
}
