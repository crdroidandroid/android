package net.crdroid.gamespace.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class GamingModeService extends Service {
    private static final String TAG = "GamingModeService";
    private static final String CHANNEL_ID = "GamingModeServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private NotificationManager notificationManager;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like responding to intents
            // For now, just log messages
            Log.d(TAG, "ServiceHandler received message: " + msg.what);

            // Example: Stop the service if told to do so
            // if (msg.arg1 == 1) {
            //     stopSelf(msg.arg2);
            // }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = getSystemService(NotificationManager.class);

        HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

        createNotificationChannel();
        Log.d(TAG, "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service Starting");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId; // Example: pass startId
        // TODO: Define message 'what' constants
        // msg.what = intent.getIntExtra("COMMAND", 0);
        serviceHandler.sendMessage(msg);

        startForeground(NOTIFICATION_ID, createNotification());

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Gaming Mode Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        // TODO: Add proper icon and strings
        // Intent notificationIntent = new Intent(this, GamingModeSettingsActivity.class);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Gaming Mode Active") // Placeholder
                .setContentText("Tap to configure.") // Placeholder
                .setSmallIcon(net.crdroid.gamespace.R.drawable.ic_launcher_foreground) // Use drawable for notification
                // .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
        // TODO: Implement IBinder for communication with Settings UI if needed (e.g. AIDL)
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceLooper.quit();
        Log.d(TAG, "Service Destroyed");
    }
}
