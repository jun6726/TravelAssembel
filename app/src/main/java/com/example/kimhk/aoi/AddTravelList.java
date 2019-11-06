package com.example.kimhk.aoi;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

public class AddTravelList extends AppCompatActivity {
    TextView tv;
    TextView period;

    String getUserId;
    EditText travelLocation, travelPeriod;
    TravelList_send travelListSend;
    String date_start, date_end;
    double latitude, longitude;
    String getTravelId;

    Intent add_marker_intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_travel_list);

        tv = (TextView) findViewById(R.id.tvTravel_id);
        travelLocation = (EditText) findViewById(R.id.travel_location);
        period = (TextView) findViewById(R.id.tv_period);
        travelPeriod = (EditText) findViewById(R.id.travel_Period);

        travelLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(AddTravelList.this);
            }
        });
        travelPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmoothDateRangePickerFragment smoothDateRangePickerFragment =
                        SmoothDateRangePickerFragment.newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                            @Override
                            public void onDateRangeSet(SmoothDateRangePickerFragment view, int yearStart, int monthStart, int dayStart,
                                                       int yearEnd, int monthEnd, int dayEnd) {
                                date_start = yearStart + "/" + (++monthStart) + "/" + dayStart ;
                                 date_end =  yearEnd + "/" + (++monthEnd) + "/" + dayEnd;
                                travelPeriod.setText(date_start + " ~ " + date_end);
                            }
                        });
                smoothDateRangePickerFragment.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id ==  R.id.next) {
            String hint = "여행 장소를 선택해주세요", hint2="미정";
             if (travelLocation.getHint().equals(hint) && travelLocation.getText()==null){
                 travelLocation.setHint(hint2);
                 Toast.makeText(this, "장소가 선택되지않았습니다.\n'미정'상태로 설정합니다.", Toast.LENGTH_SHORT).show();
            } else {
                String add_location = travelLocation.getText().toString();
                getUserId = Mypage.tvUserId.getText().toString();
                travelListSend = new TravelList_send();
                travelListSend.execute("http://jun6726.cafe24.com/php_folder/add_folder/Travel_add.php", getUserId, add_location, date_start, date_end);
                add_marker_intent = new Intent(getApplicationContext(), MapActivity.class);

                int random = (int) ((Math.random()*10));
                    if(travelLocation.getHint().equals(hint2)) {
                        latitude = GPSAsia[random][0];
                        longitude = GPSAsia[random][1];
                    }
                add_marker_intent.putExtra("TravelID", getTravelId);
                add_marker_intent.putExtra("lat",latitude);
                add_marker_intent.putExtra("long",longitude);
                startActivity(add_marker_intent);

                travelLocation.setHint("여행 장소를 선택해주세요");
                travelLocation.setText("");
                travelPeriod.setHint("기간을 선택해주세요.");
                travelPeriod.setText("");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(final AddTravelList addTravelList) {
        final Dialog dialog = new Dialog(addTravelList);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.country_dialog);

        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.continent_item, R.id.tvTravel_id, continentName);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                dialog.dismiss();
                showDialogCountry(AddTravelList.this, position);
            }
        });
        dialog.show();
    }

    private void showDialogCountry(final AddTravelList addTravelList, int position) {
        final Dialog dialog = new Dialog(addTravelList);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.country_dialog);

        ListView listView = (ListView) dialog.findViewById(R.id.listview);

        switch (position){
            case 0:
                ArrayAdapter arrayAdapter0 = new ArrayAdapter(this, R.layout.continent_item, R.id.tvTravel_id, countryNameAsia);
                listView.setAdapter(arrayAdapter0);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameAsia[position]);
                        latitude = GPSAsia[position][0];
                        longitude = GPSAsia[position][1];
                        dialog.dismiss();
                    }
                });
                break;
            case 1:
                ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, R.layout.continent_item, R.id.tvTravel_id, countryNameEurope);
                listView.setAdapter(arrayAdapter1);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameEurope[position]);
                        latitude = GPSAsia[position][0];
                        longitude = GPSAsia[position][1];
                        dialog.dismiss();
                    }
                });
                break;
            case 2:
                ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, R.layout.continent_item, R.id.tvTravel_id, countryNameNorthAmerica);
                listView.setAdapter(arrayAdapter2);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameNorthAmerica[position]);
                        latitude = GPSNorthAmerica[position][0];
                        longitude = GPSNorthAmerica[position][1];
                        dialog.dismiss();
                    }
                });
                break;
            case 3:
                ArrayAdapter arrayAdapter3 = new ArrayAdapter(this, R.layout.continent_item, R.id.tvTravel_id, countryNameSouthAmerica);
                listView.setAdapter(arrayAdapter3);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameSouthAmerica[position]);
                        latitude = GPSSouthAmerica[position][0];
                        longitude = GPSSouthAmerica[position][1];
                        dialog.dismiss();
                    }
                });
                break;
            case 4:
                ArrayAdapter arrayAdapter4 = new ArrayAdapter(this, R.layout.continent_item, R.id.tvTravel_id, countryNameAfrica);
                listView.setAdapter(arrayAdapter4);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameAfrica[position]);
                        latitude = GPSAfrica[position][0];
                        longitude = GPSAfrica[position][1];
                        dialog.dismiss();
                    }
                });
                break;
            case 5:
                ArrayAdapter arrayAdapter5 = new ArrayAdapter(this, R.layout.continent_item, R.id.tvTravel_id, countryNameAustralia);
                listView.setAdapter(arrayAdapter5);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameAustralia[position]);
                        latitude = GPSAustralia[position][0];
                        longitude = GPSAustralia[position][1];
                        dialog.dismiss();
                    }
                });
                break;
        }

        dialog.show();
    }

    private class TravelList_send extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String userid = (String) params[1];
            String location = (String) params[2];
            String date_start = (String) params[3];
            String date_end = (String)params[4];

            String parameters = "&userid=" + userid + "&location=" + location + "&date_start=" + date_start + "&date_end=" + date_end;

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
                getTravelId = sb.toString();
                bufferedReader.close();
                return getTravelId;
            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String[] continentName = {"아시아", "유럽", "북미", "남미", "아프리카", "오세아니아"};
    private String[] countryNameAsia = {"대한민국","북한","일본","홍콩","중국","대만","인도네시아","인도","파키스탄","라오스","말레이시아","미얀마","필리핀","싱가폴","태국","동티모르","베트남","스리랑카"};
    private String[] countryNameEurope = {"오스트리아","벨기에", "덴마크", "잉글랜드", "핀란드","프랑스","독일","그리스","헝가리","아이슬란드", "아일랜드섬", "이탈리아","마케도니아","모나코", "나우루", "네덜란드",
            "노르웨이","폴란드","루마니아","러시아","슬로바키아","스웨덴","스위스","우크라이나"};
    private String[] countryNameNorthAmerica = {"미국","캐나다","멕시코","과테말라","그레나다","도미니카 연방","쿠바","벨리즈","바베이도스","자메이카"};
    private String[] countryNameSouthAmerica = {"브라질","아르헨티나","에콰도르","파라과이","가나","칠레","베네수엘라"};
    private String[] countryNameAfrica = {"알제리","콩고","잠비아","토고","소말리아"};
    private String[] countryNameAustralia = {"뉴질랜드","오스트레일리아","키리바시","파푸아뉴기니","통가","솔로몬제도","사모아","피지","팔라우"};

    private int[] Africa = {R.drawable.africa_algeri, R.drawable.africa_congo, R.drawable.africa_zambia, R.drawable.africa_togo, R.drawable.africa_somalia};

    static double[][] GPSAsia = {{37.56667,126.97806},{39.03306,125.75417}, {35.69722,139.70833}, {22.28056,114.17222}, {39.91389,116.39167}, {25.10000, 121.60000},
            {-6.20278, 106.84944}, {19.07500, 72.87778}, {33.71833, 73.06028}, {17.96333, 102.61444}, {3.13583,101.68806}, {19.75056,96.10056}, {14.59889,120.98417}, {1.28000, 103.85000}, {13.72917,100.52389},
            {-8.55000, 125.58333}, {21.03333,105.85000}, {6.88250, 79.90694}};
    double[][] GPSEurope = {{}};
    double[][] GPSNorthAmerica = {{38.89500, -77.03667}, {45.41694, -75.70000}, {19.43194, -99.13306}, {14.61333,-90.53528}, {12.05278, -61.74944}, {18.46667, -69.95000}, {23.13333, -82.38333}, {17.50472,-88.18667}, {13.10583, -59.61306}, {17.98333,-76.80000}};
    double[][] GPSSouthAmerica = {{-15.78083,-47.92917},{-34.60333,-58.38167}, {-2.18333 ,-79.88333}, {-25.28222, -57.63500}, {5.55500,-0.19722}, {-33.46944,-70.64306}, {10.49028, -66.90167}};
    double[][] GPSAfrica = {{36.75333,3.04194}, {-4.32500,15.32222}, {-15.40889,28.28722}, {6.13778 ,1.21250}, {2.03333,45.35000}};
    double[][] GPSAustralia = {{-41.28889, 174.77722}, {-35.30806,149.12444},{1.44028,173.08056}, {-9.48333,147.19028}, {-21.13306, -175.20028}, {-9.43056, 159.94722}, {-13.83333, -171.75000}, {-18.14167, 178.44194}, {6.90000,134.13333}};
}


