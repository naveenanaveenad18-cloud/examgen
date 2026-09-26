package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.AddQuestionRequest
import com.example.data.models.QuestionDto
import com.example.data.models.UpdateQuestionRequest
import com.example.data.repository.ExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuestionBankUiState(
    val questions: List<QuestionDto> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedDifficulty: String = "All",
    val isAddDialogOpen: Boolean = false,
    val editingQuestion: QuestionDto? = null,
    val error: String? = null,
    val message: String? = null
)

class QuestionBankViewModel(
    private val repository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestionBankUiState())
    val uiState: StateFlow<QuestionBankUiState> = _uiState.asStateFlow()

    fun loadQuestions(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val diffParam = if (_uiState.value.selectedDifficulty == "All") null else _uiState.value.selectedDifficulty
            val queryParam = _uiState.value.searchQuery.ifBlank { null }
            val result = repository.getQuestionBank(userId, diffParam, queryParam)
            result.fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(questions = list, isLoading = false)
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = err.localizedMessage ?: "Failed to load question bank"
                    )
                }
            )
        }
    }

    fun onSearchQueryChanged(newQuery: String, userId: String) {
        _uiState.value = _uiState.value.copy(searchQuery = newQuery)
        loadQuestions(userId)
    }

    fun onDifficultyFilterChanged(difficulty: String, userId: String) {
        _uiState.value = _uiState.value.copy(selectedDifficulty = difficulty)
        loadQuestions(userId)
    }

    fun openAddDialog() {
        _uiState.value = _uiState.value.copy(isAddDialogOpen = true)
    }

    fun closeAddDialog() {
        _uiState.value = _uiState.value.copy(isAddDialogOpen = false)
    }

    fun openEditDialog(question: QuestionDto) {
        _uiState.value = _uiState.value.copy(editingQuestion = question)
    }

    fun closeEditDialog() {
        _uiState.value = _uiState.value.copy(editingQuestion = null)
    }

    fun addQuestion(
        userId: String,
        subject: String,
        marks: Int,
        difficulty: String,
        type: String,
        content: String,
        tags: List<String>
    ) {
        if (subject.isBlank() || content.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Subject and Content cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val request = AddQuestionRequest(
                user_id = userId,
                subject = subject.trim(),
                marks = marks,
                difficulty = difficulty,
                question_type = type,
                content = content.trim(),
                tags = tags
            )
            repository.addQuestion(request)
            closeAddDialog()
            loadQuestions(userId)
            _uiState.value = _uiState.value.copy(message = "Question added successfully")
        }
    }

    fun updateQuestion(
        userId: String,
        id: String,
        subject: String,
        marks: Int,
        difficulty: String,
        type: String,
        content: String,
        tags: List<String>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val request = UpdateQuestionRequest(
                subject = subject.trim(),
                marks = marks,
                difficulty = difficulty,
                question_type = type,
                content = content.trim(),
                tags = tags
            )
            repository.updateQuestion(id, request)
            closeEditDialog()
            loadQuestions(userId)
            _uiState.value = _uiState.value.copy(message = "Question updated")
        }
    }

    fun deleteQuestion(userId: String, id: String) {
        viewModelScope.launch {
            repository.deleteQuestion(id)
            loadQuestions(userId)
            _uiState.value = _uiState.value.copy(message = "Question deleted")
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(error = null, message = null)
    }
}
