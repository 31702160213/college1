package com.example.administrator.college1.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.college1.Bean.GradeBean;
import com.example.administrator.college1.R;
import com.example.administrator.college1.Utils.Local;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GradeView {
    private ListView mListView;
    private MyyBaseAdapter myAdapter;
    private Activity mContext;
    private LayoutInflater mInflater;
    private View mCurrentView;
    private List<GradeBean> scores;

 private void initData(){
     new Thread(new Runnable() {
         @Override
         public void run() {
             String url2 = Local.urls.get("学习成绩查询");
             try {
                 Document doc = Jsoup.connect(Local.url+url2)
                         .cookie("ASP.NET_SessionId",Local.sessionID.substring(Local.sessionID.indexOf("=") + 1, Local.sessionID.length()))
                         .referrer("http://jwc.mmvtc.cn/xs_main.aspx?xh="+Local.number)
                         .timeout(3000)
                         .get();
                 Elements inputs = doc.getElementsByTag("input");
                 String viewState = "";
                 for (Element e:inputs){
                     String view = e.attr("name");
                     if (view.equals("__VIEWSTATE")){
                         viewState =e.attr("value");
                     }
                 }

                 Document doc2 = Jsoup.connect(Local.url+url2)
                         .cookie("ASP.NET_SessionId",Local.sessionID.substring(Local.sessionID.indexOf("=") + 1, Local.sessionID.length()))
                         .data("__EVENTTARGET","")
                         .data("__EVENTARGUMENT","")
                         .data("__VIEWSTATE",viewState)
                         .data("ddlXN","")
                         .data("ddlXQ","")
                         .data("ddl_kcxz","")
                         .data("btn_zcj","历年成绩")
                         .referrer("http://jwc.mmvtc.cn/xs_main.aspx?xh="+Local.number)
                         .timeout(3000)
                         .post();
                 Element body =doc2.body();
                 Element tbody = body.getElementsByTag("table").get(1);
                 Elements trs =tbody.getElementsByTag("tr");
                 GradeBean scoreBean;
                 for (Element tr:trs){
                     if (tr==trs.get(0))continue;
                     Elements tds =tr.getElementsByTag("td");
                     scoreBean=new GradeBean();
                     for (int i=0;i<tds.size();i++){
                         if (i==2||i==4||i==5){
                             continue;
                         }
                         if (i==9)break;
                         Element td = tds.get(i);
                         String msg = td.text();
                         switch (i) {
                             case 0:
                                 scoreBean.setAcademicYear(msg);
                                 break;
                             case 1:
                                 scoreBean.setSemester(msg);
                                 break;
                             case 3:
                                 scoreBean.setCourseName(msg);
                                 break;
                             case 6:
                                 scoreBean.setCredits(msg);
                                 break;
                             case 7:
                                 scoreBean.setGradePoint(msg);
                                 break;
                             case 8:
                                 scoreBean.setResults(msg);
                                 break;
                         }
                     }
                     scores.add(scoreBean);
                 }
                 mContext.runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         myAdapter.notifyDataSetChanged();
                     }
                 });


             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }).start();
 }

    public GradeView(Activity context) {
        mContext = context;
        //为之后将Layout转化为view时用
        mInflater = LayoutInflater.from(mContext);
    }
    private  void createView() {
        initView();
    }

    /**
     * 获取界面控件
     */

    private void initView() {
        //设置布局文件
        scores = new ArrayList();
        mCurrentView = View.inflate(mContext, R.layout.activity_grade, null);
        //mInflater.inflate();
        mListView = (ListView) mCurrentView.findViewById(R.id.lv_grade2);
        myAdapter = new MyyBaseAdapter();
        mListView.setAdapter(myAdapter);
        initData();
    }

    public View getView() {
        if (mCurrentView == null) {
            createView();
        }
        return mCurrentView;
    }

    /**
     * 显示当前导航栏上方所对应的view界面
     */

    public void showView(){
        if(mCurrentView == null){
            createView();
        }
        mCurrentView.setVisibility(View.VISIBLE);
    }


    private class MyyBaseAdapter extends BaseAdapter {
        //得到item的总数
        @Override
        public int getCount() {
            return scores.size();
        }
        //得到item代表的对象
        @Override
        public Object getItem(int position) {
            return scores.get(position);
        }
        //得到item的id
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = View.inflate(mContext,R.layout.grade,null);
            TextView mTextView1 = (TextView) view.findViewById(R.id.tv_item1);
            TextView mTextView2 = (TextView) view.findViewById(R.id.tv_item2);
            TextView mTextView3 = (TextView) view.findViewById(R.id.tv_item3);
            TextView mTextView4 = (TextView) view.findViewById(R.id.tv_item4);
            TextView mTextView5 = (TextView) view.findViewById(R.id.tv_item5);
            TextView mTextView6 = (TextView) view.findViewById(R.id.tv_item6);
            mTextView1.setText(scores.get(position).getAcademicYear());
            mTextView2.setText(scores.get(position).getSemester());
            mTextView3.setText(scores.get(position).getCourseName());
            mTextView4.setText(scores.get(position).getCredits());
            mTextView5.setText(scores.get(position).getGradePoint());
            mTextView6.setText(scores.get(position).getResults());
            return view;
        }
    }


}
