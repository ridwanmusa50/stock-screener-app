package io.rid.stockscreenerapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(Dimen.CornerRadius.cornerRadius2),
    small = RoundedCornerShape(Dimen.CornerRadius.cornerRadius4),
    medium = RoundedCornerShape(Dimen.CornerRadius.cornerRadius8),
    large = RoundedCornerShape(Dimen.CornerRadius.cornerRadius12),
    extraLarge = RoundedCornerShape(Dimen.CornerRadius.cornerRadius16)
)

val fullRoundedCornerShape = Shapes(
    extraSmall = RoundedCornerShape(Dimen.CornerRadius.cornerRadius2),
    small = RoundedCornerShape(Dimen.CornerRadius.cornerRadius4),
    medium = RoundedCornerShape(Dimen.CornerRadius.cornerRadius8),
    large = RoundedCornerShape(Dimen.CornerRadius.cornerRadius12),
    extraLarge = RoundedCornerShape(Dimen.CornerRadius.cornerRadius16)
)