package org.odk.collect.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.odk.collect.android.R;


public class HomeScreenActivity extends CollectAbstractActivity {

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

    }


    public void SurveysIn(View view) {
        startActivity(new Intent(this, MainMenuActivity.class));

    }

    public void ScanQR (View view){
        startActivity(new Intent(this, ScannerWithFlashlightActivity.class));
    }

    public void Tutorial (View view){
        startActivity(new Intent(this, TutorialActivity.class));
    }

    public void htua(View view) {
        setContentView(R.layout.activity_web_view);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://hedera.online/howto-covid19.html");
    }
}


