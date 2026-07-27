<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="logo.png">
    <img src="logo.png" width="130" alt="Gale">
  </picture>
</p>

<h1 align="center">Gale</h1>

<p align="center">
  <b>A higher-performance <a href="https://github.com/PaperMC/Paper">Paper</a> fork without behavioral changes</b>
</p>

<p align="center">
  <a href="https://discord.gg/gwezNT8c24"><img src="https://img.shields.io/discord/1045402468416233592?color=5865F2&label=discord&logo=discord&style=flat-square" alt="Discord"></a>
  <a href="https://github.com/GaleMC/Gale/releases"><img src="https://img.shields.io/github/v/release/GaleMC/Gale?style=flat-square&label=latest%20build" alt="Latest build"></a>
  <a href="https://github.com/GaleMC/Gale/blob/main/LICENSE.md"><img src="https://img.shields.io/github/license/GaleMC/Gale?style=flat-square" alt="License"></a>
</p>

---

## About

Gale is a drop-in alternative for Paper that provides higher performance without any behavioral changes. Just swap your server jar and you're done.

- **Faster** than Paper, on every server
- **Zero** behavioral changes – all your plugins work exactly as before
- **Stable** and carefully reviewed, no risky experiments

Gale contains [dozens of performance improvements](Features.md), including SIMD-accelerated operations, virtual thread usage, and extensive caching.

## Server owners

1. [Download the latest `.jar`](https://github.com/GaleMC/Gale/releases)
2. Point to it in your start script – replace `paper-<version>.jar` with `gale-<version>.jar`

That's it. No configuration changes needed.

## Developers

Building from source:

```
./gradlew applyAllPatches
./gradlew :gale-server:createPaperclipJar
```

Pull requests are welcome! See the [contributing guidelines](CONTRIBUTING.md).

## Acknowledgements

Built on [Paper](https://papermc.io/), [Spigot](https://www.spigotmc.org/) and [Bukkit](https://bukkit.org/).

If you want features or optimizations that change behavior, check out [Leaf](https://www.leafmc.one/).

### Authors and contributors

<p align="center">
  <a href="https://github.com/Dreeam-qwq"><img src="https://github.com/Dreeam-qwq.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="Dreeam" title="Dreeam"/></a>
  <a href="https://github.com/granny"><img src="https://github.com/granny.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="granny" title="granny"/></a>
  <a href="https://github.com/HaHaWTH"><img src="https://github.com/HaHaWTH.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="HaHaWTH" title="HaHaWTH"/></a>
  <a href="https://github.com/hayanesuru"><img src="https://github.com/hayanesuru.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="hayanesuru" title="hayanesuru"/></a>
  <a href="https://github.com/MartijnMuijsers"><img src="https://github.com/MartijnMuijsers.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="Martijn Muijsers" title="Martijn Muijsers"/></a>
  <a href="https://github.com/wling-art"><img src="https://github.com/wling-art.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="MrlingXD" title="MrlingXD"/></a>
  <a href="https://github.com/MrPowerGamerBR"><img src="https://github.com/MrPowerGamerBR.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="MrPowerGamerBR" title="MrPowerGamerBR"/></a>
  <a href="https://github.com/noramibu"><img src="https://github.com/noramibu.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="noramibu" title="noramibu"/></a>
  <a href="https://github.com/nostalfinals"><img src="https://github.com/nostalfinals.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="Nostal Yuu" title="Nostal Yuu"/></a>
  <a href="https://github.com/OverwriteMC"><img src="https://github.com/OverwriteMC.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="OverwriteMC" title="OverwriteMC"/></a>
  <a href="https://github.com/RuleGaed"><img src="https://github.com/RuleGaed.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="RuleGaed" title="RuleGaed"/></a>
  <a href="https://github.com/Smorki"><img src="https://github.com/Smorki.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="Smorki" title="Smorki"/></a>
  <a href="https://github.com/Taiyou06"><img src="https://github.com/Taiyou06.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="Taiyou" title="Taiyou"/></a>
  <a href="https://github.com/toprakdevx"><img src="https://github.com/toprakdevx.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="Toprak" title="Toprak"/></a>
  <a href="https://github.com/vytskalt"><img src="https://github.com/vytskalt.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="vytskalt" title="vytskalt"/></a>
</p>

### Third-party credits

<p align="center">
  <a href="https://github.com/2No2Name"><img src="https://github.com/2No2Name.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="2No2Name" title="2No2Name"/></a>
  <a href="https://github.com/billygalbreath"><img src="https://github.com/billygalbreath.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="Billy Galbreath" title="Billy Galbreath"/></a>
  <a href="https://github.com/etil2jz"><img src="https://github.com/etil2jz.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="etil2jz" title="etil2jz"/></a>
  <a href="https://github.com/foss-mc"><img src="https://github.com/foss-mc.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="foss-mc" title="foss-mc"/></a>
  <a href="https://github.com/ishland"><img src="https://github.com/ishland.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="ishland" title="ishland"/></a>
  <a href="https://github.com/jaskarth"><img src="https://github.com/jaskarth.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="jaskarth" title="jaskarth"/></a>
  <a href="https://github.com/jellysquid3"><img src="https://github.com/jellysquid3.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="jellysquid3" title="jellysquid3"/></a>
  <a href="https://github.com/PaulBGD"><img src="https://github.com/PaulBGD.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="PaulBGD" title="PaulBGD"/></a>
  <a href="https://github.com/PureGero"><img src="https://github.com/PureGero.png?size=80" width="45" height="45" style="border-radius: 50%; object-fit: cover;" alt="PureGero" title="PureGero"/></a>
</p>
