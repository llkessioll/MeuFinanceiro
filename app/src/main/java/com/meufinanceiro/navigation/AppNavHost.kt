package com.meufinanceiro.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
        composable(Screen.Registrar.route) {
            RegistrarScreen(navController)
        }
        composable(Screen.Categorias.route) {
            CategoriasScreen(navController)
        }
        composable(Screen.Resumo.route) {
            ResumoFinanceiroScreen(navController)
        }
    }
}
