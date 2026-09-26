package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.PaperSummaryDto
import com.example.data.repository.ExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val papers: List<PaperSummaryDto> = emptyList(),
    val isLoading: Boolean = false,
    val previewPaper: PaperSummaryDto? = null,
    val message: String? = null,
    val error: String? = null
)

class HistoryViewModel(
    private val repository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadPapers(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getPapers(userId)
            result.fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(papers = list, isLoading = false)
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = err.localizedMessage ?: "Failed to load papers"
                    )
                }
            )
        }
    }

    fun deletePaper(userId: String, paperId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.deletePaper(paperId)
            loadPapers(userId)
            _uiState.value = _uiState.value.copy(message = "Paper removed from history")
        }
    }

    fun openPreview(paper: PaperSummaryDto) {
        _uiState.value = _uiState.value.copy(previewPaper = paper)
    }

    fun closePreview() {
        _uiState.value = _uiState.value.copy(previewPaper = null)
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}
