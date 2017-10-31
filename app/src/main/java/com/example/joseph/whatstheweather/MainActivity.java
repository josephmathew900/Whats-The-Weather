package com.example.joseph.whatstheweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    String city;
    int cityid;
    String json;
    ArrayList<String> numberlist = new ArrayList<String>();

    public class JsonProcessing extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data;
                data = inputStreamReader.read();
                while (data != -1){
                    char current = (char)data;
                    result += current;
                    data = inputStreamReader.read();
                  //  Log.i("result",result);
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String weather = jsonObject.getString("list");
               // Log.i("result",weather);
                JSONArray array = new JSONArray(weather);
                for (int i = 0;i<array.length();i++){
                    JSONObject jsonObject1 = array.getJSONObject(i);
                    if (jsonObject1.getString("dt_txt").equals("2017-10-31 15:00:00")){
                    Log.i("main",jsonObject1.getString("weather"));
                    Log.i("description",jsonObject1.getString("clouds"));
                }}

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.city);

    }

    public void findWeather(View view){
        city = cityName.getText().toString();
       // Log.i("city",city);
        try {
            InputStream jsonin = getAssets().open("city.list.json");
            int size = jsonin.available();
            byte[] buffer = new byte[size];
            jsonin.read(buffer);
            jsonin.close();

            json = new String(buffer,"UTF-8");
            JSONArray jsonArrayid = new JSONArray(json);
            for (int i =0;i < jsonArrayid.length();i++){
                JSONObject obj = jsonArrayid.getJSONObject(i);
                if (obj.getString("name").equals(city)){

                    cityid = obj.getInt("id");
                   Log.i("id",Integer.toString(cityid));
                    break;
                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonProcessing jsonProcessing = new JsonProcessing();
        jsonProcessing.execute("https://api.openweathermap.org/data/2.5/forecast?id="+cityid+"&APPID=a70112ef4169be3dac4a3f85e2582612");

    }
}
