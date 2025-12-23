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
The VoxelMap texture resources exist in `common/src/main/resources/assets/voxelmap/images/` but were not being copied to the build output directories (`common/build/resources/main/` and `forge/build/resources/main/`) during the Gradle build process.

## Solution
Run a clean build to ensure all resources are properly processed:

```bash
./gradlew clean build
```

Or manually ensure resources are processed:
```bash
./gradlew :common:processResources :forge:processResources
```

Then run the client:
```bash
./gradlew :forge:runClient
```

## Verification
After building, verify that resources exist in the build output:
```bash
ls -la common/build/resources/main/assets/voxelmap/images/
ls -la forge/build/resources/main/
```

You should see all the texture files including:
- mmarrow.png
- squaremap.png
- roundmap.png
- colorpicker.png
- radar/*.png files
- waypoints/*.png files

## What Was Fixed
The resources are properly configured in `forge/build.gradle.kts`:
```kotlin
tasks.jar {
    val main = project.project(":common").sourceSets.getByName("main")
    from(main.output.classesDirs) {
        exclude("/voxelmap.refmap.json")
    }
    from(main.output.resourcesDir)  // This copies common resources to forge jar
    ...
}
```

The issue was simply that the `processResources` task hadn't been run or the build output directories were stale. A clean build resolves this.
