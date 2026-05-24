# Enchanted UI

[English](readme.md) | 简体中文

EnchantedUI 是一个面向 Minecraft 客户端的 GUI 框架，用来构建模块化界面、分页工具、设置面板和可复用控件。

> 早期开发版
>
> API 仍在快速演进。框架表面还在成形阶段，请预期会有重构、抽象补充和破坏性变更。

## 当前定位

EnchantedUI 已经不只是一个小型配置界面辅助库。当前代码库提供：

- 带页面生命周期钩子的分页界面
- 面向常用控件的表单式构建器
- 内容超出视口时的页面自动滚动
- 感知 overlay 的下拉和列表控件
- 带校验的文本输入
- 单选、枚举选择、可搜索选择、多选和可编辑列表控件
- 按键绑定控件
- toast 和模态对话框辅助能力
- 公共 API 包中的可复用 widget 包装类型
- 用于进度、状态、键值数据、只读列表、摘要、加载、错误和空状态的基础展示块

## 公共 API

推荐的公共入口位于：

`top.diaoyugan.enchanted_ui.api.client.gui`

主要入口辅助类型：

- `EnchantedUI`
- `UITabbedScreen`
- `UIFormPage`
- `UIForm`
- `UIPage`
- `UIBottomBar`

旧的内部 builder 入口 `top.diaoyugan.enchanted_ui.client.gui.builder.UI` 仍然存在，但它应该被视为面向实现层的接口，而不是推荐给外部使用的首选入口。

## 示例

```java
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.EnchantedUI;
import top.diaoyugan.enchanted_ui.api.client.gui.UIBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UITabbedScreen;

public final class ExampleScreen extends UITabbedScreen {
    public ExampleScreen(Screen parent) {
        super(parent, Component.literal("Example"));

        tab(10, 30, 20, Component.literal("Main"), EnchantedUI.formPage(220, form -> {
            form.title(Component.literal("General"));
            form.toggle(Component.literal("Enabled"), () -> true, value -> {});
            form.button(Component.literal("Ping"), () -> showToast(Component.literal("Clicked")));
        }));

        bottomBar(UIBottomBar.closeOnly(Component.literal("Close")));
    }
}
```

## 当前控件表面

`UIForm` 当前提供：

- 文本 / 标题控件
- 按钮、按钮行、图标按钮、贴图按钮
- 进度条、键值行、状态徽标、信息块、加载状态、错误状态、只读列表、摘要块和空状态块
- 开关和开关行
- integer、long、float、double 滑块
- 带校验的文本框
- integer 和 double 数字输入框
- 多行文本区域
- 按键绑定控件
- 颜色预览组
- 只读下拉列表
- 可编辑下拉列表
- select、enum-select、searchable select、multi-select
- 单选组
- section 嵌套和自定义 widget 挂载
- 表单脏状态辅助能力（`hasUnsavedChanges()`、`save()`、`reload()`、`markClean()`）
- widget 状态条件（`visibleIf(...)`、`activeIf(...)`）

展示文本由 `Component` 提供，默认展示文案可以通过相关重载覆盖。

在界面层，`UITabbedScreen` 当前提供：

- tabs 和底栏
- 通过 `UIPage` / `UIFormSpec` 暴露的页面生命周期钩子
- `showToast(...)`
- `showDialog(...)`
- `showConfirm(...)`
- `unsavedChangesPrompt(...)`

## Demo 覆盖范围

`DemoScreen` 是当前框架的集成表面检查。它覆盖：

- 表单控件
- 带校验的输入
- 按钮和动作
- 选择控件
- 展示控件
- 可编辑和只读列表
- 滚动
- toast 和 dialog 辅助能力
- 页面生命周期回调

## 内部测试产物

用于本地集成测试时，项目当前会发布：

- `enchanted_ui-common-<minecraft_version>`
- `enchanted_ui-fabric-<minecraft_version>`

使用以下命令发布：

```powershell
./gradlew publishForInternalTesting
```

输出目录：

```text
build/test-maven
```

NeoForge 侧目前有意不放入活跃测试路径。

## 推荐使用模式

独立运行时依赖：

- 编译时依赖 `enchanted_ui-common`
- 将 `enchanted_ui-fabric` 放入游戏的 `mods` 文件夹

嵌入式使用：

- 当你希望把框架实现随另一个 mod 一起发布时，将 `enchanted_ui-common` 嵌入该 mod

Fabric nested-mod 打包：

- 对 `enchanted_ui-fabric` 同时使用 `modImplementation` 和 `include`

具体本地测试流程见 [docs/internal-testing-usage-zhcn.md](docs/internal-testing-usage-zhcn.md)。

## 文档

- [docs/gui-api-zhcn.md](docs/gui-api-zhcn.md)
- [docs/internal-testing-usage-zhcn.md](docs/internal-testing-usage-zhcn.md)

## 备注

- Maven 坐标仍是临时的
- 文档可能会落后于最新重构
- 当前阶段不保证向后兼容
