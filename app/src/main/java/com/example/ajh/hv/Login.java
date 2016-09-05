package com.example.ajh.hv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    TextView id,pw;
    CheckBox cb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        id = (TextView) findViewById(R.id.editText);
        pw = (TextView) findViewById(R.id.editText2);
        cb = (CheckBox) findViewById(R.id.checkBox);
        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();
        Check_AUTO();
    }
    public void Check_AUTO()
    {
        if(setting.getBoolean("AUTO", false))
        {
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("ID",setting.getString("ID",""));
            intent.putExtra("PW",setting.getString("PW",""));
            startActivity(intent);
            finish();
        }
    }
    public void onClick(View v)
    {
        if(cb.isChecked()) {
            editor.putString("ID", ""+id.getText());
            editor.putString("PW", ""+pw.getText());
            editor.putBoolean("AUTO", true);
            editor.commit();
        }
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("ID",""+id.getText());
        intent.putExtra("PW",""+pw.getText());
        System.out.println("LoginPlease"+id.getText()+pw.getText());
        startActivity(intent);
        finish();
    }
}
