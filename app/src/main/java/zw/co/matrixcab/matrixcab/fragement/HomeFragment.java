package zw.co.matrixcab.matrixcab.fragement;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import zw.co.matrixcab.matrixcab.Server.Server;
import zw.co.matrixcab.matrixcab.custom.CalculateDistanceTime;
import zw.co.matrixcab.matrixcab.custom.CheckConnection;
import zw.co.matrixcab.matrixcab.custom.GrabAddress;
import zw.co.matrixcab.matrixcab.pojo.NearbyData;
import zw.co.matrixcab.matrixcab.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.acitivities.HomeActivity;
import zw.co.matrixcab.matrixcab.custom.GPSTracker;
import zw.co.matrixcab.matrixcab.custom.PlaceJSONParser;
import com.loopj.android.http.RequestParams;
import com.sdsmdg.tastytoast.TastyToast;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;


/**
 * Created by android on 7/3/17.
 */

public class HomeFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback, Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private String driver_id = "";
    private String cost = "";
    private String unit = "";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    public String NETWORK;
    public String ERROR = "error occurred";
    public String TRYAGAIN;
    private View rootView;
    Boolean flag = false;
    GoogleMap myMap;
    LatLng temp;
    ImageView current_location, clear;
    MapView mMapView;
    PlacesTask placesTask;
    ParserTask parserTask;
    PlacesTask1 placesTask1;
    ParserTask1 parserTask1;
    LatLng tempLatLng;
    int i = 0;
    String result = "";
    /*private String serverKey = "AIzaSyBJy7WI8GilhqvZuy5q6EwSkIBS-2ugbS8";*/
    private String serverKey = "AIzaSyAlBu8MsC7jxJ68rpRR722Ojl_HQiWpnhQ";
    private RelativeLayout header, footer;
    Animation animFadeIn, animFadeOut;
    String TAG = "home";
    ProgressDialog progressDialog;
    AutoCompleteTextView pickup_location, drop_location;
    RelativeLayout relative_drop;
    RelativeLayout linear_pickup;
    TextView txt_vehicleinfo, rate, txt_info, txt_cost, txt_color, txt_address, request_ride;
    LinearLayout linear_request;
    SessionManager sessionManager;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private String check = "";
    private String drivername = "";


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NETWORK = getResources().getString(R.string.network);
        TRYAGAIN = getResources().getString(R.string.try_again);
        MapsInitializer.initialize(this.getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.home_fragment, container, false);
            // globatTitle = "Home";
            ((HomeActivity) getActivity()).fontToTitleBar("Home");
            bindView(savedInstanceState);
            if (!CheckConnection.haveNetworkConnection(getActivity())) {
                TastyToast.makeText(getActivity(), "network is not available", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askCompactPermissions(permissionAsk, new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        if (!GPSEnable()) {
                            tunonGps();
                        } else {

                            getCurrentlOcation();
                        }
                    }

                    @Override
                    public void permissionDenied() {

                    }

                    @Override
                    public void permissionForeverDenied() {

                        openSettingsApp(getActivity());
                    }
                });

            } else {
                if (!GPSEnable()) {
                    tunonGps();
                } else {
                    getCurrentlOcation();
                }

            }

            drop_location.setThreshold(1);
            pickup_location.setThreshold(1);

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                        header.startAnimation(animFadeOut);
                        footer.startAnimation(animFadeOut);
                        header.setVisibility(View.GONE);
                        footer.setVisibility(View.GONE);
                    }
                }
            });
            linear_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnection.haveNetworkConnection(getActivity())) {
                        if (!pickup_location.getText().toString().trim().equals("") && !drop_location.getText().toString().trim().equals("")) {
                            if (!progressDialog.isShowing()) {
                                progressDialog.show();
                            }
                            GrabAddress grabAddress = new GrabAddress();

                            LatLng pickup = grabAddress.getLocationInfo(pickup_location.getText().toString().trim());
                            LatLng drop = grabAddress.getLocationInfo(drop_location.getText().toString().trim());


                            Bundle bundle = new Bundle();
                            bundle.putParcelable("pickup", pickup);
                            bundle.putParcelable("drop", drop);
                            bundle.putString("driver_id", driver_id);
                            bundle.putString("fare", cost);
                            bundle.putString("drivername", drivername);
                            RequestFragment fragobj = new RequestFragment();
                            fragobj.setArguments(bundle);
                            Log.e("pass", pickup + " " + drop);
                            if (!driver_id.equals("") && !drivername.equals("")) {
                                progressDialog.dismiss();
                                ((HomeActivity) getActivity()).changeFragment(fragobj, "Request Ride");
                            } else {
                                progressDialog.dismiss();
                                TastyToast.makeText(getActivity(), TRYAGAIN, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                            }
                        } else {
                            TastyToast.makeText(getActivity(), "invalid location", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                        }
                    } else {
                        TastyToast.makeText(getActivity(), NETWORK, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                    }
                }
            });
            drop_location.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (s.equals("") && s.length() == 0) {
                        //do nothing
                        Log.e("text changed", s.length() + "  " + s.toString());
                    } else {
                        placesTask1 = new PlacesTask1();
                        placesTask1.execute(s.toString());
                    }
                    //getNearestPlaces(new String[]{String.valueOf(s)});

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            pickup_location.addTextChangedListener(new TextWatcher() {


                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));

                    if (s.equals("") && s.length() == 0) {
                        // do nothing
                        Log.e("text changed", s.length() + "  " + s.toString());
                    } else {
                        placesTask = new PlacesTask();
                        placesTask.execute(s.toString());
                    }

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                    Log.e("text", "before" + s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                    Log.e("text", "after" + s);
                }
            });
        } catch (InflateException e) {
            Log.e("tag", "Inflate exception");
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                getCurrentlOcation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMapView != null) {
            Log.e("onresume", "called");
            mMapView.onResume();
            if (currentLatitude != null && !currentLatitude.equals(0.0) && currentLongitude != null && !currentLongitude.equals(0.0)) {
                Neaby(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
            } else {
                Log.e("onresume", "null location");
            }

        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       /* if (mMapView != null) {
            Log.e("onresume", "called");
            mMapView.onResume();
            if (currentLatitude != null && !currentLatitude.equals(0.0) && currentLongitude != null && !currentLongitude.equals(0.0)) {
                Neaby(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
            } else {
                Log.e("onresume", "null location");
            }

        }*/
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

              /*  String name = marker.getTitle();
                String info = marker.getSnippet();
                Log.e("info", name + "  " + info);*/

                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {

                View v = getActivity().getLayoutInflater().inflate(R.layout.view_custom_marker, null);


                LatLng latLng = marker.getPosition();
                TextView title = (TextView) v.findViewById(R.id.t);
                TextView t1 = (TextView) v.findViewById(R.id.t1);
                TextView t2 = (TextView) v.findViewById(R.id.t2);
                ImageView imageView = (ImageView) v.findViewById(R.id.profile_image);
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
                t1.setTypeface(font);
                t2.setTypeface(font);

                String name = marker.getTitle();
                title.setText(name);
                String info = marker.getSnippet();
                t1.setText(info);
                driver_id = (String) marker.getTag();
                drivername = marker.getTitle();

                txt_info.setText(info);

                if (CheckConnection.haveNetworkConnection(getActivity())) {

                    if (!progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                    LoadInfo(marker);
                } else {

                    TastyToast.makeText(getActivity(), NETWORK, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                }

                return v;

            }
        });
        myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
                    header.setVisibility(View.GONE);
                    footer.setVisibility(View.GONE);
                } else {
                    header.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    header.startAnimation(animFadeIn);
                    footer.startAnimation(animFadeIn);
                }

            }
        });

        if (myMap != null) {
            Log.e("location", currentLatitude + "  " + currentLongitude);
            tunonGps();
        }
    }


    protected void multipleMarker(List<NearbyData> list) {
        if (list != null) {
            for (NearbyData location : list) {
                Double latitude = Double.valueOf(location.getLatitude());
                Double longitude = Double.valueOf(location.getLongitude());
                Log.d("data", latitude + " " + longitude + " " + location.getName() + " " + location.getVehicle_info());
                Marker marker = myMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).anchor(0.5f, .05f).title(location.getName()).snippet(location.getVehicle_info()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marker.setTag(location.getUser_id());
                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10);
                myMap.animateCamera(camera);
            }
            progressDialog.dismiss();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {


    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void bindView(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading.....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        MapsInitializer.initialize(this.getActivity());
        current_location = (ImageView) rootView.findViewById(R.id.current_location);
        clear = (ImageView) rootView.findViewById(R.id.clear);
        txt_vehicleinfo = (TextView) rootView.findViewById(R.id.txt_vehicleinfo);
        rate = (TextView) rootView.findViewById(R.id.rate);

        txt_info = (TextView) rootView.findViewById(R.id.txt_info);
        txt_address = (TextView) rootView.findViewById(R.id.txt_addresss);
        request_ride = (TextView) rootView.findViewById(R.id.request_rides);
        txt_color = (TextView) rootView.findViewById(R.id.txt_color);
        txt_cost = (TextView) rootView.findViewById(R.id.txt_cost);
        sessionManager = new SessionManager(getActivity());


        mMapView = (MapView) rootView.findViewById(R.id.mapview);
        linear_request = (LinearLayout) rootView.findViewById(R.id.linear_request);
        header = (RelativeLayout) rootView.findViewById(R.id.header);
        footer = (RelativeLayout) rootView.findViewById(R.id.footer);
        pickup_location = (AutoCompleteTextView) rootView.findViewById(R.id.pickup_location);
        drop_location = (AutoCompleteTextView) rootView.findViewById(R.id.drop_location);
        linear_pickup = (RelativeLayout) rootView.findViewById(R.id.linear_pickup);
        relative_drop = (RelativeLayout) rootView.findViewById(R.id.relative_drop);

        placesTask = new PlacesTask();
        placesTask1 = new PlacesTask1();
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this);
        // load animations
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fade_out);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        applyfonts();

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drop_location.setText("");
                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
                    header.setVisibility(View.GONE);
                    footer.setVisibility(View.GONE);
                }
            }
        });
        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCancelable(true);

                if (!CheckConnection.haveNetworkConnection(getActivity())) {
                    TastyToast.makeText(getActivity(), NETWORK, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        askCompactPermissions(permissionAsk, new PermissionResult() {
                            @Override
                            public void permissionGranted() {
                                if (!GPSEnable()) {
                                    // Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
                                    tunonGps();
                                } else {
                                    if (!progressDialog.isShowing()) {
                                        progressDialog.show();
                                        progressDialog.setMessage("Loading....");
                                    }
                                    if (currentLatitude != null && currentLatitude != 0.0 && currentLongitude != null && currentLongitude != 0.0) {
                                        //Toast.makeText(getActivity(), currentLatitude + " " + currentLongitude, Toast.LENGTH_SHORT).show();

                                        final GrabAddress grabAddress = new GrabAddress();
                                        if (i == 0) {
                                            Thread thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                result = grabAddress.downloadUrl(currentLatitude, currentLongitude);
                                                                if (!result.equals("")) {
                                                                    progressDialog.dismiss();
                                                                    pickup_location.setText(result);
                                                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));
                                                                    i = 1;
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    TastyToast.makeText(getActivity(), TRYAGAIN, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                                                                }
                                                            } catch (IOException e) {
                                                                progressDialog.dismiss();
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });

                                                }
                                            });
                                            thread.start();

                                        } else {
                                            i = 0;
                                            pickup_location.setText("");
                                            progressDialog.dismiss();
                                            current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));

                                        }


                                    } else {
                                        progressDialog.dismiss();
                                    }
                                }


                            }

                            @Override
                            public void permissionDenied() {

                            }

                            @Override
                            public void permissionForeverDenied() {
                                Snackbar.make(rootView, "Allow Permission", Snackbar.LENGTH_LONG).show();

                                openSettingsApp(getActivity());

                            }
                        });
                    } else {
                        if (!GPSEnable()) {
                            tunonGps();
                            //Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!progressDialog.isShowing()) {
                                progressDialog.show();
                                progressDialog.setMessage("Loading....");
                            }

                            if (currentLatitude != null && currentLatitude != 0.0 && currentLongitude != null && currentLongitude != 0.0) {
                                // Toast.makeText(getActivity(), currentLatitude + " " + currentLongitude, Toast.LENGTH_SHORT).show();
                                final GrabAddress grabAddress = new GrabAddress();

                                if (i == 0) {
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        result = grabAddress.downloadUrl(currentLatitude, currentLongitude);
                                                        if (!result.equals("")) {
                                                            progressDialog.dismiss();
                                                            pickup_location.setText(result);
                                                            current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));
                                                            i = 1;
                                                        } else {

                                                            progressDialog.dismiss();
                                                            Toast.makeText(getActivity(), "try again", Toast.LENGTH_SHORT).show();
                                                        }
                                                        progressDialog.dismiss();
                                                    } catch (IOException e) {
                                                        Log.d("catch", e.toString());
                                                    }
                                                }
                                            });


                                        }
                                    });
                                    thread.start();

                                } else {
                                    i = 0;
                                    pickup_location.setText("");
                                    progressDialog.dismiss();
                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));

                                }
                            } else {
                                progressDialog.dismiss();
                            }
                        }


                    }

                }

            }
        });

    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
              //  TastyToast.makeText(getActivity(), "couldn't get your location", Toast.LENGTH_SHORT, TastyToast.ERROR).show();
            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
                    if (!flag) {
                        Neaby(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
                    } else {
                    }
                } else {

                    TastyToast.makeText(getActivity(), "couldn't get your location", Toast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        } else {
            askCompactPermissions(permissionAsk, new PermissionResult() {
                @Override
                public void permissionGranted() {

                }

                @Override
                public void permissionDenied() {
                    //Toast.makeText(getActivity(), "denied", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void permissionForeverDenied() {
                    Snackbar.make(rootView, "Allow Permission", Snackbar.LENGTH_LONG).show();
                    openSettingsApp(getActivity());


                }
            });


        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }

    }



    private class PlacesTask1 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + serverKey;

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            String parameters = input + "&" + types + "&" + sensor + "&" + key;
            String output = "json";

            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
            Log.e("url", url);
            try {
                // Fetching the data from we service
                data = downloadUrl(url);
                Log.e("data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask1 = new ParserTask1();

            // Starting Parsing the JSON string returned by Web Service
            if (result != null) {
                parserTask1.execute(result);
            }

        }
    }

    private class ParserTask1 extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);
                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            drop_location.setAdapter(adapter);

        }
    }

    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + serverKey;

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            String parameters = input + "&" + types + "&" + sensor + "&" + key;
            String output = "json";

            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
            Log.e("url", url);
            try {
                // Fetching the data from we service
                data = downloadUrl(url);
                Log.e("data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            if (result != null) {
                parserTask.execute(result);
            }
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);
                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            pickup_location.setAdapter(adapter);

        }
    }

    public void applyfonts() {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Book.otf");
        pickup_location.setTypeface(font);
        drop_location.setTypeface(font);
        txt_vehicleinfo.setTypeface(font1);
        rate.setTypeface(font1);

        txt_color.setTypeface(font);
        txt_address.setTypeface(font);
        request_ride.setTypeface(font1);


    }

    public void Neaby(String latititude, String longitude) {
        flag = true;
        RequestParams params = new RequestParams();
        params.put("lat", latititude);
        params.put("long", longitude);
        Server.setHeader(sessionManager.getKEY());

        Server.get("api/user/nearby/format/json", params, new JsonHttpResponseHandler() {
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
                Log.d(TAG, response.toString());
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Gson gson = new GsonBuilder().create();
                        List<NearbyData> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<NearbyData>>() {

                        }.getType());

                        multipleMarker(list);
                        cost = response.getJSONObject("fair").getString("cost");
                        unit = response.getJSONObject("fair").getString("unit");
                        sessionManager.setUnit(unit);
                        Log.d("fare", cost + "  " + unit);
                        progressDialog.dismiss();

                    }
                } catch (JSONException e) {
                    Log.d(TAG, e.toString());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                // Log.d(TAG, errorResponse.toString() + "");
                TastyToast.makeText(getActivity(), "try again later", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, responseString);
                progressDialog.dismiss();
            }
        });
    }

    public void calculateTime(LatLng startLatLng, LatLng endLatLng) {
        CalculateDistanceTime distance_task = new CalculateDistanceTime(getActivity());
        distance_task.getDirectionsUrl(startLatLng, endLatLng);

        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
            @Override
            public void taskCompleted(String[] time_distance) {
                //TextView distance = (TextView) view.findViewById(R.id.distance);
                //  String concate = "Distance:  " + time_distance[0] + "  Time:  " + time_distance[1];
                //  distance.setText(concate);
              /*  approximate_time.setText("" + time_distance[1]);
                approximate_diatance.setText("" + time_distance[0]);*/
            }

        });
    }


    public void getCurrentlOcation() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            getCurrentlOcation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and setting the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            return false;
        }


    }

    public String LoadInfo(final Marker marker) {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        final GrabAddress grabAddress = new GrabAddress();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            check = grabAddress.downloadUrl(marker.getPosition().latitude, marker.getPosition().longitude);

                            if (!check.equals("")) {
                                txt_address.setText(check);
                                if (unit != null && cost != null) {
                                    txt_cost.setText(cost + "  " + unit);
                                    progressDialog.cancel();
                                } else {
                                    progressDialog.cancel();
                                    TastyToast.makeText(getActivity(), TRYAGAIN, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                                }

                            } else {
                                check = grabAddress.getLatLongInfo(getActivity(), new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                                txt_address.setText(check);
                                if (unit != null && cost != null) {
                                    txt_cost.setText(cost + "  " + unit);
                                    progressDialog.cancel();
                                } else {
                                    progressDialog.cancel();
                                    TastyToast.makeText(getActivity(), TRYAGAIN, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            progressDialog.cancel();
                        }
                    }
                });

            }
        });
        thread.start();

        return check;
    }
}


