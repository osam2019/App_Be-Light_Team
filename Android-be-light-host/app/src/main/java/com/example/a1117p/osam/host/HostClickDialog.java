package com.example.a1117p.osam.host;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class HostClickDialog extends Dialog {
    HostListItem item;
    Activity activity;

    public HostClickDialog(@NonNull Activity activity, HostListItem item) {
        super(activity);
        this.activity = activity;
        this.item = item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.host_click_dialog);


        findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, HostEditActivity.class);
                intent.putExtra("item", item);
                activity.startActivity(intent);
                dismiss();
            }
        });

        findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final HashMap params = new HashMap<String, String>();

                        params.put("hostIdx", item.getIdx());
                        final String html = RequestHttpURLConnection.request("https://be-light.store/api/host?_method=DELETE", params,true, "POST");
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Toast.makeText(activity, html, Toast.LENGTH_LONG).show();
                            }

                        });

                    }
                }).start();
                dismiss();
            }
        });
    }

}
