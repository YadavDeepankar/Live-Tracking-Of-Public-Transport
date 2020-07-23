package com.example.LTPT.User;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.LTPT.R;

import androidx.appcompat.app.AppCompatActivity;

public class FeedBack extends AppCompatActivity{

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_feedback);
            WebView wv=findViewById(R.id.webb);
            wv.setWebViewClient(new WebViewClient());
            wv.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSfeKzjBgjGpmpp0FOk9htIE7dVCO2RdcumI9Wb6CdCatqn34A/viewform");
            wv.getSettings().setJavaScriptEnabled(true);
        }
}