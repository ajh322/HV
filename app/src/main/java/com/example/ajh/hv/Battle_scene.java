package com.example.ajh.hv;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

//다음스테이지 넘어가는 기능 : 몬스터 HP 프로그레시브바가 전부 0이면 버튼누르고 인텐트 재시작하면됨.
public class Battle_scene extends Activity implements View.OnClickListener{

    private static Context mContext;
    String first_selected_skill="";
    String second_selected_skill="";
    boolean ready_for_target=false;
    int monster_number;
    LinearLayout rights;
    LinearLayout rights_left;
    LinearLayout rights_right;
    LinearLayout bottoms_top;
    LinearLayout bottoms_bottom;
    TextView HP;
    TextView MP;
    TextView SP;
    TextView CHARGE;
    TextView STAMI;
    TextView DIFF;
    TextView LV;
    TextView TNLV;
    TextView CREDIT;
    TextView MID_TEXT;
    Document doc;
    LinearLayout[] monster_layouts;
    LinearLayout[] thing_layouts;
    LinearLayout[] skill_layouts;
    ImageView[] skill_icons;
    TextView[] monsters_textview_alpha;
    TextView[] monsters_textview_name;
    ProgressBar[] monsters_HP;
    ProgressBar[] monsters_MP;
    String[][] str_rights;
    String[][] str_bottoms;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_scene);
        mContext = this;
        rights = (LinearLayout)findViewById(R.id.rights);
        rights_left = (LinearLayout)findViewById(R.id.right_layout_left);
        rights_right = (LinearLayout)findViewById(R.id.right_layout_right);
        HP = (TextView)findViewById(R.id.HP);
        MP = (TextView)findViewById(R.id.MP);
        SP = (TextView)findViewById(R.id.SP);
        CHARGE = (TextView)findViewById(R.id.CHARGE);
        STAMI = (TextView)findViewById(R.id.STAMI);
        DIFF = (TextView)findViewById(R.id.DIFF);
        LV = (TextView)findViewById(R.id.LV);
        TNLV = (TextView)findViewById(R.id.TNLV);
        CREDIT = (TextView)findViewById(R.id.CREDIT);
        MID_TEXT = (TextView)findViewById(R.id.middle_text);
        bottoms_bottom=(LinearLayout)findViewById(R.id.bottoms_bottom);
        bottoms_top=(LinearLayout)findViewById(R.id.bottoms_top);
        pre_set_resources();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void pre_set_resources()
    {
        MainActivity.GET_new();
        MainActivity.GET_lefts();
        str_rights = MainActivity.GET_rights(mContext);
        if(str_rights==null)
        {
            finish();
            //return;
        }
        skill_layouts = new LinearLayout[22];
        skill_icons = new ImageView[22];
        for(int i = 0; i < 22; i++) //bottoms 디스플레이
        {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(3, Color.WHITE);
            drawable.setCornerRadius(2.5f);
            bottoms_top.setWeightSum(11f);
            bottoms_bottom.setWeightSum(11f);
            if(i<11)
            {
                skill_layouts[i] = new LinearLayout(this);
                skill_layouts[i].setId(i);
                skill_layouts[i].setBackgroundDrawable(drawable);
                skill_layouts[i].setOrientation(LinearLayout.HORIZONTAL);
                skill_layouts[i].setOnClickListener(this);
                LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1f);
                skill_layouts[i].setLayoutParams(childParam1);
                bottoms_top.addView(skill_layouts[i]);
            }
            else
            {
                skill_layouts[i] = new LinearLayout(this);
                skill_layouts[i].setId(i);
                skill_layouts[i].setBackgroundDrawable(drawable);
                skill_layouts[i].setOrientation(LinearLayout.HORIZONTAL);
                skill_layouts[i].setOnClickListener(this);
                LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1f);
                skill_layouts[i].setLayoutParams(childParam1);
                bottoms_bottom.addView(skill_layouts[i]);
            }
        }
        monster_number=MainActivity.number;
        if(MainActivity.data.IS_FIGHTING==true) //이미 전투중
        {
            monster_layouts = new LinearLayout[10];
            thing_layouts = new LinearLayout[10];
            monsters_textview_alpha = new TextView[10];
            monsters_textview_name = new TextView[10];
            monsters_HP = new ProgressBar[10];
            monsters_MP = new ProgressBar[10];


            for(int i = 0; i < 10; i++) { //monsters 디스플레이
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(3, Color.WHITE);
                drawable.setCornerRadius(8);
                monster_layouts[i] = new LinearLayout(this);
                monster_layouts[i].setOnClickListener(listener);
                monster_layouts[i].setId(i);
                monster_layouts[i].setBackgroundDrawable(drawable);
                monster_layouts[i].setOrientation(LinearLayout.HORIZONTAL);
                if(i>=5) //오른쪽 레이아웃에 등록하기, 각 레이아웃마다 [알파벳 텍스트뷰] 또하나의 [버티컬 레이아웃] 하나 추가하고 버티컬 레이아웃에 [텍스트뷰][호리즌 프로그레스바x2]
                {
                    rights_right.setWeightSum(10f);
                    // 레이아웃 등록
                    LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,2f);
                    monster_layouts[i].setLayoutParams(childParam1);
                    rights_right.addView(monster_layouts[i]);

                    monster_layouts[i].setWeightSum(10f);
                    //텍스트뷰 레이아웃에 넣을꺼임 알파벳 표시
                    monsters_textview_alpha[i] = new TextView(this);
                    LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,3f);
                    monsters_textview_alpha[i].setLayoutParams(childParam2);
                    monsters_textview_alpha[i].setBackgroundColor(Color.parseColor("#00FFFFFF"));
                    monsters_textview_alpha[i].setPadding(10, 10, 10, 10);
                    monsters_textview_alpha[i].setTextColor(Color.parseColor("#FF7200"));
                    monsters_textview_alpha[i].setText("B");
                    monster_layouts[i].addView(monsters_textview_alpha[i]);

                    //버티컬 레이아웃 레이아웃에 넣을꺼임
                    thing_layouts[i] = new LinearLayout(this);
                    thing_layouts[i].setId(i);
                    thing_layouts[i].setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams childParam3 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,7f);
                    thing_layouts[i].setLayoutParams(childParam3);
                    monster_layouts[i].addView(thing_layouts[i]);

                    //이제 3가지 thing_layouts에 추가해야함.

                    thing_layouts[i].setWeightSum(10f);
                    //텍스트뷰 몬스터이름
                    monsters_textview_name[i] = new TextView(this);
                    LinearLayout.LayoutParams childParam4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,6f);
                    monsters_textview_name[i].setLayoutParams(childParam4);
                    monsters_textview_name[i].setBackgroundColor(Color.parseColor("#00FFFFFF"));
                    monsters_textview_name[i].setTextColor(Color.parseColor("#FF7200"));
                    monsters_textview_name[i].setText("AJH");
                    thing_layouts[i].addView(monsters_textview_name[i]);

                    //프로그레스바 HP
                    monsters_HP[i] = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
                    LinearLayout.LayoutParams childParam5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,2f);
                    monsters_HP[i].setLayoutParams(childParam5);
                    monsters_HP[i].setProgress(50);
                    thing_layouts[i].addView(monsters_HP[i]);

                    //프로그레스바 MP
                    monsters_MP[i] = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
                    monsters_MP[i].setLayoutParams(childParam5);
                    monsters_MP[i].setProgress(50);
                    thing_layouts[i].addView(monsters_MP[i]);
                }
                else //왼쪽레이아웃에 등록하기
                {
                    rights_left.setWeightSum(10f);
                    // 레이아웃 등록
                    LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,2f);
                    monster_layouts[i].setLayoutParams(childParam1);
                    rights_left.addView(monster_layouts[i]);

                    monster_layouts[i].setWeightSum(10f);
                    //텍스트뷰 레이아웃에 넣을꺼임 알파벳 표시
                    monsters_textview_alpha[i] = new TextView(this);
                    LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,3f);
                    monsters_textview_alpha[i].setLayoutParams(childParam2);
                    monsters_textview_alpha[i].setBackgroundColor(Color.parseColor("#00FFFFFF"));
                    //monsters_textview_alpha[i].setPadding(10, 10, 10, 10);
                    monsters_textview_alpha[i].setTextColor(Color.parseColor("#FF7200"));
                    monsters_textview_alpha[i].setText("B");
                    monster_layouts[i].addView(monsters_textview_alpha[i]);

                    //버티컬 레이아웃 레이아웃에 넣을꺼임
                    thing_layouts[i] = new LinearLayout(this);
                    thing_layouts[i].setId(i);
                    thing_layouts[i].setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams childParam3 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,7f);
                    thing_layouts[i].setLayoutParams(childParam3);
                    monster_layouts[i].addView(thing_layouts[i]);

                    //이제 3가지 thing_layouts에 추가해야함.

                    thing_layouts[i].setWeightSum(10f);
                    //텍스트뷰 몬스터이름
                    monsters_textview_name[i] = new TextView(this);
                    LinearLayout.LayoutParams childParam4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,6f);
                    monsters_textview_name[i].setLayoutParams(childParam4);
                    monsters_textview_name[i].setBackgroundColor(Color.parseColor("#00FFFFFF"));
                    monsters_textview_name[i].setTextColor(Color.parseColor("#FF7200"));
                    monsters_textview_name[i].setText("AJH");
                    thing_layouts[i].addView(monsters_textview_name[i]);

                    //프로그레스바 HP
                    monsters_HP[i] = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
                    LinearLayout.LayoutParams childParam5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,2f);
                    monsters_HP[i].setLayoutParams(childParam5);
                    monsters_HP[i].setProgress(50);
                    thing_layouts[i].addView(monsters_HP[i]);

                    //프로그레스바 MP
                    monsters_MP[i] = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
                    monsters_MP[i].setLayoutParams(childParam5);
                    monsters_MP[i].setProgress(50);
                    thing_layouts[i].addView(monsters_MP[i]);
                }
            }
        }
        else //전투중 아님
        {
            finish();
            return;
        }
        while(MainActivity.data.Waiting==0)
        {}
        if(MainActivity.data.IS_FIGHTING==true)
        {
            HP.setText("HP: "+MainActivity.data.NOW_HP+" / "+MainActivity.data.MAX_HP);
            MP.setText("MP: "+MainActivity.data.NOW_MP+" / "+MainActivity.data.MAX_MP);
            SP.setText("SP: "+MainActivity.data.NOW_SP+" / "+MainActivity.data.MAX_SP);
            CHARGE.setText("O.C: "+MainActivity.data.NOW_OVERCHARGE+"%");
            STAMI.setText("Stamina:"+MainActivity.data.STAMINA);
            DIFF.setText("Difficuly:"+MainActivity.data.Difficulty);
            LV.setText("Level:"+MainActivity.data.LEVEL);
            TNLV.setText("NextLevel:"+MainActivity.data.ToNextLevel);
            CREDIT.setText("Credit:"+MainActivity.data.Credit);
            MID_TEXT.setText(MainActivity.GET_middles());
            for(int i=0;i<monster_number;i++)
            {
                monsters_textview_alpha[i].setText(str_rights[i][0]);
                monsters_textview_name[i].setText(str_rights[i][1]);
                monsters_HP[i].setProgress((Integer.parseInt(str_rights[i][2])*100)/120);
                monsters_MP[i].setProgress((Integer.parseInt(str_rights[i][3])/100)/120);
            }
            for(int i=monster_number;i<10;i++)
            {
                monster_layouts[i].setVisibility(View.GONE);
            }
            str_bottoms=MainActivity.Get_bottoms();
            LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            for(int i=0; i<22;i++)
            {
                skill_icons[i] = new ImageView(this);
                skill_icons[i].setLayoutParams(childParam2);
                skill_icons[i].setPadding(0,1,0,6);
                if(str_bottoms[i][2]=="false")
                    skill_icons[i].setImageAlpha(50);
                skill_icons[i].setImageDrawable(MainActivity.fetchImage(str_bottoms[i][6]));
                try {
                    skill_layouts[i].addView(skill_icons[i]);
                }
                catch (Exception e) { System.out.println(e); }
            }
        }
        else
        {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void get_some()
    {
        MainActivity.GET_new();
        MainActivity.GET_lefts();
        str_rights = MainActivity.GET_rights(mContext);
        System.out.println("size:"+str_rights.length);
        Check_end();
        if(MainActivity.data.IS_FIGHTING==true)
            {
                HP.setText("HP: "+MainActivity.data.NOW_HP+" / "+MainActivity.data.MAX_HP);
                MP.setText("MP: "+MainActivity.data.NOW_MP+" / "+MainActivity.data.MAX_MP);
                SP.setText("SP: "+MainActivity.data.NOW_SP+" / "+MainActivity.data.MAX_SP);
                CHARGE.setText("O.C: "+MainActivity.data.NOW_OVERCHARGE+"%");
                STAMI.setText("Stamina:"+MainActivity.data.STAMINA);
                DIFF.setText("Difficuly:"+MainActivity.data.Difficulty);
                LV.setText("Level:"+MainActivity.data.LEVEL);
                TNLV.setText("NextLevel:"+MainActivity.data.ToNextLevel);
                CREDIT.setText("Credit:"+MainActivity.data.Credit);
                MID_TEXT.setText(MainActivity.GET_middles());
                monster_number=MainActivity.number;
                System.out.println(monster_number);
                for(int i=0;i<10;i++)
                {
                    if(i<monster_number)
                        monster_layouts[i].setVisibility(View.VISIBLE);
                    if(i>=monster_number)
                        monster_layouts[i].setVisibility(View.GONE);
                }
                for(int i=0;i<monster_number;i++)
                {
                    monsters_textview_alpha[i].setText(str_rights[i][0]);
                    monsters_textview_name[i].setText(str_rights[i][1]);
                    monsters_HP[i].setProgress((Integer.parseInt(str_rights[i][2])*100)/120);
                    monsters_MP[i].setProgress((Integer.parseInt(str_rights[i][3])/100)/120);
                }
                str_bottoms=MainActivity.Get_bottoms();
                for(int i=0; i<22;i++)
                {
                    if(str_bottoms[i][2]=="false")
                        skill_icons[i].setImageAlpha(50);
                }
            }
        else
        {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    public void Check_end()
    {
        int HPS=0;
        for(String[] A : str_rights)
        {
            HPS+=Integer.parseInt(A[2]);
            System.out.println("A[2]:"+Integer.parseInt(A[2])+"      HPS:"+HPS);
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    View.OnClickListener listener = new View.OnClickListener(){ //몬스터 레이아웃 클릭시 오는 버튼리스너
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            if(ready_for_target==true)
            {
                final int monster_index = v.getId();
                final int index= Integer.parseInt(first_selected_skill);
                // 제대로 된것임 전송해야함.
                for(int i=0;i<monster_number;i++)
                {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setStroke(3, Color.WHITE);
                    drawable.setCornerRadius(2.5f);
                    monster_layouts[i].setBackground(drawable);
                }
                Thread asd = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //아 깐깐하게시리 데이터에 ''붙이면 작동안한디야
                            //System.out.println("sending:"+"  baatleaction:"+str_bottoms[index][3]+"  battle_targetmode:"+str_bottoms[index][4]+"   battle_target:"+monster_index+1+"  battle_subattack:"+str_bottoms[index][5]);
                            Jsoup
                                    .connect("http://hentaiverse.org/")
                                    .timeout(5000)
                                    .data("battleaction", str_bottoms[index][3])
                                    .data("battle_targetmode", str_bottoms[index][4])
                                    .data("battle_target", String.valueOf((monster_index+1)))
                                    .data("battle_subattack", str_bottoms[index][5])
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
                    get_some();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ready_for_target=false;
                first_selected_skill="";
            }
        }
    };
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onClick(View v)
    {
        final int index=v.getId();
        //System.out.println(index);
        //활성화 체크
        if(str_bottoms[index][2]=="true")
        {
            if(str_bottoms[index][1]=="true") //즉발처리 가지고 있는 데이터로 바로 패킷 전송함
            {
                Thread asd = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                                //아 깐깐하게시리 데이터에 ''붙이면 작동안한디야
                            //System.out.println("sending:"+"  baatleaction:"+str_bottoms[index][3]+"  battle_targetmode:"+str_bottoms[index][4]+"  battle_subattack:"+str_bottoms[index][5]);
                            Jsoup
                            .connect("http://hentaiverse.org/")
                            .timeout(5000)
                            .data("battleaction", str_bottoms[index][3])
                            .data("battle_targetmode", str_bottoms[index][4])
                            .data("battle_target", "0")
                            .data("battle_subattack", str_bottoms[index][5])
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
                    get_some();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //리셋
            }
            else
            {
                //비즉발성 스킬임. 따라서 빡짝이 활성화한다음에 몬스터 타겟 인덱스 받아온뒤에 패킷전송.
                if(first_selected_skill==""&&second_selected_skill=="")
                {
                    for(int i=0;i<monster_number;i++)
                    {
                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setShape(GradientDrawable.RECTANGLE);
                        drawable.setStroke(3, Color.YELLOW);
                        drawable.setCornerRadius(2.5f);
                        monster_layouts[i].setBackground(drawable);
                    }
                    ready_for_target=true;
                    first_selected_skill= String.valueOf(index);
                }
                else if(first_selected_skill==String.valueOf(index))
                { //같은스킬 두번누름 => 취소
                    for(int i=0;i<monster_number;i++)
                    {
                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setShape(GradientDrawable.RECTANGLE);
                        drawable.setStroke(3, Color.WHITE);
                        drawable.setCornerRadius(2.5f);
                        monster_layouts[i].setBackground(drawable);
                    }
                    ready_for_target=false;
                    first_selected_skill="";
                    second_selected_skill="";
                }
                else if(first_selected_skill!=String.valueOf(index))
                {
                    for(int i=0;i<monster_number;i++)
                    {
                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setShape(GradientDrawable.RECTANGLE);
                        drawable.setStroke(3, Color.YELLOW);
                        drawable.setCornerRadius(2.5f);
                        monster_layouts[i].setBackground(drawable);
                    }
                    //다른스킬 선택함.
                    ready_for_target=true;
                    first_selected_skill= String.valueOf(index);
                }
                //System.out.println("first_selected_skill:"+first_selected_skill+"  second_selected_skill:"+second_selected_skill);
            }
        }
    }
    Thread thread = new Thread(new Runnable() {
        public void run() {
            try {
                doc = Jsoup
                        .connect("http://hentaiverse.org/")
                        .timeout(5000)
                        .cookies(MainActivity.data.cookies)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
}
