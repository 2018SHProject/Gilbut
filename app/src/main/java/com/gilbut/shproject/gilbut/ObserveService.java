package com.gilbut.shproject.gilbut;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.gilbut.shproject.gilbut.model.Connection;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ObserveService extends Service {
    String myId;
    NotificationManager Notifi_M;
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
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
                                Intent intent = new Intent(ObserveService.this, MainActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(ObserveService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                Notifi = new Notification.Builder(getApplicationContext())
                                        .setContentTitle("Content Title")
                                        .setContentText("Content Text")
                                        .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                                        .setTicker("알림!!!")
                                        .setContentIntent(pendingIntent)
                                        .build();

                                //소리추가
                                Notifi.defaults = Notification.DEFAULT_SOUND;

                                //알림 소리를 한번만 내도록
                                Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

                                //확인하면 자동으로 알림이 제거 되도록
                                Notifi.flags = Notification.FLAG_AUTO_CANCEL;


                                Notifi_M.notify( 777 , Notifi);

                                Toast.makeText(ObserveService.this, "대상("+connection.tId+")이 위치 전송을 안합니다!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    Observer emergencyObserver = new Observer();
                    emergencyObserver.setObservingConnectionEmergency(connection.tId, new Observer.OnObservedDataChange() {
                        @Override
                        public void OnDataChange(Object object) {
                            boolean emergency = (boolean)object;
                            if(emergency){
                                Intent intent = new Intent(ObserveService.this, MainActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(ObserveService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                Notification noti = new Notification.Builder(getApplicationContext())
                                        .setContentTitle("Guilbut 알림")
                                        .setContentText(connection.tId+" 긴급신호!")
                                        .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                                        .setTicker("알림")
                                        .setContentIntent(pendingIntent)
                                        .build();
                                Notifi_M.notify(0, noti);
                                Toast.makeText(ObserveService.this, "대상("+connection.tId+")이 긴급신호를 보냈어요!", Toast.LENGTH_LONG).show();
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
}
