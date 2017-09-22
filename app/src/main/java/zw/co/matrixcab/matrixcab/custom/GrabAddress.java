package zw.co.matrixcab.matrixcab.custom;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static com.loopj.android.http.AsyncHttpClient.DEFAULT_MAX_CONNECTIONS;

/**
 * Created by android on 22/3/17.
 */

public class GrabAddress {
    private double latitude;
    private double longitude;


    public String getLatLongInfo(Context context, Double platitide, Double plongitude, Double dlatitide, Double dlongitude) {

        String merged = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(platitide, plongitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String state = addresses.get(0).getAdminArea();
                String area = addresses.get(0).getFeatureName();
                String sublocality = addresses.get(0).getSubLocality();
                String pincode = addresses.get(0).getPostalCode();

                if (city != null) {
                    merged = city;
                } else {
                    city = "";
                }

                if (state != null) {
                    merged = city + " ," + state;
                } else {
                    state = "";
                }
                if (country != null) {
                    merged = city + " ," + state + "," + country;
                } else {
                    country = "";
                }
                if (area != null) {
                    merged = area + "," + city + ", " + state + ", " + country;
                } else {
                    area = "";
                }
                if (pincode != null) {
                    merged = area + "," + city + ", " + state + ", " + pincode + "," + country;
                } else {
                    pincode = "";
                }
                if (sublocality != null) {
                    merged = sublocality + "," + area + "," + city + ", " + state + ", " + pincode + "," + country;
                } else {
                    sublocality = "";
                }
                merged = sublocality + "," + area + "," + city + "," + state + "," + pincode + "," + country;
                /*if (pickup) {
                    Marker pos_Marker = myMap.addMarker(new MarkerOptions().position(new LatLng(platitide, plongitude)).title("Pickup Location").snippet(merged).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    //pos_Marker.showInfoWindow();
                    pickup_location.setText(sublocality + "," + area + "," + city + "," + state + "," + pincode + "," + country);

                }*/

            }
        } catch (IOException | IllegalArgumentException e) {
            //  e.printStackTrace();
            Log.d("data", e.toString());
        }

        return merged;
    }

    public String getLatLongInfo(Context context, Double platitide, Double plongitude) {
        Geocoder geocoder;
        String merged = "";
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(platitide, plongitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String state = addresses.get(0).getAdminArea();
                String area = addresses.get(0).getFeatureName();
                String sublocality = addresses.get(0).getSubLocality();
                String pincode = addresses.get(0).getPostalCode();

                if (city != null) {
                    merged = city;
                } else {
                    city = "";
                }

                if (state != null) {
                    merged = city + " ," + state;
                } else {
                    state = "";
                }
                if (country != null) {
                    merged = city + " ," + state + "," + country;
                } else {
                    country = "";
                }
                if (area != null) {
                    merged = area + "," + city + ", " + state + ", " + country;
                } else {
                    area = "";
                }
                if (pincode != null) {
                    merged = area + "," + city + ", " + state + ", " + pincode + "," + country;
                } else {
                    pincode = "";
                }
                if (sublocality != null) {
                    merged = sublocality + "," + area + "," + city + ", " + state + ", " + pincode + "," + country;
                } else {
                    sublocality = "";
                }
                merged = sublocality + "," + area + "," + city + "," + state + "," + pincode + "," + country;
                /*if (pickup) {
                    Marker pos_Marker = myMap.addMarker(new MarkerOptions().position(new LatLng(platitide, plongitude)).title("Pickup Location").snippet(merged).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    //pos_Marker.showInfoWindow();
                    pickup_location.setText(sublocality + "," + area + "," + city + "," + state + "," + pincode + "," + country);

                }*/

            }
        } catch (IOException | IllegalArgumentException e) {
            //  e.printStackTrace();
            Log.d("data", e.toString());
        }

        return merged;
    }

    public String getLatLongInfo(Context context, LatLng latLng) {
        String merged = "";

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String state = addresses.get(0).getAdminArea();
                String area = addresses.get(0).getFeatureName();
                String sublocality = addresses.get(0).getSubLocality();
                String pincode = addresses.get(0).getPostalCode();

                if (city != null) {
                    merged = city;
                } else {
                    city = "";
                }

                if (state != null) {
                    merged = city + " ," + state;
                } else {
                    state = "";
                }
                if (country != null) {
                    merged = city + " ," + state + "," + country;
                } else {
                    country = "";
                }
                if (area != null) {
                    merged = area + "," + city + ", " + state + ", " + country;
                } else {
                    area = "";
                }
                if (pincode != null) {
                    merged = area + "," + city + ", " + state + ", " + pincode + "," + country;
                } else {
                    pincode = "";
                }
                if (sublocality != null) {
                    merged = sublocality + "," + area + "," + city + ", " + state + ", " + pincode + "," + country;
                } else {
                    sublocality = "";
                }
                merged = sublocality + "," + area + "," + city + "," + state + "," + pincode + "," + country;
                /*if (pickup) {
                    Marker pos_Marker = myMap.addMarker(new MarkerOptions().position(new LatLng(platitide, plongitude)).title("Pickup Location").snippet(merged).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    //pos_Marker.showInfoWindow();
                    pickup_location.setText(sublocality + "," + area + "," + city + "," + state + "," + pincode + "," + country);

                }*/

            }
        } catch (IOException | IllegalArgumentException e) {
            //  e.printStackTrace();
            Log.d("data", e.toString());
        }

        return merged;
    }

    public String getFormattedAddress(double lat, double lng) throws IOException, JSONException {
        URL url = null;
        String full = "";
        String formatted_address = "";

        url = new URL("http://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=false");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(DEFAULT_MAX_CONNECTIONS);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        BufferedReader reader = null;
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return "";
            }

            Log.d("Test", buffer.toString());
        }
        JSONObject jsonObject = new JSONObject(full);
        formatted_address = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
        Log.e("test", "formattted address:" + formatted_address);

        return formatted_address;
    }

    public LatLng getLatLong(String address1) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_MAX_CONNECTIONS);
        try {
            address1 = address1.replaceAll(" ", "%20");
            client.post("http://maps.google.com/maps/api/geocode/json?address=" + address1 + "&sensor=false", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());

                        try {
                            latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            longitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");

                        } catch (JSONException e) {
                            Log.d("catch", e.toString());

                        }


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Log.d("catch", e.toString());

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("fail", responseString);

                }
            });
        } catch (Exception e) {
            Log.d("catch", e.toString());
        }


        return new LatLng(latitude, longitude);
    }

    public String getAddress(double lat, double lng) {
        String formattedAddress = null;
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=false");


            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                stringBuilder.append((char) data);
                System.out.print(current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }


        try {

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            // JSONObject ret = getLocationInfo(lat, lng);
            JSONObject location;
            String location_string;
            try {
                //Get JSON Array called "results" and then get the 0th complete object as JSON
                location = jsonObject.getJSONArray("results").getJSONObject(0);
                // Get the value of the attribute whose name is "formatted_string"
                formattedAddress = location.getString("formatted_address");
                Log.d("test", "formattted address:" + formattedAddress);
            } catch (JSONException e1) {
                e1.printStackTrace();

            }
        } catch (
                JSONException e
                )

        {
            e.printStackTrace();
        }


        return formattedAddress;
    }

    public LatLng getLocationInfo(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            address = address.replaceAll(" ", "%20");

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            longitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new LatLng(latitude, longitude);
    }

    public boolean getLatLong(JSONObject jsonObject) {

        try {

            longitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        } catch (JSONException e) {
            return false;

        }

        return true;
    }

    public String downloadUrl(double lat, double lng) throws IOException {
        String data = "";
        String result = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL("http://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=false");
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
            try {
                JSONObject jsonObject = new JSONObject(data);
                result = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                Log.e("result", result);
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }


        } catch (Exception e) {
            Log.d("Exception ", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return result;
    }

    public String getAddress(String data) {
        String address = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            address = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

        return address;
    }


}