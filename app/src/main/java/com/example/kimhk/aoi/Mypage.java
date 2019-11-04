package com.example.kimhk.aoi;

import android.app.Activity;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.kimhk.aoi.Bluetooth.MainActivity;

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
public class Mypage extends TabActivity implements TabHost.OnTabChangeListener {
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_DATE_START="Date_start";
    private static final String TAG_DATE_END="Date_end";
    private static final String TAG_LOCATION="location";

    public static Intent Map_intent, Item_select_intent, add_travel_intent;

    JSONArray trasvels = null;
    ArrayList<HashMap<String, String>> travelArrayList;

    static SwipeMenuListView travelList;

    static TextView tvUserName,tvUserId;
    static TabHost tabs;
    ListAdapter adapter;

    private BluetoothService btService = null;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        TabSetting();

        if(btService == null){
            btService = new BluetoothService(this,mHandler);
        }

        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserId = (TextView) findViewById(R.id.tvUserId);

        travelList = (SwipeMenuListView) findViewById(R.id.Travel_List);
        travelList.setMenuCreator(creator);
        travelList.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
                travelList.smoothOpenMenu(position);
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                travelList.smoothOpenMenu(position);
            }
        });
        travelList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        travelList.removeAllViewsInLayout();
                        travelList.setAdapter(adapter);
                        break;
                    case 1:
                        // delete
                        getData("http://jun6726.cafe24.com/php_folder/delete_folder/plan_delete.php");
                        Intent restart = new Intent(Mypage.this, Mypage.class);
                        startActivity(restart);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        travelArrayList = new ArrayList<HashMap<String, String>>();
        getData("http://jun6726.cafe24.com/php_folder/show_folder/Travel_list.php"); //수정 필요
    }

    public void TabSetting() {
        tabs = getTabHost();
        TabHost.TabSpec tabSpecMypage = tabs.newTabSpec("Mypage").setIndicator("",getResources().getDrawable(R.drawable.airplane));
        tabSpecMypage.setContent(R.id.tabMypage);
        tabs.addTab(tabSpecMypage);

        TabHost.TabSpec tabSpecAddTravel = tabs.newTabSpec("AddTravel").setIndicator("",getResources().getDrawable(R.drawable.memo));
        tabSpecAddTravel.setContent(new Intent(this, AddTravelList.class));
        tabs.addTab(tabSpecAddTravel);

        TabHost.TabSpec tabSpecBluetooth = tabs.newTabSpec("Bluetooth").setIndicator("",getResources().getDrawable(R.drawable.carrier));
        tabSpecBluetooth.setContent(new Intent(this, MainActivity.class));
        tabs.addTab(tabSpecBluetooth);

        TabHost.TabSpec tabSpecLogin = tabs.newTabSpec("Login").setIndicator("",getResources().getDrawable(R.drawable.login));
        tabSpecLogin.setContent(new Intent(this, Login.class));
        tabs.addTab(tabSpecLogin);

        tabs.getTabWidget().getChildAt(tabs.getCurrentTab()).setBackgroundColor(Color.parseColor("#03A9F4"));

        tabs.setOnTabChangedListener((TabHost.OnTabChangeListener) this);
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
                    return null; // cleartext IOException 발생, 매니패스트 애플리케이션부에 android:usesCleartextTraffic="true"으로 해결
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
            adapter = new SimpleAdapter(Mypage.this, travelArrayList, R.layout.list_item,
                    new String[]{TAG_DATE_START, TAG_DATE_END,TAG_LOCATION}, new int[]{R.id.date_start, R.id.date_end, R.id.Location}
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "Modify" item
            SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
            openItem.setWidth(200);
            openItem.setTitle("Modi");
            openItem.setTitleSize(18);
            openItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(openItem);

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            deleteItem.setWidth(200);
//            deleteItem.setIcon(R.drawable.del);

            deleteItem.setTitle("Del");
            deleteItem.setTitleSize(18);
            deleteItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(deleteItem);
        }
    };

    @Override
    public void onTabChanged(String s) {
            for(int i=0; i<tabs.getTabWidget().getChildCount(); i++) {
                tabs.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#CBE0F5"));
            }
        tabs.getTabWidget().getChildAt(tabs.getCurrentTab()).setBackgroundColor(Color.parseColor("#03A9F4"));

    }
}
