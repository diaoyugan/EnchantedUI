# Enchanted UI

一个用于 Minecraft 的 GUI 框架，用于构建模块化、可组合的 UI 界面。

> ⚠️ 早期开发版本\
> 本项目仍在持续开发中，API、行为以及构建产物结构均不稳定，可能随时发生变化，且不会提供兼容性或迁移保证。

------------------------------------------------------------------------

## 当前状态

这是一个 **早期内部开发项目**。

-   API 尚未完善，可能频繁重构\
-   Maven 坐标尚未最终确定\
-   仅用于本地联调的测试构建产物\
-   可能存在破坏性更新且不会提供迁移支持

请自行承担使用风险。

------------------------------------------------------------------------

## 概述

EnchantedUI 提供一套模块化 Minecraft GUI 系统，包括：

-   分页式 Tab 界面系统
-   基于 Page 的 UI 组合结构
-   Form 风格布局工具
-   声明式 Widget 构建上下文
-   底部操作栏系统

------------------------------------------------------------------------

## API 入口

公共 API 入口：

    top.diaoyugan.enchanted_ui.client.gui.builder.UI

------------------------------------------------------------------------

## 示例
```
public final class ExampleScreen extends UI.TabbedScreen { 
    public ExampleScreen(Screen parent) { 
        super(parent, Component.literal("Example"));

        tab(10, 30, 20, Component.literal("Main"), ctx -> List.of(
            // widgets here
        ));

        bottomBar(UI.BottomBar.closeOnly(Component.literal("Close")));
    }

}
```
------------------------------------------------------------------------

## 内部测试构建产物

当前仅提供本地测试用构建产物：

-   enchanted_ui-common：核心 API / 库模块
-   enchanted_ui-fabric：Fabric 运行时模块
-   Neoforge版会在跟进版本后后进行开发

发布方式：

    ./gradlew publishForInternalTesting

输出目录：

    build/test-maven

------------------------------------------------------------------------

## 使用模式

### 独立运行模式（Standalone）

当 EnchantedUI 作为独立 Mod 安装时使用：

-   编译依赖 enchanted_ui-common
-   运行时将 enchanted_ui-fabric 放入 mods 目录

------------------------------------------------------------------------

### 嵌入模式（Embedded）

当其他 Mod 内嵌 UI 框架时使用：

-   依赖 enchanted_ui-common（implementation）
-   可选嵌入 Fabric 运行时模块

------------------------------------------------------------------------

## 重要说明

-   构建产物坐标尚未最终确定
-   内部 Maven 仓库仅用于本地测试
-   API 仍处于不稳定阶段
-   不提供向后兼容保证
-   文档可能滞后于实现

------------------------------------------------------------------------

## 文档

详细开发文档位于仓库：

    /docs

该目录包含架构设计、API 行为说明以及实验性功能记录。

------------------------------------------------------------------------

## 推荐使用方式

-   联调阶段优先使用 standalone 模式
-   仅在必要时使用 embedded 模式
-   未来更新可能存在频繁破坏性变更
