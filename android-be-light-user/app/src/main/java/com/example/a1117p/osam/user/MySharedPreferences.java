package com.example.a1117p.osam.user;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class MySharedPreferences {
    public static SharedPreferences preferences;

    static void init(Context context) {
        preferences = context.getSharedPreferences("OsamUser", MODE_PRIVATE);
    }

    static String getProfileImgPath() {
        return preferences.getString("profile_img", null);
    }

    static void setProfileImgPath(String path) {
        preferences.edit().putString("profile_img", path).apply();
    }

    static String getId() {
        return preferences.getString("id", null);
    }

    static String getPw() {
        return preferences.getString("passwd", null);
    }

    static void setIdPw(String id, String pw) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", id);
        editor.putString("passwd", pw);
        editor.apply();
    }

    static void removeIdPw() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("id");
        editor.remove("passwd");
        editor.apply();
    }
}
