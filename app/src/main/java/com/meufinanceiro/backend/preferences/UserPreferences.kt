package com.meufinanceiro.backend.preferences

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun salvarNome(nome: String) {
        prefs.edit().putString("nome_usuario", nome).apply()
    }

    fun recuperarNome(): String {
        return prefs.getString("nome_usuario", "Usuário") ?: "Usuário"
    }
}