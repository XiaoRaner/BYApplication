package com.swufe.byapplication;
//主页面
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //打开新页面的方法3
  public void openOne(View btn){

       Intent config = new Intent(this, SearchActivity.class);//调用Intent对象。参数：从哪个窗口打开，要打开的窗口名字
        //Intent web=new Intent(Intent.)    //谷歌搜索：android点击按钮打开浏览器网页 //还可以打电话等

        startActivity(config); //打开窗口（可以传数据过去）
    }



    //添加菜单的方法
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list,menu);//把menu文件下的list.xml布局引入该方法,加载到menu中来
        return true;//返回真，表示菜单中有数据
    }

    //菜单处理的方法
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.data_list){//如果点击了 日期排列
            Intent datalist = new Intent(this, TodayActivity.class);
            startActivity(datalist);

        }else if(item.getItemId()==R.id.like_list){//如果点击了 下载榜
            Intent likelist = new Intent(this, SortActivity.class);
            startActivity(likelist);
        }
        else if(item.getItemId()==R.id.by_search){//如果点击了 下载榜
            openWeb();
        }


        return super.onOptionsItemSelected(item);
    }

    private void openWeb(){
        //打开一个页面Activity
        Intent web=new Intent(Intent.ACTION_VIEW,Uri.parse("https://cn.bing.com/"));   //谷歌搜索：android点击按钮打开浏览器网页 //还可以打电话等
        startActivity(web); //打开窗口（可以传数据过去）

    }


}
