package com.example.ui.screens.bank

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.QuestionDto
import com.example.ui.components.AddEditQuestionDialog
import com.example.ui.viewmodels.QuestionBankViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuestionBankScreen(
    userId: String,
    viewModel: QuestionBankViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadQuestions(userId)
    }

    val difficulties = listOf("All", "Easy", "Medium", "Hard", "Balanced")
    var diffExpanded by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                modifier = Modifier.testTag("add_question_fab"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Question")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search & Filter Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it, userId) },
                    placeholder = { Text("Search topics, content, tags...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("", userId) }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("question_search_input")
                )

                // Difficulty Filter Dropdown
                ExposedDropdownMenuBox(
                    expanded = diffExpanded,
                    onExpandedChange = { diffExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedDifficulty,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Difficulty") },
                        leadingIcon = { Icon(imageVector = Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diffExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .testTag("difficulty_filter_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = diffExpanded,
                        onDismissRequest = { diffExpanded = false }
                    ) {
                        difficulties.forEach { diff ->
                            DropdownMenuItem(
                                text = { Text(diff) },
                                onClick = {
                                    viewModel.onDifficultyFilterChanged(diff, userId)
                                    diffExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.questions.size} Questions Found",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading && uiState.questions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.questions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No questions in bank yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the '+' button below to add your first question or adjust your search filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.questions, key = { it.id }) { question ->
                        QuestionItemCard(
                            question = question,
                            onEdit = { viewModel.openEditDialog(question) },
                            onDelete = { viewModel.deleteQuestion(userId, question.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp)) // Clearance for FAB
                    }
                }
            }
        }
    }

    // Add Dialog
    if (uiState.isAddDialogOpen) {
        AddEditQuestionDialog(
            initialQuestion = null,
            onDismiss = { viewModel.closeAddDialog() },
            onConfirm = { subject, marks, diff, type, content, tags ->
                viewModel.addQuestion(userId, subject, marks, diff, type, content, tags)
            }
        )
    }

    // Edit Dialog
    uiState.editingQuestion?.let { question ->
        AddEditQuestionDialog(
            initialQuestion = question,
            onDismiss = { viewModel.closeEditDialog() },
            onConfirm = { subject, marks, diff, type, content, tags ->
                viewModel.updateQuestion(userId, question.id, subject, marks, diff, type, content, tags)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionItemCard(
    question: QuestionDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_card_${question.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Subject, Type badge, Difficulty badge, Marks, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = question.subject,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = question.question_type,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    val diffColor = when (question.difficulty.lowercase()) {
                        "easy" -> Color(0xFF10B981)
                        "hard" -> Color(0xFFEF4444)
                        "medium" -> Color(0xFFF59E0B)
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                    Box(
                        modifier = Modifier
                            .background(diffColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = question.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            color = diffColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "• ${question.marks}M",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("edit_question_${question.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Question",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_question_${question.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Question",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Question content
            Text(
                text = question.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Tags
            if (question.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    question.tags.forEach { tag ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text("#$tag", fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = null
                        )
                    }
                }
            }
        }
    }
}
