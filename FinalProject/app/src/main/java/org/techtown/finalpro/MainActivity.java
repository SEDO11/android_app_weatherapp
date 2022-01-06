package org.techtown.finalpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Weather");

        Button todayBtn = (Button) findViewById(R.id.todayWeatherBtn);
        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, today.class);
                startActivity(intent);
            }
        });

        Button covidBtn = (Button) findViewById(R.id.covidBtn);
        covidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, covid.class);
                startActivity(intent);
            }
        });

        Button airBtn = (Button) findViewById(R.id.airBtn);
        airBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, air.class);
                startActivity(intent);
            }
        });

        Button weekBtn = (Button) findViewById(R.id.weekWeatherBtn);
        weekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, week.class);
                startActivity(intent);
            }
        });
    }
}