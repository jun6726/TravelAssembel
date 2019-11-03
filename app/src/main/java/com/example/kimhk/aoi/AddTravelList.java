package com.example.kimhk.aoi;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    TextView tv, period;
    EditText travelLocation, travelPeriod;
    TravelList_send travelListSend;
    String date_start, date_end;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_travel_list);

        tv = (TextView) findViewById(R.id.tv);
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
            if (date_start == null) {
                Toast.makeText(AddTravelList.this, "날짜가 선택되지않았습니다.\n'미정' 상태로 설정합니다.\n진행을 원하시면 다음을 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show();
                date_start = "미정";
                travelLocation.setText("미정");
            }
            else {
                String add_location = travelLocation.getText().toString();
                travelListSend = new TravelList_send();
                travelListSend.execute("http://jun6726.cafe24.com/php_folder/add_folder/Travel_add.php", String.valueOf(Login.user_id), add_location, date_start, date_end);

                Intent add_marker_intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(add_marker_intent);
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
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.continent_item, R.id.tv, continentName);
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
                ArrayAdapter arrayAdapter0 = new ArrayAdapter(this, R.layout.continent_item, R.id.tv, countryNameAsia);
                listView.setAdapter(arrayAdapter0);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameAsia[position]);
                        dialog.dismiss();
                    }
                });
                break;
            case 1:
                ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, R.layout.continent_item, R.id.tv, countryNameEurope);
                listView.setAdapter(arrayAdapter1);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameEurope[position]);
                        dialog.dismiss();
                    }
                });
                break;
            case 2:
                ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, R.layout.continent_item, R.id.tv, countryNameNorthAmerica);
                listView.setAdapter(arrayAdapter2);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameNorthAmerica[position]);
                        dialog.dismiss();
                    }
                });
                break;
            case 3:
                ArrayAdapter arrayAdapter3 = new ArrayAdapter(this, R.layout.continent_item, R.id.tv, countryNameSouthAmerica);
                listView.setAdapter(arrayAdapter3);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameSouthAmerica[position]);
                        dialog.dismiss();
                    }
                });
                break;
            case 4:
                ArrayAdapter arrayAdapter4 = new ArrayAdapter(this, R.layout.continent_item, R.id.tv, countryNameAfrica);
                listView.setAdapter(arrayAdapter4);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameAfrica[position]);
                        dialog.dismiss();
                    }
                });
                break;
            case 5:
                ArrayAdapter arrayAdapter5 = new ArrayAdapter(this, R.layout.continent_item, R.id.tv, countryNameAustralia);
                listView.setAdapter(arrayAdapter5);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        travelLocation.setText(countryNameAustralia[position]);
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
    private String[] continentName = {"아시아", "유럽", "북미", "남미", "아프리카", "오세아니아"};
    private String[] countryNameAsia = {"대한민국","북한","일본","홍콩","중국", "대만", "인도네시아", "인도", "파키스탄", "라오스", "말레이시아", "미얀마", "필리핀","싱가폴", "태국", "동티모르", "베트남", "스리랑카", "필리핀",};
    private String[] countryNameEurope = {"오스트리아","벨기에", "덴마크", "잉글랜드", "핀란드","프랑스","독일","그리스","헝가리","아이슬란드", "아일랜드섬", "이탈리아","마케도니아","모나코", "나우루", "네덜란드",
            "노르웨이","폴란드","루마니아","러시아","슬로바키아","스웨덴","스위스","우크라이나"};
    private String[] countryNameNorthAmerica = {"미국","캐나다","멕시코","과테말라","그레나다","도미니카 연방","쿠바","벨리즈","바베이도스","자메이카"    };
    private String[] countryNameSouthAmerica = {"브라질","아르헨티나","에콰도르","파라과이","가나","칠레","베네수엘라"};
    private String[] countryNameAfrica = {"알제리","콩고","잠비아","토고","소말리아"};
    private String[] countryNameAustralia = {"뉴질랜드","오스트레일리아","키리바시","파푸아뉴기니","통가","솔로몬제도","사모아","피지","팔라우"};
}


