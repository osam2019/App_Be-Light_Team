package com.example.a1117p.osam.user;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final String pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{9,}$",emailPattern="^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$",phonePattern="^(\\d{3}|\\d{4})[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";


        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = ((EditText) findViewById(R.id.name)).getText().toString();
                final String id = ((EditText) findViewById(R.id.id)).getText().toString();
                final String passwd = ((EditText) findViewById(R.id.passwd)).getText().toString();
                String passwd_confirm = ((EditText) findViewById(R.id.passwd_confirm)).getText().toString();
                final String email = ((EditText) findViewById(R.id.email)).getText().toString();
                final String phone = ((EditText) findViewById(R.id.phone)).getText().toString();
                final String address = ((EditText) findViewById(R.id.address)).getText().toString();
                if (id.equals("")) {
                    Toast.makeText(RegisterActivity.this, "ID를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (id.length() < 4) {
                    Toast.makeText(RegisterActivity.this, "ID가 너무 짧습니다.", Toast.LENGTH_LONG).show();
                    return;
                } else if (name.equals("")) {
                    Toast.makeText(RegisterActivity.this, "이름을 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (passwd.equals("")) {
                    Toast.makeText(RegisterActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (!Pattern.compile(pwPattern).matcher(passwd).matches()) {
                    new AlertDialog.Builder(RegisterActivity.this).setMessage("비밀번호는 영문자,숫자,특수문자를 1개 이상씩 포함하여 9자리 이상이여야 합니다.")
                            .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();

                    return;
                } else if (passwd_confirm.equals("")) {
                    Toast.makeText(RegisterActivity.this, "비밀번호확인을 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (!passwd.equals(passwd_confirm)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호와 비밀번호 확인이 같지않습니다.", Toast.LENGTH_LONG).show();
                    return;
                } else if (email.equals("")) {
                    Toast.makeText(RegisterActivity.this, "이메일을 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (!Pattern.compile(emailPattern).matcher(email).matches()) {

                    Toast.makeText(RegisterActivity.this, "이메일의 형식이 정상적이지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                }else if (phone.equals("")) {
                    Toast.makeText(RegisterActivity.this, "전화번호를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }else if (!Pattern.compile(phonePattern).matcher(phone).matches()) {

                    Toast.makeText(RegisterActivity.this, "전화번호의 형식이 정상적이지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                } else if (address.equals("")) {
                    Toast.makeText(RegisterActivity.this, "주소를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }
                final ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                dialog.setMessage("회원가입 중 입니다.");

                dialog.show();
                final HashMap<String, String> params = new HashMap<>();

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();

                                    return;
                                }

                                String token = task.getResult().getToken();

                                params.put("userId", id);
                                params.put("userName", name);
                                params.put("userPassword", passwd);
                                params.put("userEmail", email);
                                params.put("userPhoneNumber", phone);
                                params.put("userAddress", address);
                                params.put("userDeviceToken", token);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final String html = RequestHttpURLConnection.request("https://be-light.store/api/auth/register", params, "POST");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                JSONParser parser = new JSONParser();
                                                try {
                                                    JSONObject object = (JSONObject) parser.parse(html);
                                                    Long status = (Long) object.get("status");
                                                    if (status == 200) {
                                                        Toast.makeText(RegisterActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(RegisterActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }
                                }).start();
                             }
                        });



            }
        });
    }


}
