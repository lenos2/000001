package zw.co.matrixcab.matrixcab.fragement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.Server.Server;
import zw.co.matrixcab.matrixcab.acitivities.HomeActivity;
import zw.co.matrixcab.matrixcab.custom.SetCustomFont;
import zw.co.matrixcab.matrixcab.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by android on 10/3/17.
 */

public class PendingDetailFragment extends Fragment {
    private View view;

    private String ride_id = "";

    TextView title, drivername, mobilenumber, pickup_location, drop_location, fare,payment_status;
    SessionManager sessionManager;
    AppCompatButton btn_cancel;
    private AlertDialog alert;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.pendingrequest_detail, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar("Passenger Information");
        BindView();
        return view;

    }

    private void BindView() {
        String mobile = "";
        String pickup = "";
        String drop = "";
        String driver = "";
        String basefare = "";
         String paymnt_status = "";
        title = (TextView) view.findViewById(R.id.title);
        drivername = (TextView) view.findViewById(R.id.txt_drivername);
        mobilenumber = (TextView) view.findViewById(R.id.txt_mobilenumber);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickuplocation);
        drop_location = (TextView) view.findViewById(R.id.txt_droplocation);
        fare = (TextView) view.findViewById(R.id.txt_fare);
        //   btn_accept = (AppCompatButton) view.findViewById(R.id.btn_accept);
        btn_cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        payment_status = (TextView) view.findViewById(R.id.txt_paymentstatus);
        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        sessionManager = new SessionManager(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            title.setText("TAXI");

            pickup = bundle.getString("from_add");
            drop = bundle.getString("to_add");
            driver = bundle.getString("drivername");
            basefare = bundle.getString("fare");
            mobile = bundle.getString("mobile");
            ride_id = bundle.getString("ride_id");
            paymnt_status = bundle.getString("payment_status");

            if (pickup != null) {
                pickup_location.setText(pickup);
            }
            if (drop != null) {
                drop_location.setText(drop);
            }
            if (driver != null) {
                drivername.setText(driver);
            }
            if (fare != null) {
                fare.setText(basefare + " $");
            }
            if (mobile != null) {
                mobilenumber.setText(mobile);
            }
            if (paymnt_status != null && !paymnt_status.equals("")) {
                payment_status.setText(paymnt_status);
            } else {
                payment_status.setText("UNPAID");
            }
            if (ride_id != null) {

            } else {
                ride_id = "";
            }

        }
        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialogCreate("RIDE CANCELLATION", "Do you want to cancel your pending ride?", "CANCELLED");
            }
        });

    }

    public void sendStatus(String ride_id, String status) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(true);
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);
        SessionManager sessionManager = new SessionManager(getActivity());
        Server.setHeader(sessionManager.getKEY());
        Server.setContetntType();
        Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
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
                    if (response.has("status") && response.getString("status").equals("success")) {
                        progressDialog.dismiss();
                        TastyToast.makeText(getActivity(), "ride request cancelled", TastyToast.SUCCESS, TastyToast.LENGTH_LONG).show();
                        ((HomeActivity) getActivity()).changeFragment(new PengingRequest(), "Pending Request");
                    } else {
                        progressDialog.dismiss();
                        String error = response.getString("data");
                        TastyToast.makeText(getActivity(), error, TastyToast.ERROR, TastyToast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    TastyToast.makeText(getActivity(), "error occurred", TastyToast.ERROR, TastyToast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
                TastyToast.makeText(getActivity(), "error occurred", TastyToast.ERROR, TastyToast.LENGTH_LONG).show();
            }
        });
    }

    public void AlertDialogCreate(String title, String message, final String status) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new AlertDialog.Builder(getActivity())
                .setIcon(drawable)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sendStatus(ride_id, status);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }


}
