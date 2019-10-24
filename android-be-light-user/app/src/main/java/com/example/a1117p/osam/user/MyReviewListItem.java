package com.example.a1117p.osam.user;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyReviewListItem  implements Parcelable {
    private String userId,review,reviewDate;
    private long reviewScore,reviewNumber,hostIdx;
    MyReviewListItem(JSONObject object){
        userId = String.valueOf(object.get("userId"));
        review = (String)object.get("review");
        reviewDate = (String)object.get("reviewDate");
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(reviewDate);
            reviewDate = new SimpleDateFormat("yy/MM/dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reviewScore = (long)object.get("reviewScore");
        reviewNumber = (long)object.get("reviewNumber");
        hostIdx = (long)object.get("hostIdx");
    }

    protected MyReviewListItem(Parcel in) {
        userId = in.readString();
        review = in.readString();
        reviewDate = in.readString();
        reviewScore = in.readLong();
    }

    public static final Creator<MyReviewListItem> CREATOR = new Creator<MyReviewListItem>() {
        @Override
        public MyReviewListItem createFromParcel(Parcel in) {
            return new MyReviewListItem(in);
        }

        @Override
        public MyReviewListItem[] newArray(int size) {
            return new MyReviewListItem[size];
        }
    };

    public String getUserId() {
        return userId;
    }


    public String getReview() {
        return review;
    }


    public String getReviewDate() {
        return reviewDate;
    }


    public long getReviewScore() {
        return reviewScore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(review);
        parcel.writeString(reviewDate);
        parcel.writeLong(reviewScore);
    }

    public long getReviewNumber() {
        return reviewNumber;
    }

    public long getHostIdx() {
        return hostIdx;
    }
}
