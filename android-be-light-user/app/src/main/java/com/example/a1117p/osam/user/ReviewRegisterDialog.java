package com.example.a1117p.osam.user;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;

public class ReviewRegisterDialog extends Dialog {
    String hostname, hostAddress, hostTel,review;
    ArrayList<Button> stars;
    Activity context;
    long hostidx, reviewScore = 5;
    MyReviewListItem item=null;

    public ReviewRegisterDialog(@NonNull Activity activity, String hostname, String hostAddress, String hostTel,String review, long hostidx,long reviewScore) {
        super(activity);
        this.context = activity;
        this.hostAddress = hostAddress;
        this.hostname = hostname;
        this.hostTel = hostTel;
        this.hostidx = hostidx;
        this.review=review;
        this.reviewScore=reviewScore;
    }
    public ReviewRegisterDialog(@NonNull Activity activity,MyReviewListItem item){
        super(activity);
        this.context=activity;
        this.item=item;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        getWindow().setAttributes(layoutParams);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(R.layout.review_register_dialog);
        stars = new ArrayList<>();
        stars.add((Button) findViewById(R.id.star1));
        stars.add((Button) findViewById(R.id.star2));
        stars.add((Button) findViewById(R.id.star3));
        stars.add((Button) findViewById(R.id.star4));
        stars.add((Button) findViewById(R.id.star5));
        if(item!=null)
            reviewScore = item.getReviewScore();
        for (int i = 0; i < 5; i++) {
            final int finalI = i;
            Button tmp=stars.get(i);
            tmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int j;
                    reviewScore = finalI+1;
                    for (j = 0; j <= finalI; j++)
                        stars.get(j).setText("★");
                    for (j = finalI + 1; j < 5; j++)
                        stars.get(j).setText("☆");
                }
            });
            if(i<reviewScore)
                tmp.setText("★");
            else
                tmp.setText("☆");
        }
        if(item==null) {
            findViewById(R.id.edit).setVisibility(View.GONE);

            ((TextView) findViewById(R.id.host_name)).setText(hostname);
            ((TextView) findViewById(R.id.host_addr)).setText(hostAddress);
            ((TextView) findViewById(R.id.host_num)).setText(hostTel);
            ((EditText)findViewById(R.id.review)).setText(review);
            findViewById(R.id.review_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String review = ((EditText) findViewById(R.id.review)).getText().toString();
                    if (review.equals("")) {
                        Toast.makeText(context, "내용을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("review", review);
                    params.put("reviewScore", reviewScore + "");
                    params.put("hostIdx", hostidx + "");
                    final ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("리뷰를 등록 중 입니다.");

                    dialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String html = RequestHttpURLConnection.request("https://be-light.store/api/review", params, true, "POST");
                            context.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    JSONParser parser = new JSONParser();
                                    try {
                                        JSONObject object = (JSONObject) parser.parse(html);
                                        Long status = (Long) object.get("status");
                                        if (status == 200) {
                                            Toast.makeText(context, "리뷰 작성에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        } else {
                                            Toast.makeText(context, "리뷰 작성에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                    }
                                }

                            });

                        }
                    }).start();
                }
            });

            findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }else{

            findViewById(R.id.register).setVisibility(View.GONE);

        //    ((TextView) findViewById(R.id.host_name)).setText(item.get);
           // ((TextView) findViewById(R.id.host_addr)).setText();
            ((TextView) findViewById(R.id.host_num)).setText(hostTel);
            ((EditText)findViewById(R.id.review)).setText(item.getReview());
            findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String review = ((EditText) findViewById(R.id.review)).getText().toString();
                    if (review.equals("")) {
                        Toast.makeText(context, "내용을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("review", review);
                    params.put("reviewScore", reviewScore + "");
                    params.put("hostIdx", item.getHostIdx()+"");
                    params.put("reviewNumber", item.getReviewNumber()+"");
                    final ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("리뷰를 수정 중 입니다.");

                    dialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String html = RequestHttpURLConnection.request("https://be-light.store/api/review?_method=PUT", params, true, "POST");
                            context.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    JSONParser parser = new JSONParser();
                                    try {
                                        JSONObject object = (JSONObject) parser.parse(html);
                                        Long status = (Long) object.get("status");
                                        if (status == 200) {
                                            Toast.makeText(context, "리뷰 수정에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        } else {
                                            Toast.makeText(context, "리뷰 수정에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                    }
                                }

                            });

                        }
                    }).start();
                }
            });

            findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("hostIdx", item.getHostIdx()+"");
                    params.put("reviewNumber", item.getReviewNumber()+"");
                    final ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("리뷰를 삭제 중 입니다.");

                    dialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String html = RequestHttpURLConnection.request("https://be-light.store/api/review?_method=DELETE", params, true, "POST");
                            context.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    JSONParser parser = new JSONParser();
                                    try {
                                        JSONObject object = (JSONObject) parser.parse(html);
                                        Long status = (Long) object.get("status");
                                        if (status == 200) {
                                            Toast.makeText(context, "리뷰 삭제에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        } else {
                                            Toast.makeText(context, "리뷰 삭제에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                    }
                                }

                            });

                        }
                    }).start();
                }
            });
        }
    }

}
