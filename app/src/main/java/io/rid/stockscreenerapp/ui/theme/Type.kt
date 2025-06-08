package io.rid.stockscreenerapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.rid.stockscreenerapp.R

private val AppFontFamily = FontFamily(
    Font(R.font.open_sans_light, FontWeight.W300),
    Font(R.font.open_sans_regular, FontWeight.W400),
    Font(R.font.open_sans_medium, FontWeight.W500),
    Font(R.font.open_sans_semi_bold, FontWeight.W600),
    Font(R.font.open_sans_bold, FontWeight.W700),
    Font(R.font.open_sans_extra_bold, FontWeight.W800),
)

internal val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.W700,
        fontFamily = AppFontFamily,
        fontSize = Dimen.TxtSize.txtSize30,
        lineHeight = Dimen.LineHeight.lineHeight40,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.W700,
        fontFamily = AppFontFamily,
        fontSize = Dimen.TxtSize.txtSize26,
        lineHeight = Dimen.LineHeight.lineHeight36,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    ),
    headlineSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W700,
        fontSize = Dimen.TxtSize.txtSize24,
        lineHeight = Dimen.LineHeight.lineHeight32,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = Dimen.TxtSize.txtSize24,
        lineHeight = Dimen.LineHeight.lineHeight28,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = Dimen.TxtSize.txtSize18,
        lineHeight = Dimen.LineHeight.lineHeight24,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0Point15,
        color = gray02100
    ),
    titleSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = Dimen.TxtSize.txtSize16,
        lineHeight = Dimen.LineHeight.lineHeight20,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0Point1,
        color = gray02100
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = Dimen.TxtSize.txtSize18,
        lineHeight = Dimen.LineHeight.lineHeight24,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = Dimen.TxtSize.txtSize16,
        lineHeight = Dimen.LineHeight.lineHeight20,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = Dimen.TxtSize.txtSize14,
        lineHeight = Dimen.LineHeight.lineHeight16,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W700,
        fontSize = Dimen.TxtSize.txtSize14,
        lineHeight = Dimen.LineHeight.lineHeight20,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    ),
    labelMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = Dimen.TxtSize.txtSize12,
        lineHeight = Dimen.LineHeight.lineHeight16,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = Dimen.TxtSize.txtSize10,
        lineHeight = Dimen.LineHeight.lineHeight16,
        letterSpacing = Dimen.LetterSpacing.letterSpacing0,
        color = gray02100
    )
)