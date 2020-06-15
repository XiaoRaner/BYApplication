package com.swufe.byapplication;
//按日期排列（主页面——最新榜）
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TodayActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener {
    Handler handler;//handler是用于接收子线程的消息处理
    String TAG="TAG";
    String data[]= {"正在加载，请稍等哦 (＊￣︶￣＊) ..."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_today);

        ListAdapter adapter = new ArrayAdapter<String>(TodayActivity.this,android.R.layout.simple_list_item_1,data);//页面，布局，数据。adapter负责匹配
        setListAdapter(adapter);

        Thread t=new Thread(this);
        t.start();                   //开启线程
        Log.i(TAG, "线程已开启 " );


        handler=new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5) { //数据获取
                    Log.i(TAG, "数据已获得 ");

                    List<String> list2=(List<String>)msg.obj;//拆包
                    ListAdapter adapter = new ArrayAdapter<String>(TodayActivity.this,android.R.layout.simple_list_item_1,list2);//引入adapter,作为沟通数据和列表的桥梁
                    setListAdapter(adapter);//父类提供的方法，使当前界面由adapter来管理
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);//设置列表监听
    }

    @Override
    public void run() {
        List<String> retList=new ArrayList<String>();
        Log.i(TAG,"方法run开始运行");
        Document doc=null;
        try{
            SharedPreferences sp = getSharedPreferences("Title", Activity.MODE_PRIVATE);//获取SP里保存的数据
            SharedPreferences.Editor editor = sp.edit();//创建editor,数据通过editor传入sp


            Thread.sleep(1000);
            for(int p=1;p<=10;p++) {//控制和修改页码处
                Log.i(TAG,"run="+p);
                doc = Jsoup.connect("https://bing.ioliu.cn/?p="+p).get();
                Log.i(TAG,"run="+doc);
                Log.i(TAG,"run="+doc.title());//可以判断有没有获取到网页

                //获得链接【下标从10000递增】
                Elements tables = doc.getElementsByTag("div");//得到div的集合
                //获得标题【下标从1000递增】
                Elements h3 = doc.getElementsByTag("h3");
                //Log.i("TAG", "run=" + "h3里面是(应该是标题集合)：" + h3);

                int j = 4;
                int i = 10000 + 12 * (p - 1);
                int m = 1000 + 12 * (p - 1);
                for (int n = 0; n < 12; n++) {
                    String A = tables.get(j).getElementsByTag("a").attr("href");
                    String B = "https://bing.ioliu.cn/".concat(A.substring(1)).concat("?p=" + p);//B是完整链接
                    Log.i(TAG, "run: 编码url[" + i + "]=" + "完整的链接是：" + B);//检查完整链接的情况
                    String J = String.valueOf(i); //j转成字符串
                    editor.putString(J, B);//将键为J,值为B(链接)的数据存入 editor
                    i = i + 1;
                    j = j + 4;
                }

                for (int k = 0; k < h3.size(); k++) {
                    Element td = h3.get(k);
                    String val = td.text();//val即标题内容
                    String II = String.valueOf(m);//内容编号
                    editor.putString(II, val);//将键为II的值,值为val(标题)的数据存入 editor

                    retList.add(val);
                    Log.i(TAG, "run=" + "h3里面的内容是：" + val + "编号是：" + II);
                    m++;

                }
            }
            editor.commit();//关闭editor

        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(5);//获得数据
        msg.obj = retList;//存放数据给obj
        handler.sendMessage(msg);//发送消息
        }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//Adapter对象，行对象，position和id一样，

       SharedPreferences sp=getSharedPreferences("Title", Activity.MODE_PRIVATE);//用key-"Title"把存储的标题、链接取出（点击哪个取哪个）
        String Position=String.valueOf(position+10000);//点击的是文字，更改位置，使实际上是链接被点击。位置的更改是根据文字和链接存入editor时关键词的差异来定。
        Log.i("TAG","更改后的位置编码是：run="+Position);
        String URL=sp.getString(Position,"");
        Log.i("TAG","链接：run="+URL);
       Intent web=new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
       startActivity(web);

    }
}

