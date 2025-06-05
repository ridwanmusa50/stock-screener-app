package io.rid.stockscreenerapp.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.ui.theme.AppTypography
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.fullRoundedCornerShape
import io.rid.stockscreenerapp.ui.theme.gray03100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOutlinedTxtField(
    value: String = "",
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    @StringRes placeHolderTxtResId: Int? = null,
    placeHolderTxtStyle: TextStyle = AppTypography.bodySmall.copy(color = gray03100),
    txtStyle: TextStyle = AppTypography.bodyMedium,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    horizontalPadding: Dp = Dimen.Spacing.spacing12,
    verticalPadding: Dp = Dimen.Spacing.spacing12,
    focusedBorderColor: Color = Color.Companion.Transparent,
    unFocusedBorderColor: Color = Color.Companion.Transparent,
    errorBorderColor: Color = Color.Companion.Transparent,
    focusedContainerColor: Color = Color.Companion.Transparent,
    unFocusedContainerColor: Color = Color.Companion.Transparent,
    disabledContainerColor: Color = Color.Companion.Transparent,
    errorContainerColor: Color = Color.Companion.Transparent,
    shape: Shape = fullRoundedCornerShape.small,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Companion.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Companion.Default,
    visualTransformation: VisualTransformation = VisualTransformation.Companion.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Companion.Black),
    @DrawableRes leadingIconResId: Int? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val colors = TextFieldDefaults.colors(
        focusedIndicatorColor = focusedBorderColor,
        unfocusedIndicatorColor = unFocusedBorderColor,
        errorIndicatorColor = errorBorderColor,
        focusedContainerColor = focusedContainerColor,
        unfocusedContainerColor = unFocusedContainerColor,
        disabledContainerColor = disabledContainerColor,
        errorContainerColor = errorContainerColor
    )
    val updatedKeyboardActions = KeyboardActions(
        onNext = { focusManager.moveFocus(FocusDirection.Companion.Next) },
        onDone = {
            focusManager.clearFocus()
            keyboardActions.onDone?.let { it() }
        },
        onSearch = {
            focusManager.clearFocus()
            keyboardActions.onSearch?.let { it() }
        },
        onGo = {
            focusManager.clearFocus()
            keyboardActions.onGo?.let { it() }
        }
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = isEnabled,
        textStyle = txtStyle,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = updatedKeyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        cursorBrush = cursorBrush
    ) { innerTxtField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value,
            visualTransformation = visualTransformation,
            innerTextField = innerTxtField,
            placeholder = if (placeHolderTxtResId == null) null else {
                {
                    AppTxt(
                        txtResId = placeHolderTxtResId,
                        style = placeHolderTxtStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                }
            },
            trailingIcon = trailingIcon,
            leadingIcon = if (leadingIconResId == null) null else {
                {
                    Icon(
                        painter = painterResource(id = leadingIconResId),
                        modifier = Modifier.padding(all = Dimen.Spacing.spacing8),
                        contentDescription = null
                    )
                }
            },
            singleLine = singleLine,
            enabled = isEnabled,
            isError = isError,
            interactionSource = interactionSource,
            colors = colors,
            contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                start = horizontalPadding,
                end = horizontalPadding,
                top = verticalPadding,
                bottom = verticalPadding
            ),
            container = {
                OutlinedTextFieldDefaults.Container(
                    enabled = isEnabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    shape = shape,
                )
            }
        )
    }
}

@Preview
@Composable
private fun PreviewAppOutlinedTxtField() {
    AppOutlinedTxtField(
        placeHolderTxtResId = R.string.placeholder_search_stock,
        leadingIconResId = R.drawable.ic_search,
        onValueChange = { }
    )
}