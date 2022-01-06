package org.techtown.finalpro;

// 날씨 앱, 도시를 edit text를 통해 검색하면 해당 도시의 날씨 정보를 불러와 출력한다.

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class today extends AppCompatActivity {

    TextView weatherView;
    Button wBtn;
    EditText editCity;
    String result;
    static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today);
        setTitle("현재 날씨");

        //선언
        wBtn = (Button) findViewById(R.id.weatherBtn);
        weatherView = (TextView) findViewById(R.id.todayWeatherInfo);
        editCity = (EditText) findViewById(R.id.editcity);

        wBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //editText를 통해 입력받은 도시 이름을 city에 저장하고 weather 메소드에 매개변수로 보냄
                String city = editCity.getText().toString();
                if(city.length() == 0) { // 입력창이 공백일 경우
                    showToast("지역을 입력하세요");
                } else { // 공백이 아닐경우 날씨정보 불러오기
                    String latLon = latLon(city);
                    if(latLon.equals("")){ //입력된 지역이 없을경우
                        showToast("해당 지역은 날씨를 확인 할 수 없습니다.");
                    } else { //입력된 지역이 있을 경우
                        CurrentWeatherCall(latLon);
                        showToast("해당 지역의 날씨 정보를 불러왔습니다.");
                    }
                }
            }
        });
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    //입력된 지역을 위경도 좌표로 변환, 좌표는 해당 시, 도청을 기준으로 했음
    public String latLon (String city) {
        String gps = null;
        if(city.equals("서울") || city.equals("서울시")) {
            gps = "37.52902713182659" + " " +"126.98584370564363";
        } else if (city.equals("부산") || city.equals("부산시")) {
            gps = "35.17971206113396" + " " + "129.07502533651083";
        } else if (city.equals("대구") || city.equals("대구시")) {
            gps = "35.868178380060996" + " " + "128.5806281121894";
        } else if (city.equals("인천") || city.equals("인천시")) {
            gps = "37.45603972071989" + " " + "126.70587037086081";
        } else if (city.equals("광주") || city.equals("광주시")) {
            gps = "35.15994501030649" + " " + "126.8513577806873";
        } else if (city.equals("울산") || city.equals("울산시")) {
            gps = "35.539034699322144" + " " + "129.31128293861397";
        } else if (city.equals("세종") || city.equals("세종시")) {
            gps = "36.480140506578" + " " + "127.28882917092174";
        } else if (city.equals("경기") || city.equals("경기도")) {
            gps = "37.27491443264456" + " " + "127.0091917091811";
        } else if (city.equals("강원") || city.equals("강원도")) {
            gps = "37.86292436315532" + " " + "127.73312217355867";
        } else if (city.equals("충북") || city.equals("충청북도")) {
            gps = "36.635629413623" + " " + "127.49138308910058";
        } else if (city.equals("충남") || city.equals("충청남도")) {
            gps = "36.65984296872477" + " " + "126.67258032525156";
        } else if (city.equals("전북") || city.equals("전라북도")) {
            gps = "35.82027298461823" + " " + "127.10880209974063";
        } else if (city.equals("전남") || city.equals("전라남도")) {
            gps = "34.81612168959558" + " " + "126.46292419786823";
        } else if (city.equals("경북") || city.equals("경상북도")) {
            gps = "36.57649124965447" + " " + "128.5059126687219";
        } else if (city.equals("경남") || city.equals("경상남도")) {
            gps = "35.23817656270288" + " " + "128.69243018438465";
        } else if (city.equals("제주") || city.equals("제주도")) {
            gps = "33.49955543024103" + " " + "126.53124172452273";
        } else if (city.equals("울릉도")) {
            gps = "37.50796085833531" + " " + "130.85702733102426";
        } else if (city.equals("독도")) {
            gps = "37.23928353528971" + " " + "131.8682408214758";
        } else { //지역이 없을경우
            gps = "";
        }
        return gps;
    }

    public void CurrentWeatherCall(String city){
        int idx = city.indexOf(" ");

        // 공백의 앞부분을 추출 lat
        // substring은 첫번째 지정한 인덱스는 포함하지 않는다.
        String lat = city.substring(0, idx);

        // 공백의 뒷부분을 추출 lon
        String lon = city.substring(idx+1);

        //날씨 받아오는 api, edit text를 통해 받은 city값을 api에 넣어줘서 해당 도시의 날씨 정보를 불러옴
        String key = "cf33495ce789e9e32dc58938c1af0d91";
        String langKor = "&lang=kr";
        //String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + langKor;
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid="+key + langKor;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    result = "";
                    //실시간 날씨 확인
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    //년-월-일 출력
                    SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
                    //시-분 출력
                    SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");
                    String getDay = simpleDateFormatDay.format(date);
                    String getTime = simpleDateFormatTime.format(date);
                    String getDate = getDay + "\n" + getTime;
                    //data textView에 시간 출력
                    result += "날짜, 시간\n" + getDate + "\n\n";

                    JSONObject jsonObject = new JSONObject(response);

                    //입력받은 지역 출력
                    result += "지역: " + editCity.getText().toString() + "\n\n";

                    //날씨 키값 받기
                    JSONArray weatherJson = jsonObject.getJSONArray("weather");
                    JSONObject weatherObj = weatherJson.getJSONObject(0);
                    String weather = weatherObj.getString("description");

                    //weather view에 날씨 출력
                    result += "날씨: " + weather + "\n\n";

                    //온도, 습도 키값 받기
                    JSONObject tempK = new JSONObject(jsonObject.getString("main"));

                    //온도 받고 켈빈 온도를 섭씨 온도로 변경
                    //정수값으로 온도를 출력하기 위해 int값으로 받음
                    int tempDo = (Math.round((tempK.getInt("temp")-273)*100)/100);
                    //습도
                    int humidity = tempK.getInt("humidity");
                    //temp view에 날씨 출력
                    result += "기온: " + tempDo + "°C\n\n";
                    result += "습도: " + humidity +"%\n\n";

                    weatherView.setText(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    //토스트 메세지
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}