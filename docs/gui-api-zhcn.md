# EnchantedUI GUI API

本文档描述 EnchantedUI 当前的公共 API 表面。

## 公共入口

推荐包路径：

`top.diaoyugan.enchanted_ui.api.client.gui`

主要类型：

- `EnchantedUI`
- `UITabbedScreen`
- `UIPage`
- `UIFormPage`
- `UIFormSpec`
- `UIForm`
- `UIBottomBar`

## 基础界面示例

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
            form.button(Component.literal("Run"), () -> showToast(Component.literal("Clicked")));
        }));

        bottomBar(UIBottomBar.closeOnly(Component.literal("Close")));
    }
}
```

## 核心概念

### `UITabbedScreen`

通用界面容器，提供：

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
- `saveAll()`
- `hasUnsavedChanges()`
- `reloadAll()`
- `requestClose()`
- `showToast(...)`
- `showDialog(...)`
- `showConfirm(...)`
- `unsavedChangesPrompt(...)`

### `UIPage`

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

### `UIFormPage` 和 `UIFormSpec`

更高层的表单页面包装。

当你需要自动布局和表单控件能力时，使用 `EnchantedUI.formPage(...)`。

`UIFormSpec` 与 `UIPage` 共享相同的生命周期模型，只是会额外拿到 `UIForm`。

### `UIBuildContext`

界面构建上下文：

- `screenWidth()`
- `screenHeight()`
- `centerX()`
- `vertical(...)`
- `horizontal(...)`

## `UIForm` 控件能力

当前辅助方法包括：

- `title(...)`
- `space(...)`
- `section(...)`
- `widget(...)`

按钮：

- `button(...)`
- `buttonRow(...)`
- `iconButton(...)`
- `textureButton(...)`

展示：

- `progressBar(...)`
- `keyValueRow(...)`
- `statusBadge(...)`
- `emptyState(...)`
- `infoBlock(...)`
- `loadingState(...)`
- `errorState(...)`
- `readonlyList(...)`
- `summaryBlock(...)`

展示文本由 `Component` 提供。包含默认状态文案的辅助方法也提供重载，用于自定义值、空状态和溢出文本等由框架生成的文案。

布尔 / 数值：

- `toggle(...)`
- `toggleRow(...)`
- `intSlider(...)`
- `longSlider(...)`
- `floatSlider(...)`
- `doubleSlider(...)`
- `rgbaSlidersWithPreview(...)`

文本输入：

- `textField(...)`
- `intField(...)`
- `doubleField(...)`
- `textArea(...)`

按键绑定：

- `keyBinding(...)`
- `combinationKeyBinding(...)`

列表和选择：

- `dropdownList(...)`
- `editableDropdownList(...)`
- `select(...)`
- `enumSelect(...)`
- `searchableSelect(...)`
- `multiSelect(...)`
- `radioGroup(...)`

校验：

- `validate()`
- `UITextValidator`

表单状态：

- `save()`
- `hasUnsavedChanges()`
- `reload()`
- `markClean()`

控件状态：

- `visibleIf(...)`
- `activeIf(...)`

## Widget 包装类型

公共 API 返回包装类型，而不是直接暴露内部 widget 实现类。

当前包装类型包括：

- `UIWidget`
- `UIButton`
- `UIText`
- `UIToggle`
- `UISlider`
- `UITextField`
- `UIDropdownList`
- `UIEditableDropdownList`
- `UIKeyBinding`
- `UICombinationKeyBinding`
- `UIColorPreview`

`UIWidget` 提供的通用能力：

- tooltip 相关方法
- visible / active / focused 状态
- 位置和尺寸更新
- message 更新
- 通过 `vanilla()` 访问底层 vanilla widget

## 对话框和反馈

`UITabbedScreen` 当前支持：

- `showToast(Component)`
- `showToast(Component, int durationTicks)`
- `showDialog(Component title, List<Component> lines, UIDialogAction... actions)`
- `showConfirm(Component title, Component message, Runnable confirmAction)`
- `showConfirm(Component title, Component message, Component confirmLabel, Component cancelLabel, Runnable confirmAction)`
- `unsavedChangesPrompt(UIUnsavedChangesPrompt prompt)`

有脏数据的页面默认使用内置的本地化未保存更改提示。需要业务化文案时，可以按屏幕覆盖：

```
unsavedChangesPrompt(UIUnsavedChangesPrompt.of(
        Component.literal("放弃资料编辑？"),
        Component.literal("当前资料有未保存的更改。")
));
```

如果按钮文案也需要自定义，使用完整工厂：

```
unsavedChangesPrompt(UIUnsavedChangesPrompt.of(
        Component.literal("离开编辑器？"),
        List.of(Component.literal("未保存的规则更改将会丢失。")),
        Component.literal("离开"),
        Component.literal("继续编辑")
));
```

## 内部示例

参考实现：

- `common/src/main/java/top/diaoyugan/enchanted_ui/client/gui/screen/DemoScreen.java`
