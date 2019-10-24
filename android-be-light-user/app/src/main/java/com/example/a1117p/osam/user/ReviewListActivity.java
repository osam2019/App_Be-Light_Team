package com.example.a1117p.osam.user;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;

public class ReviewListActivity extends AppCompatActivity {
    ListView listView;
    ListAdapter adapter;
    ImageView profile;

    void OvalProfile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profile.setBackground(new ShapeDrawable(new OvalShape()));
            profile.setClipToOutline(true);
        }
    }

    void showList() {
        final ProgressDialog dialog = new ProgressDialog(ReviewListActivity.this);
        dialog.setMessage("리뷰내역을 불러오는 중 입니다.");

        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String html = RequestHttpURLConnection.request("https://be-light.store/api/review", null, true, "GET");
                    JSONParser jsonParser = new JSONParser();
                    JSONArray jsonArray = (JSONArray) jsonParser.parse(html);
                    adapter = new MyReviewListAdapter(jsonArray);
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

                            Toast.makeText(ReviewListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }

                    });
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        listView = findViewById(R.id.reviews);
        showList();
        profile=findViewById(R.id.profile_img);
        if(MySharedPreferences.getProfileImgPath()!=null){
            File imgFile = new  File(MySharedPreferences.getProfileImgPath());

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profile.setImageBitmap(myBitmap);
            }
        }
        OvalProfile();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyReviewListItem item = (MyReviewListItem) adapter.getItem(position);
                Dialog dialog1 = new ReviewRegisterDialog(ReviewListActivity.this, item);
                dialog1.show();
                dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        showList();
                    }
                });
            }
        });
    }

}
