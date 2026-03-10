package com.github.kimijb

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PluginXmlDependencyTest {

    @Test
    fun `plugin xml declares terminal plugin dependency`() {
        val stream = this::class.java.classLoader.getResourceAsStream("META-INF/plugin.xml")
        requireNotNull(stream) { "META-INF/plugin.xml not found in test resources" }
        val xml = stream.bufferedReader().use { it.readText() }

        assertTrue(
            xml.contains("<depends>org.jetbrains.plugins.terminal</depends>"),
            "plugin.xml must declare org.jetbrains.plugins.terminal dependency"
        )
    }
}
