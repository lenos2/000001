package zw.co.matrixcab.matrixcab.acitivities;
/*

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import icanstudioz.com.taxiapp.R;
import icanstudioz.com.taxiapp.custom.GPSTracker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionCallback {

    private TextView title, shopname;
    private GoogleMap googleMap;
    private String serverKey ="AIzaSyCEwOjqMNUJ7sjbvz-LDoiTMRUdiXtHq0Q";
    private LatLng camera = new LatLng(13.7457211, 100.5646619);
    private LatLng origin = new LatLng(13.7371063, 100.5642539);
    private LatLng destination = new LatLng(13.7604896, 100.5594266);
    Location location;
    Intent intent;
    LatLng LOCATION, Location1;
    Fragment f = null;
    double longitude, log;
    Polyline line;
    TextView km;
    double latitude, lat;
    public String SHOPNAME, TITLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map)).getMapAsync(this);
        // mapFragment.getMapAsync(this);
        km = (TextView) findViewById(R.id.KM);
        title = (TextView) findViewById(R.id.txt_title);
        shopname = (TextView) findViewById(R.id.txt_shopName);



       *//* LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(bestProvider);

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            String locLat = String.valueOf(latitude) + "," + String.valueOf(longitude);
            Log.e("lat", String.valueOf(longitude));
            Log.e("lng", String.valueOf(latitude));


        } else {
            Log.e("location", String.valueOf(location));
        }*//*

        GPSTracker gps = new GPSTracker(getApplicationContext());
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.e("lattitude", String.valueOf(latitude));
            Log.e("longitude", String.valueOf(longitude));
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            log = Double.valueOf(extras.getString("log"));
            lat = Double.valueOf(extras.getString("lat"));
            SHOPNAME = extras.getString("shop");
            TITLE = extras.getString("title");
            shopname.setText(SHOPNAME);
            title.setText(TITLE);
            // Log.e("map", log + "..." + lat);
            LOCATION = new LatLng(lat, log);
            Location1 = new LatLng(latitude, longitude);
            float[] results = new float[1];
            Location.distanceBetween(lat, log,
                    latitude, longitude, results);

            Log.e("Distance", results.toString());
           *//* String urlTopass = makeURL(latitude,
                    longitude, lat,
                   log);
            new connectAsyncTask(urlTopass).execute();*//*


            Location loc1 = new Location("");
            loc1.setLatitude(latitude);
            loc1.setLongitude(longitude);

            Location loc2 = new Location("");
            loc2.setLatitude(lat);
            loc2.setLongitude(log);
            float distanceInMeters = loc1.distanceTo(loc2);
            Log.e("Distance", String.valueOf(distanceInMeters));
            km.setText("Distance of Shop is " + String.valueOf(distanceInMeters) + " Km");
            // getDetailsLocation(latitude, longitude);
        } else {
            Toast.makeText(MapsActivity.this, "No Location Found", Toast.LENGTH_SHORT).show();
        }
        requestDirection();

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        *//*if (direction.isOK()) {
            ArrayList<LatLng> sectionPositionList = direction.getRouteList().get(0).getLegList().get(0).getSectionPoint();
            for (LatLng position : sectionPositionList) {
                googleMap.addMarker(new MarkerOptions().position(position));
            }

            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(this, stepList, 5, Color.RED, 3, Color.BLUE);
            for (PolylineOptions polylineOption : polylineOptionList) {
                googleMap.addPolyline(polylineOption);
            }


        }*//*
        if (direction.isOK()) {
            //googleMap.addMarker(new MarkerOptions().position(origin));
            // googleMap.addMarker(new MarkerOptions().position(destination));

            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
        }
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(origin.latitude, origin.longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String state = addresses.get(0).getAdminArea();
            googleMap.addMarker(new MarkerOptions().position(origin).title(city + "," + state + "," + country).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            addresses = geocoder.getFromLocation(destination.latitude, destination.longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String state = addresses.get(0).getAdminArea();
            googleMap.addMarker(new MarkerOptions().position(destination).title(city + "," + state + "," + country).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        camera = new LatLng(lat, log);
        this.googleMap = googleMap;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 10));
    }

    public void requestDirection() {
        origin = new LatLng(latitude, longitude);
        destination = new LatLng(lat, log);
        // Snackbar.make(btnRequestDirection, "Direction Requesting...", Snackbar.LENGTH_SHORT).show();
        //origin = new LatLng(latitude, longitude);
        //  destination = new LatLng(lat, log);
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }
}*/
/*

    *//**
 * Manipulates the map once available.
 * This callback is triggered when the map is ready to be used.
 * This is where we can add markers or lines, add listeners or move the camera. In this case,
 * we just add a marker near Sydney, Australia.
 * If Google Play services is not installed on the device, the user will be prompted to install
 * it inside the SupportMapFragment. This method will only be triggered once the user has
 * installed Google Play services and returned to the app.
 *//*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
      *//*  LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*//*

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                getAddress(lat, log);
                getAddress2(latitude, longitude);

                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION, 1);
                mMap.animateCamera(update);

                //addLines();
                addpolygonLine();

            }
        });
        mMap.setMyLocationEnabled(true);


    }

    void getAddress(double lat, double log) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, log, 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            mMap.addMarker(new MarkerOptions().position(LOCATION).title(city + "," + state + "," + country).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void getAddress2(double lat, double log) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, log, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String state = addresses.get(0).getAdminArea();
            mMap.addMarker(new MarkerOptions().position(Location1).title(city + "," + state + "," + country));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDetailsLocation(double lat, double log) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, log, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, checkky with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();


            Log.e("locationDetails", address + ".." + city + ".." + state + "..." + country);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void addLines() {

        mMap.addPolyline((new PolylineOptions())
                .add(LOCATION, Location1).width(5).color(Color.BLUE)
                .geodesic(true));
        // move camera to zoom on map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Location1, 1));


        Log.e("newlocation", String.valueOf(LOCATION));
    }

    public void addpolygonLine() {
        List<LatLng> list = new ArrayList<>();
        list.add(LOCATION);
        list.add(Location1);
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.RED);
        polyOptions.width(5);
        polyOptions.addAll(list);
        mMap.clear();
        mMap.addPolyline(polyOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }
        final LatLngBounds bounds = builder.build();
        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 14);
        mMap.animateCamera(cu);
    }


}*/


