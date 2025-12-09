package com.meufinanceiro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meufinanceiro.backend.service.ResumoFinanceiroService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResumoFinanceiroViewModel(
    private val service: ResumoFinanceiroService
) : ViewModel() {

    private val _totalReceitas = MutableStateFlow(0.0)
    val totalReceitas: StateFlow<Double> = _totalReceitas

    private val _totalDespesas = MutableStateFlow(0.0)
    val totalDespesas: StateFlow<Double> = _totalDespesas

    private val _saldo = MutableStateFlow(0.0)
    val saldo: StateFlow<Double> = _saldo

    init {
        carregarResumo()
    }

    fun carregarResumo() {
        viewModelScope.launch {
            val resumo = service.calcularResumo()
            _totalReceitas.value = resumo.totalReceitas
            _totalDespesas.value = resumo.totalDespesas
            _saldo.value = resumo.saldo
        }
    }
}
