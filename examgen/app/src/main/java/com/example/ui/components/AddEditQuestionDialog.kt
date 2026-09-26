package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.models.QuestionDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditQuestionDialog(
    initialQuestion: QuestionDto? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        subject: String,
        marks: Int,
        difficulty: String,
        type: String,
        content: String,
        tags: List<String>
    ) -> Unit
) {
    var subject by remember { mutableStateOf(initialQuestion?.subject ?: "Physics") }
    var marksText by remember { mutableStateOf((initialQuestion?.marks ?: 2).toString()) }
    var content by remember { mutableStateOf(initialQuestion?.content ?: "") }
    var tagsText by remember { mutableStateOf(initialQuestion?.tags?.joinToString(", ") ?: "Important, Unit 1") }

    val difficulties = listOf("Easy", "Medium", "Hard", "Balanced")
    var diffExpanded by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(initialQuestion?.difficulty ?: "Medium") }

    val questionTypes = listOf("MCQ", "Short", "Long")
    var typeExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(initialQuestion?.question_type ?: "Short") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = if (initialQuestion == null) "Add New Question" else "Edit Question",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("question_subject_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Difficulty dropdown
                    ExposedDropdownMenuBox(
                        expanded = diffExpanded,
                        onExpandedChange = { diffExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedDifficulty,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Difficulty") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diffExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = diffExpanded,
                            onDismissRequest = { diffExpanded = false }
                        ) {
                            difficulties.forEach { diff ->
                                DropdownMenuItem(
                                    text = { Text(diff) },
                                    onClick = {
                                        selectedDifficulty = diff
                                        diffExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Question Type dropdown
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            questionTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        selectedType = type
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = marksText,
                    onValueChange = { marksText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Marks") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("question_marks_input")
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Question Content / Problem Statement") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("question_content_input")
                )

                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Tags (comma separated)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("question_tags_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val marks = marksText.toIntOrNull() ?: 2
                    val parsedTags = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    onConfirm(subject, marks, selectedDifficulty, selectedType, content, parsedTags)
                },
                modifier = Modifier.testTag("save_question_button")
            ) {
                Text(if (initialQuestion == null) "Add Question" else "Update")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
