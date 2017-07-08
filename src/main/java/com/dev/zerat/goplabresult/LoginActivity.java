package com.dev.zerat.goplabresult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static java.lang.System.in;


public class LoginActivity extends Activity {

    private EditText m_editTextTCKN;
    private EditText m_editTextFileNo;
    private CircularProgressButton m_Buttonlogin;
    List<String> resultList = new ArrayList<>();

    private void init() {
        m_editTextTCKN = (EditText) this.findViewById(R.id.LOGINACTIVITY_EDITTEXT_TCKN);
        m_editTextFileNo = (EditText) this.findViewById(R.id.LOGINACTIVITY_EDITTEXT_FILENO);
        m_Buttonlogin = (CircularProgressButton) this.findViewById(R.id.LOGINACTIVITY_BUTTON_LOGIN);
        m_Buttonlogin.setIndeterminateProgressMode(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.init();
        m_Buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(m_Buttonlogin.getProgress()==0)
                {
                    m_Buttonlogin.setProgress(30);
                }
                else if (m_Buttonlogin.getProgress() == -1) {
                    m_Buttonlogin.setProgress(0);
                }
                String result = "";
                LoginResultDefinion.ResultString resultString = new LoginResultDefinion.ResultString();

                if (!isNetworkAvailable()) {
                    result = resultString.NoInternetConnection;
                }
                String tckn = m_editTextTCKN.getText().toString();
                String fileNo = m_editTextFileNo.getText().toString();

                if (tckn.isEmpty() || fileNo.isEmpty()) {
                    result = resultString.EmptyIdentityOrFileNo;
                }
                if (tckn.length() != 11) {
                    result = resultString.InvalindIdentityLenght;
                }
                ParamsInfo paramsInfo = new ParamsInfo(tckn, fileNo, null);
                paramsInfo.setresultDescription(result);
                new LabResultTask().execute(paramsInfo, null);

            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private class LabResultTask extends AsyncTask<ParamsInfo, Void, List<String>> {

       protected void onPreExecute() {
        }
        protected List<String> doInBackground(ParamsInfo... params) {
            List<String> returnList = new ArrayList<String>();
            try {
                if (!params[0].getresultDescription().isEmpty()) {
                    returnList.add(params[0].getresultDescription());
                    return returnList;
                }
                String tckinlik = params[0].gettckn();
                String dosyaNo = params[0].getfileNo();
                Connection.Response res = Jsoup.connect("http://lab.gophastanesi.com.tr:8080/")
                        .data("tckimlik", tckinlik, "dosyano", dosyaNo)
                        .method(Connection.Method.POST)
                        .execute();

                Document doc = res.parse();
                if (doc.body().toString().contains("Yanlis bilgi girdiniz.")) {
                    LoginResultDefinion.ResultString definition = new LoginResultDefinion.ResultString();
                    returnList.add(definition.WrongIdentityOrFileNo);
                    return returnList;
                }

                String sessionId = res.cookie("PHPSESSID");

                doc = Jsoup.connect("http://lab.gophastanesi.com.tr:8080/?tckimlik=" + tckinlik + "&dosyano=" + dosyaNo)
                        .cookie("PHPSESSID", sessionId)
                        .get();
                Element element = doc.select("table").get(3);
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
            if (result.size() == 1) {
                m_Buttonlogin.setErrorText(result.get(0));
                m_Buttonlogin.setProgress(-1);
                return;
            }
            Intent intent = new Intent(getApplicationContext(), ResultListActivity.class);
            ArrayList<String> finalList = new ArrayList<>();
            finalList.addAll(result);
            intent.putStringArrayListExtra("result", finalList);
            intent.putExtra("tckn", m_editTextTCKN.getText().toString());
            intent.putExtra("fileno", m_editTextFileNo.getText().toString());
            startActivityForResult(intent, 0);
        }
    }


}
