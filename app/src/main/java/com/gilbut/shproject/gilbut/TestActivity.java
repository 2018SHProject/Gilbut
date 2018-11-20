package com.gilbut.shproject.gilbut;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gilbut.shproject.gilbut.model.Connection;

import java.util.concurrent.ExecutionException;

public class TestActivity extends AppCompatActivity {
    TextView textView;
    Button btn;
    ConnectionController connectionController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        textView = (TextView) findViewById(R.id.textview);
        btn = (Button) findViewById(R.id.btn);
    }
    public void btnClicked(View v) throws ExecutionException, InterruptedException {
        connectionController = new ConnectionController();

        textView.setText("잠시만 기다려주세요");
        connectionController.addNewConnection("target1", "protector1", 1, new ConnectionController.OnSetCompleteListener() {
            @Override
            public void onComplete() {
                textView.setText("성공");
                connectionController.getConnection("target1", "protector1", new ConnectionController.OnGetConnectionListener() {
                    @Override
                    public void onComplete(Connection connection) {
                        textView.setText(connection.tId);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }
        });

        connectionController.updateConnectionStatus("tf", "p", 2, new ConnectionController.OnSetCompleteListener() {
            @Override
            public void onComplete() {

            }
        });

    }


}
