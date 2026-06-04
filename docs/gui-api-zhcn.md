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
- `UIWidget`
- `UISlider`
- `UITextField`
- `UITextArea`
- `UIColorGroup`
- `UILocalization.ColorLabels`
- `UILocalization.FieldValidationMessages`
- `UILocalization.KeyBindingMessages`

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

## 文本和本地化

所有面向用户的文本都使用 `Component`。如果你的 mod 会被其他语言环境使用，建议传入
`Component.translatable("your_mod.some_key")`，而不是把文案写成
`Component.literal(...)`，这样文本会归属于你自己的 namespace。

EnchantedUI 自身也提供兜底语言文件：
`assets/enchanted_ui/lang/*.json`。这些 key 对整个 `enchanted_ui` namespace
都是全局的；如果其他 mod 或资源包覆写它们，所有使用这些兜底 key 的界面都会受到影响。
因此，当文案属于你的 mod 时，应使用可传入自定义 label 或 translation key 的重载。

当前框架自动生成的内置文案包括：

- `eui.config.rgba.red`
- `eui.config.rgba.green`
- `eui.config.rgba.blue`
- `eui.config.rgba.alpha`
- `eui.config.color_preview`
- `eui.config.keybind.current`
- `eui.config.keybind.none`
- `eui.config.keybind.listening`
- `eui.dropdown.empty`
- `eui.dropdown.add`
- `eui.select.none`
- `eui.validation.duplicate_entry`
- `eui.validation.int.required`
- `eui.validation.int.range`
- `eui.validation.double.required`
- `eui.validation.double.range`
- `eui.display.empty`
- `eui.display.more`
- `eui.dialog.confirm`
- `eui.dialog.unsaved_changes.title`
- `eui.dialog.unsaved_changes.message`
- `eui.dialog.unsaved_changes.discard`
- `eui.dialog.unsaved_changes.cancel`

调用方接管框架生成文本的示例：

```
form.rgbaSlidersWithPreview(
        Component.translatable("my_mod.color.title"),
        new UILocalization.ColorLabels(
                Component.translatable("my_mod.color.red"),
                Component.translatable("my_mod.color.green"),
                Component.translatable("my_mod.color.blue"),
                Component.translatable("my_mod.color.alpha"),
                Component.translatable("my_mod.color.preview")
        ),
        () -> r, value -> r = value,
        () -> g, value -> g = value,
        () -> b, value -> b = value,
        () -> a, value -> a = value,
        false
);
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

普通滑块的 label 由调用方传入。`rgbaSlidersWithPreview(...)` 有一个使用上方
RGBA 内置 key 的兜底重载，也有一个接受 `UILocalization.ColorLabels` 的重载。为其他 mod
构建界面时，建议使用 `UILocalization.ColorLabels` 传入自己 namespace 下的通道文本。

文本输入：

- `textField(...)`
- `intField(...)`
- `doubleField(...)`
- `textArea(...)`

`intField(...)` 和 `doubleField(...)` 会在保存前校验输入，兜底错误提示使用上方列出的
validation key。需要自己的错误文案时，使用接受 `UILocalization.FieldValidationMessages`
的重载传入自己 namespace 下的 key；需要完全自定义校验规则时，使用
`textField(..., UITextValidator)`。

按键绑定：

- `keyBinding(...)`
- `combinationKeyBinding(...)`

按键绑定控件会生成当前按键、未设置、等待输入等状态文本。需要这些文本归属于你的 mod
时，使用接受 `UILocalization.KeyBindingMessages` 的重载传入自己的 namespace key。

列表和选择：

- `dropdownList(...)`
- `editableDropdownList(...)`
- `select(...)`
- `enumSelect(...)`
- `searchableSelect(...)`
- `multiSelect(...)`
- `radioGroup(...)`

下拉和选择类控件也提供了用于生成文本的重载，例如空列表文本、重复条目错误、“未选择”
标签等。当这些默认文案会出现在你的 mod 界面中时，建议使用这些重载传入自己的文案。

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
- `UISlider`
- `UITextField`
- `UITextArea`
- `UIColorGroup`

大多数控件返回 `UIWidget`。滑块、单行文本框和多行文本框因为有额外的取值能力，所以保留专用包装类型。

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
