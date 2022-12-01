<img src="logo.png" alt="Gale logo" align="right" width="26%" style="margin: 4%;">
<div align="center">
  <h1>Gale</h1>
  <h3>A Minecraft server fork of <a href="https://github.com/PaperMC/Paper">Paper</a></h3>
  <h4><b>Master</b> branch: no known issues! ✅</h4>

[![Discord](https://img.shields.io/discord/1045402468416233592?color=5865F2&label=discord&style=for-the-badge)](https://discord.com/invite/pbsPkpUjG4)
</div>

## About

Gale is a [Paper](https://github.com/PaperMC/Paper) fork. It is intended to provide high performance, but we do not make any specific claims. You can see a comparison with other forks [here](https://github.com/GaleMC/Gale/wiki/Comparison-with-other-forks).

## Building

You can clone this repository and build it yourself.
If you are interested in making a Paperweight fork, check out [the example template](https://github.com/PaperMC/paperweight-examples)!

In order to distribute and use this server software, you need a Paperclip file:

```bash
./gradlew applyPatches && ./gradlew createReobfPaperclipJar
```

## Acknowledgements

This fork would not exist without the years-long work of all the contributors to the [Paper](https://github.com/PaperMC/Paper) and [Spigot](https://www.spigotmc.org/) projects,
including the dedication of their community to stability and trust among all users.
Over the years, many forks of Paper have appeared, including some that contain changes that are too minor to be worth inclusion and maintaining by the Paper project.
We gratefully include various tested and carefully individually reviewed changes from Paper forks, including [Airplane](https://github.com/TECHNOVE/Airplane), [Mirai](https://github.com/etil2jz/Mirai), [Purpur](https://github.com/PurpurMC/Purpur), [JettPack](https://gitlab.com/Titaniumtown/JettPack), [Patina](https://github.com/PatinaMC/Patina), [EmpireCraft](https://github.com/starlis/empirecraft) and [Slice](https://github.com/Cryptite/Slice), and from various Fabric mods, including [Lithium](https://github.com/CaffeineMC/lithium-fabric), [VMP](https://github.com/RelativityMC/VMP-fabric) and [C2ME](https://github.com/RelativityMC/C2ME-fabric).
We would also like to thank our friends at [MultiPaper](https://github.com/MultiPaper/MultiPaper) and MCMT ([Fabric](https://github.com/himekifee/MCMTFabric), [Forge](https://github.com/jediminer543/JMT-MCMT)) for their shared knowledge and hard work towards a common goal.

## License
Paperweight files are licensed under MIT. Patches are licensed under GPL-3.0, unless indicated differently in their header.