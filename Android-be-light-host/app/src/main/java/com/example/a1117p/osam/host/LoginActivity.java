package com.example.a1117p.osam.host;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = ((EditText) findViewById(R.id.id)).getText().toString();
                String passwd = ((EditText) findViewById(R.id.passwd)).getText().toString();
                if (id.equals("")) {
                    Toast.makeText(LoginActivity.this, "ID를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (passwd.equals("")) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }
                final HashMap params = new HashMap<String, String>();

                params.put("hostUserId", id);
                params.put("hostUserPassword", passwd);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String html = RequestHttpURLConnection.request("https://be-light.store/api/auth/hoster/login", params, "POST");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Toast.makeText(LoginActivity.this, html, Toast.LENGTH_LONG).show();
                            }

                        });

                    }
                }).start();
            }
        });
    }


}
