package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.datastore.UserPreferencesRepository
import com.example.data.repository.ExamRepository
import com.example.ui.ExamViewModelFactory
import com.example.ui.screens.MainScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodels.AuthViewModel
import com.example.ui.viewmodels.DashboardViewModel
import com.example.ui.viewmodels.GenerateViewModel
import com.example.ui.viewmodels.HistoryViewModel
import com.example.ui.viewmodels.QuestionBankViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExamGenApp()
                }
            }
        }
    }
}

@Composable
fun ExamGenApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val preferencesRepository = remember { UserPreferencesRepository(context.applicationContext) }
    val repository = remember { ExamRepository(preferencesRepository) }
    val factory = remember { ExamViewModelFactory(repository, preferencesRepository) }

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val generateViewModel: GenerateViewModel = viewModel(factory = factory)
    val questionBankViewModel: QuestionBankViewModel = viewModel(factory = factory)
    val historyViewModel: HistoryViewModel = viewModel(factory = factory)
    val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)

    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "main" else "login"
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                authViewModel = authViewModel,
                generateViewModel = generateViewModel,
                questionBankViewModel = questionBankViewModel,
                historyViewModel = historyViewModel,
                dashboardViewModel = dashboardViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
