package zw.co.matrixcab.matrixcab.adapter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import zw.co.matrixcab.matrixcab.R;
import zw.co.matrixcab.matrixcab.acitivities.HomeActivity;
import zw.co.matrixcab.matrixcab.fragement.AcceptedDetailFragment;
import zw.co.matrixcab.matrixcab.fragement.CompletedDetailFragment;
import zw.co.matrixcab.matrixcab.pojo.PendingRequestPojo;

/**
 * Created by android on 8/3/17.
 */

public class CompletedRequestAdapter extends RecyclerView.Adapter<CompletedRequestAdapter.Holder> {
    List<PendingRequestPojo> list;

    public CompletedRequestAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.completedrequest_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        holder.from_add.setText(list.get(holder.getAdapterPosition()).getPickup_adress());
        holder.to_add.setText(list.get(holder.getAdapterPosition()).getDrop_address());
        holder.drivername.setText(list.get(holder.getAdapterPosition()).getDriver_name());

        try {
            SimpleDateFormat toFullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fullDate = toFullDate.parse(list.get(holder.getAdapterPosition()).getTime());
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss a");
            String shortTime = time.format(fullDate);
            SimpleDateFormat date = new SimpleDateFormat("dd-MMMM-yyyy");

            String shortTimedate = date.format(fullDate);
            holder.time.setText(shortTime);
            holder.date.setText(shortTimedate);


            SimpleDateFormat sdf = new SimpleDateFormat("d'%s' MMM, yyyy");
            // String myDate = String.format(sdf.format(date), dateSuffix();
            // int day = Calendar.getInstance().setTime(fullDate).get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            // To change body of catch statement use File | Settings | File Templates.
            Log.e("catch", e.toString());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                bundle.putString("from_add", list.get(holder.getAdapterPosition()).getPickup_adress());
                bundle.putString("to_add", list.get(holder.getAdapterPosition()).getDrop_address());
                bundle.putString("drivername", list.get(holder.getAdapterPosition()).getDriver_name());
                bundle.putString("fare", list.get(holder.getAdapterPosition()).getAmount());
                bundle.putString("mobile", list.get(holder.getAdapterPosition()).getDriver_mobile());
                bundle.putString("payment_status",list.get(holder.getAdapterPosition()).getPayment_status());
                bundle.putString("request","completed");
                AcceptedDetailFragment completedDetailFragment = new AcceptedDetailFragment();
                completedDetailFragment.setArguments(bundle);
                ((HomeActivity) holder.itemView.getContext()).changeFragment(completedDetailFragment, "Passenger Information");
            }
        });
        BookFont(holder, holder.f);
        BookFont(holder, holder.t);
        BookFont(holder, holder.dn);
        BookFont(holder, holder.dt);

        MediumFont(holder, holder.date);

        MediumFont(holder, holder.from_add);
        MediumFont(holder, holder.to_add);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView drivername, from_add, to_add, date, time;
        TextView f, t, dn, dt;

        Holder(View itemView) {
            super(itemView);

            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);

            dn = (TextView) itemView.findViewById(R.id.drivername);
            dt = (TextView) itemView.findViewById(R.id.datee);

            drivername = (TextView) itemView.findViewById(R.id.txt_drivername);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }

    private void BookFont(Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    private void MediumFont(Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }
}
