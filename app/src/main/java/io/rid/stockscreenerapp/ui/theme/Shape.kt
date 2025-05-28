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

val topRoundedCornerShape = Shapes(
    extraSmall = RoundedCornerShape(
        topStart = Dimen.CornerRadius.cornerRadius2,
        topEnd = Dimen.CornerRadius.cornerRadius2
    ),
    small = RoundedCornerShape(
        topStart = Dimen.CornerRadius.cornerRadius4,
        topEnd = Dimen.CornerRadius.cornerRadius4
    ),
    medium = RoundedCornerShape(
        topStart = Dimen.CornerRadius.cornerRadius8,
        topEnd = Dimen.CornerRadius.cornerRadius8
    ),
    large = RoundedCornerShape(
        topStart = Dimen.CornerRadius.cornerRadius12,
        topEnd = Dimen.CornerRadius.cornerRadius12
    ),
    extraLarge = RoundedCornerShape(
        topStart = Dimen.CornerRadius.cornerRadius16,
        topEnd = Dimen.CornerRadius.cornerRadius16
    )
)

val bottomRoundedCornerShape = Shapes(
    extraSmall = RoundedCornerShape(
        bottomStart = Dimen.CornerRadius.cornerRadius2,
        bottomEnd = Dimen.CornerRadius.cornerRadius2
    ),
    small = RoundedCornerShape(
        bottomStart = Dimen.CornerRadius.cornerRadius4,
        bottomEnd = Dimen.CornerRadius.cornerRadius4
    ),
    medium = RoundedCornerShape(
        bottomStart = Dimen.CornerRadius.cornerRadius8,
        bottomEnd = Dimen.CornerRadius.cornerRadius8
    ),
    large = RoundedCornerShape(
        bottomStart = Dimen.CornerRadius.cornerRadius12,
        bottomEnd = Dimen.CornerRadius.cornerRadius12
    ),
    extraLarge = RoundedCornerShape(
        bottomStart = Dimen.CornerRadius.cornerRadius16,
        bottomEnd = Dimen.CornerRadius.cornerRadius16
    )
)