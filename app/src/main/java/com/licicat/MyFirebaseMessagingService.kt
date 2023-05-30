package com.licicat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Aquí puedes manejar la recepción de las notificaciones
        // y mostrar una notificación en el dispositivo.

        // Por ejemplo, puedes obtener los datos del mensaje remoto.
       Looper.prepare()
        Handler().post {
           Toast.makeText(baseContext, "Notificació rebuda", Toast.LENGTH_LONG).show()
        }
        Looper.loop()

    }

    override fun onNewToken(token: String) {
        // Aquí puedes obtener el nuevo token de registro del dispositivo
        // y realizar acciones según tus necesidades, como enviarlo a tu servidor backend.

        // Por ejemplo, puedes mostrar el token en el logcat para verificar su recepción.
        println("Nuevo token: $token")
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "my_channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Verifica si el dispositivo tiene Android Oreo (API 26) o superior para crear un canal de notificación.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}