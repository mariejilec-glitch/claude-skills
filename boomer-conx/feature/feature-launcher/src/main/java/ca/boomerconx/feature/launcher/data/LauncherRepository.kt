package ca.boomerconx.feature.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import ca.boomerconx.core.data.db.dao.LauncherItemDao
import ca.boomerconx.core.data.db.entity.LauncherItemEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

data class LaunchableApp(
    val packageName: String,
    val label: String,
    val resolveInfo: ResolveInfo
)

@Singleton
class LauncherRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val launcherItemDao: LauncherItemDao
) {
    fun getLayoutItems(): Flow<List<LauncherItemEntity>> = launcherItemDao.getAll()

    suspend fun saveLayout(items: List<LauncherItemEntity>) {
        launcherItemDao.deleteAll()
        launcherItemDao.insertAll(items)
    }

    suspend fun addItem(item: LauncherItemEntity) = launcherItemDao.insert(item)

    suspend fun removeItem(item: LauncherItemEntity) = launcherItemDao.delete(item)

    fun getAllLaunchableApps(): List<LaunchableApp> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = context.packageManager
        return pm.queryIntentActivities(intent, 0)
            .map { resolveInfo ->
                LaunchableApp(
                    packageName = resolveInfo.activityInfo.packageName,
                    label = resolveInfo.loadLabel(pm).toString(),
                    resolveInfo = resolveInfo
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    fun launchApp(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent?.let { context.startActivity(it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }
}
