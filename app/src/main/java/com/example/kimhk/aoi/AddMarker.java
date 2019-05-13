package com.example.kimhk.aoi;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by kimhk on 2019-01-09.
 */

public class AddMarker extends AppCompatActivity {

    TextView tvDate;
    TextView tvTime;
    TextView tvCost;
    TextView tv1;
    TextView tv2;
    EditText evDate, evTime, evCost;
    Button btnCancle, btnSubmit;
    Calendar cal = Calendar.getInstance();

    Intent getLatLng;
    Double lat,lng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_marker);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);

        tvDate = (TextView) findViewById(R.id.tv_Date);
        evDate = (EditText) findViewById(R.id.ev_Date);
        evDate.setFocusable(false);
        evDate.setClickable(false);

        tvTime = (TextView) findViewById(R.id.tv_Time);
        evTime = (EditText) findViewById(R.id.ev_Time);
        evTime.setFocusable(false);
        evTime.setClickable(false);

        tvCost = (TextView) findViewById(R.id.tv_Cost);
        evCost = (EditText) findViewById(R.id.ev_Cost);

        btnCancle = (Button) findViewById(R.id.btn_Cancle);
        btnSubmit = (Button) findViewById(R.id.btn_Submit);

        getLatLng = getIntent();
        lat = getLatLng.getDoubleExtra("Lat", 0);
        lng = getLatLng.getDoubleExtra("Lng", 0);
        String str_lat = String.valueOf(lat);
        String str_lng = String.valueOf(lng);

        tv1.setText(str_lat);
        tv2.setText(str_lng);

        evDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmoothDateRangePickerFragment smoothDateRangePickerFragment =
                        SmoothDateRangePickerFragment.newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                            @Override
                            public void onDateRangeSet(SmoothDateRangePickerFragment view, int yearStart, int monthStart, int dayStart,
                                            int yearEnd, int monthEnd, int dayEnd) {
                                String date = yearStart + "/" + (++monthStart) + "/" + dayStart + " ~ " + yearEnd + "/" + (++monthEnd) + "/" + dayEnd;
                                evDate.setText(date);
                            }
                        });
                smoothDateRangePickerFragment.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        evTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TimePickerDialog TimePicker = new TimePickerDialog(AddMarker.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        String time_string = String.format("%d시 %d분", hour, min);
                        Toast.makeText(AddMarker.this, time_string, Toast.LENGTH_SHORT).show();
                        evTime.setText(time_string);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                TimePicker.show();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapActivity mapActivity = new MapActivity();
                mapActivity.marker.remove();
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Time_send send = new Time_send();

                String Date = evDate.getText().toString();
                String Time = evTime.getText().toString();
                String Cost = evCost.getText().toString();

                String str_lat = tv1.getText().toString();
                String str_lng = tv2.getText().toString();

                send.execute("http://jun6726.cafe24.com/php_folder/Data_send.php", Date, Time, Cost, str_lat,str_lng);

                Toast.makeText(AddMarker.this, "제출", Toast.LENGTH_SHORT).show();

                Mypage.Map_intent = new Intent();
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
            String Lat = (String)params[4];
            String Lng = (String)params[5];

            String postParameters = "&Date=" + Date + "&Time=" + Time + "&Cost=" + Cost + "&Lat=" + Lat +"&Lng=" + Lng;

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
