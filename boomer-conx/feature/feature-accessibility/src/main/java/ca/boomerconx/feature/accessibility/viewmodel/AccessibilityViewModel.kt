package ca.boomerconx.feature.accessibility.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.boomerconx.feature.accessibility.data.AccessibilityPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccessibilityUiState(
    val fontScale: Float = 1.0f,
    val highContrast: Boolean = false,
    val reducedMotion: Boolean = false
)

@HiltViewModel
class AccessibilityViewModel @Inject constructor(
    private val accessibilityPreferences: AccessibilityPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccessibilityUiState())
    val uiState: StateFlow<AccessibilityUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accessibilityPreferences.accessibilityState.collect { state ->
                _uiState.value = AccessibilityUiState(
                    fontScale = state.fontScale,
                    highContrast = state.highContrast,
                    reducedMotion = state.reducedMotion
                )
            }
        }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch {
            accessibilityPreferences.setFontScale(scale)
        }
    }

    fun setHighContrast(enabled: Boolean) {
        viewModelScope.launch {
            accessibilityPreferences.setHighContrast(enabled)
        }
    }

    fun setReducedMotion(enabled: Boolean) {
        viewModelScope.launch {
            accessibilityPreferences.setReducedMotion(enabled)
        }
    }
}
