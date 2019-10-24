package com.example.a1117p.osam.user;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReciptRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipt_register);
        final Calendar cal = Calendar.getInstance();


        final Button checkin = findViewById(R.id.checkIn);
        final Button checkout = findViewById(R.id.checkOut);

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dialog = new DatePickerDialog(ReciptRegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        checkin.setText(String.format("%04d-%02d-%02d", year, month+1, date));
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                dialog.getDatePicker().setMinDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();


            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dialog = new DatePickerDialog(ReciptRegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        checkout.setText(String.format("%04d-%02d-%02d", year, month+1, date));
                    }
                }, cal.get(Calendar.YEAR)+1, cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                dialog.getDatePicker().setMinDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();


            }
        });
        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idx = ((EditText) findViewById(R.id.hostidx)).getText().toString();
                String checkin = ((Button) findViewById(R.id.checkIn)).getText().toString();
                String checkout = ((Button) findViewById(R.id.checkOut)).getText().toString();
                String paid = ((EditText) findViewById(R.id.paid)).getText().toString();
                if (idx.equals("")) {
                    Toast.makeText(ReciptRegisterActivity.this, "호스트인덱스을 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                } else if (checkin.equals("체크인 날짜")) {
                    Toast.makeText(ReciptRegisterActivity.this, "체크인 날짜를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }else if (checkout.equals("체크아웃 날짜")) {
                    Toast.makeText(ReciptRegisterActivity.this, "체크아웃 날짜를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }else if (paid.equals("")) {
                    Toast.makeText(ReciptRegisterActivity.this, "가격을 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }
                final HashMap params = new HashMap<String, String>();

                params.put("hostIdx", idx);
                params.put("checkIn", checkin);
                params.put("checkOut", checkout);
                params.put("paid", paid);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String html = RequestHttpURLConnection.request("https://be-light.store/api/user/order", params,true, "POST");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Toast.makeText(ReciptRegisterActivity.this, html, Toast.LENGTH_LONG).show();
                            }

                        });

                    }
                }).start();
            }
        });
    }
}
