package com.meufinanceiro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.meufinanceiro.backend.model.TransacaoComCategoria
import com.meufinanceiro.backend.repository.TransacaoRepository
import com.meufinanceiro.ui.extensions.toTransacao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoricoViewModel(
    private val repository: TransacaoRepository
) : ViewModel() {

    private val _transacoes = MutableStateFlow<List<TransacaoComCategoria>>(emptyList())
    val transacoes: StateFlow<List<TransacaoComCategoria>> = _transacoes

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _transacoes.value = repository.listarComCategoria()
        }
    }

    fun deletar(id: Long) {
        viewModelScope.launch {
            val t = repository.buscarComCategoriaPorId(id)
            if (t != null) {
                repository.deletar(t.toTransacao())
                carregar()
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class HistoricoFactory(
    private val repository: TransacaoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoricoViewModel(repository) as T
    }
}
