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

import java.util.ArrayList;

public class TargetListAdapter extends ArrayAdapter<Target> {
    ArrayList<Target> list;
    Button button;
    Context context;
    public TargetListAdapter( Context context, int resource,  ArrayList<Target> objects) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
    }

    public View getView(int position, View view , ViewGroup parent){
        View v = view;
        if(v== null){
            LayoutInflater vi =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.target_list, null);


        }

        Target target = list.get(position);
        if(target != null){
            TextView tid = (TextView)v.findViewById(R.id.target_id);
            TextView tpos = (TextView)v.findViewById(R.id.target_pos);


            if(tid == null){
                tid.setText(target.getM_Id());

            }
            if(tpos == null){
                tpos.setText(target.getLatitude() + " + " + target.getLongitude());
            }
        }

        button = (Button)v.findViewById(R.id.goto_setting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingActivity.class);
                ((Activity)context).startActivityForResult(intent, 1);
            }
        });
        return v;
    }
}
