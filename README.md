<img src="logo.png" alt="Gale logo" align="right" width="26%">
<div align="center">
  <h1>Gale</h1>
  <h3>The reliable high-performance Minecraft server fork.</h3>

[![Discord](https://img.shields.io/discord/1045402468416233592?color=5865F2&label=discord&style=for-the-badge)](https://discord.com/invite/gwezNT8c24)
[![Latest version](https://img.shields.io/badge/Latest_version-26.2-4fa31a?style=for-the-badge)](https://github.com/GaleMC/Gale/actions)
</div>

## About

Gale is a drop-in replacement for [Paper](https://github.com/PaperMC/Paper)
that improves performance reliably and without changing game mechanics.

## Installation

* Download the latest dev build from [GitHub Actions](https://github.com/GaleMC/Gale/actions)
* Replace the Paper server `.jar` file with the Gale `.jar` file

## Benefits

* **High reliability**\
  All features are reviewed with care, verified line-by-line and tested in production.

* **No changes to game mechanics**\
  Gale improves performance without changing behavior (by default).\
  Editing the configuration is optional.

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
    Gale configuration files.
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
    <i>Cache world generator sea level</i> (original by <a href="https://github.com/jaskarth">jaskarth</a>)<br>
    This method is potentially called for every block in the chunk,
    so this will save a lot of registry lookups.<br>
    Leaf: <code>Cache-world-generator-sea-level.patch</code>
  </li>
  <li>
    <i>Check targeting range before getting visibility</i> (original by <a href="https://github.com/PaulBGD">PaulBGD</a>)<br>
    Check targeting range before computing visibility distance,
    because the latter is more expensive.<br>
    Leaf: <code>Check-targeting-range-before-getting-visibility.patch</code>
  </li>
  <li>
    <i>Check type before distance when testing targets</i> (original by <a href="https://github.com/Taiyou06">Taiyou</a>)<br>
    Check type before computing distance, because the latter is more expensive.<br>
    Leaf: part of <code>Optimize-SetLookAndInteract-and-NearestVisibleLiving.patch</code>
  </li>
  <li>
    <i>Compute heuristic persistence reactively</i><br>
    Compute a heuristic for whether mobs are persistent when conditions change, instead of every tick.
  </li>
  <li>
    <i>Compute peaceful despawn reactively</i><br>
    Compute whether to despawn non-peaceful mobs when conditions change, instead of every tick.
  </li>
  <li>
    <i>Delay canSee check in entity collisions</i><br>
    Perform other checks before <code>canSee</code>,
    because the latter is more expensive.<br>
    Leaf: <code>Reduce-canSee-work.patch</code>
  </li>
  <li>
    <i>Don't iterate over empty tick effect collections</i> ((original by <a href="https://github.com/hayanesuru">hayanesuru</a>)<br>
    Leaf: part of <code>optimize-tickEffects.patch</code>
  </li>
  <li>
    <i>Inline player UUID comparison</i>
  </li>
  <li>
    <i>Initialize sensing with low capacity</i><br>
    Most mobs only ever target at most 1 entity (typically a nearby player or a mob farm bait entity)<br>
    so we create their sensing cache with an initial capacity of 2 instead of 16.<br>
    Leaf: <code>Initialize-line-of-sight-cache-with-low-capacity.patch</code>
  </li>
  <li>
    <i>Minimize size of packed dirty synched entity data item list</i> (original by <a href="https://github.com/hayanesuru">hayanesuru</a>)<br>
    Leaf: <code>Optimize-SynchedEntityData-packDirty.patch</code>
  </li>
  <li>
    <i>Only update frozen ticks if changed</i> (original by <a href="https://github.com/hayanesuru">hayanesuru</a>)<br>
    Leaf: <code>Only-update-frozen-ticks-if-changed.patch</code>
  </li>
  <li>
    <i>Optimize entity data serializer list</i><br>
    Make <code>EntityDataSerializers#SERIALIZERS</code> a simple list
    and store the index in each <code>EntityDataSerializer</code> directly.
  </li>
  <li>
    <i>Optimize global block state palette</i><br>
    Store the index in <code>Block#BLOCK_STATE_REGISTRY</code> in each <code>BlockState</code> directly.
  </li>
  <li>
    <i>Optimize matching item checks</i><br>
    Leaf: <code>Optimize-matching-item-checks.patch</code>
  </li>
  <li>
    <i>Optimize player passenger counting</i><br>
    Updates player passenger count reactively, instead of calculating it every tick.
  </li>
  <li>
    <i>Optimize pushable selector</i> (original by <a href="https://github.com/OverwriteMC">OverwriteMC</a>)<br>
    Avoid duplicated <code>Bukkit#isPushable</code> check, already checked inside the <code>Bukkit#canCollideWithBukkit</code>.<br>
    Leaf: <code>Optimize-pushable-selector.patch</code>
  </li>
  <li>
    <i>Optimize registry values</i><br>
    Optimize registry value to id conversion, and optimize maps that use registry values as keys.
  </li>
  <li>
    <i>Optimize tag checks</i><br>
    Store the tags for each registry value in an efficient data structure.
  </li>
  <li>
    <i>Pre-compute biome fiddle table</i> (original by <a href="https://github.com/hayanesuru">hayanesuru</a>)<br>
    Leaf: part of <code>cache-biome-for-mob-spawning-and-advancements.patch</code>
  </li>
  <li>
    <i>Pre-compute block state predicates</i> (original by <a href="https://github.com/hayanesuru">hayanesuru</a>)<br>
    Pre-computes some commonly tested predicates on <code>BlockBehaviour</code>.<br>
    Leaf:
    <ul>
      <li>part of <code>Cache-block-state-tags.patch</code></li>
      <li><code>optimize-canHoldAnyFluid.patch</code></li>
      <li>part of <code>optimize-getOnPos.patch</code></li>
      <li><code>optimize-isStateClimbable.patch</code></li>
    </ul>
  </li>
  <li>
    <i>Predict Halloween</i><br>
    Pre-compute the epoch milliseconds for Halloween, for faster comparison.<br>
    Leaf: <code>Predict-Halloween.patch</code>
  </li>
  <li>
    <i>Quick NearestVisibleLivingEntities entity iteration</i> (original by <a href="https://github.com/Taiyou06">Taiyou</a>)<br>
    Leaf: part of <code>Optimize-SetLookAndInteract-and-NearestVisibleLiving.patch</code>
  </li>
  <li>
    <i>Reduce RandomSource instances</i> (original by <a href="https://github.com/foss-mc">foss-mc</a>)<br>
    Re-use <code>RandomSource</code> instances where it doesn't affect game mechanics.<br>
    Leaf: <code>Reduce-RandomSource-instances.patch</code>
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
    <i>Skip enderman teleport if requires main thread chunk load</i> (original by <a href="https://github.com/PaulBGD">PaulBGD</a>)<br>
    Leaf: <code>Reduce-enderman-teleport-chunk-lookups.patch</code>
  </li>
  <li>
    <i>Skip entity move if movement is zero</i> (original by <a href="https://github.com/ishland">ishland</a>)<br>
    Run a simplified version of <code>Entity#move()</code> if the movement delta is zero.<br>
    Leaf: <code>Skip-entity-move-if-movement-is-zero.patch</code>
  </li>
  <li>
    <i>Skip negligible planar movement multiplication</i><br>
    Skip calling <code>Entity#getBlockSpeedFactor()</code> from <code>Entity#move()</code>
    when planar delta movement is negligible (within 1.0E-6 threshold).<br>
    Leaf: <code>Skip-negligible-planar-movement-multiplication.patch</code>
  </li>
  <li>
    <i>Skip PlayerCommandSendEvent if no listeners</i> (original by <a href="https://github.com/billygalbreath">BillyGalbreath</a>)<br>
    Leaf: <code>Skip-PlayerCommandSendEvent-if-there-are-no-listener.patch</code>
  </li>
  <li>
    <i>Skip canSee self check for chunk map player update</i> (original by <a href="https://github.com/MrPowerGamerBR">MrPowerGamerBR</a>)<br>
    Skip the self check in <code>CraftPlayer#canSee</code> if called from <code>ChunkMap#updatePlayer</code>,
    as it is checked already at that point.<br>
    Leaf: <code>SparklyPaper-Optimize-canSee-checks.patch</code>
  </li>
  <li>
    <i>Sort tryAddFrost checks</i> (original by <a href="https://github.com/2No2Name">2No2Name</a>)<br>
    Sort the checks in <code>LivingEntity#tryAddFrost()</code>
    in ascending order of cost.<br>
    Leaf: <code>Check-frozen-ticks-before-landing-block.patch</code>
  </li>
  <li>
    <i>Store canSee in a fast-access format</i><br>
    Complement the map that backs <code>canSee</code>
    by a packed boolean array for fast operations.
  </li>
  <li>
    <i>Store despawn ranges in enum map</i> (original by <a href="https://github.com/hayanesuru">hayanesuru</a>)<br>
    Leaf: part of <code>optimize-despawn.patch</code>
  </li>
  <li>
    <i>Store mob counts in an array</i> (original by <a href="https://github.com/ishland">ishland</a>)<br>
    Leaf: <code>Store-mob-counts-in-an-array.patch</code>
  </li>
  <li>
    <i>Store seenBy in a fast-access format</i><br>
    Replace the <code>seenBy</code> set by a list (for fast iteration)
    and packed boolean array (for fast contains checks).
  </li>
  <li>
    <i>Update boss bar within tick</i> (original by <a href="https://github.com/jellysquid3">jellysquid3</a>)<br>
    Performs boss bar update code at most once per tick during <code>tick()</code>,
    instead of every time <code>updateBossbar()</code> is called.
    <br>
    Leaf: <code>Update-boss-bar-within-tick.patch</code>
  </li>
  <li>
    <i>Use fastutil collections</i><br>
    Use <code>it.unimi.dsi.fastutil</code> collections
    instead of other (such as <code>java.util</code>) collections.<br>
    Leaf: <code>Replace-throttle-tracker-map-with-optimized-collecti.patch</code>
  </li>
  <li>
    <i>Use linked collections</i><br>
    Use linked data structures for collections that are frequently iterated over.<br>
    Leaf: <code>Use-linked-map-for-entity-trackers.patch</code>
  </li>
</ul></h6>
