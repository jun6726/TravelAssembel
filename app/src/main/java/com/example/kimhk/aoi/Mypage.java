package com.example.kimhk.aoi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kimhk on 2019-01-19.
 */

public class Mypage extends Activity {
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_DATE_START="Date_start";
    private static final String TAG_DATE_END="Date_end";
    private static final String TAG_LOCATION="location";

    public static Intent Map_intent, Item_select_intent, add_travel_intent;

    JSONArray trasvels = null;
    ArrayList<HashMap<String, String>> travelArrayList;

    ListView travelList;
    Button btnAddTravel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        btnAddTravel = (Button) findViewById(R.id.btnAddTravel);
        btnAddTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_travel_intent = new Intent(getApplicationContext(), AddTravelList.class);
                startActivity(add_travel_intent);
            }
        });

        travelList = (ListView) findViewById(R.id.Travel_List);
        travelArrayList = new ArrayList<HashMap<String, String>>();
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
                    trasvels = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < trasvels.length(); i++) {
                        JSONObject c = trasvels.getJSONObject(i);

                        String date_start = c.getString(TAG_DATE_START);
                        String date_end = c.getString(TAG_DATE_END);
                        String location = c.getString(TAG_LOCATION);

                        HashMap<String, String> persons = new HashMap<String, String>();

                        persons.put(TAG_DATE_START, date_start);
                        persons.put(TAG_DATE_END, date_end);
                        persons.put(TAG_LOCATION, location);

                        travelArrayList.add(persons);
                    }
                    ListAdapter adapter = new SimpleAdapter(
                            Mypage.this, travelArrayList, R.layout.list_item,
                            new String[]{TAG_DATE_START, TAG_DATE_END,TAG_LOCATION},
                    new int[]{R.id.date_start, R.id.date_end, R.id.Location}
            );
            travelList.setAdapter(adapter);

            travelList.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Item_select_intent = new Intent(getApplicationContext(), ItemSelect.class);
                    Item_select_intent.putExtra("position", travelArrayList.get(position).get(TAG_LOCATION));
                    startActivity(Item_select_intent);
                }
            });

            travelList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(Mypage.this);
                    dlg.setTitle("계획 삭제");
                    dlg.setNegativeButton("계획 삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getData("http://jun6726.cafe24.com/php_folder/plan_delete.php");
                            finish();
                            Intent restart = new Intent(Mypage.this, Mypage.class);
                            startActivity(restart);
                        }
                    });
                    dlg.show();
                    return true;
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}