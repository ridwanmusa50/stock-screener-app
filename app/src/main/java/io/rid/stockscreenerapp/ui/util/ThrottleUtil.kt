package io.rid.stockscreenerapp.ui.util

import android.text.format.DateUtils

internal interface ThrottleEvent {
    fun processEvent(event: () -> Unit)

    companion object
}

internal fun ThrottleEvent.Companion.get(): ThrottleEvent = ThrottleUtil()

private class ThrottleUtil : ThrottleEvent {

    companion object {
        const val THROTTLE_DURATION = 0.8 * DateUtils.SECOND_IN_MILLIS
    }

    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= THROTTLE_DURATION) event.invoke()
        lastEventTimeMs = now
    }

}