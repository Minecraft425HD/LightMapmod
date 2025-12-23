# VoxelMap Crash Fix - Missing Texture Resources

## Problem
The game was crashing with `IllegalStateException: Already building!` and missing texture errors:
```
[VoxelMap/]: Arrow texture not found: voxelmap:images/mmarrow.png
[VoxelMap/]: Square map texture not found: voxelmap:images/squaremap.png
[VoxelMap/]: Failed to load texture: voxelmap:images/radar/hostile.png
[VoxelMap/]: Error loading color picker: No value present
```

## Root Cause
The VoxelMap texture resources exist in `common/src/main/resources/assets/voxelmap/images/` but were not being loaded at runtime during development. This is because Forge requires resource root marker files (`.mcassetsroot`) to properly identify resource directories during development runs.

## Solution

### Step 1: Add Resource Root Markers
The `.mcassetsroot` files have been added to mark the resource directories:
- `common/src/main/resources/.mcassetsroot`
- `forge/src/main/resources/.mcassetsroot`

These empty marker files tell ForgeGradle that these directories contain mod resources.

### Step 2: Clean and Rebuild
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

1. **Added `.mcassetsroot` markers** to `common/src/main/resources/` and `forge/src/main/resources/` directories. These files tell Forge's development environment where to find mod resources.

2. **Build configuration already correct** in `forge/build.gradle.kts`:
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
In a multi-loader project setup (common + forge), resources in the `common` module need to be properly marked for the ForgeGradle development environment. While the JAR packaging works correctly, the development run configuration requires `.mcassetsroot` marker files to identify resource directories.
