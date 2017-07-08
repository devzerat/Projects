package com.dev.zerat.goplabresult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultDetailListActivity extends Activity {

    private class ParamsInfo {
        private String m_tckn;
        private String m_mfileNo;

        public String gettckn() {
            return m_tckn;
        }

        public void settckn(String m_tckn) {
            this.m_tckn = m_tckn;
        }

        public String getfileNo() {
            return m_mfileNo;
        }

        public void setfileNo(String m_mfileNo) {
            this.m_mfileNo = m_mfileNo;
        }

        public String geturl() {
            return m_url;
        }

        public void seturl(String m_url) {
            this.m_url = m_url;
        }

        private String m_url;

        public ParamsInfo(String tckn, String fileno, String url)
        {
            m_tckn = tckn;
            m_mfileNo = fileno;
            m_url = url;
        }


    }
    private ListView m_ListViewResultList;
    private String tckn;
    private String fileNo;
    private ArrayList<String> list;

    private void init(String[] items) {
        m_ListViewResultList = (ListView) this.findViewById(R.id.RESULTDETAILEDLISTACTIVITY_LISTVIEW_RESULTLIST);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.headerlayout, m_ListViewResultList, false);
        m_ListViewResultList.addHeaderView(headerView);
        ResultListAdapter adapter = new ResultListAdapter(this, R.layout.rowlayout, R.id.ROWLAYOUT_TEXTVIEW_DATE, items);
        m_ListViewResultList.setAdapter(adapter);

    }
    private void loadData() {
        try {
            Intent intent = this.getIntent();
            list = intent.getStringArrayListExtra("result");
            tckn = intent.getStringExtra("tckn");
            fileNo = intent.getStringExtra("fileno");

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail_list);
        setTitle("Sonuç Listesi");
        loadData();
        String[] mStringArray = new String[list.size()];
        mStringArray = list.toArray(mStringArray);
        final String [] fArray=mStringArray;
        init(mStringArray);
        m_ListViewResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String url=fArray[position-1].split("__")[2];
                ParamsInfo pi = new ParamsInfo(tckn, fileNo, url);
               new LabResultDetailTask(ResultDetailListActivity.this).execute(pi);
            }
        });
    }

    private class LabResultDetailTask extends AsyncTask<ParamsInfo, Void, String> {


        private LoginActivity activity;
        ProgressDialog pd;

        public LabResultDetailTask(ResultDetailListActivity activity) {
            pd = new ProgressDialog(activity);
        }



        protected void onPreExecute() {
            pd.setMessage("Yükleniyor");
            pd.setTitle("Sonuçlar Alınıyor");
            pd.show();
        }

        protected  String doInBackground(ParamsInfo... params) {
            List<String> returnList = new ArrayList<String>();
            try {
                String tckinlik = params[0].gettckn();
                String dosyaNo = params[0].getfileNo();
                String url=params[0].geturl();
                Connection.Response res = Jsoup.connect("http://lab.gophastanesi.com.tr:8080/")
                        .data("tckimlik", tckinlik, "dosyano", dosyaNo)
                        .method(Connection.Method.POST)
                        .execute();

                Document doc = res.parse();

                String sessionId = res.cookie("PHPSESSID");

                doc = Jsoup.connect("http://lab.gophastanesi.com.tr:8080/"+url)
                        .cookie("PHPSESSID", sessionId)
                        .get();

                return doc.body().toString();
            } catch (IOException t) {

                return null;
            }
        }



        protected void onPostExecute(String result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }

            Intent intent = new Intent(getApplicationContext(), ShowResultActivity.class);
            result=result.replace("<center><img src=\"images/gop_logo.jpg\"></center>","");
            result=result.replace("<td width=\"15\" align=\"center\"><a href=\"http://www.bizmed.biz\"><img src=\"../favicon.ico\" width=\"16\" height=\"16\" border=\"0\" title=\"bizMED Hastane Otomasyon Programy - Sinerji Bili?im\"></a></td>","");
            result=result.replace("src=\"images/up.gif\"","");
            result=result.replace("src=\"images/down.gif\"","");
            intent.putExtra("result", result);
            intent.putExtra("tckn",tckn);
            intent.putExtra("fileno",fileNo);
            startActivityForResult(intent, 0);
        }
    }
}
