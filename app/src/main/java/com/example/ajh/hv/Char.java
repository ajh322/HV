package com.example.ajh.hv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Char extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char);
        Intent intent=getIntent();
        MainActivity.Data data = (MainActivity.Data) intent.getSerializableExtra("data"); //메인엑티비티에서 데이터 받아오기.
        TextView HP = (TextView)findViewById(R.id.HP);
        TextView MP = (TextView)findViewById(R.id.MP);
        TextView SP = (TextView)findViewById(R.id.SP);

        HP.setText("HP: "+data.NOW_HP+" / "+data.MAX_HP);
        MP.setText("MP: "+data.NOW_MP+" / "+data.MAX_MP);
        SP.setText("SP: "+data.NOW_SP+" / "+data.MAX_SP);
    }
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.id_back:
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
