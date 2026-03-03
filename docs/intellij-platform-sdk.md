# IntelliJ Platform Plugin SDK 文档

> **文档来源**: [JetBrains IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)  
> **最后更新**: 2025年3月  
> **目标平台版本**: IntelliJ Platform 2025.3.3

---

## 目录

1. [概述](#概述)
2. [快速开始](#快速开始)
3. [插件开发基础](#插件开发基础)
4. [插件结构](#插件结构)
5. [基础平台](#基础平台)
6. [项目模型](#项目模型)
7. [程序结构接口 (PSI)](#程序结构接口-psi)
8. [自定义语言支持](#自定义语言支持)
9. [测试](#测试)
10. [API 与兼容性](#api-与兼容性)
11. [主题开发](#主题开发)
12. [资源与工具](#资源与工具)

---

## 概述

### 什么是 IntelliJ Platform

IntelliJ Platform 本身不是一个产品，而是用于构建 IDE 的平台。它驱动着 JetBrains 的产品（如 IntelliJ IDEA），也是开源的，可被第三方用于构建 IDE（如 Google 的 Android Studio）。

**核心特性**:
- **组件驱动**的跨平台 JVM 应用程序宿主
- **高级 UI 工具包**，用于创建工具窗口、树视图、列表（支持快速搜索）、弹出菜单和对话框
- **全功能文本编辑器**，支持语法高亮、代码折叠、代码补全等
- **程序结构接口 (PSI)**，用于解析文件、构建丰富的语法和语义代码模型
- **开放 API**，用于构建标准 IDE 功能（项目模型、构建系统、调试体验）

**基于 IntelliJ Platform 的 IDE**:
- IntelliJ IDEA
- Android Studio
- PyCharm
- WebStorm
- PhpStorm
- Rider
- CLion
- GoLand
- RubyMine

---

## 快速开始

### 开发环境要求

- **IntelliJ IDEA**（推荐使用最新版本）
- **Gradle** 构建工具
- **JDK** 17 或更高版本

### 创建插件项目

#### 方式一：使用 Gradle 插件（推荐）

1. **安装必要插件**:
   - Gradle
   - Plugin DevKit（从 JetBrains Marketplace 安装）

2. **创建项目**:
   - 使用 IntelliJ IDEA 的 "New Project" 向导
   - 选择 "IntelliJ Platform Plugin" 模板
   - 配置 Gradle 和插件设置

3. **Gradle 插件版本选择**:

| 平台版本 | 推荐 Gradle 插件 |
|---------|----------------|
| 2024.2+ | IntelliJ Platform Gradle Plugin (2.x) |
| 2022.3+ | IntelliJ Platform Gradle Plugin (2.x) 或 Gradle IntelliJ Plugin (1.x) |

#### 方式二：使用 DevKit（已弃用）

DevKit 工作流已弃用，不推荐用于新项目。

### 插件开发工作流

```
1. 创建项目 → 2. 实现功能 → 3. 测试 → 4. 打包 → 5. 发布
```

---

## 插件开发基础

### 插件类型

| 类型 | 描述 | 示例 |
|-----|------|------|
| **简单插件** | 添加菜单项、工具窗口等基础功能 | 代码统计工具 |
| **语言插件** | 添加新语言支持 | Kotlin 插件 |
| **框架插件** | 添加框架支持 | Spring 插件 |
| **工具集成插件** | 集成外部工具 | Docker 插件 |
| **主题插件** | 自定义 IDE 外观 | Material Theme |

### 插件替代方案

在某些情况下，实现实际的 IntelliJ Platform 插件可能不是必需的：

- **File Watchers**: 在文件保存时执行外部工具
- **External Tools**: 集成命令行工具
- **Live Templates**: 代码片段
- **Code Inspections**: 使用现有框架

### 插件用户体验 (UX)

开发插件前，确保理解以下要求：

- **性能**: 避免阻塞 UI 线程
- **内存**: 避免内存泄漏
- **兼容性**: 支持多个 IDE 版本
- **本地化**: 支持多语言

---

## 插件结构

### 插件配置文件 (plugin.xml)

`plugin.xml` 是插件的核心配置文件，位于 `META-INF` 目录下。

```xml
<idea-plugin>
    <!-- 插件唯一标识符 -->
    <id>com.example.myplugin</id>
    
    <!-- 插件名称 -->
    <name>My Plugin</name>
    
    <!-- 版本 -->
    <version>1.0.0</version>
    
    <!-- 供应商信息 -->
    <vendor email="support@example.com" url="https://example.com">
        Example Company
    </vendor>
    
    <!-- 描述 -->
    <description><![CDATA[
        Plugin description here...
    ]]></description>
    
    <!-- 更新日志 -->
    <change-notes><![CDATA[
        Version 1.0.0: Initial release
    ]]></change-notes>
    
    <!-- 兼容的 IDE 版本 -->
    <idea-version since-build="233" until-build="243.*"/>
    
    <!-- 依赖 -->
    <depends>com.intellij.modules.platform</depends>
    
    <!-- 扩展 -->
    <extensions defaultExtensionNs="com.intellij">
        <!-- 扩展实现 -->
    </extensions>
    
    <!-- 动作 -->
    <actions>
        <!-- 动作定义 -->
    </actions>
</idea-plugin>
```

### 核心组件

#### 1. Actions（动作）

Actions 允许插件向 IDE 菜单和工具栏添加项目。

**实现**:
```kotlin
class MyAction : AnAction("My Action") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        // 执行动作逻辑
    }
    
    override fun update(e: AnActionEvent) {
        // 控制动作的可用性和可见性
        e.presentation.isEnabledAndVisible = true
    }
}
```

**注册**:
```xml
<actions>
    <action id="MyPlugin.MyAction" 
            class="com.example.MyAction" 
            text="My Action"
            description="Description of my action">
        <add-to-group group-id="ToolsMenu" anchor="last"/>
        <keyboard-shortcut keymap="$default" first-keystroke="ctrl alt M"/>
    </action>
</actions>
```

**重要规则**:
- `AnAction` 子类**不能有任何字段**（会导致内存泄漏）
- 对于在索引期间可用的动作，继承 `DumbAwareAction` 而不是 `AnAction`

#### 2. Services（服务）

服务是按需加载的插件组件，用于封装逻辑或提供可重用功能。

**类型**:
- **Application-level**: 全局单例
- **Project-level**: 每个项目一个实例
- **Module-level**: 每个模块一个实例（不推荐，内存开销大）

**实现**:
```kotlin
@Service(Service.Level.PROJECT)
class MyProjectService(private val project: Project) {
    fun doSomething() {
        // 服务逻辑
    }
}

// 使用
val service = project.getService(MyProjectService::class.java)
```

**Light Services**（轻量级服务）:
- 使用 `@Service` 注解直接注册
- 无需在 `plugin.xml` 中声明

#### 3. Extensions（扩展）

扩展是实现特定接口或类的组件，用于扩展 IDE 功能。

**常见扩展点**:

| 扩展点 | 用途 |
|-------|------|
| `com.intellij.toolWindow` | 添加工具窗口 |
| `com.intellij.applicationConfigurable` | 添加设置页面 |
| `com.intellij.projectConfigurable` | 添加项目设置页面 |
| `com.intellij.completion.contributor` | 代码补全 |
| `com.intellij.inspectionTool` | 代码检查 |
| `com.intellij.intentionAction` | 意图动作 |

**声明扩展**:
```xml
<extensions defaultExtensionNs="com.intellij">
    <toolWindow id="My Tool Window" 
                icon="/icons/toolWindowIcon.svg"
                anchor="right"
                factoryClass="com.example.MyToolWindowFactory"/>
</extensions>
```

#### 4. Listeners（监听器）

用于订阅 IDE 和其他插件发出的事件。

```xml
<applicationListeners>
    <listener class="com.example.MyListener"
              topic="com.intellij.openapi.project.ProjectManagerListener"/>
</applicationListeners>
```

#### 5. Extension Points（扩展点）

允许其他插件扩展你的插件功能。

```xml
<extensionPoints>
    <extensionPoint name="myExtension"
                    interface="com.example.MyExtensionInterface"/>
</extensionPoints>
```

### 插件依赖

```xml
<!-- 必需依赖 -->
<depends>com.intellij.modules.platform</depends>

<!-- 可选依赖 -->
<depends optional="true" config-file="optional-features.xml">
    com.intellij.modules.java
</depends>

<!-- 不兼容插件 -->
<incompatible-with>com.example.incompatible</incompatible-with>
```

### 动态插件

支持无需重启 IDE 即可安装、更新和卸载的插件。

**要求**:
- 使用 `com.intellij.serviceContainer.PreloadActivity` 替代 `ApplicationComponent`
- 正确实现 `Disposable` 接口
- 避免使用静态状态

---

## 基础平台

### 核心概念

#### 1. Virtual File System（虚拟文件系统）

提供统一接口访问文件系统，支持本地文件、JAR 文件、HTTP 文件等。

**关键类**:
- `VirtualFile`: 虚拟文件抽象
- `VirtualFileManager`: 管理虚拟文件

**使用**:
```kotlin
val virtualFile = VirtualFileManager.getInstance()
    .findFileByUrl("file:///path/to/file")
```

#### 2. Documents（文档）

表示编辑器中加载的文本内容，支持事务性编辑。

**关键类**:
- `Document`: 文档接口
- `DocumentManager`: 文档管理

**使用**:
```kotlin
val document = FileDocumentManager.getInstance()
    .getDocument(virtualFile)
```

#### 3. Editors（编辑器）

提供对编辑器功能和状态的访问。

**关键类**:
- `Editor`: 编辑器接口
- `EditorFactory`: 创建编辑器

**使用**:
```kotlin
val editor = FileEditorManager.getInstance(project)
    .openTextEditor(OpenFileDescriptor(project, virtualFile), true)
```

### 用户界面组件

#### 1. Tool Windows（工具窗口）

**声明式设置**（推荐）:
```xml
<extensions defaultExtensionNs="com.intellij">
    <toolWindow id="My Tool Window"
                icon="/icons/toolWindowIcon.svg"
                anchor="right"
                factoryClass="com.example.MyToolWindowFactory"/>
</extensions>
```

```kotlin
class MyToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance()
            .createContent(MyPanel(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}
```

**程序化设置**:
```kotlin
val toolWindow = ToolWindowManager.getInstance(project)
    .registerToolWindow("My Tool Window")
```

#### 2. Dialogs（对话框）

```kotlin
Messages.showMessageDialog(
    project,
    "Message",
    "Title",
    Messages.getInformationIcon()
)
```

#### 3. Notifications（通知）

```kotlin
NotificationGroupManager.getInstance()
    .getNotificationGroup("My Group")
    .createNotification("Title", "Content", NotificationType.INFORMATION)
    .notify(project)
```

#### 4. Popups（弹出窗口）

```kotlin
JBPopupFactory.getInstance()
    .createComponentPopupBuilder(component, preferredFocusComponent)
    .createPopup()
    .showInFocusCenter()
```

### 持久化

#### 1. Settings（设置）

**应用级别设置**:
```kotlin
@State(
    name = "MySettings",
    storages = [Storage("myPluginSettings.xml")]
)
class MySettings : PersistentStateComponent<MySettings.State> {
    data class State(var value: String = "")
    
    private var state = State()
    
    override fun getState() = state
    override fun loadState(state: State) { this.state = state }
}
```

**项目级别设置**:
```kotlin
@State(
    name = "MyProjectSettings",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class MyProjectSettings : PersistentStateComponent<MyProjectSettings.State> {
    // 类似应用级别设置
}
```

### 线程模型

**重要规则**:
- **读写操作**: 在读取或写入操作时，不允许执行长时间运行的操作
- **后台任务**: 使用 `ProgressManager` 或 `Task.Backgroundable`
- **EDT**: UI 更新必须在事件分发线程 (EDT) 上执行

**后台任务**:
```kotlin
ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Title") {
    override fun run(indicator: ProgressIndicator) {
        // 后台任务逻辑
    }
    
    override fun onSuccess() {
        // 成功回调（在 EDT 上）
    }
})
```

---

## 项目模型

### 核心概念

#### 1. Project（项目）

封装项目的所有源代码、库和构建指令。

**关键类**:
- `Project`: 项目接口
- `ProjectManager`: 项目管理

**使用**:
```kotlin
val project = ProjectManager.getInstance().openProjects.first()
```

#### 2. Module（模块）

功能的离散单元，可以独立运行、测试和调试。

**关键类**:
- `Module`: 模块接口
- `ModuleManager`: 模块管理

**使用**:
```kotlin
val modules = ModuleManager.getInstance(project).modules
```

#### 3. SDK

软件开发工具包，用于编译和运行代码。

**关键类**:
- `Sdk`: SDK 接口
- `ProjectRootManager`: 项目根管理

#### 4. Library（库）

项目依赖的库文件集合。

**关键类**:
- `Library`: 库接口
- `LibraryTable`: 库表

#### 5. Facet（方面）

模块的额外配置，如 Web、Spring 等。

**关键类**:
- `Facet`: 方面接口
- `FacetManager`: 方面管理

### Workspace Model（工作区模型）

2024.2+ 引入的新 API，用于替代 Project Model API。

**特点**:
- 更好的性能
- 更清晰的 API
- 支持大型项目

---

## 程序结构接口 (PSI)

### 什么是 PSI

PSI（Program Structure Interface）是 IntelliJ Platform 中负责解析文件和创建语法、语义代码模型的层。

### PSI 文件

PSI 文件是表示文件内容的层次结构的根。

**关键类**:
- `PsiFile`: PSI 文件基类
- `PsiJavaFile`: Java 文件
- `XmlFile`: XML 文件

**获取 PSI 文件**:
```kotlin
// 从 VirtualFile
val psiFile = PsiManager.getInstance(project).findFile(virtualFile)

// 从 Document
val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)

// 从 Action
val psiFile = event.getData(CommonDataKeys.PSI_FILE)

// 从 PSI Element
val psiFile = psiElement.containingFile
```

### PSI 元素

PSI 元素是 PSI 树中的节点。

**关键类**:
- `PsiElement`: PSI 元素基类

**导航**:
```kotlin
// 遍历子元素
psiFile.accept(object : PsiRecursiveElementWalkingVisitor() {
    override fun visitElement(element: PsiElement) {
        super.visitElement(element)
        // 处理元素
    }
})

// 获取父元素
val parent = psiElement.parent

// 获取子元素
val children = psiElement.children
```

### 修改 PSI

**规则**:
- 所有 PSI 修改必须在写入操作中进行
- 使用 `WriteCommandAction` 进行批量修改

```kotlin
WriteCommandAction.runWriteCommandAction(project) {
    // 修改 PSI
    psiElement.delete()
    psiElement.replace(newElement)
}
```

### PSI 引用

用于实现导航（转到定义）和重命名重构。

**实现**:
```kotlin
class MyReference(element: PsiElement) : PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        // 返回引用的目标元素
        return targetElement
    }
    
    override fun getVariants(): Array<Any> {
        // 返回补全变体
        return arrayOf()
    }
}
```

### PSI 性能

**最佳实践**:
- 避免频繁遍历 PSI 树
- 使用缓存（如 `CachedValuesManager`）
- 使用索引（如 `FilenameIndex`）

---

## 自定义语言支持

### 开发步骤

1. **注册文件类型**
2. **实现词法分析器 (Lexer)**
3. **实现解析器和 PSI**
4. **语法和错误高亮**
5. **引用和解析**
6. **代码补全**
7. **查找用法**
8. **重命名重构**
9. **代码格式化**
10. **代码检查和意图**

### 注册文件类型

```kotlin
class MyFileType : LanguageFileType(MyLanguage.INSTANCE) {
    override fun getName() = "My Language"
    override fun getDescription() = "My language files"
    override fun getDefaultExtension() = "my"
    override fun getIcon() = MyIcons.FILE
}

// 注册
class MyFileTypeFactory : FileTypeFactory() {
    override fun createFileTypes(consumer: FileTypeConsumer) {
        consumer.consume(MyFileType.INSTANCE, "my")
    }
}
```

### 实现词法分析器

```kotlin
class MyLexer : LexerBase() {
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        // 初始化词法分析
    }
    
    override fun getTokenType(): IElementType? {
        // 返回当前 token 类型
    }
    
    override fun advance() {
        // 移动到下一个 token
    }
    
    // 其他必要方法...
}
```

### 代码补全

**引用补全**:
```kotlin
class MyReference(element: PsiElement) : PsiReferenceBase<PsiElement>(element) {
    override fun getVariants(): Array<Any> {
        // 返回补全变体
        return collectVariants().toTypedArray()
    }
}
```

**贡献者补全**:
```kotlin
class MyCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            MyCompletionProvider()
        )
    }
}

class MyCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addElement(LookupElementBuilder.create("keyword"))
    }
}
```

### 代码检查

```kotlin
class MyInspection : LocalInspectionTool() {
    override fun checkElement(
        element: PsiElement,
        manager: InspectionManager,
        isOnTheFly: Boolean
    ): Array<ProblemDescriptor>? {
        // 检查逻辑
        val problems = mutableListOf<ProblemDescriptor>()
        // ...
        return problems.toTypedArray()
    }
}
```

---

## 测试

### 测试类型

#### 1. 模型级功能测试（推荐）

- 在无头环境中运行
- 使用真实生产实现
- 测试整个功能而非单个函数

#### 2. 轻量级测试 (Light Tests)

- 使用内存中的项目模型
- 启动速度快
- 适合大多数测试场景

#### 3. 重量级测试 (Heavy Tests)

- 使用真实文件系统
- 启动速度慢
- 适合需要真实文件系统的测试

### 测试基类

```kotlin
class MyTest : BasePlatformTestCase() {
    override fun getTestDataPath() = "testdata/path"
    
    fun testMyFeature() {
        myFixture.configureByFile("input.txt")
        myFixture.performEditorAction("MyAction")
        myFixture.checkResultByFile("expected.txt")
    }
}
```

### 测试数据

测试数据文件放在 `testdata` 目录下。

**标记语法**:
```kotlin
// <caret> 表示光标位置
// <selection>选中的文本</selection>
// <error>错误文本</error>
```

### 测试高亮

```kotlin
fun testHighlighting() {
    myFixture.configureByFile("input.java")
    myFixture.checkHighlighting()
}
```

---

## API 与兼容性

### 构建号范围

用于指定插件兼容的 IDE 版本。

**格式**:
- `since-build`: 最低兼容版本
- `until-build`: 最高兼容版本（使用 `.*` 表示该分支的所有版本）

**示例**:
```xml
<idea-version since-build="233" until-build="243.*"/>
```

**常见版本号**:

| 版本 | 构建号 |
|-----|--------|
| 2024.3 | 243 |
| 2024.2 | 242 |
| 2024.1 | 241 |
| 2023.3 | 233 |
| 2023.2 | 232 |

### 验证插件兼容性

使用 **Plugin Verifier** 检查插件与指定版本范围的兼容性。

**Gradle 配置**:
```kotlin
tasks.runPluginVerifier {
    ideVersions.set(listOf("IU-2024.3", "IC-2024.2"))
}
```

### 不兼容的 API 变更

JetBrains 会记录每个版本的不兼容变更：

- [2025.* 变更](https://plugins.jetbrains.com/docs/intellij/api-notable-2025.html)
- [2024.* 变更](https://plugins.jetbrains.com/docs/intellij/api-notable-2024.html)
- [2023.* 变更](https://plugins.jetbrains.com/docs/intellij/api-notable-2023.html)

### 内部 API 迁移

避免使用内部 API（包名包含 `internal`）。如果必须使用，关注迁移指南。

---

## 主题开发

### 主题结构

主题插件应独立，不与其他插件功能混合。

**必需文件**:
1. 主题描述文件（JSON）- `resources/theme_name.json`
2. `plugin.xml` 中的 `themeProvider` 声明

**可选文件**:
- 编辑器配色方案（XML）
- 背景图片
- 图标文件

### 主题描述文件

```json
{
  "name": "My Theme",
  "author": "Author Name",
  "dark": true,
  "editorScheme": "/my_theme.xml",
  "ui": {
    "Panel.background": "#2B2B2B",
    "Panel.foreground": "#BBBBBB"
  }
}
```

### 注册主题

```xml
<extensions defaultExtensionNs="com.intellij">
    <themeProvider id="my_theme" path="/theme_name.json"/>
</extensions>
```

---

## 资源与工具

### 探索 API

#### 1. IntelliJ Platform Explorer

[https://jb.gg/ipe](https://jb.gg/ipe) - 浏览开源插件中的 API 使用示例。

#### 2. 扩展点列表

- [IntelliJ Platform 扩展点](https://plugins.jetbrains.com/docs/intellij/extension-point-list.html)
- [IntelliJ Platform 插件扩展点](https://plugins.jetbrains.com/docs/intellij/extension-point-list.html)

#### 3. 代码洞察

在 `plugin.xml` 的 `<extensions>` 块中使用自动补全查看所有可用扩展点。

### 有用链接

| 资源 | 链接 |
|-----|------|
| 官方文档 | https://plugins.jetbrains.com/docs/intellij |
| GitHub 仓库 | https://github.com/JetBrains/intellij-sdk-docs |
| 代码示例 | https://github.com/JetBrains/intellij-sdk-code-samples |
| 社区论坛 | https://platform.jetbrains.com |
| JetBrains Marketplace | https://plugins.jetbrains.com |

### 学习资源

- [Busy Plugin Developers 系列](https://www.youtube.com/playlist?list=PLQ176FUIyIUZRWGCFY7G9V5zaM00THymY)
- [IntelliJ Platform Blog](https://blog.jetbrains.com/platform/)

### 发布插件

#### 1. 插件签名

发布前必须对插件进行签名。

**Gradle 配置**:
```kotlin
signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
}
```

#### 2. 发布到 JetBrains Marketplace

**手动上传**:
1. 登录 [JetBrains Account](https://account.jetbrains.com)
2. 进入 [Marketplace](https://plugins.jetbrains.com)
3. 上传插件文件

**Gradle 自动发布**:
```kotlin
publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
    channels.set(listOf("stable"))
}
```

#### 3. 发布前检查清单

- [ ] 遵循 Plugin UX 指南
- [ ] 遵循 Marketplace 最佳实践
- [ ] 插件已通过 Plugin Verifier 检查
- [ ] 插件已签名
- [ ] 提供清晰的描述和截图

---

## 最佳实践

### 性能

1. **避免阻塞 UI 线程**: 使用后台任务
2. **使用缓存**: 使用 `CachedValuesManager` 缓存 PSI 计算结果
3. **避免内存泄漏**: 正确实现 `Disposable`
4. **延迟加载**: 使用服务按需加载

### 兼容性

1. **指定版本范围**: 使用 `since-build` 和 `until-build`
2. **使用 Plugin Verifier**: 验证兼容性
3. **避免内部 API**: 使用公共 API
4. **测试多版本**: 在多个 IDE 版本上测试

### 用户体验

1. **提供清晰的文档**: 描述插件功能和用法
2. **支持本地化**: 使用资源包
3. **处理错误**: 提供有意义的错误消息
4. **遵循 UI 指南**: 保持与 IDE 一致的界面风格

---

## 常见问题

### 如何获取当前项目？

```kotlin
// 从 Action
val project = event.getData(CommonDataKeys.PROJECT)

// 从服务
val project = ProjectManager.getInstance().openProjects.first()
```

### 如何获取当前编辑器？

```kotlin
val editor = FileEditorManager.getInstance(project).selectedTextEditor
```

### 如何获取当前文件？

```kotlin
// VirtualFile
val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE)

// PSI File
val psiFile = event.getData(CommonDataKeys.PSI_FILE)
```

### 如何显示通知？

```kotlin
NotificationGroupManager.getInstance()
    .getNotificationGroup("My Group")
    .createNotification("Title", "Content", NotificationType.INFORMATION)
    .notify(project)
```

### 如何执行后台任务？

```kotlin
ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Title") {
    override fun run(indicator: ProgressIndicator) {
        // 后台任务
    }
})
```

---

## 参考

- [IntelliJ Platform SDK 官方文档](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [IntelliJ Platform GitHub](https://github.com/JetBrains/intellij-community)
- [SDK 代码示例](https://github.com/JetBrains/intellij-sdk-code-samples)
- [JetBrains Marketplace](https://plugins.jetbrains.com)

---

*本文档由 AI 生成，基于 JetBrains IntelliJ Platform Plugin SDK 官方文档。*
