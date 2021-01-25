package io.figured.offlinetimeupdater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Button pullTimebtn, stopbtn, setTimebtn;
    private Context context = getBaseContext();
    private TextView timeText;
    private boolean isCancelled;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    // private Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    private static final String VALUE = "value";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // startActivity(i);
        context = getBaseContext();
        pullTimebtn = findViewById(R.id.pullTimebtn);
//        stopbtn = findViewById(R.id.stopbtn);
        setTimebtn = findViewById(R.id.setTimebtn);
        timeText = findViewById(R.id.timeText);
        isCancelled = false;
        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

        setTimebtn.setEnabled(false);


        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            return;
        }


//        if (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            Toast.makeText(context, "Permission for location must be granted", Toast.LENGTH_SHORT).show();
//
//            // Permission is not granted
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                // No explanation needed; request the permission
//                ActivityCompat.requestPermissions(MainActivity.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else {
//            // Permission has already been granted
//        }

        pullTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCancelled = false;
                InitialiseLocationListener(context);
            }
        });

//        stopbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isCancelled = true;
//                Toast.makeText(MainActivity.this, "Pushed Stop", Toast.LENGTH_LONG).show();
//            }
//        });

        setTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnValue(timeText);
                Toast.makeText(MainActivity.this, "Pushed set time", Toast.LENGTH_LONG).show();
            }
        });



    }

    public void InitialiseLocationListener(android.content.Context context) {

        android.location.LocationManager locationManager = (android.location.LocationManager)
                context.getSystemService(android.content.Context.LOCATION_SERVICE);

        android.location.LocationListener locationListener = new android.location.LocationListener() {

            public void onLocationChanged(android.location.Location location) {

                String time = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.'000'X").format(location.getTime());


                if( location.getProvider().equals(android.location.LocationManager.GPS_PROVIDER)) {
                    timeText.setText(time);
                    android.util.Log.d("Location", "Time GPS: " + time); // This is what we want!
                    setTimebtn.setEnabled(true);

                } else {
                    timeText.setText("Getting Signal - Stand outside under the sky");
/*                    timeText.setText("Time Device (" + location.getProvider() + "): " + time);
                    android.util.Log.d("Location", "Time Device (" + location.getProvider() + "): " + time);*/
                    setTimebtn.setEnabled(false);
                }

            }

            public void onStatusChanged(String provider, int status, android.os.Bundle extras) {
            }
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) {
            }
        };

        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            android.util.Log.d("Location", "Incorrect 'uses-permission', requires 'ACCESS_FINE_LOCATION'");
            return;
        }

        locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

        if (isCancelled) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public void returnValue(View view) {
        String savedTime = timeText.getText().toString();

        Intent intent = new Intent();
        intent.putExtra(VALUE, savedTime);
        setResult(RESULT_OK, intent);
        finish();
    }

}
