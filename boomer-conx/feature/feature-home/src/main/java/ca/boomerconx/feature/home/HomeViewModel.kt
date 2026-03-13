package ca.boomerconx.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.boomerconx.core.data.db.dao.BackupMetadataDao
import ca.boomerconx.core.data.db.dao.CredentialDao
import ca.boomerconx.core.data.db.dao.IceContactDao
import ca.boomerconx.core.data.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val lastBackupTimestamp: Long? = null,
    val vaultItemCount: Int = 0,
    val iceContactCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    userPreferences: UserPreferences,
    backupMetadataDao: BackupMetadataDao,
    credentialDao: CredentialDao,
    iceContactDao: IceContactDao
) : ViewModel() {

    val isOnboarded: StateFlow<Boolean> = userPreferences.isOnboarded
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val uiState: StateFlow<HomeUiState> = combine(
        backupMetadataDao.getLastBackupTimestamp(),
        credentialDao.getCount(),
        iceContactDao.getCount()
    ) { lastBackup, vaultCount, iceCount ->
        HomeUiState(
            lastBackupTimestamp = lastBackup,
            vaultItemCount = vaultCount,
            iceContactCount = iceCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
