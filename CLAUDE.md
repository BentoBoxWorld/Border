# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Border is a BentoBox addon for Minecraft Paper servers that creates and renders per-player world borders around islands. Players cannot pass the border. It supports two rendering modes: barrier blocks with particles, and vanilla world borders.

**Target:** Paper 1.21+ with BentoBox 3.12+, Java 21

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

## Dependency Source Lookup

When you need to inspect source code for a dependency (e.g., BentoBox, addons):

1. **Check local Maven repo first**: `~/.m2/repository/` — sources jars are named `*-sources.jar`
2. **Check the workspace**: Look for sibling directories or Git submodules that may contain the dependency as a local project (e.g., `../bentoBox`, `../addon-*`)
3. **Check Maven local cache for already-extracted sources** before downloading anything
4. Only download a jar or fetch from the internet if the above steps yield nothing useful

Prefer reading `.java` source files directly from a local Git clone over decompiling or extracting a jar.

In general, the latest version of BentoBox should be targeted.

## Project Layout

Related projects are checked out as siblings under `~/git/`:

**Core:**
- `bentobox/` — core BentoBox framework

**Game modes:**
- `addon-acidisland/` — AcidIsland game mode
- `addon-bskyblock/` — BSkyBlock game mode
- `Boxed/` — Boxed game mode (expandable box area)
- `CaveBlock/` — CaveBlock game mode
- `OneBlock/` — AOneBlock game mode
- `SkyGrid/` — SkyGrid game mode
- `RaftMode/` — Raft survival game mode
- `StrangerRealms/` — StrangerRealms game mode
- `Brix/` — plot game mode
- `parkour/` — Parkour game mode
- `poseidon/` — Poseidon game mode
- `gg/` — gg game mode

**Addons:**
- `addon-level/` — island level calculation
- `addon-challenges/` — challenges system
- `addon-welcomewarpsigns/` — warp signs
- `addon-limits/` — block/entity limits
- `addon-invSwitcher/` / `invSwitcher/` — inventory switcher
- `addon-biomes/` / `Biomes/` — biomes management
- `Bank/` — island bank
- `Border/` — world border for islands
- `Chat/` — island chat
- `CheckMeOut/` — island submission/voting
- `ControlPanel/` — game mode control panel
- `Converter/` — ASkyBlock to BSkyBlock converter
- `DimensionalTrees/` — dimension-specific trees
- `discordwebhook/` — Discord integration
- `Downloads/` — BentoBox downloads site
- `DragonFights/` — per-island ender dragon fights
- `ExtraMobs/` — additional mob spawning rules
- `FarmersDance/` — twerking crop growth
- `GravityFlux/` — gravity addon
- `Greenhouses-addon/` — greenhouse biomes
- `IslandFly/` — island flight permission
- `IslandRankup/` — island rankup system
- `Likes/` — island likes/dislikes
- `Limits/` — block/entity limits
- `lost-sheep/` — lost sheep adventure
- `MagicCobblestoneGenerator/` — custom cobblestone generator
- `PortalStart/` — portal-based island start
- `pp/` — pp addon
- `Regionerator/` — region management
- `Residence/` — residence addon
- `TopBlock/` — top ten for OneBlock
- `TwerkingForTrees/` — twerking tree growth
- `Upgrades/` — island upgrades (Vault)
- `Visit/` — island visiting
- `weblink/` — web link addon
- `CrowdBound/` — CrowdBound addon

**Data packs:**
- `BoxedDataPack/` — advancement datapack for Boxed

**Documentation & tools:**
- `docs/` — main documentation site
- `docs-chinese/` — Chinese documentation
- `docs-french/` — French documentation
- `BentoBoxWorld.github.io/` — GitHub Pages site
- `website/` — website
- `translation-tool/` — translation tool

Check these for source before any network fetch.

## Key Dependencies (source locations)

- `world.bentobox:bentobox` → `~/git/bentobox/src/`
