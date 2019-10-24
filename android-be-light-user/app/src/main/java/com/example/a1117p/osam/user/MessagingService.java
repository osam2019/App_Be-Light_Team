package com.example.a1117p.osam.user;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        FirebaseMessaging.getInstance().subscribeToTopic("notice");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       Map<String,String> data = remoteMessage.getData();
        if (data.size() > 0) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(this, SplashActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Be-light")
                    .setContentTitle(data.get("title"))
                    .setContentText(data.get("body"))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            //OREO API 26 이상에서는 채널 필요
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                CharSequence channelName  = "Be-light";
                String description = "Be-light의 알림을 위한 채널";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel("Be-light", channelName , importance);
                channel.setDescription(description);

                // 노티피케이션 채널을 시스템에 등록
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);

            }else builder.setSmallIcon(R.drawable.logo);

            assert notificationManager != null;
            notificationManager.notify(9999, builder.build());


        }
    }
}
