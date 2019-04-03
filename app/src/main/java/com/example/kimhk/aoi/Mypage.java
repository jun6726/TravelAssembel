package com.example.kimhk.aoi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kimhk on 2019-01-19.
 */

public class Mypage extends Activity {
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "user_id";
    private static final String TAG_TRAVEL_NUMBER = "travel_number";
    private static final String TAG_TERM="term";
    private static final String TAG_LOCATION="location";
    private static final String TAG_TRAVEL_PROGRESS="travel_progress";

    public static Intent Map_intent, Item_select_intent, add_travel_intent;

    JSONArray Travels = null;
    ArrayList<HashMap<String, String>> Travel_Array_list;

    ListView Travel_List;
    Button btn_date,btn_remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        btn_date = (Button) findViewById(R.id.btn_date);
        btn_remove = (Button) findViewById(R.id.btn_remove);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_travel_intent = new Intent(getApplicationContext(), AddTravelList.class);
                startActivity(add_travel_intent);
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData("http://jun6726.cafe24.com/php_folder/marker_delete.php");
            }
        });

        Travel_List = (ListView) findViewById(R.id.Travel_List);
        Travel_Array_list = new ArrayList<HashMap<String, String>>();
        getData("http://jun6726.cafe24.com/php_folder/select_TravelList.php"); //수정 필요
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            public void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            Travels = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < Travels.length(); i++) {
                JSONObject c = Travels.getJSONObject(i);

                String user_id = c.getString(TAG_ID);
                String travel_number = c.getString(TAG_TRAVEL_NUMBER);
                String term = c.getString(TAG_TERM);
                String location = c.getString(TAG_LOCATION);
                String travel_progress = c.getString(TAG_TRAVEL_PROGRESS);

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_ID, user_id);
                persons.put(TAG_TRAVEL_NUMBER, travel_number);
                persons.put(TAG_TERM, term);
                persons.put(TAG_LOCATION, location);
                persons.put(TAG_TRAVEL_PROGRESS,travel_progress);

                Travel_Array_list.add(persons);
            }
            ListAdapter adapter = new SimpleAdapter(
                    Mypage.this, Travel_Array_list, R.layout.list_item,
                    new String[]{TAG_ID, TAG_TRAVEL_NUMBER, TAG_TERM, TAG_LOCATION ,TAG_TRAVEL_PROGRESS},
                    new int[]{R.id.ID, R.id.Travel_no, R.id.Term, R.id.Location, R.id.Travel_progress}
            );
            Travel_List.setAdapter(adapter);

            Travel_List.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Item_select_intent = new Intent(getApplicationContext(), ItemSelect.class);
                    Item_select_intent.putExtra("position",Travel_Array_list.get(position).get(TAG_ID));
                    startActivity(Item_select_intent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}