package ca.boomerconx.feature.scamshield.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import ca.boomerconx.feature.scamshield.domain.AnalyzeSmsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsAnalysisService : BroadcastReceiver() {

    @Inject lateinit var analyzeSmsUseCase: AnalyzeSmsUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val fullMessage = messages.joinToString("") { it.messageBody }

        scope.launch {
            val result = analyzeSmsUseCase(fullMessage)
            if (result.isScam) {
                showScamAlert(context, fullMessage, result.reasons, result.severity)
            }
        }
    }

    private fun showScamAlert(
        context: Context,
        message: String,
        reasons: List<String>,
        severity: Int
    ) {
        val notificationHelper = ScamNotificationHelper(context)
        notificationHelper.showScamWarning(
            title = "Arnaque détectée!",
            body = reasons.firstOrNull() ?: "Ce message semble suspect",
            severity = severity
        )
    }
}

class ScamNotificationHelper(private val context: Context) {
    fun showScamWarning(title: String, body: String, severity: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as android.app.NotificationManager

        val channel = android.app.NotificationChannel(
            "scam_alerts",
            "Alertes arnaque",
            android.app.NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val notification = android.app.Notification.Builder(context, "scam_alerts")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
