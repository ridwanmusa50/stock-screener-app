package io.rid.stockscreenerapp.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.ui.util.ThrottleEvent
import io.rid.stockscreenerapp.ui.util.get

@Composable
fun AppTxt(
    @StringRes txtResId: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    txtAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Companion.Clip,
    onClick: (() -> Unit)? = null
) {
    AppTxt(
        txt = stringResource(txtResId),
        modifier = modifier,
        style = style,
        txtAlign = txtAlign,
        maxLines = maxLines,
        overflow = overflow,
        onClick = onClick
    )
}

@Composable
fun AppTxt(
    txt: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    txtAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Companion.Clip,
    onClick: (() -> Unit)? = null
) {
    val throttleUtil = remember { ThrottleEvent.Companion.get() }
    val updatedModifier = remember(onClick, modifier) {
        if (onClick == null) {
            modifier
        } else {
            modifier.clickable(
                interactionSource = null,
                indication = null,
                onClick = {
                    throttleUtil.processEvent { onClick() }
                }
            )
        }
    }

    Text(
        text = txt,
        modifier = updatedModifier,
        textAlign = txtAlign,
        overflow = overflow,
        maxLines = maxLines,
        style = style
    )
}

@Preview
@Composable
private fun PreviewAppTxt() {
    AppTxt(txtResId = R.string.app_name)
}