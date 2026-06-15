# Artifact And Embedding Guide

The build publishes three artifacts:

- `enchanted_ui-common-<minecraft_version>`: loader-neutral UI classes only
- `enchanted_ui-fabric-<minecraft_version>`: standalone Fabric mod shell
- `enchanted_ui-neoforge-<minecraft_version>`: standalone NeoForge mod shell

Publish local artifacts with:

```powershell
./gradlew publishForInternalTesting
```

They are written to `build/test-maven`.

## Standalone Mode

Compile against the common artifact and install the matching platform shell at
runtime. Declare a normal `enchanted_ui` mod dependency in the consumer's Loader
metadata.

```groovy
dependencies {
    compileOnly "<group>:enchanted_ui-common-<minecraft_version>:<version>"
}
```

The platform shells contain the Loader metadata, entrypoints, demo command,
standalone icon, and standalone localization resources.

## Embedded Mode

Embed only the common artifact. Never embed the Fabric or NeoForge artifact:
those are real mods and intentionally contain public Loader metadata and
`assets/enchanted_ui`.

The common jar contains no mod metadata, assets, data, mixin configuration, or
public `eui.*` translation keys. Relocate its entire root package so every
consumer owns a private copy:

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

Configure Loom or ModDevGradle to remap/package that shadow jar according to the
consumer project's normal build flow. Shadow rewrites references from consumer
classes as well as the library classes.

Framework fallback translation keys are derived from
`UILocalization.class.getPackageName()`. After relocation they use the private
package prefix automatically. Consumers that need localized framework defaults
can add those relocated keys to their own language JSON; consumer-owned UI text
should continue using the consumer mod's normal translation keys.

Run `./gradlew :common:verifyIsolation` to check the common artifact boundary.
