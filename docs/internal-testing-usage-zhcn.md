# 内部测试使用说明

当前项目提供两种适合本地联调的构建产物：

- `enchanted_ui-common-<minecraft_version>`：普通库 jar，适合嵌入到其他 mod
- `enchanted_ui-fabric-<minecraft_version>`：Fabric 运行时 mod jar，适合独立安装或 jar-in-jar 使用

在编写接入代码时，建议优先使用公共 API 包：

```text
top.diaoyugan.enchanted_ui.api.client.gui
```

不要直接基于下面这种内部实现包去写新集成：

```text
top.diaoyugan.enchanted_ui.client.gui.builder
```

## 发布本地测试产物

运行：

```powershell
./gradlew publishForInternalTesting
```

产物会发布到：

```text
build/test-maven
```

## 模式 1：独立运行时依赖

当 `EnchantedUI` 作为独立 mod 安装进游戏 `mods` 文件夹时，使用这种方式。

消费方项目仓库配置：

```groovy
repositories {
    maven {
        url = uri("C:/path/to/EnchantedUI/build/test-maven")
    }
}
```

消费方依赖：

```groovy
dependencies {
    compileOnly "unspecified:enchanted_ui-common-26.2-snapshot-6:0.0.1-dev"
}
```

运行时：

- 把 `enchanted_ui-fabric-26.2-snapshot-6-0.0.1-dev.jar` 放进游戏 `mods` 文件夹
- 在消费方 mod 元数据里正常声明对 `enchanted_ui` 的 Fabric 依赖

## 模式 2：内嵌依赖

当其他 mod 想把 UI 实现一起打包进去时，使用这种方式。

消费方仓库配置：

```groovy
repositories {
    maven {
        url = uri("C:/path/to/EnchantedUI/build/test-maven")
    }
}
```

消费方依赖：

```groovy
dependencies {
    implementation "unspecified:enchanted_ui-common-26.2-snapshot-6:0.0.1-dev"
}
```

然后按照消费方项目自己的打包流程，把这个库一起打进最终 mod jar。

如果是 Fabric 项目，并且你更偏向嵌套 mod 打包而不是普通类库嵌入，也可以直接依赖 Fabric 产物：

```groovy
dependencies {
    modImplementation "unspecified:enchanted_ui-fabric-26.2-snapshot-6:0.0.1-dev"
    include "unspecified:enchanted_ui-fabric-26.2-snapshot-6:0.0.1-dev"
}
```

## 当前建议

- 独立依赖：编译依赖 `common`，运行时安装 `fabric`
- 内嵌依赖：嵌入 `common`
- Fabric 嵌套 mod 打包：对 `fabric` 使用 `modImplementation` 加 `include`
