package com.gilbut.shproject.gilbut;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        //추가 예시
//        connectionController.addNewConnection("target1", "protector1", 1, new ConnectionController.OnSetCompleteListener() {
//            @Override
//            public void onComplete() {
//                textView.setText("성공");
//
//                //GetConection 예시
//                connectionController.getConnection("target1", "protector1", new ConnectionController.OnGetConnectionListener() {
//                    @Override
//                    public void onComplete(Connection connection) {
//                        textView.setText(connection.tId);
//                    }
//
//                    @Override
//                    public void onFailure() {
//
//                    }
//                });
//            }
//        });

        //업데이트 예시
//        connectionController.updateConnectionStatus("tf", "p", 2, new ConnectionController.OnSetCompleteListener() {
//            @Override
//            public void onComplete() {
//
//            }
//        });

        //getAlarm 에시
//        connectionController.getAlarm("target1", "protector1", new ConnectionController.OnGetAlarmListener() {
//            @Override
//            public void onComplete(boolean status) {
//                textView.setText(String.valueOf(status));
//            }
//
//            @Override
//            public void onFailure() {
//
//            }
//        });

        // getConnection 타겟만 있을 떄 예시
//        connectionController.getConnection("fff", new ConnectionController.OnGetConnectionListener() {
//            @Override
//            public void onComplete(Connection connection) {
//                textView.setText(connection.pId);
//            }
//
//            @Override
//            public void onFailure(String err) {
//                textView.setText(err);
//            }
//        });
//
//         progector id로 연결 찾기. + range 받아오기
//        connectionController.getConnections("protector1", new ConnectionController.OnGetConnectionsListener() {
//            @Override
//            public void onComplete(List<Connection> connections) {
//                String str ="";
//                for(Connection connection : connections){
//                    str = str + " " + connection.location;
//                      // range 받아오기
////                    RangeController range = new RangeController(connection.rangeRef, new RangeController.OnGetRangeListener() {
////                        @Override
////                        public void onComplete(List<LatLng> range) {
////                            textView.setText(""+range.get(0).latitude);
////                        }
////
////                        @Override
////                        public void onFailure(String err) {
////
////                        }
////                    });
//                    textView.setText(str);
//                }
//
//            }
//
//            @Override
//            public void onFailure(String err) {
//
//            }
//        });

        // RangeController setting 예시
        RangeController rangeController;
//        ArrayList<Location> newRange = new ArrayList<Location>();
//        newRange.add(new Location(34.555, 188.444));
//        newRange.add(new Location(33.555, 188.444));
//        newRange.add(new Location(37.555, 180.9));
//        rangeController.addRange(newRange, new RangeController.OnSetRangeListener() {
//            @Override
//            public void onComplete(String rangeRef) {
//                textView.setText(rangeRef);
//            }
//
//            @Override
//            public void onFailure(String err) {
//                Toast.makeText(TestActivity.this, err, Toast.LENGTH_SHORT).show();
//            }
//        });

        // 연결 정보 검사.
//        connectionController.setObserveConnectionStatus("target1", "protector1", new ConnectionController.OnObservedDataChange() {
//            @Override
//            public void OnDataChange(Object object) {
//                textView.setText(object.toString());
//            }
//        });

        //위치 변화 검사.
        Observer statusObserver = new Observer();
        statusObserver.setObservingLocation("target1", "protector1", new Observer.OnObservedDataChange() {
            @Override
            public void OnDataChange(Object object) {
                textView.setText(object.toString());
            }
        });

        // setAlarm 예시
//        connectionController.setAlarm("target1", "protector1", true, new ConnectionController.OnSetCompleteListener() {
//            @Override
//            public void onComplete() {
//                textView.setText("흠");
//            }
//        });
    }


}
