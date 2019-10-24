package com.example.a1117p.osam.user;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {
    final String pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{9,}$";
    String[] Permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MySharedPreferences.init(this);
        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(i);
                //finish();
            }
        });
        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.login_form).setVisibility(View.VISIBLE);
                ((Button) v).setText("Log in");
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String id = ((EditText) findViewById(R.id.id)).getText().toString();
                        final String passwd = ((EditText) findViewById(R.id.passwd)).getText().toString();
                        if (id.equals("")) {
                            Toast.makeText(SplashActivity.this, "ID를 입력하세요", Toast.LENGTH_LONG).show();
                            return;
                        } else if (passwd.equals("")) {
                            Toast.makeText(SplashActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_LONG).show();
                            return;
                        } else if (!Pattern.compile(pwPattern).matcher(passwd).matches()) {
                            new AlertDialog.Builder(SplashActivity.this).setMessage("비밀번호는 영문자,숫자,특수문자를 1개 이상씩 포함하여 9자리 이상이여야 합니다.")
                                    .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create().show();

                            return;
                        }
                        final ProgressDialog dialog = new ProgressDialog(SplashActivity.this);
                        dialog.setMessage("로그인중입니다.");

                        dialog.show();
                        final HashMap params = new HashMap<String, String>();

                        params.put("userId", id);
                        params.put("userPassword", passwd);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String html = RequestHttpURLConnection.request("https://be-light.store/api/auth/login", params, "POST");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        JSONParser parser = new JSONParser();
                                        try {
                                            JSONObject object = (JSONObject) parser.parse(html);
                                            Long status = (Long) object.get("status");
                                            if (status == 200) {
                                                MySharedPreferences.setIdPw(id, passwd);


                                                Intent i = new Intent(SplashActivity.this, MapActivity.class);
                                                startActivity(i);
                                                finish();
                                            } else {
                                                Toast.makeText(SplashActivity.this, "ID와 PW를 확인하세요.", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            Toast.makeText(SplashActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                        }).start();
                    }
                });
            }
        });
        if (checkPermission()) {
            ActivityCompat.requestPermissions(this, Permissions, 0);

        } else {
            StartThread(getIntent().getBooleanExtra("needLoading", true));
        }
    }

    boolean checkPermission() {
        for (String perm : Permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length < Permissions.length || checkPermission()) {
            Toast.makeText(this, "권한없음!!!!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            StartThread(true);
        }

    }

    void StartThread(boolean b) {
        //Intent i = new Intent(SplashActivity.this, MapActivity.class);
        //startActivity(i);
        //finish();
        if (b) {
            String id = MySharedPreferences.getId();
            String passwd = MySharedPreferences.getPw();
            if (id == null || passwd == null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View v = findViewById(R.id.btns);
                                v.setVisibility(View.VISIBLE);
                                AlphaAnimation animation = new AlphaAnimation(0, 1);
                                animation.setDuration(1000);
                                v.startAnimation(animation);

                            }
                        });
                    }
                }).start();
            } else {
                final ProgressDialog dialog = new ProgressDialog(SplashActivity.this);
                dialog.setMessage("자동 로그인중입니다.");
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                });
                dialog.show();
                final HashMap params = new HashMap<String, String>();

                params.put("userId", id);
                params.put("userPassword", passwd);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String html = RequestHttpURLConnection.request("https://be-light.store/api/auth/login", params, "POST");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                JSONParser parser = new JSONParser();
                                try {
                                    JSONObject object = (JSONObject) parser.parse(html);
                                    Long status = (Long) object.get("status");
                                    if (status == 200) {


                                        Intent i = new Intent(SplashActivity.this, MapActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(SplashActivity.this, "자동로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                        MySharedPreferences.removeIdPw();
                                        StartThread(false);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Toast.makeText(SplashActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                }).start();
            }
        } else {
            findViewById(R.id.btns).setVisibility(View.VISIBLE);
            findViewById(R.id.login_btn).performClick();

        }
    }
}
