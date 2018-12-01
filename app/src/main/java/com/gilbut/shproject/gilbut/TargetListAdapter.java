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

import java.util.ArrayList;

public class TargetListAdapter extends ArrayAdapter<Connection> {
    ArrayList<Connection> list;
    Button button;
    Context context;
    public TargetListAdapter( Context context, int resource,  ArrayList<Connection> objects) {
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

        Connection connection = list.get(position);
        if(connection != null){
            TextView tid = (TextView)v.findViewById(R.id.target_id);
            TextView tpos = (TextView)v.findViewById(R.id.target_pos);


            if(tid != null){
                tid.setText(connection.tId);

            }
            if(tpos != null){
                tpos.setText(connection.location.get("latitude").toString() + ", " +connection.location.get("longitude").toString());
            }
        }

        button = (Button)v.findViewById(R.id.goto_setting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), SettingActivity.class);
                ((Activity)parent.getContext()).startActivityForResult(intent, 1);
                //Toast.makeText(context, "?", Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }
}
