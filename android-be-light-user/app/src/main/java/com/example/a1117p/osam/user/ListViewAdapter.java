package com.example.a1117p.osam.user;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    static String reciptNo;
    ArrayList<ReciptListItem> hosts = new ArrayList<>();
    Activity context;

    ListViewAdapter(JSONArray jsonArray, Activity context) {
        for (Object object : jsonArray) {
            hosts.add(new ReciptListItem((JSONObject) object));
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        return hosts.size();
    }

    @Override
    public Object getItem(int position) {
        return hosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hostitem, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView checkin = convertView.findViewById(R.id.checkIn);
        TextView checkOut = convertView.findViewById(R.id.checkOut);
        TextView drop_addr = convertView.findViewById(R.id.drop_addr);
        TextView drop_name = convertView.findViewById(R.id.drop_name);
        TextView pick_addr = convertView.findViewById(R.id.pick_addr);
        TextView pick_name = convertView.findViewById(R.id.pick_name);
        TextView count = convertView.findViewById(R.id.count);
        TextView year = convertView.findViewById(R.id.year);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ReciptListItem listViewItem = hosts.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        checkin.setText(listViewItem.getCheckin());
        checkOut.setText(listViewItem.getCheckOut());
        drop_addr.setText(listViewItem.getHostaddress());
        drop_name.setText(listViewItem.getHostName());
        pick_addr.setText(listViewItem.getGhostaddress());
        pick_name.setText(listViewItem.getGhostName());
        count.setText("예약 1건");
        year.setText("2019년");
        if (listViewItem.getStatusCode() == 2) {
            convertView.findViewById(R.id.pick_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reciptNo = listViewItem.getReciptNumber();
                    IntentIntegrator integrator = new IntentIntegrator(context);
                    integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
                    integrator.setOrientationLocked(false);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    integrator.initiateScan();
                }
            });
        } else convertView.findViewById(R.id.pick_btn).setVisibility(View.GONE);

        return convertView;
    }
}
