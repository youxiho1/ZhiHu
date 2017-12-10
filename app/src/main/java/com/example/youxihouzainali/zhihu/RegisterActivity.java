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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {
    private MyDatabaseHelper dbHelper;
    private Button mHelp;
    private Button mBack;
    public void alert(String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
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
        setContentView(R.layout.activity_register);
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
        Button buttonRegister = (Button) findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password, repassword, telephone;
                EditText edittext_username = (EditText) findViewById(R.id.edittext_username);
                EditText edittext_pass = (EditText) findViewById(R.id.edittext_pass);
                EditText edittext_repass = (EditText) findViewById(R.id.edittext_repass);
                EditText edittext_tel = (EditText) findViewById(R.id.edittext_tel);
                username = edittext_username.getText().toString();
                password = edittext_pass.getText().toString();
                repassword = edittext_repass.getText().toString();
                telephone = edittext_tel.getText().toString();
                if (username.length() == 0 || password.length() == 0 || telephone.length() == 0) {
                    alert("提示", "您的信息未填写完整");
                    return;
                }
                if (username.length() > 18) {
                    alert("提示", "用户名长度不能超过18位");
                    return;
                }
                char[] c = new char[20];
                c = username.toCharArray();
                int length = username.length();
                if(c[0] >= 48 && c[0] <= 57) {
                    alert("提示", "用户名首位不能是数字");
                    return;
                }
                for (int i = 0; i < length; i++) {
                    if (!((c[i] >= 48 && c[i] <= 57) || (c[i] >= 65 && c[i] <= 90) || (c[i] >= 97 && c[i] <= 122) || c[i] == ' ')) {
                        alert("警告", "用户名中含有非数字、字母、空格的字符，请重新输入");
                        return;
                    }
                }
                c = password.toCharArray();
                length = password.length();
                for (int i = 0; i < length; i++) {
                    if (!((c[i] >= 48 && c[i] <= 57) || (c[i] >= 65 && c[i] <= 90) || (c[i] >= 97 && c[i] <= 122) || c[i] == ' ')) {
                        alert("警告", "密码中含有非数字、字母、空格的字符，请重新输入");
                        return;
                    }
                }
                int flag = 0;
                for (int i = 0; i < length; i++) {
                    if (! (c[i] >= 48 && c[i] <= 57))
                        flag = 1;
                }
                if(flag == 0) {
                    alert("警告", "密码由纯数字构成，出于安全性考虑，请换用更复杂的密码");
                    return;
                }
                if(password.equals("abcdef") || password.equals("abcabc") || password.equals("abc123") ||
                        password.equals("a1b2c3") || password.equals("aaa111") || password.equals("123qwe") ||
                        password.equals("qwerry") || password.equals("qweasd") || password.equals("admin") ||
                        password.equals("Admin") || password.equals("administrator")) {
                    alert("警告", "您输入的密码属于弱口令，出于安全性考虑，请换用更复杂的密码");
                    return;
                }
                if(password.length() < 6) {
                    alert("警告", "为了您的安全考虑，密码长度不能少于6位");
                    return;
                }
                if(telephone.length() != 11 && telephone.length() != 8) {
                    alert("提示", "手机号格式错误，请检查");
                    return;
                }
                if (password.length() > 18) {
                    alert("提示", "密码长度不能超过18位");
                    return;
                }

                if (!(password.equals(repassword))) {
                    alert("提示", "两次输入的密码不一致");
                    return;
                }
                if (password.equals(repassword)) {
                    int id = -1;
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor cursor = db.query("User", null, "username=?", new String[] {username}, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            id = cursor.getInt(cursor.getColumnIndex("id"));
                        } while (cursor.moveToNext());
                    }
                    if(id != -1) {
                        alert("提示", "该用户名已被注册");
                        return;
                    }
                    cursor.close();
                    Cursor cursor1 = db.query("User", null, "telnumber=?", new String[] {telephone}, null, null, null);
                    if (cursor1.moveToFirst()) {
                        do {
                            id = cursor1.getInt(cursor1.getColumnIndex("id"));
                        } while (cursor1.moveToNext());
                    }
                    if(id != -1) {
                        alert("提示", "该手机号已被注册");
                        return;
                    }
                    cursor1.close();
                    ContentValues values = new ContentValues();
                    //开始组装第一条数据
                    values.put("username", username);
                    values.put("password", password);
                    values.put("telnumber", telephone);
                    db.insert("User", null, values);    //插入第一条数据
                    values.clear();
                    Toast.makeText(RegisterActivity.this, "successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, VitalActivity.class);
                    //Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("extra_data", username);
                    startActivity(intent);
                }
            }
        });
    }
}
