package io.rid.stockscreenerapp.ui.screen.dashboard

sealed class StarredAction {
    object STARRED : StarredAction()
    object UNSTARRED : StarredAction()
}