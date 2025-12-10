package com.meufinanceiro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.meufinanceiro.backend.model.TipoTransacao
import com.meufinanceiro.backend.preferences.UserPreferences
import com.meufinanceiro.backend.repository.TransacaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TransacaoRepository,
    private val userPreferences: UserPreferences // <--- Injeção do Preferences
) : ViewModel() {

    private val _saldoTotal = MutableStateFlow(0.0)
    val saldoTotal = _saldoTotal.asStateFlow()

    // Nome dinâmico (Começa como "Usuário")
    private val _nomeUsuario = MutableStateFlow("Usuário")
    val nomeUsuario = _nomeUsuario.asStateFlow()

    init {
        carregarSaldo()
        carregarNome()
    }

    fun carregarSaldo() {
        viewModelScope.launch {
            val lista = repository.listarTodas()
            val receitas = lista.filter { it.tipo == TipoTransacao.RECEITA }.sumOf { it.valor }
            val despesas = lista.filter { it.tipo == TipoTransacao.DESPESA }.sumOf { it.valor }
            _saldoTotal.value = receitas - despesas
        }
    }

    private fun carregarNome() {
        _nomeUsuario.value = userPreferences.recuperarNome()
    }

    fun atualizarNome(novoNome: String) {
        userPreferences.salvarNome(novoNome)
        _nomeUsuario.value = novoNome
    }
}

// Factory atualizada para receber o Contexto
class HomeViewModelFactory(
    private val repository: TransacaoRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                repository,
                UserPreferences(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}