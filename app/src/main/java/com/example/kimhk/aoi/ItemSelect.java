package com.example.kimhk.aoi;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by YEON on 2019-02-02.
 */

public class ItemSelect extends Activity {
    public int getData_position;
    Button button;
    ListView listView;
    JSONArray Travels1 = null;

    ArrayList<HashMap<String, String>> Array_list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_selected);

        final Intent getData_Intent = getIntent();
        getData_position = getData_Intent.getExtras().getInt("position");
        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listVIew);
        Array_list = new ArrayList<HashMap<String, String>>();

        Position_send position_send = new Position_send();

        position_send.execute("http://jun6726.cafe24.com/Item_select.php", String.valueOf(getData_position));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    getData("http://jun6726.cafe24.com/Item_select.php");
            }
        });
    }

    public class Position_send extends AsyncTask<String, Void, String>
    {
        @Override
        public String doInBackground(String... params) {

            String URL = (String) params[0];
            String position = (String) params[1];
            String parameter = "position=" + position;

            try{
                URL url = new URL(URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(position.getBytes("UTF-8"));
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
                getData("http://jun6726.cafe24.com/Item_select.php");
                return sb.toString();

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    String myJSON1;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "ID";
    private static final String TAG_Date = "Date";
    private static final String TAG_Time="Time";
    private static final String TAG_Cost="Cost";


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
                myJSON1 = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON1);
            Travels1 = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < Travels1.length(); i++) {
                JSONObject c = Travels1.getJSONObject(i);

                String ID = c.getString(TAG_ID);
                String Date = c.getString(TAG_Date);
                String Time = c.getString(TAG_Time);
                String Cost = c.getString(TAG_Cost);

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_ID, ID);
                persons.put(TAG_Date, Date);
                persons.put(TAG_Time, Time);
                persons.put(TAG_Cost, Cost);

                Array_list.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    ItemSelect.this, Array_list, R.layout.list_item,
                    new String[]{TAG_ID, TAG_Date, TAG_Time, TAG_Cost},
                    new int[]{R.id.ID, R.id.Date, R.id.Time, R.id.Cost}
            );
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
} // 02.16 소스트리 공유 테스트