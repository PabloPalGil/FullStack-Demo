package com.pablo.eventos_retrofit

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pablo.eventos_retrofit.data.Evento

//NAVEGACION: control de navegacion entre pantallas
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "1-PantallaInicio") {
        composable("1-PantallaInicio") {
            PantallaInicio(navController)
        }
        composable("2-MostrarEventos") {
            MostrarEventos(navController)
        }
        composable(
            route = "3-DetalleEvento/{evento}",
            arguments = listOf(navArgument("evento") { type = NavType.StringType })
        ) { backStackEntry ->
            // Recuperar el objeto
            val eventoJson = backStackEntry.arguments?.getString("evento")
            val evento = Evento.fromJson(eventoJson ?: "")
            DetalleEvento(evento, navController)
        }
        composable(
            route = "4-EditarEvento/{evento}",
            arguments = listOf(navArgument("evento") { type = NavType.StringType })
        ) { backStackEntry ->
            // Recuperar el objeto
            val eventoJson = backStackEntry.arguments?.getString("evento")
            val evento = Evento.fromJson(eventoJson ?: "")
            EditarEvento(evento, navController)
        }
    }
}