package com.example.a1117p.osam.host;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;

public class ReciptListActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipt_list);
        listView = findViewById(R.id.ReciptList);
        getList(true);
        final Button pending= findViewById(R.id.pending),accept= findViewById(R.id.accept);
        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept.setBackgroundColor(getResources().getColor(R.color.w));
                accept.setTextColor(getResources().getColor(R.color.b));
                pending.setBackgroundColor(getResources().getColor(R.color.main));
                pending.setTextColor(getResources().getColor(R.color.w));
                getList(true);
            }
        });
       accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pending.setBackgroundColor(getResources().getColor(R.color.w));
                pending.setTextColor(getResources().getColor(R.color.b));
                accept.setBackgroundColor(getResources().getColor(R.color.main));
                accept.setTextColor(getResources().getColor(R.color.w));
                getList(false);

            }
        });
    }
    void getList(final boolean b){

        listView.setAdapter(null);
        final ProgressDialog Pdialog = new ProgressDialog(this);
        Pdialog.setMessage("예약내역을 불러오는 중입니다.");

        Pdialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    final String html ;
                    if(b)
                        html= RequestHttpURLConnection.request("https://be-light.store/api/hoster/order/pending?statusCode=0", null, true, "GET");
                    else
                        html= RequestHttpURLConnection.request("https://be-light.store/api/hoster/order/all?statusCode=1", null, true, "GET");
                    JSONParser jsonParser = new JSONParser();
                    JSONArray jsonArray = (JSONArray) jsonParser.parse(html);
                    final ReciptListAdapter adapter = new ReciptListAdapter(jsonArray, ReciptListActivity.this);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            Pdialog.dismiss();
                            listView.setAdapter(adapter);
                        }

                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            Toast.makeText(ReciptListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    });
                }

            }
        }).start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            final ProgressDialog dialog = new ProgressDialog(ReciptListActivity.this);
            dialog.setMessage("짐을 맡는 중 입니다.");

            dialog.show();
            final HashMap<String, String> params = new HashMap<>();

            params.put("userId",ReciptListAdapter.userId);
            params.put("reciptNumber",ReciptListAdapter.reciptNo+"");
            params.put("randomString",scanResult.getContents());
            new Thread(new Runnable(){
                @Override
                public void run() {
                    final String html = RequestHttpURLConnection.request("https://be-light.store/api/hoster/order/status",params,true,"POST");
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            dialog.dismiss();
                            JSONParser parser = new JSONParser();
                            try {
                                JSONObject object = (JSONObject) parser.parse(html);
                                Long status = (Long) object.get("status");
                                if (status == 200) {
                                    Toast.makeText(ReciptListActivity.this, "성공하였습니다.", Toast.LENGTH_LONG).show();
                                    getList(false);
                                } else {
                                    Toast.makeText(ReciptListActivity.this, "실패하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Toast.makeText(ReciptListActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            }    }

                    });

                }
            }).start();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
