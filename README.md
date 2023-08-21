<img src="logo.png" alt="Gale logo" align="right" width="26%">
<div align="center">
  <h1>Gale</h1>
  <h3>A Minecraft server fork of <a href="https://github.com/PaperMC/Paper">Paper</a></h3>
  <h5><i>In active testing - reporting any issues you encounter is highly appreciated!</i></h5>

[![Discord](https://img.shields.io/discord/1045402468416233592?color=5865F2&label=discord&style=for-the-badge)](https://discord.com/invite/gwezNT8c24)
</div>

## About

Gale is a fork of [Paper](https://github.com/PaperMC/Paper). It is intended to provide strong performance.
The project is in open alpha.

## Current features

* **Faster threading system**\
  Gale comes with a custom threading system, that immediately makes terrain generation 2-3x faster than Paper on most systems!\
  (this is up to 1.19 only, working on rewriting it to 1.20 too but there were some chunk system changes related to Folia, my apologies)
* **Micro-optimizations**\
  A number of micro-optimizations that do not change game mechanics from other projects, such as [Airplane](https://github.com/TECHNOVE/Airplane) and [Lithium](https://github.com/CaffeineMC/lithium-fabric), are also included. Every included optimization has been carefully tested and reviewed line-by-line; faulty or risky optimizations will not be added.
* **Fixes and options**\
  Gale contains fixes for a few small Minecraft bugs from [Purpur](https://github.com/PurpurMC/Purpur), options to disable some console logs, the option to re-enable sand duping, and more. Every change is fully configurable, and can always be set to Paper behavior.
* **Variable entity wake-up**\
  Waking up inactive entities happens spread over time, instead of many entities at once, which makes entities feel and behave more natural.

## Contributing

Pull requests are welcomed! Don't be afraid to submit a pull request that you may feel is just for yourself. All ideas are welcome. To learn how to submit pull requests, check out the tutorial [here](https://github.com/GaleMC/Gale/wiki/Tutorial:-Contributing).

## Building from source

If you only want to build the Gale .jar file yourself, without a plan to add or change any patches, check out the tutorial [here](https://github.com/GaleMC/Gale/wiki/Tutorial:-Building-from-source).

## Making your own fork

If you would like to make a Paper fork based on Gale, you can check out the [example template](https://github.com/PaperMC/paperweight-examples).

## Acknowledgements

Of course, this fork would not exist without the years-long work of all the contributors to the [Paper](https://github.com/PaperMC/Paper) and [Spigot](https://www.spigotmc.org/) projects.

Additional thanks and friendly greetings go out to the following forks and other projects, for their code, shared knowledge or generous support:
* [Airplane](https://github.com/TECHNOVE/Airplane)
* [Purpur](https://github.com/PurpurMC/Purpur)
* [Leaf](https://github.com/Winds-Studio/Leaf)
* [Mirai](https://github.com/etil2jz/Mirai)
* [Kaiiju](https://github.com/KaiijuMC/Kaiiju)
* [Plazma](https://github.com/PlazmaMC/Plazma)
* [KeYi](https://github.com/MC-Multithreading-Lab/KeYi-MT)
* [EmpireCraft](https://github.com/starlis/empirecraft)
* [Slice](https://github.com/Cryptite/Slice)
* [JettPack](https://gitlab.com/Titaniumtown/JettPack)
* [Lithium](https://github.com/CaffeineMC/lithium-fabric)
* [VMP](https://github.com/RelativityMC/VMP-fabric)
* [C2ME](https://github.com/RelativityMC/C2ME-fabric)
* [MultiPaper](https://github.com/MultiPaper/MultiPaper)
* MCMT ([Fabric](https://github.com/himekifee/MCMTFabric), [Forge](https://github.com/jediminer543/JMT-MCMT))

## License
Paperweight files are licensed under MIT.
Patches are licensed under GPL-3.0, unless indicated differently in their header.
Binaries are licensed under GPL-3.0.
