package com.example.kimhk.aoi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by kimhk on 2019-01-20.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionsGranted = false;
    Marker marker = null; // 마커 변수
    private ArrayList<Marker> arrMarkerList; // 마커 리스트 변수
    private Geocoder mGeocoder;
    private GoogleMap mMap; // 구글맵변수
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;//검색창 위치 자동완성

    TabHost tabs = Mypage.tabs;

    double latitude=37.00, longitude=128.00;
    String TravelID;
    LatLng latLng;
    CardView cardView;
    TextView txtLocationAddress;
    Intent GPSIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        GPSIntent = getIntent();
        TravelID = GPSIntent.getStringExtra("TravelID");
        latitude = GPSIntent.getDoubleExtra("lat",latitude);
        longitude = GPSIntent.getDoubleExtra("long",longitude);
        latLng = new LatLng(latitude,longitude);

        @SuppressLint("ResourceType") Toolbar mToolbar = (Toolbar) findViewById(R.menu.menu2);
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
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MapActivity.this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
            if(id ==  R.id.done) {
                finish();
                tabs.setCurrentTab(0);
            }
            return true;
        }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGeocoder = new Geocoder(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),7));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                AlertDialog.Builder addMarker_dialog = new AlertDialog.Builder(MapActivity.this);
                addMarker_dialog.setTitle("새로운 여행지 등록")
                               .setMessage("여행지 예상 비용을 입력해주세요.");

                final EditText ev_Expense = new EditText(MapActivity.this);
                ev_Expense.setInputType(InputType.TYPE_CLASS_NUMBER);
                addMarker_dialog.setView(ev_Expense);
                addMarker_dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String Expense = ev_Expense.getText().toString();
                        Addmarker addmarker = new Addmarker();
                        addmarker.execute("http://jun6726.cafe24.com/php_folder/add_folder/Marker_add.php", String.valueOf(TravelID), Expense, String.valueOf(latLng.latitude), String.valueOf(latLng.longitude) );
                        addMarker(latLng);
                    }
                });

                addMarker_dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                addMarker_dialog.show();
            }
        });
        initCameraIdle();//카메라 함수
        mMap.setOnMarkerClickListener(this);
    }

    public class Addmarker extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String)params[0];
            String TravelID = (String)params[1];
            String Expense = (String)params[2];
            String Latitude = (String)params[3];
            String Longitude = (String)params[4];

            String postParameters = "&TravelID="+TravelID+"&Expense=" + Expense +"&Latitude=" + Latitude + "&Longitude=" + Longitude;

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

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    // Marking lat. long. on the GoogleMaps
    private void addMarker(LatLng latlng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        mMap.addMarker(markerOptions);
    }

    public boolean onMarkerClick(final Marker marker) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MapActivity.this);
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
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),7));
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
                txtLocationAddress.setText("검색하고자 하는 장소를 입력해주세요");
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

    //지도 초기화
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
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
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 30);
                mMap.animateCamera(cameraUpdate);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            }}
    }

    // 스마트폰 위치 얻기
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
//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),DEFAULT_ZOOM);
                        }else{
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
        }
    }

    //현재위치 퍼미션
    private void getLocationPermission(){
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

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
}