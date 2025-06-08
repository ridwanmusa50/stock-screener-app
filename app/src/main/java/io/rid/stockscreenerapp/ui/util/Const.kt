package io.rid.stockscreenerapp.ui.util

import io.rid.stockscreenerapp.BuildConfig

object Const {

    object ApiStatusCode {
        const val TOO_MANY_REQUESTS = 429
    }

    object Common {
        const val SEARCH_DEBOUNCE = 300L
    }

    object DemoData {
        const val COMPANY_OVERVIEW_SYMBOL = "QQQ"
        const val TIME_SERIES_MONTHLY_SYMBOL = "IBM"
    }

    object Environment {
        val IS_DEVELOPMENT_DEBUG_BUILD = BuildConfig.BUILD_TYPE.contains("developmentDebug")
    }

}