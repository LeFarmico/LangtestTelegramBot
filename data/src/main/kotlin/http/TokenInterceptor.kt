package http

import okhttp3.Interceptor
import okhttp3.Response
import res.Resources

class TokenInterceptor : Interceptor {

    private val token get() = Resources.token

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        if (original.url.encodedPath.contains("/login") && original.method == "post" ||
            original.url.encodedPath.contains("/register") && original.url.encodedPath == "post"
        ) {
            return chain.proceed(original)
        }

        val originalHttpUrl = original.url
        val requestBuilder = original.newBuilder().addHeader("Authorization", token)
            .url(originalHttpUrl)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
