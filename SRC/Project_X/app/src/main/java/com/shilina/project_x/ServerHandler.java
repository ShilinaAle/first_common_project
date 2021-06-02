package com.shilina.project_x;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Boolean.valueOf;

class ServerHandler extends AsyncTask<Void, Void, String>
{
    public static final String HOST = "192.168.0.101";
    public static final String DB_LOGIN = "asd";
    public static final String DB_PASS = "asd";

    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_SIGNUP = "signup";
    public static final String ACTION_SET_PREMIUM = "set_premium";
    public static final String ACTION_SET_SETTING = "set_setting";
    public static final String ACTION_CHANGE_PASS = "change_pass";
    public static final String ACTION_GET_RESCHEDULING = "get_rescheduling";
    public static final String ACTION_SET_RESCHEDULING = "set_rescheduling";
    public static final String ACTION_DELETE_CALL = "delete_call";


    HashMap<String, String> data;
    String action;
    String resultString = null;

    public ServerHandler(String action, HashMap<String, String> data) {
        this.action = action;
        this.data = data;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String myURL = "http://" + HOST + "/?action=" + action;
            String myData = "host=" + HOST + "&db_login=" + DB_LOGIN + "&db_pass=" + DB_PASS;

            for (String key : data.keySet()) {
                String value = data.get(key);
                myData += "&" + key + "=" + value;
            }

            byte[] streamData = null;
            InputStream inputStream = null;

            Log.i("LOOK HERE: SH", "Connecting to server...");
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "" + myData.getBytes().length);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //Передаем данные на сервер
            streamData = myData.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(streamData);
            os.flush();
            os.close();
            conn.connect();
            int responseCode = conn.getResponseCode();

            //Получаем ответ с сервера
            Log.i("LOOK HERE: SH", "Reading server response...");
            streamData = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
                byte[] buffer = new byte[8192];
                // Далее так читаем ответ
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                streamData = baos.toByteArray();
                resultString = new String(streamData, "UTF-8");  // сохраняем в переменную ответ сервера, у нас "OK"
            } else {
                resultString = "Ошибка сервера";
            }
            conn.disconnect();
        } catch (Exception e) {
            resultString = "Exception: " + e.getMessage();
        }
        Log.i("LOOK HERE: SH", "ОТВЕТ: " + resultString);
        return resultString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    public static String isErrored (String str) {
        try {
            JSONObject result_obj = new JSONObject(str);;
            if ("1".equals(result_obj.getString("error"))) {
                String error = result_obj.getString("error_text");
                Log.i("LOOK HERE: SH", "Query error: " + error);
                return error;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("LOOK HERE: SH", "Query succeed");
        return null;
    }
}