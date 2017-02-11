package org.petabytes.awesomeblogs.web;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.Coordinator;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action0;

class WebViewCoordinator extends Coordinator {

    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.web) WebView webView;

    private final String url;
    private final Action0 onCloseAction;

    WebViewCoordinator(@NonNull String url, @NonNull Action0 onCloseAction) {
        this.url = url;
        this.onCloseAction = onCloseAction;
    }

    @Override
    public void attach(View view) {
        super.attach(view);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Views.setGone(loadingView);
                Views.setVisible(webView);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
        webView.loadUrl(url);
    }

    @OnClick(R.id.close)
    void onCloseClicked() {
        onCloseAction.call();
    }

    boolean historyBackIfNeeded() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}
