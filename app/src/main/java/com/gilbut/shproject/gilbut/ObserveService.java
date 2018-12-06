package com.gilbut.shproject.gilbut;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.gilbut.shproject.gilbut.model.Connection;
import com.gilbut.shproject.gilbut.model.TargetMember;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class ObserveService extends Service {
    String myId;
    NotificationManager notifManager;
    Notification Notifi ;
    ArrayList<Observer> observers;

    public ObserveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        observers = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        myId = auth.getCurrentUser().getEmail();
        ConnectionController connectionController = new ConnectionController();
        //연결들 중에서 나를 보호자로 하는 연결들을 찾음.
        connectionController.getProtectorConnections(myId, new ConnectionController.OnGetConnectionsListener() {
            @Override
            public void onComplete(ArrayList<Connection> connections) {
                for(final Connection connection: connections){
                    Observer observer  = new Observer();
                    // 관찰하고 있던 위치값이 바뀌면 실행할 함수 등록.
                    observer.setObservingLocation(connection.tId, new Observer.OnObservedDataChange() {
                        @Override
                        public void OnDataChange(final Object object) {
                            final LatLng location = (LatLng)object;
                            Toast.makeText(ObserveService.this, "대상 위치1"+location, Toast.LENGTH_SHORT).show();
                            final Member member = new Member();
                                // 범위 밖으로 가는거 알람인지 아니면 들어오는거 알람인지.
                                member.getPrevent(myId, new Member.OnGetPreventListener() {
                                    @Override
                                    public void onComplete(final boolean prevent) {
                                        RangeController rangeController = new RangeController();
                                        if(!connection.rangeRef.equals("")) {
                                            //연결에서 가져온 rangeRef로 Range가져오기.
                                            rangeController.getRange(connection.rangeRef, new RangeController.OnGetRangeListener() {
                                                @Override
                                                public void onComplete(ArrayList<LatLng> range) {
                                                    //*********결과에 따라 노티 표시.*************
                                                    Toast.makeText(ObserveService.this, "대상 위치"+range, Toast.LENGTH_SHORT).show();
                                                    boolean result = checkLeave(location,range,prevent );
                                                    Toast.makeText(ObserveService.this, "결과는: "+result, Toast.LENGTH_SHORT).show();
                                                    if(!result){
                                                        Toast.makeText(ObserveService.this, "대상 위치!!", Toast.LENGTH_SHORT).show();
                                                        if(prevent){
                                                            createNotification(connection.tId+"가 범위를 벗어났습니다.",connection.tId+"가 범위를 벗어났습니다.",ObserveService.this);
                                                        }else{
                                                            createNotification(connection.tId+"가 범위안으로 들어왔습니다.",connection.tId+"가 범위안으로 들어왔습니다.",ObserveService.this);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(String err) {

                                                }
                                            });

                                        }else{

                                        }
                                    }

                                    @Override
                                    public void onFailure(String err) {

                                    }
                                });


                        }
                    });

                    Observer alarmObserver = new Observer();
                    alarmObserver.setObservingConnectionAlarm(connection.tId, new Observer.OnObservedDataChange() {
                        @Override
                        public void OnDataChange(Object object) {
                            boolean alarm = (boolean)object;
                            if(!alarm){
                               createNotification(connection.tId+"가 위치정보를 보내지않습니다.", "확인확인", ObserveService.this);
                            }
                        }
                    });

                    Observer emergencyObserver = new Observer();
                    emergencyObserver.setObservingConnectionEmergency(connection.tId, new Observer.OnObservedDataChange() {
                        @Override
                        public void OnDataChange(Object object) {
                            boolean emergency = (boolean)object;
                            if(emergency){
                                createNotification(connection.tId+"의 긴급신호!!!", connection.tId+"가 긴급 신호를 보냈습니다!!", ObserveService.this);
                            }
                        }
                    });
                    observers.add(observer);
                    observers.add(alarmObserver);
                }
            }

            @Override
            public void onFailure(String err) {

            }
        });
    }

    @Override
    public void onDestroy() {
        for(Observer observer : observers){
            if(observer != null)
                observer.removeObserver();
        }
        super.onDestroy();
    }

    public boolean checkLeave(LatLng targetPoint, ArrayList<LatLng> lists, boolean Tprevent){

        boolean inside = PolyUtil.containsLocation(targetPoint, lists , true );
        // true 면 좌표 안에 존재하는 것
        // false 면 좌표 안에 존재하지 않는 것
        if(Tprevent){ // Tprevent에 따라 return이 달라짐
            //ex Tprevent == true >> 범위 이탈을 보고 싶다
            // Tprevent == true && inside == true
            // 범위 내에 잘 있다
            //Tprevent == true && inside == false
            // 범위 이탈 했다.
            return inside;
        }
        else{
            // Tprevent == false >> 접근 금지를 보고 싶다
            // 범위 내에 있지 않다
            //Tprevent == false && inside == true
            // 범위 내로 접근 했다.
            //Tprevent == false && inside == false
            // 범위에 접근 하지 않았다
            return !(inside);

            // 아무튼 inside가 false 일 때 알림 주기
            // else 에서 return !(inside) 이기 때문에
            // 안에 포함되는 경우 (접근한 경우)
        }
    }

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
            intent = new Intent(context, ProtectorActivity.class);
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
            intent = new Intent(context, ProtectorActivity.class);
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
