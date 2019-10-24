package com.example.a1117p.osam.host;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class HostEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_edit);
        final HostListItem item = getIntent().getParcelableExtra("item");
        ((EditText)findViewById(R.id.hostname)).setText(item.getTel());
        ((EditText)findViewById(R.id.tel)).setText(item.getName());
        ((EditText)findViewById(R.id.address)).setText(item.getAddress());
        ((EditText)findViewById(R.id.postalcode)).setText(item.getPostalCode());

        findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText) findViewById(R.id.hostname)).getText().toString();
                String tel = ((EditText) findViewById(R.id.tel)).getText().toString();
                String address = ((EditText) findViewById(R.id.address)).getText().toString();
                String postalCode = ((EditText) findViewById(R.id.postalcode)).getText().toString();
                if (name.equals("")) {
                    Toast.makeText(HostEditActivity.this, "호스트명을 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (tel.equals("")) {
                    Toast.makeText(HostEditActivity.this, "전화번호를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }else if (address.equals("")) {
                    Toast.makeText(HostEditActivity.this, "주소를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }else if (postalCode.equals("")) {
                    Toast.makeText(HostEditActivity.this, "우편번호를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }
                final HashMap params = new HashMap<String, String>();
                params.put("idx",item.getIdx());
                params.put("hostName", name);
                params.put("hostTel", tel);
                params.put("hostAddress", address);
                params.put("hostPostalCode", postalCode);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String html = RequestHttpURLConnection.request("https://be-light.store/api/host?_method=PUT", params,true, "POST");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Toast.makeText(HostEditActivity.this, html, Toast.LENGTH_LONG).show();
                            }

                        });

                    }
                }).start();
            }
        });
    }
}
