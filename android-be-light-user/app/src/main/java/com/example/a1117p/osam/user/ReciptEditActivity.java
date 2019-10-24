package com.example.a1117p.osam.user;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ReciptEditActivity extends AppCompatActivity {
    static String drop_date, pick_date;
    long checkInCount;
    long total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipt_edit);
        final ReciptListItem item = getIntent().getParcelableExtra("item");

        final Button checkin = findViewById(R.id.checkIn);
        final Button checkout = findViewById(R.id.checkOut);
        drop_date = item.getCheckin();
        pick_date = item.getCheckOut();
        checkin.setText(drop_date);
        checkout.setText(pick_date);
        ((TextView) findViewById(R.id.drop_name)).setText(item.getHostName());
        ((TextView) findViewById(R.id.drop_addr)).setText(item.getHostaddress());
        ((TextView) findViewById(R.id.drop_addr2)).setText(item.getHostaddress());
        ((TextView) findViewById(R.id.drop_num)).setText(item.getHostUserPhoneNumber());

        ((TextView) findViewById(R.id.pick_name)).setText(item.getGhostName());
        ((TextView) findViewById(R.id.pick_addr)).setText(item.getGhostaddress());
        ((TextView) findViewById(R.id.pick_addr2)).setText(item.getGhostaddress());
        ((TextView) findViewById(R.id.pick_num)).setText(item.getgHostUserPhoneNumber());

        checkInCount = item.getItemCount();
        ((TextView) findViewById(R.id.count)).setText(checkInCount + "");

        total = Long.parseLong(item.getPaid());

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

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tmp = checkin.getText().toString().split("-");
                int[] cal = {Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]), Integer.valueOf(tmp[2])};

                DatePickerDialog dialog = new DatePickerDialog(ReciptEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        drop_date = String.format("%04d-%02d-%02d", year, month + 1, date);
                        checkin.setText(drop_date);
                        refreshPrice();
                    }
                }, cal[0], cal[1] - 1, cal[2]);

                dialog.getDatePicker().setMinDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();


            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] tmp = checkout.getText().toString().split("-");
                int[] cal = {Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]), Integer.valueOf(tmp[2])};

                DatePickerDialog dialog = new DatePickerDialog(ReciptEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        pick_date = String.format("%04d-%02d-%02d", year, month + 1, date);
                        checkout.setText(pick_date);
                        refreshPrice();
                    }
                }, cal[0], cal[1] - 1, cal[2]);

                dialog.getDatePicker().setMinDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();


            }
        });
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null, date2;
        try {
            date1 = format.parse(pick_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        date2 = new Date();
        TextView title = findViewById(R.id.title);
        switch ((int) item.getStatusCode()){

            case -1:
                title.setText("취소된예약");
                break;
            case 0:
                title.setText("신청한예약");
                break;
            case 1:
            case 2:
                title.setText("진행중");
                break;
            case 3:
                title.setText("주문내역");
                break;


        }
        if (date1.compareTo(date2) >= 0 && item.getStatusCode() == 0) {
            findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String checkIn = ((Button) findViewById(R.id.checkIn)).getText().toString();
                    String checkOut = ((Button) findViewById(R.id.checkOut)).getText().toString();
                    String itemCount = ((TextView) findViewById(R.id.count)).getText().toString();
                    final HashMap params = new HashMap<String, String>();
                    params.put("reciptNumber", item.getReciptNumber());
                    params.put("checkIn", checkIn);
                    params.put("checkOut", checkOut);
                    params.put("itemCount", itemCount);
                    final ProgressDialog dialog = new ProgressDialog(ReciptEditActivity.this);
                    dialog.setMessage("예약내역 수정 중 입니다.");

                    dialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String html = RequestHttpURLConnection.request("https://be-light.store/api/user/order?_method=PUT", params, true, "POST");
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    JSONParser parser = new JSONParser();
                                    try {
                                        JSONObject object = (JSONObject) parser.parse(html);
                                        Long status = (Long) object.get("status");
                                        if (status == 200) {
                                            Toast.makeText(ReciptEditActivity.this, "예약내역 수정에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(ReciptEditActivity.this, ReciptMgtActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(ReciptEditActivity.this, "예약내역 수정에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Toast.makeText(ReciptEditActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
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
                    final ProgressDialog dialog = new ProgressDialog(ReciptEditActivity.this);
                    dialog.setMessage("예약내역 삭제 중 입니다.");

                    dialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final HashMap params = new HashMap<String, String>();

                            params.put("reciptNumber", item.getReciptNumber());
                            final String html = RequestHttpURLConnection.request("https://be-light.store/api/user/order?_method=DELETE", params, true, "POST");
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    JSONParser parser = new JSONParser();
                                    try {
                                        JSONObject object = (JSONObject) parser.parse(html);
                                        Long status = (Long) object.get("status");
                                        if (status == 200) {
                                            Toast.makeText(ReciptEditActivity.this, "예약내역 삭제에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(ReciptEditActivity.this, ReciptMgtActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(ReciptEditActivity.this, "예약내역 삭제에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Toast.makeText(ReciptEditActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                    }
                                }

                            });

                        }
                    }).start();
                }
            });
            findViewById(R.id.review_btn1).setVisibility(View.GONE);
            findViewById(R.id.review_btn2).setVisibility(View.GONE);
        } else if (item.getStatusCode() == 3) {
            findViewById(R.id.edit_btns).setVisibility(View.GONE);
            findViewById(R.id.review_btn1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new ReviewRegisterDialog(ReciptEditActivity.this, item.getHostName(), item.getHostaddress(), item.getHostUserPhoneNumber(), "", item.getHostIdx(), 5);
                    dialog.show();
                }
            });
            findViewById(R.id.review_btn2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new ReviewRegisterDialog(ReciptEditActivity.this, item.getGhostName(), item.getGhostaddress(), item.getgHostUserPhoneNumber(), "", item.getGhostidx(), 5);
                    dialog.show();
                }
            });
        } else {
            findViewById(R.id.edit_btns).setVisibility(View.GONE);

            findViewById(R.id.review_btn1).setVisibility(View.GONE);
            findViewById(R.id.review_btn2).setVisibility(View.GONE);
        }
        refreshPrice();
    }

    void refreshPrice() {
        ((TextView) findViewById(R.id.count)).setText(checkInCount + "");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate, endDate;
        try {
            beginDate = formatter.parse(drop_date);
            endDate = formatter.parse(pick_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return;
        }


        long diff = endDate.getTime() - beginDate.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) - 1;

        ((TextView) findViewById(R.id.default_calc)).setText(checkInCount + " objects x 1Day x 7000won");
        ((TextView) findViewById(R.id.default_price)).setText((checkInCount * 7000) + "won");

        ((TextView) findViewById(R.id.additional_calc)).setText(checkInCount + " objects x " + diffDays + "Day x 7000won");
        ((TextView) findViewById(R.id.additional_price)).setText((checkInCount * 7000 * diffDays) + "won");
        ((TextView) findViewById(R.id.total_price)).setText((total = checkInCount * 7000 * (diffDays + 1)) + "won");
    }
}
