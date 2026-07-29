# EnchantedUI 架构

## 分层

代码按职责分为四层：

1. `api.client.gui` 是调用方入口，包含页面预设、`UIForm`、`UIPage` 和控件包装类型。
2. `client.gui.screen.base` 接管 Minecraft `Screen` 生命周期、页签、viewport、滚动、弹窗和关闭流程。
3. `client.gui.builder` 是表单实现层，统一维护纵向布局游标和脏状态；输入与展示控件分别由
   `FormInputFactory`、`FormDisplayFactory` 创建。
4. `client.gui.widget` 包含实际 widget，按 button、display、input、list、option、overlay、scroll 分类。

`standalone` 只放集成 Demo、命令和平台无关的演示状态，不属于可嵌入的公共库。

## 页面预设

`UIConfigScreenPreset` 负责配置页声明生命周期和共享 Builder。具体视觉布局只需要继承它并在
构造函数中设置 tab layout 与 content viewport：

- `UISidebarConfigScreen`：纵向侧边栏。
- `UITopTabbedConfigScreen`：标题下方的横向页签。
- `UIInfoScreen`：无页签的单页信息/交互页面。

不要在新预设中复制 Builder。新预设应复用 `UIConfigScreenPreset`，只提供布局参数。

## 运行时不变量

- 表单控件按最终 `getHeight()` 推进布局游标；自动换行控件必须在加入表单时确定高度。
- `PageView` 保存未滚动的基础坐标，每次刷新都从基础坐标重新计算，不能累计修改坐标。
- 页面 widget 和展开式 overlay 使用同一个 content viewport 与 scissor 边界。
- 自动 tab layout 只显示完整按钮；隐藏页签通过方向按钮、滚轮或程序切页重新进入可见窗口。
- `section(title, builder)` 不改变对齐；只有显式 indent 重载可以缩进。

## 保持边界清晰

`BaseTabbedScreen` 体积较大，但其中的 PageView、ModalDialog 和 Toast 都直接依赖同一个
Minecraft Screen 生命周期，因此当前保留为生命周期局部协作者。只有当某项能力能以稳定、
无 Screen 桥接方法的接口独立测试时，再将其拆成单独组件。

`UIForm` 是唯一面向调用方的表单入口。它直接把控件创建委托给 `FormInputFactory` 和
`FormDisplayFactory`；内部 `UI.Form` 只负责共享布局、状态、生命周期和剩余的复合控件。
新增控件时应在 factory 中实现，并只通过 `UIForm` 暴露一次，不再向 `UI.Form` 添加镜像转发方法。

每组重载控件 API 都必须公开一个包含完整参数集的规范重载。较短重载只负责传入具名默认值并委托给
规范重载；factory 只实现规范形态，不再复制便捷重载。

输入控件可以在可见的录入状态中消费键盘和鼠标事件。运行时轮询、绑定触发、注册、配置持久化以及
修改全局 `KeyMapping` 均不属于 common GUI 库职责。
