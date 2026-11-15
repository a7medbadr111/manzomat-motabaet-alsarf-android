package com.digitalsmarttree.fertilizercontrol;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
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
        " var oldLabel = 'مركز متابعة الأسمدة';" +
        " function makeBrand() {" +
        "   var walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null);" +
        "   var nodes = []; while (walker.nextNode()) nodes.push(walker.currentNode);" +
        "   nodes.forEach(function(node) {" +
        "     if ((node.nodeValue || '').indexOf(oldLabel) === -1 || !node.parentElement) return;" +
        "     var host = node.parentElement;" +
        "     var wrap = document.createElement('span');" +
        "     wrap.setAttribute('data-dst-sarf-brand', '1');" +
        "     wrap.style.cssText = 'display:inline-flex!important;align-items:center!important;justify-content:center!important;gap:10px!important;direction:ltr!important;white-space:nowrap!important;background:#fff!important;';" +
        "     var img = document.createElement('img'); img.src = '__DST_LOGO_DATA__'; img.alt = 'DIGITAL SMART TREE';" +
        "     img.style.cssText = 'display:block!important;width:118px!important;height:62px!important;object-fit:contain!important;background:#fff!important;';" +
        "     var word = document.createElement('span'); word.textContent = 'صرف';" +
        "     word.style.cssText = 'font-weight:700!important;font-size:24px!important;color:#238fa5!important;line-height:1!important;';" +
        "     wrap.appendChild(img); wrap.appendChild(word);" +
        "     var before = node.nodeValue.slice(0, node.nodeValue.indexOf(oldLabel));" +
        "     var after = node.nodeValue.slice(node.nodeValue.indexOf(oldLabel) + oldLabel.length);" +
        "     var frag = document.createDocumentFragment();" +
        "     if (before.trim()) frag.appendChild(document.createTextNode(before));" +
        "     frag.appendChild(wrap);" +
        "     if (after.trim()) frag.appendChild(document.createTextNode(after));" +
        "     host.replaceChild(frag, node);" +
        "   });" +
        "   document.querySelectorAll('[data-dst-sarf-brand]').forEach(function(brand) {" +
        "     var row = brand.parentElement;" +
        "     for (var level=0; level<4 && row; level++, row=row.parentElement) {" +
        "       if (!row || !row.children) continue;" +
        "       var iconSiblings = Array.from(row.children).filter(function(el) {" +
        "         return !el.contains(brand) && (el.querySelector('svg, img, [role=img]') || el.matches('svg,img,[role=img]'));" +
        "       });" +
        "       if (iconSiblings.length) {" +
        "         iconSiblings.forEach(function(el) { el.remove(); });" +
        "         row.style.setProperty('display','flex','important');" +
        "         row.style.setProperty('align-items','center','important');" +
        "         row.style.setProperty('justify-content','center','important');" +
        "         row.style.setProperty('gap','10px','important');" +
        "         row.style.setProperty('background','#fff','important');" +
        "         break;" +
        "       }" +
        "     }" +
        "   });" +
        " }" +
        " makeBrand();" +
        " if (!window.__dstSarfObserver) {" +
        "   window.__dstSarfObserver = new MutationObserver(function() { makeBrand(); });" +
        "   window.__dstSarfObserver.observe(document.documentElement,{childList:true,subtree:true,characterData:true});" +
        " }" +
        " setTimeout(makeBrand,300); setTimeout(makeBrand,900); setTimeout(makeBrand,1800);" +
        "})();";

    private String getBrandingScript() {
        Drawable drawable = getDrawable(R.drawable.dst_logo);
        int width = 720, height = 380;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0xFFFFFFFF);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        bitmap.recycle();
        String encoded = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP);
        return BRANDING_SCRIPT.replace("__DST_LOGO_DATA__", "data:image/png;base64," + encoded);
    }

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
                view.evaluateJavascript(getBrandingScript(), null);
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
            webView.post(() -> webView.evaluateJavascript(getBrandingScript(), null));
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