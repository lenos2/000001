package zw.co.matrixcab.matrixcab.fragement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.acitivities.HomeActivity;
import zw.co.matrixcab.matrixcab.custom.CalculateDistanceTime;
import zw.co.matrixcab.matrixcab.custom.CheckConnection;
import zw.co.matrixcab.matrixcab.custom.GPSTracker;
import zw.co.matrixcab.matrixcab.custom.GrabAddress;
import com.sdsmdg.tastytoast.TastyToast;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;


/**
 * Created by android on 10/3/17.
 */

public class MapView extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback, GoogleMap.OnMapLoadedCallback, Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private View view;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};

    private LatLng origin = new LatLng(21.7051, 72.9959);
    private LatLng destination = new LatLng(23.0225, 72.5714);
    GoogleMap myMap;
    com.google.android.gms.maps.MapView mMapView;
    private String serverKey = "AIzaSyAlBu8MsC7jxJ68rpRR722Ojl_HQiWpnhQ";
    ProgressDialog progressDialog;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.mapview, container, false);
        BindView(savedInstanceState);
        if (!CheckConnection.haveNetworkConnection(getActivity())) {
            TastyToast.makeText(getActivity(), "network is not available", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
        }


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //serverKey  = getResources().getString(R.string.google_android_map_api_key);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        if (direction.isOK()) {
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
        }
        GrabAddress grabAddress = new GrabAddress();
        String pickup = "";
        try {
            pickup = grabAddress.downloadUrl(origin.latitude, origin.longitude);
            if (pickup.equals("")) {
                pickup = grabAddress.getLatLongInfo(getActivity(), origin);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String drop = "";
        try {
            drop = grabAddress.downloadUrl(destination.latitude, destination.longitude);
            if (drop.equals("")) {
                drop = grabAddress.getLatLongInfo(getActivity(), destination);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Pickup Location").snippet(pickup).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Drop Location").snippet(drop).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 50));

        TextView distance = (TextView) view.findViewById(R.id.distance);
        distance.setText("");


    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Log.d("direction fail", t.toString());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        myMap = googleMap;
        myMap.setMaxZoomPreference(80);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                requestDirection();
            }
        });
        thread.start();


        Log.d("max", myMap.getMaxZoomLevel() + "");


    }

    public void requestDirection() {

        Snackbar.make(view, "Direction Requesting...", Snackbar.LENGTH_SHORT).show();
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public void setCurrentLocation(Double lat, Double log) {
        if (lat != null && log != null) {
            GrabAddress grabAddress = new GrabAddress();
            String snippet = null;
            try {
                snippet = grabAddress.downloadUrl(lat, log);
                if (snippet.equals("")) {
                    snippet = grabAddress.getLatLongInfo(getActivity(), new LatLng(lat, log));
                }
            } catch (IOException e) {
                TastyToast.makeText(getActivity(), "couldn't get location", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();

                e.printStackTrace();
            }
            myMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).title("Your Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi)).snippet(snippet));

        } else {
            TastyToast.makeText(getActivity(), "couldn't get your location", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
        }
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.d("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public void calculateTime(LatLng startLatLng, LatLng endLatLng) {
        CalculateDistanceTime distance_task = new CalculateDistanceTime(getActivity());
        distance_task.getDirectionsUrl(startLatLng, endLatLng);

        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
            @Override
            public void taskCompleted(String[] time_distance) {
                TextView distance = (TextView) view.findViewById(R.id.distance);
                String concate = "Distance:  " + time_distance[0] + "  Time:  " + time_distance[1];
                distance.setText(concate);
              /*  approximate_time.setText("" + time_distance[1]);
                approximate_diatance.setText("" + time_distance[0]);*/
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLoaded() {
        //myMap.resetMinMaxZoomPreference();
    }

    public void fetchLatlong(final String pickup, final String drop) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!progressDialog.isShowing()) {
                            progressDialog.show();
                        }
                        GrabAddress grabAddress = new GrabAddress();
                        origin = grabAddress.getLocationInfo(pickup);
                        destination = grabAddress.getLocationInfo(drop);
                    }
                });
            }
        });
        thread.start();
        progressDialog.dismiss();


    }

    public void BindView(Bundle savedInstanceState) {
        ((HomeActivity) getActivity()).fontToTitleBar("Track Ride");
        mMapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading......");
        progressDialog.setCancelable(true);


        Bundle bundle = getArguments();
        try {
            if (bundle != null) {
                origin = bundle.getParcelable("pickup");
                if (origin != null) {
              /*  String[] latlong = pickup.split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);*/
                    // origin = new LatLng(latitude, longitude);
                    Log.d("origin:", "" + origin.latitude + " , " + origin.longitude);

                }
                destination = bundle.getParcelable("drop");
                if (destination != null) {
/*
                String[] latlong1 = drop.split(",");
                double latitude1 = Double.parseDouble(latlong1[0]);
                double longitude1 = Double.parseDouble(latlong1[1]);*/
                    //   destination = new LatLng(latitude1, longitude1);
                    Log.d("destination:", "" + destination.latitude + " , " + destination.longitude);
                }
            }
        } catch (Exception e) {
            TastyToast.makeText(getActivity(), "location error", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
        }

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

            }
        });

       /* GPSTracker gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            log = gps.getLongitude();
            Log.e("latitude", String.valueOf(lat));
            Log.e("longitude", String.valueOf(log));

        } else {
            gps.showSettingsAlert();
        }*/


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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Toast.makeText(getActivity(), "onconnected called", Toast.LENGTH_SHORT).show();
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            Log.e("location", currentLatitude + "  " + currentLongitude);
            setCurrentLocation(currentLatitude, currentLongitude);
            //  TastyToast.makeText(getActivity(), "" + currentLatitude + " " + currentLongitude, TastyToast.LENGTH_LONG, TastyToast.SUCCESS, Gravity.CENTER_VERTICAL);
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
            Log.d("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.e("location", currentLatitude + "  " + currentLongitude);
        setCurrentLocation(currentLatitude, currentLongitude);

        //  TastyToast.makeText(getActivity(), "" + currentLatitude + " " + currentLongitude, TastyToast.LENGTH_LONG, TastyToast.SUCCESS, Gravity.CENTER_VERTICAL);

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
}
