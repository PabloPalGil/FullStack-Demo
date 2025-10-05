package com.pablo.eventos_retrofit.data


import com.google.gson.Gson //Añadimos Gson para poder serializar la clase Evento
// y poder pasarlos fácilmente entre pantallas

data class Evento(
    val id: String? = null,
    var titulo: String,
    var descripcion: String? = "",
    var categoria: String,
    var fechaRealizacion: String,
    val favorito: Boolean
) {
    //Mét0do para serializar para pasar instancias de Evento en la navegación entre pantallas
    fun toJson(): String = Gson().toJson(this)
    companion object {
        fun fromJson(json: String): Evento = Gson().fromJson(json, Evento::class.java)
    }
}
