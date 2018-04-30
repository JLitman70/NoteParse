package com.example.john.noteparse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Web extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView web = (WebView) findViewById(R.id.webView);
        Intent intent =getIntent();
        String page = intent.getStringExtra("HTML");
        web.loadData(page, "text/html",null);

    }
}
