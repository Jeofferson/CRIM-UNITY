package com.jeofferson.onclas.PackageActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jeofferson.onclas.R;

public class ChatBot extends AppCompatActivity {

    WebView webView;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://yumh.net/dormfinder/hakathon/index.html");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        toolbar = findViewById(R.id.toolbar);
        setUpToolbar();

    }


    public void setUpToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chat Bot");

    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();

        }else {
            super.onBackPressed();
            finish();
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

        }

        return true;

    }
}