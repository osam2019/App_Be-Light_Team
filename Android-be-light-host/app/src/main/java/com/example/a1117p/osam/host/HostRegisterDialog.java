package com.example.a1117p.osam.host;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class HostRegisterDialog extends Dialog {
    Activity context;
    Button openTime, closeTime;
    boolean isReg = true;
    HostListItem item;
    public File file=null;
    ImageView img;

    public HostRegisterDialog(@NonNull Activity activity) {
        super(activity);
        this.context = activity;
    }

    public HostRegisterDialog(@NonNull Activity activity, boolean isReg, HostListItem item) {
        super(activity);
        this.context = activity;
        this.isReg = isReg;
        this.item = item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        getWindow().setAttributes(layoutParams);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(R.layout.host_register_dialog);
        openTime = findViewById(R.id.openTime);
        openTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TimePickerDialog picker = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {

                                openTime.setText(String.format("%d:%02d", sHour, sMinute));
                            }
                        }, 0, 0, true);
                picker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                picker.show();
            }
        });
        img = findViewById(R.id.host_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                context.startActivityForResult(intent, 0);
            }
        });
        closeTime = findViewById(R.id.closeTime);
        closeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TimePickerDialog picker = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {


                                closeTime.setText(String.format("%d:%02d", sHour, sMinute));
                            }
                        }, 0, 0, true);
                picker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                picker.show();
            }
        });

        final EditText Hostname = findViewById(R.id.hostname);
        final EditText HostTel = findViewById(R.id.hosttel);
        final EditText HostAddress = findViewById(R.id.hostaddr);
        final EditText HostIntro = findViewById(R.id.hostIntro);
        final EditText HostPostalCode = findViewById(R.id.postalcode);
        if (isReg) {
            findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String hostName = Hostname.getText().toString();
                    String hostTel = HostTel.getText().toString();
                    String hostAddress = HostAddress.getText().toString();
                    String hostIntro = HostIntro.getText().toString();
                    String hostPostalCode = HostPostalCode.getText().toString();
                    String OpenTime = openTime.getText().toString();
                    String CloseTime = closeTime.getText().toString();

                    if (hostName.equals("")) {
                        Toast.makeText(context, "호스트명을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (hostTel.equals("")) {
                        Toast.makeText(context, "전화번호를 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (hostAddress.equals("")) {
                        Toast.makeText(context, "주소를 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (hostIntro.equals("")) {
                        Toast.makeText(context, "가게설명을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (hostPostalCode.equals("")) {
                        Toast.makeText(context, "우편번호를 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (OpenTime.equals("클릭")) {
                        Toast.makeText(context, "오픈시간을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (CloseTime.equals("클릭")) {
                        Toast.makeText(context, "마감시간을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    }else if(file==null){

                        Toast.makeText(context, "호스트사진을 변경해주세요.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Geocoder geocoder = new Geocoder(context);
                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocationName(hostAddress, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList.size() == 0) {
                        Toast.makeText(context, "입력한 주소에 맞는 좌표를 찾을수 없습니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("등록중입니다.");

                    dialog.show();

                    Address address = addressList.get(0);
                    final HashMap<String, String> params = new HashMap<>();

                    params.put("hostName", hostName);
                    params.put("hostTel", hostTel);
                    params.put("hostAddress", hostAddress);
                    params.put("hostIntro", hostIntro);
                    params.put("hostOpenTime", OpenTime);
                    params.put("hostCloseTime", CloseTime);
                    params.put("hostPostalCode", hostPostalCode);
                    params.put("hostLatitude", address.getLatitude() + "");
                    params.put("hostLongitude", address.getLongitude() + "");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String html = RequestHttpURLConnection.requestWithFile("https://be-light.store/api/host", params, true, "POST",file,"hostImage");
                            context.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    JSONParser parser = new JSONParser();
                                    try {
                                        JSONObject object = (JSONObject) parser.parse(html);
                                        Long status = (Long) object.get("status");
                                        if (status == 200) {
                                            Toast.makeText(context, "가게 등록에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        } else {
                                            Toast.makeText(context, "가게 등록에 실패하였습니다.", Toast.LENGTH_LONG).show();
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
            findViewById(R.id.edit).setVisibility(View.GONE);
        } else {
            Hostname.setText(item.getName());
            HostTel.setText(item.getTel());
            HostAddress.setText(item.getAddress());
            HostIntro.setText(item.getIntro());
            HostPostalCode.setText(item.getPostalCode());
            openTime.setText(item.getOpenTime());
            closeTime.setText(item.getCloseTime());
            new DownloadImageTask(img).execute(item.getHostImage());


            findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String hostName = Hostname.getText().toString();
                    String hostTel = HostTel.getText().toString();
                    String hostAddress = HostAddress.getText().toString();
                    String hostIntro = HostIntro.getText().toString();
                    String hostPostalCode = HostPostalCode.getText().toString();
                    String OpenTime = openTime.getText().toString();
                    String CloseTime = closeTime.getText().toString();

                    if (hostName.equals("")) {
                        Toast.makeText(context, "호스트명을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (hostTel.equals("")) {
                        Toast.makeText(context, "전화번호를 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (hostAddress.equals("")) {
                        Toast.makeText(context, "주소를 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (hostIntro.equals("")) {
                        Toast.makeText(context, "가게설명을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (hostPostalCode.equals("")) {
                        Toast.makeText(context, "우편번호를 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (OpenTime.equals("클릭")) {
                        Toast.makeText(context, "오픈시간을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    } else if (CloseTime.equals("클릭")) {
                        Toast.makeText(context, "마감시간을 입력하세요", Toast.LENGTH_LONG).show();
                        return;
                    }else if(file==null){

                        Toast.makeText(context, "호스트사진을 변경해주세요.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Geocoder geocoder = new Geocoder(context);
                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocationName(hostAddress, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList.size() == 0) {
                        Toast.makeText(context, "입력한 주소에 맞는 좌표를 찾을수 없습니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("수정중입니다.");

                    dialog.show();

                    Address address = addressList.get(0);
                    final HashMap<String, String> params = new HashMap<>();

                    params.put("idx", item.getIdx());
                    params.put("hostName", hostName);
                    params.put("hostTel", hostTel);
                    params.put("hostAddress", hostAddress);
                    params.put("hostIntro", hostIntro);
                    params.put("hostOpenTime", OpenTime);
                    params.put("hostCloseTime", CloseTime);
                    params.put("hostPostalCode", hostPostalCode);
                    params.put("hostLatitude", address.getLatitude() + "");
                    params.put("hostLongitude", address.getLongitude() + "");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String html = RequestHttpURLConnection.requestWithFile("https://be-light.store/api/host?_method=PUT", params, true, "POST",file,"hostImage");
                            context.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    JSONParser parser = new JSONParser();
                                    try {
                                        JSONObject object = (JSONObject) parser.parse(html);
                                        Long status = (Long) object.get("status");
                                        if (status == 200) {
                                            Toast.makeText(context, "가게 수정에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        } else {
                                            Toast.makeText(context, "가게 수정에 실패하였습니다.", Toast.LENGTH_LONG).show();
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
                    final ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("삭제중입니다.");

                    dialog.show();
                    final HashMap<String, String> params = new HashMap<>();

                    params.put("hostIdx", item.getIdx());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String html = RequestHttpURLConnection.request("https://be-light.store/api/host?_method=DELETE", params, true, "POST");
                            context.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    JSONParser parser = new JSONParser();
                                    try {
                                        JSONObject object = (JSONObject) parser.parse(html);
                                        Long status = (Long) object.get("status");
                                        if (status == 200) {
                                            Toast.makeText(context, "가게 삭제에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        } else {
                                            Toast.makeText(context, "가게 삭제에 실패하였습니다.", Toast.LENGTH_LONG).show();
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

            findViewById(R.id.register).setVisibility(View.GONE);
        }
    }

}
