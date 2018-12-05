package com.gilbut.shproject.gilbut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gilbut.shproject.gilbut.model.Connection;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ProtectorListAdapter extends ArrayAdapter<Connection> {
    ArrayList<Connection> list;
    Button button;
    Context context;
    LatLng latLng;
    TextView tid;
    public ProtectorListAdapter( Context context, int resource,  ArrayList<Connection> objects) {
        super(context, resource, objects);

        this.list = objects;
        this.context = context;
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
                    public void onComplete(LatLng location) {
                        if(location != null)
                            latLng = location;
                        tpos.setText(String.valueOf(location.latitude) + ", " +String.valueOf(location.longitude));
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
