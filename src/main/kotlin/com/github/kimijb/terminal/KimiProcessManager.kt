package com.github.kimijb.terminal

import com.pty4j.PtyProcessBuilder
import com.intellij.openapi.diagnostic.Logger
import com.jediterm.terminal.TtyConnector
import java.util.concurrent.TimeUnit

open class KimiProcessManager {

    private val LOG = Logger.getInstance(KimiProcessManager::class.java)

    fun start(
        workDir: String,
        kimiCommand: String = "kimi",
        initialColumns: Int? = null,
        initialRows: Int? = null
    ): KimiProcess {
        return start(workDir, listOf(kimiCommand), initialColumns, initialRows)
    }

    fun start(
        workDir: String,
        command: List<String>,
        initialColumns: Int? = null,
        initialRows: Int? = null
    ): KimiProcess {
        LOG.info("Starting kimi process: cmd='${command.joinToString(" ")}', workDir='$workDir'")
        try {
            val env = createTerminalEnvironment()
            val process = startPtyProcess(command.toTypedArray(), workDir, env, initialColumns, initialRows)
            LOG.info("Kimi process started successfully, pid=${process.pid()}")
            return KimiProcess(process, workDir)
        } catch (e: Exception) {
            LOG.error("Failed to start kimi process: cmd='${command.joinToString(" ")}', workDir='$workDir'", e)
            throw e
        }
    }

    internal open fun startPtyProcess(
        command: Array<String>,
        workDir: String,
        environment: Map<String, String>,
        initialColumns: Int?,
        initialRows: Int?
    ): Process {
        val builder = PtyProcessBuilder(command)
            .setDirectory(workDir)
            .setEnvironment(environment)
            .setRedirectErrorStream(true)
        initialColumns?.let { builder.setInitialColumns(it) }
        initialRows?.let { builder.setInitialRows(it) }
        return builder.start()
    }

    internal open fun baseEnvironment(): Map<String, String> = System.getenv()

    private fun createTerminalEnvironment(): MutableMap<String, String> {
        val env = baseEnvironment().toMutableMap()
        val loginShellPath = detectLoginShellPath()
        val mergedPath = mergePathEntries(
            env["PATH"],
            loginShellPath,
            defaultPathEntries()
        )
        if (mergedPath.isNotEmpty()) {
            env["PATH"] = mergedPath
        }
        env.putIfAbsent("TERM", "xterm-256color")
        env.putIfAbsent("COLORTERM", "truecolor")
        return env
    }

    internal open fun detectLoginShellPath(): String? {
        return try {
            val shell = baseEnvironment()["SHELL"]?.takeIf { it.isNotBlank() } ?: "/bin/zsh"
            val process = ProcessBuilder(shell, "-lc", "printf %s \"${'$'}PATH\"")
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText().trim()
            process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)
            output.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            LOG.warn("Failed to detect login shell PATH", e)
            null
        }
    }

    private fun mergePathEntries(vararg rawPaths: String?): String {
        val ordered = LinkedHashSet<String>()
        rawPaths.forEach { raw ->
            raw?.split(":")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.forEach { ordered.add(it) }
        }
        return ordered.joinToString(":")
    }

    private fun defaultPathEntries(): String {
        return listOf(
            "/opt/homebrew/bin",
            "/opt/homebrew/sbin",
            "/usr/local/bin",
            "/usr/local/sbin",
            "/usr/bin",
            "/bin",
            "/usr/sbin",
            "/sbin"
        ).joinToString(":")
    }

    fun stop(process: KimiProcess) {
        LOG.info("Stopping kimi process, pid=${process.process.pid()}, alive=${process.process.isAlive}")
        process.process.destroy()
    }

    fun stopGracefully(
        process: KimiProcess,
        ttyConnector: TtyConnector?,
        timeoutMillis: Long = 1500
    ) {
        LOG.info("Stopping kimi process gracefully, pid=${process.process.pid()}, alive=${process.process.isAlive}")
        if (!process.process.isAlive) {
            return
        }

        try {
            ttyConnector?.write("\u0004")
            if (process.process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS)) {
                return
            }

            ttyConnector?.write("/exit\n")
            if (process.process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS)) {
                return
            }
        } catch (e: Exception) {
            LOG.warn("Graceful kimi shutdown failed, falling back to destroy()", e)
        }

        stop(process)
    }

    fun isRunning(process: KimiProcess): Boolean {
        return process.process.isAlive
    }

    fun findKimiExecutable(): String? {
        LOG.info("Looking for kimi executable...")

        // First, try common absolute paths (fallback for GUI apps with different PATH)
        val commonPaths = getCommonKimiPaths()
        for (path in commonPaths) {
            LOG.debug("Checking absolute path: $path")
            if (java.io.File(path).exists() && java.io.File(path).canExecute()) {
                LOG.info("Found kimi at absolute path: $path")
                return path
            }
        }

        // Second, try PATH lookup
        return try {
            val isWindows = System.getProperty("os.name").lowercase().contains("win")
            val command = if (isWindows) listOf("where", "kimi") else listOf("which", "kimi")
            LOG.debug("Running command: ${command.joinToString(" ")}")
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readLine()?.trim()
            val exitCode = process.waitFor()
            LOG.debug("which/where exitCode=$exitCode, output=$output")
            if (exitCode == 0 && !output.isNullOrEmpty()) {
                LOG.info("Found kimi executable in PATH: $output")
                output
            } else {
                LOG.warn("Kimi executable not found in PATH (exitCode=$exitCode)")
                null
            }
        } catch (e: Exception) {
            LOG.error("Error finding kimi executable in PATH", e)
            null
        }
    }

    private fun getCommonKimiPaths(): List<String> {
        val home = System.getProperty("user.home")
        val isWindows = System.getProperty("os.name").lowercase().contains("win")

        return if (isWindows) {
            listOf(
                "C:\\Program Files\\kimi\\kimi.exe",
                "$home\\AppData\\Local\\Programs\\kimi\\kimi.exe",
                "$home\\.local\\bin\\kimi.exe"
            )
        } else {
            listOf(
                "$home/.local/bin/kimi",
                "/usr/local/bin/kimi",
                "/opt/homebrew/bin/kimi",
                "/usr/bin/kimi"
            )
        }
    }

    fun getKimiVersion(executablePath: String): String? {
        return try {
            val process = ProcessBuilder(executablePath, "--version")
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readLine()?.trim()
            process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)
            output?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            LOG.warn("Failed to get kimi version from $executablePath", e)
            null
        }
    }
}
