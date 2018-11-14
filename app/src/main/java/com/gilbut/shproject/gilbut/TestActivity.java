package com.gilbut.shproject.gilbut;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gilbut.shproject.gilbut.models.Connection;

import java.util.concurrent.ExecutionException;

public class TestActivity extends AppCompatActivity {
    FirestoreHandler firestoreHandler;
    TextView textView;
    Button btn;
    Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        firestoreHandler = new FirestoreHandler();
        textView = (TextView) findViewById(R.id.textview);
        btn = (Button) findViewById(R.id.btn);
    }
    public void btnClicked(View v) throws ExecutionException, InterruptedException {
        firestoreHandler.setConnectionFirestoreListener(new FirestoreHandler.ConnectionFirestoreListener() {
            @Override
            public void onReceivedData(Connection newConnection) {
                connection = newConnection;
                Log.d("no", "nn"+newConnection);
                textView.setText(Integer.toString(connection.getConnection()));
            }
        });
        firestoreHandler.getConnection("target1", "protector1");

    }
}
