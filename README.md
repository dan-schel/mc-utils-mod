# Dan's Minecraft Utils Mod

A server-side Minecraft mod which adds a few custom commands, and prevents certain mobs griefing the world. Requires/powered by Fabric.

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

## Development Guide

### Getting set up (VSCode)

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
