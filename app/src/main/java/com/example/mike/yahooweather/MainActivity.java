package com.example.mike.yahooweather;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 0;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSION_INTERNET = 2;
    private static LocationListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    MY_PERMISSION_ACCESS_FINE_LOCATION );
        }

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.INTERNET ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.INTERNET  },
                    MY_PERMISSION_INTERNET );
        }

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.INTERNET ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        /*
        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Previously mock location is cleared.
                // getLastKnownLocation(LocationManager.GPS_PROVIDER); will not return mock location.
            }
        };
        */

        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        //Test Coordinates for New York City, NY
        //longitude = -74.0059;
        //latitude = 40.7128;

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String data = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + Double.toString(latitude) + "," + Double.toString(longitude) + "&key=AIzaSyAi9bRJzDHsfgwA08E42r5DCzh3nNerkno";


        TestAsyncTask testAsyncTask = new TestAsyncTask(MainActivity.this, data, longitude, latitude);
        testAsyncTask.execute();

    }

    public class TestAsyncTask extends AsyncTask<Void, Void, String> {
        private Context mContext;
        private String mUrl;
        TextView t;
        Weather weatherForcast;

        public TestAsyncTask(Context context, String url, double longitude, double latitude) {
            weatherForcast = new Weather();
            mContext = context;
            mUrl = url;
            weatherForcast.setLongitude(longitude);
            weatherForcast.setLatitude(latitude);
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultString = null;
            String newURL = getLocation(getJSON(mUrl));
            getWeather(getJSON(newURL));
            return resultString;
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            //set the views here
            updateViews();
        }

        public String getLocation(String rawJSON)
        {
            //parse json for location

            String locality = null;
            String state = null;

            //Parses the JSON
            try {
                //Converts JSON String into a JSON object to be parsed
                JSONObject json = new JSONObject(rawJSON);

                if(json.get("status") != "OK")
                {
                    //abort mission
                }

                JSONArray geoResults = json.getJSONArray("results");

                //Iterates through the contents of the JSON array
                for(int i = 0; i < geoResults.length() && locality == null; i++)
                {
                    JSONArray tempArray = geoResults.getJSONObject(i).getJSONArray("address_components");
                    for(int n = 0; n < tempArray.length(); n++)
                    {
                        for(int x = 0; x < tempArray.getJSONObject(n).getJSONArray("types").length(); x++)
                        {
                            String wtf = tempArray.getJSONObject(n).getJSONArray("types").getString(x);
                            if (tempArray.getJSONObject(n).getJSONArray("types").getString(x).equals("locality")) {
                                locality = tempArray.getJSONObject(n).get("short_name").toString();
                            }
                            if (tempArray.getJSONObject(n).getJSONArray("types").getString(x).equals("administrative_area_level_1")) {
                                state = tempArray.getJSONObject(n).get("short_name").toString();
                            }
                        }
                    }
                    if(locality == null)
                    {
                        state = null;
                    }
                    if(state == null)
                    {
                        locality = null;
                    }
                }
            }
            catch (JSONException e){
                //If there is an issue with parsing the JSON, throws an exception
                throw new RuntimeException(e);
            }

            if(locality == null || state == null)
            {
                //catch issue here
                return "null data error";
            }

            locality = locality.replace(" ", "%20");
            weatherForcast.setLocation(locality + ", " + state);
            String woeid = locality + "%2C%20" + state;
            String newUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" +
                    woeid + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
            return newUrl;
        }

        public void getWeather(String rawJSON)
        {
            //parse json for weather details
            try {
                //Converts JSON String into a JSON object to be parsed
                JSONObject json = new JSONObject(rawJSON);
                json = json.getJSONObject("query");
                json = json.getJSONObject("results");
                json = json.getJSONObject("channel");

                weatherForcast.setTempUnit(json.getJSONObject("units").getString("temperature"));
                weatherForcast.setHumidity(json.getJSONObject("atmosphere").getString("humidity"));

                json = json.getJSONObject("item");
                //title info?

                json = json.getJSONObject("condition");

                weatherForcast.setTemp(json.getString("temp"));
                weatherForcast.setSkyCondition(json.get("text").toString());
            }
            catch (JSONException e){
                //If there is an issue with parsing the JSON, throws an exception
                throw new RuntimeException(e);
            }
        }

        public void updateViews()
        {
            ListView weatherListView = (ListView) findViewById(R.id.weatherListView);
            List<String> data = new ArrayList<String>();
            data.add(weatherForcast.getLocation().replace("%20", " "));
            data.add("Temp: " + (weatherForcast.getTempUnit() == "F" ? weatherForcast.getTemp() + " \u2103" : weatherForcast.getTemp() + " \u2109"));
            data.add("Humidity: " + weatherForcast.getHumidity() + "%");
            data.add("Conditions: " + weatherForcast.getSkyCondition());
            data.add("Coordinates: " + weatherForcast.getLatitude() + ", " + weatherForcast.getLongitude());
            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mContext, R.layout.datarow, data);


            weatherListView.setAdapter(listAdapter);
        }

        public String getJSON(String url) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.connect();
                int status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (Exception ex) {
                return ex.toString();
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        //disconnect error
                    }
                }
            }
            return null;
        }
    }


}
