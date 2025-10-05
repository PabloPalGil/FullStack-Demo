package com.pablo.eventos_retrofit.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EventoApiService {
    @GET("/eventos")
    suspend fun obtenerEventos(): Response<List<Evento>>

    @PUT("/eventos/{id}")
    suspend fun actualizarEvento(@Path("id") id: String, @Body evento: Evento): Response<Evento>

    @POST("/eventos")
    suspend fun agregarEvento(@Body evento: Evento): Response<Evento>

    @DELETE("/eventos/{id}")
    suspend fun eliminarEvento(@Path("id") id: String): Response<Unit>
}