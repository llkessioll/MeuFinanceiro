package com.meufinanceiro.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.meufinanceiro.ui.screens.HomeScreen
import com.meufinanceiro.ui.screens.HistoricoScreen
import com.meufinanceiro.ui.screens.RegistrarScreen
import com.meufinanceiro.ui.screens.CategoriasScreen
import com.meufinanceiro.ui.screens.ResumoFinanceiroScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Historico.route) {
            HistoricoScreen(navController)
        }

        // --- ROTA ATUALIZADA PARA ACEITAR ID (EDIÇÃO) ---
        composable(
            route = "registrar?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L // Se não passar nada, é 0 (Novo)
                }
            )
        ) { backStackEntry ->
            // Recupera o ID passado na navegação
            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            // Passa o ID para a tela de Registrar
            RegistrarScreen(navController, transacaoId = id)
        }
        // -------------------------------------------------

        composable(Screen.Categorias.route) {
            CategoriasScreen(navController)
        }
        composable(Screen.Resumo.route) {
            ResumoFinanceiroScreen(navController)
        }
    }
}