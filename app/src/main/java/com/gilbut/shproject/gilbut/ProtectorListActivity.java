package com.gilbut.shproject.gilbut;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.gilbut.shproject.gilbut.model.Connection;

import java.util.ArrayList;

public class ProtectorListActivity extends AppCompatActivity {

    ArrayList<Connection> arrayList;
    ProtectorListAdapter protectorListAdapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protector_list);

        Intent intent = getIntent();
        arrayList = (ArrayList)intent.getSerializableExtra("list");

        listView = (ListView)findViewById(R.id.list);

        protectorListAdapter = new ProtectorListAdapter(this,R.layout.protector_list,arrayList);
        listView.setAdapter(protectorListAdapter);
    

    }
}
