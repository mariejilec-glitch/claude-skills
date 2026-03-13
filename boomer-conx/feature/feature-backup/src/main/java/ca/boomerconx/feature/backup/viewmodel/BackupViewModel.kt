package ca.boomerconx.feature.backup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.boomerconx.core.data.db.dao.BackupMetadataDao
import ca.boomerconx.core.data.db.entity.BackupMetadataEntity
import ca.boomerconx.feature.backup.domain.BackupContactsUseCase
import ca.boomerconx.feature.backup.domain.BackupPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BackupUiState(
    val isBackingUp: Boolean = false,
    val backupType: String? = null,
    val progress: Float = 0f,
    val error: String? = null,
    val lastResult: String? = null
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupContactsUseCase: BackupContactsUseCase,
    private val backupPhotosUseCase: BackupPhotosUseCase,
    backupMetadataDao: BackupMetadataDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    val backupHistory: StateFlow<List<BackupMetadataEntity>> = backupMetadataDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun backupContacts() {
        viewModelScope.launch {
            _uiState.value = BackupUiState(isBackingUp = true, backupType = "Contacts")
            val result = backupContactsUseCase()
            result.fold(
                onSuccess = { meta ->
                    _uiState.value = BackupUiState(
                        lastResult = "${meta.itemCount} contacts sauvegardés"
                    )
                },
                onFailure = { e ->
                    _uiState.value = BackupUiState(error = e.message)
                }
            )
        }
    }

    fun backupPhotos() {
        viewModelScope.launch {
            _uiState.value = BackupUiState(isBackingUp = true, backupType = "Photos")
            val result = backupPhotosUseCase()
            result.fold(
                onSuccess = { meta ->
                    _uiState.value = BackupUiState(
                        lastResult = "${meta.itemCount} photos sauvegardées"
                    )
                },
                onFailure = { e ->
                    _uiState.value = BackupUiState(error = e.message)
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
