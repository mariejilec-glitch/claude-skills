package ca.boomerconx.feature.scamshield.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.annotation.RequiresApi
import ca.boomerconx.core.data.db.entity.ScamType
import ca.boomerconx.feature.scamshield.data.ScamRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class CallerScreeningService : CallScreeningService() {

    @Inject lateinit var scamRepository: ScamRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: run {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        scope.launch {
            val patterns = scamRepository.getPatternsByType(ScamType.CALLER.name)
            val isScam = patterns.any { pattern ->
                phoneNumber.contains(pattern.pattern)
            }

            val response = if (isScam) {
                CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
            } else {
                CallResponse.Builder().build()
            }

            respondToCall(callDetails, response)
        }
    }
}
