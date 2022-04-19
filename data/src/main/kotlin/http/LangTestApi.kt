package http

import entity.QuizData
import entity.UserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LangTestApi {

    @POST("api/auth/register")
    fun register(@Body user: UserData): Call<String>

    @POST("api/auth/login")
    fun login(@Body user: UserData): Call<String>

    // TODO заменить на body
    @POST("api/quiz/request")
    fun createQuiz(
        @Query("client_id") clientId: Long,
        @Query("words_in_quiz") wordsInQuiz: Int,
        @Query("break_time_in_millis") breakTimeInMillis: Long,
        @Query("language_id") languageId: Long,
        @Query("category_id") categoryId: Long,
        @Query("chat_id") chatID: Long,
    ): Call<QuizData>

    @GET("api/quiz/{client_id}")
    fun getQuizData(@Path("client_id") clientId: Long): Call<QuizData>

    @PUT("api/quiz/{client_id}/update")
    fun updateQuizData(
        @Query("client_id") clientId: Long,
        @Query("words_in_quiz") wordsInQuiz: Int,
        @Query("break_time_in_millis") breakTimeInMillis: Long,
        @Query("language_id") languageId: Long,
        @Query("category_id") categoryId: Long,
        @Query("chat_id") chatID: Long,
    ): Call<QuizData>

    @DELETE("api/quiz/{client_id}/delete")
    fun deleteQuizData(@Path("client_id") clientId: Long): Call<Boolean>
}
