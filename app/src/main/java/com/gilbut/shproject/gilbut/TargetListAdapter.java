package com.gilbut.shproject.gilbut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gilbut.shproject.gilbut.model.Connection;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TargetListAdapter extends ArrayAdapter<Connection> {
    ArrayList<Connection> list;
    Button button;
    Context context;
    LatLng latLng;
    TextView tid;
    Geocoder geocoder;
    public TargetListAdapter( Context context, int resource,  ArrayList<Connection> objects) {
        super(context, resource, objects);

        this.list = objects;
        this.context = context;
        geocoder = new Geocoder(this.context, Locale.KOREA);
    }

    public View getView(int position, View view , final ViewGroup parent){
        View v = view;
        if(v== null){
            LayoutInflater vi =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.target_list, null);


        }

        final Connection connection = list.get(position);
        if(connection != null){
            tid = (TextView)v.findViewById(R.id.target_id);
            final TextView tpos = (TextView)v.findViewById(R.id.target_pos);


            if(tid != null){
                tid.setText(connection.tId);

            }
            if(tpos != null){
                Member member = new Member();
                member.getLocation(connection.tId, new Member.OnGetLocationListener() {
                    @Override
                    public void onComplete(LatLng location) throws IOException {
                        if(location != null) {
                            //tpos.setText(String.valueOf(location.latitude) + ", " + String.valueOf(location.longitude));
                            List<Address> address = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                            if(!address.isEmpty()) {
                                tpos.setText(address.get(0).getAddressLine(0).toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(String err) {

                    }
                });

            }
        }

        button = (Button)v.findViewById(R.id.goto_setting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), SettingActivity.class);
                intent.putExtra("targetId", tid.getText());
                ((Activity)parent.getContext()).startActivityForResult(intent, 1);
                //Toast.makeText(context, "?", Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }

}
