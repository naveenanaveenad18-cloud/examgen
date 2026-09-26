package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.GeneratePaperRequest
import com.example.data.models.PaperSummaryDto
import com.example.data.repository.ExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GenerateUiState(
    val subject: String = "Physics",
    val grade: String = "12",
    val difficulty: String = "Balanced",
    val questionCount: Int = 10,
    val durationMinutes: Int = 90,
    val totalMarks: Int = 50,
    val selectedTypes: Set<String> = setOf("MCQ", "Short", "Long"),
    val instructions: String = "",
    val isGenerating: Boolean = false,
    val error: String? = null,
    val generatedPaper: PaperSummaryDto? = null
)

class GenerateViewModel(
    private val repository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerateUiState())
    val uiState: StateFlow<GenerateUiState> = _uiState.asStateFlow()

    fun updateSubject(value: String) {
        _uiState.value = _uiState.value.copy(subject = value)
    }

    fun updateGrade(value: String) {
        _uiState.value = _uiState.value.copy(grade = value)
    }

    fun updateDifficulty(value: String) {
        _uiState.value = _uiState.value.copy(difficulty = value)
    }

    fun updateQuestionCount(value: Int) {
        _uiState.value = _uiState.value.copy(questionCount = value.coerceIn(3, 50))
    }

    fun updateDuration(value: Int) {
        _uiState.value = _uiState.value.copy(durationMinutes = value.coerceIn(15, 360))
    }

    fun updateTotalMarks(value: Int) {
        _uiState.value = _uiState.value.copy(totalMarks = value.coerceIn(10, 200))
    }

    fun toggleQuestionType(type: String) {
        val current = _uiState.value.selectedTypes.toMutableSet()
        if (current.contains(type)) {
            if (current.size > 1) { // keep at least one
                current.remove(type)
            }
        } else {
            current.add(type)
        }
        _uiState.value = _uiState.value.copy(selectedTypes = current)
    }

    fun updateInstructions(value: String) {
        _uiState.value = _uiState.value.copy(instructions = value)
    }

    fun applyPreset(subject: String, grade: String, marks: Int, duration: Int) {
        _uiState.value = _uiState.value.copy(
            subject = subject,
            grade = grade,
            totalMarks = marks,
            durationMinutes = duration
        )
    }

    fun generatePaper(userId: String) {
        val state = _uiState.value
        if (state.subject.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please specify a subject name")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, error = null)
            val request = GeneratePaperRequest(
                user_id = userId,
                subject = state.subject.trim(),
                grade = state.grade,
                difficulty = state.difficulty,
                count = state.questionCount,
                marks = state.totalMarks,
                duration = state.durationMinutes,
                types = state.selectedTypes.toList(),
                instructions = state.instructions.trim().ifEmpty { null }
            )

            val result = repository.generatePaper(request)
            result.fold(
                onSuccess = { res ->
                    val paper = PaperSummaryDto(
                        id = res.paperId,
                        title = res.title,
                        subject = state.subject,
                        grade = state.grade,
                        marks = state.totalMarks,
                        duration = state.durationMinutes,
                        difficulty = state.difficulty,
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        content = res.content,
                        pdf_url = res.pdfUrl
                    )
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        generatedPaper = paper
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        error = err.localizedMessage ?: "Failed to generate paper"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
