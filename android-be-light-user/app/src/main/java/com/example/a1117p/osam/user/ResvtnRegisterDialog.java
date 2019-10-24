package com.example.a1117p.osam.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ResvtnRegisterDialog extends Dialog {
    static InfoWindowData drop_data = null, pick_data = null;
    static String drop_date, pick_date;
    Activity context;
    Integer checkInCount;
    long total;

    /*
                    final HashMap params = new HashMap<String, String>();
                    String paid = "6000";
                    String checkin = ((Button) findViewById(R.id.checkIn)).getText().toString();
                    String checkout = ((Button) findViewById(R.id.checkOut)).getText().toString();

                    params.put("checkIn", checkin);
                    params.put("checkOut", checkout);
                    params.put("paid", paid);
                    params.put("hostIdx", drop_data.hostIdx + "");
                    params.put("gHostIdx", pick_data.hostIdx + "");
                    params.put("itemCount", checkInCount);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String html = RequestHttpURLConnection.request("https://be-light.store/api/user/order", params, true, "POST");
                            context.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    Toast.makeText(context, html, Toast.LENGTH_LONG).show();
                                }

                            });

                        }
                    }).start();
                    */
    public ResvtnRegisterDialog(@NonNull Activity activity, Object[] datas) {
        super(activity);
        this.context = activity;
        drop_data = (InfoWindowData) datas[0];
        pick_data = (InfoWindowData) datas[1];
        drop_date = (String) datas[2];
        pick_date = (String) datas[3];
        checkInCount = (Integer) datas[4];
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        getWindow().setAttributes(layoutParams);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(R.layout.recipt_register);

        ((TextView) findViewById(R.id.drop_name)).setText(drop_data.hostName);
        ((TextView) findViewById(R.id.drop_addr)).setText(drop_data.hostAddress);
        ((TextView) findViewById(R.id.drop_addr2)).setText(drop_data.hostAddress);

        ((TextView) findViewById(R.id.drop_num)).setText(drop_data.hostTel);
        ((TextView) findViewById(R.id.drop_dist)).setText(String.format("%.1f",drop_data.dist)+"km");
        int star= (int) drop_data.score;
        String stars="";
        for(int i=0;i<star;i++)
            stars+="★";
        if(drop_data.score-star>0.5)
            stars+="☆";
        ImageView pick= findViewById(R.id.pick_Img),drop=findViewById(R.id.drop_Img);

        if(pick_data.hostImage!=null&&!pick_data.hostImage.equals("")){
            new DownloadImageTask(pick,true)
                    .execute(pick_data.hostImage);
        }
        if(drop_data.hostImage!=null&&!drop_data.hostImage.equals("")){
            new DownloadImageTask(drop,true)
                    .execute(drop_data.hostImage);
        }
        OvalView(findViewById(R.id.pick_Img));
        OvalView(findViewById(R.id.drop_Img));
        ((TextView) findViewById(R.id.drop_score_star)).setText(stars);
        ((TextView) findViewById(R.id.drop_score)).setText(String.format("%.1f",drop_data.score));

        ((TextView) findViewById(R.id.pick_name)).setText(pick_data.hostName);
        ((TextView) findViewById(R.id.pick_addr)).setText(pick_data.hostAddress);
        ((TextView) findViewById(R.id.pick_addr2)).setText(pick_data.hostAddress);

        ((TextView) findViewById(R.id.pick_num)).setText(pick_data.hostTel);
        ((TextView) findViewById(R.id.pick_dist)).setText(String.format("%.1f",pick_data.dist)+"km");
        star= (int) pick_data.score;
        stars="";
        for(int i=0;i<star;i++)
            stars+="★";
        if(pick_data.score-star>0.5)
            stars+="☆";
        ((TextView) findViewById(R.id.pick_score_star)).setText(stars);
        ((TextView) findViewById(R.id.pick_score)).setText(String.format("%.1f",pick_data.score));


        findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInCount <= 2)
                    checkInCount = 2;
                --checkInCount;
                refreshPrice();
            }
        });
        findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++checkInCount;
                refreshPrice();
            }
        });


        final Button resvtn = findViewById(R.id.register_btn);

        final Button checkinB = findViewById(R.id.checkIn);
        final Button checkoutB = findViewById(R.id.checkOut);
        checkinB.setText(drop_date);
        checkoutB.setText(pick_date);

        checkinB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date beginDate;
                try {
                    beginDate = formatter.parse(drop_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    dismiss();
                    return;
                }
                final Calendar cal = new GregorianCalendar();
                cal.setTime(beginDate);

                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        drop_date = String.format("%04d-%02d-%02d", year, month + 1, date);
                        checkinB.setText(drop_date);
                        refreshPrice();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                dialog.getDatePicker().setMinDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();


            }
        });
        checkoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date endDate;
                try {
                    endDate = formatter.parse(pick_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    dismiss();
                    return;
                }
                final Calendar cal = new GregorianCalendar();
                cal.setTime(endDate);


                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        pick_date = String.format("%04d-%02d-%02d", year, month + 1, date);
                        checkoutB.setText(pick_date);
                        refreshPrice();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                dialog.getDatePicker().setMinDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();


            }
        });
        findViewById(R.id.code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "프로모션코드가 뭐지;;;;", Toast.LENGTH_LONG).show();
            }
        });

        resvtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage("등록중입니다.");

                dialog.show();
                final HashMap<String, String> params = new HashMap<>();
                String checkin = ((Button) findViewById(R.id.checkIn)).getText().toString();
                String checkout = ((Button) findViewById(R.id.checkOut)).getText().toString();

                params.put("checkIn", checkin);
                params.put("checkOut", checkout);
                params.put("paid", total + "");
                params.put("hostIdx", drop_data.hostIdx + "");
                params.put("gHostIdx", pick_data.hostIdx + "");
                params.put("itemCount", checkInCount + "");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String html = RequestHttpURLConnection.request("https://be-light.store/api/user/order", params, true, "POST");
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                dialog.dismiss();
                                JSONParser parser = new JSONParser();
                                try {
                                    JSONObject object = (JSONObject) parser.parse(html);
                                    long status = (Long) object.get("status");
                                    if (status == 200) {
                                        Toast.makeText(context, "예약등록에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "예약등록에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                    }
                                    dismiss();
                                } catch (org.json.simple.parser.ParseException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            }

                        });

                    }
                }).start();
            }
        });

        refreshPrice();
    }
    void OvalView(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            v.setBackground(new ShapeDrawable(new OvalShape()));
            v.setClipToOutline(true);
        }
    }
    void refreshPrice() {
        ((TextView) findViewById(R.id.count)).setText(checkInCount + "");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate, endDate;
        try {
            beginDate = formatter.parse(drop_date);
            endDate = formatter.parse(pick_date);
        } catch (ParseException e) {
            e.printStackTrace();
            dismiss();
            return;
        }


        long diff = endDate.getTime() - beginDate.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) - 1;
        if(diffDays<0)
            diffDays=0;

        ((TextView) findViewById(R.id.default_calc)).setText(checkInCount + " objects x 1Day x 7000won");
        ((TextView) findViewById(R.id.default_price)).setText((checkInCount * 7000) + "won");

        ((TextView) findViewById(R.id.additional_calc)).setText(checkInCount + " objects x " + diffDays + "Day x 7000won");
        ((TextView) findViewById(R.id.additional_price)).setText((checkInCount * 7000 * diffDays) + "won");
        ((TextView) findViewById(R.id.total_price)).setText((total = checkInCount * 7000 * (diffDays + 1)) + "won");
    }
}
