package di

import http.LangTestApi
import http.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import res.Resources
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utils.httpBuilder
import java.util.concurrent.TimeUnit

object DataDI {

    val dataModule = module {
        single<OkHttpClient> {
            OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor( 
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.HEADERS
                    }
                ).addInterceptor(TokenInterceptor())
                .build()
        }
        single<Retrofit> {
            Retrofit.Builder()
                .baseUrl(httpBuilder(Resources.HOST, Resources.PORT))
                .addConverterFactory(GsonConverterFactory.create())
                .client(get())
                .build()
        }

        single<LangTestApi> { provideAPI(get()) }
    }

    private fun provideAPI(retrofit: Retrofit): LangTestApi = retrofit.create(LangTestApi::class.java)
}
