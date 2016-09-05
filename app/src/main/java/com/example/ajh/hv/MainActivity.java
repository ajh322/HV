package com.example.ajh.hv;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    SharedPreferences setting;
    static SharedPreferences.Editor editor;
    static int number;
    static Runnable r = new thread_get();
    static Document doc2;
    public static Data data = new Data();
    private static Context Context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context = this;
        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();
        Intent intent = getIntent();
        try{
        String ID=intent.getExtras().getString("ID");
        String PW=intent.getExtras().getString("PW");
        data.ID=ID;
        data.PW=PW;
        }
        catch (Exception e) {}
        thread_login.start();
        GET_new();
        GET_lefts();
    }
    Thread thread_login = new Thread(new Runnable()
    {
        public void run()
        {
            try {
                Connection.Response res = Jsoup
                        .connect("https://forums.e-hentai.org/index.php?act=Login&CODE=01")
                        .timeout(5000)
                        .data("UserName", data.ID)
                        .data("PassWord", data.PW)
                        .data("ipb_login_submit", "Login!")
                        .data("ipb_login_submit", "Login!")
                        .data("b", "d")
                        .data("bt", "6")
                        .data("CookieDate", "1")
                        .method(Connection.Method.POST)
                        .execute();
                data.cookies=res.cookies();
                data.Login=true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    });
    public static void GET_new()
    {
        data.Waiting=0;
        new Thread(r).start();
    }
    public static void GET_lefts()
    {
        try {
            Get_HP_MP_SP();
            Get_lefts_5_things();
        }
        catch (Exception e)
        {
            //여기다가 로그인 실패하였으므로 데이터 초기화하고 로그인 액티비티로 다시 전환
            editor.remove("ID");
            editor.remove("PW");
            editor.remove("AUTO");
            editor.clear();
            editor.commit();
            System.out.println(e);
            re_Login(Context);
        }
    }
    public static String GET_middles()
    {
        String str="";
        while(data.Waiting==0)
        {}
        Elements middles_trs = doc2.select("body div#mainpane div#togpane_log table tbody tr");
        for(Element tr : middles_trs)
        {
            Elements middles_tds = tr.select("td");
            //System.out.println(middles_tds.size());
            str+=middles_tds.get(0).text()+" "+middles_tds.get(1).text()+" "+middles_tds.get(2).text()+"\n";
        }

        return str;
    }
    public static void re_Login(Context mContext) { //스태틱 메쏘드에서 인텐트를 사용해야할때 이런식으로 해야함.
        Toast.makeText(mContext, "Login failed!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, Login.class);
        mContext.startActivity(intent);
    }
    public static void Riddle(Context mContext) { //스태틱 메쏘드에서 인텐트를 사용해야할때 이런식으로 해야함.
        Toast.makeText(mContext, "Time to solve the riddle!",Toast.LENGTH_SHORT).show();
        Intent riddle = new Intent(mContext, RiddleMaster.class);
        mContext.startActivity(riddle);
    }
    public static String[][] GET_rights(Context mContext)
    {
        // 1차:몬스터번호 2차:알파벳,이름,체력,마나
        // 상태는 나중에 추가해야함.

        while(data.Waiting==0)
        {}
        Elements riddle = doc2.select("body div.stuffbox div#mainpane form#riddleform > div");
        if(riddle.size()!=0)
        {
            //리들마스터 걸렸음. 새로운 인텐트를 만들어야함.
            Riddle(mContext);
            String str[][]=null;
            MainActivity.data.riddle_url=riddle.get(2).select("img").attr("src");
            return str;
        }
        Elements monsters = doc2.select("body div.stuffbox div#mainpane div#monsterpane > div"); //이거 개수만큼 몬스터 개수임.
        number = monsters.size();
        String str[][] = new String[number][4];
        for(int i=0;i<number;i++)
        {
            //사망처리는 사망하면 체력바에있는 스타일이 없어짐을 이용하자.
            str[i][0]=monsters.get(i).select("div.btm2 div img").attr("src").substring(monsters.get(i).select("div.btm2 div img").attr("src").length()-5,monsters.get(i).select("div.btm2 div img").attr("src").length()-4);
            str[i][1]=new StringBuilder(Get_text(monsters.get(i).select("div.btm3").get(0))).reverse().toString();

            Elements HP_MP = monsters.get(i).select("div.btm4 div.btm5 div.chbd");
            if(HP_MP.size()==2) //HP, MP 스타일 존재
            {
                str[i][2]=HP_MP.get(0).select("img").attr("style").substring(6,HP_MP.get(0).select("img").attr("style").length()-2);
                str[i][3]=HP_MP.get(1).select("img").attr("style").substring(6,HP_MP.get(1).select("img").attr("style").length()-2);
            }
            else if(HP_MP.size()==1) // MP밖에 존재하지않음. => 사망에 이르렀다.
            {
                str[i][2]="0";
                str[i][3]="0";
            }
        }
        return str;
    }
    public static String[][] Get_bottoms() //0번 그냥 attack 1번
    {
        while(data.Waiting==0)
        {}
        String str[][] = new String[22][11];
        Elements divs = doc2.select("body div.stuffbox div#leftpane div#quickbar > div"); // 각 div들이 스킬 임.
        /*
        스킬 배열
        0[스킬이름]
        1[즉발성]
        2[bool 활성화,비활성화]
        3[battleaction]
        4[battle_targetmode]
        5[battle_subattack]
        6[이미지 url]
        7[설명]
        8[필요MP]
        9[필요O.C]
        10[쿨타임]
         */
        int i=6;
        for(Element element : divs)
        {
            String[] onmouseover;
            String url="http://hentaiverse.org"+element.select("img.btqi").attr("src");

            String[] onclick=element.attr("onclick").split(";");
            try {
                onmouseover = element.attr("onmouseover").split("\\(")[1].split("\\)")[0].split(", '");
            } catch ( Exception e )
            {
                i++;
                continue;
            }
            str[0][0]="Attack";
            str[0][1]="false";
            str[0][2]="true";
            str[0][3]="1";
            str[0][4]="attack";
            str[0][5]="0";
            str[0][6]=null;
            str[0][7]="Damages a single enemy. Depending on your equipped weapon, this can place certain status effects on the affected monster. To attack, click here, then click your target. Simply clicking an enemy will also perform a normal attack.";
            str[0][8]="0";
            str[0][9]="0";
            str[0][10]="0";

            str[1][0]="Attack";
            str[1][1]="false";
            str[1][2]="true";
            str[1][3]="1";
            str[1][4]="items";
            str[1][5]="0"; //subattack이 아이템 인덱스임 어떻게 처리할지...
            str[1][6]=null;
            str[1][7]="Use various consumable items that can replenish your vitals or augment your power in various ways.";
            str[1][8]="0";
            str[1][9]="0";
            str[1][10]="0";

            str[2][0]="Sprit";
            str[2][1]="true";
            str[2][2]="true";
            str[2][3]="1";
            str[2][4]="spirit";
            str[2][5]="0";
            str[2][6]=null;
            str[2][7]="Toggle Spirit Channeling";
            str[2][8]="0";
            str[2][9]="0";
            str[2][10]="0";

            str[3][0]="Defend";
            str[3][1]="true";
            str[3][2]="true";
            str[3][3]="1";
            str[3][4]="defend";
            str[3][5]="0";
            str[3][6]=null;
            str[3][7]="Increases your defensive capabilities for next turn.";
            str[3][8]="0";
            str[3][9]="0";
            str[3][10]="0";

            str[4][0]="Focus";
            str[4][1]="true";
            str[4][2]="true";
            str[4][3]="1";
            str[4][4]="focus";
            str[4][5]="0";
            str[4][6]=null;
            str[4][7]="Reduces the chance that your next spell will be resisted. Your defenses and evade chances are lowered for next turn.";
            str[4][8]="0";
            str[4][9]="0";
            str[4][10]="0";

            str[5][0]="Flee";
            str[5][1]="true";
            str[5][2]="true";
            str[5][3]="1";
            str[5][4]="magic";
            str[5][5]="1001";
            str[5][6]="http://hentaiverse.org//y/a/sflee.png";
            str[5][7]="Run away from the current battle.";
            str[5][8]="0";
            str[5][9]="0";
            str[5][10]="0";
            //0번 스킬이름 1번 설명 2번 의문의 코드, 소모마나,, 필요O.C, 쿨타임
            //onmouseover='Absorb', 'The next magical attack against the target has a chance to be absorbed and partially converted to MP.', '1033', 23, 0, 20
            //onclick=battle.lock_action(this, 1, 'magic', 1001); battle.set_friendly_subattack(1001); battle.touch_and_go()
            //onclick=battle.lock_action(this, 1, 'magic', 131); battle.set_hostile_subattack(131)
            //onclick 존재하는지, battle.lock_action안에 스킬이름 battleaction battle_targetmode battle_subattack battle.touch_and_go() 확인, 이미지 url
            if(element.attr("onclick")!="")
            {
                str[i][2]="true";
                String[] battle_lock_action=onclick[0].split("\\(")[1].split("\\)")[0].split(",");
                str[i][3]=battle_lock_action[1].replaceAll(" ","");
                str[i][4]=battle_lock_action[2].replaceAll("'","").replaceAll(" ","");
                str[i][5]=battle_lock_action[3].replaceAll(" ","");

                if(onclick.length == 3)
                {
                    str[i][1]="true";
                }
                else
                {
                    str[i][1]="false";
                }
                str[i][0]=onmouseover[0].substring(1,onmouseover[0].length()-1);
                str[i][7]=onmouseover[1].substring(0,onmouseover[1].length());
                str[i][8]=onmouseover[2].split(",")[1];
                str[i][9]=onmouseover[2].split(",")[2];
                str[i][10]=onmouseover[2].split(",")[3];

                //url 가져오기
                str[i][6]=url;
            }
            else // onmouseover로 정보가져옴 이거도 없으면 그냥 null임 있으면 활성화 X인 스킬
            {
                if(element.attr("onmouseover")!=null)
                {
                    //비활성화 스킬.
                    str[i][1]="false";
                    str[i][2]="false";
                    str[i][0]=onmouseover[0].substring(1,onmouseover[0].length()-1);
                    str[i][7]=onmouseover[1].substring(0,onmouseover[1].length());
                    str[i][8]=onmouseover[2].split(",")[1];
                    str[i][9]=onmouseover[2].split(",")[2];
                    str[i][10]=onmouseover[2].split(",")[3];

                    //url 가져오기
                    str[i][6]=url;
                }
            }
            i++;
        }
        return str;
    }
    public static void Get_HP_MP_SP()
    {
        while(data.Waiting==0) //로그인 쿠키가 담겨질때까지 기달리기.
        {}
        Elements lefts = doc2.select("div.clb");
        Elements HP_MP_SP = lefts.select("div.cwbdv div.cwbt div.fd2"); //이거의 개수로 전투 비전투 판단.
        System.out.println(HP_MP_SP.size());
        if(HP_MP_SP.size()==3) //비전투
        {
            Element HP = HP_MP_SP.get(0);
            Element MP = HP_MP_SP.get(1);
            Element SP = HP_MP_SP.get(2);
            String[][] str = new String[3][];
            str[0] = Get_text(HP).split(" / ");
            str[1] = Get_text(MP).split(" / ");
            str[2] = Get_text(SP).split(" / ");
            data.NOW_HP=str[0][0];
            data.MAX_HP=str[0][1];
            data.NOW_MP=str[1][0];
            data.MAX_MP=str[1][1];
            data.NOW_SP=str[2][0];
            data.MAX_SP=str[2][1];
            data.IS_FIGHTING=false;
        }
        else if(HP_MP_SP.size()==4) //전투
        {

            Element HP = HP_MP_SP.get(0);
            Element MP = HP_MP_SP.get(1);
            Element SP = HP_MP_SP.get(2);
            Element OVERCHARGE = HP_MP_SP.get(3);
            String[][] str = new String[4][2];
            str[0] = Get_text(HP).split(" / ");
            str[1] = Get_text(MP).split(" / ");
            str[2] = Get_text(SP).split(" / ");
            str[3][0] = Get_text(OVERCHARGE);
            data.NOW_HP=str[0][0];
            data.MAX_HP=str[0][1];
            data.NOW_MP=str[1][0];
            data.MAX_MP=str[1][1];
            data.NOW_SP=str[2][0];
            data.MAX_SP=str[2][1];
            data.NOW_OVERCHARGE=str[3][0];
            data.IS_FIGHTING=true;
        }

    }
    public static void Get_lefts_5_things()
    {
        Elements things = doc2.select("table.cit");
        Elements asd = things.select("tbody tr td > div");
            Element STAMI = asd.get(0);
            Element DIFF = asd.get(2);
            Element LV = asd.get(3);
            Element TNLV = asd.get(6);
            Element CREDIT = asd.get(8);
            String[] str = new String[5];
            str[0] = Get_text(STAMI);
            str[1] = Get_text(DIFF);
            str[2] = Get_text(LV);
            str[3] = Get_text(TNLV);
            str[4] = Get_text(CREDIT);
            data.STAMINA=str[0].split(" ")[1];
            data.Difficulty=str[1];
            data.LEVEL=str[2].split(" ")[1];
            data.ToNextLevel=str[3];
            data.Credit=str[4];
    }
    public static String Get_text(Element ele) //div 안에 div들로 이루어진 텍스트들이 있어야함.
    {
        String Str = "";
        List<String> Str_set;
        Elements elements = ele.select("div div");
        for (int i=elements.size()-1;i>=0;i--)
        {
            Str_set = new ArrayList<String>(elements.get(i).classNames());
            for(int j=0;j<=Str_set.size()-1;j++)
            {
                Str+=read_class(Str_set.get(j).toString());
            }
        }

        return Str;
    }
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.id_char:
                Intent intent_char = new Intent(this,Char.class);
                intent_char.putExtra("data", data);
                startActivity(intent_char);
                finish();
                break;
            case R.id.button3:
                Intent intent = new Intent(this,Donation.class);
                startActivity(intent);
                finish();
                break;
            case R.id.id_gr:
                if(data.IS_FIGHTING==true)
                {
                    Toast.makeText(this, "You are already in the battle.",Toast.LENGTH_SHORT).show();
                }
                else if(data.IS_FIGHTING==false) //gr입장 패킷전송
                {
                    data.IS_FIGHTING=true;
                    Runnable r = new thread_GO(1);
                    Thread asd = new Thread(r);
                    asd.start();
                    try {
                        asd.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent_battle_scene = new Intent(this,Battle_scene.class);
                finish();
                startActivity(intent_battle_scene);
                break;

        }
    }

    public static String read_class(String class_name)
    {
        switch (class_name) {
            case "f20":
            case "f40":
                return "0";
            case "f41":
            case "f21":
                return "1";
            case "f42":
            case "f22":
                return "2";
            case "f43":
            case "f23":
                return "3";
            case "f44":
            case "f24":
                return "4";
            case "f45":
            case "f25":
                return "5";
            case "f46":
            case "f26":
                return "6";
            case "f47":
            case "f27":
                return "7";
            case "f48":
            case "f28":
                return "8";
            case "f49":
            case "f29":
                return "9";
            case "f418":
            case "f218":
                return "/";
            case "f439":
            case "f239":
                return " ";
            case "f440":
            case "f240":
                return "A";
            case "f441":
            case "f241":
                return "B";
            case "f442":
            case "f242":
                return "C";
            case "f443":
            case "f243":
                return "D";
            case "f444":
            case "f244":
                return "E";
            case "f445":
            case "f245":
                return "F";
            case "f446":
            case "f246":
                return "G";
            case "f447":
            case "f247":
                return "H";
            case "f448":
            case "f248":
                return "I";
            case "f449":
            case "f249":
                return "J";
            case "f450":
            case "f250":
                return "K";
            case "f451":
            case "f251":
                return "L";
            case "f452":
            case "f252":
                return "M";
            case "f453":
            case "f253":
                return "N";
            case "f454":
            case "f254":
                return "O";
            case "f455":
            case "f255":
                return "P";
            case "f456":
            case "f256":
                return "Q";
            case "f457":
            case "f257":
                return "R";
            case "f458":
            case "f258":
                return "S";
            case "f459":
            case "f259":
                return "T";
            case "f460":
            case "f260":
                return "U";
            case "f461":
            case "f261":
                return "V";
            case "f462":
            case "f262":
                return "W";
            case "f463":
            case "f263":
                return "X";
            case "f464":
            case "f264":
                return "Y";
            case "f465":
            case "f265":
                return "Z";
            case "f410":
            case "f210":
                return ".";
            default:
                return "";
        }
    }
    public static class thread_get implements Runnable {
        public thread_get()
        {
            //기본생성자
        }

        public void run()
        {
            try {
                while(data.Login==false) //로그인 쿠키가 담겨질때까지 기달리기.
                {
                }
                doc2 = Jsoup
                        .connect("http://hentaiverse.org/")
                        .timeout(5000)
                        .cookies(data.cookies)
                        .get();
                data.Waiting=1;
                System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");

                //여기서 갱신할 정보들을 받아와야함.
/*
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.loading).setVisibility(View.GONE);
                    }
                });
*/
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public class thread_GO implements Runnable {
        int i;
        public thread_GO(int parameter) {
            i=parameter;
        }

        public void run() {
            try {
                doc2 = Jsoup
                        .connect("http://hentaiverse.org/?s=Battle&ss=gr")
                        .timeout(5000)
                        .data("arenaid", String.valueOf(i))
                        .cookies(data.cookies)
                        .post();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static Drawable fetchImage(final String urlstr ) {
        final int[] i = {0};
        final String str = urlstr;
        final Bitmap[] img = new Bitmap[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url;
                    url = new URL(str);

                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setDoInput(true);
                    c.connect();
                    InputStream is = c.getInputStream();
                    img[0] = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException e) {
                    Log.d("RemoteImageHandler", "fetchImage passed invalid URL: " + str);
                } catch (IOException e) {
                    Log.d("RemoteImageHandler", "fetchImage IO exception: " + e);
                }
                i[0] =1;
            }
        });
        thread.start();
        while(i[0]==0){}
        return new BitmapDrawable(img[0]);
    }
    public static class Data implements Serializable{
        Map<String, String> cookies;
        String ID;
        String PW;
        String MAX_HP;
        String NOW_HP;
        String MAX_MP;
        String NOW_MP;
        String STAMINA;
        String MAX_SP;
        String NOW_SP;
        String NOW_OVERCHARGE;
        String Difficulty;
        String LEVEL;
        String ToNextLevel;
        String Credit;
        String riddle_url;
        boolean IS_FIGHTING;
        boolean Login;
        int Waiting; //로그인 -> 접속
        public Data()
        { //생성자1: 객체가 생성될 때 호출
            IS_FIGHTING=false;
            Login=false;
            Waiting=0;
        }

    }
}