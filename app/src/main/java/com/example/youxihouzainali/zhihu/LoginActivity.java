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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;

public class LoginActivity extends BaseActivity {
    private MyDatabaseHelper dbHelper;
    private EditText mEditTextName;
    private EditText mEditTextPassword;
    private String s = null;
    private ImageButton buttonTest;
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
                if(editText2.getTransformationMethod() == PasswordTransformationMethod.getInstance())
                    editText2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    editText2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        /*Button button_forget_password = (Button) findViewById(R.id.button_forget_password);
        button_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加入忘记密码功能
            }
        });*/
        CodeUtils codeUtils = new CodeUtils();
        buttonTest = (ImageButton) findViewById(R.id.button_teltest) ;
        buttonTest.setImageBitmap(codeUtils.createBitmap());
        s = codeUtils.getCode();
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeUtils codeUtils = new CodeUtils();
                buttonTest.setImageBitmap(codeUtils.createBitmap());
                s = codeUtils.getCode();
            }
        });
        Button button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                EditText editText1 = (EditText) findViewById(R.id.edit_text1);
                EditText editText2 = (EditText) findViewById(R.id.edit_text2);
                EditText editText3 = (EditText) findViewById(R.id.edittext_teltest);
                String username = null, password = null, rightPassword = null, test = null;
                username = editText1.getText().toString();
                password = editText2.getText().toString();
                test = editText3.getText().toString();
                if(!(s.equals(test))) {
                    Toast.makeText(LoginActivity.this, "验证码输入错误", Toast.LENGTH_SHORT).show();
                    CodeUtils codeUtils = new CodeUtils();
                    buttonTest.setImageBitmap(codeUtils.createBitmap());
                    s = codeUtils.getCode();
                    return;
                }
                if(username.equals("")) {
                    Toast.makeText(LoginActivity.this, "您还没有输入用户名！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.equals("")) {
                    Toast.makeText(LoginActivity.this, "您还没有输入密码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Cursor cursor = db.query("User", null, "username=? or telnumber=?", new String[] {username, username}, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        rightPassword = cursor.getString(cursor.getColumnIndex("password"));
                    } while (cursor.moveToNext());
                }
                if(rightPassword == null) {
                    Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                }
                else {
                    MD5Utils md5Utils = new MD5Utils();
                    password = md5Utils.encode(password);
                    if(password.equals(rightPassword)) {
                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, VitalActivity.class);
                        //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        char c[] = username.toCharArray();
                        if(c[0] >= 48 && c[0] <= 57) {
                            cursor = db.query("User", null, "telnumber=?", new String[] {username}, null, null, null, null);
                            if (cursor.moveToFirst()) {
                                do {
                                    username = cursor.getString(cursor.getColumnIndex("username"));
                                } while (cursor.moveToNext());
                            }
                        }
                        intent.putExtra("extra_data", username);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(LoginActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();

                }
            }

        });
    }
}
