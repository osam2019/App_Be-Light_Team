package com.example.a1117p.osam.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public class ResvtnClickDialog extends Dialog {
    static InfoWindowData drop_data = null, pick_data = null;
    static String drop_date, pick_date;
    InfoWindowData data;
    Activity context;
    Integer checkInCount = 0;


    public ResvtnClickDialog(@NonNull Activity activity, InfoWindowData data, int count) {
        super(activity);
        this.context = activity;
        this.data = data;
        checkInCount = count;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        getWindow().setAttributes(layoutParams);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(R.layout.resvtn_click_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (checkInCount != 0) {
            ((TextView) findViewById(R.id.checkInCount)).setText(checkInCount + "");
            ((TextView) findViewById(R.id.checkOutCount)).setText(checkInCount + "");
        }
        final Button resvtn = findViewById(R.id.rcipt_btn);
        final Calendar cal = Calendar.getInstance();
        final Button checkinB = findViewById(R.id.checkIn);
        final Button checkoutB = findViewById(R.id.checkOut);
        if (drop_date != null)
            checkinB.setText(drop_date);
        if (pick_date != null)
            checkoutB.setText(pick_date);
        checkinB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        drop_date = String.format("%04d-%02d-%02d", year, month + 1, date);
                        checkinB.setText(drop_date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                dialog.getDatePicker().setMinDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();


            }
        });
        checkoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        pick_date = String.format("%04d-%02d-%02d", year, month + 1, date);
                        checkoutB.setText(pick_date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                dialog.getDatePicker().setMinDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();


            }
        });
        if (drop_data != null) {
            ((TextView) findViewById(R.id.drop_name)).setText(drop_data.hostName);
            ((TextView) findViewById(R.id.drop_addr)).setText(drop_data.hostAddress);
        }
        if (pick_data != null) {
            ((TextView) findViewById(R.id.pick_name)).setText(pick_data.hostName);
            ((TextView) findViewById(R.id.pick_addr)).setText(pick_data.hostAddress);
        }
        if (pick_data == null || drop_data == null) {
            resvtn.setText("확인");
        }
        findViewById(R.id.drop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.equals(pick_data)) {
                    new AlertDialog.Builder(context)
                            .setTitle("확인창")
                            .setMessage("맡기신곳에서 찾아가시겠습니까?")
                            .setPositiveButton("예", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    drop_data = data;

                                    ((TextView) findViewById(R.id.drop_name)).setText(drop_data.hostName);
                                    ((TextView) findViewById(R.id.drop_addr)).setText(drop_data.hostAddress);
                                    resvtn.setText("예약");

                                }
                            }).setNegativeButton("아니요", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(context, "짐을 맡기실 곳을 선택해주세요.", Toast.LENGTH_LONG).show();
                            EditText editText = context.findViewById(R.id.search_edit);
                            editText.setText("");
                            editText.requestFocus();
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


                            dismiss();
                        }
                    }).create().show();
                } else {
                    drop_data = data;
                    //((TextView) findViewById(R.id.drop_what)).setText("여기가 뭐하는 부분인지 모름");

                    ((TextView) findViewById(R.id.drop_name)).setText(drop_data.hostName);
                    ((TextView) findViewById(R.id.drop_addr)).setText(drop_data.hostAddress);
                    if (pick_data != null) {
                        resvtn.setText("예약");
                    }
                }
            }
        });
        findViewById(R.id.pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.equals(drop_data)) {
                    new AlertDialog.Builder(context)
                            .setTitle("확인창")
                            .setMessage("맡기신곳에서 찾아가시겠습니까?")
                            .setPositiveButton("예", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    pick_data = data;

                                    ((TextView) findViewById(R.id.pick_name)).setText(pick_data.hostName);
                                    ((TextView) findViewById(R.id.pick_addr)).setText(pick_data.hostAddress);
                                    resvtn.setText("예약");

                                }
                            }).setNegativeButton("아니요", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(context, "짐을 찾아가실 곳을 선택해주세요.", Toast.LENGTH_LONG).show();
                            EditText editText = context.findViewById(R.id.search_edit);
                            editText.setText("");
                            editText.requestFocus();
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


                            dismiss();
                        }
                    }).create().show();
                } else {
                    pick_data = data;

                    ((TextView) findViewById(R.id.pick_name)).setText(pick_data.hostName);
                    ((TextView) findViewById(R.id.pick_addr)).setText(pick_data.hostAddress);
                    if (drop_data != null) {
                        resvtn.setText("예약");
                    }
                }
            }
        });
        resvtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pick_data == null || drop_data == null)
                    dismiss();
                else {
                    if (drop_date == null || drop_date.equals("")) {
                        Toast.makeText(context, "맡기는 날짜를 입력하세요.", Toast.LENGTH_LONG).show();
                        return;
                    } else if (pick_date == null || pick_date.equals("")) {
                        Toast.makeText(context, "찾는 날짜를 입력하세요.", Toast.LENGTH_LONG).show();
                        return;
                    } else if (checkInCount == 0) {
                        Toast.makeText(context, "물품의 개수를 입력하세요.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Object[] datas = new Object[5];
                    datas[0] = drop_data;
                    datas[1] = pick_data;
                    datas[2] = drop_date;
                    datas[3] = pick_date;
                    datas[4] = checkInCount;
                    Dialog dialog = new ResvtnRegisterDialog(context, datas);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                    dismiss();
                }
            }
        });

        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
