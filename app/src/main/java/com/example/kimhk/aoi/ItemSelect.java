package com.example.kimhk.aoi;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by YEON on 2019-02-02.
 */
public class ItemSelect extends AppCompatActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap; // 구글맵변수
    private Geocoder mGeocoder;
    public String getData_position;
    public Integer getTravelID;
    ArrayList<HashMap<String, String>> MarkerarrayList;
    public ArrayList<MarkerItem> markerList = new ArrayList();

    private static final String TAG_MARKER = "result";
    private static final String TAG_MARKER_LAT = "marker_lat";
    private static final String TAG_MARKER_LONG = "marker_long";

    JSONArray markers = null;
    String marker = null;

    LatLng center;
    Intent getData_Intent;
    String putTravelID, UserID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_selected);

        getData_Intent = getIntent();
        getTravelID = getData_Intent.getExtras().getInt("TravelID");
        UserID = getData_Intent.getExtras().getString("UserID");
        putTravelID = String.valueOf(getTravelID);
        MarkerarrayList = new ArrayList<HashMap<String, String>>();

//        Position_send position_send = new Position_send();
//        position_send.execute("http://jun6726.cafe24.com/php_folder/show_folder/Travel_select.php", putTravelID);

        getData("http://jun6726.cafe24.com/php_folder/show_folder/Travel_select.php", putTravelID);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGeocoder = new Geocoder(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.56667, 126.97806), 10));

//        load_marker();
        initCameraIdle();//카메라 함수
    }

    //카메라 함수
    private void initCameraIdle() {
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                center = mMap.getCameraPosition().target;
            }
        });
    }

    //데이터 가져오기 php
    public void getData(String url, String putTravelID) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String putTravelID = params[1];
                String parameters = "&putTravelID=" + getData_position;

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.connect();

                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(parameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

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
                marker = result;
//                load_marker();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    // 마커 로드
    public void load_marker() {
        try {
            getData("http://jun6726.cafe24.com/php_folder/test_folder/marker_test.php",getData_position);

            JSONObject jsonObj = new JSONObject(marker);
            markers = jsonObj.getJSONArray(TAG_MARKER);

            for (int i = 0; i < markers.length(); i++) {
                JSONObject jsonObject = markers.getJSONObject(i);

                String marker_lat = jsonObject.getString(TAG_MARKER_LAT);
                String marker_long = jsonObject.getString(TAG_MARKER_LONG);

                HashMap<String, String> markerObject = new HashMap<String, String>();

                markerObject.put(TAG_MARKER_LAT, marker_lat);
                markerObject.put(TAG_MARKER_LONG, marker_long);

                double load_lat = Double.parseDouble(marker_lat);
                double load_long = Double.parseDouble(marker_long);
                markerList.add(new MarkerItem(load_lat,load_long));
            }

            for (MarkerItem markerItem : markerList) {
                addMarker(markerItem, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Marking lat. long. on the GoogleMaps
    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {
        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(position.latitude, position.longitude));

        return mMap.addMarker(markerOptions);
    }

    public boolean onMarkerClick(final Marker marker) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(ItemSelect.this);
        dlg.setNegativeButton("마커 삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                marker.remove();
            }
        });
        dlg.show();
        return true;
    }
}