package com.pablo.eventos_retrofit

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pablo.eventos_retrofit.data.Evento
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun DetalleEvento(evento: Evento, navController: NavController) {
    //MENU LATERAL:
    //Variable de estado del ModalDrawer:
    val myDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)


    //Animación para el icono corazon
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


    DetalleModalDrawer(
        drawerState = myDrawerState,
        navController,
        contenido = {
            Scaffold(
                //Definimos nuestra topBar
                topBar = {
                    DetalleTopAppBar(
                        myDrawerState,
                        navController,
                        evento
                    )
                },
                bottomBar = { DetalleBottomAppBar(navController) },
                floatingActionButton = { EditarFAB(navController, evento) }
            ) { paddingValues ->

                var isVisible by remember { mutableStateOf(false) }

                //Activamos los elementos tras un intervalo
                LaunchedEffect(Unit) {
                    delay(1)
                    isVisible = true
                }


                //Animación que combina desplazamiento y desvanecimiento
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 }, // Comienza desde la mitad de su altura
                        animationSpec = tween(durationMillis = 300) // Duración de la animación
                    ) + fadeIn(animationSpec = tween(durationMillis = 400)), // Combina con desvanecimiento
                    exit = slideOutVertically(
                        targetOffsetY = { -it / 2 }, // Desaparece hacia arriba
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeOut(animationSpec = tween(durationMillis = 400)) // Combina con desvanecimiento
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 64.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingValues)
                                .padding(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            elevation = CardDefaults.cardElevation(5.dp),
                            shape = RoundedCornerShape(30.dp)
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .testTag("Layout detalles evento")
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                //TITULO
                                Text(
                                    text = evento.titulo, fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                InsertSpacer(24)
                                Text(
                                    text = formateaFecha(evento.fechaRealizacion),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                InsertSpacer(32)
                                //CATEGORÍA
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        text = "Categoría: ", fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = evento.categoria, fontSize = 16.sp)
                                }

                                InsertSpacer(32)
                                //DESCRIPCION
                                Text(
                                    text = "Descripción:", fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = evento.descripcion ?: "", fontSize = 16.sp)

                                InsertSpacer(16)

                                //FAVORITO
                                Row(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (evento.favorito) "Favorito" else "No favorito",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    //Indicamos con un icono si el evento está en favoritos
                                    if (evento.favorito) {
                                        Icon(
                                            imageVector = Icons.Outlined.Favorite,
                                            contentDescription = "Evento favorito",
                                            tint = Color.Red,
                                            modifier = Modifier.size(sizeAnimHeart.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Evento no favorito",
                                            tint = Color.Black,
                                            modifier = Modifier.size(25.dp)
                                        )
                                    }
                                }
                                InsertSpacer(16)

                                //ID
                                Text(
                                    text = "Id:", fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = evento.id!!, fontSize = 16.sp)

                                InsertSpacer(16)

                                Image(
                                    painter = painterResource(id = R.drawable.imagen1),
                                    contentDescription = "Agenda en blanco",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}


//Funcion que define la TopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTopAppBar(
    drawerState: DrawerState,
    navController: NavController,
    evento: Evento
) {
    //Estado para controlar si el menú está desplegado o no
    var menuExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val corutina = rememberCoroutineScope()

    CenterAlignedTopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Detalles del evento", fontSize = 30.sp)
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                corutina.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Icono Menú",
                )
            }
        },
        actions = {
            //Botón de overflow, al hacer clic desplegamos el menú
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más opciones"
                )
            }
            //Menú desplegable
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                //Primera opción del menú
                DropdownMenuItem(
                    text = { Text("Editar") },
                    onClick = {
                        navController.navigate("4-EditarEvento/${evento.toJson()}")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                )
                //Segunda opción del menú
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

@Composable
fun EditarFAB(navController: NavController, evento: Evento) {
    ExtendedFloatingActionButton(
        onClick = { navController.navigate("4-EditarEvento/${evento.toJson()}") },
        text = {
            Text(text = "EDITAR")
        },
        modifier = Modifier.testTag("Editar evento FAB"),
        icon = { Icon(Icons.Filled.Edit, "Editar evento") }
    )
}


//Funcion que define la BottomAppBar
@Composable
fun DetalleBottomAppBar(navController: NavController) {
    BottomAppBar(
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(route = "2-MostrarEventos")
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

//Función que define el menú lateral ModalDrawer
@Composable
fun DetalleModalDrawer(
    drawerState: DrawerState,
    navController: NavController,
    contenido: @Composable () -> Unit
) {
    val corutina = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        modifier = Modifier
            .clickable {
                corutina.launch {
                    drawerState.apply {
                        close()
                    }
                }
            },
        drawerContent = {
            ModalDrawerSheet(drawerTonalElevation = 100.dp) {
                CabeceraDetalleModalDrawer()
                NavigationDrawerItem(
                    label = { Text(text = "Home") },
                    selected = false,
                    onClick = {
                        navController.navigate(route = "1-PantallaInicio")
                    },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Icono de inicio") },
                )
                NavigationDrawerItem(
                    label = { Text(text = "Nuevo Evento") },
                    selected = false,
                    onClick = {
                        val evento = getNewEvento()
                        navController.navigate("4-EditarEvento/${evento.toJson()}")
                    },
                    icon = {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Crear evento"
                        )
                    },
                )
            }
        }
    ) {
        contenido()
    }
}


//Función que crea una cabecera para el menú lateral
@Composable
fun CabeceraDetalleModalDrawer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Imagen de internet:
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://images.unsplash.com/photo-1511871893393-82e9c16b81e3?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
                .build(),
            modifier = Modifier
                .fillMaxWidth(),
            contentDescription = "Imagen de Lista de Eventos",
            //Forzamos la imagen a ocupar el ancho, mantiendo la rel. de aspecto
            contentScale = ContentScale.Inside
        )
    }
}