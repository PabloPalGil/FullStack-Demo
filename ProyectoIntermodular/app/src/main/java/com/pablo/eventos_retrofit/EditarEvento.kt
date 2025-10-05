package com.pablo.eventos_retrofit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pablo.eventos_retrofit.data.Evento
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


//Pantalla con los campos que permiten tanto editar un evento existente como crear un evento nuevo
//Si la función es crear un evento nuevo, se recibe un Evento con id = null
//Si la función es editar un Evento existente, ya cuenta con un id (autogenerado por mongodb)
@Composable
fun EditarEvento(
    eventoOriginal: Evento,
    navController: NavController
) {
    //Hasta el momento de Guardar, trabajamos con una copia del evento para actualizar la IU
    val evento = eventoOriginal

    //Variables de estado para reflejar los cambios en la IU
    var titulo by rememberSaveable { mutableStateOf(evento.titulo) } //Titulo
    var descripcion by rememberSaveable { mutableStateOf(evento.descripcion) } //Descripción
    var categoria by rememberSaveable { mutableStateOf(evento.categoria) } //Categoría

    //Variables para manejar la fecha y hora del evento
    //Las fechas que obtenemos de mongodb siguen el formato ISO 8601: "yyyy-MM-dd'T'HH:mm:ss"
    val diaString = evento.fechaRealizacion.split('T')[0] //String con la fecha "yyyy-MM-dd"
    val horaString = evento.fechaRealizacion.split('T')[1] //String con la hora "HH-mm-ss"
    val horaInt = horaString.split(':')[0].toIntOrNull() ?: 0
    val minutoInt = horaString.split(':')[1].toIntOrNull() ?: 0

    //Variable de estado de la fecha
    var fechaDia by rememberSaveable { mutableStateOf(diaString) }

    //Variable de estado de la hora
    var fechaHora by rememberSaveable { mutableIntStateOf(horaInt) }

    //Variable de estado del minuto
    var fechaMinuto by rememberSaveable { mutableIntStateOf(minutoInt) }

    //Formato de fecha requerido (formato ISO 8601 de mongodb: "yyyy-MM-dd'T'HH:mm:ss")
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    //Variable de estado de favorito
    var favorito by rememberSaveable { mutableStateOf(eventoOriginal.favorito) }

    //Variables de estado de campos no válidos
    var errorTitulo by rememberSaveable { mutableStateOf(false) }
    var errorCategoria by rememberSaveable { mutableStateOf(false) }
    //Variable para controlar la entrada de Fechas:
    var esFechaValida by remember { mutableStateOf(true) }

    val context = LocalContext.current //Contexto para lanzar

    fun eventoActualizado(): Evento {
        val hora = if (fechaHora < 10) "0${fechaHora}" else fechaHora.toString()
        val min = if (fechaMinuto < 10) "0${fechaMinuto}" else fechaMinuto.toString()

        val eventoNuevo = Evento(
            id = eventoOriginal.id,
            titulo,
            descripcion,
            categoria,
            fechaRealizacion = fechaDia + "T" + hora + ":" + min + ":00",
            favorito
        )
        return eventoNuevo
    }


    //ANIMACIONES

    //Animación para el icono favorito
    val sizeAnimHeart by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 25f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Rotation animation"
    )

    //Animaciones de estado
    val animColor1 by animateColorAsState(
        targetValue = if (favorito) Color.Red else Color.Black,
        animationSpec = tween(durationMillis = 800),
        label = "Animación color",
    )

    var isVisible by remember { mutableStateOf(false) }

    //Activamos los elementos tras un intervalo
    LaunchedEffect(Unit) {
        delay(1)
        isVisible = true
    }

    Scaffold(
        topBar = { //TopAppBar
            when {
                evento.id != null -> EdicionTopAppBar(evento, navController)
                else -> CreacionTopAppBar()
            }
        },
        bottomBar = { //BottomAppBar
            when {
                evento.id != null -> EdicionBottomAppBar(navController, eventoOriginal)
                else -> CreacionBottomAppBar(navController)
            }
        },
        floatingActionButton = {}
    ) { paddingValues ->

        //Animación que combina desplazamiento y desvanecimiento
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it / 2 }, //Comienza desde la mitad de su altura
                animationSpec = tween(durationMillis = 400) //Duración de la animación
            ) + fadeIn(animationSpec = tween(durationMillis = 400)), //Combina con desvanecimiento
            exit = slideOutVertically(
                targetOffsetY = { -it / 2 }, //Desaparece hacia arriba
                animationSpec = tween(durationMillis = 400)
            ) + fadeOut(animationSpec = tween(durationMillis = 400)) //Combina con desvanecimiento
        ) {

            //LAYOUT CON LOS CAMPOS PARA EDITAR UN EVENTO:
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(//Título
                    value = titulo,
                    onValueChange = {
                        titulo = it
                        errorTitulo = titulo.isBlank()
                    },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Title,
                            contentDescription = "Título"
                        )
                    }
                )
                if (errorTitulo)
                    Text(
                        text = "*Obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag("Titulo es obligatorio")
                    )

                InsertSpacer(8)
                OutlinedTextField(//Categoría
                    value = categoria,
                    onValueChange = {
                        categoria = it
                        errorCategoria = categoria.isBlank()
                    },
                    label = { Text("Categoría") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Category,
                            contentDescription = "Categoría"
                        )
                    }
                )
                if (errorCategoria)
                    Text(
                        text = "*Obligatorio",
                        color = MaterialTheme.colorScheme.error
                    )

                InsertSpacer(8)
                OutlinedTextField(//Descripción
                    value = descripcion ?: "",
                    onValueChange = {
                        descripcion = it
                    },
                    label = { Text("Descripción") },
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .height(200.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = "Descripción"
                        )
                    }
                )
                InsertSpacer(8)

                //FECHA DIA
                OutlinedTextField(
                    value = fechaDia,
                    onValueChange = {
                        fechaDia = it
                        esFechaValida = try {
                            //Intentamos parsear la fecha
                            LocalDate.parse(it, dateFormatter)
                            true
                        } catch (e: DateTimeParseException) {
                            //Si ha dado error es que el formato de fecha no es correcto
                            false
                        }
                    },
                    label = { Text("Fecha (yyyy-MM-dd)") },
                    placeholder = {
                        Text(
                            text = "Ejemplo: 2025-02-15",
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !esFechaValida,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.EditCalendar,
                            contentDescription = "Fecha del evento"
                        )
                    }
                )
                if (!esFechaValida) {
                    Text(
                        "Fecha no válida. Usa el formato yyyy-MM-dd",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (fechaDia.isNotEmpty()) {
                    val parsedDate = LocalDate.parse(fechaDia, dateFormatter)
                    Text("Fecha válida: ${parsedDate}.")
                }
                InsertSpacer(8)

                //HORA
                Text(
                    text = "Ajusta la hora del evento:", fontSize = 16.sp
                )
                //Mostrar barra de progreso:
                val rangeH = 0f..23f//Valores min y max
                val stepH = 21//Pasos intermedios de un extremo al otro
                //Colocamos el slider
                Slider(
                    value = fechaHora.toFloat(),
                    valueRange = rangeH,
                    steps = stepH,
                    onValueChange = { fechaHora = it.toInt() },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                //MINUTOS
                Text(
                    text = "Minutos:", fontSize = 16.sp
                )
                //Mostrar barra de progreso:
                val rangeM = 0f..59f//Valores min y max
                val stepM = 57//Pasos intermedios de un extremo al otro
                Slider(
                    value = fechaMinuto.toFloat(),
                    valueRange = rangeM,
                    steps = stepM,
                    onValueChange = { fechaMinuto = it.toInt() },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Text(
                    text = "A las ${fechaHora}:${fechaMinuto}h.",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                InsertSpacer(16)
                //FAVORITO
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Favorito: ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = {
                            favorito = !favorito
                        },
                        modifier = Modifier.testTag("Alternar favorito")
                    ) {
                        if (favorito) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Marcado favorito",
                                tint = animColor1,
                                modifier = Modifier.size(sizeAnimHeart.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = "Sin marcar favorito",
                                tint = animColor1,
                                modifier = Modifier.size(sizeAnimHeart.dp)
                            )
                        }
                    }
                }

                InsertSpacer(16)

                //BOTONES DE ACCIÓN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        //Diferenciamos según estamos editando un evento o creando uno nuevo
                        if (evento.id != null) //Editando evento existente
                            navController.navigate("3-DetalleEvento/${eventoOriginal.toJson()}")
                        else
                            navController.navigate(route = "2-MostrarEventos")
                    }) {
                        Text("Cancelar", color = Color.White, fontSize = 18.sp)
                    }
                    Button(
                        onClick = {
                            //Comprobamos que los campos del formulario son válidos:
                            if (esFechaValida && !titulo.isBlank() && !categoria.isBlank()) {
                                //Diferenciamos según estamos editando un evento o creando uno nuevo
                                if (evento.id != null) {//Editando evento existente
                                    val eventoNuevo = eventoActualizado()

                                    //agregarEvento(eventoNuevo, context)
                                    actualizarEvento(eventoNuevo.id!!, eventoNuevo, context)

                                    //Vamos a la pantalla del evento
                                    navController.navigate("3-DetalleEvento/${eventoNuevo.toJson()}")
                                } else { //Creando nuevo evento
                                    val eventoNuevo = eventoActualizado()
                                    //Guardamos el evento en la base de datos
                                    agregarEvento(eventoNuevo, context)
                                    //Volvemos al listado de eventos
                                    navController.navigate("2-MostrarEventos")
                                }
                            } else { //Marcamos los errores en el formulario
                                esFechaValida = false
                                errorTitulo = true
                                errorCategoria = true
                            }
                        },
                        modifier = Modifier
                            .testTag("Guardar evento"),
                        enabled = (esFechaValida && !errorTitulo && !errorCategoria)
                    ) {
                        Text("Guardar", fontSize = 20.sp)
                    }
                }
            }
        }
    }
}



//Funcion que define la BottomAppBar
@Composable
fun EdicionBottomAppBar(
    navController: NavController,
    eventoOriginal: Evento
) {
    BottomAppBar(
        actions = {
            IconButton(
                onClick = {
                    navController.navigate("3-DetalleEvento/${eventoOriginal.toJson()}")
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
}

//Funcion que define la TopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdicionTopAppBar(
    evento: Evento,
    navController: NavController
) {
    //Estado para controlar si el menú está desplegado o no
    var menuExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    CenterAlignedTopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Edición de evento", fontSize = 30.sp)
            }
        },
        navigationIcon = {},
        actions = {
            //Botón de overflow, al hacer clic desplegamos el menú
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más opciones",
                )
            }
            //Menú desplegable
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Eliminar evento") },
                    onClick = {
                        eliminarEvento(evento.id!!, context)
                        //Volvemos al listado de eventos
                        navController.navigate("2-MostrarEventos")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Borrar evento")
                    }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}


//Funcion que define la BottomAppBar
@Composable
fun CreacionBottomAppBar(navController: NavController) {
    BottomAppBar(
        actions = {
            IconButton(
                onClick = {
                    navController.navigate("2-MostrarEventos")
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
}

//Funcion que define la TopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreacionTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Crear evento", fontSize = 30.sp)
            }
        },
        navigationIcon = {},
        actions = {},
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
