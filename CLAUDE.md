# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Border is a BentoBox addon for Minecraft Paper servers that creates and renders per-player world borders around islands. Players cannot pass the border. It supports two rendering modes: barrier blocks with particles, and vanilla world borders.

**Target:** Paper 1.21+ with BentoBox 3.10+, Java 21

## Build Commands

```bash
mvn clean package          # Build the plugin JAR
mvn test                   # Run all tests
mvn test -Dtest=BorderTest # Run a single test class
mvn verify                 # Full build with JaCoCo coverage
```

Output JAR: `target/Border-{version}.jar`

## Architecture

### Addon Lifecycle (BentoBox Pladdon pattern)

`BorderPladdon` (plugin entry point) → `Border` (addon, extends `Addon`) → registers commands, listeners, and border implementations per game mode.

### Border Rendering — Strategy + Proxy

`BorderShower` is the core interface with methods: `showBorder`, `hideBorder`, `clearUser`, `refreshView`, `teleportEntity`.

Two implementations:
- **`ShowBarrier`** — Renders barrier blocks and colored particles around island edges. Caches barrier block positions per player. Uses async chunk loading.
- **`ShowWorldBorder`** — Uses Paper's per-player WorldBorder API. Manipulates border animation (shrink/grow/static) to achieve color effects.

**`PerPlayerBorderProxy`** delegates to the correct implementation based on per-player metadata. Falls back to addon-wide default settings.

### Per-Player State via Metadata

Player preferences are stored as BentoBox `MetaDataValue` entries:
- `Border_state` — border on/off toggle
- `Border_bordertype` — BARRIER or VANILLA (stored as byte id)
- `Border_color` — RED, GREEN, or BLUE

### Commands

Registered as subcommands under each game mode's player command:
- `IslandBorderCommand` (`/[gamemode] border`) — toggle border visibility
- `BorderTypeCommand` (`/[gamemode] bordertype`) — switch between barrier/vanilla
- `BorderColorCommand` (`/[gamemode] bordercolor`) — change border color

### Event Handling

`PlayerListener` handles all player events (join, quit, move, teleport, mount, item drop) to trigger border show/hide/refresh and enforce boundary teleportation.

## Testing

Uses JUnit 5 + Mockito 5 + MockBukkit. All test classes extend `CommonTestSetup` which provides comprehensive mocking of BentoBox, Bukkit server, worlds, players, and island managers.

`WhiteBox` is a reflection utility for accessing private fields in tests.

## Key Conventions

- Configuration is managed via the `Settings` class with BentoBox's `@StoreAt`/`@ConfigComment` annotations and `Config<Settings>` loader
- Game modes can be excluded via `Settings.getDisabledGameModes()`
- Localization files live in `src/main/resources/locales/` (20+ languages, YAML format)
- `BorderType` is an enum with byte-based serialization ids (`fromId`/`getId`)
