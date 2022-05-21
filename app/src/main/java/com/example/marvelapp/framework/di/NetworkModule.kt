package com.example.marvelapp.framework.di

import com.example.core.data.network.interceptor.AuthorizationInterceptor
import com.example.marvelapp.BuildConfig
import com.example.marvelapp.util.Constants.TIMEOUT_SECONDS
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

val networkModule = module {
    factory { provideLoggingInterceptor() }
    factory { provideOkHttpClient(get<HttpLoggingInterceptor>(), get<AuthorizationInterceptor>()) }
    factory { provideGsonConverterFactory() }
    factory { provideAuthorizationInterceptor() }
    single { provideRetrofit(get<OkHttpClient>(), get<GsonConverterFactory>()) }
}

fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        setLevel(
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else HttpLoggingInterceptor.Level.NONE
        )
    }
}

fun provideAuthorizationInterceptor(): AuthorizationInterceptor {
    return AuthorizationInterceptor(
        publicKey = BuildConfig.PUBLIC_KEY,
        privateKey = BuildConfig.PRIVATE_KEY,
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    )
}

fun provideOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor,
    authorizationInterceptor: AuthorizationInterceptor
): OkHttpClient {

    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authorizationInterceptor)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
}

fun provideGsonConverterFactory(): GsonConverterFactory {
    return GsonConverterFactory.create()
}

fun provideRetrofit(
    okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build()
}


