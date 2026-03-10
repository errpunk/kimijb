package com.github.kimijb.ui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KimiTerminalStartupControllerTest {

    @Test
    fun `requestStart starts immediately when terminal is ready`() {
        var startCount = 0
        var registerCount = 0
        val controller = KimiTerminalStartupController(
            isReady = { true },
            start = { startCount += 1 }
        )

        controller.requestStart {
            registerCount += 1
            {}
        }

        assertEquals(1, startCount)
        assertEquals(0, registerCount)
    }

    @Test
    fun `requestStart waits for retry trigger until terminal becomes ready`() {
        var ready = false
        var startCount = 0
        lateinit var trigger: () -> Unit
        val controller = KimiTerminalStartupController(
            isReady = { ready },
            start = { startCount += 1 }
        )

        controller.requestStart {
            trigger = it
            {}
        }

        assertEquals(0, startCount)

        ready = true
        trigger()

        assertEquals(1, startCount)
    }

    @Test
    fun `controller starts only once after multiple retries`() {
        var ready = false
        var startCount = 0
        lateinit var trigger: () -> Unit
        val controller = KimiTerminalStartupController(
            isReady = { ready },
            start = { startCount += 1 }
        )

        controller.requestStart {
            trigger = it
            {}
        }

        controller.retry()
        assertEquals(0, startCount)

        ready = true
        trigger()
        controller.retry()
        trigger()

        assertEquals(1, startCount)
    }
}
