package io.rid.stockscreenerapp.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.ui.theme.AppTypography
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.blue02100
import io.rid.stockscreenerapp.ui.util.ThrottleEvent
import io.rid.stockscreenerapp.ui.util.get

@Composable
fun AppBtn(
    @StringRes txtResId: Int,
    modifier: Modifier = Modifier,
    modifierTxt: Modifier = Modifier,
    txtStyle: TextStyle = AppTypography.titleSmall.copy(color = Color.Companion.White, fontWeight = FontWeight.Companion.W700),
    shape: Shape = ButtonDefaults.elevatedShape,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.elevatedButtonElevation(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    isEnabled: Boolean = true,
    onClick: () -> Unit
){
    AppBtn(
        txt = stringResource(txtResId),
        modifier = modifier,
        modifierTxt = modifierTxt,
        txtStyle = txtStyle,
        shape = shape,
        border = border,
        colors = colors,
        elevation = elevation,
        contentPadding = contentPadding,
        isEnabled = isEnabled,
        onClick = onClick
    )
}

@Composable
fun AppBtn(
    txt: String,
    modifier: Modifier = Modifier,
    modifierTxt: Modifier = Modifier,
    txtStyle: TextStyle = AppTypography.titleSmall.copy(color = Color.Companion.White, fontWeight = FontWeight.Companion.W700),
    shape: Shape = ButtonDefaults.elevatedShape,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.elevatedButtonElevation(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val jcThrottleUtils = remember { ThrottleEvent.Companion.get() }
    val updatedColor = colors.copy(contentColor = blue02100) // Ripple color

    ElevatedButton(
        modifier = modifier.defaultMinSize(minWidth = Dimen.Spacing.spacing0),
        enabled = isEnabled,
        interactionSource = remember { MutableInteractionSource() },
        shape = shape,
        border = border,
        colors = updatedColor,
        elevation = elevation,
        contentPadding = contentPadding,
        onClick = {
            jcThrottleUtils.processEvent { onClick() }
        },
    ) {
        AppTxt(
            txt = txt,
            modifier = modifierTxt,
            style = txtStyle,
            txtAlign = TextAlign.Companion.Center
        )
    }

}

@Preview
@Composable
fun PreviewBtn() {
    AppBtn(txtResId = R.string.app_name, onClick = { })
}