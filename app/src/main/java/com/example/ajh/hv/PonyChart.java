package com.example.ajh.hv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PonyChart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pony_chart);
    }
    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, RiddleMaster.class);
        startActivity(intent);
    }
}
