package com.example.eternotev2.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.auth.SessionManager
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.ui.theme.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder { DATE_ASC, DATE_DESC, NAME_ASC, NAME_DESC }

data class CapsuleFilters(
    val status: FilterStatus = FilterStatus.ALL,
    val hasVoiceNote: Boolean? = null,
    val isCoreMemory: Boolean? = null
)

enum class FilterStatus { ALL, OPENED, UNOPENED }

data class HomeUiState(
    val currentMood: Mood = Mood.HOPEFUL,
    val capsules: List<Capsule> = emptyList(),
    val filteredCapsules: List<Capsule> = emptyList(),
    val isLoading: Boolean = false,
    val userName: String = "Traveler",
    val filters: CapsuleFilters = CapsuleFilters(),
    val sortOrder: SortOrder = SortOrder.DATE_DESC
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val capsuleRepository: CapsuleRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeSession()
        loadCapsules()
    }

    private fun observeSession() {
        sessionManager.userIdFlow
            .onEach { _ ->
                _uiState.update { it.copy(userName = sessionManager.getUserName()) }
            }
            .launchIn(viewModelScope)
    }

    fun logout() {
        sessionManager.clearSession()
    }

    private fun loadCapsules() {
        capsuleRepository.getAllCapsules()
            .onEach { capsules ->
                _uiState.update { state ->
                    state.copy(
                        capsules = capsules,
                        filteredCapsules = applyFilterAndSort(capsules, state.filters, state.sortOrder),
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onMoodChanged(newMood: Mood) {
        _uiState.update { it.copy(currentMood = newMood) }
    }

    fun updateFilters(newFilters: CapsuleFilters) {
        _uiState.update { state ->
            state.copy(
                filters = newFilters,
                filteredCapsules = applyFilterAndSort(state.capsules, newFilters, state.sortOrder)
            )
        }
    }

    fun updateSortOrder(newSortOrder: SortOrder) {
        _uiState.update { state ->
            state.copy(
                sortOrder = newSortOrder,
                filteredCapsules = applyFilterAndSort(state.capsules, state.filters, newSortOrder)
            )
        }
    }

    private fun applyFilterAndSort(
        list: List<Capsule>,
        filters: CapsuleFilters,
        sort: SortOrder
    ): List<Capsule> {
        return list.asSequence()
            .filter { capsule ->
                val statusMatch = when (filters.status) {
                    FilterStatus.ALL -> true
                    FilterStatus.OPENED -> capsule.isUnlocked
                    FilterStatus.UNOPENED -> !capsule.isUnlocked
                }
                val voiceMatch = filters.hasVoiceNote?.let { capsule.hasVoiceNote == it } ?: true
                val coreMatch = filters.isCoreMemory?.let { capsule.isCoreMemory == it } ?: true
                statusMatch && voiceMatch && coreMatch
            }
            .sortedWith { a, b ->
                when (sort) {
                    SortOrder.DATE_ASC -> a.createdAt.compareTo(b.createdAt)
                    SortOrder.DATE_DESC -> b.createdAt.compareTo(a.createdAt)
                    SortOrder.NAME_ASC -> a.title.compareTo(b.title, ignoreCase = true)
                    SortOrder.NAME_DESC -> b.title.compareTo(a.title, ignoreCase = true)
                }
            }
            .toList()
    }
}
