# Internal Testing Usage

This project now exposes two practical artifacts for local integration testing:

- `enchanted_ui-common-<minecraft_version>`: plain library jar for embedding into another mod
- `enchanted_ui-fabric-<minecraft_version>`: Fabric runtime mod jar for standalone installation or jar-in-jar use

When writing integration code, prefer the public API package:

```text
top.diaoyugan.enchanted_ui.api.client.gui
```

Avoid building new integrations directly on top of internal implementation packages such as:

```text
top.diaoyugan.enchanted_ui.client.gui.builder
```

## Publish local test artifacts

Run:

```powershell
./gradlew publishForInternalTesting
```

Artifacts are published to:

```text
build/test-maven
```

## Mode 1: standalone runtime dependency

Use this when `EnchantedUI` is installed as its own mod in the game `mods` folder.

Consumer project repository:

```groovy
repositories {
    maven {
        url = uri("C:/path/to/EnchantedUI/build/test-maven")
    }
}
```

Consumer dependencies:

```groovy
dependencies {
    compileOnly "unspecified:enchanted_ui-common-26.2-snapshot-6:0.0.1-dev"
}
```

Runtime:

- put `enchanted_ui-fabric-26.2-snapshot-6-0.0.1-dev.jar` in the game `mods` folder
- declare a normal Fabric mod dependency on `enchanted_ui` in the consumer mod metadata

## Mode 2: embedded dependency

Use this when another mod wants to bundle the UI implementation itself.

Consumer repository:

```groovy
repositories {
    maven {
        url = uri("C:/path/to/EnchantedUI/build/test-maven")
    }
}
```

Consumer dependency:

```groovy
dependencies {
    implementation "unspecified:enchanted_ui-common-26.2-snapshot-6:0.0.1-dev"
}
```

Then embed that library into the final mod jar using the consumer project's packaging flow.

For Fabric projects, if you prefer nested mod packaging instead of plain class embedding, depend on the Fabric artifact instead:

```groovy
dependencies {
    modImplementation "unspecified:enchanted_ui-fabric-26.2-snapshot-6:0.0.1-dev"
    include "unspecified:enchanted_ui-fabric-26.2-snapshot-6:0.0.1-dev"
}
```

## Current recommendation

- standalone dependency: compile against `common`, run with `fabric`
- embedded dependency: embed `common`
- nested mod packaging on Fabric: `modImplementation` + `include` on `fabric`
