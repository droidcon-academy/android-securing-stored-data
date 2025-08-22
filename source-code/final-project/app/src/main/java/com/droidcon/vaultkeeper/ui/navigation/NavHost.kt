package com.droidcon.vaultkeeper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.droidcon.vaultkeeper.ui.fileimport.FileImportScreen
import com.droidcon.vaultkeeper.ui.home.HomeScreen
import com.droidcon.vaultkeeper.ui.noteeditor.NoteEditorScreen
import com.droidcon.vaultkeeper.ui.passwordchecker.PasswordCheckerScreen
import com.droidcon.vaultkeeper.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object NoteEditor : Screen("note_editor?noteId={noteId}") {
        fun createRoute(noteId: Int = -1): String = "note_editor?noteId=$noteId"
    }
    object FileImport : Screen("file_import")
    object PasswordChecker : Screen("password_checker")
}

@Composable
fun VaultKeeperNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                navigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                navigateToNoteEditor = { noteId ->
                    navController.navigate(Screen.NoteEditor.createRoute(noteId))
                },
                navigateToFileImport = {
                    navController.navigate(Screen.FileImport.route)
                },
                navigateToPasswordChecker = {
                    navController.navigate(Screen.PasswordChecker.route)
                }
            )
        }
        
        composable(
            route = Screen.NoteEditor.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            NoteEditorScreen(
                noteId = noteId,
                navigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.FileImport.route) {
            FileImportScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.PasswordChecker.route) {
            PasswordCheckerScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
} 