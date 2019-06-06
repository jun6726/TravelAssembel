package com.example.kimhk.aoi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class AddTravelList extends AppCompatActivity {
    TextView tv, tv_period;
    EditText travelLocation, travelPeriod;
    Button btnCancle2, btnSubmit2;
    TravelList_send travelListSend;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_travel_list);

        tv = (TextView) findViewById(R.id.tv);
        travelLocation = (EditText) findViewById(R.id.travel_location);
        tv_period = (TextView) findViewById(R.id.tv_period);
        travelPeriod = (EditText) findViewById(R.id.travelPeriod);
        btnCancle2 = (Button) findViewById(R.id.btn_Cancle2);
        btnSubmit2 = (Button) findViewById(R.id.btn_Submit2);

        travelPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmoothDateRangePickerFragment smoothDateRangePickerFragment =
                        SmoothDateRangePickerFragment.newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                            @Override
                            public void onDateRangeSet(SmoothDateRangePickerFragment view, int yearStart, int monthStart, int dayStart,
                                                       int yearEnd, int monthEnd, int dayEnd) {
                                String date = yearStart + "/" + (++monthStart) + "/" + dayStart + " ~ " + yearEnd + "/" + (++monthEnd) + "/" + dayEnd;
                                travelPeriod.setText(date);
                            }
                        });
                smoothDateRangePickerFragment.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        btnCancle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSubmit2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String add_location = travelLocation.getText().toString();
                travelListSend = new TravelList_send();
                travelListSend.execute("http://jun6726.cafe24.com/php_folder/add_folder/Travel_add.php", String.valueOf(Login.user_id),add_location);

                Intent add_marker_intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(add_marker_intent);
            }
        });

    }

    private class TravelList_send extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String userid = (String) params[1];
            String location = (String) params[2];

            String parameters = "&userid=" + userid + "&location=" + location;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(parameters.getBytes("UTF-8"));
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
            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
