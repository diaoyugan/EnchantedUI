# Enchanted UI

[English](readme.md) | 简体中文

EnchantedUI 是一个面向 Minecraft 客户端的 GUI 框架，用来构建模块化界面、分页工具界面、设置面板和可复用控件。

> 早期开发版本
>
> 当前 API 仍在快速演进。现阶段应预期会有重构、抽象补充和破坏性变更。

## 当前定位

EnchantedUI 现在已经不只是一个简单的配置界面辅助库，当前代码大致提供：

- 带页面生命周期的分页界面
- 表单式控件构建器
- 内容超出视口时的自动滚动
- 支持 overlay 交互的下拉和列表控件
- 带校验的文本输入
- 单选、枚举选择、可搜索选择、多选、可编辑列表
- 按键绑定控件
- toast 和模态对话框辅助能力
- 独立公共 API 包装层

## 公共 API

建议外部使用的入口位于：

`top.diaoyugan.enchanted_ui.api.client.gui`

主要入口包括：

- `EnchantedUI`
- `UiTabbedScreen`
- `UiFormPage`
- `UiForm`
- `UiPage`
- `UiBottomBar`

旧的内部 builder 入口 `top.diaoyugan.enchanted_ui.client.gui.builder.UI` 仍然存在，但更适合作为内部实现层，而不是外部首选入口。

## 示例

```java
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.EnchantedUI;
import top.diaoyugan.enchanted_ui.api.client.gui.UiBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UiTabbedScreen;

public final class ExampleScreen extends UiTabbedScreen {
    public ExampleScreen(Screen parent) {
        super(parent, Component.literal("Example"));

        tab(10, 30, 20, Component.literal("Main"), EnchantedUI.formPage(220, form -> {
            form.title(Component.literal("General"));
            form.toggle(Component.literal("Enabled"), () -> true, value -> {});
            form.button(Component.literal("Ping"), () -> showToast(Component.literal("Clicked")));
        }));

        bottomBar(UiBottomBar.closeOnly(Component.literal("Close")));
    }
}
```

## 当前控件能力

`UiForm` 目前提供：

- 文本标题类控件
- 普通按钮、按钮行、图标按钮、贴图按钮
- 开关和双列开关
- 整数滑条
- 带校验的单行文本框
- 多行文本框
- 按键绑定控件
- 颜色预览组合控件
- 只读下拉列表
- 可编辑下拉列表
- 单选选择器、枚举选择器、可搜索选择器、多选选择器
- 单选组
- 分组 section 和自定义 widget 挂载

`UiTabbedScreen` 目前提供：

- tab 和底栏
- 通过 `UiPage` / `UiFormSpec` 暴露的页面生命周期
- `showToast(...)`
- `showDialog(...)`
- `showConfirm(...)`

## Demo 覆盖

当前 `DemoScreen` 主要用于验证框架表面能力，覆盖：

- 表单控件
- 校验输入
- 动作按钮
- 选择类控件
- 只读和可编辑列表
- 页面滚动
- toast 和 dialog
- 页面生命周期回调

## 内部测试构建产物

当前仅提供本地联调用构建产物：

- `enchanted_ui-common-<minecraft_version>`
- `enchanted_ui-fabric-<minecraft_version>`

发布方式：

```powershell
./gradlew publishForInternalTesting
```

输出目录：

```text
build/test-maven
```

NeoForge 目前不在活跃测试路径内。

## 推荐使用方式

独立运行依赖：

- 编译依赖 `enchanted_ui-common`
- 运行时把 `enchanted_ui-fabric` 放进 `mods` 目录

内嵌使用：

- 需要把框架实现一起打包进别的 mod 时，内嵌 `enchanted_ui-common`

Fabric 嵌套 mod 打包：

- 对 `enchanted_ui-fabric` 使用 `modImplementation` 加 `include`

具体本地联调方式见 [docs/internal-testing-usage.md](docs/internal-testing-usage.md)。

## 文档

- [docs/gui-api.md](docs/gui-api.md)
- [docs/internal-testing-usage.md](docs/internal-testing-usage.md)

## 说明

- Maven 坐标目前仍是临时状态
- 文档可能落后于最新重构
- 现阶段不保证向后兼容
