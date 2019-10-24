package com.example.a1117p.osam.host;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.simple.JSONObject;

public class HostListItem implements Parcelable {
    public static final Creator<HostListItem> CREATOR = new Creator<HostListItem>() {
        @Override
        public HostListItem createFromParcel(Parcel in) {
            return new HostListItem(in);
        }

        @Override
        public HostListItem[] newArray(int size) {
            return new HostListItem[size];
        }
    };
    private String name, tel, address, postalCode, idx, openTime, closeTime, intro,hostImage;

    HostListItem(JSONObject object) {
        idx = String.valueOf(object.get("hostIdx"));
        name = (String) object.get("hostName");
        tel = (String) object.get("hostTel");
        address = (String) object.get("hostAddress");
        postalCode = (String) object.get("hostPostalCode");
        openTime = (String) object.get("hostOpenTime");
        closeTime = (String) object.get("hostCloseTime");
        intro = (String) object.get("hostIntro");

        hostImage = (String) object.get("hostImage");
        if(hostImage!=null&&!hostImage.equals(""))
            hostImage="https://be-light.store"+hostImage;
    }

    protected HostListItem(Parcel in) {
        name = in.readString();
        tel = in.readString();
        address = in.readString();
        postalCode = in.readString();
        idx = in.readString();
    }


    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getIdx() {
        return idx;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(tel);
        dest.writeString(address);
        dest.writeString(postalCode);
        dest.writeString(idx);
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public String getIntro() {
        return intro;
    }

    public String getHostImage() {
        return hostImage;
    }
}
