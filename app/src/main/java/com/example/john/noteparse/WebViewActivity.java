package com.example.john.noteparse;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by mikos on 4/29/2018.
 */

public class WebViewActivity extends Activity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        webView = (WebView) findViewById(R.id.webViewId);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl(urlString); // needs to be real variable, placeholder for now
    }
}
