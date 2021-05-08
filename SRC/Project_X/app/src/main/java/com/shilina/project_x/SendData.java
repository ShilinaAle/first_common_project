package com.shilina.project_x;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

class SendData extends AsyncTask<Void, Void, String>
{
    String resultString = null;

//    public String[] params;
    public HashMap<String, String> parames = new HashMap<>();
//    public String pass1_in;
//    public String pass2_in;
    public String server, action;

    public Context contextt;

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            String myURL = server;
            String parammetrs = "";

            for ( String key : parames.keySet() ) {
                String value = parames.get(key);
                parammetrs += key+"="+value+"&";
            }

//            String parammetrs = "name="+fio_in+"&dol="+dol_in+"&tel="+tel_in;
            byte[] data = null;
            InputStream is = null;

            try
            {
                URL url = new URL(myURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", "" + Integer.toString(parammetrs.getBytes().length));
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // конвертируем передаваемую строку в UTF-8
                data = parammetrs.getBytes("UTF-8");

                OutputStream os = conn.getOutputStream();

                // передаем данные на сервер
                os.write(data);
                os.flush();
                os.close();
                data = null;
                conn.connect();
                int responseCode= conn.getResponseCode();

                // передаем ответ сервер
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                if (responseCode == 200)     // Если все ОК (ответ 200)
                {
                    is = conn.getInputStream();
                    byte[] buffer = new byte[8192]; // размер буфера

                    // Далее так читаем ответ
                    int bytesRead;

                    while ((bytesRead = is.read(buffer)) != -1)
                    {
                        baos.write(buffer, 0, bytesRead);
                    }

                    data = baos.toByteArray();
                    resultString = new String(data, "UTF-8");  // сохраняем в переменную ответ сервера, у нас "OK"
                    int t = 0;
                }
                else {
                    String resultString = "Всё плохо";
                }

                conn.disconnect();

            }
            catch (MalformedURLException e)
            {

                resultString = "MalformedURLException:" + e.getMessage();
            }
            catch (IOException e)
            {
                resultString = "IOException:" + e.getMessage();
            }
            catch (Exception e)
            {
                resultString = "Exception:" + e.getMessage();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return resultString;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        try
        {
            JSONObject result_obj = new JSONObject(result);
            String is_error = result_obj.getString("error");
            if (is_error.equals("1"))
            {
                String error_text = result_obj.getString("error_text");
                Toast toast = Toast.makeText(contextt, error_text, Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                // значит ошибок нет, запись в БД прошла успешно
                switch (this.action)
                {
                    case "signup":
                        // тут действия после успешной регистрации
                        break;
                    case "login":
                        // тут действия после успешной авторизации
                        // Ире: то, что здесь написано было в com\shilina\project_x\LoginActivity.java и остались закомменчены.
                        String username = this.parames.get("email");
                        boolean status = true;

                        SettingsActivity.setUser(contextt, username);
                        SettingsActivity.setPremium(contextt, status);
                        Toast.makeText(contextt, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(contextt, LaterCallsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        contextt.startActivity(intent);
//                        finish();
                        break;
                        // и так далее
                    default:
                        break;
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}