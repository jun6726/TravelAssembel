package com.example.kimhk.aoi;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by YEON on 2019-02-02.
 */
public class ItemSelect extends AppCompatActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // 구글맵변수
    private Geocoder mGeocoder;
    public String getData_position;
    ArrayList<HashMap<String, String>> arrayList;

    LatLng center;
    int markernumber = 1;
    String str_markernumer = String.valueOf(markernumber);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_selected);

        final Intent getData_Intent = getIntent();
        getData_position = getData_Intent.getExtras().getString("position");

        arrayList = new ArrayList<HashMap<String, String>>();

        Position_send position_send = new Position_send();
        position_send.execute("http://jun6726.cafe24.com/php_folder/Item_select.php", getData_position);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGeocoder = new Geocoder(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.537523, 126.96558), 10));

        setMapMarker();
        initCameraIdle();//카메라 함수
    }

    public ArrayList<MarkerItem> markerList = new ArrayList();

    public void setMapMarker() {
        getData("http://jun6726.cafe24.com/php_folder/marker_test.php");
    }

    // Marking lat. long. on the GoogleMaps
    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {
        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);

        return mMap.addMarker(markerOptions);
    }

    JSONArray markers = null;
    private static final String TAG_MARKER = "result";
    private static final String TAG_MARKER_LAT = "marker_lat";
    private static final String TAG_MARKER_LONG = "marker_long";
    String markerjson = null;

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
                markerjson = result;
                load_marker();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void load_marker() {
        try {
            JSONObject jsonObj = new JSONObject(markerjson);
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

                markerList.add(new MarkerItem(load_lat,load_long, markernumber));
            }

            for (MarkerItem markerItem : markerList) {
                addMarker(markerItem, true);
                markernumber++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onMarkerClick(final Marker marker) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(ItemSelect.this);
        dlg.setTitle("마커 위치");
        dlg.setNegativeButton("마커 삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                marker.remove();
            }
        });

        dlg.show();
        return true;
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

    public class Position_send extends AsyncTask<String, Void, String>
    {
        @Override
        public String doInBackground(String... params) {

            String URL = (String) params[0];
            String position = (String) params[1];
            String parameter = "&position=" + position;

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
                outputStream.write(parameter.getBytes("UTF-8"));
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
}