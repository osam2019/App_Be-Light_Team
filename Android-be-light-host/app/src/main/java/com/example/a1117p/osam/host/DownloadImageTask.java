package com.example.a1117p.osam.host;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.HashMap;

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    static HashMap<String, Bitmap> cache = null;
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
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
                cache.put(urldisplay,mIcon11);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
