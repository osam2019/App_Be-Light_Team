package com.example.a1117p.osam.user;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewListItem {
    private String userId,review,reviewDate;
    private long reviewScore;
    ReviewListItem(JSONObject object){
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
    }

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

}
