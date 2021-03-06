package com.coors.ibikego;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cuser on 2016/8/11.
 */
public class LoginStatusChkTask extends AsyncTask<Object, String, String> {
    private final static String TAG = "LoginStatusChkTask";
    private final static String ACTION = "login";

    @Override
    protected String doInBackground(Object... params) {
//      url, action, blog_no,mem_no, blog_title, blog_content, blog_cre, blog_del, imageBase64
        String url = params[0].toString();
        String action = ACTION;
        String mem_acc = (String) params[1];
        String mem_pw = (String) params[2];
//        MemberVO memberVO;
        String jsonIn = null;
        String result = null;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", action);
        jsonObject.addProperty("mem_acc", mem_acc);
        jsonObject.addProperty("mem_pw", mem_pw);
        try {
            jsonIn=getRemoteData(url, jsonObject.toString());
            //若檢查帳密不相符時，將bl設定為false
            if("\"noMatch\"".equals(jsonIn)){
                result = "false";
            }else {
                result = "true";
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
//        Gson gson = new Gson();
//        Type listType = new TypeToken<MemberVO>() {
//        }.getType();

        return result;
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "jsonIn: " + sb);
        return sb.toString();
    }
}
