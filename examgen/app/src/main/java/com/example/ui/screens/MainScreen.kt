package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.ServerSettingsDialog
import com.example.ui.screens.bank.QuestionBankScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.generate.GeneratePaperScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.viewmodels.AuthViewModel
import com.example.ui.viewmodels.DashboardViewModel
import com.example.ui.viewmodels.GenerateViewModel
import com.example.ui.viewmodels.HistoryViewModel
import com.example.ui.viewmodels.QuestionBankViewModel

sealed class TabItem(
    val index: Int,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Generate : TabItem(0, "Generate", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_generate")
    object QuestionBank : TabItem(1, "Question Bank", Icons.Filled.Quiz, Icons.Outlined.Quiz, "tab_question_bank")
    object History : TabItem(2, "History", Icons.Filled.History, Icons.Outlined.History, "tab_history")
    object Dashboard : TabItem(3, "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "tab_dashboard")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    generateViewModel: GenerateViewModel,
    questionBankViewModel: QuestionBankViewModel,
    historyViewModel: HistoryViewModel,
    dashboardViewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentServerUrl by authViewModel.serverUrl.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showServerSettingsDialog by remember { mutableStateOf(false) }

    val tabs = listOf(
        TabItem.Generate,
        TabItem.QuestionBank,
        TabItem.History,
        TabItem.Dashboard
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedTabIndex) {
                            0 -> "ExamGen • Generator"
                            1 -> "ExamGen • Question Bank"
                            2 -> "ExamGen • Paper History"
                            else -> "ExamGen • Dashboard"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showServerSettingsDialog = true },
                        modifier = Modifier.testTag("top_bar_server_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "Server Settings"
                        )
                    }

                    IconButton(
                        onClick = {
                            authViewModel.logout()
                            onLogout()
                        },
                        modifier = Modifier.testTag("top_bar_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTabIndex == tab.index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = tab.index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> GeneratePaperScreen(
                    userId = currentUser.id,
                    viewModel = generateViewModel
                )
                1 -> QuestionBankScreen(
                    userId = currentUser.id,
                    viewModel = questionBankViewModel
                )
                2 -> HistoryScreen(
                    userId = currentUser.id,
                    viewModel = historyViewModel
                )
                3 -> DashboardScreen(
                    user = currentUser,
                    viewModel = dashboardViewModel,
                    onNavigateToGenerateWithPreset = { subject, grade, marks, duration ->
                        generateViewModel.applyPreset(subject, grade, marks, duration)
                        selectedTabIndex = 0
                    },
                    onNavigateToQuestionBank = { selectedTabIndex = 1 },
                    onNavigateToHistory = { selectedTabIndex = 2 }
                )
            }
        }
    }

    if (showServerSettingsDialog) {
        ServerSettingsDialog(
            currentUrl = currentServerUrl,
            onDismiss = { showServerSettingsDialog = false },
            onSave = { newUrl -> authViewModel.updateServerUrl(newUrl) }
        )
    }
}
