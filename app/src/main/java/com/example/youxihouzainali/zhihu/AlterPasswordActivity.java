package com.example.youxihouzainali.zhihu;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AlterPasswordActivity extends BaseActivity {
    private MyDatabaseHelper dbHelper;
    private Button mHelp;
    private Button mBack;
    private String username = null;
    private int status = 0;
    public void alert(String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AlterPasswordActivity.this);
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
        setContentView(R.layout.activity_alter_password);
        Intent intent = getIntent();
        username = intent.getStringExtra("extra_data");
        status = intent.getIntExtra("status", 0);
        dbHelper = new MyDatabaseHelper(this, "Zhihu.db", null, 1);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null)
            actionbar.hide();
        mBack = (Button) findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mHelp = (Button) findViewById(R.id.help) ;
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
        Button btn_enter = (Button) findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_pass = (EditText) findViewById(R.id.et_oldpassword);
                EditText et_newpass = (EditText) findViewById(R.id.et_newpassword);
                EditText et_repass = (EditText) findViewById(R.id.et_repassword);
                String oldpass = et_pass.getText().toString();
                String newpass = et_newpass.getText().toString();
                String repass = et_newpass.getText().toString();
                String rightPassword = null;
                if (et_pass.length() == 0 || et_newpass.length() == 0 || et_repass.length() == 0) {
                    alert("提示", "您的信息未填写完整");
                    return;
                }
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor = db.query("User", null, "username=?", new String[] {username}, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        rightPassword = cursor.getString(cursor.getColumnIndex("password"));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                MD5Utils md5Utils = new MD5Utils();
                oldpass = md5Utils.encode(oldpass);
                if(oldpass.equals(rightPassword)) {
                    char[] c = new char[20];
                    c = newpass.toCharArray();
                    int length = newpass.length();
                    for (int i = 0; i < length; i++) {
                        if (!((c[i] >= 48 && c[i] <= 57) || (c[i] >= 65 && c[i] <= 90) || (c[i] >= 97 && c[i] <= 122) || c[i] == ' ')) {
                            alert("警告", "新密码中含有非数字、字母、空格的字符，请重新输入");
                            return;
                        }
                    }
                    int flag = 0;
                    for (int i = 0; i < length; i++) {
                        if (! (c[i] >= 48 && c[i] <= 57))
                            flag = 1;
                    }
                    if(flag == 0) {
                        alert("警告", "新密码由纯数字构成，出于安全性考虑，请换用更复杂的密码");
                        return;
                    }
                    if(newpass.equals("abcdef") || newpass.equals("abcabc") || newpass.equals("abc123") ||
                            newpass.equals("a1b2c3") || newpass.equals("aaa111") || newpass.equals("123qwe") ||
                            newpass.equals("qwerry") || newpass.equals("qweasd") || newpass.equals("admin") ||
                            newpass.equals("Admin") || newpass.equals("administrator")) {
                        alert("警告", "您输入的新密码属于弱口令，出于安全性考虑，请换用更复杂的密码");
                        return;
                    }
                    if(newpass.length() < 6) {
                        alert("警告", "为了您的安全考虑，密码长度不能少于6位");
                        return;
                    }
                    if (newpass.length() > 18) {
                        alert("提示", "密码长度不能超过18位");
                        return;
                    }
                    if (!(newpass.equals(repass))) {
                        alert("提示", "两次输入的密码不一致");
                        return;
                    }
                    if(newpass.equals(oldpass)) {
                        alert("提示", "新旧密码完全相同");
                        return;
                    }
                    ContentValues values = new ContentValues();
                    values.put("username", username);
                    md5Utils = new MD5Utils();
                    newpass = md5Utils.encode(newpass);
                    values.put("password", newpass);
                    db.update("User", values, "username=?", new String[] {username});    //插入第一条数据
                    values.clear();
                    Toast.makeText(AlterPasswordActivity.this, "successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AlterPasswordActivity.this, AlterActivity.class);
                    intent.putExtra("extra_data", username);
                    intent.putExtra("status", status);
                    startActivity(intent);
                }
                else {
                    alert("错误", "旧密码输入错误");
                    return;
                }

            }
        });
    }
}
