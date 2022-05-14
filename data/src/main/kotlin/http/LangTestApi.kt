package http

import entity.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LangTestApi {

    // +
    @POST("api/auth/register")
    fun register(@Body user: UserData): Call<JwtData>

    // +
    @POST("api/auth/login")
    fun login(@Body user: UserData): Call<JwtData>

    // +
    // TODO заменить на body
    @POST("api/quiz/create")
    fun createQuiz(
        @Query("chat_id") chatId: Long,
        @Query("words_in_quiz") wordsInQuiz: Int,
        @Query("break_time_in_millis") breakTimeInMillis: Long,
        @Query("language_id") languageId: Long,
        @Query("category_id") categoryId: Long,
    ): Call<QuizData>
    // +
    @GET("api/quiz/{chat_id}")
    fun getQuizData(@Path("chat_id") chatId: Long): Call<QuizData>

    // +
    @PUT("api/quiz/{chat_id}/update")
    fun updateQuizData(
        @Path("chat_id") chatId: Long,
        @Query("words_in_quiz") wordsInQuiz: Int? = null,
        @Query("break_time_in_millis") breakTimeInMillis: Long? = null,
        @Query("language_id") languageId: Long? = null,
        @Query("category_id") categoryId: Long? = null,
    ): Call<QuizData>

    // +
    @DELETE("api/quiz/{chat_id}/delete")
    fun deleteQuizData(@Path("chat_id") chatId: Long): Call<String>

    // +
    @GET("api/data/language")
    fun getAvailableLanguages(): Call<List<Language>>

    // +
    @GET("api/data/language/{language_id}/category")
    fun getAvailableCategories(@Path("language_id") languageId: Long): Call<List<Category>>

    // +
    @GET("api/data/language/{language_id}")
    fun getLanguageById(@Path("language_id") languageId: Long): Call<Language>

    // +
    @GET("api/data/language/category/{category_id}")
    fun getCategoryById(@Path("category_id") categoryId: Long): Call<Category>

    @GET("api/quiz/{chat_id}/quiz_word/next")
    fun getNextQuizWord(@Path("chat_id") chatId: Long): Call<QuizWord>

    // +
    @POST("api/quiz/{chat_id}/createQuizWords")
    fun createQuizWords(@Path("chat_id") chatId: Long): Call<List<QuizWord>>

    @PUT("api/quiz/{chat_id}/quiz_word/{quiz_word_id}")
    fun setAnswerForQuizWord(
        @Path("chat_id") chatId: Long,
        @Path("quiz_word_id") wordId: Long,
        @Query("answer") answer: Boolean
    ): Call<QuizWordStats>

    @PUT("api/quiz/{chat_id}/resetQuiz")
    fun resetQuiz(@Path("chat_id") chatId: Long): Call<String>

    @PUT("api/quiz/{chat_id}/resetQuizWordNumber")
    fun resetQuizWordNumber(@Path("chat_id") chatId: Long): Call<String>
}
