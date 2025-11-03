# Dan's Minecraft Utils Mod

## Getting set up (VSCode)

To develop the mod, you'll need to install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack), and then run:

```sh
export JAVA_HOME="/usr/lib/jvm/java-21-openjdk" # Make sure it's Java 21
chmod +x ./gradlew
./gradlew vscode
./gradlew genSources
```

This will create a VSCode run/debug workflow, which will boot up the game with the mod installed.

## Exporting the compiled mod

Use the following command to compile the mod:

```sh
./gradlew clean build
```

It saves to `/build/libs`.

There is also a GitHub action which compiles the mod as an artifact, that you download it from there.

This only works if you have Java 21 installed (or whichever version is listed in `build.gradle`, I think).

## TODO

- Remove Client side mod.
- Rename "teleport to player" command, to allow for "teleport to custom location" command?
  - `/stalk [player]`
  - `/visit [player]`
  - `/warp to [player]` (i.e. make it a subcommand)
- Create "teleport to custom location" command.
  - `/warp set [location]`
  - `/warp [location]` (this syntax is ideal, but if autocomplete we can achieve autocomplete only by having `/warp to [location]` then that's a tradeoff I'm happy to make)
    - If we had to piggyback off scoreboards, i.e. create a scoreboard objective on the player just to get autocomplete, would that be the end of the world? They'd have a random `/warp to deaths` suggestion because of the scoreboard but... like... whatever, right?
- Move AFK stuff into the mod.
  - `/afk`
  - Probably no need for a "disable AFK" command.
- Utility commands:
  - `/fuckoffrain` (or similar 😉)
