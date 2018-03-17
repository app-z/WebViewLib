package cross.html.com.htmllib;

import android.util.Log;
import android.webkit.WebView;

/**
 * Created by appz on 3/18/18.
 */

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    public static String COMPASS_ANGLE_FNC = "compass_angle";

    static public void callJavaScript(WebView webView, String methodName, Object... params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:try{");
        stringBuilder.append(methodName);
        stringBuilder.append("(");
        String separator = "";
        for (Object param : params) {
            stringBuilder.append(separator);
            separator = ",";
            if (param instanceof String) {
                stringBuilder.append("'");
            }
            stringBuilder.append(param);
            if (param instanceof String) {
                stringBuilder.append("'");
            }

        }
        stringBuilder.append(")}catch(error){console.error(error.message);}");
        final String call = stringBuilder.toString();
        Log.i(TAG, "callJavaScript: call=" + call);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(call, null);
        } else {
            webView.loadUrl(call);
        }

        Log.d(TAG, params.toString());
    }

}
