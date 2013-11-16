package com.kdclient.ui;

import com.kdclient.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class LicenseActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.license_activity);
        WebView webView = (WebView) findViewById(R.id.content);
        webView.loadUrl("file:///android_asset/licence.txt");
    }
}

