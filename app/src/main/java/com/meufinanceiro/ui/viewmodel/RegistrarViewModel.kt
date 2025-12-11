package com.meufinanceiro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.meufinanceiro.backend.model.Categoria
import com.meufinanceiro.backend.model.Transacao
import com.meufinanceiro.backend.model.TipoTransacao
import com.meufinanceiro.backend.repository.CategoriaRepository
import com.meufinanceiro.backend.repository.TransacaoRepository
import com.meufinanceiro.ui.screens.TipoTela
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrarViewModel(
    private val transacaoRepository: TransacaoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    // Armazena a transação que está sendo editada (se houver)
    var transacaoIdAtual: Long = 0L

    init {
        carregarCategorias()
    }

    private fun carregarCategorias() {
        viewModelScope.launch {
            _categorias.value = categoriaRepository.listarTodas()
        }
    }

    // NOVA FUNÇÃO: Carrega dados para editar
    fun carregarDadosParaEdicao(id: Long, onResult: (Transacao, Categoria?) -> Unit) {
        if (id == 0L) return
        transacaoIdAtual = id

        viewModelScope.launch {
            val transacaoCompleta = transacaoRepository.buscarComCategoriaPorId(id)
            if (transacaoCompleta != null) {
                onResult(transacaoCompleta.transacao, transacaoCompleta.categoria)
            }
        }
    }

    fun salvarTransacao(
        tipoTela: TipoTela,
        valor: Double,
        dataMillis: Long,
        categoriaId: Long,
        descricao: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val tipoBackend = if (tipoTela == TipoTela.RECEITA)
                TipoTransacao.RECEITA
            else
                TipoTransacao.DESPESA

            val novaTransacao = Transacao(
                id = transacaoIdAtual, // Se for 0 cria novo, se for >0 atualiza
                tipo = tipoBackend,
                valor = valor,
                categoriaId = categoriaId,
                descricao = descricao,
                dataMillis = dataMillis
            )

            try {
                transacaoRepository.salvar(novaTransacao)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Erro ao salvar: ${e.message}")
            }
        }
    }
}

class RegistrarViewModelFactory(
    private val transacaoRepo: TransacaoRepository,
    private val categoriaRepo: CategoriaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrarViewModel(transacaoRepo, categoriaRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}