package Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.abdull.scorebatao.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

import pojo.localdata;

/**
 * Created by abdull on 7/8/17.
 */

public class personalDetailAdapter extends ArrayAdapter {
    ArrayList liveMatches = new ArrayList();
    Context context;

    public personalDetailAdapter(@NonNull Context context, @LayoutRes int resource,  @NonNull ArrayList live) {
        super(context, resource, live);
        this.liveMatches=live;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= LayoutInflater.from(getContext()).inflate(R.layout.match_info_user,parent,false);
        localdata localdata= (pojo.localdata) liveMatches.get(position);
        TextView name= (TextView) view.findViewById(R.id.nameID);
        TextView phonenumber= (TextView) view.findViewById(R.id.phoneID);
        TextView update= (TextView) view.findViewById(R.id.updateID);
        TextView updateRequest= (TextView) view.findViewById(R.id.updateRequest);
        TextView status= (TextView) view.findViewById(R.id.status);

        name.setText((CharSequence) localdata.getName());
        phonenumber.setText((CharSequence) localdata.getPhonenumber());
        update.setText((CharSequence) localdata.getUpdate());
        updateRequest.setText((CharSequence) localdata.getRequest());
        status.setText((CharSequence) localdata.getStatus());








        return view;
    }
}
