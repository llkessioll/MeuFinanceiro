package com.meufinanceiro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.meufinanceiro.backend.model.TransacaoComCategoria
import com.meufinanceiro.backend.repository.TransacaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoricoViewModel(
    private val repository: TransacaoRepository
) : ViewModel() {

    private val _transacoes = MutableStateFlow<List<TransacaoComCategoria>>(emptyList())
    val transacoes = _transacoes.asStateFlow()

    init {
        atualizarLista()
    }

    // Função pública para a tela chamar
    fun atualizarLista() {
        viewModelScope.launch {
            _transacoes.value = repository.listarComCategoria()
        }
    }

    // Função de deletar também atualiza a lista depois
    fun deletar(transacaoId: Long) {
        viewModelScope.launch {
            val itemCompleto = repository.buscarComCategoriaPorId(transacaoId)
            if (itemCompleto != null) {
                // Precisamos converter de volta para Transacao simples para deletar
                val transacaoParaDeletar = itemCompleto.transacao
                repository.deletar(transacaoParaDeletar)
                atualizarLista() // Recarrega após deletar
            }
        }
    }
}

class HistoricoFactory(private val repository: TransacaoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoricoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}