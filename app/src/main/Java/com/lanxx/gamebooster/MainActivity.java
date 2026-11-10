package com.lanxx.gamebooster;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView;
    private static final int OVERLAY_REQUEST = 901;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        webView.setBackgroundColor(0xFF050505);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new Bridge(), "Lanxx");
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private class Bridge {
        @JavascriptInterface
        public void launchGame(String game) {
            runOnUiThread(() -> {
                String pkg = game.equals("max")
                    ? "com.dts.freefiremax"
                    : "com.dts.freefireth";
                Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
                if (launch != null) {
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launch);
                } else {
                    Toast.makeText(MainActivity.this,
                        "Game belum terpasang atau package tidak ditemukan.",
                        Toast.LENGTH_LONG).show();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + pkg)));
                    } catch (Exception ignored) {}
                }
            });
        }

        @JavascriptInterface
        public void enableOverlay() {
            runOnUiThread(() -> {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                    startActivityForResult(i, OVERLAY_REQUEST);
                } else {
                    startOverlay();
                }
            });
        }

        @JavascriptInterface
        public void stopOverlay() {
            stopService(new Intent(MainActivity.this, OverlayService.class));
        }

        @JavascriptInterface
        public void configureCrosshair(String color, int size) {
            OverlayService.setCrosshairStyle(color, size);
        }

        @JavascriptInterface
        public void openAndroidSettings() {
            runOnUiThread(() -> startActivity(new Intent(Settings.ACTION_SETTINGS)));
        }
    }

    private void startOverlay() {
        if (Settings.canDrawOverlays(this)) {
            Intent service = new Intent(this, OverlayService.class);
            startService(service);
            Toast.makeText(this, "Floating window aktif", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_REQUEST) {
            if (Settings.canDrawOverlays(this)) startOverlay();
            else Toast.makeText(this, "Izin overlay belum diberikan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
          }
                      
