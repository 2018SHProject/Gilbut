package com.gilbut.shproject.gilbut;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        connectionController.addNewConnection("tfl", "p", 1, new ConnectionController.OnSetCompleteListener() {
            @Override
            public void onComplete() {
                textView.setText("성공");
                connectionController.getConnectionStatus("target1", "protector1", new ConnectionController.OnGetCompleteListener() {
                    @Override
                    public void onComplete(int status) {
                        textView.setText(String.valueOf(status));
                        Log.d("aa","aaa"+status );
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
