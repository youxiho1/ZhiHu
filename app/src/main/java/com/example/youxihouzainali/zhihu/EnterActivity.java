package com.example.youxihouzainali.zhihu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EnterActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        dbHelper = new MyDatabaseHelper(this, "Zhihu.db", null, 1);
        dbHelper.getWritableDatabase();
        Button buttonLogin = (Button) findViewById(R.id.button_login);
        Button buttonRegister = (Button) findViewById(R.id.button_register);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(EnterActivity.this, LoginActivity.class);
                startActivity(intent1);
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(EnterActivity.this, RegisterActivity.class);
                startActivity(intent2);
            }
        });
    }
}
