package io.rid.stockscreenerapp.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.ui.theme.AppTypography
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.alertDialogFullVerticalEnterTransition
import io.rid.stockscreenerapp.ui.theme.alertDialogHalfVerticalEnterTransition
import io.rid.stockscreenerapp.ui.theme.alertDialogVerticalExitTransition
import io.rid.stockscreenerapp.ui.theme.blue01100
import io.rid.stockscreenerapp.ui.theme.fullRoundedCornerShape
import io.rid.stockscreenerapp.ui.theme.gray01100

enum class DialogButtonType { POSITIVE, NEGATIVE, NONE }

@Composable
fun AppLoadingDialog() {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // To display popup full width screen
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) { AppLoading(modifier = Modifier.fillMaxSize()) }
}

@Composable
private fun AppLoading(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Companion.Center,
        modifier = modifier
    ) {
        AppLoadImage(
            imageResId = R.drawable.ic_loading_spinner,
            modifier = Modifier.size(Dimen.Icon.icLoading),
            contentScale = ContentScale.Companion.Crop
        )
    }
}

@Preview
@Composable
private fun PreviewAppLoadingDialog() {
    AppLoadingDialog()
}

@Composable
fun AppNoInternetDialog(isShowingDialog : Boolean = false) {
    val transitionState = remember { MutableTransitionState(isShowingDialog) }

    LaunchedEffect(isShowingDialog) {
        transitionState.targetState = isShowingDialog
    }

    AnimatedVisibility(
        visibleState = transitionState,
        enter = alertDialogFullVerticalEnterTransition(),
        exit = alertDialogVerticalExitTransition()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Companion.White),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppLoadImage(
                imageResId = R.drawable.ic_no_internet,
                modifier = Modifier.padding(horizontal = Dimen.Spacing.spacing80)
            )

            AppTxt(
                txtResId = R.string.internet_popup_title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimen.Spacing.spacing40, end = Dimen.Spacing.spacing40,
                        top = Dimen.Spacing.spacing40
                    ),
                style = AppTypography.titleLarge,
                txtAlign = TextAlign.Companion.Center
            )

            AppTxt(
                txtResId = R.string.internet_popup_des,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimen.Spacing.spacing40, end = Dimen.Spacing.spacing40,
                        top = Dimen.Spacing.spacing20
                    ),
                style = AppTypography.bodyMedium.copy(color = gray01100),
                txtAlign = TextAlign.Companion.Center
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAppNoInternetDialog() {
    AppNoInternetDialog(isShowingDialog = true)
}

@Composable
fun AppSingleBtnDialog(
    @StringRes btnLblResId: Int,
    @StringRes msgResId: Int? = null,
    @StringRes titleResId: Int? = null,
    @DrawableRes iconResId: Int? = null,
    isDismissible: Boolean = false,
    isPreview: Boolean = false,
    onBtnClick: () -> Unit,
) {
    AppTwoBtnDialog(
        positiveBtnLblResId = btnLblResId,
        msgResId = msgResId,
        iconResId = iconResId,
        titleResId = titleResId,
        isDismissible = isDismissible,
        isPreview = isPreview,
        onPositiveBtnClick = onBtnClick
    )
}

@Composable
fun AppSingleBtnDialog(
    btnLbl: String,
    msg: CharSequence? = null,
    title: CharSequence? = null,
    @DrawableRes iconResId: Int? = null,
    isDismissible: Boolean = false,
    isPreview: Boolean = false,
    onBtnClick: () -> Unit
) {
    AppTwoBtnDialog(
        msg = msg,
        title = title,
        positiveBtnLbl = btnLbl,
        iconResId = iconResId,
        isDismissible = isDismissible,
        isPreview = isPreview,
        onPositiveBtnClick = onBtnClick
    )
}

@Preview
@Composable
private fun PreviewAppSingleBtnDialog() {
    AppSingleBtnDialog(
        btnLbl = stringResource(id = R.string.general_okay),
        msg = stringResource(id = R.string.server_generic_server_error),
        isPreview = true,
        onBtnClick = { }
    )
}

@Composable
fun AppTwoBtnDialog(
    @StringRes msgResId: Int? = null,
    @StringRes titleResId: Int? = null,
    @StringRes positiveBtnLblResId: Int,
    @StringRes negativeBtnLblResId: Int? = null,
    @DrawableRes iconResId: Int? = null,
    isDismissible: Boolean = false,
    isPreview: Boolean = false,
    onPositiveBtnClick: () -> Unit,
    onNegativeBtnClick: (() -> Unit)? = null,
) {
    AppTwoBtnDialog(
        msg = msgResId?.let { stringResource(it) },
        title = titleResId?.let { stringResource(it) },
        positiveBtnLbl = stringResource(positiveBtnLblResId),
        negativeBtnLbl = negativeBtnLblResId?.let { stringResource(it) },
        iconResId = iconResId,
        isDismissible = isDismissible,
        isPreview = isPreview,
        onPositiveBtnClick = onPositiveBtnClick,
        onNegativeBtnClick = onNegativeBtnClick
    )
}

@Composable
fun AppTwoBtnDialog(
    msg: CharSequence? = null,
    title: CharSequence? = null,
    positiveBtnLbl: String,
    negativeBtnLbl: String? = null,
    @DrawableRes iconResId: Int? = null,
    isDismissible: Boolean = false,
    isPreview: Boolean = false,
    onPositiveBtnClick: (() -> Unit)? = null,
    onNegativeBtnClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    val transitionState = remember { MutableTransitionState(isPreview) }
    var clickedButtonType by remember { mutableStateOf(DialogButtonType.NONE) }

    // Enter animation
    LaunchedEffect(Unit) {
        transitionState.targetState = true
    }

    // Once the exit animation completes, trigger the correct callback to make sure dialog shown
    LaunchedEffect(transitionState.currentState, transitionState.targetState) {
        if (!transitionState.currentState && !transitionState.targetState) {
            when (clickedButtonType) {
                DialogButtonType.POSITIVE -> onPositiveBtnClick?.invoke()
                DialogButtonType.NEGATIVE -> onNegativeBtnClick?.invoke()
                else -> {}
            }
            clickedButtonType = DialogButtonType.NONE
            onDismiss?.invoke()
        }
    }

    if (transitionState.currentState || transitionState.targetState) {
        Dialog(
            onDismissRequest = {
                transitionState.targetState = false
                clickedButtonType = DialogButtonType.NONE
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false, // To display popup full width screen
                dismissOnBackPress = isDismissible,
                dismissOnClickOutside = isDismissible
            )
        ) {
            AnimatedVisibility(
                visibleState = transitionState,
                enter = alertDialogHalfVerticalEnterTransition(),
                exit = alertDialogVerticalExitTransition()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    AppTwoBtn(
                        msg = msg,
                        title = title,
                        positiveBtnLbl = positiveBtnLbl,
                        negativeBtnLbl = negativeBtnLbl,
                        iconResId = iconResId,
                        onPositiveBtnClick = {
                            clickedButtonType = DialogButtonType.POSITIVE
                            transitionState.targetState = false
                        },
                        onNegativeBtnClick = {
                            clickedButtonType = DialogButtonType.NEGATIVE
                            transitionState.targetState = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppTwoBtn(
    msg: CharSequence? = null,
    title: CharSequence? = null,
    positiveBtnLbl: String,
    negativeBtnLbl: String? = null,
    @DrawableRes iconResId: Int? = null,
    onPositiveBtnClick: () -> Unit,
    onNegativeBtnClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.Spacing.spacing40)
            .shadow(elevation = Dimen.Elevation.elevation2, shape = fullRoundedCornerShape.small)
            .background(color = Color.Companion.White, shape = fullRoundedCornerShape.small),
    ) {
        Box(
            modifier = Modifier.padding(
                start = Dimen.Spacing.spacing20,
                end = Dimen.Spacing.spacing20,
                top = Dimen.Spacing.spacing32,
                bottom = Dimen.Spacing.spacing20
            )
        ) {
            Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
                iconResId?.let {
                    AppLoadImage(imageResId = iconResId)

                    Spacer(Modifier.height(Dimen.Spacing.spacing20))
                }

                title?.let {
                    AppTxt(
                        txt = it.toString(),
                        modifier = Modifier.fillMaxWidth(),
                        style = AppTypography.titleSmall,
                        txtAlign = TextAlign.Companion.Center
                    )

                    Spacer(Modifier.height(Dimen.Spacing.spacing12))
                }

                msg?.let {
                    AppTxt(
                        txt = it.toString(),
                        modifier = Modifier.fillMaxWidth(),
                        style = AppTypography.bodyMedium.copy(color = gray01100),
                        txtAlign = TextAlign.Companion.Center
                    )

                    Spacer(Modifier.height(Dimen.Spacing.spacing12))
                }

                Spacer(Modifier.height(Dimen.Spacing.spacing20))

                AppBtn(
                    txt = positiveBtnLbl,
                    modifier = Modifier.fillMaxWidth(),
                    txtStyle = AppTypography.labelLarge.copy(color = Color.Companion.White),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = blue01100),
                    shape = fullRoundedCornerShape.small,
                    onClick = onPositiveBtnClick
                )

                negativeBtnLbl?.let {
                    Spacer(Modifier.height(Dimen.Spacing.spacing4))

                    AppBtn(
                        txt = it,
                        modifier = Modifier.fillMaxWidth(),
                        txtStyle = AppTypography.bodySmall.copy(color = blue01100),
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.Companion.White),
                        shape = fullRoundedCornerShape.small,
                        onClick = { onNegativeBtnClick?.invoke() }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAppTwoBtnDialog() {
    AppTwoBtnDialog(
        title = stringResource(id = R.string.logout_popup_title),
        positiveBtnLbl = stringResource(id = R.string.logout),
        negativeBtnLbl = stringResource(id = R.string.general_cancel),
        isPreview = true
    )
}