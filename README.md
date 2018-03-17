# WebViewLib
WebView cross platform library 


Pass image to JavaScript
```
                String imageBase64 = getStringImageBase64(bitmap);
                String imgTag = "<img src='data:image/png;base64,"
                        + imageBase64
                        + "' align='center'/>";
                mWebView.loadData(imgTag, "text/html", "utf-8");
```


Compass Azimuth update callback
```
    @Override
    public void onAzimuthChange(float currectAzimuth) {
        AppUtils.callJavaScript(mWebView, COMPASS_ANGLE_FNC, currectAzimuth);
    }
```
