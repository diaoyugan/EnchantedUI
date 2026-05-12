# Enchanted UI

English | [简体中文](readme_zhcn.md)

A Minecraft GUI framework for building modular and composable UI
screens.

> ⚠️ Early Development Version\
> This project is under active development. APIs, behaviors, and
> artifact structure are unstable and may change at any time without
> notice.\
> No compatibility, stability, or migration guarantees are provided.

------------------------------------------------------------------------

## Status

This is an **early-stage internal development project**.

-   APIs are incomplete and may be refactored frequently\
-   Maven coordinates are not finalized\
-   Internal test artifacts are used for local integration only\
-   Breaking changes may occur without migration support

Use at your own risk.

------------------------------------------------------------------------

## Overview

EnchantedUI provides a modular GUI system for Minecraft, including:

-   Tabbed screen system
-   Page-based UI composition
-   Form-based layout utilities
-   Declarative widget building context
-   Bottom bar action system

------------------------------------------------------------------------

## Entry Point

Public API entry:

    top.diaoyugan.enchanted_ui.client.gui.builder.UI

------------------------------------------------------------------------

## Example
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

## Internal Testing Artifacts

The project currently provides local testing artifacts only:

-   enchanted_ui-common: core API / library module
-   enchanted_ui-fabric: Fabric runtime module
-   The NeoForge version will be developed after it catches up with newer game versions.

Artifacts are published via:

    ./gradlew publishForInternalTesting

Output directory:

    build/test-maven

------------------------------------------------------------------------

## Usage Modes

### Standalone Runtime Mode

Used when EnchantedUI is installed as a separate mod.

-   Compile against enchanted_ui-common
-   Run with enchanted_ui-fabric placed in the mods folder

------------------------------------------------------------------------

### Embedded Mode

Used when bundling EnchantedUI into another mod.

-   Depend on enchanted_ui-common via implementation
-   Optionally embed runtime via Fabric artifact

------------------------------------------------------------------------

## Important Notes

-   Artifact coordinates are temporary and may change
-   Internal Maven repository is local-only
-   API surface is unstable
-   No backward compatibility guarantees
-   Documentation may lag behind implementation

------------------------------------------------------------------------

## Documentation

Detailed development documentation is located in the repository:

    /docs

This directory contains in-depth guides, architecture notes, and usage
references.

------------------------------------------------------------------------

## Recommendation

-   Use standalone mode for integration testing
-   Use embedded mode only when necessary
-   Expect frequent breaking changes
