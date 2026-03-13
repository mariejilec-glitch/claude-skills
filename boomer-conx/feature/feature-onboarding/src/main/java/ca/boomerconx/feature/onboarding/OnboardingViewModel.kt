package ca.boomerconx.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.boomerconx.core.data.prefs.UserPreferences
import ca.boomerconx.core.security.PinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 0,
    val selectedLanguage: String = "fr-CA",
    val pin: String = "",
    val confirmPin: String = "",
    val pinError: String? = null,
    val isComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val pinManager: PinManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectLanguage(language: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
        viewModelScope.launch {
            userPreferences.setLanguage(language)
        }
    }

    fun nextStep() {
        _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
    }

    fun updatePin(pin: String) {
        if (pin.length <= 6) {
            _uiState.value = _uiState.value.copy(pin = pin, pinError = null)
        }
    }

    fun updateConfirmPin(confirmPin: String) {
        if (confirmPin.length <= 6) {
            _uiState.value = _uiState.value.copy(confirmPin = confirmPin, pinError = null)
        }
    }

    fun submitPin() {
        val state = _uiState.value
        if (state.pin.length != 6) {
            _uiState.value = state.copy(pinError = "Le NIP doit contenir 6 chiffres")
            return
        }
        if (state.pin != state.confirmPin) {
            _uiState.value = state.copy(pinError = "Les NIP ne correspondent pas", confirmPin = "")
            return
        }
        viewModelScope.launch {
            pinManager.setPin(state.pin)
            nextStep()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.setOnboarded(true)
            _uiState.value = _uiState.value.copy(isComplete = true)
        }
    }
}
