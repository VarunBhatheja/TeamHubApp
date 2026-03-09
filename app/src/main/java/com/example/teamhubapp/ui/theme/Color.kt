package com.example.teamhubapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Shared — same in both light and dark ─────────────────────────────────────
val AccentBlue     = Color(0xFF3D8EF0)   // brand blue — buttons, rings, selected states
val AccentBlueFade = Color(0xFF85BFFF)   // light edge of gradient underline
val ActiveGreen    = Color(0xFF34C759)   // status dot active
val InactiveRed    = Color(0xFFFF3B30)   // status dot inactive

// ── Light mode ────────────────────────────────────────────────────────────────
val HeaderTopLight          = Color(0xFF1E3A5F)
val HeaderBottomLight       = Color(0xFF2D5A8E)
val TrayBackgroundLight     = Color(0xFFF2F4F7)   // warm off-white tray
val CardBackgroundLight     = Color(0xFFFFFFFF)   // pure white card
val CardInactiveLight       = Color(0xFFF8F9FB)   // barely off-white inactive card
val TextPrimaryLight        = Color(0xFF0D1B2A)
val TextSecondaryLight      = Color(0xFF5C7185)
val TextMutedLight          = Color(0xFF9AAAB8)
val DividerLight            = Color(0xFFE2E8F0)
val FilterChipBgLight       = Color(0xFFE8EFF8)   // unselected pill background
val FilterChipSelectedLight = Color(0xFF3D8EF0)   // selected pill background
val SearchBarBgLight        = Color(0xFFFFFFFF)

// ── Dark mode ─────────────────────────────────────────────────────────────────
val HeaderTopDark           = Color(0xFF070E1A)
val HeaderBottomDark        = Color(0xFF0D1B2E)
val TrayBackgroundDark      = Color(0xFF12191F)   // very dark blue-grey tray
val CardBackgroundDark      = Color(0xFF1E2530)   // dark card
val CardInactiveDark        = Color(0xFF181E27)   // recessed inactive card
val TextPrimaryDark         = Color(0xFFE8EFF7)
val TextSecondaryDark       = Color(0xFF7A9BB5)
val TextMutedDark           = Color(0xFF4A6070)
val DividerDark             = Color(0xFF2A3545)
val FilterChipBgDark        = Color(0xFF1E2D3D)
val FilterChipSelectedDark  = Color(0xFF3D8EF0)   // same accent — works on dark too
val SearchBarBgDark         = Color(0xFF1A2535)

// ── Material3 color schemes ───────────────────────────────────────────────────
// Maps custom colours into M3 slots so the whole app respects system theme
val LightColorScheme = lightColorScheme(
    primary              = AccentBlue,
    onPrimary            = Color.White,
    primaryContainer     = Color(0xFFDBEDFF),
    onPrimaryContainer   = Color(0xFF001D36),
    background           = TrayBackgroundLight,
    onBackground         = TextPrimaryLight,
    surface              = CardBackgroundLight,
    onSurface            = TextPrimaryLight,
    onSurfaceVariant     = TextSecondaryLight,
    outline              = DividerLight,
    outlineVariant       = FilterChipBgLight,
    surfaceVariant       = CardInactiveLight,
    onSecondaryContainer = TextMutedLight
)

val DarkColorScheme = darkColorScheme(
    primary              = AccentBlue,
    onPrimary            = Color.White,
    primaryContainer     = Color(0xFF0D2744),
    onPrimaryContainer   = Color(0xFFD1E4FF),
    background           = TrayBackgroundDark,
    onBackground         = TextPrimaryDark,
    surface              = CardBackgroundDark,
    onSurface            = TextPrimaryDark,
    onSurfaceVariant     = TextSecondaryDark,
    outline              = DividerDark,
    outlineVariant       = FilterChipBgDark,
    surfaceVariant       = CardInactiveDark,
    onSecondaryContainer = TextMutedDark
)

// Returns correct gradient colours based on dark mode — used in screen headers
fun headerGradientColors(isDark: Boolean) = if (isDark)
    listOf(HeaderTopDark, HeaderBottomDark)
else
    listOf(HeaderTopLight, HeaderBottomLight)