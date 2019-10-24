package com.example.a1117p.osam.user;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {
    File file = null;
    ImageView profile;
    String path = null;

    void OvalProfile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profile.setBackground(new ShapeDrawable(new OvalShape()));
            profile.setClipToOutline(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{9,}$", emailPattern = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", phonePattern = "^(\\d{3}|\\d{4})[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";

        final ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
        dialog.setMessage("프로필을 불러오는 중 입니다.");

        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String html = RequestHttpURLConnection.request("https://be-light.store/api/user", null, true, "GET");
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObj = (JSONObject) jsonParser.parse(html);
                    final JSONObject jsonObj2 = (JSONObject) jsonObj.get("user");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            ((TextView) findViewById(R.id.id)).setText((String) jsonObj2.get("userId"));
                            ((TextView) findViewById(R.id.name)).setText((String) jsonObj2.get("userName"));
                            ((EditText) findViewById(R.id.email)).setText((String) jsonObj2.get("userEmail"));
                            ((EditText) findViewById(R.id.phone)).setText((String) jsonObj2.get("userPhoneNumber"));
                            ((EditText) findViewById(R.id.address)).setText((String) jsonObj2.get("userAddress"));
                        }

                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }

                    });
                }
            }
        }).start();
        profile = findViewById(R.id.profile_img);
        if(MySharedPreferences.getProfileImgPath()!=null){
            File imgFile = new  File(MySharedPreferences.getProfileImgPath());

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profile.setImageBitmap(myBitmap);
            }
        }
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });
        findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String phone = ((EditText) findViewById(R.id.phone)).getText().toString();
                String address = ((EditText) findViewById(R.id.address)).getText().toString();
                String passwd = ((EditText) findViewById(R.id.passwd)).getText().toString();
                String passwd_confirm = ((EditText) findViewById(R.id.passwd_confirm)).getText().toString();
                if (email.equals("")) {
                    Toast.makeText(ProfileActivity.this, "이메일을 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (!Pattern.compile(emailPattern).matcher(email).matches()) {

                    Toast.makeText(ProfileActivity.this, "이메일의 형식이 정상적이지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                } else if (phone.equals("")) {
                    Toast.makeText(ProfileActivity.this, "전화번호를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (!Pattern.compile(phonePattern).matcher(phone).matches()) {

                    Toast.makeText(ProfileActivity.this, "전화번호의 형식이 정상적이지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                } else if (address.equals("")) {
                    Toast.makeText(ProfileActivity.this, "주소를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }else if(file==null){

                    Toast.makeText(ProfileActivity.this, "프로필사진을 변경해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                final HashMap<String, String> params = new HashMap<>();

                params.put("userEmail", email);
                params.put("userPhoneNumber", phone);
                params.put("userAddress", address);
                if (!passwd.equals("")) {
                    if (passwd_confirm.equals("")) {
                        Toast.makeText(ProfileActivity.this, "비밀번호확인을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (!Pattern.compile(pwPattern).matcher(passwd).matches()) {
                        new AlertDialog.Builder(ProfileActivity.this).setMessage("비밀번호는 영문자,숫자,특수문자를 1개 이상씩 포함하여 9자리 이상이여야 합니다.")
                                .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();

                        return;
                    } else if (!passwd_confirm.equals(passwd)) {
                        Toast.makeText(ProfileActivity.this, "비밀번호와 비밀번호 확인이 같지않습니다.", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        params.put("userPassword", passwd);
                    }
                }
                final ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
                dialog.setMessage("프로필을 수정하는 중 입니다.");

                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String html = RequestHttpURLConnection.requestWith_File("https://be-light.store/api/user?_method=PUT", params, true, "POST", file);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                JSONParser parser = new JSONParser();
                                try {
                                    JSONObject object = (JSONObject) parser.parse(html);
                                    Long status = (Long) object.get("status");
                                    if (status == 200) {
                                        MySharedPreferences.setProfileImgPath(path);
                                        Toast.makeText(ProfileActivity.this, "프로필을 수정하였습니다.", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "프로필수정을 실패하였습니다.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ProfileActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        });

                    }
                }).start();
            }
        });
        OvalProfile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                try {
                    path = PathUtil.getPath(this,uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                file = new File(path);
                profile.setImageURI(uri);
                OvalProfile();
            }
        }
    }
}
