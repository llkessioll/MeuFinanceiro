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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import com.meufinanceiro.backend.db.AppDatabase
import com.meufinanceiro.backend.model.Categoria
import com.meufinanceiro.backend.model.TipoTransacao
import com.meufinanceiro.backend.repository.CategoriaRepository
import com.meufinanceiro.backend.repository.TransacaoRepository
import com.meufinanceiro.ui.viewmodel.RegistrarViewModel
import com.meufinanceiro.ui.viewmodel.RegistrarViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarScreen(
    navController: NavController,
    transacaoId: Long = 0L // NOVO: Recebe o ID para editar (0 = novo)
) {
    val context = LocalContext.current
    val db = remember { Room.databaseBuilder(context, AppDatabase::class.java, "meu_financeiro.db").build() }

    val viewModel: RegistrarViewModel = viewModel(
        factory = RegistrarViewModelFactory(
            TransacaoRepository(db.transacaoDao()),
            CategoriaRepository(db.categoriaDao())
        )
    )

    // Estados do Formulário
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Categoria?>(null) }
    var tipo by remember { mutableStateOf(TipoTela.DESPESA) }

    val calendar = remember { Calendar.getInstance() }
    var dateMillis by remember { mutableStateOf(calendar.timeInMillis) }
    var isSaving by remember { mutableStateOf(false) }

    // --- LÓGICA DE CARREGAR DADOS (UPDATE) ---
    LaunchedEffect(transacaoId) {
        if (transacaoId > 0) {
            viewModel.carregarDadosParaEdicao(transacaoId) { transacao, categoria ->
                // Preenche os campos com os dados do banco
                amountText = transacao.valor.toString().replace(".", ",")
                description = transacao.descricao ?: ""
                dateMillis = transacao.dataMillis
                selectedCategory = categoria
                tipo = if (transacao.tipo == TipoTransacao.RECEITA) TipoTela.RECEITA else TipoTela.DESPESA
            }
        }
    }
    // -----------------------------------------

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val listaCategorias by viewModel.categorias.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transacaoId > 0L) "Editar Transação" else "Nova Transação") },
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
            // TIPO
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = tipo == TipoTela.RECEITA,
                    onClick = { tipo = TipoTela.RECEITA },
                    label = { Text("Receita") },
                    leadingIcon = { if (tipo == TipoTela.RECEITA) Icon(Icons.Default.Check, null) }
                )
                FilterChip(
                    selected = tipo == TipoTela.DESPESA,
                    onClick = { tipo = TipoTela.DESPESA },
                    label = { Text("Despesa") },
                    leadingIcon = { if (tipo == TipoTela.DESPESA) Icon(Icons.Default.Check, null) }
                )
            }

            // VALOR
            OutlinedTextField(
                value = amountText,
                onValueChange = { new -> amountText = new.filter { it.isDigit() || it == '.' || it == ',' } },
                label = { Text("Valor") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.ShoppingCart, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // DATA
            val dateLabel = remember(dateMillis) { sdf.format(Date(dateMillis)) }
            val datePickerOnClick = {
                val c = Calendar.getInstance().apply { timeInMillis = dateMillis }
                DatePickerDialog(context, { _, y, m, d ->
                    val nc = Calendar.getInstance()
                    nc.set(y, m, d)
                    dateMillis = nc.timeInMillis
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
            }

            OutlinedTextField(
                value = dateLabel,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { datePickerOnClick() },
                label = { Text("Data") },
                leadingIcon = { Icon(Icons.Default.DateRange, null) }
            )

            // CATEGORIA
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedCategory?.nome ?: "Escolha uma categoria",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    label = { Text("Categoria") },
                    leadingIcon = { Icon(Icons.Default.List, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listaCategorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nome) },
                            onClick = { selectedCategory = categoria; expanded = false }
                        )
                    }
                }
            }

            // DESCRIÇÃO
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÃO SALVAR
            Button(
                enabled = !isSaving,
                onClick = {
                    val parsed = amountText.replace(",", ".").toDoubleOrNull()
                    if (parsed == null || parsed <= 0.0 || selectedCategory == null) {
                        Toast.makeText(context, "Preencha valor e categoria", Toast.LENGTH_SHORT).show()
                    } else {
                        isSaving = true
                        viewModel.salvarTransacao(
                            tipoTela = tipo,
                            valor = parsed,
                            dataMillis = dateMillis,
                            categoriaId = selectedCategory!!.id,
                            descricao = description,
                            onSuccess = {
                                Toast.makeText(context, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { isSaving = false }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSaving) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                else Text(if (transacaoId > 0L) "Atualizar" else "Salvar", fontSize = 16.sp)
            }
        }
    }
}

enum class TipoTela { RECEITA, DESPESA }