# Features

All included features are listed below.

Every feature is identified in source code by corresponding `// Gale` marker comments.

### Project setup

<ul>
  <li>
    <i>Branding</i>
    <br/>
    Server identifies as Gale, with appropriate branding in console, watchdog, and version output.
  </li>
  <li>
    <i>Configuration</i>
    <br/>
    Gale configuration files.
  </li>
</ul>

### Large features

<ul>
  <li>
    <i>Optimize Mob.checkDespawn</i>
    <br/>
    Optimize the contents and calling of <code>Mob.checkDespawn</code> method in various ways.
    <br/>
    <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
  </li>
</ul>

### Medium features

<ul>
  <li>
    <i>Cache</i>
    <br/>
    Lazily compute values and cache them while they remain valid.
    <ul>
      <li>
        <i>BiomeManager.getBiome()</i>
        <br/>
        Caches biomes requested with <code>Level.getBiome()</code>, excluding Bukkit API calls.
        This caches results for mob spawning, villager breeding, advancement triggers and more.
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>cache-biome-for-mob-spawning-and-advancements.patch</code></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Collections</i>
    <br/>
    Use an optimized data structure or pattern for collection data.
    <ul>
      <li>
        <i>ChunkMap.TrackedEntity.seenBy</i>
        <br/>
        Store <code>seenBy</code> in a fast-access format.
        <ul>
          <li>
            Split <code>ChunkMap.TrackedEntity.seenBy</code> into a list (for iteration) and a packed boolean array
            (for set functionality) in which each <code>Connection</code> is indexed by
            <code>Connection.packedSeenByWordIndex</code> and <code>Connection.packedSeenByWordMask</code>.
          </li>
          <li>
            Make <code>ServerEntity.trackedPlayers</code> a list.
          </li>
        </ul>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Intrusive values</i>
    <br/>
    Store properties of some types directly as a field,
    instead of in some external data structure.
    <ul>
      <li>
        <i>CraftPlayer.invertedVisibilityEntities</i>
        <br/>
        For each entry in <code>CraftPlayer.invertedVisibilityEntities</code>,
        store a corresponding inverse entry in <code>CraftEntity.packedVisibilityInversions</code>,
        an efficient data structure in which each player is indexed by
        <code>CraftPlayer.packedVisibilityInversionsWordIndex</code> and <code>CraftPlayer.packedVisibilityInversionsWordMask</code>.
        <br/>
        This allows for fast lookups from <code>CraftPlayer.canSee()</code>.
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Registry elements</i>
        <ul>
          <li>
            Make <code>Registry.getId(T)</code> available as <code>IdAwareRegistryValue.getIdInRegistry()</code> (which is stored in <code>FieldIdAwareRegistryValue.idInRegistry</code>).
          </li>
          <li>
            Store <code>Registry.asHolderIdMap().IdMap.getId(Holder)</code> in <code>Holder.Reference.idInRegistry</code>.
          </li>
        </ul>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Pre-compute</i>
    <br/>
    Pre-compute values instead of repeatedly computing them whenever needed.
    <ul>
      <li>
        <i>Common BlockState predicates</i>
        <br/>
        Pre-compute some commonly tested predicates on <code>BlockState</code>s.
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>), <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a> and <a href="https://github.com/RuleGaed">RuleGaed</a></sup></sub>
        <br/>
        <sub><sup>Leaf patches:</sup></sub>
        <br/>
        <sub><sup>- part of <code>Cache-block-state-tags.patch</code></sup></sub>
        <br/>
        <sub><sup>- <code>optimize-canHoldAnyFluid.patch</code></sup></sub>
        <br/>
        <sub><sup>- part of <code>optimize-getOnPos.patch</code></sup></sub>
        <br/>
        <sub><sup>- <code>optimize-isStateClimbable.patch</code></sup></sub>
      </li>
      <li>
        <i>Registry tags</i>
        <br/>
        Pre-compute the tags for each registry element and store them in <code>Holder.tagsBitSet</code>,
        an efficient data structure in which each tag is indexed by
        <code>TagKey.indexInRegistryWordIndex</code> and <code>TagKey.indexInRegistryWordIndex</code>.
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
    </ul>
  </li>
</ul>

### Small features

<ul>
  <li>
    <i>Cache</i>
    <br/>
    Lazily compute values and cache them while they remain valid.
    <ul>
      <li>
        <i>Enchantment.hashCode()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Identifier.hashCode() and Identifier.toString()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/OverwriteMC">OverwriteMC</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Cache-identifier-toString-and-hash.patch</code></sup></sub>
      </li>
      <li>
        <i>ItemLore.hashCode()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>MapDecorationType.hashCode()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>NoiseBasedChunkGenerator.getSeaLevel()</i>
        <br/>
        This method is potentially called for every block in the chunk,
        so this will save a lot of lookups.
        <br/>
        <sub><sup>By: <a href="https://github.com/jaskarth">jaskarth</a> (as part of <a href="https://github.com/CaffeineMC/lithium">Lithium</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Cache-world-generator-sea-level.patch</code></sup></sub>
      </li>
      <li>
        <i>PaintingVariant.hashCode()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>SpecialDates.isHalloween()</i>
        <br/>
        Pre-compute the epoch milliseconds for the next Halloween, for faster comparison.
        <br/>
        <sub><sup>By: <a href="https://github.com/noramibu">noramibu</a> and <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Predict-Halloween.patch</code></sup></sub>
      </li>
      <li>
        <i>TagKey.hashCode()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Collections</i>
    <br/>
    Use an optimal collection type (such as one from <code>it.unimi.dsi.fastutil</code>, or a linked map for fast iteration), potentially initialized with a specific capacity.
    <ul>
      <li>
        <i>BlockState-keyed</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>ChunkMap.entityMap</i>
        <br/>
        Using a linked map is nearly equivalent to the non-linked map,
        but slightly faster in practice due to the frequency of full iterations.
        <br/>
        <sub><sup>By: <a href="https://github.com/ishland">ishland</a> (as part of <a href="https://github.com/RelativityMC/VMP-fabric">VMP</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Use-linked-map-for-entity-trackers.patch</code></sup></sub>
      </li>
      <li>
        <i>CraftServer.getWorlds() (use fastutil)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/HaHaWTH">HaHaWTH</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Faster-CraftServer-getworlds-list-creation.patch</code></sup></sub>
      </li>
      <li>
        <i>CraftServer.spawnCategoryLimit (use array)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>optimize-mob-spawning.patch</code></sup></sub>
      </li>
      <li>
        <i>CraftServer.worlds (use fastutil)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/Taiyou06">Taiyou</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Replace-world-map-with-optimized-collection.patch</code></sup></sub>
      </li>
      <li>
        <i>CraftWorld.spawnCategoryLimit (use array)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>optimize-mob-spawning.patch</code></sup></sub>
      </li>
      <li>
        <i>MobCounts.counts (use array)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/ishland">ishland</a> (as part of <a href="https://github.com/RelativityMC/VMP-fabric">VMP</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Store-mob-counts-in-an-array.patch</code></sup></sub>
      </li>
      <li>
        <i>Sensing</i>
        <br/>
        Most mobs only ever target at most 1 entity (typically a nearby player or a mob farm bait entity)
        <br/>
        so we create their sensing cache with an initial capacity of 2 instead of 16.
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Initialize-line-of-sight-cache-with-low-capacity.patch</code></sup></sub>
      </li>
      <li>
        <i>ServerHandshakePacketListenerImpl.throttleTracker (use fastutil)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/nopjmp">nopjmp</a> as part of (<a href="https://github.com/nopjmp/Dionysus">Dionysus</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Replace-throttle-tracker-map-with-optimized-collecti.patch</code></sup></sub>
      </li>
      <!--<li>
        <i>SpawnState.mobCategoryCounts (use array)</i>
      </li>-->
      <li>
        <i>SynchedEntityData.packDirty()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Optimize-SynchedEntityData-packDirty.patch</code></sup></sub>
      </li>
      <li>
        <i>WorldConfiguration.Entities.Spawning.despawnRanges (use EnumMap)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>optimize-despawn.patch</code></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Do less work</i>
    <br/>
    Evaluate the cheapest conditions first, and avoid doing unnecessary work.
    <ul>
      <li>
        <i>Check ticks frozen difference only once</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Only-update-frozen-ticks-if-changed.patch</code></sup></sub>
      </li>
      <li>
        <i>ChunkGenerator.addVanillaDecorations() (initialize random with empty seed)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>ItemLore.styledLines (computed only when used)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>EntitySelector.pushableBy()</i>
        <br/>
        Avoid duplicated <code>Bukkit.isPushable()</code> check, already checked inside the <code>Bukkit.canCollideWithBukkit()</code>.
        <br/>
        <sub><sup>By: <a href="https://github.com/OverwriteMC">OverwriteMC</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Optimize-pushable-selector.patch</code></sup></sub>
      </li>
      <li>
        <i>Exit Bukkit event call early if no listeners</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/lilingfengdev">lilingfengdev</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Skip-event-if-no-listeners.patch</code></sup></sub>
      </li>
      <li>
        <i>Level.checkEntityCollision()</i>
        <br/>
        Perform other checks before <code>canSee</code>,
        because the latter is more expensive.
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Reduce-canSee-work.patch</code></sup></sub>
      </li>
      <li>
        <i>LivingEntity.tryAddFrost()</i>
        <br/>
        Sort the checks in <code>LivingEntity.tryAddFrost()</code>
        in ascending order of cost.
        <br/>
        <sub><sup>By: <a href="https://github.com/2No2Name">2No2Name</a> (as part of <a href="https://github.com/CaffeineMC/lithium">Lithium</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Check-frozen-ticks-before-landing-block.patch</code></sup></sub>
      </li>
      <li>
        <i>PoiTypes.forState() (skip Optional creation)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Raid.updateBossbar()</i>
        <br/>
        Performs the code in <code>Raid.updateBossbar()</code> at most once per tick (during <code>tick()</code>),
        instead of every time it is called.
        <br/>
        <sub><sup>By: <a href="https://github.com/jellysquid3">jellysquid3</a> (as part of <a href="https://github.com/CaffeineMC/lithium">Lithium</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Update-boss-bar-within-tick.patch</code></sup></sub>
      </li>
      <li>
        <i>RespawnAnchorBlock.explode()</i>
        <br/>
        Compute <code>inWater</code> as late as possible.
        <br/>
        <sub><sup>By: <a href="https://github.com/Dreeam-qwq">Dreeam</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>Optimize-respawn-anchor-explosion.patch</code></sup></sub>
      </li>
      <li>
        <i>SecondaryPoiSensor.doTick() (skip secondary POI sensor if absent)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/2No2Name">2No2Name</a> (as part of <a href="https://github.com/CaffeineMC/lithium">Lithium</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>Skip-secondary-POI-sensor-if-absent.patch</code></sup></sub>
      </li>
      <li>
        <i>ServerLevel.updatePOIOnBlockStateChange() (reduce lambdas)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>SetLookAndInteract.create()</i>
        <br/>
        Check type before computing distance, because the latter is more expensive.
        <br/>
        <sub><sup>By: <a href="https://github.com/Taiyou06">Taiyou</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>Optimize-SetLookAndInteract-and-NearestVisibleLiving.patch</code></sup></sub>
      </li>
      <li>
        <i>Skip client-side code</i>
        <br/>
        Do not perform actions server-side if the result is only used client-side.
        <ul>
          <li>
            <i>Leashable.getLeashHolder()</i>
            <br/>
            <sub><sup>By: <a href="https://github.com/wling-art">MrlingXD</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
            <br/>
            <sub><sup>Leaf patch: <code>Optimize-getLeashHolder.patch</code></sup></sub>
          </li>
        </ul>
      </li>
      <li>
        <i>Skip negligible planar movement multiplication</i>
        <br/>
        Skip calling <code>Entity.getBlockSpeedFactor()</code> from <code>Entity.move()</code>
        when planar delta movement is negligible (within 1.0E-6 threshold).
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Skip-negligible-planar-movement-multiplication.patch</code></sup></sub>
      </li>
      <li>
        <i>Skip PlayerCommandSendEvent if no listeners</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/billygalbreath">BillyGalbreath</a> (as part of <a href="https://github.com/PurpurMC/Purpur">Purpur</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Skip-PlayerCommandSendEvent-if-there-are-no-listener.patch</code></sup></sub>
      </li>
      <li>
        <i>Skip self check in CraftPlayer.canSee() if called from ChunkMap.updatePlayer()</i>
        <br/>
        Skip the self check, as it was already checked at that point.
        <br/>
        <sub><sup>By: <a href="https://github.com/MrPowerGamerBR">MrPowerGamerBR</a> (as part of <a href="https://github.com/SparklyPower/SparklyPaper">SparklyPaper</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>SparklyPaper-Optimize-canSee-checks.patch</code></sup></sub>
      </li>
      <li>
        <i>Skip unnecessary Entity.move() code if bounding box and position are unchanged</i>
        <br/>
        Run a simplified version of <code>Entity.move()</code> if the bounding box was not changed and the movement delta is zero.
        <br/>
        <sub><sup>By: <a href="https://github.com/ishland">ishland</a> (as part of <a href="https://github.com/RelativityMC/VMP-fabric">VMP</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Skip-entity-move-if-movement-is-zero.patch</code></sup></sub>
      </li>
      <li>
        <i>TargetingConditions.test()</i>
        <br/>
        Check targeting range before computing visibility distance,
        because the latter is more expensive.
        <br/>
        <sub><sup>By: <a href="https://github.com/PaulBGD">PaulBGD</a> (as part of <a href="https://github.com/TECHNOVE/Airplane">Airplane</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Check-targeting-range-before-getting-visibility.patch</code></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Don't load chunks synchronously</i>
    <br/>
    Avoid loading chunks synchronously, but only if it doesn't affect gameplay behavior.
    <ul>
      <li>
        <i>Enderman teleport</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/PaulBGD">PaulBGD</a> (as part of <a href="https://github.com/TECHNOVE/Airplane">Airplane</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Reduce-enderman-teleport-chunk-lookups.patch</code></sup></sub>
      </li>
      <li>
        <i>Nether fortress mob spawn</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>optimize-mob-spawning.patch</code></sup></sub>
      </li>
      <li>
        <i>Phantom spawn</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/PureGero">PureGero</a> (as part of <a href="https://github.com/MultiPaper/MultiPaper">MultiPaper</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Don-t-load-chunks-to-spawn-phantoms.patch</code></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Event-driven</i>
    <br/>
    Update (and keep cached) values or take certain actions
    when the underlying conditions change, instead of calculating the value
    or considering the action every time it is requested.
    <ul>
      <li>
        Supporting <i>Optimize Mob.checkDespawn</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
        <ul>
          <li>
            <i>AbstractFish.canRemoveWhenFarAway</i>
          </li>
          <li>
            <i>Axolotl.canRemoveWhenFarAway</i>
          </li>
          <li>
            <i>Cat.canRemoveWhenFarAway</i>
          </li>
          <li>
            <i>Chicken.canRemoveWhenFarAway</i>
          </li>
          <li>
            <i>Entity.isInTickList</i>
          </li>
          <li>
            <i>Mob.checkDespawnPlayerTrackingMode</i>
          </li>
          <li>
            <i>Ocelot.canRemoveWhenFarAway</i>
          </li>
          <li>
            <i>Piglin.canRemoveWhenFarAway</i>
          </li>
          <li>
            <i>Raider.canRemoveWhenFarAway</i>
          </li>
          <li>
            <i>ZombieVillager.canRemoveWhenFarAway</i>
          </li>
        </ul>
      </li>
      <li>
        <i>Entity.playerPassengerCount</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Level.affectsSpawningSelectorPlayerCount</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>LivingEntity.isAlive</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Mob.isLeashed</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Mob.isPersistent</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Mob.mustDespawnBecauseOfPeacefulDifficulty</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Mob.requiresCustomPersistence</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Mob.shouldDespawnInPeaceful</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Player.affectsSpawningSelector</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Faster implementation for subclass</i>
    <br/>
    Override a superclass method with a faster implementation
    specific to the subclass.
    <ul>
      <li>
        <i>BlockBehaviour.BlockStateBase.is(Block)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>ItemLore.equals() (skip styledLines)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>LevelChunk.getSectionIndex()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>optimize-LevelChunk-getBlockStateFinal.patch</code></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Intrusive values</i>
    <br/>
    Store properties of some types directly as a field,
    instead of in some external data structure.
    <ul>
      <li>
        <i>EntityDataSerializers.SERIALIZERS</i>
        <br/>
        Make <code>EntityDataSerializers.SERIALIZERS</code> a simple list,
        and store <code>EntityDataSerializers.getSerializedId(EntityDataSerializer)</code> in <code>AbstractEntityDataSerializer.indexInList</code>.
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Global block state palette id</i>
        <br/>
        Store <code>Block.BLOCK_STATE_REGISTRY.getId(BlockState)</code> in <code>BlockState.indexInRegistry</code>.
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Local code optimization</i>
    <br/>
    Make small, localized procedures faster.
    <ul>
      <li>
        <i>Add equality check to ItemStack.isSameItem...()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Optimize-matching-item-checks.patch</code></sup></sub>
      </li>
      <li>
        <i>BiomeManager.getBiome()</i>
        <br/>
        Aggressively inline this method.
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>ChunkGenerator.addVanillaDecorations()</i>
        <br/>
        Pre-compute <code>shouldGenerateStructures()</code> and reuse the <code>structureStarts</code> list instance.
        <br/>
        <sub><sup>By: <a href="https://github.com/noramibu">noramibu</a></sup></sub>
      </li>
      <li>
        <i>DedicatedServer.getScaledTrackingDistance()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Optimize-getScaledTrackingDistance.patch</code></sup></sub>
      </li>
      <li>
        <i>EntityBasedExplosionDamageCalculator.getBlockExplosionResistance() (reduce allocations)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/2No2Name">2No2Name</a> (as part of <a href="https://github.com/CaffeineMC/lithium">Lithium</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Reduce-lambda-and-Optional-allocation-in-EntityBased.patch</code></sup></sub>
      </li>
      <li>
        <i>EntityGetter.getNearestPlayer()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/Taiyou06">Taiyou</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Slightly-optimise-getNearestPlayer.patch</code></sup></sub>
      </li>
      <li>
        <i>GateBehavior.tickOrStop() (remove stream)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/Taiyou06">Taiyou</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Remove-stream-in-GateBehavior.patch</code></sup></sub>
      </li>
      <li>
        <i>Inline player UUID comparison</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>LevelChunk.getBlockStateFinal()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>optimize-LevelChunk-getBlockStateFinal.patch</code></sup></sub>
      </li>
      <li>
        <i>LivingEntity.tickEffects() (don't iterate over empty effect collection)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>optimize-tickEffects.patch</code></sup></sub>
      </li>
      <li>
        <i>MobEffectUtil.getDigSpeedAmplification()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/Taiyou06">Taiyou</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Optimise-MobEffectUtil-getDigSpeedAmplification.patch</code></sup></sub>
      </li>
      <li>
        <i>MobSensor.checkForMobsNearby() (remove stream)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/Taiyou06">Taiyou</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Remove-stream-in-MobSensor.patch</code></sup></sub>
      </li>
      <li>
        <i>NearestVisibleLivingEntities.findClosest() (add empty check and size variable)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/Taiyou06">Taiyou</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>Optimize-SetLookAndInteract-and-NearestVisibleLiving.patch</code></sup></sub>
      </li>
      <li>
        <i>new Advancement() (skip cloning criteria)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/etil2jz">etil2jz</a> (as part of <a href="https://github.com/etil2jz/Mirai">Mirai</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Skip-cloning-advancement-criteria.patch</code></sup></sub>
      </li>
      <li>
        <i>PatchedDataComponentMap.equals()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/HaHaWTH">HaHaWTH</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Optimize-PatchedDataComponentMap-equals.patch</code></sup></sub>
      </li>
      <li>
        <i>Ravager.roar() (re-use level variable)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/RuleGaed">RuleGaed</a></sup></sub>
      </li>
      <li>
        <i>ServerLevel.destroyBlockProgress() (allocate zero or one block destruction packets)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/vytskalt">vytskalt</a> (as part of <a href="https://github.com/Electroid/SportPaper">SportPaper</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Reduce-block-destruction-packet-allocations.patch</code></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Math</i>
    <br/>
    Optimize mathematical expressions by hardware operation efficiency.
    For example, multiplication is faster than division on all hardware.
    <ul>
      <li>
        <i>CubePointRange.getDouble() (replace division by multiplication)</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/2No2Name">2No2Name</a> (as part of <a href="https://github.com/CaffeineMC/lithium">Lithium</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Replace-division-by-multiplication-in-CubePointRange.patch</code></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Pre-compute</i>
    <br/>
    Pre-compute values instead of repeatedly computing them whenever needed.
    <ul>
      <li>
        <i>BiomeManager.getFiddle()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/hayanesuru">hayanesuru</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: part of <code>cache-biome-for-mob-spawning-and-advancements.patch</code></sup></sub>
      </li>
      <li>
        <i>BlockBehaviour.getPistonPushReaction()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/RuleGaed">RuleGaed</a></sup></sub>
      </li>
      <!--<li>
        <i>BlockBehaviour.getSoundType()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/RuleGaed">RuleGaed</a></sup></sub>
      </li>-->
      <li>
        <i>BlockBehaviour.hasBlockEntity()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/RuleGaed">RuleGaed</a></sup></sub>
      </li>
      <li>
        <i>BlockBehaviour.isDestroyable()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/RuleGaed">RuleGaed</a></sup></sub>
      </li>
      <li>
        <i>CubePointRange.size</i>
        <br/>
        Replace <code>CubePointRange.parts</code> by <code>CubePointRange.size</code>.
        <br/>
        <sub><sup>By: <a href="https://github.com/Taiyou06">Taiyou</a> (as part of <a href="https://github.com/Winds-Studio/Leaf">Leaf</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: replaces <code>Remove-stream-in-matchingSlot.patch</code></sup></sub>
      </li>
      <li>
        <i>Direction.values()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/RuleGaed">RuleGaed</a></sup></sub>
      </li>
      <li>
        <i>Enchantment.matchingSlot()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>PoiTypes.forState()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>PotentSulfurBlockEntity.geyserPositional() PositionalRandomFactory</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>WalkNodeEvaluator.getPathTypeFromState()</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
    </ul>
  </li>
  <li>
    <i>Reduce allocations</i>
    <br/>
    Reduce object allocations. This also saves time during garbage collection.
    <ul>
      <li>
        <i>RandomSource</i>
        <br/>
        Re-use <code>RandomSource</code> instances where it doesn't affect game mechanics.
        <br/>
        <sub><sup>By: <a href="https://github.com/foss-mc">foss-mc</a> (as part of <a href="https://github.com/PatinaMC/Patina">Patina</a>)</sup></sub>
        <br/>
        <sub><sup>Leaf patch: <code>Reduce-RandomSource-instances.patch</code></sup></sub>
      </li>
      <li>
        <i>RandomSupport.Seed128bit</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
      <li>
        <i>Xoroshiro128PlusPlus</i>
        <br/>
        <sub><sup>By: <a href="https://github.com/MartijnMuijsers">Martijn Muijsers</a></sup></sub>
      </li>
    </ul>
  </li>
</ul>

## Notes

### Differences with Paper

#### Plugins that don't follow the API

Gale changes do not affect plugins. All changes are purely internal.

However, some plugins do not just use the plugin API, but rely very deeply on specific server internals.
These plugins typically break after every Minecraft version.

If such a plugin breaks on Gale, it's not our fault.
Still, we really try to support these plugins regardless.
In the rare case that a plugin can not be supported and you need it, you can switch back to Paper in an instant.

#### Practical considerations

Some differences with Paper do actually exist.
They exist because of pragmatic choices that had to be made.
We include this section to be transparent about them.
We are confident that they do not affect you.
If they do, we will remove them.

* *Exceptions when calling synchronous events with no listeners asynchronously*\
  While rarely used, plugins can manually fire Bukkit events.
  Obviously, it is not allowed to fire a synchronous event asynchronously.
  Paper throws an Exception if a misbehaving plugin does so anyway.
  Paper throws an Exception even when the event has no listeners (and so it would never have caused a problem).
  Gale only throws the Exception if it would have caused a problem.
  Even if a badly written plugin somehow decides to rely on Paper repeatedly throwing these Exceptions when the plugin makes forbidden calls,
  there is no imaginable way that this could actually matter for events with no listeners.
* *Bugs in Paper*\
  If there is a bug in Paper, we may fix it accidentally just from the replacing of that piece of code.
  An example of this is non-spherical despawn ranges (which do not exist by default, but they are possible with the right Paper configuration): they are very slightly bugged in Paper.
  We only fix bugs if they don't have any observable effect: if fixing a bug can break a farm, we will not fix it.
* *Floating-point operations*\
  Floating-point operations are not precise.
  For example, `16777217 + 1.0f - 1.0f` returns `16777215.0f`.
  Any change in an expression may cause the result to slightly change.
  However, these differences happen all the time, and no plugins or behaviors could ever rely
  on floating-point errors not existing.
  Of course, world generation on Gale is guaranteed to be the exact same as on Paper.
* *Random-based synchronous chunk loading*\
  Some events in Minecraft target a random location,
  with no regard for whether that location is loaded or not.
  In some cases, this can lead to a sudden synchronous chunk load,
  which *really* hurts performance.
  For events that no farm could ever depend on, Gale may choose to limit the choice to chunks that have already been loaded.
  This can prevent some heavy lag spikes without any effect on gameplay.

### Why not PR to Paper?

Actually, a large number of features in Gale have been submitted to Paper.

Many have been merged over time, but many are also either still stuck in consideration or have been rejected.
In most cases, the reason is that to the Paper maintainers, the future maintenance cost outweighs the benefits.
That is very understandable: sometimes we as Gale authors also must drop a change when the maintenance becomes too high or the benefits too small.
Some PRs to Paper have been approved on their merits, but not merged, with the explicit feedback that they are better placed in a fork than in Paper itself.
