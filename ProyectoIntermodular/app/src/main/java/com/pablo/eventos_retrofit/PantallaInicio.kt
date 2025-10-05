package com.pablo.eventos_retrofit

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.system.exitProcess

//Pantalla de inicio
@Composable
fun PantallaInicio(navController: NavController) {
    val context = LocalContext.current //Contexto para lanzar el Toast de Bienvenida
    var isVisible by rememberSaveable { mutableStateOf(true) } //Para animar el texto de inicio

    //Animación indefinida
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) //Tiempo visible
            isVisible = !isVisible
        }
    }

    //Ponemos t0do en un Box para poder ubicar los elementos a placer
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .testTag("Pantalla de Inicio")
                .clickable {
                    navController.navigate(route = "2-MostrarEventos")
                }
                .verticalScroll(rememberScrollState()), //Con posibilidad de hacer scroll,
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InsertSpacer(32)
            //Título de la app
            Text(text = "Eventos", fontSize = 32.sp)

            Card(
                modifier = Modifier
                    .size(232.dp)
                    .padding(16.dp)
                    .clickable {
                        Toast
                            .makeText(
                                context, "¡Bienvenido a mi app: Eventos!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    },
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de Eventos",
                    modifier = Modifier.requiredSize(200.dp)
                )
            }


            InsertSpacer(32)//Espacio entre el título y los botones

            //Animación para el icono campana
            val value by rememberInfiniteTransition(label = "").animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "Animación desvanecer"
            )

            Text(
                text = "Toca para comenzar",
                modifier = Modifier.alpha(value),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            InsertSpacer(32)

            //Después de los botones ponemos nuestro nombre y una imagen
            Row(verticalAlignment = Alignment.CenterVertically) {
                //Nombre del autor:
                Text(text = "Pablo Palanques Gil - 2025", fontSize = 12.sp)
            }
        }

        //Estado para controlar si el menú está desplegado o no
        var menuExpanded by remember { mutableStateOf(false) }

        SmallFloatingActionButton(
            onClick = {
                menuExpanded = true
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(32.dp),
            contentColor = MaterialTheme.colorScheme.secondary,
            shape = RoundedCornerShape(50)
        ) {
            Icon(Icons.Filled.Menu, "Icono de menú")
            //Menú desplegable
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                //Primera opción del menú
                DropdownMenuItem(
                    text = { Text("Salir") },
                    onClick = {
                        exitProcess(0)
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Close, contentDescription = "Salir")
                    }
                )
            }
        }
    }
}


