package com.meufinanceiro.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarScreen(
    navController: NavController,
    // callback para ligar ao backend quando quiser
    onSave: suspend (tipo: TipoTela, valor: Double, dataMillis: Long, categoria: String, descricao: String?) -> Unit = { _, _, _, _, _ -> }
) {
    val context = LocalContext.current

    // ESTADOS DO FORMULÁRIO
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var tipo by remember { mutableStateOf(TipoTela.DESPESA) }

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // DATA (Padrão = Hoje)
    val calendar = remember { Calendar.getInstance() }
    var dateMillis by remember { mutableStateOf(calendar.timeInMillis) }
    var isSaving by remember { mutableStateOf(false) }

    // Categorias de exemplo
    val sampleCategories = listOf("Alimentação", "Transporte", "Salário", "Lazer", "Outros")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Transação") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
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
            // 1. SELETOR DE TIPO (Receita / Despesa)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = tipo == TipoTela.RECEITA,
                    onClick = { tipo = TipoTela.RECEITA },
                    label = { Text("Receita") },
                    leadingIcon = {
                        if (tipo == TipoTela.RECEITA) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
                FilterChip(
                    selected = tipo == TipoTela.DESPESA,
                    onClick = { tipo = TipoTela.DESPESA },
                    label = { Text("Despesa") },
                    leadingIcon = {
                        if (tipo == TipoTela.DESPESA) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
            }

            // 2. CAMPO DE VALOR
            OutlinedTextField(
                value = amountText,
                onValueChange = { new ->
                    // Permite apenas números, ponto e vírgula
                    val filtered = new.filter { it.isDigit() || it == '.' || it == ',' }
                    amountText = filtered
                },
                label = { Text("Valor (ex: 123.45)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                // Troquei o ícone Money (que falta) pelo ShoppingCart
                leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 3. CAMPO DE DATA (DatePicker)
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
                // Troquei CalendarToday (que falta) pelo DateRange
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
            )

            // 4. CAMPO DE CATEGORIA (Dropdown)
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
                        .menuAnchor(), // Importante para o Menu
                    label = { Text("Categoria") },
                    // Troquei Category (que falta) pelo List
                    leadingIcon = { Icon(Icons.Default.List, contentDescription = null) },
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

            // 5. CAMPO DE DESCRIÇÃO
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 6. BOTÃO DE SALVAR
            Button(
                onClick = {
                    // Validação simples
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
                            isSaving = true
                            Toast.makeText(context, "Salvando...", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
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

// ENUM auxiliar
enum class TipoTela {
    RECEITA, DESPESA
}