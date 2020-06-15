package com.swufe.byapplication;
//按标题查询必应图片（主页面——SEARCH）
//AM文件需要配置网络权限
//build.gradle界面需要配置  implementation 'org.jsoup:jsoup:1.11.3'  解析包
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener{

    EditText input;//输入控件
    String TAG="TAG",updateDate="";
    Handler handler;
    String NR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);//引入界面

        input=(EditText)findViewById(R.id.inputin);//引入输入控件

//更新时间
        SharedPreferences SP=getSharedPreferences("Time", Activity.MODE_PRIVATE);
        updateDate=SP.getString("update_rate","");
        Date today= Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy_MM_dd");
        final String today_sdr=sdf.format(today);

        Log.i(TAG,"oncreate:需要更新的时间"+updateDate);
        Log.i(TAG,"oncreate:当前时间"+today_sdr);

        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE,1);
        today = ca.getTime();
        final String enddate = sdf.format(today);
        if(!today_sdr.equals(updateDate)){
            Thread t=new Thread(this);
            t.start();
            Log.i(TAG,"oncreate:需要更新");
        }
        else{
            Log.i(TAG,"oncreate:不需要更新");
        }


//储存数据
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    SharedPreferences SP=getSharedPreferences("Time",Activity.MODE_PRIVATE);
                   SharedPreferences.Editor editor=SP.edit();
                   editor.putString("update_rate",enddate);
                    Log.i(TAG,"oncreate:更新时间"+enddate);
                    Toast.makeText(SearchActivity.this,"已更新~\\(≧▽≦)/~",Toast.LENGTH_LONG).show();;
                }
                super.handleMessage(msg);
            }


        };



    }

    //按钮事件处理
    public void onClick(View btn) {
        NR=input.getText().toString();//输入的查询内容
        if (NR.length() > 0) {//判断有没有输入内容，有内容时：
            Log.i(TAG,"输入内容是：run="+NR);

            if(btn.getId()==R.id.select){  //引入搜索控件
                SharedPreferences sp=getSharedPreferences("Title", Activity.MODE_PRIVATE);//从SharedPreferences中取出key为title的键
                SharedPreferences.Editor editor = sp.edit();//创建editor,数据通过editor传入sp
                int m=sp.getInt("I",100);//数字m，标题总数
                String data[]=new String[m+1];//符串data，长度为标题总数加1

                for(int i=1000,x=0;i<=m;i++){
                    String I= String.valueOf(i);//字符I，是数据i的格式转化
                    String v=sp.getString(I,"  "); //字符V，标题内容，从sp中提取出的
                  //  Log.i("TAG","从sp中提取出的标题：run="+v);
                    if(v.contains(NR)){//如果字符串v中包含要查询的内容
                        data[x]=v;//就放入字符串data[]中，下标为x
                        Log.i("TAG","包含查询内容的匹配项：run="+v);

                        String X= String.valueOf(x);
                        editor.putString(X,I);//把匹配信息的在sp中的key值，作为数据存入sp     X:从0递增，循环一次加1   I：匹配成功的标题的下标值
                        Log.i("TAG","存入sp的Key值（X的值）：run="+X);
                        Log.i("TAG","匹配成功的标题的原key值（I的值）：run="+I);


                        x++;//下标加1
                    }
                }

                if(data[0]==null){//没有匹配项时
                    Toast.makeText(this, "没有查到哦", Toast.LENGTH_SHORT).show();
                    String erry[]={"呜呜呜，什么都没有查到╮(╯_╰)╭"};
                    ListView listView=findViewById(R.id.list);  //引入列表
                    ListAdapter adapter=new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_list_item_1,erry);
                    listView.setAdapter(adapter);                }
                else{ //有匹配项时
                    ListView listView=findViewById(R.id.list);//调用列表控件
                    ListAdapter adapter=new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_list_item_1,data);
                    listView.setAdapter(adapter);//设置adapter匹配工具，匹配列表和数据

                    listView.setOnItemClickListener(this);}//设置列表监听

                editor.commit();//关闭editor

            }
        } else {
            Toast.makeText(this, "亲，还没输入查询的关键词呢-(￢∀￢)σ", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Document doc=null;
        try {//解析网页

            SharedPreferences sp = getSharedPreferences("Title", Activity.MODE_PRIVATE);//获取SP里保存的数据
            SharedPreferences.Editor editor = sp.edit();//创建editor,数据通过editor传入sp


              //获取网页
            for(int p=1;p<=10;p++) {
                doc = Jsoup.connect("https://bing.ioliu.cn/?p="+p).get();
                //   Log.i("TAG","run="+doc.title());//可以判断有没有获取到网页

             //获得链接【下标从10000递增】
                Elements tables = doc.getElementsByTag("div");//得到div的集合
                //Element div = tables.get(4);
                //div里面是单项值
                //  Log.i("TAG","run="+"tables里面是："+tables);

                //创建sp，用于数据的存储     key为title

                int j = 4;
                int i = 10000 + 12 * (p - 1);
                for (int n = 0; n < 12; n++) {
                    String A = tables.get(j).getElementsByTag("a").attr("href");
                    String B = "https://bing.ioliu.cn/".concat(A.substring(1)).concat("?p=" + p);//B是完整链接

                    Log.i(TAG, "run: 编码url[" + i + "]=" + "完整的链接是：" + B);//检查完整链接的情况
                    String J = String.valueOf(i); //j转成字符串
                    editor.putString(J, B);//将键为J,值为B(链接)的数据存入 editor
                    i = i + 1;
                    j = j + 4;
                }


              //获得标题【下标从1000递增】
                Elements h3 = doc.getElementsByTag("h3");
                //Log.i("TAG", "run=" + "h3里面是(应该是标题集合)：" + h3);


                int m = 1000 + 12 * (p - 1);
                for (int k = 0; k < h3.size(); k++) {
                    Element td = h3.get(k);
                    String val = td.text();
                    String II = String.valueOf(m);//内容编号
                    Log.i("TAG", "run=" + "h3里面的内容是：" + val + "编号是：" + II);
                    editor.putString(II, val);//将键为II的值,值为val(标题)的数据存入 editor

                    editor.putInt("I",m);//将键为I,值为val(标题)的数据存入 editor
                    m++;

                }
            }//页码循环结束


            editor.commit();//关闭editor




        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg=handler.obtainMessage(5);
        handler.sendMessage(msg);
    }
//在之前的方法里，分别从网页中解析出了链接和标题文字，并且用 editor.putString(key键（最好用数字）,值）的方式存入editor,放入了SP。
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //3个参数：adapter,行对象，
        SharedPreferences sp=getSharedPreferences("Title", Activity.MODE_PRIVATE);//用key-"Title"把存储的标题、链接取出（点击哪个取哪个）

        String Po=String.valueOf(position);
        Log.i("TAG","点击触发的位置编码是：run="+Po);//发现是从0递增，同X值

        String A=sp.getString(Po,"  ");
        Log.i("TAG","用“触发的位置编码”作为key键取出的标题下标值是：run="+A);
        int AA = Integer.parseInt(A);
        String Position=String.valueOf(AA+9000);//点击的是文字，更改位置，使实际上是链接被点击。位置的更改是根据文字和链接存入editor时关键词的差异来定。
        Log.i("TAG","更改后的位置编码是：run="+Position);
        String URL=sp.getString(Position,"");
        Log.i("TAG","链接：run="+URL);
        Intent web=new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(web);//记得要在AndroidManifest.xml界面中加权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
    }
}