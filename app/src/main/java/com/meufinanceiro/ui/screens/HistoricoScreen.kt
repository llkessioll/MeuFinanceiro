package com.meufinanceiro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoricoScreen(
    navController: NavController,
    // quando integrar: essa lista virá do ViewModel
    transacoes: List<TransacaoUi> = sampleList(), // lista fake por enquanto
    onDelete: (Long) -> Unit = {}
) {

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Histórico de Transações") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {

            if (transacoes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma transação registrada", fontSize = 18.sp)
                }
            } else {

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(transacoes.size) { index ->
                        val item = transacoes[index]
                        TransacaoCard(
                            transacao = item,
                            onDelete = { onDelete(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransacaoCard(
    transacao: TransacaoUi,
    onDelete: () -> Unit
) {
    val color = if (transacao.tipo == "RECEITA")
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    else
        MaterialTheme.colorScheme.error.copy(alpha = 0.15f)

    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {

        Row(
            modifier = Modifier
                .background(color)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = transacao.descricao ?: transacao.categoria,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formatDate(transacao.dataMillis),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "R$ ${"%.2f".format(transacao.valor)}",
                    color = if (transacao.tipo == "RECEITA")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    fontSize = 18.sp
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Deletar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


// ---------------------------
// MODELO UI TEMPORÁRIO
// ---------------------------

data class TransacaoUi(
    val id: Long,
    val tipo: String,        // RECEITA ou DESPESA
    val valor: Double,
    val descricao: String?,
    val categoria: String,
    val dataMillis: Long
)

// Lista falsa para testar UI
fun sampleList() = listOf(
    TransacaoUi(1, "RECEITA", 2000.0, "Salário", "Trabalho", System.currentTimeMillis()),
    TransacaoUi(2, "DESPESA", 45.90, "Lanche", "Alimentação", System.currentTimeMillis()),
    TransacaoUi(3, "DESPESA", 300.0, "Uber", "Transporte", System.currentTimeMillis())
)

fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}
