package zw.co.matrixcab.matrixcab.acitivities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import zw.co.matrixcab.matrixcab.R;

import zw.co.matrixcab.matrixcab.Server.Server;
import zw.co.matrixcab.matrixcab.custom.CheckConnection;
import zw.co.matrixcab.matrixcab.custom.SetCustomFont;
import zw.co.matrixcab.matrixcab.session.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sdsmdg.tastytoast.TastyToast;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends ActivityManagePermission {
    private static final String TAG = "login";
    RelativeLayout relative_register;
    EditText input_email, input_password;
    AppCompatButton login;
    SessionManager sessionManager;
    TextView as, txt_createaccount, forgot_password;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        bindView();
        applyfonts();
        //AskPermission();


        relative_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    // login user

                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                        login(input_email.getText().toString().trim(), input_password.getText().toString().trim());
                    } else {
                        TastyToast.makeText(getApplicationContext(), "Network is not available", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                    }

                } else {
                    // do nothing
                }
            }
        });

        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] params) {
                token= FirebaseInstanceId.getInstance().getToken();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                sessionManager.setGcmToken(token);
            }
        }.execute();

    }

    public void bindView() {
        as = (TextView) findViewById(R.id.as);
        txt_createaccount = (TextView) findViewById(R.id.txt_createaccount);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        relative_register = (RelativeLayout) findViewById(R.id.relative_register);
        login = (AppCompatButton) findViewById(R.id.login);
        sessionManager = new SessionManager(getApplicationContext());
        forgot_password = (TextView) findViewById(R.id.txt_forgotpassword);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                    changepassword_dialog();
                } else {
                    TastyToast.makeText(getApplicationContext(), "Network is not available", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                }
            }
        });


    }

    public Boolean validate() {
        Boolean value = true;
        if (input_email.getText().toString().equals("") && !android.util.Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString().trim()).matches()) {
            value = false;
            input_email.setError("email is invalid");
        } else {
            input_email.setError(null);
        }

        if (input_password.getText().toString().trim().equalsIgnoreCase("")) {
            value = false;
            input_password.setError("field is required");
        } else {
            input_password.setError(null);
        }
        return value;
    }

    public void applyfonts() {
        if (getCurrentFocus() != null) {
            SetCustomFont setCustomFont = new SetCustomFont();
            setCustomFont.overrideFonts(getApplicationContext(), getCurrentFocus());
        } else {
            Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
            Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
            input_email.setTypeface(font1);
            input_password.setTypeface(font1);
            login.setTypeface(font);
            txt_createaccount.setTypeface(font);
            forgot_password.setTypeface(font);
        }


    }

    public void login(String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(true);
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("utype", "0");
        params.put("gcm_token", token);

        Server.post("user/login/format/json", params, new JsonHttpResponseHandler() {
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
                Log.d("success", response.toString());
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        SessionManager sessionManager = new SessionManager(LoginActivity.this);

                        String key = response.getJSONObject("data").getString("key");
                        sessionManager.setKEY(key);

                        String name = response.getJSONObject("data").getString("name");
                        String email = response.getJSONObject("data").getString("email");
                        String user_id = response.getJSONObject("data").getString("user_id");
                        String avatar = response.getJSONObject("data").getString("avatar");
                        String mobile = response.getJSONObject("data").getString("mobile");

                        sessionManager.createLoginSession(name, email, user_id, avatar, mobile);

                        progressDialog.dismiss();
                        TastyToast.makeText(LoginActivity.this, "success", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        TastyToast.makeText(LoginActivity.this, response.getString("data"), TastyToast.LENGTH_LONG, TastyToast.ERROR);

                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    TastyToast.makeText(LoginActivity.this, "Error occured", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();
                TastyToast.makeText(LoginActivity.this, "Error occured", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        });


    }

    public void resetPassword(String email, final Dialog dialog) {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(false);
        RequestParams params = new RequestParams();
        params.put("email", email);
        Server.post("user/forgot_password/format/json", params, new JsonHttpResponseHandler() {
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
                        String data = response.getString("data");
                        progressDialog.cancel();
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        TastyToast.makeText(LoginActivity.this, data, TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();

                    } else {
                        progressDialog.cancel();
                        String data = response.getString("data");
                        TastyToast.makeText(LoginActivity.this, data, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();


                    }
                } catch (JSONException e) {
                    progressDialog.cancel();
                    TastyToast.makeText(LoginActivity.this, "Error occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.cancel();
                TastyToast.makeText(LoginActivity.this, "Error occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

            }
        });


    }

    public void changepassword_dialog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.password_reset);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        final EditText email = (EditText) dialog.findViewById(R.id.input_email);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView message = (TextView) dialog.findViewById(R.id.message);

        AppCompatButton btn_change = (AppCompatButton) dialog.findViewById(R.id.btn_reset);
        AppCompatButton btn_cancel = (AppCompatButton) dialog.findViewById(R.id.btn_cancel);

        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        btn_change.setTypeface(font1);
        btn_cancel.setTypeface(font1);
        email.setTypeface(font);
        title.setTypeface(font);
        message.setTypeface(font);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LoginActivity.this.getCurrentFocus();
                if (view != null) {
                    CheckConnection.hideKeyboard(LoginActivity.this, view);
                }
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                    //dialog.cancel();
                    resetPassword(email.getText().toString().trim(), dialog);

                } else {
                    email.setError("email is not valid");
                    // TastyToast.makeText(LoginActivity.this, "email is invalid", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                }


            }
        });
        dialog.show();

    }
}
