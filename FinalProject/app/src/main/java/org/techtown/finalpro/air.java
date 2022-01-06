package org.techtown.finalpro;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

public class air extends AppCompatActivity {

    TextView airView;
    EditText edt;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.air);
        setTitle("금일 황사 정보");
        airView = (TextView)findViewById(R.id.airView);
        edt = (EditText) findViewById(R.id.editLon);
    }

    // 버튼을 클릭 했을 경우
    public void btnClick(View v){
        switch (v.getId()){
            case R.id.button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String lon = edt.getText().toString();
                        String gps = latLon(lon);
                        String time = time();
                        data = getXmlData(time, gps);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                airView.setText(data);
                                if(data.trim().equals("")){
                                    showToast("아직 데이터가 업데이트되지 않아 \n황사 정보를 갖고 오지 못했습니다.");
                                } else {
                                    showToast("금일 황사 정보를 갖고왔습니다.");
                                }
                            }
                        });
                    }
                }).start();
                break;
        }
    }

    //금일 or 전일 연, 월, 일, 시간 정보 불러오기
    public String time() {
        //현재 시간 불러오기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        //년-월-일
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyyMMdd");
        //현재시간
        SimpleDateFormat mFormat = new SimpleDateFormat("hhmm");

        String getDay;
        getDay = simpleDateFormatDay.format(date);
        return getDay;
    }

    //대기 오염 데이터를 api에서 파싱해서 가져옴
    String getXmlData(String getDay, String gps){ //연월일 데이터 가져옴
        StringBuffer buffer = new StringBuffer();
        String query="FWh87%2FqaLEma7tme7KUMsUs6zp6rbczh1uHDI88B80cXFV29f1uSbPx5tCvgP3eH8jf1vxJ1i0vWZbPXUpGelQ%3D%3D";
        String queryUrl="http://apis.data.go.kr/1480523/MetalMeasuringResultService/MetalService?numOfRows=12&pageNo=1&resultType=xml&stationcode=" + gps + "&date=" + getDay + "&timecode=RH02&itemcode=90319&serviceKey="+ query;

        try{
            URL url= new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();//xml파싱을 위한
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();
            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//테그 이름 얻어오기
                        if(tag.equals("item")) ;// 첫번째 검색결과
                        else if(tag.equals("sdate")){ //시간
                            xpp.next();
                            buffer.append(xpp.getText().substring(8, 10));//시간 부분만 슬라이싱
                            buffer.append(" 시");
                            buffer.append("\n");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("value")){
                            xpp.next();
                            buffer.append(xpp.getText());//value 요소의 TEXT 읽어와서 문자열버퍼에 추가 .substring(0, 4)
                            buffer.append(" ng/m3");
                            buffer.append("\n");//줄바꿈 문자 추가

                            double x = Double.parseDouble(xpp.getText().substring(0, 4));
                            buffer.append(showAir(x));
                            buffer.append("\n");//줄바꿈 문자 추가
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName(); //테그 이름 얻어오기

                        if(tag.equals("item")) buffer.append("\n");// 첫번째 검색결과종료..줄바꿈
                        break;
                }
                eventType= xpp.next();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return buffer.toString();//StringBuffer 문자열 객체 반환

    }//getXmlData method....

    //황사 수치 분류
    public String showAir(double x) {
        String airName = null;
        if(x <= 199.0) {
            airName = "약함";
        } else if (x <= 399.0) {
            airName = "보통";
        } else if (x <= 599.0) {
            airName = "약간 나쁨";
        } else if (x <= 799.0) {
            airName = "나쁨";
        } else {
            airName = "매우 나쁨";
        }
        return airName;
    }

    //대기 환경 코드
    public String latLon (String city) {
        String gps = null;
        if(city.equals("서울") || city.equals("서울시")) {
            gps = "1";
        } else if (city.equals("백령") || city.equals("백령도")) {
            gps = "2";
        } else if (city.equals("호남") || city.equals("호남권")) {
            gps = "3";
        } else if (city.equals("중부") || city.equals("중부권")) {
            gps = "4";
        } else if (city.equals("제주") || city.equals("제주권")) {
            gps = "5";
        } else if (city.equals("영남") || city.equals("영남권")) {
            gps = "6";
        } else if (city.equals("경기") || city.equals("경기권")) {
            gps = "7";
        } else if (city.equals("충청") || city.equals("충청권")) {
            gps = "8";
        } else if (city.equals("전북") || city.equals("전북권")) {
            gps = "9";
        } else if (city.equals("강원") || city.equals("강원권")) {
            gps = "10";
        } else { //지역이 없을경우 기본값, 서울
            gps = "1";
        }
        return gps;
    }

    //토스트 메세지
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

