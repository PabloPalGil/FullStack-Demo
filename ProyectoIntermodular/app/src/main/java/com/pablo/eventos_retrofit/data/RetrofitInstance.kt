package com.pablo.eventos_retrofit.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
//    private const val BASE_URL = "http://localhost:8080" //Url del servidor backend
    private const val BASE_URL = "http://10.0.2.2:8080" //Url del localhost del equipo
    // (porque localhost apunta al emulador de android)

    val api: EventoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventoApiService::class.java)
    }
}
