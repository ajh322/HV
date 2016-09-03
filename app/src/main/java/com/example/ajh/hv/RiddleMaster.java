package com.example.ajh.hv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import org.jsoup.Jsoup;

import java.io.IOException;

public class RiddleMaster extends AppCompatActivity {

    ImageView imgview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riddle_master);
        imgview= (ImageView) findViewById(R.id.imageView);
        imgview.setImageDrawable(MainActivity.fetchImage(MainActivity.data.riddle_url));
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.A:
                Thread asd = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Jsoup
                                    .connect("http://hentaiverse.org/")
                                    .timeout(5000)
                                    .data("riddlemaster", "A")
                                    .cookies(MainActivity.data.cookies)
                                    .post();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                asd.start();
                try {
                    asd.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MainActivity.data.IS_FIGHTING)
                {
                    Intent intent = new Intent(this, Battle_scene.class);
                    startActivity(intent);
                }
                break;
            case R.id.B:
                Thread ads = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Jsoup
                                    .connect("http://hentaiverse.org/")
                                    .timeout(5000)
                                    .data("riddlemaster", "B")
                                    .cookies(MainActivity.data.cookies)
                                    .post();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ads.start();
                try {
                    ads.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MainActivity.data.IS_FIGHTING)
                {
                    Intent intent = new Intent(this, Battle_scene.class);
                    startActivity(intent);
                }
                break;
            case R.id.C:
                Thread dsa = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Jsoup
                                    .connect("http://hentaiverse.org/")
                                    .timeout(5000)
                                    .data("riddlemaster", "C")
                                    .cookies(MainActivity.data.cookies)
                                    .post();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dsa.start();
                try {
                    dsa.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
                if (MainActivity.data.IS_FIGHTING)
                {
                    Intent intent = new Intent(this, Battle_scene.class);
                    startActivity(intent);
                }
                break;
            case R.id.PC:
                Intent intent_PC = new Intent(this, PonyChart.class);
                startActivity(intent_PC);
                finish();
                break;
        }
    }
}
