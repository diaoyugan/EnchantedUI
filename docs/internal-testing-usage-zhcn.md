# 产物与内嵌使用说明

构建会发布三个产物：

- `enchanted_ui-common-<minecraft_version>`：只包含 UI 类的 Loader 无关库
- `enchanted_ui-fabric-<minecraft_version>`：Fabric 独立 Mod 外壳
- `enchanted_ui-neoforge-<minecraft_version>`：NeoForge 独立 Mod 外壳

发布本地测试产物：

```powershell
./gradlew publishForInternalTesting
```

产物会写入 `build/test-maven`。

## 独立 Mod 模式

消费方编译时依赖 common，运行时安装对应平台外壳，并在自己的 Loader metadata 中正常声明
对 `enchanted_ui` 的依赖。

```groovy
dependencies {
    compileOnly "<group>:enchanted_ui-common-<minecraft_version>:<version>"
}
```

平台外壳包含 Loader metadata、入口、演示命令、独立版图标和本地化资源。

## 内嵌模式

只允许内嵌 common。不要内嵌 Fabric 或 NeoForge 产物：它们是真正的 Mod，按设计包含公共
Loader metadata 和 `assets/enchanted_ui`。

common jar 不包含 Mod metadata、assets、data、Mixin 配置或公共 `eui.*` 翻译 key。
消费方必须 relocation 整个根包，使每个 Mod 拥有自己的私有副本：

```groovy
configurations {
    enchantedUiEmbed
}

dependencies {
    compileOnly "<group>:enchanted_ui-common-<minecraft_version>:<version>"
    enchantedUiEmbed "<group>:enchanted_ui-common-<minecraft_version>:<version>"
}

tasks.named("shadowJar") {
    configurations = [project.configurations.enchantedUiEmbed]
    relocate(
            "top.diaoyugan.enchanted_ui",
            "com.example.yourmod.internal.enchantedui"
    )
}
```

再按消费方现有构建流程配置 Loom 或 ModDevGradle，对 shadow jar 执行 remap/打包。
Shadow 会同时改写消费方类和库类中的类型引用。

框架 fallback 翻译 key 基于 `UILocalization.class.getPackageName()` 动态生成，relocation 后会
自动使用私有包前缀。需要本地化框架默认文案时，可在消费 Mod 自己的语言 JSON 中提供 relocation
后的 key；业务文案仍应使用消费 Mod 自己的常规翻译 key。

运行 `./gradlew :common:verifyIsolation` 可检查 common 产物边界。
