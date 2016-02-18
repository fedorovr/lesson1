package com.csc.lesson1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    public static final String IMAGE_KEY = "My photo";
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize);

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        final Bitmap bitmap = getBitmapFromMemCache(IMAGE_KEY);
        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
        } else {
            new GetImage().execute(iv);
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    class GetImage extends AsyncTask<ImageView, Void, Bitmap> {
        private static final String TAG = "GetImageAsyncTask";
        private ImageView imageView;
        private Bitmap bitmap;
        @Override
        protected Bitmap doInBackground(ImageView... ivs) {
            try {
                imageView = ivs[0];
                URL url = new URL("https://pp.vk.me/c628417/v628417682/398db/4mEwA3S9VIE.jpg");
                URLConnection conn = url.openConnection();
                bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                addBitmapToMemoryCache(IMAGE_KEY, bitmap);
            } catch (Exception ex) {
                Log.e(TAG, "Exception during downloading image");
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
