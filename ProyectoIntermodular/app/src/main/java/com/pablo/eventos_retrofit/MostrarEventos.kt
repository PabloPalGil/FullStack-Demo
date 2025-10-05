package com.pablo.eventos_retrofit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pablo.eventos_retrofit.data.Evento
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


//Pantalla que muestra y filtra los eventos
@Composable
fun MostrarEventos(navController: NavHostController) {
    //OBTENER EVENTOS CON RETROFIT
    val eventos = obtenerEventos(LocalContext.current)


    //FILTRAR
    //Recabamos todas las categorías existentes:
    val categorias = getCategorias(eventos)

    //Estado del filtro rápido (Chips de categorías)
    val filtroRapido = remember { mutableStateListOf<String>() }

    //Variables que controlan la barra de búsqueda:
    var searchText by rememberSaveable { mutableStateOf("") }//Texto de búsqueda
    var searchBar by rememberSaveable { mutableStateOf(false) }//Muestra/esconde la barra de búsqueda

    //Estado del filtro de favoritos
    var filtroFav by rememberSaveable { mutableStateOf(false) }//Filtra por favoritos

    //Filtrado según campo de búsqueda, filtro de Favoritos y filtro de categorías:
    val filteredEvents = eventos.filter {
        it.titulo.contains(searchText.trim(), ignoreCase = true)
    }.filter {
        if (filtroFav) it.favorito else true
    }.filter {
        if (filtroRapido.isNotEmpty())
            it.categoria in filtroRapido
        else true
    }


    //variable de estado de la lista GRID de eventos:
    val lazyGridState = rememberLazyStaggeredGridState()

    //Creamos un CoroutineScope para realizar el scroll
    val coroutineScope = rememberCoroutineScope()


    //Variable que controla el nº de columnas del GRID:
    var columnasGrid by remember { mutableStateOf(1) }

    //Comprobamos la orientación
    val landscape = orientacionHorizontal()
    //En horizontal se dibujan 2 columnas de eventos
    if (landscape) columnasGrid = 2 else columnasGrid = 1


    //Variables para determinar la dirección del scroll según la posición anterior
    var previousIndex by remember { mutableStateOf(0) }
    var previousScrollOffset by remember { mutableStateOf(0) }

    //Variable para controlar si estamos haciendo scroll hacia arriba
    var isScrollingUp by remember { mutableStateOf(false) }

    //Listener de scroll. LaunchedEffect lanza una corrutina cuando se da o cambia alguno de sus parámetros,
    //firstVisibleItemIndex indica qué elemento de la LazyGrid está visible arriba en la pantalla actualmente,
    //firstVisibleItemScrollOffset es cuánto no se ve de ese primer elemento (en px dibujados hasta arriba),
    //en este caso, se activará sólo cuando se hace scroll y calcula si el scroll es hacia arriba o no:
    LaunchedEffect(
        lazyGridState.firstVisibleItemIndex,
        lazyGridState.firstVisibleItemScrollOffset
    ) {
        val currentIndex = lazyGridState.firstVisibleItemIndex
        val currentScrollOffset = lazyGridState.firstVisibleItemScrollOffset

        //Detecta si el usuario está haciendo scroll hacia arriba
        isScrollingUp = if (currentIndex == previousIndex) {
            //Mismo índice, verificar el desplazamiento vertical
            currentScrollOffset < previousScrollOffset
        } else {
            //Diferente índice, verificar el índice
            currentIndex < previousIndex
        }

        //Actualizamos la posición anterior
        previousIndex = currentIndex
        previousScrollOffset = currentScrollOffset
    }

    //Variable que controle mostrar un botón sólo cuando se llegue al final de los comentarios
    var scrollBottom by remember { mutableStateOf(false) }

    //Monitorizamos el scroll para ver si hemos llegado al final
    LaunchedEffect(lazyGridState.firstVisibleItemIndex) {
        //snapshotFlow nos da información sobre el estado puntual del lazyGrid
        snapshotFlow { lazyGridState.layoutInfo }
            //Almacenamos información sobre layoutInfo, que nos permite acceder a la cantidad de
            //elementos visibles de la lazyGrid:
            .collect { layoutInfo ->
                //Guardamos el ultimo elemento actualmente visible:
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                //guardamos el total de los elementos de la lazyGrid:
                val totalItems = layoutInfo.totalItemsCount
                //Si el último elemento visible no es nulo y es igual al último de la lazyGrid,
                //podemos asumir que estamos al final de la lista (o al menos ya se ve el último elemento):
                scrollBottom =
                    (lastVisibleItemIndex != null && lastVisibleItemIndex == totalItems - 1)
            }
    }

    //Animación para el botón de Subir arriba (aparece cuando el scroll lega abajo)
    val unitFloat by animateFloatAsState(
        targetValue = if (scrollBottom) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )

    //MENU LATERAL:
    //Variable de estado del ModalDrawer:
    val myDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    EventosModalDrawer(
        drawerState = myDrawerState,
        navController,
        contenido = {
            //SCAFFOLD
            Scaffold(
                floatingActionButton = {
                    NuevoEventoFAB(navController)
                                       },
                //Definimos nuestra topBar
                topBar = {
                    EventosTopAppBar(
                        myDrawerState,
                        filtrarFavoritos = { filtroFav = !filtroFav }, //Filtro de favoritos
                        onClickSearch = {
                            searchBar = !searchBar //Mostrar/ocultar la barra de búsqueda
                        }
                    )
                },
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("MostrarEventos layout")
                            .padding(padding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {

                        //Barra de búsqueda:
                        if (searchBar) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(30.dp)
                                    )
                            ) {
                                TextField(
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    placeholder = { Text("Buscar...", fontSize = 14.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(30.dp),
                                    trailingIcon = {
                                        IconButton(onClick = {}) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Buscar"
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        //Función que implementa un LazyRow de chips para filtrado rápido:
                        LazyRow(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(0.9f)
                        ) {
                            //Recorremos cada categoria y creamos un Chip
                            for (opcion in categorias) {
                                item {
                                    ItemFilterChip(opcion, filtroRapido)
                                    InsertSpacer(8)
                                }
                            }
                        }

                        InsertSpacer(4)

                        //LazyVerticalStaggeredView con las reviews
                        LazyVerticalStaggeredGrid(
                            state = lazyGridState,
                            columns = StaggeredGridCells.Fixed(columnasGrid),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f),
                            verticalItemSpacing = 12.dp,
                        ) {
                            //Pasamos sólo los eventos tras filtros rápidos y barra de búsqueda:
                            items(filteredEvents) {
                                ItemEvento(it, navController)
                            }
                        }

                        //El botón sólo se muestra si estamos abajo del tod0 y además no estamos haciendo scrollUp:
                        if (scrollBottom && !isScrollingUp) {
                            Button(
                                modifier = Modifier
                                    .alpha(unitFloat)
                                    .align(Alignment.CenterHorizontally)
                                    .padding(16.dp)
                                    .fillMaxWidth(0.6f),
                                onClick = {
                                    coroutineScope.launch {
                                        lazyGridState.scrollToItem(0) // Desplazarse al primer elemento
                                    }
                                }
                            ) {
                                Text(text = "Ir arriba")
                            }
                        }
                    }
                }
            )
        }
    )
}



@Composable
fun ItemEvento(evento: Evento, navController: NavController) {
    var isVisible by remember { mutableStateOf(false) }

    //Activamos los elementos tras un intervalo
    LaunchedEffect(Unit) {
        delay(1)
        isVisible = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("3-DetalleEvento/${evento.toJson()}") }
            .testTag("Evento"),//Para los Tests de UI
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //Mostramos el título del evento
            Text(
                text = evento.titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            //Mostramos la fecha del evento formateada:
            Text(
                text = formateaFecha(evento.fechaRealizacion),
                fontSize = 16.sp
            )
            HorizontalDivider(
                color = Color.DarkGray,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            //En la zona inferior de la Card ponemos un Row que tendrá avrios iconos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //Indicamos con un icono si el evento está en favoritos
                if (evento.favorito) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = "Evento favorito",
                        tint = Color.Red,
                        modifier = Modifier.size(25.dp)
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
        }
    }
}


//Funcion que crea el FilterChip con la categoria
@Composable
fun ItemFilterChip(categoria: String, filtroRapido: MutableList<String>) {
    var selected by rememberSaveable { mutableStateOf(false) }

    //Creamos el filter Chip
    FilterChip(
        onClick = {
            selected = !selected //Selección / deselección al clicar
            if (selected) filtroRapido.add(categoria) else filtroRapido.remove(categoria)
        },
        label = {
            Text(categoria)//El texto es la categoría
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,//icono de check
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosTopAppBar(
    drawerState: DrawerState,
    filtrarFavoritos: () -> Unit,
    onClickSearch: () -> Unit
) {
    //Estado del icono de favoritos
    var filtroFavoritos by rememberSaveable { mutableStateOf(false) }

    val corutina = rememberCoroutineScope()

    TopAppBar(
        //Título de la Topbar
        title = { Text(text = "Eventos") },
        //Icono de navegación, a la izquierda
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
        //Iconos de acción, a la derecha
        actions = {
            //Icono que filtra por favoritos
            IconButton(
                onClick = {
                    filtroFavoritos = !filtroFavoritos
                    filtrarFavoritos()
                },
            ) {
                if (filtroFavoritos) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Filtro de favoritos ON"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Filtro de favoritos OFF"
                    )
                }
            }
            //Icono que activa/desactiva la barra de búsqueda
            IconButton(
                onClick = {
                    onClickSearch() //Biestado de la barra de búsqueda
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Icono de la barra de búsqueda"
                )
            }
        },
        //Colores
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}


//Función que define el menú lateral ModalDrawer
@Composable
fun EventosModalDrawer(
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
                CabeceraModalDrawer()
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
fun CabeceraModalDrawer() {
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
                .data("https://images.unsplash.com/photo-1535981767287-35259dbf7d0e?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
                .build(),
            modifier = Modifier
                .fillMaxWidth(),
            contentDescription = "Imagen de Lista de Eventos",
            //Forzamos la imagen a ocupar el ancho, mantiendo la rel. de aspecto
            contentScale = ContentScale.Inside
        )
    }
}


//Función que da un formato adecuado a las fechas que obtenemos de mongodb
fun formateaFecha(fechaString: String): String {
    //La fecha en formato String que leemos de mongodb es de tipo ISO 8601 sin Z (sin zona horaria)
    //Parseamos el String a LocalDateTime
    val localDateTime1 = LocalDateTime.parse(fechaString) //ISO 8601 sin "Z"

    //Convertimos LocalDateTime a Date
    val locDateTime = localDateTime1.atZone(ZoneId.systemDefault())
    val date = Date.from(locDateTime.toInstant())

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale("es", "ES"))
    val localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    return formatter.format(localDateTime)
}



//Funcion que devuelve una lista de Strings con todas las categorias de los eventos
fun getCategorias(eventos: List<Evento>): List<String> {
    val categorias = mutableListOf<String>()

    eventos.forEach {
        if (it.categoria !in categorias)
            categorias.add(it.categoria)
    }
    return categorias
}


@Composable
fun NuevoEventoFAB(navController: NavController) {
    val value by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        ),
        label = "Animación de resaltado"
    )

    //Creamos el Brush para la animación de resaltado
    val colors = listOf(
        Color(0xFF405DE6),
        Color(0xFFC13584),
        Color(0xFFFD1D1D),
        Color(0xFFFFDC80)
    )
    val gradientBrush by remember {
        mutableStateOf(
            Brush.horizontalGradient(
                colors = colors,
                startX = -10.0f,
                endX = 400.0f,
                tileMode = TileMode.Repeated
            )
        )
    }
    FloatingActionButton(
        onClick = {
            val evento = getNewEvento()
            navController.navigate("4-EditarEvento/${evento.toJson()}")
        },
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .testTag("CrearEventoFAB")
            .drawBehind {
                rotate(value) {
                    drawCircle(
                        gradientBrush, style = Stroke(width = 30.dp.value),
                        radius = 75f
                    )
                }
            },
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Añadir Evento"
        )
    }
}
