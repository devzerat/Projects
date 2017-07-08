package com.dev.zerat.goplabresult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
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

public class ResultListActivity extends Activity {

    private ListView m_ListViewResultList;
    private String tckn;
    private String fileNo;
    private ArrayList<String> list;

    private void init(String[] items) {
        m_ListViewResultList = (ListView) this.findViewById(R.id.RESULTLISTACTIVITY_LISTVIEW_RESULTLIST);
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
        setContentView(R.layout.activity_result_list);
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
                new LabResultDetailTask(ResultListActivity.this).execute(pi);
            }
        });
    }



    private class LabResultDetailTask extends AsyncTask<ParamsInfo, Void, List<String>> {

        List<String> str;
        private LoginActivity activity;
        ProgressDialog pd;

        public LabResultDetailTask(ResultListActivity activity) {
            pd = new ProgressDialog(activity);
        }



        protected void onPreExecute() {
            pd.setMessage("Yükleniyor");
            pd.setTitle("Sonuçlar Alınıyor");
            pd.show();
        }

        protected List<String> doInBackground(ParamsInfo... params) {
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
                if (doc.body().toString().contains("Yanlis bilgi girdiniz."))
                    return null;

                String sessionId = res.cookie("PHPSESSID");

                doc = Jsoup.connect("http://lab.gophastanesi.com.tr:8080/"+url)
                        .cookie("PHPSESSID", sessionId)
                        .get();
                Element element = doc.select("table").get(3);
               /* for ( Element element : elements) {
                    // Here your room is available
                    element.select("a");
                }*/
                Elements trElements = element.select("tr");
                int index = 0;
                for (Element trElement : trElements) {
                    Elements tdElements = trElement.select("td");
                    index = 0;
                    String date = "";
                    String department = "";
                    String href = "";
                    for (Element tdElement : tdElements) {
                        index++;
                        if (index == 2) {
                            date = tdElement.text();
                        }
                        if (index == 3) {
                            department = tdElement.text();
                            href = tdElement.select("a").attr("href");
                        }
                    }
                    if (!href.equals(""))
                        returnList.add(date + "__" + department + "__" + href);
                }
                return returnList;
            } catch (IOException t) {

                return null;
            }
        }



        protected void onPostExecute(List<String> result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (result == null) {
                Toast.makeText(ResultListActivity.this, "Yanlis bilgi girdiniz. T.C. Kimlik No ve Dosya No'yu kontrol edip yeniden deneyiniz.", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(getApplicationContext(), ResultDetailListActivity.class);
            ArrayList<String> finalList = new ArrayList<>();
            finalList.addAll(result);
            intent.putStringArrayListExtra("result", finalList);
            intent.putExtra("tckn",tckn);
            intent.putExtra("fileno",fileNo);
            startActivityForResult(intent, 0);
        }
    }
}
