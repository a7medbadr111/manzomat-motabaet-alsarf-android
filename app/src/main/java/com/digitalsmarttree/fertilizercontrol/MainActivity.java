package com.digitalsmarttree.fertilizercontrol;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER_REQUEST = 1001;
    private static final String HOME_URL = "https://fert-control-tested-2026.vercel.app/";
    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;

    // Cosmetic changes are applied only inside this Android WebView.
    // The Vercel website source and deployment are not modified.
    private static final String BRANDING_SCRIPT =
        "(function() {" +
        " if (window.__dstSarfBrandingInstalled) return;" +
        " window.__dstSarfBrandingInstalled = true;" +
        " var oldLabel = 'مركز متابعة الأسمدة';" +
        " function replaceBranding() {" +
        "   var walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null);" +
        "   var nodes = []; while (walker.nextNode()) nodes.push(walker.currentNode);" +
        "   nodes.forEach(function(node) {" +
        "     if ((node.nodeValue || '').indexOf(oldLabel) === -1) return;" +
        "     var host = node.parentElement; if (!host) return;" +
        "     var wrap = document.createElement('span');" +
        "     wrap.setAttribute('data-dst-sarf-brand', '1');" +
        "     wrap.style.cssText = 'display:inline-flex;align-items:center;justify-content:center;gap:8px;direction:ltr;vertical-align:middle;white-space:nowrap;background:#fff;';" +
        "     var img = document.createElement('img');" +
        "     img.src = 'file:///android_res/drawable/dst_logo';" +
        "     img.alt = 'DIGITAL SMART TREE';" +
        "     img.style.cssText = 'display:block;width:108px;height:48px;object-fit:contain;background:#fff;';" +
        "     var word = document.createElement('span'); word.textContent = 'صرف';" +
        "     word.style.cssText = 'font-weight:700;font-size:1.15em;color:#238fa5;line-height:1;';" +
        "     wrap.appendChild(img); wrap.appendChild(word);" +
        "     var before = node.nodeValue.slice(0, node.nodeValue.indexOf(oldLabel));" +
        "     var after = node.nodeValue.slice(node.nodeValue.indexOf(oldLabel) + oldLabel.length);" +
        "     var frag = document.createDocumentFragment();" +
        "     if (before) frag.appendChild(document.createTextNode(before));" +
        "     frag.appendChild(wrap);" +
        "     if (after) frag.appendChild(document.createTextNode(after));" +
        "     host.replaceChild(frag, node);" +
        "   });" +
        " }" +
        " replaceBranding();" +
        " var observer = new MutationObserver(function() { replaceBranding(); });" +
        " observer.observe(document.documentElement, {childList:true,subtree:true,characterData:true});" +
        " setTimeout(replaceBranding, 500); setTimeout(replaceBranding, 1500);" +
        "})();";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.evaluateJavascript(BRANDING_SCRIPT, null);
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(Uri.parse(url));
            }
            private boolean handleUrl(Uri uri) {
                String host = uri.getHost();
                if ("fert-control-tested-2026.vercel.app".equalsIgnoreCase(host)) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                catch (ActivityNotFoundException ignored) { }
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (filePathCallback != null) filePathCallback.onReceiveValue(null);
                filePathCallback = callback;
                Intent intent = params.createIntent();
                try { startActivityForResult(intent, FILE_CHOOSER_REQUEST); }
                catch (ActivityNotFoundException e) { filePathCallback = null; return false; }
                return true;
            }
        });

        if (savedInstanceState == null) webView.loadUrl(HOME_URL);
        else {
            webView.restoreState(savedInstanceState);
            webView.post(() -> webView.evaluateJavascript(BRANDING_SCRIPT, null));
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST && filePathCallback != null) {
            Uri[] results = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) webView.saveState(outState);
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (webView != null) { webView.destroy(); webView = null; }
        super.onDestroy();
    }
}