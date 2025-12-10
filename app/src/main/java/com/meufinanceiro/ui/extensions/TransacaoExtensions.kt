package com.meufinanceiro.ui.extensions

import com.meufinanceiro.backend.model.Categoria
import com.meufinanceiro.backend.model.Transacao
import com.meufinanceiro.backend.model.TransacaoComCategoria

// Converte TransacaoComCategoria → Transacao (para deletar)
fun TransacaoComCategoria.toTransacao(): Transacao {
    return transacao.copy()
}

// Nome amigável da categoria
val TransacaoComCategoria.categoriaNome: String
    get() = categoria.nome
