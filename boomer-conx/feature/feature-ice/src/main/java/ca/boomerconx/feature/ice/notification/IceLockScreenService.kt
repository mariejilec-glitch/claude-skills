package ca.boomerconx.feature.ice.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ca.boomerconx.core.data.db.dao.IceContactDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IceLockScreenService : Service() {

    @Inject lateinit var iceContactDao: IceContactDao

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            val contacts = iceContactDao.getAll().first()
            val notification = buildIceNotification(
                contacts.joinToString("\n") { "${it.name}: ${it.phone}" }
            )
            startForeground(ICE_NOTIFICATION_ID, notification)
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            ICE_CHANNEL_ID,
            "Urgence (ICE)",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Informations d'urgence sur l'écran de verrouillage"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildIceNotification(contactInfo: String): Notification {
        return NotificationCompat.Builder(this, ICE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("En cas d'urgence - Contacts ICE")
            .setContentText(contactInfo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contactInfo))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()
    }

    companion object {
        private const val ICE_CHANNEL_ID = "ice_channel"
        private const val ICE_NOTIFICATION_ID = 1001
    }
}
