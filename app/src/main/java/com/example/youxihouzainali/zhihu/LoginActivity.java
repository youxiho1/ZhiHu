package com.example.youxihouzainali.zhihu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;

public class LoginActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private EditText mEditTextName;
    private EditText mEditTextPassword;
    private Button mHelp;
    private Button mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        mEditTextName = (EditText) findViewById(R.id.edit_text1);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text2);
        ImageButton button_eye = (ImageButton) findViewById(R.id.button_eye);
        button_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText2 = (EditText)findViewById(R.id.edit_text2);
                if(editText2.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    editText2.setInputType(InputType.TYPE_CLASS_TEXT);
                    Toast.makeText(LoginActivity.this, "PASSWORD->TEXT", Toast.LENGTH_SHORT).show();
                }
                else {
                    editText2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Toast.makeText(LoginActivity.this, "TEXT->PASSWORD", Toast.LENGTH_SHORT).show();
                    //这个setInputType为什么没作用，明明toast都能成功？！！
                }
            }
        });
        Button button_forget_password = (Button) findViewById(R.id.button_forget_password);
        button_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加入忘记密码功能
            }
        });
        Button button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                EditText editText1 = (EditText) findViewById(R.id.edit_text1);
                EditText editText2 = (EditText) findViewById(R.id.edit_text2);
                String username = null, password = null, rightPassword = null;
                username = editText1.getText().toString();
                password = editText2.getText().toString();
                if(username.equals(null)) {
                    Toast.makeText(LoginActivity.this, "您还没有输入用户名！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.equals(null)) {
                    Toast.makeText(LoginActivity.this, "您还没有输入密码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Cursor cursor = db.query("User", null, "username=?", new String[] {username}, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        rightPassword = cursor.getString(cursor.getColumnIndex("password"));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                if(password.equals(rightPassword)) {
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, VitalActivity.class);
                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("extra_data", username);
                    startActivity(intent);
                }
                else
                    Toast.makeText(LoginActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
