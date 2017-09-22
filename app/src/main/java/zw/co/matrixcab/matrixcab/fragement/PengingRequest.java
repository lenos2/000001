package zw.co.matrixcab.matrixcab.fragement;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.Server.Server;
import zw.co.matrixcab.matrixcab.acitivities.HomeActivity;
import zw.co.matrixcab.matrixcab.adapter.PendingRequestAdapter;
import zw.co.matrixcab.matrixcab.custom.CheckConnection;
import zw.co.matrixcab.matrixcab.pojo.PendingRequestPojo;
import zw.co.matrixcab.matrixcab.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sdsmdg.tastytoast.TastyToast;


import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by android on 8/3/17.
 */

public class PengingRequest extends Fragment {

    RecyclerView recyclerView;
    View view;
    ProgressDialog progressDialog;
    String userid = "";
    String key = "";
    SessionManager sessionManager;
    TextView txt_error;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pending_request, container, false);

        ((HomeActivity) getActivity()).fontToTitleBar("Pending Request");
        bindView();
        if (CheckConnection.haveNetworkConnection(getActivity())) {
            getPendingRequest(userid, "PENDING", key);
        } else {
            TastyToast.makeText(getActivity(), "network is not available", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
        }


        return view;
    }

    public void bindView() {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        txt_error = (TextView) view.findViewById(R.id.txt_error);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(true);
        sessionManager = new SessionManager(getActivity());
        if (sessionManager != null) {
            HashMap<String, String> users = sessionManager.getUserDetails();
            if (users != null) {
                String id = users.get(SessionManager.USER_ID);
                if (id != null) {
                    userid = id;
                }
                String k = sessionManager.getKEY();
                if (k != null) {
                    key = k;
                }

            }
        }

    }


    public void getPendingRequest(String id, String status, String key) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("status", status);
        Server.setHeader(key);
        Server.get("api/user/rides/format/json", params, new JsonHttpResponseHandler() {
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
                    Gson gson = new GsonBuilder().create();

                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());

                        if (response.has("data") && response.getJSONArray("data").length() == 0) {
                            txt_error.setVisibility(View.VISIBLE);

                        } else {
                            PendingRequestAdapter pendingRequestAdapter = new PendingRequestAdapter(list);
                            recyclerView.setAdapter(pendingRequestAdapter);
                            pendingRequestAdapter.notifyDataSetChanged();
                        }
                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        TastyToast.makeText(getActivity(), "error occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();


                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Log.e("catch", e.toString());
                    TastyToast.makeText(getActivity(), "error occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();
                TastyToast.makeText(getActivity(), "error occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
            }
        });
    }

}
