package ca.boomerconx.feature.scamshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.boomerconx.core.data.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScamShieldUiState(
    val threatsBlocked: Int = 0,
    val lastScanTime: Long? = null
)

@HiltViewModel
class ScamShieldViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScamShieldUiState())
    val uiState: StateFlow<ScamShieldUiState> = _uiState.asStateFlow()

    val smsProtectionEnabled: StateFlow<Boolean> = userPreferences.smsProtection
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val callProtectionEnabled: StateFlow<Boolean> = userPreferences.callProtection
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun toggleSmsProtection(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setSmsProtection(enabled) }
    }

    fun toggleCallProtection(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setCallProtection(enabled) }
    }
}
