package com.example.a1117p.osam.host;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.net.URISyntaxException;

public class HostMgtActivity extends AppCompatActivity {
    ListView listView;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_mgt);
        listView = findViewById(R.id.hosts);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String html = RequestHttpURLConnection.request("https://be-light.store/api/host", null, true, "GET");
                    JSONParser jsonParser = new JSONParser();
                    JSONArray jsonArray = (JSONArray) jsonParser.parse(html);
                    adapter = new ListViewAdapter(jsonArray);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            listView.setAdapter(adapter);
                        }

                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            Toast.makeText(HostMgtActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }

                    });
                }
            }
        }).start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HostListItem item = (HostListItem) adapter.getItem(position);
                new HostClickDialog(HostMgtActivity.this,item).show();
            }
        });
    }


}
