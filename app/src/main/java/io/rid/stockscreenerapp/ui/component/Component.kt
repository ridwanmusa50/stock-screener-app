@file:Suppress("ModifierParameter")

package io.rid.stockscreenerapp.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing
import io.rid.stockscreenerapp.ui.theme.blue01010
import io.rid.stockscreenerapp.ui.util.ThrottleEvent
import io.rid.stockscreenerapp.ui.util.get

@Composable
fun AppLoadImage(
    imageUrl: String? = null,
    @DrawableRes imageResId: Int? = null,
    modifier: Modifier = Modifier,
    @DrawableRes placeholderResId: Int? = null,
    contentScale: ContentScale = ContentScale.Companion.Fit
) {
    val context = LocalContext.current
    val placeholder = placeholderResId ?: imageResId

    val imageRequest = ImageRequest.Builder(context)
        .data(imageUrl ?: imageResId)
        .crossfade(true)
        .apply {
            placeholder?.let { placeholder(it) }
            decoderFactory(GifDecoder.Factory())
        }
        .build()

    AsyncImage(
        model = imageRequest,
        modifier = modifier,
        contentDescription = null,
        contentScale = contentScale
    )
}

@Preview
@Composable
private fun PreviewAppLoadImage() {
    AppLoadImage(imageResId = R.drawable.ic_launcher_foreground)
}

@Composable
fun AppImageBtn(
    imageUrl: String? = null,
    @DrawableRes imageResId: Int? = null,
    backgroundModifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    @DrawableRes placeholderResId: Int? = null,
    isEnabled: Boolean = true,
    contentScale: ContentScale = ContentScale.Companion.None,
    onClick: () -> Unit
) {
    val jcThrottleUtils = remember { ThrottleEvent.Companion.get() }

    Box(
        modifier = backgroundModifier
            .clickable(
                enabled = isEnabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = false,
                    radius = Spacing.spacing16,
                    color = blue01010
                ),
                onClick = {
                    jcThrottleUtils.processEvent { onClick() }
                }
            ),
        contentAlignment = Alignment.Companion.Center
    ) {
        AppLoadImage(
            imageUrl = imageUrl,
            imageResId = imageResId,
            modifier = imageModifier.padding(all = Spacing.spacing8),
            placeholderResId = placeholderResId,
            contentScale = contentScale
        )
    }
}

@Preview
@Composable
private fun PreviewImageBtn() {
    AppImageBtn(imageResId = R.drawable.ic_launcher_foreground, onClick = { })
}

@Composable
fun AppSnackBar(snackBarHostState: SnackbarHostState) {
    SnackbarHost(snackBarHostState) { data ->
        Snackbar(
            modifier = Modifier.padding(Spacing.spacing16),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            actionColor = MaterialTheme.colorScheme.primary,
            snackbarData = data
        )
    }
}

@Preview
@Composable
private fun PreviewAppSnackBar() {
    val snackBarHostState = remember { SnackbarHostState() }
    AppSnackBar(snackBarHostState)
}