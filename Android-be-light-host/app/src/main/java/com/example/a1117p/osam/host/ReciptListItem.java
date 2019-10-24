package com.example.a1117p.osam.host;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReciptListItem {
    private String username, drop_date, pick_date;
    private long recipt_no, itemCount,paid,statusCode;

    ReciptListItem(JSONObject object) {
        username = String.valueOf(object.get("userId"));
        drop_date = (String) object.get("checkIn");
        pick_date = (String) object.get("checkOut");
        recipt_no = (Long) object.get("reciptNumber");
        itemCount = (Long) object.get("itemCount");
        paid = (Long) object.get("paid");
        statusCode=(Long) object.get("statusCode");
        try {
            SimpleDateFormat from, to;
            from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            to = new SimpleDateFormat("yy/MM/dd");
            Date date = from.parse(drop_date);
            drop_date = to.format(date);
            date = from.parse(pick_date);
            pick_date = to.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public long getRecipt_no() {
        return recipt_no;
    }

    public long getItemCount() {
        return itemCount;
    }

    public String getUsername() {
        return username;
    }

    public String getDrop_date() {
        return drop_date;
    }

    public String getPick_date() {
        return pick_date;
    }

    public long getPaid() {
        return paid;
    }

    public long getStatusCode() {
        return statusCode;
    }
}
