package zw.co.matrixcab.matrixcab.fragement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.Server.Server;
import zw.co.matrixcab.matrixcab.acitivities.HomeActivity;
import zw.co.matrixcab.matrixcab.acitivities.PaynowActivity;
import zw.co.matrixcab.matrixcab.custom.CheckConnection;
import zw.co.matrixcab.matrixcab.custom.SetCustomFont;
import zw.co.matrixcab.matrixcab.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by android on 14/3/17.
 */

public class AcceptedDetailFragment extends Fragment {
    private View view;
    AppCompatButton trackRide;
    private String pickup = "";
    private String drop = "";
    private String driver = "";
    private String basefare;
    private String mobile = "";
    private TextView capacity;
    private String p_location = "";
    String driver_id = "";
    private String d_location = "";
    AppCompatButton btn_cancel, btn_payment;
    private String paymnt_status = "";
    double platitude, plonngitude, dlatitude, dlonngitude;

    TextView title, drivername, mobilenumber, pickup_location, drop_location, fare, payment_status;
    SessionManager sessionManager;
    private String ride_id = "";
    private AlertDialog alert;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final String CONFIG_ENVIRONMENT = Server.ENVIRONMENT;
    private static PayPalConfiguration config;
    PayPalPayment thingToBuy;
    private String distance = "";
    /*private Double finalfare;*/
    private ProgressDialog progressDialog;
    private String payment_mode;
    LatLng latLngPickup, latLngDrop;
    String request = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.accepted_detail_fragmnet, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar("Passenger Information");
        BindView();
        configPaypal();

        trackRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (latLngDrop != null && latLngPickup != null) {
                    bundle.putParcelable("pickup", latLngPickup);
                    bundle.putParcelable("drop", latLngDrop);
                    MapView mapView = new MapView();
                    mapView.setArguments(bundle);
                    ((HomeActivity) getActivity()).changeFragment(mapView, "Track Ride");
                } else {
                    TastyToast.makeText(getActivity(), "invalid location", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                }
            }
        });

        return view;
    }

    public void BindView() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);

        title = (TextView) view.findViewById(R.id.title);
        drivername = (TextView) view.findViewById(R.id.driver_name);
        mobilenumber = (TextView) view.findViewById(R.id.txt_mobilenumber);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickuplocation);
        drop_location = (TextView) view.findViewById(R.id.txt_droplocation);
        fare = (TextView) view.findViewById(R.id.txt_basefare);
        trackRide = (AppCompatButton) view.findViewById(R.id.btn_trackride);
        btn_payment = (AppCompatButton) view.findViewById(R.id.btn_payment);
        btn_cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        payment_status = (TextView) view.findViewById(R.id.txt_paymentstatus);
        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        sessionManager = new SessionManager(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null)

        {
            title.setText("TAXI");
            pickup = bundle.getString("from_add");
            drop = bundle.getString("to_add");
            driver = bundle.getString("drivername");
            basefare = bundle.getString("fare");
            mobile = bundle.getString("mobile");
            ride_id = bundle.getString("ride_id");
            paymnt_status = bundle.getString("payment_status");
            driver_id = bundle.getString("driver_id");
            payment_mode = bundle.getString("payment_mode");
            try {
                String[] latlong = bundle.getString("pickup_location").split(",");
                platitude = Double.parseDouble(latlong[0]);
                plonngitude = Double.parseDouble(latlong[1]);
                latLngPickup = new LatLng(platitude, plonngitude);
                Log.e("intentpickup", platitude + "    " + plonngitude);
            } catch (Exception e) {
                Log.d("intentError", e.toString());
            }
            try {
                String[] latlong = bundle.getString("drop_location").split(",");
                dlatitude = Double.parseDouble(latlong[0]);
                dlonngitude = Double.parseDouble(latlong[1]);
                latLngDrop = new LatLng(dlatitude, dlonngitude);
                Log.e("intentdrop", dlatitude + "    " + dlonngitude);
            } catch (Exception e) {
                Log.d("intentError", e.toString());
            }
            Log.e("payment", paymnt_status + " " + ride_id);


            if (pickup != null) {
                pickup_location.setText(pickup);
            }
            if (drop != null) {
                drop_location.setText(drop);
            }
            if (driver != null) {
                drivername.setText(driver);
            }
            if (fare != null) {
                fare.setText(basefare + " " + sessionManager.getUnit());
            }
            if (mobile != null) {
                mobilenumber.setText(mobile);
            }

            if (mobile != null) {
                mobilenumber.setText(mobile);
            }
            if (payment_mode == null) {
                payment_mode = "";
            }
            if (paymnt_status != null && !paymnt_status.equals("") || payment_mode.equals("OFFLINE")) {
                if (payment_mode.equals("OFFLINE")) {
                    payment_status.setText(R.string.cash_on_hand);
                } else {
                    payment_status.setText(paymnt_status);
                }

                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.VISIBLE);
            } else {
                payment_status.setText("UNPAID");
                btn_payment.setVisibility(View.VISIBLE);
                trackRide.setVisibility(View.GONE);

            }
            if (ride_id != null) {

            } else {
                ride_id = "";
            }
            p_location = bundle.getString("pickup_location");
            d_location = bundle.getString("drop_location");

            request = bundle.getString("request");
            if (request != null && !request.equals("") && request.equals("pending")) {
                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.GONE);

            }
            if (request != null && !request.equals("") && request.equals("cancelled")) {
                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);

            }
            if (request != null && !request.equals("") && request.equals("completed")) {
                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);

            }
        }

        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view

        );

        btn_payment.setOnClickListener(new View.OnClickListener()

                                       {
                                           @Override
                                           public void onClick(View v) {
                                               if (CheckConnection.haveNetworkConnection(getActivity())) {

                                                   new AlertDialog.Builder(getActivity()).setTitle("Choose Payment method").setItems(R.array.payment_mode, new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {
                                                           if (which == 0) {
                                                               RequestParams params = new RequestParams();
                                                               params.put("ride_id", ride_id);
                                                               params.put("payment_mode", "OFFLINE");
                                                               Server.setContetntType();
                                                               Server.setHeader(sessionManager.getKEY());

                                                               Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
                                                                   @Override
                                                                   public void onStart() {
                                                                       if (!progressDialog.isShowing()) {
                                                                           progressDialog.show();
                                                                           progressDialog.setMessage("Updating payment....");
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                                       super.onSuccess(statusCode, headers, response);
                                                                       payment_mode = "OFFLINE";
                                                                       if (payment_mode.equals("OFFLINE")) {
                                                                           payment_status.setText(R.string.cash_on_hand);
                                                                       } else {
                                                                           payment_status.setText(paymnt_status);
                                                                       }

                                                                       btn_payment.setVisibility(View.GONE);
                                                                       trackRide.setVisibility(View.VISIBLE);
                                                                       TastyToast.makeText(getActivity(), "Payment Updated", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                                                                   }

                                                                   @Override
                                                                   public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                                       super.onFailure(statusCode, headers, responseString, throwable);
                                                                       Log.e("error", responseString);
                                                                   }

                                                                   @Override
                                                                   public void onFinish() {
                                                                       super.onFinish();
                                                                       progressDialog.dismiss();
                                                                   }
                                                               });

                                                           } if (which == 1){
                                                               //paynowPayment();

                                                               /*String url = "http://www.example.com";
                                                               Intent i = new Intent(Intent.ACTION_VIEW);
                                                               i.setData(Uri.parse(url));
                                                               startActivity(i);*/

                                                               Bundle values = new Bundle();
                                                               values.putString("ride_id",ride_id);
                                                               values.putString("amount",basefare);
                                                               values.putString("key",sessionManager.getKEY());
                                                               Intent intent = new Intent(view.getContext(), PaynowActivity.class);
                                                               intent.putExtras(values);
                                                               startActivity(intent);
                                                           }else {
                                                               MakePayment();
                                                           }
                                                       }
                                                   }).create().show();

                                                   //MakePayment();
                                               } else {
                                                   TastyToast.makeText(getActivity(), "network is not available", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                                               }
                                           }
                                       }

        );

        btn_cancel.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View v) {

                                              AlertDialogCreate("RIDE CANCELLATION", "Do you want to cancel your accepted ride?", "CANCELLED");
                                          }
                                      }

        );

    }

    public void paynowPayment(){
        /**
         * This is code to allow payments to be done via paynow*/

        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("payment_mode", "ONLINE");
        Server.setContetntType();
        Server.setHeader(sessionManager.getKEY());

        Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setMessage("Updating payment....");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                payment_mode = "ONLINE";
                if (payment_mode.equals("ONLINE")) {
                    payment_status.setText(R.string.cash_on_hand);
                } else {
                    payment_status.setText(paymnt_status);
                }

                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.VISIBLE);
                TastyToast.makeText(getActivity(), "Payment Updated", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("error", responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.dismiss();
            }
        });
        /**
         * Paynow code ends here*/
    }

    public void Updatepayemt(String ride_id, String payment_status) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("payment_status", payment_status);
        params.put("payment_mode", "PAYPAL");
        Server.setContetntType();
        Server.setHeader(sessionManager.getKEY());

        Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setMessage("Updating payment....");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("success: update payment", response.toString());
                progressDialog.dismiss();
                TastyToast.makeText(getActivity(), "Payment Updated", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();
                TastyToast.makeText(getActivity(), "error while updating  payment", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                Log.d("fail: update payment", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
                TastyToast.makeText(getActivity(), "server didn't response ", TastyToast.ERROR, TastyToast.LENGTH_LONG).show();

            }
        });

    }


    public void AlertDialogCreate(String title, String message, final String status) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new AlertDialog.Builder(getActivity())
                .setIcon(drawable)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sendStatus(ride_id, status);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void cancelAlert(String title, String message, final String status) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(title);

        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        alertDialog.setIcon(drawable);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendStatus(ride_id, status);

            }
        });


        alertDialog.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.cancel();
            }
        });
        alert = alertDialog.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }

    public void sendStatus(String ride_id, final String status) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(true);
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);
        SessionManager sessionManager = new SessionManager(getActivity());
        Server.setHeader(sessionManager.getKEY());
        Server.setContetntType();
        Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equals("success")) {
                        progressDialog.dismiss();
                        if (status.equalsIgnoreCase("COMPLETED")) {
                            TastyToast.makeText(getActivity(), "ride request completed", TastyToast.SUCCESS, TastyToast.LENGTH_LONG).show();

                        } else {
                            TastyToast.makeText(getActivity(), "ride request cancelled", TastyToast.SUCCESS, TastyToast.LENGTH_LONG).show();

                        }
                        ((HomeActivity) getActivity()).changeFragment(new AcceptedRequestFragment(), "Accepted Request");
                    } else {
                        progressDialog.dismiss();
                        String error = response.getString("data");
                        TastyToast.makeText(getActivity(), error, TastyToast.ERROR, TastyToast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    TastyToast.makeText(getActivity(), "error occurred", TastyToast.ERROR, TastyToast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
                TastyToast.makeText(getActivity(), "try again", TastyToast.ERROR, TastyToast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
                TastyToast.makeText(getActivity(), "error occurred", TastyToast.ERROR, TastyToast.LENGTH_LONG).show();

            }
        });

    }

    public void configPaypal() {
        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(Server.PAYPAL_KEY)
                .merchantName(getString(R.string.merchant_name))
                .merchantPrivacyPolicyUri(
                        Uri.parse(getString(R.string.merchant_privacy)))
                .merchantUserAgreementUri(
                        Uri.parse(getString(R.string.merchant_user_agreement)));
    }

    public void MakePayment() {

        if (!progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setMessage(getString(R.string.cal_fare));
            progressDialog.setCancelable(true);
        }

        if (basefare != null && !basefare.equals("")) {
            progressDialog.cancel();
            Intent intent = new Intent(getActivity(), PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            getActivity().startService(intent);
            thingToBuy = new PayPalPayment(new java.math.BigDecimal(String.valueOf(basefare)), getString(R.string.paypal_payment_currency), "Ride Payment", PayPalPayment.PAYMENT_INTENT_SALE);
            Intent payment = new Intent(getActivity(),
                    PaymentActivity.class);

            payment.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
            payment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            startActivityForResult(payment, REQUEST_CODE_PAYMENT);
            progressDialog.dismiss();


        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                //  String.valueOf(finalfare)
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));
                        Updatepayemt(ride_id, "PAID");

                    } catch (JSONException e) {
                        Log.d("payment", e.toString());
                        TastyToast.makeText(getActivity(), e.toString(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                TastyToast.makeText(getActivity(), "Payment has been cancelled", TastyToast.LENGTH_LONG, TastyToast.INFO).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                TastyToast.makeText(getActivity(), "Error Occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                Log.d("payment", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject()
                                .toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.d("FuturePaymentExample", authorization_code);

                        /*sendAuthorizationToServer(auth);
                        Toast.makeText(getActivity(),
                                "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();*/
                        Log.e("paypal", "future Payment code received from PayPal  :" + authorization_code);

                    } catch (JSONException e) {
                        TastyToast.makeText(getActivity(), "failure Occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                        Log.e("FuturePaymentExample",
                                "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                TastyToast.makeText(getActivity(), "Payment has been cancelled", TastyToast.LENGTH_LONG, TastyToast.INFO).show();

                Log.d("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {

                TastyToast.makeText(getActivity(), "Error Occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                Log.d("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }

    }
}
