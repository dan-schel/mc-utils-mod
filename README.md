# Dan's Minecraft Utils Mod

## Getting set up

To develop the mod, you'll need to run:

```
chmod +x ./gradlew
./gradlew vscode
./gradlew genSources
```

This will create a VSCode run/debug workflow, which will boot up the game with the mod installed.

## Exporting the compiled mod

Use the following command to compile the mod:

```
./gradlew clean build
```

There is also a GitHub action which compiles the mod as an artifact, that you download it from there.

This only works if you have Java 17 installed (or whichever version is listed in `build.gradle`, I think).
