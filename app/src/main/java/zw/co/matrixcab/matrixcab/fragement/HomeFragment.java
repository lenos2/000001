package zw.co.matrixcab.matrixcab.fragement;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.Server.Server;
import zw.co.matrixcab.matrixcab.acitivities.HomeActivity;
import zw.co.matrixcab.matrixcab.custom.CheckConnection;
import zw.co.matrixcab.matrixcab.custom.GPSTracker;
import zw.co.matrixcab.matrixcab.pojo.NearbyData;
import zw.co.matrixcab.matrixcab.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sdsmdg.tastytoast.TastyToast;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * Created by android on 7/3/17.
 */

public class HomeFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback, Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public String NETWORK;
    public String ERROR = "error occurred";
    public String TRYAGAIN;
    Boolean flag = false;
    GoogleMap myMap;
    LatLng temp;
    ImageView current_location, clear;
    MapView mMapView;
    LatLng tempLatLng;
    int i = 0;
    String result = "";
    Animation animFadeIn, animFadeOut;
    String TAG = "home";
    ProgressDialog progressDialog;
    TextView pickup_location, drop_location;
    RelativeLayout relative_drop;
    RelativeLayout linear_pickup;
    TextView txt_vehicleinfo, rate, txt_info, txt_cost, txt_color, txt_address, request_ride;
    LinearLayout linear_request;
    SessionManager sessionManager;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    List<NearbyData> list;
    Place pickup, drop;
    private String driver_id = "";
    private String cost = "";
    private String unit = "";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private View rootView;
    private String serverKey = Server.MAPS_APIKEY_BROWSER;
    private RelativeLayout header, footer;
    private String drivername = "";
    private String driverNumber = "";
    private int PLACE_PICKER_REQUEST = 7896;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;
    private Marker my_marker;

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
        MapsInitializer.initialize(getActivity());
        bindView(savedInstanceState);
        ((HomeActivity) getActivity()).fontToTitleBar("Home");
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

                        LatLng pickupl = pickup.getLatLng();
                        LatLng dropl = drop.getLatLng();


                        Bundle bundle = new Bundle();
                        bundle.putParcelable("pickup", pickupl);
                        bundle.putParcelable("drop", dropl);
                        bundle.putString("driver_id", driver_id);
                        bundle.putString("fare", cost);
                        bundle.putString("drivername", drivername);
                        //bundle.putString("driverNumber",);
                        bundle.putString("pickup_address", pickup.getAddress().toString());
                        bundle.putString("drop_address", drop.getAddress().toString());
                        RequestFragment fragobj = new RequestFragment();
                        fragobj.setArguments(bundle);


                        ((HomeActivity) getActivity()).changeFragment(fragobj, "Request Ride");

                    } else {
                        TastyToast.makeText(getActivity(), "Invalid location", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                    }
                } else {
                    TastyToast.makeText(getActivity(), NETWORK, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                }
            }
        });


        pickup_location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(getActivity());
                        //startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        /*pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    //startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });*/

        drop_location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(getActivity());
                        //startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                        startActivityForResult(builder.build(getActivity()), PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }
            }
        });
        /*drop_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    //startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    startActivityForResult(builder.build(getActivity()), PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.home_fragment, container, false);
            // globatTitle = "Home";

        } catch (InflateException e) {
            Log.e("tag", "Inflate exception");
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                getCurrentlOcation();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                pickup = PlaceAutocomplete.getPlace(getActivity(), data).freeze();
                pickup_location.setText(pickup.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                TastyToast.makeText(getActivity(), status.getStatusMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                drop = PlaceAutocomplete.getPlace(getActivity(), data).freeze();
                drop_location.setText(drop.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                TastyToast.makeText(getActivity(), status.getStatusMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

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

                NearbyData nearbyData = (NearbyData) marker.getTag();
                if (nearbyData != null) {
                    nearbyData.getUser_id();
                    txt_info.setText(info);
                    t2.setVisibility(View.VISIBLE);
                } else {
                    t2.setVisibility(View.GONE);
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
                    if (marker.getTag() != null) {
                        LoadInfo(marker);
                        header.setVisibility(View.VISIBLE);
                        footer.setVisibility(View.VISIBLE);
                        header.startAnimation(animFadeIn);
                        footer.startAnimation(animFadeIn);
                    }

                }

            }
        });

        if (myMap != null) {
            tunonGps();
        }
    }


    protected void multipleMarker(List<NearbyData> list) {
        try {
            if (list != null) {
                for (NearbyData location : list) {
                    Double latitude = Double.valueOf(location.getLatitude());
                    Double longitude = Double.valueOf(location.getLongitude());
                    Marker marker = myMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).anchor(0.5f, .05f).title(location.getName()).snippet(location.getVehicle_info()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    marker.setTag(location);
                }

            }
        } catch (Exception e) {

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
        pickup_location = (TextView) rootView.findViewById(R.id.pickup_location);
        drop_location = (TextView) rootView.findViewById(R.id.drop_location);
        linear_pickup = (RelativeLayout) rootView.findViewById(R.id.linear_pickup);
        relative_drop = (RelativeLayout) rootView.findViewById(R.id.relative_drop);


        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this);
        // load animations
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_open);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(),
                R.anim.dialogue_scale_anim_exit);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        applyfonts();

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askCompactPermissions(permissionAsk, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            setCurrentLocation();
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
                        setCurrentLocation();
                    }

                }
            }
        });

    }

    private void setCurrentLocation() {
        if (!GPSEnable()) {
            tunonGps();
        } else {

            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    try {
                        pickup = likelyPlaces.get(0).getPlace().freeze();
                        pickup_location.setText(likelyPlaces.get(0).getPlace().getAddress());
                        current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
                        likelyPlaces.release();
                    } catch (Exception e) {

                    }

                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (result == PackageManager.PERMISSION_GRANTED) {
                android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location == null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                } else {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
                        if (!flag) {
                            my_marker=myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).anchor(0.5f, .05f).title("You").snippet("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14);
                            myMap.animateCamera(camera);
                            Neaby(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
                        }
                    } else {
                        TastyToast.makeText(getActivity(), "couldn't get your location", Toast.LENGTH_SHORT, TastyToast.ERROR).show();
                    }
                }
            }
        } catch (Exception e) {

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
        }

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        try {
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                my_marker.setPosition(new LatLng(currentLatitude,currentLongitude));
                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14);
                myMap.animateCamera(camera);
            }
        } catch (Exception e) {

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

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Gson gson = new GsonBuilder().create();
                        list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<NearbyData>>() {

                        }.getType());

                        multipleMarker(list);
                        cost = response.getJSONObject("fair").getString("cost");
                        unit = response.getJSONObject("fair").getString("unit");
                        sessionManager.setUnit(unit);

                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (progressDialog.isShowing()) {
                    progressDialog.hide();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                try {
                    TastyToast.makeText(getActivity(), "try again later", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                } catch (Exception e) {

                }
            }


        });
    }

    public void getCurrentlOcation() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Places.GEO_DATA_API)
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
                            } catch (Exception e) {
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

    public void LoadInfo(final Marker marker) {
        drivername = marker.getTitle();
        driver_id=((NearbyData)marker.getTag()).getUser_id();
        Server.getPublic("http://maps.google.com/maps/api/geocode/json?latlng=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&sensor=false", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    result = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                    txt_address.setText(result);

                } catch (JSONException e) {

                }
                txt_cost.setText(cost + "  " + unit);
            }
        });

    }

}


