package org.techtown.finalpro;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class week extends AppCompatActivity {

    TextView weekView;
    EditText edt;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week);
        setTitle("중기 예보");
        weekView = (TextView)findViewById(R.id.weekWatherView);
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
                                weekView.setText(data);
                                if(data.trim().equals("")){
                                    showToast("아직 데이터가 업데이트되지 않아 \n예보 정보를 갖고 오지 못했습니다.");
                                } else {
                                    showToast("중기 예보 정보를 갖고왔습니다.");
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

        String getDay;
        getDay = simpleDateFormatDay.format(date);
        return getDay;
    }

    //코로나 확진자 데이터를 api에서 파싱해서 가져옴
    String getXmlData(String getDay, String gps){ //연월일 데이터 가져옴
        StringBuffer buffer = new StringBuffer();
        String query="FWh87%2FqaLEma7tme7KUMsUs6zp6rbczh1uHDI88B80cXFV29f1uSbPx5tCvgP3eH8jf1vxJ1i0vWZbPXUpGelQ%3D%3D";
        String queryUrl="http://apis.data.go.kr/1360000/MidFcstInfoService/getMidFcst?serviceKey="+ query +"&numOfRows=10&pageNo=1&stnId="+ gps +"&tmFc="+ getDay +"0600";

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
                        else if(tag.equals("wfSv")){ //중기예보 내용
                            xpp.next();
                            buffer.append(xpp.getText());
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

    //위치 코드
    public String latLon (String city) {
        String gps = null;
        if(city.equals("강원") || city.equals("강원도")) {
            gps = "105";
        } else if (city.equals("전국")) {
            gps = "108";
        } else if (city.equals("서울") || city.equals("인천") || city.equals("경기") || city.equals("경기도")) {
            gps = "109";
        } else if (city.equals("충북") || city.equals("충청북도")) {
            gps = "131";
        } else if (city.equals("대전") || city.equals("세종") || city.equals("충남") || city.equals("충청남도")) {
            gps = "133";
        } else if (city.equals("전북") || city.equals("전라북도")) {
            gps = "146";
        } else if (city.equals("광주") || city.equals("전남") || city.equals("전라남도")) {
            gps = "156";
        } else if (city.equals("대구") || city.equals("경북") || city.equals("경상북도")) {
            gps = "143";
        } else if (city.equals("부산") || city.equals("울산") || city.equals("경남") || city.equals("경상남도")) {
            gps = "159";
        } else if (city.equals("제주") || city.equals("제주도")) {
            gps = "184";
        } else { //지역을 입력하지 않았을 경우 기본값, 전국
            gps = "108";
        }
        return gps;
    }

    //토스트 메세지
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}