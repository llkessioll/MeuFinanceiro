package com.meufinanceiro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.meufinanceiro.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    // Estado para esconder/mostrar saldo (feature clássica de banco)
    var showBalance by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ---------------------------------------------
            // 1. CABEÇALHO COM SALDO (Estilo Cartão)
            // ---------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Topo do cartão: Nome e Olho
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Olá, Guilherme",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(onClick = { showBalance = !showBalance }) {
                            Icon(
                                imageVector = if (showBalance) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Mostrar saldo",
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Centro do cartão: Saldo
                    Column {
                        Text(
                            text = "Seu saldo total",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (showBalance) "R$ 1.250,00" else "R$ •••••",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ---------------------------------------------
            // 2. AÇÕES RÁPIDAS (Botões redondos)
            // ---------------------------------------------
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ações Rápidas",
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    icon = Icons.Default.Add,
                    label = "Nova\nTransação",
                    onClick = { navController.navigate(Screen.Registrar.route) }
                )
                ActionButton(
                    icon = Icons.Default.History,
                    label = "Ver\nHistórico",
                    onClick = { navController.navigate(Screen.Historico.route) }
                )
                ActionButton(
                    icon = Icons.Default.PieChart,
                    label = "Resumo\nMensal",
                    onClick = { navController.navigate(Screen.Resumo.route) }
                )
                ActionButton(
                    icon = Icons.Default.Settings,
                    label = "Categorias",
                    onClick = { navController.navigate(Screen.Categorias.route) }
                )
            }

            // Aqui embaixo você poderia colocar uma lista das "Últimas 3 transações"
            // para a tela não ficar vazia
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        // O botão em si (Círculo)
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            modifier = Modifier.size(60.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // O texto embaixo
        Text(
            text = label,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}