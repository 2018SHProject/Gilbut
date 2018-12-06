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
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

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
        connectionController.getProtectorConnections(myId, new ConnectionController.OnGetConnectionsListener() {
            @Override
            public void onComplete(ArrayList<Connection> connections) {
                for(final Connection connection: connections){
                    Observer observer  = new Observer();
                    observer.setObservingLocation(connection.tId, new Observer.OnObservedDataChange() {
                        @Override
                        public void OnDataChange(Object object) {
                            LatLng location = (LatLng) object;
                            //검사!!
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
                                showAEmergencyDialog(connection.tId);
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

    // 다이얼로그 띄우기.
    public void showAEmergencyDialog(final String targetId){
            new AlertDialog.Builder(ObserveService.this)
                    .setTitle("<긴급!!>")
                    .setMessage(targetId+"가 긴급신로를 보냈했습니다.")
                    .setPositiveButton("연결", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 긴급 상태 업데이트
                           Member member = new Member();
                           member.setEmergency(targetId, false, new Member.OnSetCompleteListener() {
                               @Override
                               public void onComplete() {

                               }

                               @Override
                               public void onFailure(String err) {

                               }
                           });
                        }
                    });

    }
}
