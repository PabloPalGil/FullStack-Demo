package com.pablo.eventos_retrofit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pablo.eventos_retrofit.data.EventoApiService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


//Tests con hilt (inyección de la Api Retrofit)
@HiltAndroidTest
class MainActivityTestHilt {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var apiService: EventoApiService

    @Before
    fun init() {
        hiltRule.inject() //Inyecta las dependencias antes de ejecutar las pruebas
    }

    @Test
    fun testRetrofitApiCall() {
        //Prueba para verificar la llamada conRetrofit
        runBlocking {
            val response = apiService.obtenerEventos()
            assert(response.isSuccessful)
        }
    }


    @Test
    fun testPantallaInicio_muestraTexto() {
        //Prueba para verificar la llamada conRetrofit
        runBlocking {
            val response = apiService.obtenerEventos()
            assert(response.isSuccessful)
        }
        //Le damos a Compose la información para la navegación
        composeTestRule.setContent {
            AppNavigation()
        }
        //Comprobamos que automáticamente carga la pantalla de inicio y se muestra el texto inicial
        composeTestRule.onNodeWithText("Toca para comenzar").assertIsDisplayed()
    }


    @Test
    fun testNavegacion_MostrarEventos() {
        //Prueba para verificar la llamada conRetrofit
        runBlocking {
            val response = apiService.obtenerEventos()
            assert(response.isSuccessful)
        }
        //Le damos a Compose la información para la navegación
        composeTestRule.setContent {
            AppNavigation()
        }
        //Comprobamos que automáticamente carga la pantalla de inicio y clicamos
        composeTestRule.onNodeWithTag("Pantalla de Inicio").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        //Comprobamos que se ha navegado a la pantalla MostrarEventos
        composeTestRule.onNodeWithContentDescription("AñadirEventoFAB").assertIsDisplayed()
    }


    //TESTS UI:
    //Primer test UI: Main Activity llama a AppNavigation y ésta carga PantallaInicio, donde deberá
    //aparecer un texto: "Toca para empezar". Comprobamos que el texto se muestra:
    @Test
    fun pantallaInicio_muestraTocaParaComenzar() {
        composeTestRule.setContent {
            AppNavigation()
        }
        //Verificamos que el texto "Toca para comenzar" aparece en la UI
        composeTestRule.onNodeWithText("Toca para comenzar").assertIsDisplayed()
    }


    //Segundo test UI: Carga de Eventos del servidor. Si pulsamos sobre la pantalla de inicio
    // navegamos hasta la pantalla MostrarEventos donde deberán cargar los eventos del backend.
    // Comprobamos que se muestran:
    @Test
    fun mostrarEventosTest() {
        //Prueba para verificar la llamada conRetrofit
        runBlocking {
            val response = apiService.obtenerEventos()
            assert(response.isSuccessful)
        }
        //Le damos a Compose la información para la navegación
        composeTestRule.setContent {
            AppNavigation()
        }
        composeTestRule.onNodeWithText("Toca para comenzar").performClick()
        composeTestRule.waitForIdle() //Esperamos a que termine
        //Comprobamos que se muestran los eventos
        composeTestRule.onNodeWithTag("Evento").assertIsDisplayed()
    }


    //Tercer test UI: crear Eventos. Si pulsamos sobre el FAB nos llevará a la pantalla de
    // EditarEvento. Por defecto los campos estarán vacíos, si intentamos guardar el evento
    // aparecerán los Text de error que indican los campos obligatorios.
    // Comprobamos que aparece el aviso del campo Título:
    @Test
    fun crearEventoTest() {
        composeTestRule.setContent {
            //Prueba para verificar la llamada conRetrofit
            runBlocking {
                val response = apiService.obtenerEventos()
                assert(response.isSuccessful)
            }
            //Le damos a Compose la información para la navegación
            composeTestRule.setContent {
                AppNavigation()
            }

            composeTestRule.onNodeWithText("Toca para comenzar").performClick()
            composeTestRule.waitForIdle() //Esperamos a que termine
            composeTestRule.onNodeWithTag("CrearEventoFAB").performClick()
            composeTestRule.waitForIdle() //Esperamos a que termine
            //Una vez en la pantalla EditarEvento, comprobamos que por defecto no aparecen
            //los mensajes de error por campos no válidos. Comprobamos el de título:
            composeTestRule.onNodeWithTag("Titulo es obligatorio").assertIsNotDisplayed()
            //Ahora localizamos el botón de Crear Evento y clicamos
            composeTestRule.onNodeWithTag("Guardar evento").performClick()
            composeTestRule.waitForIdle() //Esperamos a que termine
            //Comprobamos que aparece el Text de error por campo Título vacío:
            composeTestRule.onNodeWithTag("Titulo es obligatorio").assertIsDisplayed()
        }
    }

    //Cuarto test UI: comprobamos que podemos acceder a la información de un evento y modificarlo
    //y los cambios se reflejan en la UI:
    @Test
    fun editarEventoTest() {
        //Prueba para verificar la llamada conRetrofit
        runBlocking {
            val response = apiService.obtenerEventos()
            assert(response.isSuccessful)
        }
        //Le damos a Compose la información para la navegación
        composeTestRule.setContent {
            AppNavigation()
        }

        //Comprobamos que automáticamente carga la pantalla de inicio
        composeTestRule.onNodeWithText("Toca para comenzar").performClick()
        composeTestRule.waitForIdle() //Esperamos a que termine
        //Navegamos a la pantalla de MostrarEventos
        composeTestRule.onNodeWithTag("Evento").performClick()
        composeTestRule.waitForIdle() //Esperamos a que termine
        //Navegamos a la pantalla de DetalleEvento y localizamos el icono del corazón
        //que indica si es no favorito y almacenamos su estado:
        composeTestRule.waitForIdle() //Esperamos a que termine
        val fav = composeTestRule.onNodeWithContentDescription("Evento favorito").isDisplayed()
        //fav será true si este evento es favorito y false en caso contrario.
        //Ahora localizamos el FAB de edición de evento y hacemos clic:
        composeTestRule.waitForIdle() //Esperamos a que termine
        composeTestRule.onNodeWithTag("Editar evento FAB").performClick()
        composeTestRule.waitForIdle() //Esperamos a que termine
        //En la pantalla de edición de evento, clicamos en el icono de favorito:
        composeTestRule.onNodeWithTag("Alternar favorito").performClick()
        composeTestRule.waitForIdle() //Esperamos a que termine
        //Localizamos el botón de actualizar evento y clicamos para volver a la pantalla anterior:
        composeTestRule.onNodeWithTag("Guardar evento").performClick()
        composeTestRule.waitForIdle() //Esperamos a que termine
        //ALmacenamos de nuevo el estado del icono de favorito:
        val newFav =
            composeTestRule.onNodeWithContentDescription("Evento favorito").isDisplayed()
        composeTestRule.waitForIdle() //Esperamos a que termine
        //Y comprobamos que su estado ha cambiado:
        assertFalse(fav == newFav)
        //Si tiene éxito, ejecutar de nuevo para no alterar los datos de la BD
    }
}