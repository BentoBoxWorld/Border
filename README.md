# Border
[![Build Status](https://ci.codemc.io/job/BentoBoxWorld/job/Border/badge/icon)](https://ci.codemc.io/job/BentoBoxWorld/job/Border/)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=BentoBoxWorld_Border&metric=bugs)](https://sonarcloud.io/dashboard?id=BentoBoxWorld_Border)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=BentoBoxWorld_Border&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=BentoBoxWorld_Border)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=BentoBoxWorld_Border&metric=security_rating)](https://sonarcloud.io/dashboard?id=BentoBoxWorld_Border)

**Border** shows a world border around islands. The world border is the Minecraft world border and players cannot go outside of it or a barrier block border that can show up when required.

##Features##

* Minecraft vanilla world border (requires WorldBorderAPI plugin - see below)
* Alternative barrier blocks border - can block transit or just show where the border is using particles.
* Option to show maximum border - uses 🚫 particle.

## Installation
1. Put this jar into the BentoBox addons folder
2. Restart

## Configuration

The config.yml file contains a number of options.

### Disabled GameModes
By default, Border will operate in all game mode worlds on the BentoBox server. To disable a game mode it is necessary to write its name on new line that starts with -. Example:
```
 disabled-gamemodes:
   - BSkyBlock
```

### Use WorldBorderAPI (WBAPI)
If you want to use the vanilla world border then you must download the WorldBorderAPI plugin. You can find them here: https://github.com/yannicklamprecht/WorldBorderAPI/releases

Players cannot exit past the vanilla world border, so it will completely block movement outside of a player's protected island area. If you do not want this, then do not use WBAPI.

To activate WABI, set this to true in the config.yml:

```
use-wbapi: true
```

### Use barrier blocks.
This only applies if you are not using WBAPI.

If true, the the border will use barrier blocks to prevent most players from exiting the border. If players do manage to exit it, they will get teleported back inside it. 

If false, the border is indicated by particles only.

The default is to use barrier blocks.

```
use-barrier-blocks: true
```

### Default border behavior
Players can turn the border on and off if they have the right permission using the border command. This setting makes the default on or off:

``` 
show-by-default: true
```

### Show max-protection range border.

This only applies if you are not using WABBI.

This is a visual border only and not a barrier. It displays the 🚫 particle. This is useful for game modes like Boxed where the player's protection area can move around.

```
show-max-border: true
```

## Commands

There is one command that turns the border on or off. Since Version 3.0.0 it requires a permission:

/[player command] border

## Permissions

There is one permission to allow/disallow use of the border command:

`[gamemode].border.toggle`

This permission is not given by default. 

## Like this addon?
[Sponsor tastybento](https://github.com/sponsors/tastybento) to get more addons like this and make this one better!

## Are you a coder?
This is one of the easier addons from a code perspective. Maybe you could make it better! Border is open source and we love Pull Requests. Become a BentoBox co-author today!