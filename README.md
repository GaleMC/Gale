<img src="logo.png" alt="Gale logo" align="right" width="26%">
<div align="center">
  <h1>Gale</h1>
  <h3>A reliable high-performance Minecraft server fork.</h3>

[![Discord](https://img.shields.io/discord/1045402468416233592?color=5865F2&label=discord&style=for-the-badge)](https://discord.com/invite/gwezNT8c24)
</div>

## About

Gale is a fork of [Paper](https://github.com/PaperMC/Paper) with reliable and non-gameplay-changing performance improvements.

## Benefits

* **Reliability**\
  All features are reviewed with care. Every feature is carefully implemented and verified line-by-line.

* **No changes to game mechanics**\
  Gale improves performance without changing behavior (by default).

* **Configuration is optional**\
  By default, Gale behaves the same as Paper. Editing the configuration is optional.

## Contributing

Pull requests are welcomed!
Don't be afraid to submit a pull request that you may feel is just for yourself.
All ideas are welcome.
If submitted pull requests do not meet our standards, we can work together to improve them.

## Building from source

* Clone the project
* Run `./gradlew applyAllPatches`
* Run `./gradlew :gale-server:createPaperclipJar`

## Acknowledgements

Of course, this fork would not exist without the years-long work of all the contributors to
[Paper](https://github.com/PaperMC/Paper).

Additional thanks goes out to the contributors to
[Spigot](https://www.spigotmc.org/) and Bukkit,
and to the contributors to other Minecraft server software.

Gale includes only *strongly verified* performance improvements.
If you need even more performance, you can try
[Leaf](https://www.leafmc.one/), which comes with more features.

## License

Paperweight files are licensed under MIT.
Patch files and binaries are licensed under GPL-3.0.

## Features

All included features are listed below.\
Features originating from other projects are carefully verified and updated as part of Gale.

<h6><ul>
  <li>
    <i>Branding</i><br>
    Server identifies as Gale, with appropriate branding in console, watchdog, and version output.
  </li>
  <li>
    <i>Configuration</i><br>
    Adds Gale configuration files
  </li>
  <li>
    <i>Allocate zero or one block destruction packets</i> (original by <a href="https://github.com/vytskalt">vytskalt</a>)<br>
    Leaf: <code>Reduce-block-destruction-packet-allocations.patch</code>
  </li>
  <li>
    <i>Cache Identifier toString and hashCode</i> (original by <a href="https://github.com/OverwriteMC">OverwriteMC</a>)<br>
    Lazily cache <code>Identifier</code> <code>toString()</code> and <code>hashCode()</code>.<br>
    Leaf: <code>Cache-identifier-toString-and-hash.patch</code>
  </li>
  <li>
    <i>Initialize sensing with low capacity</i><br>
    Most mobs only ever target at most 1 entity (typically a nearby player or a mob farm bait entity)<br>
    so we create their sensing cache with an initial capacity of 2 instead of 16.<br>
    Leaf: <code>Initialize-line-of-sight-cache-with-low-capacity.patch</code>
  </li>
  <li>
    <i>Only update frozen ticks if changed</i> (original by <a href="https://github.com/hayanesuru">hayanesuru</a>)<br>
    Leaf: <code>Only-update-frozen-ticks-if-changed.patch</code>
  </li>
  <li>
    <i>Optimize matching item checks</i><br>
    Leaf: <code>Optimize-matching-item-checks.patch</code>
  </li>
  <li>
    <i>Optimize pushable selector</i> (original by <a href="https://github.com/OverwriteMC">OverwriteMC</a>)<br>
    Avoid duplicated <code>Bukkit#isPushable</code> check, already checked inside the <code>Bukkit#canCollideWithBukkit</code>.<br>
    Leaf: <code>Optimize-pushable-selector.patch</code>
  </li>
  <li>
    <i>Pre-compute biome fiddle table</i> (original by <a href="https://github.com/hayanesuru">hayanesuru</a>)<br>
    Leaf: part of <code>cache-biome-for-mob-spawning-and-advancements.patch</code>
  </li>
  <li>
    <i>Replace division by multiplication</i> (original by <a href="https://github.com/2No2Name">2No2Name</a>)<br>
    Multiplication is faster than division in every environment.<br>
    Leaf: <code>Replace-division-by-multiplication-in-CubePointRange.patch</code>
  </li>
  <li>
    <i>Skip Bukkit callEvent early if no listeners</i> (original by <a href="https://github.com/lilingfengdev">lilingfengdev</a>)<br>
    Leaf: <code>Skip-event-if-no-listeners.patch</code>
  </li>
  <li>
    <i>Skip cloning advancement criteria</i> (original by <a href="https://github.com/etil2jz">etil2jz</a>)<br>
    Leaf: <code>Skip-cloning-advancement-criteria.patch</code>
  </li>
  <li>
    <i>Skip entity move if movement is zero</i> (original by <a href="https://github.com/ishland">ishland</a>)<br>
    Run a simplified version of <code>Entity#move()</code> if the movement delta is zero.<br>
    Leaf: <code>Skip-entity-move-if-movement-is-zero.patch</code>
  </li>
  <li>
    <i>Store mob counts in an array</i> (original by <a href="https://github.com/ishland">ishland</a>)<br>
    Leaf: <code>Store-mob-counts-in-an-array.patch</code>
  </li>
</ul></h6>
