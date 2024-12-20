package org.logic_gates_book.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.logic_gates_book.HapticFeedback
import org.logic_gates_book.Uri
import org.logic_gates_book.ui.screens.book.BookScreen
import org.logic_gates_book.ui.screens.book.PageScreen
import org.logic_gates_book.ui.screens.start.StartScreen

/**
 * Composable que define el [NavHost] para la pantalla de inicio de la aplicación.
 * La navegación comienza en la ruta [Routes.Start.route] y permite la transición
 * entre las pantallas de inicio, créditos y libro con animaciones suaves de entrada.
 *
 * @param rememberNavController El controlador de navegación principal para gestionar las rutas.
 *
 * @see Routes.Start.route Ruta de la pantalla de inicio.
 * @see Routes.Credits.route Ruta de la pantalla de créditos.
 * @see Routes.Book.route Ruta de la pantalla del libro.
 */
@Composable
fun NavControllerStart(
    rememberNavController: NavHostController,
    hapticFeedback: HapticFeedback,
    uri: Uri,
    screenWidth: Float
) {
    // Definición de las pantallas principales del flujo de inicio
    NavHost(
        navController = rememberNavController,
        startDestination = Routes.Start.route,
        modifier = Modifier
            .background(colorScheme.background)
            .fillMaxSize(),
        enterTransition = { fadeIn(animationSpec = tween(5000)) }, // Animación de entrada suave
    ) {
        // Definición de las rutas de navegación para las pantallas
        composable(Routes.Start.route) {
            StartScreen(
                navController = rememberNavController,
                uri = uri
            )
        }
        composable(Routes.Credits.route) { "CreditsScreen(navController = rememberNavController)" }
        composable(Routes.Book.route) {
            BookScreen(
                navController = rememberNavController,
                hapticFeedback = hapticFeedback,
                screenWidth = screenWidth
            )
        }
    }
}

/**
 * Composable que define el [NavHost] para la navegación dentro del libro.
 * Permite la navegación entre las páginas del libro con animaciones de deslizamiento
 * y desaparición/fading al moverse entre páginas.
 *
 * @param navController El controlador de navegación que maneja las rutas dentro del libro.
 * @param navControllerHome El controlador de navegación para manejar las rutas a la pantalla de inicio.
 *
 * @see PageScreen Pantalla que muestra una página específica del libro.
 * @see navControllerControl El controlador de navegación para manejar las transiciones entre las páginas.
 */
@Composable
fun NavControllerBook(
    navController: NavHostController,
    navControllerHome: NavHostController,
    hapticFeedback: HapticFeedback,
    screenWidth: Float
) {
    // Definición del NavHost para la navegación dentro del libro
    NavHost(navController = navController, startDestination = "page/1") {
        // Composable para las páginas del libro, identificadas por su número de página
        composable(
            route = "page/{pageNumber}", // Ruta que incluye el número de la página como parámetro
            enterTransition = {
                // Animación de entrada: deslizamiento desde la derecha y fade-in
                slideInHorizontally(
                    initialOffsetX = { it } // Desliza desde la derecha
                ) + fadeIn(animationSpec = tween(2500)) // Añade un fade-in suave
            },
            exitTransition = {
                // Animación de salida: deslizamiento hacia la izquierda y fade-out
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(animationSpec = tween(2500))
            },
            popEnterTransition = {
                // Animación de entrada cuando volvemos atrás: deslizar desde la izquierda y fade-in
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn(animationSpec = tween(2500))
            },
            popExitTransition = {
                // Animación de salida cuando retrocedemos: deslizar hacia la derecha y fade-out
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(2500))
            }
        ) { backStackEntry ->
            // Obtener el número de la página desde la ruta
            val pageNumber = backStackEntry.arguments?.getString("pageNumber")?.toInt() ?: 1
            // Llamamos a PageScreen para mostrar el contenido de la página correspondiente
            PageScreen(
                pageNumber = pageNumber,
                hapticFeedback = hapticFeedback,
                screenWidth = screenWidth,
                navController = navControllerHome, // Controlador de navegación para la pantalla principal
                onNextPage = {
                    // Navegar a la siguiente página
                    navController.popBackStack() // Eliminar la página actual de la pila
                    navController.navigate("page/${pageNumber + 1}") // Navegar a la página siguiente
                },
                onPreviousPage = {
                    // Navegar a la página anterior
                    navController.popBackStack() // Eliminar la página actual de la pila
                    navController.navigate("page/${pageNumber - 1}") // Navegar a la página anterior
                }
            )
        }
    }
}