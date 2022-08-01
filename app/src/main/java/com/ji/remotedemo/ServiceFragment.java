package com.ji.remotedemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
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
    private IRemoteDemo mRemoteDemo;
    private TextView mDataView;
    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.service_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "bindService");
                Intent fgService = new Intent().setPackage("com.ji.remotedemo").setAction("com.ji.remotedemo.FgService");
                view.getContext().bindService(fgService, mConnection, Context.BIND_AUTO_CREATE);
            }
        });
        view.findViewById(R.id.service_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "unbindService");
                if (mRemoteDemo != null) {
                    try {
                        mRemoteDemo.unregister(mRemoteCallback);
                    } catch (RemoteException e) {
                        Log.e(TAG, "unregister", e);
                    }
                    view.getContext().unbindService(mConnection);
                }
                mRemoteDemo = null;
            }
        });
        mDataView = view.findViewById(R.id.service_data);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            mRemoteDemo = IRemoteDemo.Stub.asInterface(service);
            try {
                mRemoteDemo.register(mRemoteCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "register", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected");
            mRemoteDemo = null;
        }
    };

    private IRemoteCallback.Stub mRemoteCallback = new IRemoteCallback.Stub() {
        @Override
        public void dataCallback(final String data) throws RemoteException {
            Log.v(TAG, "dataCallback data:" + data);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDataView.setText(data);
                }
            });
        }
    };

    public static class FgService extends Service {
        private static final String TAG = "FgService";
        private NotificationManager mNotificationManager;
        private static final int ID = 1;
        private final RemoteCallbackList<IRemoteCallback> mCallbacks = new RemoteCallbackList<IRemoteCallback>();
        private String mData;
        private boolean mRun = false;

        private IRemoteDemo.Stub mBinder = new IRemoteDemo.Stub() {
            @Override
            public int register(IRemoteCallback callback) throws RemoteException {
                Log.v(TAG, "register");
                if (callback != null) {
                    mCallbacks.register(callback);
                }
                return 0;
            }

            @Override
            public int unregister(IRemoteCallback callback) throws RemoteException {
                Log.v(TAG, "unregister");
                if (callback != null) {
                    mCallbacks.unregister(callback);
                }
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
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                Notification.Builder builder = new Notification.Builder(this, TAG);
                builder.setContentText(TAG);
                builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

                NotificationChannel channel = new NotificationChannel(getPackageName(), "FgServiceChannel", NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
                builder.setChannelId(channel.getId());

                startForeground(ID, builder.build());
            }

            mRun = true;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (mRun) {
                        mData = String.valueOf(System.currentTimeMillis());
                        Log.v(TAG, "run mData:" + mData);
                        final int N = mCallbacks.beginBroadcast();
                        for (int i = 0; i < N; i++) {
                            try {
                                mCallbacks.getBroadcastItem(i).dataCallback(mData);
                            } catch (RemoteException e) {
                                Log.e(TAG, "run dataCallback", e);
                            }
                        }
                        mCallbacks.finishBroadcast();

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "run sleep", e);
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.v(TAG, "onDestroy");
            mCallbacks.kill();
            mRun = false;
            if (mNotificationManager != null) {
                mNotificationManager.cancel(ID);
            }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.v(TAG, "onStartCommand startId:" + startId);
            return START_NOT_STICKY;
        }
    }
}
