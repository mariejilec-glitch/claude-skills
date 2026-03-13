package ca.boomerconx.feature.backup.data

import android.content.Context
import android.content.pm.ApplicationInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class InstalledApp(
    val packageName: String,
    val label: String,
    val isSystemApp: Boolean
)

class AppListRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        pm.getInstalledApplications(0)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .map { appInfo ->
                InstalledApp(
                    packageName = appInfo.packageName,
                    label = pm.getApplicationLabel(appInfo).toString(),
                    isSystemApp = false
                )
            }
            .sortedBy { it.label.lowercase() }
    }
}
