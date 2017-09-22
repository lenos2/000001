package zw.co.matrixcab.matrixcab.acitivities;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import com.bumptech.glide.Glide;
import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.Server.Server;
import zw.co.matrixcab.matrixcab.custom.CheckConnection;
import zw.co.matrixcab.matrixcab.fragement.AcceptedRequestFragment;
import zw.co.matrixcab.matrixcab.fragement.CancelledFragment;
import zw.co.matrixcab.matrixcab.fragement.CompletedRideFragment;
import zw.co.matrixcab.matrixcab.fragement.HomeFragment;
import zw.co.matrixcab.matrixcab.fragement.PengingRequest;
import zw.co.matrixcab.matrixcab.fragement.ProfileFragment;
import zw.co.matrixcab.matrixcab.session.SessionManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sdsmdg.tastytoast.TastyToast;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by android on 7/3/17.
 */

public class HomeActivity extends ActivityManagePermission implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.ProfileUpdateListener, ProfileFragment.UpdateListener {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public Toolbar toolbar;
    TextView is_online, username;
    SwitchCompat switchCompat;
    LinearLayout linearLayout;
    NavigationView navigationView;
    SessionManager sessionManager;
    private ImageView avatar;

    private static final String TAG = "firebase token";

    int[][] states = new int[][]{
            new int[]{-android.R.attr.state_checked},
            new int[]{android.R.attr.state_checked},
    };

    int[] thumbColors = new int[]{
            Color.RED,
            Color.GREEN,
    };

    int[] trackColors = new int[]{
            Color.GREEN,
            Color.BLUE,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        //String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.d(TAG, "Refreshed token: " + refreshedToken);

        BindView();
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
       /* profileUpdateListener = this;
        profileUpdateListener = new ProfileFragment.ProfileUpdateListener() {
            @Override
            public void update(String url) {
                Log.e("update new", url);
            }
        };*/

    }

    private void setupDrawer() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //  globatTitle = );
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {


            //** Called when a drawer has settled in a completely open state. *//*

           /* public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
             //   getSupportActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }


            //Called when a drawer has settled in a completely closed state. *//**//**//**//*

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
               // getSupportActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu();
                // creates call to onPrepareOptionsMenu()
            }*/

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }


        };


        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        //drawer.shouldDelayChildPressedState();


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void drawer_close() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Fragment fragment = null;
        //navigationView.setCheckedItem(R.id.home);

        switch (item.getItemId()) {
            case R.id.home:
                // fontToTitleBar("Home");
                changeFragment(new HomeFragment(), "Home");
                break;
            case R.id.pending_requests:
                //  fontToTitleBar("Pending Request");
                changeFragment(new PengingRequest(), "Pending Request");
                break;
            case R.id.accepted_requests:
                // fontToTitleBar("Accepted Request");
                changeFragment(new AcceptedRequestFragment(), "Accepted Request");
                break;
            case R.id.completed_rides:
                //  fontToTitleBar("Completed Request");
                changeFragment(new CompletedRideFragment(), "Completed Request");
                break;
            case R.id.cancelled:
                //  fontToTitleBar("Cancelled Request");
                changeFragment(new CancelledFragment(), "Cancelled Request");
                break;
            case R.id.profile:
                // fontToTitleBar("Profile");
                changeFragment(new ProfileFragment(), "Profile");
                break;
            case R.id.logout:
                sessionManager.logoutUser();
                finish();
                break;
            default:
                break;
        }
        return true;
    }


    public void changeFragment(final Fragment fragment, final String fragmenttag) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawer_close();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack(null);
            }
        }, 0);

    }



    @Override
    public void update(String url) {
        if (!url.equals("")) {
            Glide.with(getApplicationContext()).load(url).error(R.drawable.images).into(avatar);
        }
    }

    @Override
    public void name(String name) {
        if (!name.equals("")) {
            username.setText(name);
        }
    }

    @SuppressLint("ParcelCreator")
    public class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }



    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        Log.e("font", font.getStyle() + "");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void fontToTitleBar(String title) {
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        title = "<font color='#000000'>" + title + "</font>";
        SpannableString s = new SpannableString(title);
        s.setSpan(font, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            toolbar.setTitle(Html.fromHtml(String.valueOf(s), Html.FROM_HTML_MODE_LEGACY));
        } else {
            toolbar.setTitle((Html.fromHtml(String.valueOf(s))));
        }
    }


    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = HomeActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void BindView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        sessionManager = new SessionManager(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        switchCompat = (SwitchCompat) navigationView.getHeaderView(0).findViewById(R.id.online);
        avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile);
        linearLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.linear);
        is_online = (TextView) navigationView.getHeaderView(0).findViewById(R.id.is_online);
        username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
        navigationView.setCheckedItem(R.id.home);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.home));
        setupDrawer();
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        username.setTypeface(font);
        toolbar.setTitle("");

        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            getUserInfo();

        } else {
            TastyToast.makeText(HomeActivity.this, "network is not available", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

            SessionManager sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            if (user != null) {
                String name = user.get(SessionManager.KEY_NAME);
                String url = user.get(SessionManager.AVATAR);
                username.setText(name);
                Glide.with(getApplicationContext()).load(url).error(R.mipmap.ic_account_circle_black_24dp).into(avatar);

            }

        }
    }


    public void getUserInfo() {

        RequestParams params = new RequestParams();
        if (sessionManager == null) {
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            if (user != null) {
                String uid = user.get(SessionManager.USER_ID);
                params.put("user_id", uid);
                Server.setHeader(sessionManager.getKEY());
            }
        } else {
            HashMap<String, String> user = sessionManager.getUserDetails();
            if (user != null) {
                String uid = user.get(SessionManager.USER_ID);
                params.put("user_id", uid);
                Server.setHeader(sessionManager.getKEY());
            }
        }
        Server.get("api/user/profile/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                        String name = response.getJSONObject("data").getString("name");
                        String email = response.getJSONObject("data").getString("email");
                        String user_id = response.getJSONObject("data").getString("user_id");
                        String url = response.getJSONObject("data").getString("avatar");
                        String mobile = response.getJSONObject("data").getString("mobile");
                        sessionManager.createLoginSession(name, email, user_id, url, mobile);

                        Glide.with(HomeActivity.this).load(url).error(R.drawable.images).into(avatar);
                        username.setText(name);


                    } else {
                        //  progressDialog.cancel();

                    }
                } catch (JSONException e) {
                    Log.d("catch", e.toString());
                    // progressDialog.cancel();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("fail", responseString);
                // progressDialog.cancel();

            }
        });

    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawer_close();
        } else {
            super.onBackPressed();
            Fragment fragment = getVisibleFragment();
            if (fragment != null) {
                fontToTitleBar(fragment.getTag());
            } else {
                super.onBackPressed();
            }
        }
    }


}
