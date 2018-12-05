package com.gilbut.shproject.gilbut;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.gilbut.shproject.gilbut.model.Connection;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ObserveService extends Service {
    String myId;
    public ObserveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        myId = auth.getCurrentUser().getEmail();
        ConnectionController connectionController = new ConnectionController();
        connectionController.getProtectorConnections(myId, new ConnectionController.OnGetConnectionsListener() {
            @Override
            public void onComplete(ArrayList<Connection> connections) {
                for(Connection connection: connections){
                    Observer observer  = new Observer();
                    observer.setObservingLocation(connection.tId, connection.pId, new Observer.OnObservedDataChange() {
                        @Override
                        public void OnDataChange(Object object) {
                            LatLng location = (LatLng) object;
                            //검사!!
                        }
                    });
                }
            }

            @Override
            public void onFailure(String err) {

            }
        });
    }
}
