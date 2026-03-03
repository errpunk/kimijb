# kimijb

`kimijb` 是一个 JetBrains（GoLand）插件，用于在 IDE 内直接使用 `kimi-cli`。

English version: [README.md](./README.md)

## 功能

1. 提供 `Kimi` 工具窗口，内置可交互终端运行 `kimi`。
2. 提供 `Option+K`（macOS）快捷键，将当前文件路径和光标行号注入到 `kimi` 输入上下文。

## 目标

- 在 IDE 中获得与系统终端一致的 `kimi` 交互体验（输入、回车、退格）。
- 减少手动复制粘贴路径和行号的成本。

## 环境要求

- GoLand / JetBrains 2024.3+（IntelliJ Platform `sinceBuild=243`）
- 本机已安装 `kimi-cli`
- 建议先验证：`kimi --version`

## 构建与安装

### 1. 构建插件

```bash
./gradlew buildPlugin
```

产物路径：
`build/distributions/kimijb-<version>.zip`

### 2. 在 GoLand 安装

1. 打开 `Settings/Preferences` -> `Plugins`
2. 点击齿轮图标 -> `Install Plugin from Disk...`
3. 选择 `build/distributions` 下的 zip 文件并重启 IDE

## 使用方法

1. 打开右侧 `Kimi` 工具窗口。
2. 在内置终端中直接与 `kimi` 交互。
3. 在编辑器中按 `Option+K` 注入 `filePath:lineNumber` 上下文。

## 开发

### 常用命令

```bash
./gradlew test
./gradlew runIde
./gradlew buildPlugin
```

### 项目结构

- `src/main/kotlin/com/github/kimijb/ui/`：Tool Window 与终端 UI
- `src/main/kotlin/com/github/kimijb/terminal/`：`kimi` 进程与环境处理
- `src/main/kotlin/com/github/kimijb/action/`：快捷键动作
- `src/main/kotlin/com/github/kimijb/service/`：项目级服务
- `src/main/resources/META-INF/plugin.xml`：插件声明与快捷键注册
- `src/test/kotlin/...`：单元测试

## 注意事项

- 从图形界面启动 IDE 时，环境变量可能与 shell 不一致。插件会合并 login shell 的 `PATH`，以降低因 `npx` 等命令缺失导致的 MCP 启动失败风险。
- 若 `kimi` 未安装或不可执行，面板会显示明确错误信息。

## 参考

- JetBrains 插件开发文档：
  [https://plugins.jetbrains.com/docs/intellij/developing-plugins.html](https://plugins.jetbrains.com/docs/intellij/developing-plugins.html)
- kimi-cli 仓库：
  [https://github.com/MoonshotAI/kimi-cli](https://github.com/MoonshotAI/kimi-cli)

## 贡献

请阅读 [CONTRIBUTING.md](./CONTRIBUTING.md)。

## 许可证

本项目使用 MIT License，详见 [LICENSE](./LICENSE)。
