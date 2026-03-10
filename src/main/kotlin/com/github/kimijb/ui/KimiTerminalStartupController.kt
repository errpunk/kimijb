package com.github.kimijb.ui

internal class KimiTerminalStartupController(
    private val isReady: () -> Boolean,
    private val start: () -> Unit
) {

    private var started = false
    private var unregisterTrigger: (() -> Unit)? = null

    fun requestStart(registerTrigger: ((() -> Unit) -> (() -> Unit))) {
        if (tryStart()) {
            return
        }

        if (unregisterTrigger == null) {
            unregisterTrigger = registerTrigger {
                tryStart()
            }
        }
    }

    fun retry() {
        tryStart()
    }

    private fun tryStart(): Boolean {
        if (started || !isReady()) {
            return false
        }

        started = true
        unregisterTrigger?.invoke()
        unregisterTrigger = null
        start()
        return true
    }
}
