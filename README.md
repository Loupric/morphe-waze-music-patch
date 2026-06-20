# Loupric — Waze × Morphe Music patches

Morphe patches to make **Waze** work with **Morphe Music** (the Morphe fork of YouTube Music).

## Patches

<!-- PATCHES_START EXPANDED -->

| Patch | App | Description |
|-------|-----|-------------|
| Waze Morphe Music support | `com.waze` | Adds `app.morphe.android.apps.youtube.music` to Waze's `<queries>` manifest so Waze can detect Morphe Music |
| Waze Morphe Music bytecode | `com.waze` | Injects Morphe Music into Waze's audio SDK app list so it appears as an audio source |
| Waze compatibility (MicroG) | `com.google.android.apps.youtube.music` | Bypasses the GoogleCertificates check that crashes YouTube Music when Waze connects via MicroG |

<!-- PATCHES_END -->

## Installation

1. Open **Morphe Manager**
2. Go to **Settings → Sources** and add a custom source:
   ```
   https://github.com/Loupric/morphe-waze-music-patch
   ```
   Or click: [Add to Morphe](https://morphe.software/add-source?github=Loupric/morphe-waze-music-patch)
3. Patch **Waze** with the two Waze patches
4. If you use **MicroG**, also patch **YouTube Music** with the MicroG compatibility patch

## Why this exists

Waze has an audio SDK that lets it pause/resume a music app while giving navigation instructions. Out of the box, it only supports apps Google certified — which excludes Morphe Music. Additionally, when Waze tries to connect to YouTube Music via `MediaBrowserService` on a MicroG setup, the app crashes due to a `GoogleCertificates` check.

These patches fix both problems.

## Building locally

Requires a GitHub PAT with `read:packages` scope in `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_PAT
```

Then:

```bash
./gradlew :patches:buildAndroid
```

The `.mpp` file is generated in `patches/build/libs/`.

> **Important:** use `:patches:buildAndroid`, not `build` — the Android task generates the `classes.dex` required by Morphe Manager.

## License

Licensed under the [GNU General Public License v3.0](LICENSE).
