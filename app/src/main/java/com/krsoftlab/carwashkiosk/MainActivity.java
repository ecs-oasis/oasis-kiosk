package com.krsoftlab.carwashkiosk;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.krsoftlab.carwashkiosk.device.CameraManager;
import com.krsoftlab.carwashkiosk.device.WashMachineManager;
import com.krsoftlab.carwashkiosk.payment.Sk288PaymentUtil;
import com.krsoftlab.carwashkiosk.serial.SerialPortInterface;
import com.krsoftlab.carwashkiosk.serial.SerialPortManager;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    KrJavascriptInterface javascriptInterface;
    WebView webView;

    CameraManager cameraManager;
    WashMachineManager washMachineManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreenOptions(this);
        setContentView(R.layout.activity_main);
        Sk288PaymentUtil.Companion.init(this);
        startWatchdogService();
        washMachineManager = new WashMachineManager(this);
        initWebView();
        cameraManager = new CameraManager(this, webView);
        initTestButton();
    }


    private void initWebView(){
        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initWebViewSetting(webView);
        FrameLayout flWebView = findViewById(R.id.flWebView);
        flWebView.addView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void initTestButton(){
//        findViewById(R.id.btn_car_num).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String carNum = "21고4321";
//                webView.evaluateJavascript("onCarNumDetected('"+carNum+"');", null);
//            }
//        });
//        findViewById(R.id.btn_card).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                {
////                    "cardNo":"5433330598105804", //카드번호
////                        "idntNo":"870413", //생년월일
////                        "vldDtMon":"03", //유효기간 달
////                        "vldDtYear":"25", //유효기간 년
////                        "cardPwd":"05", //비번
////                }
//                try {
//                    JSONObject joCard = new JSONObject();
//                    joCard.put("cardNo","5433330598105804");
//                    joCard.put("vldDtMon","03");
//                    joCard.put("vldDtYear","25");
//                    webView.evaluateJavascript("onCardInserted("+joCard.toString()+");", null);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @SuppressLint("JavascriptInterface")
    private void initWebViewSetting(WebView webView) {
//        WebView.setWebContentsDebuggingEnabled(true);
        javascriptInterface = new KrJavascriptInterface(this, webView, washMachineManager);
        webView.addJavascriptInterface(javascriptInterface, "AndroidClient");
        WebSettings websettings = webView.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setLoadWithOverviewMode(true);
        websettings.setUseWideViewPort(true);
        websettings.setMediaPlaybackRequiresUserGesture(false);
        websettings.setAllowFileAccess(true);
        websettings.setAllowFileAccessFromFileURLs(true);
        websettings.setAllowUniversalAccessFromFileURLs(true);
        websettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        websettings.setAppCacheEnabled(true);
        websettings.setDomStorageEnabled(true);
        websettings.setSupportZoom(false);
        websettings.setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            websettings.setSafeBrowsingEnabled(false);
        }
        websettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebChromeClient(new MyWebChromeClient(webView));
        webView.setWebViewClient(new WebViewClient());
    }

    final class MyWebChromeClient extends WebChromeClient {
        WebView wv;

        public MyWebChromeClient(WebView w) {
            this.wv = w;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            return super.onConsoleMessage(cm);
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.confirm();
            return true;
        }

        @Override
        public void onProgressChanged(final WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }


        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    request.grant(request.getResources());
                }
            });
        }
    }

    public static void setFullscreenOptions(Activity activity){
        /* Full-Screen */
        View mDecorView = activity.getWindow().getDecorView();
        int mUIOptions = mDecorView.getSystemUiVisibility();
        mUIOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        mUIOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        mUIOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        mUIOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        mUIOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        mUIOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        mUIOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        mDecorView.setSystemUiVisibility(mUIOptions);
    }

    private void startWatchdogService() {
        Intent intent = new Intent(this, WatchdogService.class);
        startService(intent);
    }

    private void finishApp() {
        Intent intent = new Intent(this, WatchdogService.class);
        intent.setAction("STOP");
        startService(intent);
        finishAffinity();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onBackPressed() {
        finishApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullscreenOptions(this);
    }

}