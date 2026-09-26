package com.example.data.api

import com.example.data.models.AddQuestionRequest
import com.example.data.models.DashboardStats
import com.example.data.models.GeneratePaperRequest
import com.example.data.models.GeneratePaperResponse
import com.example.data.models.GenericResponse
import com.example.data.models.LoginRequest
import com.example.data.models.LoginResponse
import com.example.data.models.PaperSummaryDto
import com.example.data.models.QuestionDto
import com.example.data.models.RegisterRequest
import com.example.data.models.RegisterResponse
import com.example.data.models.UpdateQuestionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ExamApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("/api/dashboard/stats")
    suspend fun getDashboardStats(@Query("user_id") userId: String): Response<DashboardStats>

    @GET("/api/dashboard/recent-papers")
    suspend fun getRecentPapers(@Query("user_id") userId: String): Response<List<PaperSummaryDto>>

    @POST("/api/generate")
    suspend fun generatePaper(@Body request: GeneratePaperRequest): Response<GeneratePaperResponse>

    @GET("/api/question-bank")
    suspend fun getQuestionBank(
        @Query("user_id") userId: String,
        @Query("difficulty") difficulty: String? = null,
        @Query("query") query: String? = null
    ): Response<List<QuestionDto>>

    @POST("/api/question-bank/add")
    suspend fun addQuestion(@Body request: AddQuestionRequest): Response<GenericResponse>

    @PUT("/api/question-bank/update/{id}")
    suspend fun updateQuestion(
        @Path("id") id: String,
        @Body request: UpdateQuestionRequest
    ): Response<GenericResponse>

    @DELETE("/api/question-bank/delete/{id}")
    suspend fun deleteQuestion(@Path("id") id: String): Response<GenericResponse>

    @GET("/api/papers")
    suspend fun getPapers(@Query("user_id") userId: String): Response<List<PaperSummaryDto>>

    @DELETE("/api/paper/{id}")
    suspend fun deletePaper(@Path("id") id: String): Response<GenericResponse>
}
