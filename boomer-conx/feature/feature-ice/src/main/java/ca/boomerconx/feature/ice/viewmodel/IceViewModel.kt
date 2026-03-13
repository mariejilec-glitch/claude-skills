package ca.boomerconx.feature.ice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.boomerconx.core.data.db.entity.IceContactEntity
import ca.boomerconx.feature.ice.data.IceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IceUiState(
    val isEditing: Boolean = false,
    val editingContact: IceContactEntity? = null,
    val bloodType: String = "",
    val allergies: String = "",
    val medications: String = "",
    val healthCard: String = ""
)

@HiltViewModel
class IceViewModel @Inject constructor(
    private val iceRepository: IceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IceUiState())
    val uiState: StateFlow<IceUiState> = _uiState.asStateFlow()

    val contacts: StateFlow<List<IceContactEntity>> = iceRepository.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addContact(name: String, phone: String, relationship: String) {
        viewModelScope.launch {
            val priority = (contacts.value.maxOfOrNull { it.priority } ?: 0) + 1
            val contact = IceContactEntity(
                name = name,
                phone = phone,
                relationship = relationship,
                priority = priority
            )
            iceRepository.saveContact(contact)
            _uiState.value = _uiState.value.copy(isEditing = false)
        }
    }

    fun deleteContact(contact: IceContactEntity) {
        viewModelScope.launch {
            iceRepository.deleteContact(contact)
        }
    }

    fun toggleEditing() {
        _uiState.value = _uiState.value.copy(isEditing = !_uiState.value.isEditing)
    }
}
