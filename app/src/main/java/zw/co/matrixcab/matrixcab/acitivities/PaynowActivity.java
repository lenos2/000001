package zw.co.matrixcab.matrixcab.acitivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.Server.Server;
import zw.co.matrixcab.matrixcab.session.SessionManager;

public class PaynowActivity extends AppCompatActivity {

    static final String RIDE_ID = "ride_id";
    static final String AMOUNT = "amount";
    static final String EMAIL = "email";

    WebView paynow;
    Bundle values;
    String paynowURL = "http://www.matrixcab.co.zw/cabadmin/payment/Paynow_Controller/make_payment";
    RequestParams params = new RequestParams();
    String ride_id;
    String amount;
    String htm = "https://www.paynow.co.zw/Payment/ConfirmPayment/494357/leomoyo@hotmail.com/-/";
    String email;
    //SessionManager sessionManager;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paynow);

        paynow = (WebView) findViewById(R.id.wvpaynow);

        if (getIntent().getExtras() != null){
            values = getIntent().getExtras();
            ride_id = values.getString(RIDE_ID);
            amount = values.getString(AMOUNT);
            email = values.getString(EMAIL);
            key = values.getString("key");
        }



        params.put(RIDE_ID,ride_id);
        params.put(AMOUNT,amount);
        params.put(EMAIL,"leomoyo@hotmail.com");

        Server.setContetntType();
        Server.setHeader(key);

        WebSettings webSettings = paynow.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //paynow.me
        //paynow.setWebChromeClient(new WebChromeClient());
        paynow.setWebViewClient(new WebViewClient());
        paynow.clearCache(true);
        //paynow.loadUrl(htm);
        Server.post("payment/Paynow_Controller/make_payment", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(PaynowActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //paynow.loadData(responseString,"text/html", "UTF-8");
                paynow.loadUrl(responseString);
                //Toast.makeText(PaynowActivity.this,responseString, Toast.LENGTH_LONG).show();

            }
        });




        /**
         * Setup the post values for the page
         */
        //paynow.postUrl(paynowURL,params.toString().getBytes());



    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void make_payment(){

    }
}
