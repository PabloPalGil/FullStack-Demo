package com.pablo.eventos_retrofit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

//Tests sin utilizar Hilt
class AppNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun navegacionTest1() {
        composeTestRule.setContent {
            AppNavigation()
        }
        //Comprobamos que automáticamente carga la pantalla de inicio y clicamos
        composeTestRule.onNodeWithText("Toca para comenzar").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle() //Esperamos a que termine
        composeTestRule.onNodeWithText("Eventos").assertIsDisplayed()
    }

    @Test
    fun navegacionTest2() {
        composeTestRule.setContent {
            AppNavigation()
        }
        //Comprobamos que automáticamente carga la pantalla de inicio y clicamos
        composeTestRule.onNodeWithText("Toca para comenzar").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle() //Esperamos a que termine
        composeTestRule.onNodeWithText("Eventos").assertIsDisplayed()
        //Localizamos el FAB de creación de nuevo Evento y clicamos
        composeTestRule.onNodeWithContentDescription("Icono Menú").assertIsDisplayed()
        composeTestRule.waitForIdle() //Esperamos a que termine
        //Navegamos a la pantalla CrearEvento y localizamos el botón de Crear Evento
        composeTestRule.onNodeWithTag("Crear evento").assertIsDisplayed()
    }

}