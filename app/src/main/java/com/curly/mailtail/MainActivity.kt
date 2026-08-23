package com.curly.mailtail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.curly.mailtail.presentation.home.HomeScreen
import com.curly.mailtail.presentation.notebook.NotebookScreen
import com.curly.mailtail.presentation.post.CreatePostScreen
import com.curly.mailtail.presentation.splash.SplashScreen
import com.curly.mailtail.presentation.ui.theme.MailTailTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MailTailTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash", // СТАРТОВЫЙ ЭКРАН ТЕПЕРЬ SPLASH
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. МАРШРУТ SPLASH
                        composable("splash") {
                            SplashScreen(
                                onNavigateNext = {
                                    // После Splash переходим на Home и убираем Splash из истории
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 2. ГЛАВНЫЙ ЭКРАН (ТОТ САМЫЙ, КОТОРЫЙ ПОТЕРЯЛСЯ)
                        composable("home") {
                            HomeScreen(
                                onNavigateToNotebook = { notebookId ->
                                    navController.navigate("notebook/$notebookId")
                                }
                            )
                        }

                        // 3. ЭКРАН БЛОКНОТА
                        composable(
                            route = "notebook/{notebookId}",
                            arguments = listOf(navArgument("notebookId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val notebookId = backStackEntry.arguments?.getString("notebookId") ?: ""

                            NotebookScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToCreatePost = { navController.navigate("create_post/$notebookId") }
                            )
                        }

                        // 4. ЭКРАН СОЗДАНИЯ ПОСТА
                        composable(
                            route = "create_post/{notebookId}",
                            arguments = listOf(navArgument("notebookId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val notebookId = backStackEntry.arguments?.getString("notebookId") ?: ""

                            CreatePostScreen(
                                notebookId = notebookId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}