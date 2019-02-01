package com.example.kimhk.aoi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by kimhk on 2019-01-09.
 */

public class InfoActivity extends AppCompatActivity {

    TextView tv_Date,tv_Time, tv_Cost;
    EditText ev_Date, ev_Time, ev_Cost;
    Button btn_Cancle, btn_Submit;
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        tv_Date = (TextView) findViewById(R.id.tv_Date);
        ev_Date = (EditText) findViewById(R.id.ev_Date);
        ev_Date.setFocusable(false);
        ev_Date.setClickable(false);

        tv_Time = (TextView) findViewById(R.id.tv_Time);
        ev_Time = (EditText) findViewById(R.id.ev_Time);
        ev_Time.setFocusable(false);
        ev_Time.setClickable(false);

        tv_Cost = (TextView) findViewById(R.id.tv_Cost);
        ev_Cost = (EditText) findViewById(R.id.ev_Cost);

        btn_Cancle = (Button) findViewById(R.id.btn_Cancle);
        btn_Submit = (Button) findViewById(R.id.btn_Submit);

        ev_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(InfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String Date_string = String.format("%d년 %d월 %d일", year, month+1, day);
                        ev_Date.setText(Date_string);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });


        ev_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TimePickerDialog TimePicker = new TimePickerDialog(InfoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        String time_string = String.format("%d시 %d분", hour, min);
                        Toast.makeText(InfoActivity.this, time_string, Toast.LENGTH_SHORT).show();
                        ev_Time.setText(time_string);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                TimePicker.show();
            }
        });

        btn_Cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_Submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Time_send send = new Time_send();

                String Date = ev_Date.getText().toString();
                String Time = ev_Time.getText().toString();
                String Cost = ev_Cost.getText().toString();

                send.execute("http://jun6726.cafe24.com/Time_send.php", Date, Time, Cost);
                Toast.makeText(InfoActivity.this, "제출", Toast.LENGTH_SHORT).show();

                Mypage.Map_intent = new Intent();
                Mypage.Map_intent.putExtra("put_lat","12.0000");
                Mypage.Map_intent.putExtra("put_long", "34.231312");
                setResult(3000,Mypage.Map_intent);
                finish();
            }
        });
    }
    public class Time_send extends AsyncTask<String, Void, String> {

        @Override
        public String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String Date = (String)params[1];
            String Time = (String)params[2];
            String Cost = (String)params[3];
            String postParameters = "&Date=" + Date + "&Time=" + Time + "&Cost=" + Cost;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                InputStream inputStream;
                inputStream = httpURLConnection.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString();


            }catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
