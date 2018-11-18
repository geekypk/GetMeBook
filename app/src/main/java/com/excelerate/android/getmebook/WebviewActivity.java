package com.excelerate.android.getmebook;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebviewActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        final ProgressBar pbar = findViewById(R.id.pB1);
        WebView webView = findViewById(R.id.webView1);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            webView.getSettings().setAllowFileAccess(true);
//        }
        pbar.setMax(100);
        webView.setWebViewClient(new AppWebViewClients(pbar,webView));
        webView.loadUrl(BookActivity.bookUri.toString());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
    }
}