package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.DashboardStats
import com.example.data.models.PaperSummaryDto
import com.example.data.repository.ExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val stats: DashboardStats = DashboardStats(total_papers = 2, questions_in_bank = 6, blueprints = 4),
    val recentPapers: List<PaperSummaryDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel(
    private val repository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val statsResult = repository.getDashboardStats(userId)
            val papersResult = repository.getRecentPapers(userId)

            val currentStats = statsResult.getOrDefault(DashboardStats(total_papers = 2, questions_in_bank = 6, blueprints = 4))
            val currentPapers = papersResult.getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(
                stats = currentStats,
                recentPapers = currentPapers,
                isLoading = false
            )
        }
    }
}
