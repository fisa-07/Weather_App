package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String API_KEY = "1a7d313da09d7582c929e1ac07db446b";
    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    TextView temperature, condition, location;
    Button find;
    ImageView image;
    EditText editText;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = findViewById(R.id.temperature);
        condition = findViewById(R.id.condition);
        location = findViewById(R.id.location);
        image = findViewById(R.id.image);
        editText = findViewById(R.id.edit_text_city);
        find = findViewById(R.id.search_btn);
        progressBar = findViewById(R.id.progress);

        // By Default location is Kolkata
        editText.setText("Kolkata");
        getWeatherDetails();
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                getWeatherDetails();
            }
        });

    }
    private void getWeatherDetails(){
        String temp_url = "";
        String location_city = editText.getText().toString().trim();
        if(location_city.equals("")){
            Toast.makeText(getApplicationContext(),"Location can't empty",Toast.LENGTH_SHORT).show();
        }
        else {
            temp_url = WEATHER_URL + location_city + "&appid=" + API_KEY;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, temp_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String temp = jsonObjectWeather.getString("description");
                    String condition1 = temp.substring(0,1).toUpperCase() + temp.substring(1);
                    jsonObjectWeather = jsonObject.getJSONObject("main");
                    double temperature1 = jsonObjectWeather.getDouble("temp") - 273.15;

                    location.setText(location_city);
                    temperature.setText(Double.toString(temperature1).substring(0,5)+" C");
                    condition.setText(condition1);

                    condition1 = condition1.toLowerCase();

                    if(condition1.contains("haze")){
                        image.setImageResource(R.drawable.wind);
                    } else if (condition1.contains("cloud")) {
                        image.setImageResource(R.drawable.icons8_cloud);
                    } else if (condition1.contains("rain")||condition1.contains("thunder")) {
                        image.setImageResource(R.drawable.heavy_rain);
                    } else if (condition1.contains("mist")||condition1.contains("snow")) {
                        image.setImageResource(R.drawable.icons8_snow);
                    } else{
                        image.setImageResource(R.drawable.icons8_sun);
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),e.toString().trim(),Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString().trim(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}