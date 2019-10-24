package com.example.a1117p.osam.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.HashMap;

public class ReciptMgtActivity extends AppCompatActivity {
    ListView listView;
    ListAdapter adapter;
    ImageView profile;
    void OvalProfile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profile.setBackground(new ShapeDrawable(new OvalShape()));
            profile.setClipToOutline(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipt_mgt);
        showList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReciptListItem item = (ReciptListItem) adapter.getItem(position);
                Intent intent = new Intent(ReciptMgtActivity.this, ReciptEditActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
                finish();
            }
        });
        profile = findViewById(R.id.profile_img);
        if(MySharedPreferences.getProfileImgPath()!=null){
            File imgFile = new  File(MySharedPreferences.getProfileImgPath());

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profile.setImageBitmap(myBitmap);
            }
        }
        OvalProfile();
    }

    void showList() {
        final ProgressDialog dialog = new ProgressDialog(ReciptMgtActivity.this);
        dialog.setMessage("예약내역을 불러오는 중 입니다.");

        dialog.show();
        listView = findViewById(R.id.hosts);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String html = RequestHttpURLConnection.request("https://be-light.store/api/user/order", null, true, "GET");
                    JSONParser jsonParser = new JSONParser();
                    JSONArray jsonArray = (JSONArray) jsonParser.parse(html);
                    adapter = new ListViewAdapter(jsonArray, ReciptMgtActivity.this);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dialog.dismiss();
                            listView.setAdapter(adapter);
                        }

                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            Toast.makeText(ReciptMgtActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }

                    });
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            final ProgressDialog dialog = new ProgressDialog(ReciptMgtActivity.this);
            dialog.setMessage("짐을 찾는 중 입니다.");

            dialog.show();
            final HashMap<String, String> params = new HashMap<>();

            params.put("userId", MySharedPreferences.getId());
            params.put("reciptNumber", ListViewAdapter.reciptNo + "");
            params.put("randomString", scanResult.getContents());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String html = RequestHttpURLConnection.request("https://be-light.store/api/user/order/status", params, true, "POST");
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dialog.dismiss();
                            JSONParser parser = new JSONParser();
                            try {
                                JSONObject object = (JSONObject) parser.parse(html);
                                Long status = (Long) object.get("status");
                                if (status == 200) {
                                    Toast.makeText(ReciptMgtActivity.this, "성공하였습니다.", Toast.LENGTH_LONG).show();
                                    showList();
                                } else {
                                    Toast.makeText(ReciptMgtActivity.this, "실패하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Toast.makeText(ReciptMgtActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        }

                    });

                }
            }).start();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
