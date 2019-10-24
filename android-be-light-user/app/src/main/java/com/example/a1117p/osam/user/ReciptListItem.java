package com.example.a1117p.osam.user;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.simple.JSONObject;

public class ReciptListItem implements Parcelable {
    public static final Creator<ReciptListItem> CREATOR = new Creator<ReciptListItem>() {
        @Override
        public ReciptListItem createFromParcel(Parcel in) {
            return new ReciptListItem(in);
        }

        @Override
        public ReciptListItem[] newArray(int size) {
            return new ReciptListItem[size];
        }
    };
    private String hostaddress, hostPostalCode, hostName, ghostaddress, ghostPostalCode, ghostName, reciptNumber, checkin, checkOut, paid, hostUserPhoneNumber, gHostUserPhoneNumber;
    private long itemCount, hostIdx, ghostidx, statusCode;

    ReciptListItem(JSONObject object) {
        hostaddress = (String) object.get("hostaddress");
        hostPostalCode = (String) object.get("hostPostalCode");
        hostName = (String) object.get("hostName");
        ghostaddress = (String) object.get("gHostAddress");
        ghostPostalCode = (String) object.get("gHostPostalCode");
        ghostName = (String) object.get("gHostName");
        reciptNumber = String.valueOf(object.get("reciptNumber"));
        checkin = ((String) object.get("checkin")).split("T")[0];
        checkOut = ((String) object.get("checkOut")).split("T")[0];
        paid = String.valueOf(object.get("paid"));
        hostUserPhoneNumber = (String) object.get("hostUserPhoneNumber");
        gHostUserPhoneNumber = (String) object.get("gHostUserPhoneNumber");
        itemCount = (Long) object.get("itemCount");
        hostIdx = (Long) object.get("hostIdx");
        ghostidx = (Long) object.get("gHostIdx");
        statusCode = (Long) object.get("statusCode");
    }


    protected ReciptListItem(Parcel in) {
        hostaddress = in.readString();
        hostPostalCode = in.readString();
        hostName = in.readString();
        ghostaddress = in.readString();
        ghostPostalCode = in.readString();
        ghostName = in.readString();
        reciptNumber = in.readString();
        checkin = in.readString();
        checkOut = in.readString();
        paid = in.readString();
        hostUserPhoneNumber = in.readString();
        gHostUserPhoneNumber = in.readString();
        itemCount = in.readLong();
        hostIdx = in.readLong();
        ghostidx = in.readLong();
        statusCode = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getHostaddress() {
        return hostaddress;
    }

    public String getHostPostalCode() {
        return hostPostalCode;
    }

    public String getHostName() {
        return hostName;
    }

    public String getReciptNumber() {
        return reciptNumber;
    }

    public String getCheckin() {
        return checkin;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public String getPaid() {
        return paid;
    }

    public String getHostUserPhoneNumber() {
        return hostUserPhoneNumber;
    }

    public String getGhostaddress() {
        return ghostaddress;
    }

    public String getGhostName() {
        return ghostName;
    }

    public String getGhostPostalCode() {
        return ghostPostalCode;
    }

    public String getgHostUserPhoneNumber() {
        return gHostUserPhoneNumber;
    }

    public long getItemCount() {
        return itemCount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hostaddress);
        dest.writeString(hostPostalCode);
        dest.writeString(hostName);
        dest.writeString(ghostaddress);
        dest.writeString(ghostPostalCode);
        dest.writeString(ghostName);
        dest.writeString(reciptNumber);
        dest.writeString(checkin);
        dest.writeString(checkOut);
        dest.writeString(paid);
        dest.writeString(hostUserPhoneNumber);
        dest.writeString(gHostUserPhoneNumber);
        dest.writeLong(itemCount);
        dest.writeLong(hostIdx);
        dest.writeLong(ghostidx);
        dest.writeLong(statusCode);
    }

    public long getHostIdx() {
        return hostIdx;
    }

    public long getGhostidx() {
        return ghostidx;
    }

    public long getStatusCode() {
        return statusCode;
    }
}
