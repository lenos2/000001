package zw.co.matrixcab.matrixcab.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.acitivities.HomeActivity;
import zw.co.matrixcab.matrixcab.custom.SetCustomFont;
import zw.co.matrixcab.matrixcab.session.SessionManager;

/**
 * Created by android on 16/3/17.
 */

public class CompletedDetailFragment extends Fragment {
    private View view;

    private String pickup = "";
    private String drop = "";
    private String driver = "";
    private String basefare = "";
    private String mobile = "";
    private String paymnt_status = "";
    TextView title, drivername, mobilenumber, pickup_location, drop_location, fare,payment_status;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.completedrequest_detail, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar("Passenger Information");
        BindView();
        return view;

    }

    public void BindView() {
        title = (TextView) view.findViewById(R.id.title);
        drivername = (TextView) view.findViewById(R.id.txt_drivername);
        mobilenumber = (TextView) view.findViewById(R.id.txt_mobilenumber);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickuplocation);
        drop_location = (TextView) view.findViewById(R.id.txt_droplocation);
        payment_status = (TextView) view.findViewById(R.id.txt_paymentstatus);
        fare = (TextView) view.findViewById(R.id.txt_basefare);
        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        sessionManager = new SessionManager(getActivity());
        Bundle bundle = getArguments();

        if (bundle != null) {
            title.setText("TAXI");
            pickup = bundle.getString("from_add");
            drop = bundle.getString("to_add");
            driver = bundle.getString("customername");
            basefare = bundle.getString("fare");
            mobile = bundle.getString("mobile");
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

        }
        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view);


    }
}
