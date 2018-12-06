package com.gilbut.shproject.gilbut;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gilbut.shproject.gilbut.model.Connection;
import com.gilbut.shproject.gilbut.model.TargetMember;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
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
//        connectionController.getConnections("protector1", new ConnectionController.OnGetConnectionsListener()
////        {
////            @Override
////            public void onComplete(ArrayList<Connection> connections) {
////                String str ="";
////                for(Connection connection : connections){
////                    str = str + " " + connection.location;
////                      // range 받아오기
//////                    RangeController range = new RangeController(connection.rangeRef, new RangeController.OnGetRangeListener() {
//////                        @Override
//////                        public void onComplete(List<LatLng> range) {
//////                            textView.setText(""+range.get(0).latitude);
//////                        }
//////
//////                        @Override
//////                        public void onFailure(String err) {
//////
//////                        }
//////                    });
////                    textView.setText(str);
////                }
////
////            }
////
////            @Override
////            public void onFailure(String err) {
////
////            }
////        });

        // RangeController setting 예시
//        RangeController rangeController = new RangeController();
//        ArrayList<LatLng> newRange = new ArrayList<LatLng>();
//        newRange.add(new LatLng(34.555, 188.444));
//        newRange.add(new LatLng(33.555, 188.444));
//        newRange.add(new LatLng(37.555, 180.9));
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

        //위치 변화 검사.
        Observer statusObserver = new Observer();
//        statusObserver.setObservingLocation("target1", "protector1", new Observer.OnObservedDataChange() {
//            @Override
//            public void OnDataChange(Object object) {
//                textView.setText(object.toString());
//            }
//        });

        //자식검사
//        statusObserver.setObserveringNewConnection("jse52595@gmail.com", new Observer.OnObservedDataChange() {
//            @Override
//            public void OnDataChange(Object object) {
//                Connection connection = (Connection) object;
//                textView.setText(connection.tId);
//            }
//        });

        // 타겟 정보 가져오기.
      //Member member = new Member();
//        member.getTarget("jse52595@gmail.com", new Member.OnGetTargetListener() {
//            @Override
//            public void onGetData(TargetMember target) {
//                textView.setText(""+target.getLocation());
//            }
//        });

        // setAlarm 예시
//        member.setAlarm("jse52595@gmail.com",  true, new Member.OnSetCompleteListener() {
//            @Override
//            public void onComplete() {
//                textView.setText("흠");
//            }
//
//            @Override
//            public void onFailure(String err) {
//
//            }
//
//        });

        Context context = TestActivity.this;
        createNotification("짜잔", "그래그래", context);


    }
    private NotificationManager notifManager;
    public void createNotification(String notiTitle, String message, Context context) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = context.getString(R.string.default_notification_channel_id); // default_channel_id
        String title = context.getString(R.string.default_notification_channel_title); // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(notiTitle)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(notiTitle)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(notiTitle)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(notiTitle)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }
}
