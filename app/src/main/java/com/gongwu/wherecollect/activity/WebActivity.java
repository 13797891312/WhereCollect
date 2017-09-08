package com.gongwu.wherecollect.activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gongwu.wherecollect.R;
import com.zhaojin.myviews.Loading;
/**
 * Function: 加载网页
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class WebActivity extends BaseViewActivity {
    private Loading loading;
    private WebView webView;
    private String url;

    public static void start(Context context, String title, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        String title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        titleLayout.setTitle(title);
        titleLayout.setBack(true, null);
        webView = (WebView) findViewById(R.id.webView1);
        loading = Loading.show(loading, this, "正在加载...");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                if (loading != null) {
                    loading.dismiss();
                }
            }
        });
        webView.loadUrl(url);
    }

    private void back() {
        try {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
