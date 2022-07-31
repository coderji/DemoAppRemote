package com.ji.remotedemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ji.util.BaseFragment;
import com.ji.util.Log;

public class ServiceFragment extends BaseFragment {
    private static final String TAG = "ServiceFragment";
    private static TextView mDataView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Intent fgService = new Intent(getContext(), FgService.class);
        view.findViewById(R.id.service_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "startService");
                view.getContext().startService(fgService);
            }
        });
        view.findViewById(R.id.service_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "stopService");
                view.getContext().stopService(fgService);
            }
        });
        mDataView = view.findViewById(R.id.service_data);
    }

    public static class FgService extends Service {
        private static final String TAG = "FgService";
        private static final int ID = 1;
        private IRemoteCallback mRemoteCallback;
        private String mData;

        private IBinder mBinder = new IRemoteDemo.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

            }

            @Override
            public int register(IRemoteCallback callback) throws RemoteException {
                mRemoteCallback = callback;
                return 0;
            }

            @Override
            public int unregister(IRemoteCallback callback) throws RemoteException {
                mRemoteCallback = null;
                return 0;
            }

            @Override
            public String getData() throws RemoteException {
                return mData;
            }

            @Override
            public int setData(String data) throws RemoteException {
                mData = data;
                return 0;
            }
        };

        @Override
        public IBinder onBind(Intent intent) {
            return mBinder;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.v(TAG, "onCreate");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.v(TAG, "onDestroy");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.v(TAG, "onStartCommand");
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                Notification.Builder builder = new Notification.Builder(this, TAG);
                builder.setContentText(TAG);
                builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

                NotificationChannel channel =
                        new NotificationChannel(getPackageName(),
                                "FgServiceChannel", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId(channel.getId());

                startForeground(ID, builder.build());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (i < 60) {
                        mData = String.valueOf(System.currentTimeMillis());
                        Log.v(TAG, "run data:" + mData);
                        if (mRemoteCallback != null) {
                            try {
                                mRemoteCallback.dataCallback(mData);
                            } catch (RemoteException e) {
                                Log.e(TAG, "run dataCallback", e);
                            }
                        }
                        mDataView.post(new Runnable() {
                            @Override
                            public void run() {
                                mDataView.setText(mData);
                            }
                        });

                        i++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "run sleep", e);
                        }
                    }
                }
            }).start();
            return super.onStartCommand(intent, flags, startId);
        }
    }
}
