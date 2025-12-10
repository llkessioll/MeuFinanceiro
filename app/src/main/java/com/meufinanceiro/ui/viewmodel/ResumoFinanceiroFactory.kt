package com.meufinanceiro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meufinanceiro.backend.service.ResumoFinanceiroService

class ResumoFinanceiroFactory(
    private val service: ResumoFinanceiroService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ResumoFinanceiroViewModel(service) as T
    }
}
