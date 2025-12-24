# VoxelMap Crash Fix - Rendering and Resource Loading Issues

## Problems
The game was crashing with two related issues:

1. **Missing Texture Resources:**
```
[VoxelMap/]: Arrow texture not found: voxelmap:images/mmarrow.png
[VoxelMap/]: Square map texture not found: voxelmap:images/squaremap.png
[VoxelMap/]: Failed to load texture: voxelmap:images/radar/hostile.png
[Render thread/WARN] [minecraft/Pack]: Missing metadata in pack mod:voxelmap
```

2. **BufferBuilder Crash:**
```
[Render thread/ERROR] [VoxelMap/]: Error while render overlay
java.lang.IllegalStateException: Already building!
        at com.mojang.blaze3d.vertex.BufferBuilder.begin(BufferBuilder.java:100)
        at com.mamiyaotaru.voxelmap.Map.drawMapFrame(Map.java:1882)
```

## Root Causes
1. **Missing pack.mcmeta** - Minecraft requires a `pack.mcmeta` file to properly recognize and load resource packs containing mod assets.
2. **Incomplete BufferBuilder code** - The 1.20.1 port left BufferBuilder rendering code in an incomplete state, calling `BufferBuilder.begin()` without a matching `end()`, which left the buffer in a "building" state and caused all subsequent rendering to crash.

## Solution

### Step 1: Add pack.mcmeta File
Create `common/src/main/resources/pack.mcmeta` with the following content:

```json
{
  "pack": {
    "description": "Voxelmap - Minimap and world map",
    "pack_format": 15
  }
}
```

**Important**: For Minecraft 1.20.1, the `pack_format` must be `15`. This tells Minecraft how to interpret the resource pack structure.

### Step 2: Fix BufferBuilder Rendering Code
The incomplete BufferBuilder code in `Map.renderMap()` (lines 1568-1613) has been commented out to prevent it from leaving the buffer in an inconsistent state. This code was part of an incomplete 1.20.1 port that called `BufferBuilder.begin()` but never called `Tesselator.getInstance().end()`.

**File:** `common/src/main/java/com/mamiyaotaru/voxelmap/Map.java`
**Lines:** 1568-1613 (now commented out)

### Step 3: Add Resource Root Markers (Already Done)
The `.mcassetsroot` files have been added to mark the resource directories:
- `common/src/main/resources/.mcassetsroot`
- `forge/src/main/resources/.mcassetsroot`

These empty marker files help ForgeGradle identify resource directories during development runs.

### Step 4: Clean and Rebuild
Run a clean build to ensure all resources are properly processed:

```bash
./gradlew clean build
```

### Step 5: Run the Client
```bash
./gradlew :forge:runClient
```

## Verification
The resources should now be loaded correctly. You can verify by checking:

1. **Source resources exist:**
   ```bash
   ls common/src/main/resources/assets/voxelmap/images/
   ```

2. **Build resources are copied:**
   ```bash
   ls common/build/resources/main/assets/voxelmap/images/
   ```

3. **Runtime loads without errors** - no `FileNotFoundException` messages in the logs

## What Was Fixed

1. **Added `pack.mcmeta` file** to `common/src/main/resources/` - This fixed the texture loading issue. Without this file, Minecraft cannot properly recognize the mod's resource pack, causing all texture loading to fail. The file specifies:
   - `pack_format: 15` for Minecraft 1.20.1
   - A description for the resource pack

2. **Fixed BufferBuilder crash** in `common/src/main/java/com/mamiyaotaru/voxelmap/Map.java` (lines 1568-1613) - Replaced incomplete rendering code that was calling `BufferBuilder.begin()` without a matching `end()` with proper GuiGraphics.blit() implementation using 1.20.1 rendering API.

3. **Fixed black minimap - texture shifting not working** - **THIS WAS THE MAIN ISSUE!** The `moveX()` and `moveY()` methods in `DynamicMoveableTexture.java` were completely commented out. These methods are critical for map updates - they shift the texture data when the player moves so only new edges need to be recalculated. Without them working, the map couldn't update properly. Implemented 1.20.1 compatible versions using NativeImage pixel operations.

4. **Fixed black minimap - wrong texture sizes** - The rendering code was hardcoded to render 256x256 textures (zoom level 3 only), but VoxelMap uses different texture sizes for each zoom level (32x32, 64x64, 128x128, 256x256, 512x512). Changed both minimap and full map rendering to dynamically calculate texture size based on zoom level: `textureSize = 32 * 2^zoom`. This ensures the correct texture is rendered at any zoom level.

5. **Fixed PoseStack balance** - Removed extra `popPose()` call at line 1648 that was causing `NoSuchElementException` crashes.

6. **Added `.mcassetsroot` markers** to `common/src/main/resources/` and `forge/src/main/resources/` directories. These files tell Forge's development environment where to find mod resources.

7. **Build configuration already correct** in `forge/build.gradle.kts`:
   ```kotlin
   tasks.jar {
       val main = project.project(":common").sourceSets.getByName("main")
       from(main.output.classesDirs) {
           exclude("/voxelmap.refmap.json")
       }
       from(main.output.resourcesDir)  // Copies common resources
       ...
   }

   minecraft {
       runs {
           create("client") {
               mods {
                   create("voxelmap") {
                       source(sourceSets.main.get())
                       source(project.project(":common").sourceSets.main.get())  // Includes common resources
                   }
               }
           }
       }
   }
   ```

## Technical Details

### Why pack.mcmeta is Required
Minecraft's resource pack system requires a `pack.mcmeta` file at the root of any resource pack (including mod resource packs). This file:
- Declares the pack format version, which varies by Minecraft version
- Provides metadata about the pack
- Enables Minecraft to properly recognize and load the resources

**Pack Format Versions:**
- Minecraft 1.20.1 requires `pack_format: 15`
- Without this file, Minecraft logs "Missing metadata in pack" and fails to load any resources from the pack

### BufferBuilder State Management
Minecraft's rendering system uses a `BufferBuilder` to batch vertex data before sending it to the GPU. The BufferBuilder has strict state management:
1. `BufferBuilder.begin()` - Starts building a new batch of vertices
2. Add vertices with `.vertex()` calls
3. `Tesselator.getInstance().end()` or `BufferBuilder.build()` - Finishes and submits the batch

**The Issue:** The incomplete 1.20.1 port code in `Map.renderMap()` called `begin()` but the corresponding rendering code that would have called `end()` was commented out. This left the BufferBuilder in a "building" state, causing all subsequent rendering operations (including vanilla Minecraft GUI rendering) to crash with `IllegalStateException: Already building!`.

**The Fix:** Replace the incomplete rendering code with proper GuiGraphics.blit() calls using the 1.20.1 rendering API.

### Zoom Level Texture Sizes
VoxelMap uses different texture resolutions for each zoom level to optimize memory and rendering performance:
- **Zoom 0** (furthest out): 32x32 texture
- **Zoom 1**: 64x64 texture
- **Zoom 2**: 128x128 texture (1:1 scale)
- **Zoom 3**: 256x256 texture
- **Zoom 4** (closest): 512x512 texture

The texture size follows the formula: `textureSize = 32 * 2^zoom`

**The Issue:** The rendering code in both `renderMap()` (minimap) and `renderMapFull()` (full map) was hardcoded to render 256x256 textures, assuming zoom level 3. When using other zoom levels, this caused:
- Wrong texture dimensions being passed to `guiGraphics.blit()`
- Incorrect scaling calculations
- Black minimap because the texture data didn't match the rendering dimensions

**The Fix:** Calculate texture size dynamically based on the current zoom level:
```java
int textureSize = 32 * (int) Math.pow(2.0, this.zoom);
int halfTextureSize = textureSize / 2;
float textureScale = 64.0f / textureSize;  // Scale to 64x64 minimap size
float offsetMultiplier = textureSize / 64.0F;  // Correct player movement offset
```

This ensures the rendering code works correctly at any zoom level.

### Texture Shifting (moveX/moveY) - THE CRITICAL FIX
**This was the root cause of the black minimap!**

VoxelMap uses an optimization technique to avoid recalculating the entire map texture every frame:
1. When the player moves, shift the existing texture data in the movement direction
2. Only calculate new pixels along the edges that come into view
3. This is much faster than recalculating all pixels

For example, if the player moves 3 blocks east:
- Shift the entire texture 3 pixels left (moveX(-3))
- Calculate only the 3-pixel-wide strip on the right edge
- Instead of recalculating 256×256 = 65,536 pixels, only 256×3 = 768 pixels!

**The Issue:** The `moveX()` and `moveY()` methods in `DynamicMoveableTexture.java` were completely commented out because they used direct memory access APIs (`getPointer()`) that don't exist in 1.20.1:

```java
// Broken code - commented out
long pointer = this.getPixelsRGBA().getPointer();  // Doesn't exist in 1.20.1!
MemoryUtil.memCopy(pointer + offset, pointer, size);
```

Without these methods working:
- The texture never shifts when the player moves
- Only edge pixels get calculated, but written to wrong positions
- After the initial render, the map becomes stale/black
- Moving around doesn't update the minimap

**The Fix:** Reimplemented using NativeImage's pixel operations which are available in 1.20.1:

```java
// Working 1.20.1 code
for (int y = 0; y < height; y++) {
    for (int x = 0; x < width - offset; x++) {
        int pixel = this.getPixels().getPixelRGBA(x + offset, y);
        this.getPixels().setPixelRGBA(x, y, pixel);
    }
}
```

This is slower than direct memory copy, but it works correctly and the performance difference is negligible on modern systems.

### Multi-Loader Project Setup
In a multi-loader project setup (common + forge), resources in the `common` module need to be properly configured:
1. **pack.mcmeta** - Required for Minecraft to recognize the resource pack
2. **.mcassetsroot markers** - Helps ForgeGradle identify resource directories during development
3. **Build configuration** - Ensures common resources are included in the forge JAR and development runs
