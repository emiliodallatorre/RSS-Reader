package com.LaVocedelBrunoFranchetti.rssreader;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

/**
 * Created by Emilio Dalla Torre on 22/03/2017.
 */

public class view extends Activity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        webView = (WebView) findViewById(R.id.view);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        String link = getIntent().getStringExtra("link");
        webView.loadUrl(link);
    }

}