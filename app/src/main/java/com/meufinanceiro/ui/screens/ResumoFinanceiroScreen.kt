package com.meufinanceiro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import com.meufinanceiro.backend.db.AppDatabase
import com.meufinanceiro.backend.repository.TransacaoRepository
import com.meufinanceiro.backend.service.ResumoFinanceiroService
import com.meufinanceiro.ui.extensions.toCurrency
import com.meufinanceiro.ui.viewmodel.ResumoFinanceiroFactory
import com.meufinanceiro.ui.viewmodel.ResumoFinanceiroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumoFinanceiroScreen(navController: NavController) {

    val context = LocalContext.current

    // Instancia o banco
    val db = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "meu_financeiro.db"
        ).build()
    }

    // Instancia o service real
    val service = remember {
        ResumoFinanceiroService(
            transacaoRepository = TransacaoRepository(
                dao = db.transacaoDao()
            )
        )
    }

    // ViewModel
    val viewModel: ResumoFinanceiroViewModel = viewModel(
        factory = ResumoFinanceiroFactory(service)
    )

    val receitas by viewModel.totalReceitas.collectAsState()
    val despesas by viewModel.totalDespesas.collectAsState()
    val saldo by viewModel.saldo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumo Financeiro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            CardResumo(
                titulo = "Receitas",
                valor = receitas,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                textColor = MaterialTheme.colorScheme.primary
            )

            CardResumo(
                titulo = "Despesas",
                valor = despesas,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                textColor = MaterialTheme.colorScheme.error
            )

            CardResumo(
                titulo = "Saldo",
                valor = saldo,
                color = if (saldo >= 0)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else
                    MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                textColor = if (saldo >= 0)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun CardResumo(
    titulo: String,
    valor: Double,
    color: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = titulo,
                fontSize = 20.sp,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = valor.toCurrency(),
                fontSize = 26.sp,
                color = textColor
            )
        }
    }
}