package com.example.ui.screens.generate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PaperPreviewDialog
import com.example.ui.viewmodels.GenerateViewModel
import com.example.util.PdfExporter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GeneratePaperScreen(
    userId: String,
    viewModel: GenerateViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val grades = listOf("8", "9", "10", "11", "12", "College")
    var gradeExpanded by remember { mutableStateOf(false) }

    val difficulties = listOf("Easy", "Medium", "Hard", "Balanced")
    var diffExpanded by remember { mutableStateOf(false) }

    var showFullPreviewDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero title card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Automated Exam Generator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Configure curriculum parameters to generate balanced question papers with live preview & instant PDF export.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SuggestionChip(
                        onClick = { viewModel.applyPreset("Physics", "12", 70, 180) },
                        label = { Text("Physics 12th (70M)") }
                    )
                    SuggestionChip(
                        onClick = { viewModel.applyPreset("Mathematics", "10", 80, 150) },
                        label = { Text("Math 10th (80M)") }
                    )
                    SuggestionChip(
                        onClick = { viewModel.applyPreset("Computer Science", "College", 100, 120) },
                        label = { Text("CS College (100M)") }
                    )
                }
            }
        }

        // Form Inputs Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Exam Specifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                // Subject input
                OutlinedTextField(
                    value = uiState.subject,
                    onValueChange = { viewModel.updateSubject(it) },
                    label = { Text("Subject Name") },
                    placeholder = { Text("e.g. Physics, Chemistry, Algorithms") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subject_input")
                )

                // Grade and Difficulty dropdowns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Grade Dropdown
                    ExposedDropdownMenuBox(
                        expanded = gradeExpanded,
                        onExpandedChange = { gradeExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "Grade ${uiState.grade}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Grade / Class") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .testTag("grade_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = gradeExpanded,
                            onDismissRequest = { gradeExpanded = false }
                        ) {
                            grades.forEach { grade ->
                                DropdownMenuItem(
                                    text = { Text(if (grade == "College") "College" else "Grade $grade") },
                                    onClick = {
                                        viewModel.updateGrade(grade)
                                        gradeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Difficulty Dropdown
                    ExposedDropdownMenuBox(
                        expanded = diffExpanded,
                        onExpandedChange = { diffExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = uiState.difficulty,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Difficulty") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diffExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .testTag("difficulty_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = diffExpanded,
                            onDismissRequest = { diffExpanded = false }
                        ) {
                            difficulties.forEach { diff ->
                                DropdownMenuItem(
                                    text = { Text(diff) },
                                    onClick = {
                                        viewModel.updateDifficulty(diff)
                                        diffExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Number of Questions Stepper & Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Number of Questions: ${uiState.questionCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(
                                onClick = { viewModel.updateQuestionCount(uiState.questionCount - 1) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledTonalIconButton(
                                onClick = { viewModel.updateQuestionCount(uiState.questionCount + 1) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }
                    Slider(
                        value = uiState.questionCount.toFloat(),
                        onValueChange = { viewModel.updateQuestionCount(it.toInt()) },
                        valueRange = 3f..40f,
                        steps = 36,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("question_count_slider")
                    )
                }

                // Duration and Total Marks Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.durationMinutes.toString(),
                        onValueChange = { str ->
                            str.filter { it.isDigit() }.toIntOrNull()?.let { viewModel.updateDuration(it) }
                        },
                        label = { Text("Duration (Mins)") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("duration_input")
                    )

                    OutlinedTextField(
                        value = uiState.totalMarks.toString(),
                        onValueChange = { str ->
                            str.filter { it.isDigit() }.toIntOrNull()?.let { viewModel.updateTotalMarks(it) }
                        },
                        label = { Text("Total Marks") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("total_marks_input")
                    )
                }

                // Question Types Checkboxes
                Column {
                    Text(
                        text = "Include Question Types:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("MCQ" to "MCQ (1-2M)", "Short" to "Short (3-5M)", "Long" to "Long (5-10M)").forEach { (typeKey, label) ->
                            val isChecked = uiState.selectedTypes.contains(typeKey)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.testTag("checkbox_$typeKey")
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { viewModel.toggleQuestionType(typeKey) }
                                )
                                Text(label, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // Optional Syllabus Instructions
                OutlinedTextField(
                    value = uiState.instructions,
                    onValueChange = { viewModel.updateInstructions(it) },
                    label = { Text("Custom Syllabus / Chapter Focus (Optional)") },
                    placeholder = { Text("e.g. Focus on Optics, Thermodynamics, Numerical problems") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("instructions_input")
                )

                // Generate Button
                Button(
                    onClick = { viewModel.generatePaper(userId) },
                    enabled = !uiState.isGenerating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_paper_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (uiState.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Generating Paper...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Question Paper", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Generated Paper Card
        AnimatedVisibility(
            visible = uiState.generatedPaper != null,
            enter = fadeIn() + expandVertically()
        ) {
            uiState.generatedPaper?.let { paper ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_paper_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Generated Successfully",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = "${paper.marks} Marks • ${paper.duration}m",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = paper.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Class/Grade: ${paper.grade}  |  Difficulty: ${paper.difficulty}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Preview Content Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = paper.content.lines().take(12).joinToString("\n") + "\n...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: View Full Preview & Download / View PDF
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showFullPreviewDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Preview")
                            }

                            Button(
                                onClick = {
                                    PdfExporter.openUrlOrFile(context, paper.pdf_url ?: "", paper)
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("download_pdf_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download / View PDF")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFullPreviewDialog && uiState.generatedPaper != null) {
        PaperPreviewDialog(
            paper = uiState.generatedPaper!!,
            onDismiss = { showFullPreviewDialog = false }
        )
    }
}
