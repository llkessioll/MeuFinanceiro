package com.meufinanceiro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ----------------------
// PALETA MODO CLARO
// ----------------------
private val LightColorScheme = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = Color.White,
    primaryContainer = VerdePrimarioClaro,
    onPrimaryContainer = Color.White,

    secondary = AzulSecundario,
    onSecondary = Color.White,

    background = FundoClaro,
    onBackground = TextoPrimarioClaro,

    surface = Color.White,
    onSurface = TextoPrimarioClaro
)

// ----------------------
// PALETA MODO ESCURO
// ----------------------
private val DarkColorScheme = darkColorScheme(
    primary = VerdePrimarioEscuroAlt,
    onPrimary = Color.Black,
    primaryContainer = VerdePrimarioEscuroAlt,
    onPrimaryContainer = Color.Black,

    secondary = AzulSecundarioEscuro,
    onSecondary = Color.Black,

    background = FundoEscuro,
    onBackground = TextoPrimarioEscuro,

    surface = CardEscuro,
    onSurface = TextoPrimarioEscuro
)

// ----------------------
// THEME
// ----------------------
@Composable
fun MeuFinanceiroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors =
        if (darkTheme) DarkColorScheme
        else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
