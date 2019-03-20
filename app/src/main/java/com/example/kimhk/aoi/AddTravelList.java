package com.example.kimhk.aoi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.usermgmt.UserManagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.kakao.usermgmt.StringSet.nickname;

public class AddTravelList extends AppCompatActivity {
    TextView tv, tv_calendar;
    EditText travel_location;
    Button btn_Cancle2, btn_Submit2;
    TravelList_send travelList_send;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_travel_list);

        tv = (TextView) findViewById(R.id.tv);
        tv_calendar = (TextView) findViewById(R.id.tv_calendar);
        travel_location = (EditText) findViewById(R.id.travel_location);
        btn_Cancle2 = (Button) findViewById(R.id.btn_Cancle2);
        btn_Submit2 = (Button) findViewById(R.id.btn_Submit2);

        btn_Cancle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_Submit2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String add_location = travel_location.getText().toString();
                travelList_send = new TravelList_send();
                travelList_send.execute("http://jun6726.cafe24.com/php_folder/TravelList_send.php", "1",add_location);
                Toast.makeText(AddTravelList.this, "제출 : ", Toast.LENGTH_SHORT).show();

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
