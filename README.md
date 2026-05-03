# Dan's Minecraft Utils Mod

A server-side Minecraft mod which adds a few custom commands and prevents certain mobs griefing the world. Requires/powered by Fabric.

⭐ Download the latest release [from here](https://github.com/dan-schel/mc-utils-mod/releases). ⭐

## Features

- `/visit <username>` command to teleport to another player.
- `/warp <place>` and `/warp add <name> [global]` command to teleport to a pre-defined location.
- `/unwarp` and `/unvisit` commands to return to your previous location.
- `/daytime` command to skip the night.
- `/sunshine` command to clear the weather.
- Tracking players which haven't moved for >5 mins and inform other players on the server that they're AFK (plus manual `/afk` command).
- Prevent creepers, ghasts, and enderman destroying/placing blocks without disabling `mobGriefing` (which would also disable villagers being able to farm, etc.).
- Disables the `/clear` command.
- Logs nearby players when TNT is ignited, and when TNT enters a player's inventory.

## Development Guide

### Getting set up (VS Code)

To develop the mod, you'll need to install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack), and then run:

```sh
# Setup Java & Gradle. Make sure it's Java 21 (or whatever's in build.gradle - for Minecraft 1.21.1 it's Java 21 anyway).
export JAVA_HOME="/usr/lib/jvm/java-21-openjdk"
chmod +x ./gradlew

# Generate VSCode run/debug workflows. (Press F5 to debug.)
./gradlew vscode

# Makes the Minecraft source code available to view. (Use Ctrl+P in VSCode and search with a hashtag, e.g. "#CreeperEntity".)
./gradlew genSources
```

### Exporting the compiled mod

Use the following command to compile the mod:

```sh
./gradlew clean build
```

It saves to `/build/libs`.

There is also a GitHub action which automatically builds a release when merging to master. Be sure to bump the version in `gradle.properties` before merging.

### Disabling null-check warnings (VS Code)

Since the switch from Yarn to Mojang's official mappings, there's about a gazillion null-check warnings in the code. Actually addressing them isn't straightforward, or wasn't at the time of writing, because (a) it's not an official Java thing, it's an Eclipse thing, and (b) parts of the Minecraft code, e.g. Brigadier, didn't seem to support it or something, and everything coming from it was nullable and created headaches.

So, I've just decided to disable them for now, like so:

My `.vscode/settings.json` file:

```jsonc
{
  "java.configuration.updateBuildConfiguration": "interactive",
  "java.compile.nullAnalysis.mode": "disabled",
}
```
