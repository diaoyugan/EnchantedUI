# EnchantedUI GUI API

本文档描述 EnchantedUI 当前的公共 API 表面。

## 公共入口

建议使用的包路径：

`top.diaoyugan.enchanted_ui.api.client.gui`

主要类型：

- `EnchantedUI`
- `UiTabbedScreen`
- `UiPage`
- `UiFormPage`
- `UiFormSpec`
- `UiForm`
- `UiBottomBar`

## 基础界面示例

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
            form.button(Component.literal("Run"), () -> showToast(Component.literal("Clicked")));
        }));

        bottomBar(UiBottomBar.closeOnly(Component.literal("Close")));
    }
}
```

## 核心概念

### `UiTabbedScreen`

通用界面容器，当前提供：

- tab 分页
- 底栏辅助能力
- 页面内容自动滚动
- 支持 overlay 的事件分发
- toast 辅助能力
- 模态对话框辅助能力

常用方法：

- `tab(...)`
- `bottomBar(...)`
- `showPage(...)`
- `showToast(...)`
- `showDialog(...)`
- `showConfirm(...)`

### `UiPage`

当你想自己构建 widget 时，可以直接使用更底层的页面接口。

生命周期钩子：

- `build(...)`
- `onOpen()`
- `onClose()`
- `onShow()`
- `onHide()`
- `onPageChanged(...)`
- `onSave()`
- `tick()`
- `keyPressed(...)`

### `UiFormPage` 和 `UiFormSpec`

更高层的表单页面包装。

当你想要自动布局和表单控件能力时，使用 `EnchantedUI.formPage(...)`。

`UiFormSpec` 和 `UiPage` 共享相同的生命周期模型，只是会额外拿到 `UiForm`。

### `UiBuildContext`

界面构建上下文：

- `screenWidth()`
- `screenHeight()`
- `centerX()`
- `vertical(...)`
- `horizontal(...)`

## `UiForm` 当前能力

当前辅助方法包括：

- `title(...)`
- `space(...)`
- `section(...)`
- `widget(...)`

按钮类：

- `button(...)`
- `buttonRow(...)`
- `iconButton(...)`
- `textureButton(...)`

布尔 / 数值类：

- `toggle(...)`
- `toggleRow(...)`
- `intSlider(...)`
- `rgbaSlidersWithPreview(...)`

文本输入：

- `textField(...)`
- `textArea(...)`

按键绑定：

- `keyBinding(...)`
- `combinationKeyBinding(...)`

列表与选择：

- `dropdownList(...)`
- `editableDropdownList(...)`
- `select(...)`
- `enumSelect(...)`
- `searchableSelect(...)`
- `multiSelect(...)`
- `radioGroup(...)`

校验：

- `validate()`
- `UiTextValidator`

## Widget 包装类型

公共 API 返回的是包装类型，而不是直接暴露内部 widget 实现类。

当前包装类型包括：

- `UiWidget`
- `UiButton`
- `UiText`
- `UiToggle`
- `UiSlider`
- `UiTextField`
- `UiDropdownList`
- `UiEditableDropdownList`
- `UiKeyBinding`
- `UiCombinationKeyBinding`
- `UiColorPreview`

`UiWidget` 当前提供的通用能力：

- tooltip 相关方法
- visible / active / focused 状态
- 位置和尺寸更新
- message 更新
- 通过 `vanilla()` 访问底层 vanilla widget

## 对话框和反馈能力

`UiTabbedScreen` 当前支持：

- `showToast(Component)`
- `showToast(Component, int durationTicks)`
- `showDialog(Component title, List<Component> lines, UiDialogAction... actions)`
- `showConfirm(Component title, Component message, Runnable confirmAction)`

## 内部示例

参考实现：

- `common/src/main/java/top/diaoyugan/enchanted_ui/client/gui/screen/DemoScreen.java`
