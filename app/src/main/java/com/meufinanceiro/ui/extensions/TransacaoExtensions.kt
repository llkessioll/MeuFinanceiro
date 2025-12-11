package com.meufinanceiro.ui.extensions

import com.meufinanceiro.backend.model.Transacao
import com.meufinanceiro.backend.model.TransacaoComCategoria
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. Converte TransacaoComCategoria -> Transacao
fun TransacaoComCategoria.toTransacao(): Transacao {
    return transacao.copy()
}

// 2. Nome fácil da categoria
val TransacaoComCategoria.categoriaNome: String
    get() = categoria.nome

// 3. Formatar Dinheiro (R$ 1.200,00)
fun Double.toCurrency(): String {
    val ptBr = Locale("pt", "BR")
    return NumberFormat.getCurrencyInstance(ptBr).format(this)
}

// 4. Formatar Data (12/12/2025) - Substitui o antigo formatDate
fun Long.toDateFormat(): String {
    val ptBr = Locale("pt", "BR")
    val sdf = SimpleDateFormat("dd/MM/yyyy", ptBr)
    return sdf.format(Date(this))
}