package com.meufinanceiro.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.meufinanceiro.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarScreen(
    navController: NavController,
    // callback para ligar ao backend quando quiser: recebe tipo, valor, dataMillis, categoria, descricao
    onSave: suspend (tipo: TipoTela, valor: Double, dataMillis: Long, categoria: String, descricao: String?) -> Unit = { _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState() // Material3 ainda usa SnackbarHostState; scaffoldState kept for compatibility
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var tipo by remember { mutableStateOf(TipoTela.DESPESA) }
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // default date = today
    val calendar = remember { Calendar.getInstance() }
    var dateMillis by remember { mutableStateOf(calendar.timeInMillis) }
    var isSaving by remember { mutableStateOf(false) }

    // sample categories — depois você puxa do repo real
    val sampleCategories = listOf("Alimentação", "Transporte", "Salário", "Lazer", "Outros")

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Registrar Transação") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Money, contentDescription = "Voltar")
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tipo (Receita / Despesa) - segmented like chips
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = tipo == TipoTela.RECEITA,
                    onClick = { tipo = TipoTela.RECEITA },
                    label = { Text("Receita") }
                )
                FilterChip(
                    selected = tipo == TipoTela.DESPESA,
                    onClick = { tipo = TipoTela.DESPESA },
                    label = { Text("Despesa") }
                )
            }

            // Valor
            OutlinedTextField(
                value = amountText,
                onValueChange = { new ->
                    // allow only numbers and dot/comma
                    val filtered = new.filter { it.isDigit() || it == '.' || it == ',' }
                    amountText = filtered
                },
                label = { Text("Valor (ex: 123.45)") },
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                leadingIcon = { Icon(Icons.Default.Money, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Data (DatePicker)
            val dateLabel = remember(dateMillis) { sdf.format(Date(dateMillis)) }
            val datePickerOnClick = {
                val c = Calendar.getInstance().apply { timeInMillis = dateMillis }
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val nc = Calendar.getInstance()
                        nc.set(year, month, dayOfMonth)
                        dateMillis = nc.timeInMillis
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            OutlinedTextField(
                value = dateLabel,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerOnClick() },
                label = { Text("Data") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) }
            )

            // Categoria (dropdown)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedCategory ?: "Escolha uma categoria",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Categoria") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    sampleCategories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Descrição
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save button
            Button(
                onClick = {
                    // validar
                    val parsed = amountText.replace(",", ".").toDoubleOrNull()
                    when {
                        parsed == null -> {
                            Toast.makeText(context, "Insira um valor válido", Toast.LENGTH_SHORT).show()
                        }
                        parsed <= 0.0 -> {
                            Toast.makeText(context, "Valor deve ser maior que 0", Toast.LENGTH_SHORT).show()
                        }
                        selectedCategory == null -> {
                            Toast.makeText(context, "Escolha uma categoria", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // por enquanto, só feedback visual e voltar
                            isSaving = true
                            // aqui você pode chamar o onSave em uma coroutine (ViewModel)
                            // Exemplo simplificado: mostrar toast e voltar
                            Toast.makeText(context, "Salvando...", Toast.LENGTH_SHORT).show()
                            // TODO: chamar onSave(...) usando scope no ViewModel
                            navController.popBackStack() // volta para home/historico
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salvar", fontSize = 16.sp)
            }
        }
    }
}

/**
 * Tipo usado aqui só para UI local.
 * Quando integrar com o backend, substitua pelo seu TipoTransacao model do backend.
 */
enum class TipoTela {
    RECEITA, DESPESA
}
