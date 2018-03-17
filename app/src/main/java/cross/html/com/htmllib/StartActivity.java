package cross.html.com.htmllib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;

import static cross.html.com.htmllib.AppUtils.COMPASS_ANGLE_FNC;

public class StartActivity extends AppCompatActivity implements Compass.CompaseListner {

    private static final String TAG = StartActivity.class.getSimpleName();
    private WebView mWebView;
    private Compass compass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mWebView = findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.loadUrl(getResources().getString(R.string.compass_url));

        mWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                AppUtils.callJavaScript(mWebView, COMPASS_ANGLE_FNC, 100);
            }
        });

        compass = new Compass(this, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }


    void addToBasket() {
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        integrator.setCameraId(0);
        integrator.setPrompt("");
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCaptureActivity(PortrateCaptureActivity.class);
        integrator.initiateScan();
        integrator.setOrientationLocked(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && requestCode == IntentIntegrator.REQUEST_CODE) {
            if (result.getContents() == null) {
                Log.d(TAG, "Cancelled scan");
                // Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {

                Bitmap bitmap = generateQRCode(result.getContents());

                ImageView img = findViewById(R.id.img);
                img.setImageBitmap(bitmap);


                Log.d(TAG, "Scanned : " + result.getContents());

                String content = result.getContents();
                String path = result.getBarcodeImagePath();
                Log.d(TAG, content + " : " + path);

                String imageBase64 = getStringImageBase64(bitmap);
                String imgTag = "<img src='data:image/png;base64,"
                        + imageBase64
                        + "' align='center'/>";
                mWebView.loadData(imgTag, "text/html", "utf-8");

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public Bitmap generateQRCode(String data){
        Bitmap bitmap = null;

        com.google.zxing.Writer wr = new MultiFormatWriter();
        try {
            int width = 350;
            int height = 350;
            BitMatrix bm = wr.encode(data, BarcodeFormat.QR_CODE, width, height);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {

                    bitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private String getStringImageBase64(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return imgageBase64;

    }

    public void onScanClick(View view) {
        addToBasket();
    }

    @Override
    public void onAzimuthChange(float currectAzimuth) {
        AppUtils.callJavaScript(mWebView, COMPASS_ANGLE_FNC, currectAzimuth);
    }
}
