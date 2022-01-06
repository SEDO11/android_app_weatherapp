package org.techtown.finalpro;

import android.os.Bundle;
import android.view.View;
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

public class covid extends AppCompatActivity {

    TextView covidView;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.covid);
        setTitle("금일 코로나 확진자");
        covidView = (TextView)findViewById(R.id.covidView);
    }

    // 버튼을 클릭 했을 경우
    public void btnClick(View v){
        switch (v.getId()){
            case R.id.button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String time = time();
                        data = getXmlData(time);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                covidView.setText(data);
                                if(data.trim().equals("")){
                                    showToast("아직 데이터가 업데이트되지 않아 \n금일 코로나 확진 정보를 갖고 오지 못했습니다.");
                                } else {
                                    showToast("금일 코로나 확진 정보를 갖고왔습니다.");
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

    //코로나 확진자 데이터를 api에서 파싱해서 가져옴
    String getXmlData(String getDay){ //연월일 데이터 가져옴
        StringBuffer buffer=new StringBuffer();
        String query="FWh87%2FqaLEma7tme7KUMsUs6zp6rbczh1uHDI88B80cXFV29f1uSbPx5tCvgP3eH8jf1vxJ1i0vWZbPXUpGelQ%3D%3D";
        String queryUrl="http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson?serviceKey="+ query +"&pageNo=1&numOfRows=10&startCreateDt="+ getDay + "&endCreateDt=" + getDay;
        // "+ getDay + "

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
                        else if(tag.equals("gubun")){ //위치
                            xpp.next();
                            buffer.append(xpp.getText());//title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("incDec")){
                            buffer.append(" 확진자 : ");
                            xpp.next();
                            buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append(" 명");
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

    //토스트 메세지
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
