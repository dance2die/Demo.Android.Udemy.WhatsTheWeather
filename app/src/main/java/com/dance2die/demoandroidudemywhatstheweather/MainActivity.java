package com.dance2die.demoandroidudemywhatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private final String API_URL = "http://api.openweathermap.org/data/2.5/forecast/city?q=%s&APPID=286c90a2b702c059135ae9f427fbc5ac";

    private EditText cityName;
    private TextView resultTextView;

    public void findWeather(View view) {
        String url = null;
        try {
            url = String.format(API_URL, URLEncoder.encode(cityName.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
        }
        Log.i("QUERY", url);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(cityName.getWindowToken(), 0);   // 0: it can be anything for a flag.

        DownloadTask task = new DownloadTask();
        task.execute(url);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls)
        {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data != -1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }

                return result;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
//                String weatherInfo = jsonObject.getString("weather");
//
//                Log.i("Website Content", weatherInfo);

                String message = "";
                JSONArray listArray = jsonObject.getJSONArray("list");
                for (int i = 0; i < listArray.length(); i++){
                    JSONObject jsonPart = listArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    message += main + ": " + description + "\r\n";

//                    Log.i("main", jsonPart.getString("main"));
//                    Log.i("description", jsonPart.getString("description"));
                }

                if (message != "") {
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
