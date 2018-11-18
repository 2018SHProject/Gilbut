package com.gilbut.shproject.gilbut;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class TestActivity extends AppCompatActivity {
    FirestoreController firestoreController;
    TextView textView;
    Button btn;
    Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        firestoreController = new FirestoreController();
        textView = (TextView) findViewById(R.id.textview);
        btn = (Button) findViewById(R.id.btn);
    }
    public void btnClicked(View v) throws ExecutionException, InterruptedException {
        textView.setText("잠시만 기다려주세요");
        firestoreController.setOnDatabaseGetEventListener(new FirestoreController.OnDatabaseGetEventListener() {
            @Override
            public void onGetConnectionSuccess(Connection newData) {
                textView.setText(newData.getTarget_id());
                Log.d("되라되리되", newData.getTarget_id());
            }

            @Override
            public void onGetConnectionFailure() {

            }
        });

        firestoreController.getConnection("target1", "protector1");

        firestoreController.setDatabaseSetEventListener(new FirestoreController.OnDatabaseSetEventListener() {
            @Override
            public void onSetConnectionSuccess() {
                Log.d("set 성공!!", "set 성공!!");
            }

            @Override
            public void onSetConnectionFailure() {
                Log.e("fail", "실패.");
            }
        });

        firestoreController.updateStatus("target1", "protector1", -1);
    }


}
