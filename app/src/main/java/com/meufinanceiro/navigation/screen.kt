package com.meufinanceiro.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Registrar : Screen("registrar")
    object Historico : Screen("historico")
    object Categorias : Screen("categorias")
    object Resumo : Screen("resumo")
}
