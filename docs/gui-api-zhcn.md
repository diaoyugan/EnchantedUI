# EnchantedUI GUI API

本文档描述 EnchantedUI 当前的公共 API 表面。

## 公共入口

推荐包路径：

`top.diaoyugan.enchanted_ui.api.client.gui`

主要类型：

- `EnchantedUI`
- `UIConfigScreenPreset`
- `UISidebarConfigScreen`
- `UITopTabbedConfigScreen`
- `UIInfoScreen`
- `UITabLayout`
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

## 页面预设

常规 mod 配置界面可以直接继承 `UISidebarConfigScreen`。预设统一管理侧边栏坐标、
页签间距和默认表单宽度，实现类只需声明页面内容和底栏动作：

```java
public final class ExampleConfigScreen extends UISidebarConfigScreen {
    private boolean enabled = true;

    public ExampleConfigScreen(Screen parent) {
        super(
                parent,
                Component.translatable("example.config.title"),
                Component.translatable("example.name")
        );
    }

    @Override
    protected void buildConfig(Builder config) {
        config.formPage(Component.translatable("example.config.general"), form -> {
            form.toggle(
                    Component.translatable("example.config.enabled"),
                    () -> enabled,
                    value -> enabled = value
            );
        });

        config.bottomBar(UIBottomBar.saveAndClose(
                Component.translatable("example.config.close"),
                Component.translatable("example.config.save"),
                this::saveAll
        ));
    }
}
```

自动布局的表单页使用 `config.formPage(...)`，自定义 `UIPage` 使用
`config.page(...)`。高级重载仍允许调整表单宽度或页签文字样式，但实现类无需接触
界面坐标。小型无状态界面也可以使用 `EnchantedUI.configScreen(...)`，无需声明具名子类。

其他预设：

- `UITopTabbedConfigScreen`：顶部居中显示标题，下一行横向排列 tab，适合不希望侧边栏
  占用横向空间的配置界面。
- `UIInfoScreen`：提供单个带标题、可滚动的表单内容区，适用于关于、帮助、状态、摘要等
  页面；除了展示控件，也可以使用按钮、开关等交互元素。

无状态页面可以使用 `EnchantedUI.topTabbedConfigScreen(...)` 和
`EnchantedUI.infoScreen(...)`。独立集成示例可通过 `/enchantedui demo top` 与
`/enchantedui demo info` 打开。

### 响应式与溢出行为

预设通过 `UITabLayout` 管理页签。当 tab 数量超过可用高度或宽度时，只显示完整落在
页签 viewport 中的按钮，并提供方向按钮和鼠标滚轮移动页签窗口。代码切换到隐藏页签时，
对应 tab 也会自动滚动到可见位置。

页面内容使用独立的 scissor viewport，标题、页签条和底栏保持固定；长内容自动垂直滚动。
表单宽度现在表示最大宽度，在小窗口中会收缩到内容 viewport。展开式控件使用同一个裁切
边界，并参与动态滚动范围计算。

文本型展示控件（`infoBlock`、`emptyState`、`loadingState`、`errorState`）会根据
当前控件宽度自动换行并向下扩充。自定义高度现在作为最小高度；完整正文不再依赖省略文本的
悬浮 overlay。

`section(title, builder)` 默认只负责分组，内部控件与父表单保持相同起点和宽度。
需要明确的层级缩进时，可以使用 `section(title, indent, builder)`。

## 文本和本地化

所有面向用户的文本都使用 `Component`。如果你的 mod 会被其他语言环境使用，建议传入
`Component.translatable("your_mod.some_key")`，而不是把文案写成
`Component.literal(...)`，这样文本会归属于你自己的 namespace。

框架自动生成的内置文案使用 `UILocalization.frameworkKey(...)`。key 前缀由
`UILocalization` 的运行时包名计算，因此 Shadow relocation 会同时隔离默认翻译 key。
所有内置文案都有英文 fallback；Fabric 和 NeoForge 独立外壳还会为原始包前缀提供本地化值。
内嵌方可以为 relocation 后的私有前缀提供翻译，也可以直接传入自己的 label 或 translation key。

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

`toggleRow(...)` 提供了可分别传入左右 tooltip 的重载，不再需要先取得
row 后再通过 `getFirst()` 和 `getLast()` 设置。

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

`combinationKeyBinding(...)` 使用不可变的 `CombinationKeyBinding` 值对象，支持键盘和鼠标输入，并提供
`isDown()`、`displayName()` 与 `serialize()`。序列化结果是稳定的 Minecraft 输入名称列表，例如
`key.keyboard.v`、`key.mouse.4`。如果配置本身使用 `List<String>` 或 `Set<String>`，可以直接调用
`serializedCombinationKeyBinding(...)`。

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
- `disabledTooltip(...)` / `inactiveTooltip(...)`

分页屏幕可调用 `sidebarTitle(Component)` 在 tab 上方显示侧边栏标题。
侧边栏宽度会同时适配 tab 和标题文本，标题按计算后的侧边栏宽度自动居中。

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

- `standalone/src/main/java/top/diaoyugan/enchanted_ui/standalone/gui/screen/DemoScreen.java`
