package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.datastore.UserPreferencesRepository
import com.example.data.repository.ExamRepository
import com.example.ui.viewmodels.AuthViewModel
import com.example.ui.viewmodels.DashboardViewModel
import com.example.ui.viewmodels.GenerateViewModel
import com.example.ui.viewmodels.HistoryViewModel
import com.example.ui.viewmodels.QuestionBankViewModel

class ExamViewModelFactory(
    private val repository: ExamRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(repository, preferencesRepository) as T
            modelClass.isAssignableFrom(GenerateViewModel::class.java) ->
                GenerateViewModel(repository) as T
            modelClass.isAssignableFrom(QuestionBankViewModel::class.java) ->
                QuestionBankViewModel(repository) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(repository) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
