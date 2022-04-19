package di

import http.TokenInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import res.Resources
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DI {

    val dataModule = module {
        single<OkHttpClient> {
            OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor( 
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.HEADERS
                    }
                ).addInterceptor(TokenInterceptor(Resources.token))
                .build()
        }
        single<Retrofit> {
            Retrofit.Builder()
                .baseUrl(Resources.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(get())
                .build()
        }
    }
}
