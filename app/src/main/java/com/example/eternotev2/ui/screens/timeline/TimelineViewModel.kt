package com.example.eternotev2.ui.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.repository.CapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class TimelineUiState(
    val isLoading: Boolean = true,
    val groupedCapsules: Map<String, List<Capsule>> = emptyMap(),
    val collapsedMonths: Set<String> = emptySet(),
    val errorMessage: String? = null
) {
    val isEmpty: Boolean get() = groupedCapsules.isEmpty() && !isLoading
}

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val repository: CapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        loadTimeline()
    }

    private fun loadTimeline() {
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        
        repository.getCapsulesChronological()
            .map { list ->
                // Group capsules by month and year, sorted newest first
                list.sortedByDescending { it.createdAt }
                    .groupBy { formatter.format(Date(it.createdAt)) }
            }
            .onEach { grouped ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        groupedCapsules = grouped
                    )
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun toggleMonth(monthYear: String) {
        _uiState.update { state ->
            val newCollapsed = if (state.collapsedMonths.contains(monthYear)) {
                state.collapsedMonths - monthYear
            } else {
                state.collapsedMonths + monthYear
            }
            state.copy(collapsedMonths = newCollapsed)
        }
    }
}
