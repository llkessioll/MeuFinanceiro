package com.meufinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.meufinanceiro.navigation.AppNavHost
import com.meufinanceiro.ui.theme.MeuFinanceiroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Deixa a barra de status transparente (moderno)

        setContent {
            MeuFinanceiroTheme {
                // 1. Criamos o "controle remoto" da navegação
                val navController = rememberNavController()

                // 2. Chamamos o "mapa" (NavHost) que define as telas
                // Isso vai carregar a tela definida como 'startDestination' (Home)
                AppNavHost(navController = navController)
            }
        }
    }
}