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
    Context context;
    TextView pid;
    TextView status;

    public ProtectorListAdapter(Context context, int resource, ArrayList<Connection> objects) {
        super(context, resource, objects);

        this.list = objects;
        this.context = context;
    }

    public View getView(int position, View view, final ViewGroup parent) {
        View v = view;
        if (v == null) {
            LayoutInflater vi =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.protector_list, null);


        }

        final Connection connection = list.get(position);
        if (connection != null) {
            pid = (TextView) v.findViewById(R.id.pId);
            status = (TextView) v.findViewById(R.id.status);

            if (pid != null) {
                pid.setText(connection.pId);
            }

            long checkStatus = connection.status;

            if (checkStatus == 1) {
                status.setText("연결됨");
            } else if (checkStatus == 0) {
                status.setText("수락 대기중");
            } else if (checkStatus == -1) {
                status.setText("연결 거절");
            } else {
                status.setText("err");
            }
        }
        return v;
    }
}

