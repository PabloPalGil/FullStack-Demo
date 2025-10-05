@file:Suppress("SpellCheckingInspection")

package com.pablo.eventos_retrofit

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.pablo.eventos_retrofit.data.Evento
import com.pablo.eventos_retrofit.data.EventoApiService
import com.pablo.eventos_retrofit.ui.theme.EventosRetrofitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var apiService: EventoApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventosRetrofitTheme {
                AppNavigation()
            }
        }
    }
}

//Función que devuelve un Evento vacío y con id = null
fun getNewEvento(): Evento {
    return Evento(
        null,
        "",
        "",
        "",
        "2025-01-01T00:00:00",
        false
    )
}


//ORIENTACIÓN: función que devuelve true si el dispositivo está en horizontal

@Composable
fun orientacionHorizontal(): Boolean {
    val configuration = LocalConfiguration.current //variable que indica la orientación actual

    val horizontal: Boolean = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> true
        else -> false
    }
    return horizontal
}


//Esta función inserta Spacers del tamaño indicado
@Composable
fun InsertSpacer(n: Int) {
    Spacer(modifier = Modifier.size(n.dp))
}



