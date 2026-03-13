package ca.boomerconx.feature.launcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.boomerconx.core.data.db.entity.LauncherItemEntity
import ca.boomerconx.feature.launcher.data.LaunchableApp
import ca.boomerconx.feature.launcher.data.LauncherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val launcherRepository: LauncherRepository
) : ViewModel() {

    val layoutItems: StateFlow<List<LauncherItemEntity>> = launcherRepository.getLayoutItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _allApps = MutableStateFlow<List<LaunchableApp>>(emptyList())
    val allApps: StateFlow<List<LaunchableApp>> = _allApps.asStateFlow()

    private val _showDrawer = MutableStateFlow(false)
    val showDrawer: StateFlow<Boolean> = _showDrawer.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        _allApps.value = launcherRepository.getAllLaunchableApps()
    }

    fun launchApp(packageName: String) {
        launcherRepository.launchApp(packageName)
    }

    fun addToHomeScreen(app: LaunchableApp) {
        viewModelScope.launch {
            val items = layoutItems.value
            val nextY = (items.maxOfOrNull { it.positionY } ?: -1) + 1
            launcherRepository.addItem(
                LauncherItemEntity(
                    packageName = app.packageName,
                    label = app.label,
                    positionX = 0,
                    positionY = nextY
                )
            )
        }
    }

    fun removeFromHomeScreen(item: LauncherItemEntity) {
        viewModelScope.launch { launcherRepository.removeItem(item) }
    }

    fun toggleDrawer() {
        _showDrawer.value = !_showDrawer.value
    }
}
