package com.example.kimhk.aoi;

import android.*;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
import java.util.Locale;
import java.util.Map;


/**
 * Created by YEON on 2019-02-02.
 */

public class ItemSelect extends AppCompatActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener{

    private GoogleMap select_map;
    public String getData_position;
    ListView listView;
    JSONArray Travels1 = null;

    ArrayList<HashMap<String, String>> Array_list;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionsGranted = false;
    Marker marker = null; // 마커 변수
    private ArrayList<Marker> arrMarkerList; // 마커 리스트 변수
    private Geocoder geocoder;
    private GoogleMap mMap; // 구글맵변수
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;//검색창 위치 자동완성

    public double lat, lon;
    public Double latitude, longitude;

    Intent Info_Intent;
    LatLng center;
    CardView cardView;
    TextView txtLocationAddress;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_selected);

        final Intent getData_Intent = getIntent();
        getData_position = getData_Intent.getExtras().getString("position");

        Log.d("getData_position","getData_position"+getData_position);
        listView = (ListView) findViewById(R.id.listVIew);
        Array_list = new ArrayList<HashMap<String, String>>();

        Position_send position_send = new Position_send();

        position_send.execute("http://jun6726.cafe24.com/php_folder/Item_select.php", getData_position);

        txtLocationAddress = findViewById(R.id.txtLocationAddress);
        txtLocationAddress.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtLocationAddress.setSingleLine(true);
        txtLocationAddress.setMarqueeRepeatLimit(-1);
        txtLocationAddress.setSelected(true);
        cardView = findViewById(R.id.cardView);

        //카드뷰
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(ItemSelect.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }}
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng);
                sendToServer(latLng);

                Info_Intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(Info_Intent);
            }
        });
        initCameraIdle();//카메라 함수
    }

    // Marking lat. long. on the GoogleMaps
    private void addMarker(LatLng latlng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        mMap.addMarker(markerOptions);
    }

    // Enjoining bg thread to save marker inMySQL server
    private void sendToServer(LatLng latlng) {
        new SaveTask().execute(latlng);
    }

    // bg thread to save the marker in MySQL server
    private class SaveTask extends AsyncTask<LatLng, Void, Void> {
        @Override
        protected Void doInBackground(LatLng... params) {
            String lat = Double.toString(params[0].latitude);
            String lon = Double.toString(params[0].longitude);
            String strUrl = "http://jun6726.cafe24.com/php_folder/info.php";
            URL url = null;
            try {
                url = new URL(strUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

                outputStreamWriter.write("lat=" + lat + "&lon=" + lon);
                outputStreamWriter.flush();
                outputStreamWriter.close();

                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                iStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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

        dlg.setPositiveButton("일정 추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent_test = new Intent(getApplication(), InfoActivity.class);
                startActivity(intent_test);
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
                getAddressFromLocation(center.latitude, center.longitude);
            }
        });
    }
    //주소값 얻기
    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                }
                txtLocationAddress.setText(strAddress.toString());
            } else {
                txtLocationAddress.setText("Searching Current Address");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        arrMarkerList = new ArrayList<>(); // 다중마커 리스트 선언
        // 현재 위치 퍼미션 허가
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true); // 현재위치 활성화
            mMap.getUiSettings().setMyLocationButtonEnabled(true); //현재위치 버튼
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (!place.getAddress().toString().contains(place.getName())) {
                    txtLocationAddress.setText(place.getName() + ", " + place.getAddress());
                } else {
                    txtLocationAddress.setText(place.getAddress());
                }
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16);
                mMap.animateCamera(cameraUpdate);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            }}
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()&&task.getResult()!=null){
                            Location currentLocation = (Location) task.getResult();
//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//                                    DEFAULT_ZOOM);
                        }else{
                            Toast.makeText(ItemSelect.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ItemSelect.this);
    }

    //현재위치 퍼미션
    private void getLocationPermission(){
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // 퍼미션 실패 시 코드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
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

        @Override
        public void onPostExecute(String result) {
            myJSON1 = result;
            showList();
        }
    }

    String myJSON1;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "ID";
    private static final String TAG_Date = "Date";
    private static final String TAG_Time="Time";
    private static final String TAG_Cost="Cost";

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

//            ListAdapter adapter = new SimpleAdapter(
//                    ItemSelect.this, Array_list, R.layout.list_item,
//                    new String[]{TAG_ID, TAG_Date, TAG_Time, TAG_Cost},
//                    new int[]{R.id.ID, R.id.Date, R.id.Time, R.id.Cost}
//            );
//            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
} // 02.16 소스트리 공유 테스트