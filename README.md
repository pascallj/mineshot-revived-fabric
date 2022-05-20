# Mineshot Revived (Fabric mod for Minecraft)

Mineshot Revived is a mod for Minecraft (using Fabric) for creating high-resolution screenshots, optionally using the orthographic camera.

This can be useful for a variety of scenarios:

* High quality wallpapers
* Poster images
* Map overviews
* Mob and block images for the [Minecraft Wiki](https://minecraft.fandom.com/wiki/Minecraft_Wiki)

The majority of the code is based on the original [Mineshot by Barracuda](https://github.com/ata4/mineshot). So, many thanks to the original creator. Unfortunately that project seems dead, probably because of the big changes which were introduced.

The main purpose of this project is to keep Mineshot alive by updating for newer Minecraft/Fabric versions. I previously ported a version for [Minecraft Forge](https://github.com/pascallj/mineshot-revived) as well, but during the development I discovered that I prefer Fabric much more. Especially the fast updates for new Minecraft versions. I'll keep the old project around for now, but I am not sure if there will be any future updates.

There is support for Minecraft 1.19 (beta), 1.18.x, while old versions are available for 1.17 and 1.16.x.

## Requirements
Both the [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) are required for this mod to function.

## Usage

The controls are slightly different compared to the original Mineshot:

* F9: Take screenshot
* Numpad 4: Rotate left
* Numpad 6: Rotate right
* Numpad 8: Rotate up
* Numpad 2: Rotate down
* Numpad 7: Top view
* Numpad 1: Front view
* Numpad 3: Side view
* Numpad 5: Switch between perspective and orthograpic projection
* MOD-key (Left Alt) + Numpad 5: Switch between fixed and free camera
* MOD-key (Left Alt) + Numpad 2/4/6/8: Step through angles at a fixed rate
* Plus: Zoom in
* Minus: Zoom out
* Multiply: Turn clipping off
* Divide: Render full 360 degrees around player
* Demical dot: Change the background color between sky, red and white

The key-bindings can be changed in the Minecraft Controls menu.

The configuration menu can be accessed via Mod Menu options. In here you can change the resolution of the screenshot and if you want to be notified of new development versions (you will always be notified on the title screen of new stable versions).

Please note that the screenshots created will be huge (4K is already 23,7 MB), so choose your resolution carefully. The screenshot will be a Targa image file and will therefore be limited to 65535 by 65535 pixels.

## Known limitations

There are several features which aren't working (yet) in Mineshot Revived:

* Tiled rendering: only off-screen framebuffer rendering for now

## Download

Compiled jars are available on the [releases page](https://github.com/pascallj/mineshot-revived-fabric/releases).

## Development

If you want to help develop this mod, feel free to open issues and or pull requests. A feature request can also be opened as an issue. Please provide as much details as you can when opening issues or pull requests.

A PR should be opened against the `master` branch, except when it requires the next version of Minecraft; then it should opened against the `next` branch.
