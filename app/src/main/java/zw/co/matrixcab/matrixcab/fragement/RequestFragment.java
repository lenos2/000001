package zw.co.matrixcab.matrixcab.fragement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.Server.Server;
import zw.co.matrixcab.matrixcab.acitivities.HomeActivity;
import zw.co.matrixcab.matrixcab.custom.CheckConnection;
import zw.co.matrixcab.matrixcab.custom.GrabAddress;
import zw.co.matrixcab.matrixcab.session.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
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
import com.thebrownarrow.permissionhelper.FragmentManagePermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.DEFAULT_MAX_CONNECTIONS;

/**
 * Created by android on 14/3/17.
 */

public class RequestFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback {
    View view;
    AppCompatButton confirm, cancel;
    TextView pickup_location, drop_location;
    Double finalfare;
    MapView mapView;
    private Double fare;
    //  private String serverKey = "AIzaSyBIso2PmHjbDoyvlGEd08L7UtYKAEXk9_w";
    private String serverKey = "AIzaSyAlBu8MsC7jxJ68rpRR722Ojl_HQiWpnhQ";

    GoogleMap myMap;
    AlertDialog alert;
    private LatLng origin;
    private LatLng destination;
    private String NETWORKNOT_AVAILABLE;
    private String TRY_AGIAN;
    private String DIRECTION_REQUEST, DIRECTION_FAIL;

    TextView textView1, textView2, textView3, textView4, textView5, txt_name, txt_number, txt_fare, title;
    private LatLng temp;
    ProgressDialog progressDialog;
    LatLng pickup, drop;
    private String REQUEST_RIDE;
    SessionManager sessionManager;
    String driver_id;
    private String user_id;
    private String pickup_address = "";
    private String drop_address = "";
    private String TAG = "request fragment";
    String distance = "";
    private String ride_id = "";
    private String drivername = "";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox
    // environments.
    private static final String CONFIG_CLIENT_ID = "Aad5iRmGB21BgnLAQi_K92Ecup2vdB08YQgmk81tf_OVf_sK7FcNXGuqMCIDWyli9lNzkqqIDF72TRT3";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    //private static final String CONFIG_CLIENT_ID = "AT5Isspr04a8035UuQ5LCErdEDSlEQqJtdGe1mrRSJmRKjkS-YEkiRJNG46xdsPPpNnwbtO4y5dMwd94";
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId("AYi2W29-PSkOI0-utUCLVEuPL1qP8BjYCEOAz3OlnDomdc8yXl10QbGJVX3yc7QgZwM2AEgGn-3K-aoM")
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Taxi App")
            .merchantPrivacyPolicyUri(
                    Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(
                    Uri.parse("https://www.example.com/legal"));

    PayPalPayment thingToBuy;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //serverKey = getContext().getString(R.string.google_android_map_api_key);
        NETWORKNOT_AVAILABLE = getResources().getString(R.string.network);
        TRY_AGIAN = getResources().getString(R.string.try_again);
        DIRECTION_REQUEST = getResources().getString(R.string.direction_request);
        DIRECTION_FAIL = getResources().getString(R.string.direction_fail);
        REQUEST_RIDE = getResources().getString(R.string.request_ride);
        ((HomeActivity) getActivity()).fontToTitleBar("Request Ride");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.request_ride, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar("Request Ride");
        if (!CheckConnection.haveNetworkConnection(getActivity())) {
            TastyToast.makeText(getActivity(), NETWORKNOT_AVAILABLE, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

        }

        bindView(savedInstanceState);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckConnection.haveNetworkConnection(getActivity())) {
                    TastyToast.makeText(getActivity(), NETWORKNOT_AVAILABLE, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                } else {
                    if (distance.equals("")) {

                        TastyToast.makeText(getActivity(), "invalid distance", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();


                    } else if (pickup_address.equals("")) {

                    } else if (drop_address.equals("")) {

                    } else if (sessionManager.getKEY() == null || sessionManager.getKEY().equals("")) {

                    } else if (txt_fare.getText().toString().trim().equals("") || txt_fare.getText().toString().trim().equals("$")) {
                    } else {

                        AddRide(sessionManager.getKEY(), pickup_address, drop_address, origin, destination, String.valueOf(finalfare), distance);

                    }
                    // AddRide(sessionManager.getKEY(), pickup_address, drop_address, origin, destination, "150", distance);
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));

            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void bindView(Bundle savedInstanceState) {
        ((HomeActivity) getActivity()).toolbar.setTitle(REQUEST_RIDE);
        mapView = (MapView) view.findViewById(R.id.mapview);
        confirm = (AppCompatButton) view.findViewById(R.id.btn_confirm);
        cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickup);
        drop_location = (TextView) view.findViewById(R.id.txt_drop);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView3 = (TextView) view.findViewById(R.id.textView3);
        textView4 = (TextView) view.findViewById(R.id.textView4);
        textView5 = (TextView) view.findViewById(R.id.textView5);
        txt_name = (TextView) view.findViewById(R.id.txt_name);
        txt_number = (TextView) view.findViewById(R.id.txt_number);
        txt_fare = (TextView) view.findViewById(R.id.txt_fare);
        title = (TextView) view.findViewById(R.id.title);
        sessionManager = new SessionManager(getActivity());
        Typeface book = Typeface.createFromAsset(getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        title.setTypeface(book);
        cancel.setTypeface(book);
        confirm.setTypeface(book);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        Bundle bundle = getArguments();

        if (bundle != null) {
            origin = bundle.getParcelable("pickup");
            destination = bundle.getParcelable("drop");
            driver_id = bundle.getString("driver_id");
            fare = Double.valueOf(bundle.getString("fare"));
            drivername = bundle.getString("drivername");
            if (drivername != null && !drivername.equals("")) {
                txt_name.setText(drivername);
            }

        } else {
            //Do nothing
        }
        overrideFonts(getActivity(), view);


       /* GPSTracker gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            log = gps.getLongitude();
            Log.d("latitude", String.valueOf(lat));
            Log.d("longitude", String.valueOf(log));

        } else {
            gps.showSettingsAlert();
        }*/

        if (sessionManager != null) {
            HashMap<String, String> getDetail = sessionManager.getUserDetails();
            String id = getDetail.get(SessionManager.USER_ID);
            if (id != null && !id.equals("")) {
                user_id = id;
            }

        }

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        // Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
        if (direction.isOK()) {
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
        } else {
            distanceAlert(direction.getErrorMessage());
        }
        GrabAddress grabAddress = new GrabAddress();

        try {
            pickup_address = grabAddress.downloadUrl(origin.latitude, origin.longitude);

        } catch (IOException e) {
            Log.e("catch", e.toString());
        }
        try {
            drop_address = grabAddress.downloadUrl(destination.latitude, destination.longitude);

        } catch (IOException e) {
            Log.e("catch", e.toString());
        }
        pickup_location.setText(pickup_address);
        drop_location.setText(drop_address);
        myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Pickup Location").snippet(pickup_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Drop Location").snippet(drop_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        calcaulatedistance();


    }

    @Override
    public void onDirectionFailure(Throwable t) {
        distanceAlert(t.getMessage() + "\n" + t.getLocalizedMessage() + "\n");
        //TastyToast.makeText(getActivity(),DIRECTION_FAIL,TastyToast.LENGTH_LONG,TastyToast.ERROR,Gravity.TOP).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(getActivity(), "onReady", Toast.LENGTH_SHORT).show();
        myMap = googleMap;
        requestDirection();
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 5));


    }

    public void setCurrentLocation(Double lat, Double log) {
        if (lat != null && log != null) {
            //getLatLongInfo(lat, log, BitmapDescriptorFactory.fromResource(R.drawable.taxi), "Your Current Location", true, false);

        } else {
            Toast.makeText(getActivity(), "coudn't get your location", Toast.LENGTH_SHORT).show();
        }
    }


    public void requestDirection() {

        Snackbar.make(view, DIRECTION_REQUEST, Snackbar.LENGTH_SHORT).show();
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Medium.otf"));
                //Typeface medium = Typeface.createFromAsset(getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
            }
        } catch (Exception e) {
        }
    }

    public  String getDateCurrentTimeZone() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public void AddRide(String key, String pickup_adress, String drop_address, LatLng pikup_location, LatLng drop_locatoin, String amount, String distance) {
        final RequestParams params = new RequestParams();
        params.put("driver_id", driver_id);
        params.put("user_id", user_id);
        params.put("pickup_adress", pickup_adress);
        params.put("drop_address", drop_address);
        String p = pikup_location.latitude + " ," + pikup_location.longitude;
        String d = drop_locatoin.latitude + " ," + drop_locatoin.longitude;
      //  Log.e("print", p + "   " + d);

        params.put("pikup_location", p);
        params.put("drop_locatoin", d);
        params.put("amount", amount);
        params.put("distance", distance);
        params.put("time",getDateCurrentTimeZone());
        Server.setHeader(key);

        Log.e("session", key);
        Server.post("api/user/addRide/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setMessage("Requesting Ride");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(TAG, response.toString());
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        progressDialog.cancel();
                        TastyToast.makeText(getActivity(), "Ride has been requested ", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                        ((HomeActivity) getActivity()).changeFragment(new HomeFragment(), "Home");
                        //  Updatepayemt(response.getJSONObject("data").getString("id"), "PAID");
                    } else {
                        progressDialog.dismiss();
                        TastyToast.makeText(getActivity(), TRY_AGIAN, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                    }
                } catch (JSONException e) {
                    TastyToast.makeText(getActivity(), TRY_AGIAN, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                    progressDialog.dismiss();
                    Log.e("catch", response.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, responseString);
                TastyToast.makeText(getActivity(), "error occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                progressDialog.dismiss();
            }
        });


    }


    public void calcaulatedistance() {
        android.location.Location location1 = new android.location.Location("location1");
        android.location.Location location2 = new android.location.Location("location2");
        location1.setLatitude(origin.latitude);
        location1.setLongitude(origin.longitude);
        location2.setLatitude(destination.latitude);
        location2.setLongitude(destination.longitude);
        double d = location1.distanceTo(location2) / 1000;
        distance = String.valueOf(d);
        Log.d("distance", d + "");
        Double aDouble = d;

        if (aDouble != null) {
            if (fare != null && fare != 0.0) {


                DecimalFormat dtime = new DecimalFormat("##.##");

                Double ff = aDouble * fare;

                finalfare = Double.valueOf(dtime.format(ff));

                txt_fare.setText(finalfare + " $");

                Log.d("decimal time format", finalfare + "");

            } else {
                txt_fare.setText(" $");
            }
        }

      /*  CalculateDistanceTime distance_task = new CalculateDistanceTime(getActivity());
        distance_task.getDirectionsUrl(origin, destination);
        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
            @Override
            public void taskCompleted(String[] time_distance) {
                String dis = time_distance[0];

                if (dis.contains("km")) {
                    distance = dis.replace("km", "");
                    calculateFare(distance);
                    //Toast.makeText(getActivity(), dis+"  km is   called", Toast.LENGTH_SHORT).show();
                } else {
                    txt_fare.setText("$");
                    distanceAlert("You can't make ride request for less than 1 KM distance");
                    //TastyToast.makeText(getActivity(), "distance error", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                }
                *//*else if (dis.contains("m")) {
                    Toast.makeText(getActivity(), dis+"   m is  called", Toast.LENGTH_SHORT).show();
                    distance = dis.replace("m", "");
                }*//*
                Log.d("distance", distance);

            }
        });*/
    }


    public void distanceAlert(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("INVALID DISTANCE");
        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        alertDialog.setIcon(drawable);


        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.cancel();
            }
        });
        alert = alertDialog.create();
        alert.show();
    }
}
