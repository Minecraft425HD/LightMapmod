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

2. **Fixed BufferBuilder crash** in `common/src/main/java/com/mamiyaotaru/voxelmap/Map.java` (lines 1568-1613) - Commented out incomplete rendering code that was calling `BufferBuilder.begin()` without a matching `end()`. This was leaving the BufferBuilder in a "building" state and causing the `IllegalStateException: Already building!` crash.

3. **Added `.mcassetsroot` markers** to `common/src/main/resources/` and `forge/src/main/resources/` directories. These files tell Forge's development environment where to find mod resources.

4. **Build configuration already correct** in `forge/build.gradle.kts`:
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

**The Fix:** Comment out the entire incomplete rendering section to prevent the BufferBuilder from being left in an inconsistent state.

### Multi-Loader Project Setup
In a multi-loader project setup (common + forge), resources in the `common` module need to be properly configured:
1. **pack.mcmeta** - Required for Minecraft to recognize the resource pack
2. **.mcassetsroot markers** - Helps ForgeGradle identify resource directories during development
3. **Build configuration** - Ensures common resources are included in the forge JAR and development runs
