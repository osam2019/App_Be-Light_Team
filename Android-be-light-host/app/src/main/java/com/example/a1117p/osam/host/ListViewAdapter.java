package com.example.a1117p.osam.host;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    ArrayList<HostListItem> hosts = new ArrayList<>();

    ListViewAdapter(JSONArray jsonArray) {
        for (Object object : jsonArray) {
            hosts.add(new HostListItem((JSONObject) object));
        }
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
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hostitem, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView name = convertView.findViewById(R.id.host_name);
        TextView tel = convertView.findViewById(R.id.host_num);
        TextView address = convertView.findViewById(R.id.hostaddr);
        TextView postalcode = convertView.findViewById(R.id.host_postal);
        TextView term = convertView.findViewById(R.id.term);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        HostListItem listViewItem = hosts.get(position);

        if(listViewItem.getHostImage()!=null&&!listViewItem.getHostImage().equals("")){
            new DownloadImageTask((ImageView) convertView.findViewById(R.id.host_img))
                    .execute(listViewItem.getHostImage());
        }
        // 아이템 내 각 위젯에 데이터 반영
        name.setText(listViewItem.getName());
        tel.setText(listViewItem.getTel());
        address.setText(listViewItem.getAddress());
        postalcode.setText(listViewItem.getPostalCode());
        term.setText("open  " + listViewItem.getOpenTime() + " ~ close  " + listViewItem.getCloseTime());

        return convertView;
    }
}
