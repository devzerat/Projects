package com.dev.zerat.goplabresult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ShowResultActivity extends Activity {
    private WebView m_webViewResult;

    private void init() {
        m_webViewResult = (WebView) this.findViewById(R.id.SHOWRESULTACTIVIY_WEBVIEW_RESULTVIEW);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        setTitle("Sonuçlar");
        init();
        Intent intent = this.getIntent();
        String html = intent.getStringExtra("result");


        WebSettings settings = m_webViewResult.getSettings();
        settings.setMinimumFontSize(18);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        m_webViewResult.setWebChromeClient(new WebChromeClient());

        m_webViewResult.loadDataWithBaseURL(null, html,
                "text/html", "UTF-8", null);
    }


}
