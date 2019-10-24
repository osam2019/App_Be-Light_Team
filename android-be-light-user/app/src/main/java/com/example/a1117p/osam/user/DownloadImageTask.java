package com.example.a1117p.osam.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.HashMap;

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    static HashMap<String, Bitmap> cache = null;
    ImageView bmImage;
    boolean isOval;

    public DownloadImageTask(ImageView bmImage, boolean isOval) {
        this.bmImage = bmImage;
        this.isOval = isOval;
        if (cache == null)
            cache = new HashMap<>();
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        if (cache.containsKey(urldisplay))
            mIcon11 = cache.get(urldisplay);
        else {
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                cache.put(urldisplay, mIcon11);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        if (isOval)
            OvalView(bmImage);
    }

    void OvalView(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            v.setBackground(new ShapeDrawable(new OvalShape()));
            v.setClipToOutline(true);
        }
    }
}
