package com.coors.ibikego.travel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.coors.ibikego.Common;
import com.coors.ibikego.R;
import com.coors.ibikego.daovo.TravelVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by user on 2016/8/25.
 */
public class TravelGetBitmapTask extends AsyncTask <Object,Integer,Bitmap> {
    private final static String TAG = "TravelGetAllTask";
    private final static String ACTION = "getImage";
    private final static String url = Common.URL + "travel/travelApp";
    private final WeakReference<ImageView> imageViewWeakReference;

    public TravelGetBitmapTask(ImageView imageView) {
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        String tra_no = params[0].toString();
        String imageSize = params[0].toString();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", ACTION);
        jsonObject.addProperty("tra_no", tra_no);
        jsonObject.addProperty("imageSize", imageSize);

        Bitmap bitmap;
        try {
            bitmap = getRemoteImage(url, jsonObject.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        ImageView imageView = imageViewWeakReference.get();
        if (imageView != null) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.ic_insert_photo_black_24dp);
            }
        }
        super.onPostExecute(bitmap);
    }

    private Bitmap getRemoteImage(String url, String jsonOut) throws IOException {
        Bitmap bitmap = null;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        return bitmap;
    }
}
