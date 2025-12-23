# VoxelMap Crash Fix - Missing Texture Resources

## Problem
The game was crashing with `IllegalStateException: Already building!` and missing texture errors:
```
[VoxelMap/]: Arrow texture not found: voxelmap:images/mmarrow.png
[VoxelMap/]: Square map texture not found: voxelmap:images/squaremap.png
[VoxelMap/]: Failed to load texture: voxelmap:images/radar/hostile.png
[VoxelMap/]: Error loading color picker: No value present
[Render thread/WARN] [minecraft/Pack]: Missing metadata in pack mod:voxelmap
```

## Root Cause
The VoxelMap texture resources exist in `common/src/main/resources/assets/voxelmap/images/` but were not being loaded at runtime. The root cause was **missing pack metadata** - Minecraft requires a `pack.mcmeta` file to properly recognize and load resource packs containing mod assets.

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

### Step 2: Add Resource Root Markers (Already Done)
The `.mcassetsroot` files have been added to mark the resource directories:
- `common/src/main/resources/.mcassetsroot`
- `forge/src/main/resources/.mcassetsroot`

These empty marker files help ForgeGradle identify resource directories during development runs.

### Step 3: Clean and Rebuild
Run a clean build to ensure all resources are properly processed:

```bash
./gradlew clean build
```

### Step 3: Run the Client
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

1. **Added `pack.mcmeta` file** to `common/src/main/resources/` - This is the primary fix. Without this file, Minecraft cannot properly recognize the mod's resource pack, causing all texture loading to fail. The file specifies:
   - `pack_format: 15` for Minecraft 1.20.1
   - A description for the resource pack

2. **Added `.mcassetsroot` markers** to `common/src/main/resources/` and `forge/src/main/resources/` directories. These files tell Forge's development environment where to find mod resources.

3. **Build configuration already correct** in `forge/build.gradle.kts`:
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

### Multi-Loader Project Setup
In a multi-loader project setup (common + forge), resources in the `common` module need to be properly configured:
1. **pack.mcmeta** - Required for Minecraft to recognize the resource pack
2. **.mcassetsroot markers** - Helps ForgeGradle identify resource directories during development
3. **Build configuration** - Ensures common resources are included in the forge JAR and development runs
