package zw.co.matrixcab.matrixcab.custom;

import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

/**
 * Created by android on 15/3/17.
 */

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
/*
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

    }


}
