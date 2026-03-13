package ca.boomerconx.feature.vault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.boomerconx.core.data.db.entity.CredentialEntity
import ca.boomerconx.feature.vault.data.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VaultUiState(
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val isAddingNew: Boolean = false,
    val selectedCredential: CredentialEntity? = null,
    val decryptedPassword: String? = null,
    val error: String? = null
)

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    val credentials: StateFlow<List<CredentialEntity>> = vaultRepository.getAllCredentials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCredential(
        siteName: String,
        url: String?,
        username: String,
        password: String,
        notes: String?,
        category: String?
    ) {
        viewModelScope.launch {
            try {
                vaultRepository.saveCredential(siteName, url, username, password, notes, category)
                _uiState.value = _uiState.value.copy(isAddingNew = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun revealPassword(credential: CredentialEntity) {
        try {
            val password = vaultRepository.decryptPassword(credential)
            _uiState.value = _uiState.value.copy(
                selectedCredential = credential,
                decryptedPassword = password
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = "Impossible de déchiffrer le mot de passe")
        }
    }

    fun hidePassword() {
        _uiState.value = _uiState.value.copy(decryptedPassword = null)
    }

    fun deleteCredential(credential: CredentialEntity) {
        viewModelScope.launch {
            vaultRepository.deleteCredential(credential)
        }
    }

    fun toggleAddNew() {
        _uiState.value = _uiState.value.copy(isAddingNew = !_uiState.value.isAddingNew)
    }

    fun generatePassword(length: Int = 16): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789!@#\$%&*"
        return (1..length).map { chars.random() }.joinToString("")
    }
}
