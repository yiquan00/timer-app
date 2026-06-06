package com.timerapp;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private TimerWebInterface timerBridge;
    private DevicePolicyManager dpm;
    private ComponentName adminName;
    private View adminBanner;
    private Button adminBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏模式
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }

        setContentView(R.layout.activity_main);

        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        adminName = new ComponentName(this, AdminReceiver.class);

        adminBanner = findViewById(R.id.adminBanner);
        adminBtn = findViewById(R.id.adminBtn);

        // 创建并配置 WebView
        webView = findViewById(R.id.timerWebView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setMediaPlaybackRequiresUserGesture(false);

        // 设置 WebView 客户端
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        // 创建 JavaScript 桥接
        timerBridge = new TimerWebInterface(this);
        webView.addJavascriptInterface(timerBridge, "TimerBridge");

        // 加载计时器页面
        webView.loadUrl("file:///android_asset/public/index.html");

        // 设备管理员按钮
        adminBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "启用后，计时器可以在倒计时结束时自动锁屏");
            startActivity(intent);
        });

        // 更新管理员状态
        updateAdminStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdminStatus();
    }

    private void updateAdminStatus() {
        boolean active = dpm.isAdminActive(adminName);
        adminBanner.setVisibility(active ? View.GONE : View.VISIBLE);
        adminBtn.setText(active ? "已激活" : "激活设备管理员");
    }
}
