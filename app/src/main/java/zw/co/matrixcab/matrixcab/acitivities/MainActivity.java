package zw.co.matrixcab.matrixcab.acitivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.session.SessionManager;

/**
 * Created by android on 7/3/17.
 */

public class MainActivity extends Activity {
    Button as_driver;
    RelativeLayout as_customer;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    int READ_STORAGE = 1;
    int WRITE_STORAGE = 1;
    int LOCATION = 2;
    int CAMERA = 3;
    TextView txt_customer;
    int COARSE = 4;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        as_driver = (Button) findViewById(R.id.as_driver);
        as_customer = (RelativeLayout) findViewById(R.id.as_customer);
        applyfonts();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
       /* as_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.createLoginSession("driver", "driver@gmail.com", "driver");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("as", "Login as a Driver");
                startActivity(new Intent(intent));
            }
        });
        as_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.createLoginSession("customer", "customer@gmail.com", "customer");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("as", "Login as a Customer");
                startActivity(intent);
            }
        });*/
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("permission", grantResults.length + " ");
            }

        }
    }

    public void applyfonts() {
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        as_driver.setTypeface(font);
        txt_customer = (TextView) findViewById(R.id.txt_customer);
        txt_customer.setTypeface(font);

    }
}


