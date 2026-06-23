# Regular Grid Acceleration - Integration Guide

This guide explains how to integrate the Regular Grid acceleration feature into your rendering pipeline.

## Quick Start

### 1. Basic Usage

```java
// Create your scene
Scene scene = new Scene("My Scene");

// Add geometries
Geometries geometries = new Geometries();
geometries.add(new Sphere(new Point(0, 0, 0), 2.0));
geometries.add(new Triangle(p1, p2, p3));
// ... more geometries ...
scene.setGeometries(geometries);

// Enable Regular Grid acceleration
scene.geometries.enableRegularGrid(new RegularGrid.Config(
    1.0,   // density parameter
    2,     // minimum resolution per axis
    50     // maximum resolution per axis
));

// Render as usual - acceleration is transparent
camera.renderImage();
```

### 2. Disable Grid

```java
// To disable the grid and return to baseline
scene.geometries.disableRegularGrid();
```

### 3. Change Grid Configuration

```java
// Can change configuration by re-enabling with different parameters
scene.geometries.enableRegularGrid(new RegularGrid.Config(2.0, 5, 100));
```

## Configuration Parameters

### Density Parameter

Controls how many voxels are created relative to scene size and object count.

```
Voxels per axis = ceil(sceneSize * cbrt(density * numGeometries / sceneVolume))
```

**Recommended values:**
- `0.5` - very coarse grid, minimal overhead, less ray optimization
- `1.0` - balanced (default), good for most scenes
- `2.0` - finer grid, more voxels, better spatial locality
- `5.0` - very fine grid, high overhead, extreme scenes only

### Min/Max Resolution

Clamps the computed grid resolution.

```java
new RegularGrid.Config(density, minResolution, maxResolution)
```

**Typical ranges:**
- `minResolution = 1` to `10` (minimum voxels per axis)
- `maxResolution = 30` to `200` (maximum voxels per axis)

**Example configurations:**
```java
// Small/fast scenes
new RegularGrid.Config(0.5, 1, 20)

// General purpose (recommended)
new RegularGrid.Config(1.0, 2, 50)

// Large/complex scenes
new RegularGrid.Config(2.0, 5, 100)

// Stress test
new RegularGrid.Config(5.0, 10, 200)
```

## Supported Geometries

### Finite Geometries (Included in Grid)
- ✅ Sphere
- ✅ Triangle
- ✅ Polygon
- ❌ Cylinder (currently treated as infinite for safety)
- ❌ Tube (infinite, always tested)
- ❌ Plane (infinite, always tested)

### Infinite/Unbound Geometries (Always Tested)
These are automatically handled correctly:
- Plane
- Tube
- Any geometry returning `null` from `getAABB()`

## Performance Expectations

### When to Use Regular Grid

**Use grid when:**
- Scene has 50+ geometries
- Geometries are spread across 3D space (not clustered)
- Using dense ray distributions (soft shadows, super-sampling)
- Rendering time is currently > 1 second

**Skip grid when:**
- Scene has < 20 geometries
- All geometries are in a small region
- Single ray intersection tests only
- Early project stages (complexity grows later)

### Performance Characteristics

```
Execution time = Grid Construction Time + Ray Traversal Time + Intersection Tests

Grid Construction (one-time): O(n * avg_voxels_per_geometry)
Ray Traversal: O(voxels_crossed) << O(total_geometries)
Intersection Tests: O(geometries_in_voxels) << O(total_geometries)
```

**Expected speedups:**
- 25 geometries: 1.0-1.3x (minimal gain, might be slower)
- 100 geometries: 2-3x (good speedup)
- 500+ geometries: 5-10x (significant speedup)

## Integration with MP1 Features

All Regular Grid operations are transparent to:
- ✅ Multi-threading (grid is read-only during rendering)
- ✅ Soft shadows / super-sampling
- ✅ Reflection / refraction rays
- ✅ Transparency calculations
- ✅ All ray types (primary, shadow, secondary)

No code changes needed for these features.

## Example: Comparison Test

```java
public static void compareWithAndWithoutGrid() {
    Scene scene = createComplexScene(); // your scene setup
    Ray testRay = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

    // Baseline (no grid)
    long t1 = System.currentTimeMillis();
    var results1 = scene.geometries.calcIntersections(testRay);
    long baseline = System.currentTimeMillis() - t1;

    // With grid
    scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));
    long t2 = System.currentTimeMillis();
    var results2 = scene.geometries.calcIntersections(testRay);
    long withGrid = System.currentTimeMillis() - t2;

    // Verify correctness
    assert results1.size() == results2.size() : "Result counts should match";

    // Print performance
    System.out.printf("Baseline: %dms, Grid: %dms, Speedup: %.2fx%n",
        baseline, withGrid, (double) baseline / withGrid);
}
```

## Debugging Tips

### Check Grid Status

```java
// Grid is enabled if enableRegularGrid was called
// Grid is disabled if disableRegularGrid was called

// Ray intersections still work correctly either way
var results = scene.geometries.calcIntersections(ray);
```

### Verify Geometry Assignment

All finite geometries with non-null AABB are included. Test by:

```java
// These should have AABB (included in grid)
assert sphere.getAABB() != null;
assert triangle.getAABB() != null;

// These return null (always tested normally)
assert plane.getAABB() == null;
assert tube.getAABB() == null;
```

### Monitor Performance

```java
// Log timing for each ray
long t1 = System.currentTimeMillis();
var results = scene.geometries.calcIntersections(ray);
long elapsed = System.currentTimeMillis() - t1;
System.out.println("Ray intersection: " + elapsed + "ms");
```

## Troubleshooting

### Problem: Wrong Rendering Results

**Symptoms:** Grid-accelerated rendering produces different image than baseline

**Solutions:**
1. Verify geometry AABBs are correct: `System.out.println(sphere.getAABB())`
2. Check that infinite geometries (Plane, etc.) return null from `getAABB()`
3. Disable grid and re-enable to verify toggle works: see test files for example

### Problem: No Performance Improvement

**Symptoms:** Grid is enabled but no speedup observed

**Causes & solutions:**
- Too few geometries (< 30): grid overhead dominates
  - Solution: Test with larger scenes
- Very coarse grid (high density): reduces effectiveness
  - Solution: Try lower density: `0.5` instead of `1.0`
- Very fine grid: too many voxels
  - Solution: Increase maxResolution clamp

### Problem: Out of Memory

**Symptoms:** OutOfMemoryError with grid enabled

**Solutions:**
1. Reduce maxResolution (fewer voxels)
2. Reduce density parameter
3. Disable grid if not needed

## Migration Checklist

If integrating Regular Grid into existing code:

- [ ] Add `enableRegularGrid(Config)` call before rendering
- [ ] Test with grid enabled and disabled to verify equivalence
- [ ] Run existing tests to ensure no regressions
- [ ] Measure performance on your test scenes
- [ ] Adjust density/resolution parameters for your scene size
- [ ] Document grid settings in your scene initialization code

## Example: Complete Setup

```java
public class RenderingPipeline {
    
    public void renderWithAcceleration() {
        // Create scene
        Scene scene = new Scene("Accelerated Scene");
        
        // Configure geometries
        Geometries geoms = new Geometries();
        for (Geometry g : loadGeometries()) {
            geoms.add(g);
        }
        scene.setGeometries(geoms);
        
        // Enable acceleration (computed automatically)
        int geometryCount = geoms.getList().size();
        double density = geometryCount < 50 ? 0.5 : 1.0;
        scene.geometries.enableRegularGrid(
            new RegularGrid.Config(density, 2, 100)
        );
        
        // Configure camera
        Camera camera = Camera.getBuilder()
            .setLocation(new Point(0, 0, -30))
            .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
            .setVPSize(6, 6)
            .setVPDistance(10)
            .setImageWriter(new ImageWriter("output", 800, 600, 100))
            .setRayTracer(new SimpleRayTracer(scene))
            .build();
        
        // Render (grid is transparent)
        camera.renderImage();
        camera.writeToImage();
    }
}
```

## Additional Resources

- See `TESTING_REGULAR_GRID.md` for test documentation
- See `RegularGrid.java` for algorithm details
- See `AABB.java` for bounding box implementation
- See test files for usage examples

---

**Key Takeaway:** Regular Grid acceleration is transparent to all other systems. Just enable it and rendering works with reduced intersection calculations. No other code changes required!
