package com.pablo.eventos_retrofit

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pablo.eventos_retrofit.data.Evento
import com.pablo.eventos_retrofit.data.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Función que devuelve la lista de Eventos del servidor (o una lista vacía si falla)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun obtenerEventos(context: Context): List<Evento> {
    //Recabamos los eventos de la bd con retrofit:
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.obtenerEventos()
            if (response.isSuccessful) {
                eventos = response.body()!!
                Log.d("API", "Eventos recibidos: ${eventos.count()}")
            } else {
                Log.e("API", "Error al solicitar eventos: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("API", "Excepción: ${e.message}")
        }
    }
    return eventos
}


//Funcion que actualiza un evento del servidor por su Id
fun actualizarEvento(eventoId: String, evento: Evento, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.actualizarEvento(eventoId, evento)
            if (response.isSuccessful) {
                val eventoActualizado = response.body()
                Log.d("API", "Evento actualizado: $eventoActualizado")

                //Cambiamos al hilo principal para actualizar la IU
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Evento actualizado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("API", "Error al actualizar: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("API", "Excepción: ${e.message}")
        }
    }
}


//Funcion que añade un evento al servidor
fun agregarEvento(eventoNuevo: Evento, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.agregarEvento(eventoNuevo)
            if (response.isSuccessful) {
                val eventoCreado = response.body()
                Log.d("API", "Evento creado: $eventoCreado")

                //Cambiamos al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Evento creado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("API", "Error al crear: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("API", "Excepción: ${e.message}")
        }
    }
}


//Funcion que elimina un evento del servidor por su Id
fun eliminarEvento(eventoId: String, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.eliminarEvento(eventoId)
            if (response.isSuccessful) {
                Log.d("API", "Evento eliminado correctamente")

                //Cambiamos al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Evento eliminado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("API", "Error al eliminar: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("API", "Excepción: ${e.message}")
        }
    }
}