package com.example.youxihouzainali.zhihu;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
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
                if (username.length() > 18) {
                    alert("提示", "用户名长度不能超过18位");
                    return;
                }
                char[] c = new char[20];
                c = username.toCharArray();
                int length = username.length();
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
                    if (username.length() == 0 || password.length() == 0) {
                        alert("提示", "您的信息未填写完整");
                        return;
                    }
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
                    ContentValues values = new ContentValues();
                    //开始组装第一条数据
                    values.put("username", username);
                    values.put("password", password);
                    values.put("telnumber", telephone);
                    db.insert("User", null, values);    //插入第一条数据
                    values.clear();
                    Toast.makeText(RegisterActivity.this, "successful", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
