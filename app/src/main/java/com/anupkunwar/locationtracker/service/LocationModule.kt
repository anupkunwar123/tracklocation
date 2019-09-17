package com.anupkunwar.locationtracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import com.anupkunwar.locationtracker.R
import com.anupkunwar.locationtracker.di.ServiceScope
import com.anupkunwar.locationtracker.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides

@Module
class LocationModule {

    @Provides
    @ServiceScope
    fun provideFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
        return FusedLocationProviderClient(context)
    }

    @Provides
    @ServiceScope
    fun provideNotification(context: Context): Notification {

        val pendingIntent: PendingIntent =
            Intent(context, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(context, 0, notificationIntent, 0)
            }
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                "my_service",
                "My Background Service", NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
            chan.id
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            ""
        }

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }

        return notificationBuilder
            .setContentTitle(context.getText(R.string.notification_title))
            .setContentText(context.getText(R.string.notification_message))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setTicker(context.getText(R.string.ticker_text))
            .build()
    }
}