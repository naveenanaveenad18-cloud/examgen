package com.example.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "username") val username: String = "",
    @Json(name = "email") val email: String = "",
    @Json(name = "role") val role: String = "Teacher",
    @Json(name = "full_name") val fullName: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "user") val user: UserDto? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "token") val token: String? = null
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "username") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "role") val role: String
)

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "user") val user: UserDto? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class DashboardStats(
    @Json(name = "total_papers") val total_papers: Int = 0,
    @Json(name = "questions_in_bank") val questions_in_bank: Int = 0,
    @Json(name = "blueprints") val blueprints: Int = 0
) {
    val totalPapers: Int get() = total_papers
    val questionsInBank: Int get() = questions_in_bank
}

@JsonClass(generateAdapter = true)
data class GeneratePaperRequest(
    @Json(name = "user_id") val user_id: String,
    @Json(name = "subject") val subject: String,
    @Json(name = "grade") val grade: String,
    @Json(name = "difficulty") val difficulty: String,
    @Json(name = "count") val count: Int,
    @Json(name = "marks") val marks: Int,
    @Json(name = "duration") val duration: Int,
    @Json(name = "types") val types: List<String>,
    @Json(name = "instructions") val instructions: String? = null
) {
    val userId: String get() = user_id
}

@JsonClass(generateAdapter = true)
data class GeneratePaperResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "paper_id") val paper_id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "content") val content: String = "",
    @Json(name = "pdf_url") val pdf_url: String? = null
) {
    val paperId: String get() = paper_id
    val pdfUrl: String? get() = pdf_url
}

@JsonClass(generateAdapter = true)
data class QuestionDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "subject") val subject: String = "",
    @Json(name = "marks") val marks: Int = 1,
    @Json(name = "difficulty") val difficulty: String = "Medium",
    @Json(name = "question_type") val question_type: String = "MCQ",
    @Json(name = "content") val content: String = "",
    @Json(name = "tags") val tags: List<String> = emptyList()
) {
    val questionType: String get() = question_type
}

@JsonClass(generateAdapter = true)
data class AddQuestionRequest(
    @Json(name = "user_id") val user_id: String,
    @Json(name = "subject") val subject: String,
    @Json(name = "marks") val marks: Int,
    @Json(name = "difficulty") val difficulty: String,
    @Json(name = "question_type") val question_type: String,
    @Json(name = "content") val content: String,
    @Json(name = "tags") val tags: List<String> = emptyList()
) {
    val userId: String get() = user_id
    val questionType: String get() = question_type
}

@JsonClass(generateAdapter = true)
data class UpdateQuestionRequest(
    @Json(name = "subject") val subject: String,
    @Json(name = "marks") val marks: Int,
    @Json(name = "difficulty") val difficulty: String,
    @Json(name = "question_type") val question_type: String,
    @Json(name = "content") val content: String,
    @Json(name = "tags") val tags: List<String> = emptyList()
) {
    val questionType: String get() = question_type
}

@JsonClass(generateAdapter = true)
data class PaperSummaryDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "subject") val subject: String = "",
    @Json(name = "grade") val grade: String = "",
    @Json(name = "marks") val marks: Int = 0,
    @Json(name = "duration") val duration: Int = 0,
    @Json(name = "difficulty") val difficulty: String = "",
    @Json(name = "date") val date: String = "",
    @Json(name = "content") val content: String = "",
    @Json(name = "pdf_url") val pdf_url: String? = null
) {
    val pdfUrl: String? get() = pdf_url
}

@JsonClass(generateAdapter = true)
data class GenericResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "message") val message: String? = null
)
